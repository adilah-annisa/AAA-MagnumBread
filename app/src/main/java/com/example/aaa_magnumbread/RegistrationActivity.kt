package com.example.aaa_magnumbread

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.aaa_magnumbread.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.txtSudahPunyaAkun.setClickableLink("Masuk") { goToLogin() }
        binding.btnDaftar.setOnClickListener { validateAndRegister() }
    }

    private fun validateAndRegister() {
        val nama = binding.etNama.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

        when {
            nama.isEmpty() ->
                showError(binding.etNama, "Nama wajib diisi")
            phone.length < 10 ->
                showError(binding.etPhone, "No. Handphone minimal 10 digit")
            username.isEmpty() ->
                showError(binding.etUsername, "Username wajib diisi")
            password.isEmpty() ->
                showError(binding.etPassword, "Password wajib diisi")
            password != confirm ->
                showError(binding.etConfirmPassword, "Konfirmasi password tidak sama")
            else -> {
                // Data belum disimpan di sini. Disimpan setelah OTP berhasil diverifikasi.
                val intent = Intent(this, VerifikasiActivity::class.java)
                intent.putExtra("nama", nama)
                intent.putExtra("phone", phone)
                intent.putExtra("username", username)
                intent.putExtra("password", password)
                startActivity(intent)
            }
        }
    }

    private fun showError(field: EditText, message: String) {
        field.error = message
        field.requestFocus()
    }

    private fun goToLogin() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}