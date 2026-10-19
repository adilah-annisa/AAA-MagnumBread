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
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Kondisi apabila User tidak mengisi Email dan Password
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT)
            // Kondisi apabila Email tidak pakai '@mail.com'
            } else if (!email.endsWith("@mail.com")) {
                binding.etEmail.error = "Email harus berformat '@mail.com'"
            } else if (email == "laundryexpress@mail.com" && password == "express123" ) {
                val editor = sharedPref.edit()
                editor.putBoolean("isLogin", true)
                editor.apply()

                //Masuk ke BaseActivity
                val i = Intent(this, BaseActivity::class.java)
                startActivity(i)
                finish()
            // Kondisi apabila Email dan Password Salah
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Oops!")
                    .setMessage("Email atau Password Salah! :( ")
                    .show()
            }
        }
    }
}
