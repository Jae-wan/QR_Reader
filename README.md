### ✍ 서적 참고 프로젝트 (Joyce의 안드로이드 앱 프로그래밍 with 코틀린)
<br>

> <b> 프로젝트 명 : QR_Reader <br> </b>
> 프로젝트 설명 : 오픈 라이브러리를 활용한 QR Code Reader 앱

> <b>개발 환경 : Android Studio  </b>
> * minSdk : 26
> * targetSdk : 31

> * <b> 핵심 구성요소 (Library Used) </b>
>	* [CameraX Library (Android Jetpack)](https://developer.android.com/training/camerax?hl=ko, "CameraX Library 공식 가이드")
	⇒ 카메라를 다룰 수 있게 하는 라이브러리 <br>
>	* [viewBinding (Android Jetpack)](https://timradder.tistory.com/20, "viewBinding & dataBinding에 대한 설명과 차이, 설정 방법까지!")을 사용하여 앱 안전성 증가
>	* [Google ML Kit Library](https://developers.google.com/ml-kit/vision/barcode-scanning/android, "Google ML Kit 공식 가이드")
	⇒ 구글에서 제공하는 Machine Learning Kit로, 카메라에 찍힌 정보를 분석할 수 있는 알고리즘 <br>

  

>	* <b>코어 기능 (카메라를 이용해 QR코드 인식하기) </b>
>		* viewBinding
>		* CameraX 를 활용하여 카메라 미리보기 화면 구현 (DEFAULT_BACK_CAMERA)
>		* Google ML Kit 활용하여 QR 데이터 인식 기능 구현
>		* [Result Page] => QR코드 인식 결과 보여주기 페이지 구현
>		* [Result Page] => 결과 확인 후 [돌아가기] 버튼 클릭하여 초기화면, 인식 모드로 돌아가기.

>	* <b> 부가 기능 </b>
>		* 추가중..

>	* <b> 디자인 </b>
>		* 추가중..

<br>

---


<br>

* 레이아웃 구성 예상도
<table>
  <tr>
    <td><img alt="" src="https://user-images.githubusercontent.com/57258381/160982456-09ff8bd6-3eb9-4544-a3d9-19afded39d3c.png" height="640" width="360"></td>
    <td><img alt="" src="https://user-images.githubusercontent.com/57258381/160982898-a28ac72a-0f3a-4b8b-9614-ac8a10d7bee6.png" height="640" width="360"></td>
  </tr>
  </table>
  <br>
 
  * 후면 카메라를 (DEFAULT_BACK_CAMERA) 이용하여 QR코드를 인식하는 리더기 페이지 구현
  * 인식된 QR코드를 분석(Analyze) 하여 결과 값을 나타내어주는 결과 페이지 구현
    * 결과 페이지는 [돌아가기] 버튼을 이용하여 초기 화면으로 돌아갈 수 있도록 구현

---

<h2> 사전 작업 - 1 </h2>

* viewBinding Config

```kotlin

android{
  ... 생략 ...
  defaultConfig {
  ... 생략 ...
  }
  buildFeatures {
    viewBinding true
  }
}
  
```


<h2> 사전 작업 - 2 </h2>

* CameraX & Google ML Kit Library Config

아래 코드를 작성하고, Sync Now 하여 앱 설정 동기화 해 주기.
  
```kotlin

android {
    {
    ... 생략 ...
    }
}

dependencies {
		def camerax_version = "1.1.0-alpha04"
						// androidx.camera:camera-camera2:1.1.0-alpha04
		implementation "androidx.camera:camera-camera2:$camerax_version"	
					// CameraX의 Core Library
		implementation "androidx.camera:camera-lifecycle:$camerax_version"
					// CameraX의 생명주기 관련 라이브러리
		implementation "androidx.camera:camera-view:1.0.0-alpha24"
					// CameraX의 뷰 관련 라이브러리
		implementation "com.google.mlkit:barcode-scanning:16.1.1"
					// ML Kit 라이브러리
}

```

<h2> 사전 작업 - 3 </h2>

* 카메라 권한 승인하기

[app] -> [manifests] -> [AndroidManifest.xml] 에서 아래와 같은 태그 2개 추가

```kotlin
<manifest xmlns:android= .... package= ..... >

<uses-feature android:name="android.hardware.camera" />
<uses-permission android:name="android.permission.CAMERA" />

<application>
..... 생략

```

---


<h2> MainActivity.kt (* 카메라 미리보기(Preview) 기능 구현) </h2>


<b> override fun onCreate( ) </b>
 => viewBinding 설정 및 startCamera( ) 콜

<b>fun startCamera( )</b>
 => 카메라 시작 (preview 및 이미지 분석 시작)

<b>fun getPreview( )</b>
 => 미리보기 기능 설정하고, 설정 완료 된 객체를 반환하는 함수

<b>overrid fun onResume( )</b>
 => QR코드 인식 화면

<b>fun hasPermissions( )</b>
 => [앱에 대한 Camera 권한의 유무 확인](https://developer.android.com/training/permissions/requesting?hl=ko, "앱 권한 공식 가이드")

<b>override fun onRequestPermissionsResult( )</b>
 => 권한 요청에 대한 콜백 함수로, Activity Class 포함 함수이기 떄문에 override 해 주어야 함.

<b>getImageAnalysis( )</b>
 => 현재 QR 코드가 인식 된 것이 있는지 검사함으로써 중복 인식을 방지함.
 
 => 데이터가 감지되었으면  result 화면으로 이동할 수 있도록 하는 Intent 동작
 
 

<h2> ResultActivity.kt (결과 화면 및 돌아가기 페이지) </h2>
<b> override fun onCreate( ) </b>
=> viewBinding 설정 및 MainActivity의 값을 getStringExtra로 받고, setUI 동작.


<b> private fun setUI( ) </b>
=> 넘겨받은 QR코드 데이터를 TextView에 설정
=> [돌아가기] 버튼의 동작 구현



<h2> QRCodeAnalyzer.kt : ImageAnalysis.Analyzer (QR코드 분석을 위한 .kt) </h2>
<b> override fun analyze() </b>
=> ImageProxy와 이미지 찍힐 때의 카메라 회전 각도를 고려하여 InputImage를 생성함.

=> Scanner를 통해 이미지를 분석하고, Failure / Complete / Success 관련 Listener를 주어 결과를 만들어 냄.

---
> MainActivity와 Analyzer의 Interface 역할을 하는 onDetectListener.kt
>
> ![이미지](https://user-images.githubusercontent.com/57258381/161016755-76d3c363-4237-4211-99a2-4fc811bb0f6b.png)<br>
> 1 : MainActivity에서 QRCodeAnalyzer에 QR코드 이미지를 보낸다.<br>
> 2 : QRCodeAnalyzer에서 적절한 연산을 통해 QR코드 데이터 인식을 완료한다.<br>
> 3 : QRCodeAnalyzer에서 인식한 데이터를 MainActivity로 보내야 하지만, 보낼 수 없다.<br>
>
> 위와 같은 상황에서 데이터 전달용 소통창구를 만들어주기 위해 Interface 를 사용한다.

<h2> onDetectListener.kt </h2>
1. onDetect( ) 함수가 있는 onDetectListener Interface를 생성.

```kotlin

interface onDetectListener {
	fun onDetect(msg : String)
}

```


2. MainActivity에서도 onDetectListener Interface를 구현하고, 해당 Interface 객체를 QRCodeAnalyzer에 전달한다.
```kotlin
	... 생략 ...
class MainActivity : AppCompatActivity() {

fun startCamera() {
	... 생략...
	
	val imageAnalysis = getImageAnalysis()
	
	cameraProvider.bindToLifecycle(... 생략..., imageAnalysis)
}

fun getImageAnalysis() : ImageAnalysis {

val cameraExecutor : ExecutorService = Executors.newSingleThreadExecutor()
val imageAnalysis = ImageAnalysis.Builder().build()

imageAnalysis.setAnalyzer(cameraExecutor, QRCodeAnalyzer(object : onDetectListener {
			override fun onDetect(msg : String) {
			Toast.makeText(this@MainActivity, "${msg}",
			Toast.LENGTH_SHORT).show()
		}
	}))
		return imageAnalysis
}

	... 생략 ...
```

3. QRCodeAnalyzer가 QR코드를 인식하고 나서 전달받은 Interface의 onDetect( )를 호출한다.
```kotlin
			... 생략 ...
scanner.process(image)
	.addOnSuccessListener { qrCodes ->
		for (qrCode in qrCodes) {
			onDetectListener.onDetect(qrCode.rawValue ?: "")
			}
		}
	... 생략 ...
```



4. MainActivity에서 구현한 onDetect( ) 함수가 실행되고, MainActivity에서는 onDetect( )가 실행됨으로써 데이터를 주고 받을 수 있다.












  
  
