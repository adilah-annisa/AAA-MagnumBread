package com.example.aaa_magnumbread.alamat


import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aaa_magnumbread.data.api.SupabaseClient
import com.example.aaa_magnumbread.data.model.AlamatModel
import com.example.aaa_magnumbread.data.model.AlamatRequest
import com.example.aaa_magnumbread.databinding.ActivityAlamatBinding
import com.example.aaa_magnumbread.databinding.DialogAlamatBinding
import com.example.aaa_magnumbread.util.SessionManager
import kotlinx.coroutines.launch

/**
 * CRUD Alamat jemput/antar:
 *  READ   : daftar alamat milik user
 *  CREATE : tombol Tambah Alamat (dialog)
 *  UPDATE : tombol Edit pada kartu (dialog terisi data lama)
 *  DELETE : tombol Hapus pada kartu (dengan konfirmasi)
 */
class AlamatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlamatBinding

    private val adapter = AlamatAdapter(
        onEdit = { tampilkanForm(it) },
        onDelete = { konfirmasiHapus(it) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlamatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.rvAlamat.layoutManager = LinearLayoutManager(this)
        binding.rvAlamat.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }
        binding.btnTambah.setOnClickListener { tampilkanForm(null) }
    }

    override fun onResume() {
        super.onResume()
        muatAlamat()
    }

    // ===== READ =====
    private fun muatAlamat() {
        val userId = SessionManager.getUserId(this)
        if (userId == -1L) {
            binding.tvKosong.text = "Silakan daftar akun terlebih dahulu"
            binding.tvKosong.visibility = View.VISIBLE
            return
        }

        lifecycleScope.launch {
            try {
                val data = SupabaseClient.api.getAlamat("eq.$userId")
                adapter.submitList(data)
                binding.tvKosong.text = "Belum ada alamat.\nKetuk \"Tambah Alamat\" untuk menambahkan."
                binding.tvKosong.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                toast("Gagal memuat alamat. Periksa koneksi internet.")
            }
        }
    }

    // ===== Dialog form dipakai untuk CREATE (lama = null) dan UPDATE =====
    private fun tampilkanForm(lama: AlamatModel?) {
        val form = DialogAlamatBinding.inflate(layoutInflater)
        lama?.let {
            form.etLabel.setText(it.label)
            form.etAlamatLengkap.setText(it.alamatLengkap)
            form.etCatatan.setText(it.catatan ?: "")
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (lama == null) "Tambah Alamat" else "Ubah Alamat")
            .setView(form.root)
            .setPositiveButton("Simpan", null) // listener dipasang di bawah agar dialog tidak menutup otomatis
            .setNegativeButton("Batal", null)
            .create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#6B7C93"))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setTextColor(Color.parseColor("#1677E8"))
            setOnClickListener {
                val label = form.etLabel.text.toString().trim()
                val alamat = form.etAlamatLengkap.text.toString().trim()
                val catatan = form.etCatatan.text.toString().trim()

                if (label.isEmpty() || alamat.isEmpty()) {
                    toast("Label dan alamat lengkap wajib diisi")
                } else {
                    simpanAlamat(lama, label, alamat, catatan, dialog)
                }
            }
        }
    }

    // ===== CREATE / UPDATE =====
    private fun simpanAlamat(lama: AlamatModel?, label: String, alamat: String, catatan: String, dialog: AlertDialog) {
        val userId = SessionManager.getUserId(this)
        if (userId == -1L) {
            toast("Silakan daftar akun terlebih dahulu")
            return
        }

        lifecycleScope.launch {
            try {
                val berhasil = if (lama == null) {
                    SupabaseClient.api.createAlamat(
                        AlamatRequest(label = label, alamatLengkap = alamat, catatan = catatan, userId = userId)
                    ).isSuccessful
                } else {
                    SupabaseClient.api.updateAlamat(
                        "eq.${lama.id}",
                        AlamatRequest(label = label, alamatLengkap = alamat, catatan = catatan)
                    ).isSuccessful
                }

                if (berhasil) {
                    toast(if (lama == null) "Alamat ditambahkan" else "Alamat diperbarui")
                    dialog.dismiss()
                    muatAlamat()
                } else {
                    toast("Gagal menyimpan alamat")
                }
            } catch (e: Exception) {
                toast("Tidak dapat terhubung ke server")
            }
        }
    }

    // ===== DELETE =====
    private fun konfirmasiHapus(alamat: AlamatModel) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Hapus alamat?")
            .setMessage("Alamat \"${alamat.label}\" akan dihapus.")
            .setPositiveButton("Hapus") { _, _ -> hapusAlamat(alamat) }
            .setNegativeButton("Batal", null)
            .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#EF4444"))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#6B7C93"))
    }

    private fun hapusAlamat(alamat: AlamatModel) {
        lifecycleScope.launch {
            try {
                val response = SupabaseClient.api.deleteAlamat("eq.${alamat.id}")
                if (response.isSuccessful) {
                    toast("Alamat dihapus")
                    muatAlamat()
                } else {
                    toast("Gagal menghapus alamat")
                }
            } catch (e: Exception) {
                toast("Tidak dapat terhubung ke server")
            }
        }
    }

    private fun toast(pesan: String) = Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
}
