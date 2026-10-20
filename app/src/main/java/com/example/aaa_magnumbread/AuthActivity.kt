package com.example.aaa_magnumbread

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aaa_magnumbread.databinding.ActivityAuthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        val isLogin = sharedPref.getBoolean("isLogin", false)
        if (isLogin) {
            // Step untuk biaa ke BaseActivity
            val i = Intent(this, BaseActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            // etEmail sekarang dipakai untuk Username
            val username = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            // Data hasil registrasi (tersimpan setelah OTP berhasil)
            val savedUsername = sharedPref.getString("username", null)
            val savedPassword = sharedPref.getString("password", null)

            // Kondisi apabila User tidak mengisi Username dan Password
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password wajib diisi", Toast.LENGTH_SHORT).show()

                // Rule 1: username = password
                // Rule 2: username & password sama dengan data di SharedPreferences
            } else if (username == password ||
                (username == savedUsername && password == savedPassword)) {
                val editor = sharedPref.edit()
                editor.putBoolean("isLogin", true)
                editor.apply()

                //Masuk ke BaseActivity
                val i = Intent(this, BaseActivity::class.java)
                startActivity(i)
                finish()

                // Kondisi apabila Username dan Password Salah
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Oops!")
                    .setMessage("Username atau Password Salah! :( ")
                    .show()
            }
        }

        // Klik "Belum punya akun? Daftar" -> halaman Registrasi
        binding.noAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}