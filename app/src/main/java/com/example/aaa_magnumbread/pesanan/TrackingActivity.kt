package com.example.aaa_magnumbread.pesanan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aaa_magnumbread.R
import com.example.aaa_magnumbread.data.api.SupabaseClient
import com.example.aaa_magnumbread.data.model.PesananModel
import com.example.aaa_magnumbread.data.model.PesananRequest
import com.example.aaa_magnumbread.databinding.ActivityTrackingBinding
import com.example.aaa_magnumbread.databinding.ItemTimelineBinding
import com.example.aaa_magnumbread.notifikasi.NotificationHelper
import com.example.aaa_magnumbread.util.DateUtil
import com.example.aaa_magnumbread.util.HargaUtil
import kotlinx.coroutines.launch

/**
 * Tracking pesanan: timeline status dari data Supabase.
 * Tombol "Simulasi" mewakili Admin/Kurir yang mengubah status
 * (di sistem nyata dilakukan lewat dashboard Admin pada website).
 */
class TrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingBinding
    private var pesananId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        pesananId = intent.getLongExtra(EXTRA_ID, -1L)
        if (pesananId == -1L) {
            finish()
            return
        }
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        muat()
    }

    // ===== READ =====
    private fun muat() {
        lifecycleScope.launch {
            try {
                val data = SupabaseClient.api.getPesananById("eq.$pesananId").firstOrNull()
                if (data == null) {
                    toast("Pesanan tidak ditemukan")
                    finish()
                    return@launch
                }
                tampilkan(data)
            } catch (e: Exception) {
                toast("Gagal memuat pesanan. Periksa koneksi internet.")
            }
        }
    }

    private fun tampilkan(p: PesananModel) {
        val belumBayar = p.status == StatusPesanan.MENUNGGU

        binding.tvKode.text = p.kode
        binding.tvStatus.text = p.status
        binding.tvLayanan.text = if (belumBayar) p.layanan
        else "${p.layanan}  •  ${p.jumlah} ${HargaUtil.satuan(p.layanan)}"
        binding.tvJadwal.text = "Jemput: ${DateUtil.tampilkan(p.jadwalJemput)}"
        binding.tvTotal.text = if (belumBayar) "Belum dibayar" else HargaUtil.rupiah(p.totalHarga)
        binding.tvMetode.text = if (belumBayar) "-" else (p.metodeBayar ?: "-")

        buatTimeline(p.status)
        aturTombol(p)
    }

    // ===== Timeline: 5 langkah, yang sudah tercapai berwarna biru =====
    private fun buatTimeline(statusSekarang: String) {
        val biru = ContextCompat.getColor(this, R.color.primary_blue)
        val abu = ContextCompat.getColor(this, R.color.border_gray)
        val teksUtama = ContextCompat.getColor(this, R.color.text_primary)
        val teksSekunder = ContextCompat.getColor(this, R.color.text_secondary)

        val indexSekarang = StatusPesanan.urutan.indexOf(statusSekarang)
        binding.llTimeline.removeAllViews()

        StatusPesanan.urutan.forEachIndexed { i, status ->
            val row = ItemTimelineBinding.inflate(layoutInflater, binding.llTimeline, false)
            val tercapai = i <= indexSekarang

            row.tvStep.text = status
            row.tvStepDesc.text = StatusPesanan.deskripsi[status]
            row.vDot.setBackgroundResource(if (tercapai) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
            row.tvStep.setTextColor(
                when {
                    i == indexSekarang -> biru
                    tercapai -> teksUtama
                    else -> teksSekunder
                }
            )

            if (i == StatusPesanan.urutan.lastIndex) {
                row.vLine.visibility = View.INVISIBLE
            } else {
                row.vLine.setBackgroundColor(if (i < indexSekarang) biru else abu)
            }
            binding.llTimeline.addView(row.root)
        }
    }

    private fun aturTombol(p: PesananModel) {
        val berikutnya = StatusPesanan.berikutnya(p.status)
        binding.tvCatatanDemo.visibility = View.GONE

        when {
            p.status == StatusPesanan.MENUNGGU -> {
                binding.btnAksi.visibility = View.VISIBLE
                binding.btnAksi.text = "Bayar Sekarang"
                binding.btnAksi.setOnClickListener {
                    startActivity(PembayaranActivity.intent(this, p.id))
                }
            }
            berikutnya != null -> {
                binding.btnAksi.visibility = View.VISIBLE
                binding.btnAksi.text = "Simulasi: lanjut ke \"$berikutnya\""
                binding.tvCatatanDemo.visibility = View.VISIBLE
                binding.btnAksi.setOnClickListener { ubahStatus(p, berikutnya) }
            }
            else -> binding.btnAksi.visibility = View.GONE
        }
    }

    // ===== UPDATE status (PATCH) =====
    private fun ubahStatus(p: PesananModel, statusBaru: String) {
        binding.btnAksi.isEnabled = false
        lifecycleScope.launch {
            try {
                val response = SupabaseClient.api.updatePesanan(
                    "eq.${p.id}", PesananRequest(status = statusBaru)
                )
                if (response.isSuccessful) {
                    NotificationHelper.show(
                        this@TrackingActivity,
                        "Pesanan ${p.kode}: $statusBaru",
                        StatusPesanan.deskripsi[statusBaru] ?: ""
                    )
                    muat()
                } else {
                    toast("Gagal mengubah status (kode ${response.code()})")
                }
            } catch (e: Exception) {
                toast("Tidak dapat terhubung ke server")
            } finally {
                binding.btnAksi.isEnabled = true
            }
        }
    }

    private fun toast(pesan: String) = Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()

    companion object {
        private const val EXTRA_ID = "TRACKING_PESANAN_ID"

        fun intent(context: Context, pesananId: Long): Intent =
            Intent(context, TrackingActivity::class.java).putExtra(EXTRA_ID, pesananId)
    }
}
