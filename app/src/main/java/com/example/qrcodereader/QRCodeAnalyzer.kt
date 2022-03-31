package com.example.qrcodereader

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer(val onDetectListener: OnDetectListener) : ImageAnalysis.Analyzer {
    // Interface를 생성하지 않았을 때에는 QRCodeAnalyzer로 QRCode를 넘기는것은 가능했지만, Main으로 넘기지 못했음.
    // 생성한 Interface인 onDetectListener를 통해 주 생성자의 인자로 MainActivity에게 넘겨주어야 함.
    // 이렇게 하면 MainActivity과 Analyzer에서 Listener를 통해 소통할 수 있게 됨.

    private val scanner = BarcodeScanning.getClient()
    // 바코드 스캐닝 객체를 scanner 변수에 할당
    // ML 키트 라이브러리의 임포트가 필요함.


    // ImageProxy 는 ByteBuffer에 Image Bit를 채움.
    // https://developer.android.com/training/camerax/analyze?hl=ko => Proxy 검색
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image =
                InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                ) // 이미지가 찍힐 당시 카메라의 회전 각도를 고려하여
            // InputImage를 생성함.

            // scanner를 통해 이미지를 분석. (Analyze)
            // 3가지 Listener를 각각 주어 결과를 확인할 수 있음.
            // Failure => Error 내용을 Log에 Print 함.
            // Complete => 스캐너가 이미지 분석을 완료했을 때 프록시를 닫는 작업.
            // Success => QR 코드 인식이 성공했을 때의 작업을 넣어 주어야 함.
            // 단, QR 코드 인식 성공 Message와 Data는 이거야! 하고 보여주는 것 등등.. 이 작업은 Interface가 함.
            scanner.process(image)
                .addOnSuccessListener {
                    qrCodes -> for(qrCode in qrCodes) { // qrCodes의 값을 qrCode로 넘겨 반복
                        onDetectListener.onDetect(qrCode.rawValue ?: "")
                    }// QR 코드가 성공적으로 찍히게 되면 onDet....를 실행함.
                    // rawValue가 존재하면 그 값을 보내고, 값이 null이면 빈 문자열을 보냄. (엘비스 프레슬리)
                    // qrCodes와 같은 배열이 넘어오는 이유는 한 화면에 다수의 QR코드가 찍히게 되면 모든 QR코드를
                    // 분석해 각각 배열로 보내기 때문임.
                }
                .addOnFailureListener{
                    it.printStackTrace()
                }
                .addOnCompleteListener{
                    imageProxy.close()
                }
        }
    }
}