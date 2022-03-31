### QR_Reader README
<h1> 서적을 참고하여 만든 QR 코드를 인식하는 QR Reader 기본 앱. </h1>
<h2> Google ML Kit + CameraX 라이브러리를 활용한 QR코드 리더기 </h2>

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

* 핵심 구성요소
  * CameraX Library (Android Jetpack)
    ⇒ 카메라를 다룰 수 있게 하는 라이브러리
  
  * viewBinding (Android Jetpack)

  * Google ML Kit Library
  ⇒ 구글에서 제공하는 Machine Learning Kit로, 카메라에 찍힌 정보를 분석할 수 있는 알고리즘
  
---

* 코어 기능
  * viewBinding을 사용하여 앱 안전성 증가
  * CameraX 를 활용하여 카메라 미리보기 화면 구현 (DEFAULT_BACK_CAMERA)
  * Google ML Kit 활용하여 QR 데이터 인식 기능 구현
  * [Result Page] => QR코드 인식 결과 보여주기 페이지 구현
  * [Result Page] => 결과 확인 후 [돌아가기] 버튼 클릭하여 초기화면, 인식 모드로 돌아가기.

* 부가 기능


* 디자인

---

<h2> 모듈 수준의 build.gradle 에 사용 할 Library 추가하기 </h2>


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


* CameraX & Google ML Kit Library Config
  * 아래 코드를 작성하고, Sync Now 하여 앱 설정 동기화 해 주기.
  
```kotlin

android {
			{
		... 생략 ...
		}
}

dependencies {
		def camerax_version = "1.1.0-alpha04"

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
















  
  
