package com.example.aaa_magnumbread

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aaa_magnumbread.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            val username = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Username dan password wajib diisi",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (username == "adilah" && password == "12345") {

                val prefs = getSharedPreferences(
                    "laundry_pref",
                    MODE_PRIVATE
                )

                prefs.edit()
                    .putBoolean("isLogin", true)
                    .apply()

                Toast.makeText(
                    this,
                    "Login berhasil!",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {

                Toast.makeText(
                    this,
                    "Username atau password salah",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}