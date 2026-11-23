package com.example.aaa_magnumbread.pesanan


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aaa_magnumbread.MainActivity
import com.example.aaa_magnumbread.alamat.AlamatActivity
import com.example.aaa_magnumbread.data.api.SupabaseClient
import com.example.aaa_magnumbread.data.model.AlamatModel
import com.example.aaa_magnumbread.data.model.PesananModel
import com.example.aaa_magnumbread.data.model.PesananRequest
import com.example.aaa_magnumbread.databinding.ActivityAturPesananBinding
import com.example.aaa_magnumbread.notifikasi.NotificationHelper
import com.example.aaa_magnumbread.util.DateUtil
import com.example.aaa_magnumbread.util.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Satu layar untuk dua mode:
 *  - CREATE : dibuka dari Pilih Layanan (membawa EXTRA_LAYANAN)
 *  - UPDATE : dibuka dari kartu di tab Pesanan (membawa EXTRA_ID dan data pesanan)
 */
class AturPesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAturPesananBinding

    private val daftarLayanan = listOf(
        "Cuci & Setrika", "Cuci Kering", "Setrika Saja",
        "Dry Cleaning", "Bulky Laundry", "Express"
    )
    private var daftarAlamat: List<AlamatModel> = emptyList()
    private val kalender: Calendar = Calendar.getInstance()

    private var editId = -1L            // -1 = mode CREATE, selain itu = mode UPDATE
    private var alamatTerpilihId = -1L  // untuk mengingat pilihan di spinner alamat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAturPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        setupSpinnerLayanan()
        bacaDataIntent()
        setupJadwal()

        binding.btnBack.setOnClickListener { finish() }
        binding.tvKelolaAlamat.setOnClickListener {
            startActivity(Intent(this, AlamatActivity::class.java))
        }
        binding.btnSimpan.setOnClickListener { simpan() }
    }

    // Alamat dimuat ulang tiap kembali ke layar ini (misal habis menambah alamat)
    override fun onResume() {
        super.onResume()
        muatAlamat()
    }

    private fun setupSpinnerLayanan() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daftarLayanan)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLayanan.adapter = adapter
    }

    private fun bacaDataIntent() {
        editId = intent.getLongExtra(EXTRA_ID, -1L)

        val index = daftarLayanan.indexOf(intent.getStringExtra(EXTRA_LAYANAN))
        if (index >= 0) binding.spLayanan.setSelection(index)

        if (editId != -1L) {
            // Mode UPDATE: isi form dengan data lama
            binding.tvJudul.text = "Ubah Pesanan"
            binding.btnSimpan.text = "Simpan Perubahan"
            alamatTerpilihId = intent.getLongExtra(EXTRA_ALAMAT_ID, -1L)
            binding.etCatatan.setText(intent.getStringExtra(EXTRA_CATATAN) ?: "")
            DateUtil.parseIso(intent.getStringExtra(EXTRA_JADWAL))?.let { kalender.time = it }
        } else {
            // Mode CREATE: default jadwal = besok jam 09.00
            kalender.add(Calendar.DAY_OF_YEAR, 1)
            kalender.set(Calendar.HOUR_OF_DAY, 9)
            kalender.set(Calendar.MINUTE, 0)
            kalender.set(Calendar.SECOND, 0)
        }
    }

    private fun setupJadwal() {
        tampilkanJadwal()

        binding.btnTanggal.setOnClickListener {
            DatePickerDialog(
                this,
                { _, tahun, bulan, hari ->
                    kalender.set(tahun, bulan, hari)
                    tampilkanJadwal()
                },
                kalender.get(Calendar.YEAR), kalender.get(Calendar.MONTH), kalender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnJam.setOnClickListener {
            TimePickerDialog(
                this,
                { _, jam, menit ->
                    kalender.set(Calendar.HOUR_OF_DAY, jam)
                    kalender.set(Calendar.MINUTE, menit)
                    tampilkanJadwal()
                },
                kalender.get(Calendar.HOUR_OF_DAY), kalender.get(Calendar.MINUTE), true
            ).show()
        }
    }

    private fun tampilkanJadwal() {
        val lokal = Locale("id", "ID")
        binding.btnTanggal.text = SimpleDateFormat("dd MMM yyyy", lokal).format(kalender.time)
        binding.btnJam.text = SimpleDateFormat("HH.mm", lokal).format(kalender.time)
    }

    // ===== Isi spinner alamat dari tabel alamat (READ) =====
    private fun muatAlamat() {
        val userId = SessionManager.getUserId(this)
        if (userId == -1L) return

        // Ingat pilihan sekarang agar tidak reset
        daftarAlamat.getOrNull(binding.spAlamat.selectedItemPosition)?.let { alamatTerpilihId = it.id }

        lifecycleScope.launch {
            try {
                daftarAlamat = SupabaseClient.api.getAlamat("eq.$userId")
                val teks = if (daftarAlamat.isEmpty()) listOf("Belum ada alamat")
                else daftarAlamat.map { "${it.label}: ${it.alamatLengkap}" }

                val adapter = ArrayAdapter(this@AturPesananActivity, android.R.layout.simple_spinner_item, teks)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spAlamat.adapter = adapter

                val index = daftarAlamat.indexOfFirst { it.id == alamatTerpilihId }
                if (index >= 0) binding.spAlamat.setSelection(index)
            } catch (e: Exception) {
                toast("Gagal memuat alamat. Periksa koneksi internet.")
            }
        }
    }

    // ===== CREATE / UPDATE pesanan =====
    private fun simpan() {
        val userId = SessionManager.getUserId(this)
        if (userId == -1L) {
            toast("Silakan daftar akun terlebih dahulu")
            return
        }
        val alamat = daftarAlamat.getOrNull(binding.spAlamat.selectedItemPosition)
        if (alamat == null) {
            toast("Tambahkan alamat jemput terlebih dahulu")
            return
        }
        if (kalender.time.before(Date())) {
            toast("Jadwal jemput tidak boleh di waktu yang sudah lewat")
            return
        }

        val layanan = daftarLayanan[binding.spLayanan.selectedItemPosition]
        val jadwal = DateUtil.toIso(kalender.time)
        val catatan = binding.etCatatan.text.toString().trim()

        binding.btnSimpan.isEnabled = false
        lifecycleScope.launch {
            try {
                if (editId == -1L) {
                    // ---- CREATE ----
                    val response = SupabaseClient.api.createPesanan(
                        PesananRequest(
                            userId = userId, alamatId = alamat.id, layanan = layanan,
                            jadwalJemput = jadwal, catatan = catatan
                        )
                    )
                    if (response.isSuccessful) {
                        val baru = response.body()?.firstOrNull()
                        NotificationHelper.show(
                            this@AturPesananActivity,
                            "Pesanan ${baru?.kode ?: ""} dibuat",
                            "Pesanan $layanan berhasil dibuat. Menunggu pembayaran."
                        )
                        toast("Pesanan berhasil dibuat")
                        val main = Intent(this@AturPesananActivity, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        if (baru != null) {
                            // Alur: Atur Pesanan -> Pembayaran (tombol back kembali ke Beranda)
                            startActivities(arrayOf(main, PembayaranActivity.intent(this@AturPesananActivity, baru.id)))
                        } else {
                            startActivity(main)
                        }
                        finish()
                    } else {
                        toast("Gagal membuat pesanan (kode ${response.code()})")
                    }
                } else {
                    // ---- UPDATE ----
                    val response = SupabaseClient.api.updatePesanan(
                        "eq.$editId",
                        PesananRequest(
                            alamatId = alamat.id, layanan = layanan,
                            jadwalJemput = jadwal, catatan = catatan
                        )
                    )
                    if (response.isSuccessful) {
                        toast("Pesanan berhasil diperbarui")
                        finish()
                    } else {
                        toast("Gagal memperbarui pesanan (kode ${response.code()})")
                    }
                }
            } catch (e: Exception) {
                toast("Tidak dapat terhubung ke server")
            } finally {
                binding.btnSimpan.isEnabled = true
            }
        }
    }

    private fun toast(pesan: String) = Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()

    companion object {
        const val EXTRA_ID = "PESANAN_ID"
        const val EXTRA_LAYANAN = "SERVICE_NAME"
        const val EXTRA_JADWAL = "PESANAN_JADWAL"
        const val EXTRA_ALAMAT_ID = "PESANAN_ALAMAT_ID"
        const val EXTRA_CATATAN = "PESANAN_CATATAN"

        // Dipakai tab Pesanan untuk membuka form dalam mode UPDATE
        fun intentEdit(context: Context, p: PesananModel): Intent =
            Intent(context, AturPesananActivity::class.java).apply {
                putExtra(EXTRA_ID, p.id)
                putExtra(EXTRA_LAYANAN, p.layanan)
                putExtra(EXTRA_JADWAL, p.jadwalJemput)
                putExtra(EXTRA_ALAMAT_ID, p.alamatId ?: -1L)
                putExtra(EXTRA_CATATAN, p.catatan)
            }
    }
}
