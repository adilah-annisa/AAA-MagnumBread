package com.example.aaa_magnumbread

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.aaa_magnumbread.databinding.ActivityRegistrationBinding
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.aaa_magnumbread.data.api.SupabaseClient
import com.example.aaa_magnumbread.data.model.UserRequest
import com.example.aaa_magnumbread.util.PasswordHasher
import com.example.aaa_magnumbread.util.SessionManager


class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.txtSudahPunyaAkun.setClickableLink("Masuk") { goToLogin() }
        binding.btnDaftar.setOnClickListener { daftarKeSupabase() }
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

    private fun daftarKeSupabase() {
        val nama = binding.etNama.text.toString().trim()
        val noHp = binding.etPhone.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val konfirmasi = binding.etConfirmPassword.text.toString()

        // ----- Validasi -----
        if (nama.isEmpty() || noHp.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != konfirmasi) {
            Toast.makeText(this, "Konfirmasi password tidak sama", Toast.LENGTH_SHORT).show()
            return
        }

        // ----- Kirim ke Supabase (INSERT ke tabel users) -----
        binding.btnDaftar.isEnabled = false
        lifecycleScope.launch {
            try {
                val body = UserRequest(
                    nama = nama,
                    noHp = noHp,
                    username = username,
                    password = PasswordHasher.hash(password) // yang dikirim HASH, bukan password asli
                )
                val response = SupabaseClient.api.register(body)

                if (response.isSuccessful) {
                    val user = response.body()?.firstOrNull()
                    if (user != null) {
                        SessionManager.saveUser(this@RegistrationActivity, user.id, user.nama)
                    }
                    Toast.makeText(this@RegistrationActivity, "Registrasi berhasil", Toast.LENGTH_SHORT).show()

                    // Pindah ke halaman berikutnya. Ganti dengan tujuan yang kamu pakai sebelumnya.
                    val intent = Intent(this@RegistrationActivity, VerifikasiActivity::class.java).apply {
                        putExtra("nama", nama)
                        putExtra("phone", noHp)
                        putExtra("username", username)
                        putExtra("password", password)
                    }
                    startActivity(intent)
                    finish()

                } else if (response.code() == 409) {
                    // 409 = pelanggaran UNIQUE (username sudah ada)
                    Toast.makeText(this@RegistrationActivity, "Username sudah digunakan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RegistrationActivity, "Registrasi gagal (kode ${response.code()})", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegistrationActivity, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            } finally {
                binding.btnDaftar.isEnabled = true
            }
        }
    }

}