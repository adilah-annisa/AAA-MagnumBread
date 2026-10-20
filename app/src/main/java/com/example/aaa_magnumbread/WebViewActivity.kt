package com.example.aaa_magnumbread

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.aaa_magnumbread.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            title = "Tips Laundry"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.webView.webViewClient = WebViewClient()

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true

        binding.webView.loadUrl(
            "https://www.idntimes.com/life/diy/7-tips-saat-memakai-jasa-laundry-daftar-jumlah-dan-jenis-pakaian-01-dv2xl-sjh4y9"
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}