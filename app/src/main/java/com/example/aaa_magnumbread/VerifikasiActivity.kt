package com.example.aaa_magnumbread

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aaa_magnumbread.databinding.ActivityVerifikasiBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VerifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifikasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Data dari halaman Registrasi
        val nama = intent.getStringExtra("nama").orEmpty()
        val phone = intent.getStringExtra("phone").orEmpty()
        val username = intent.getStringExtra("username").orEmpty()
        val password = intent.getStringExtra("password").orEmpty()

        binding.otpDesc.text =
            "Masukkan kode OTP yang dikirim ke nomor ${maskPhone(phone)}"

        binding.btnVerifikasi.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()

            when {
                // OTP kosong
                otp.isEmpty() ->
                    showErrorDialog("Kode OTP tidak boleh kosong.")

                // OTP harus sama dengan nomor handphone saat registrasi
                otp != phone ->
                    showErrorDialog("Kode OTP salah. Silakan coba lagi.")

                // OTP benar -> simpan data registrasi ke SharedPreferences
                else -> {
                    val editor = getSharedPreferences("user_pref", MODE_PRIVATE).edit()
                    editor.putString("nama", nama)
                    editor.putString("phone", phone)
                    editor.putString("username", username)
                    editor.putString("password", password)
                    editor.apply()

                    Toast.makeText(this, "Registrasi berhasil, silakan login", Toast.LENGTH_SHORT).show()

                    val i = Intent(this, AuthActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(i)
                    finish()
                }
            }
        }
    }

    private fun maskPhone(phone: String): String {
        if (phone.length < 6) return phone
        return phone.take(4) + "****" + phone.takeLast(2)
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Oops!")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}