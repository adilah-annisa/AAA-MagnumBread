package com.example.aaa_magnumbread.pesanan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aaa_magnumbread.R
import com.example.aaa_magnumbread.data.api.SupabaseClient
import com.example.aaa_magnumbread.data.model.PesananModel
import com.example.aaa_magnumbread.data.model.PesananRequest
import com.example.aaa_magnumbread.databinding.ActivityPembayaranBinding
import com.example.aaa_magnumbread.notifikasi.NotificationHelper
import com.example.aaa_magnumbread.util.DateUtil
import com.example.aaa_magnumbread.util.HargaUtil
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Halaman Pembayaran.
 * Membaca pesanan (READ), lalu menekan "Bayar" melakukan PATCH:
 * status "Menunggu Pembayaran" -> "Dijemput" + simpan jumlah, total, metode, waktu bayar.
 */
class PembayaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBinding
    private var pesanan: PesananModel? = null
    private var gratisOngkir = false
    private var jumlah = 1
    private var total = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val id = intent.getLongExtra(EXTRA_ID, -1L)
        if (id == -1L) {
            finish()
            return
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnKurang.setOnClickListener {
            if (jumlah > 1) { jumlah--; hitung() }
        }
        binding.btnTambah.setOnClickListener {
            if (jumlah < 50) { jumlah++; hitung() }
        }
        binding.btnBayar.isEnabled = false
        binding.btnBayar.setOnClickListener { konfirmasi() }

        muatPesanan(id)
    }

    // ===== READ pesanan yang akan dibayar =====
    private fun muatPesanan(id: Long) {
        lifecycleScope.launch {
            try {
                val data = SupabaseClient.api.getPesananById("eq.$id").firstOrNull()
                if (data == null) {
                    toast("Pesanan tidak ditemukan")
                    finish()
                    return@launch
                }
                // Sudah dibayar -> langsung ke Tracking
                if (data.status != StatusPesanan.MENUNGGU) {
                    toast("Pesanan ini sudah dibayar")
                    startActivity(TrackingActivity.intent(this@PembayaranActivity, data.id))
                    finish()
                    return@launch
                }

                // Promo "Gratis Jemput & Antar untuk pemesanan pertama"
                val semua = SupabaseClient.api.getPesanan("eq.${data.userId}")
                gratisOngkir = semua.minOfOrNull { it.id } == data.id

                pesanan = data
                jumlah = data.jumlah.coerceAtLeast(1)
                binding.tvKode.text = data.kode
                binding.tvLayanan.text = data.layanan
                binding.tvJadwal.text = "Jemput: ${DateUtil.tampilkan(data.jadwalJemput)}"
                val a = data.alamat
                binding.tvAlamat.text = if (a?.label.isNullOrBlank()) "-" else "${a?.label}: ${a?.alamatLengkap}"
                binding.tvLabelJumlah.text = "Estimasi (${HargaUtil.satuan(data.layanan)})"
                hitung()
                binding.btnBayar.isEnabled = true
            } catch (e: Exception) {
                toast("Gagal memuat pesanan. Periksa koneksi internet.")
            }
        }
    }

    // ===== Hitung rincian biaya =====
    private fun hitung() {
        val p = pesanan ?: return
        val satuan = HargaUtil.satuan(p.layanan)
        val harga = HargaUtil.hargaSatuan(p.layanan)
        val subtotal = harga * jumlah
        val ongkir = if (gratisOngkir) 0L else HargaUtil.ONGKIR
        total = subtotal + ongkir

        binding.tvJumlah.text = jumlah.toString()
        binding.tvHargaSatuan.text = "${HargaUtil.rupiah(harga)} / $satuan"
        binding.tvSubtotal.text = HargaUtil.rupiah(subtotal)
        if (gratisOngkir) {
            binding.tvOngkir.text = "Gratis"
            binding.tvOngkir.setTextColor(Color.parseColor("#22C55E"))
        } else {
            binding.tvOngkir.text = HargaUtil.rupiah(ongkir)
            binding.tvOngkir.setTextColor(Color.parseColor("#102A43"))
        }
        binding.tvTotal.text = HargaUtil.rupiah(total)
    }

    private fun metodeTerpilih(): String = when (binding.rgMetode.checkedRadioButtonId) {
        R.id.rbEwallet -> "E-Wallet"
        R.id.rbCod -> "Bayar di Tempat"
        else -> "Transfer Bank"
    }

    private fun konfirmasi() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Pembayaran")
            .setMessage("Bayar ${HargaUtil.rupiah(total)} dengan ${metodeTerpilih()}?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Bayar") { _, _ -> bayar() }
            .show()
    }

    // ===== UPDATE status + data pembayaran (PATCH) =====
    private fun bayar() {
        val p = pesanan ?: return
        val metode = metodeTerpilih()
        binding.btnBayar.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = SupabaseClient.api.updatePesanan(
                    "eq.${p.id}",
                    PesananRequest(
                        status = StatusPesanan.DIJEMPUT,
                        jumlah = jumlah,
                        totalHarga = total,
                        metodeBayar = metode,
                        dibayarPada = DateUtil.toIso(Date())
                    )
                )
                if (response.isSuccessful) {
                    NotificationHelper.show(
                        this@PembayaranActivity,
                        "Pembayaran ${p.kode} berhasil",
                        "${HargaUtil.rupiah(total)} via $metode. Kurir akan segera menjemput."
                    )
                    toast("Pembayaran berhasil")
                    startActivity(TrackingActivity.intent(this@PembayaranActivity, p.id))
                    finish()
                } else {
                    toast("Pembayaran gagal (kode ${response.code()})")
                    binding.btnBayar.isEnabled = true
                }
            } catch (e: Exception) {
                toast("Tidak dapat terhubung ke server")
                binding.btnBayar.isEnabled = true
            }
        }
    }

    private fun toast(pesan: String) = Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()

    companion object {
        private const val EXTRA_ID = "PEMBAYARAN_PESANAN_ID"

        fun intent(context: Context, pesananId: Long): Intent =
            Intent(context, PembayaranActivity::class.java).putExtra(EXTRA_ID, pesananId)
    }
}
