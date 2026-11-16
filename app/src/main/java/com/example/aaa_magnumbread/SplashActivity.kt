package com.example.aaa_magnumbread

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.aaa_magnumbread.databinding.ActivitySplashBinding
import com.example.aaa_magnumbread.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({

            val done = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("onboarding_done", false)

            val target = if (done) RegistrationActivity::class.java
            else OnboardingActivity::class.java

            startActivity(Intent(this@SplashActivity, target))
            finish()

        }, 3000)
    }
}