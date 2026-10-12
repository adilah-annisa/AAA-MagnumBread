
package com.example.aaa_magnumbread

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.aaa_magnumbread.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.loadUrl(
            "https://www.idntimes.com/life/diy/7-tips-saat-memakai-jasa-laundry-daftar-jumlah-dan-jenis-pakaian-01-dv2xl-sjh4y9"
        )

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    @Deprecated("Deprecated in Android API, retained for compatibility")
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
