package com.example.aaa_magnumbread

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aaa_magnumbread.databinding.ActivitySnackBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySnackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            finish()
        }
    }
}
