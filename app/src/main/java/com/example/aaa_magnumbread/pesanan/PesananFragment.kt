package com.example.aaa_magnumbread.pesanan

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aaa_magnumbread.ChooseCatalogActivity
import com.example.aaa_magnumbread.data.api.SupabaseClient
import com.example.aaa_magnumbread.data.model.PesananModel
import com.example.aaa_magnumbread.databinding.FragmentPesananBinding
import com.example.aaa_magnumbread.notifikasi.NotificationHelper
import com.example.aaa_magnumbread.util.SessionManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PesananFragment : Fragment() {

    private var _binding: FragmentPesananBinding? = null
    private val binding get() = _binding!!

    // Data lokal hasil READ dari Supabase
    private val daftarPesanan = mutableListOf<PesananModel>()

    // Ketuk kartu -> pilihan aksi tergantung status pesanan
    private val adapter = PesananAdapter { pesanan -> tampilkanAksi(pesanan) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPesananBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPesanan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPesanan.adapter = adapter

        // CREATE: tombol Pesan -> pilih layanan -> atur pesanan
        binding.btnPesanBaru.setOnClickListener {
            startActivity(Intent(requireContext(), ChooseCatalogActivity::class.java))
        }

        setupSwipeToDelete()
    }

    // Muat ulang tiap kali halaman tampil (misal setelah kembali dari Atur Pesanan)
    override fun onResume() {
        super.onResume()
        muatPesanan()
    }

    // Belum dibayar : Bayar / Ubah (UPDATE) / Lacak.  Sudah dibayar : langsung Tracking.
    private fun tampilkanAksi(p: PesananModel) {
        if (p.status == StatusPesanan.MENUNGGU) {
            val pilihan = arrayOf("Bayar Sekarang", "Ubah Pesanan", "Lacak Pesanan")
            AlertDialog.Builder(requireContext())
                .setTitle("Pesanan ${p.kode}")
                .setItems(pilihan) { _, which ->
                    when (which) {
                        0 -> startActivity(PembayaranActivity.intent(requireContext(), p.id))
                        1 -> startActivity(AturPesananActivity.intentEdit(requireContext(), p))
                        else -> startActivity(TrackingActivity.intent(requireContext(), p.id))
                    }
                }
                .show()
        } else {
            startActivity(TrackingActivity.intent(requireContext(), p.id))
        }
    }

    // ===== READ =====
    private fun muatPesanan() {
        val userId = SessionManager.getUserId(requireContext())
        if (userId == -1L) {
            binding.tvKosong.text = "Silakan daftar akun terlebih dahulu"
            binding.tvKosong.visibility = View.VISIBLE
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val data = SupabaseClient.api.getPesanan("eq.$userId")
                daftarPesanan.clear()
                daftarPesanan.addAll(data)
                tampilkanDaftar()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(), "Gagal memuat pesanan. Periksa koneksi internet.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun tampilkanDaftar() {
        adapter.submitList(daftarPesanan.toList())
        binding.tvKosong.text = "Belum ada pesanan.\nKetuk \"+ Pesan\" untuk membuat pesanan."
        binding.tvKosong.visibility = if (daftarPesanan.isEmpty()) View.VISIBLE else View.GONE
    }

    // ===== DELETE: geser ke kiri + URUNGKAN =====
    // Item langsung hilang dari layar. Penghapusan di Supabase baru dijalankan
    // setelah Snackbar tertutup TANPA ditekan URUNGKAN.
    private fun setupSwipeToDelete() {
        val density = resources.displayMetrics.density
        val bgPaint = Paint().apply { color = Color.parseColor("#EF4444") }
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 14 * density
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            // Hanya pesanan yang BELUM dibayar yang boleh dibatalkan (geser)
            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val item = adapter.currentList.getOrNull(viewHolder.bindingAdapterPosition) ?: return 0
                return if (item.status == StatusPesanan.MENUNGGU) super.getSwipeDirs(recyclerView, viewHolder) else 0
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posisi = viewHolder.bindingAdapterPosition
                if (posisi == RecyclerView.NO_POSITION) return

                val dihapus = daftarPesanan.removeAt(posisi)
                tampilkanDaftar()

                val appContext = requireContext().applicationContext

                Snackbar.make(binding.root, "Pesanan ${dihapus.kode} dibatalkan", Snackbar.LENGTH_LONG)
                    .setAction("URUNGKAN") {
                        daftarPesanan.add(posisi.coerceAtMost(daftarPesanan.size), dihapus)
                        tampilkanDaftar()
                    }
                    .setActionTextColor(Color.parseColor("#27C2F3"))
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event != DISMISS_EVENT_ACTION) {
                                hapusDiServer(dihapus, appContext)
                            }
                        }
                    })
                    .show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (dX < 0) {
                    val item = viewHolder.itemView
                    val radius = 16 * density
                    val area = RectF(
                        item.right + dX, item.top.toFloat(),
                        item.right.toFloat(), item.bottom.toFloat()
                    )
                    c.drawRoundRect(area, radius, radius, bgPaint)

                    val label = "Batalkan"
                    val padding = 20 * density
                    val textWidth = textPaint.measureText(label)
                    if (-dX > textWidth + padding * 2) {
                        val x = item.right - padding - textWidth
                        val y = item.top + item.height / 2f -
                                (textPaint.descent() + textPaint.ascent()) / 2f
                        c.drawText(label, x, y, textPaint)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(callback).attachToRecyclerView(binding.rvPesanan)
    }

    // Jalan di scope sendiri supaya tetap selesai walau fragment sudah ditutup
    private fun hapusDiServer(pesanan: PesananModel, appContext: android.content.Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = SupabaseClient.api.deletePesanan("eq.${pesanan.id}")
                if (response.isSuccessful) {
                    NotificationHelper.show(
                        appContext,
                        "Pesanan ${pesanan.kode} dibatalkan",
                        "Pesanan ${pesanan.layanan} berhasil dibatalkan."
                    )
                }
            } catch (e: Exception) {
                // Gagal hapus: data akan muncul lagi saat halaman dimuat ulang
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}