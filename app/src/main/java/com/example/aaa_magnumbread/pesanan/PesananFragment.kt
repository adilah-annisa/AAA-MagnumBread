package com.example.aaa_magnumbread.pesanan

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aaa_magnumbread.databinding.FragmentPesananBinding
import com.example.aaa_magnumbread.notifikasi.NotificationHelper
import com.google.android.material.snackbar.Snackbar

class PesananFragment : Fragment() {

    private var _binding: FragmentPesananBinding? = null
    private val binding get() = _binding!!

    private val adapter = PesananAdapter()

    // Sumber data (dummy). Perubahan selalu dikirim ke adapter lewat tampilkanDaftar()
    private val daftarPesanan = mutableListOf(
        ModelPesanan("LE-001", "Cuci & Setrika", "Sedang Dicuci", "Estimasi selesai: Besok, 17.00"),
        ModelPesanan("LE-002", "Express", "Sedang Dicuci", "Estimasi selesai: Hari ini, 20.00"),
        ModelPesanan("LE-003", "Dry Cleaning", "Sedang Disetrika", "Estimasi selesai: Besok, 10.00"),
        ModelPesanan("LE-004", "Bulky Laundry", "Sedang Dicuci", "Estimasi selesai: 3 Nov, 15.00")
    )

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

        setupSwipeToDelete()
        tampilkanDaftar()
    }

    // Kirim salinan list ke adapter + atur tampilan "kosong"
    private fun tampilkanDaftar() {
        adapter.submitList(daftarPesanan.toList())
        binding.tvKosong.visibility = if (daftarPesanan.isEmpty()) View.VISIBLE else View.GONE
    }

    // ===== FITUR: Swipe to Delete (geser ke kiri untuk membatalkan) =====
    private fun setupSwipeToDelete() {
        val density = resources.displayMetrics.density

        // Cat latar merah + teks "Batalkan" yang muncul saat item digeser
        val bgPaint = Paint().apply { color = Color.parseColor("#EF4444") }
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 14 * density
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // Drag & drop tidak dipakai
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            // Dipanggil saat item selesai digeser
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posisi = viewHolder.bindingAdapterPosition
                if (posisi == RecyclerView.NO_POSITION) return

                val dihapus = daftarPesanan.removeAt(posisi)
                tampilkanDaftar()

                // applicationContext aman dipakai walau fragment sudah tidak tampil
                val appContext = requireContext().applicationContext

                // Snackbar dengan tombol URUNGKAN
                Snackbar.make(binding.root, "Pesanan ${dihapus.id} dibatalkan", Snackbar.LENGTH_LONG)
                    .setAction("URUNGKAN") {
                        daftarPesanan.add(posisi.coerceAtMost(daftarPesanan.size), dihapus)
                        tampilkanDaftar()
                    }
                    .setActionTextColor(Color.parseColor("#27C2F3"))
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            // Notifikasi hanya dikirim jika pembatalan TIDAK di-urungkan
                            if (event != DISMISS_EVENT_ACTION) {
                                NotificationHelper.show(
                                    appContext,
                                    "Pesanan ${dihapus.id} dibatalkan",
                                    "Pesanan ${dihapus.layanan} berhasil dibatalkan."
                                )
                            }
                        }
                    })
                    .show()
            }

            // Menggambar latar merah di belakang item yang sedang digeser
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
                    // Teks baru digambar jika ruang merahnya sudah cukup lebar
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // hindari memory leak
    }
}