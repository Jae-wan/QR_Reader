package com.example.qrcodereader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qrcodereader.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root // viewBinding 설정
        setContentView(view)

        val result = intent.getStringExtra("msg") ?: "데이터가 존재하지 않습니다."
        // 엘비스 프레슬리를 활용하여 true, false에 따른 값을 설정하되
        // MainActivity에서 putExtra로 보낸 데이터를 getStringExtra로 받았음.
        // 값은 result 변수에 넣어줌.

        // result 값을 가지고 setUI function을 동작
        setUI(result)

    }

    // setUI는 화면을 초기화하는 작업을 함. TextView에 데이터를 넣고, 버튼을 누르면 액티비티 종료 로직을 넣었음.
    private fun setUI(result: String) {
        binding.tvContent.text = result // 넘어온 QR코드 속 Data를 TextView에다 설정 해 줌
        binding.btnGoBack.setOnClickListener {
            finish() // [돌아가기] 버튼을 눌렀을 때 ResultActivity를 종료.
        }
    }
}