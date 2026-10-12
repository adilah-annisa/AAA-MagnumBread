package com.example.aaa_magnumbread

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.aaa_magnumbread.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({

            val sharedPref = getSharedPreferences(
                "laundry_pref",
                MODE_PRIVATE
            )

            val isLogin = sharedPref.getBoolean("isLogin", false)

            val tujuan = if (isLogin) {
                MainActivity::class.java
            } else {
                AuthActivity::class.java
            }

            startActivity(Intent(this, tujuan))
            finish()

        }, 3000)
    }
}
