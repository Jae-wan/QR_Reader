package com.example.qrcodereader

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.qrcodereader.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding // 바인딩 변수 생성

    private lateinit var cameraProviderFuture :
            ListenableFuture<ProcessCameraProvider>
    // ListenableFuture 형 변수를 선언하였다.
    // Task가 제대로 끝났을 때 동작을 지정해줄 수 있다.
    // (참고로 Future는 안드로이드 병렬 프로그래밍에서 Task가 제대로 끝났는지 확인할 때 사용함.

    private val PERMISSIONS_REQUEST_CODE = 1 // 그냥 태그해둔 것으로, 권한 요청 후 결과를 비교할 때 사용
    // onRequestPermissionsResult에서 바등ㄹ 때 필요, 0과 같거나 큰 양수이면 어떤 수든 상관 없음.

    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA) // 카메라 권한 지정

    private var isDetected = false
    // Image Analyze가 실시간으로 계속 이루어지므로, onDetect() 함수의 여러번 호출을 방지하기 위해 Boolean 변수 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewBinding 설정
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        if (!hasPermissions(this)) {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        } else {
            startCamera() // 카메라 시작
            // onResume( ) 이 아니어도 되는거?
        }
    }

    override fun onResume() {
        super.onResume()
        isDetected = false
        // 사용자의 Focus가 MainActivity로 돌아온다면 다시 QR코드를 인식하게 할 수 있도록 onResume() 함수를 오버라이드하여
        // isDetected 를 false로 돌려준다. (중요한거임)
    }

    // CAMERA 권한의 유무를 확인한다.
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) ==
                PackageManager.PERMISSION_GRANTED
    }
        // all은 PERMISSIONS_REQUIRED 배열의 원소가 모두 조건문을 만족하면 true를 반환, 아니라면 false를 반환한다.
        // https://developer.android.com/training/permissions/requesting?hl=ko
        // 앱에 권한이 있는지에 따라 PERMISSION_GRANTED or PERMISSION_DENIED 를 반환한다.


    override fun onRequestPermissionsResult( // 권한 요청 콜백 함수, Activity Class 포함 함수이므로 Override 해 주어야 함.
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 권한이 수락되면 startCamera()를 호출 / 권한이 거부되면 Activity 종료 finish() 를 호출
        // hasPermissions에서 requestPermissions의 인수로 넣은 PERMISSIONS_REQUEST_CODE와 맞는지 대조 함.
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if(PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                Toast.makeText(this@MainActivity, "권한 요청이 승인되었습니다.",
                    Toast.LENGTH_LONG).show()
                startCamera()
            } else {
                Toast.makeText(this@MainActivity, "권한 요청이 거부되었습니다.",
                    Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    // 카메라 시작 (미리보기와 이미지 분석 시작)
    fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this) // 객체의 참조값 할당
        cameraProviderFuture.addListener(Runnable { // cameraProviderFuture 태스크가 끝나면 실행 됨.
            val cameraProvider = cameraProviderFuture.get()
            // 카메라 생명주기를 Activity / Fragment와 같은 생명주기에 바인드 해 주는 ProcessCameraProvider 객체를 가져옴
            val preview = getPreview() // 미리보기 객체를 가져옴
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // 후면 카메라를 DEFAULT로 선택

            val imageAnalysis = getImageAnalysis()

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            // 미리보기 기능 선택
                // 미리보기, 이미지 분석, 이미지 캡쳐 중 무엇을 쓸지 지정함.
                // 일단 미리보기 (preview) 만 넣어주었음.

        }, ContextCompat.getMainExecutor(this))

    }

    // 미리보기 기능을 설정하고, 설정이 완료 된 객체를 반환하는 함수
    fun getPreview() : Preview {
        val preview : Preview = Preview.Builder().build() // Preview 객체 생성
        preview.setSurfaceProvider(binding.barcodePreview.surfaceProvider)
        // setSurfaceProvider() : Preview 객체에 SurfaceProvider 를 설정 해 줌.
        // SurfaceProvider는 Preview에 Surface를 제공 해 주는 인터페이스임. (화면을 보이게 해 주는..)
        // 즉, 픽셀들이 모여 있는 객체가 Surface(지표면..?) 이라고 생각하면 된다.


        return preview
    }

    fun getImageAnalysis() : ImageAnalysis {
        val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        val imageAnalysis = ImageAnalysis.Builder().build()

        imageAnalysis.setAnalyzer(cameraExecutor,
            QRCodeAnalyzer(object : OnDetectListener {
                override fun onDetect(msg: String) {
                    if (!isDetected) { // (!false => true 이므로, if 동작)
                        isDetected = true // isDetected가 true이면 true로 변경. (데이터 감지 되었으므로)
                        // 한번도 QR코드가 인식된 적이 없는지 검사함. 중복 실행을 막는 코드임.

                        // 데이터 감지되었다면 Result 화면으로 이동할 수 있도록 하는 Intent 생성
                        val intent = Intent(this@MainActivity, ResultActivity::class.java)
                        intent.putExtra("msg", msg)
                        startActivity(intent)
                        // intent를 통해 다음 Activity로 데이터를 putExtra(키-쌍) 형태로 넘겨줌.
                        // 첫번째 인수 "msg"는 키 이며, 두번째 인수 msg는
                    // over fun onDetect(msg: String)에서 넘어온 문자열 값임.


                    }
                }
            }))
        return imageAnalysis
    }
    // QRCodeAnalyzer 객체를 생성해 setAnalyzer 함수의 인수로 넣어준다.
    // QRCodeAnalyzer는 OnDetectListener Interface를 구현해야 했었다.
    // object를 통해 Interface 객체를 만들어주고 onDetect() 함수를 오버라이드 했다.
    // onDetect() 함수가 QRCodeAnalyzer에서 콜 되었을 때 동작을 여기서 정의한다.
    // 현재 프로젝트에서는 단순히 Toast(팝업) 형식으로 메시징 하였다.
}