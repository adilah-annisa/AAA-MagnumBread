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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aaa_magnumbread.R
import com.example.aaa_magnumbread.databinding.FragmentPesananBinding
import com.google.android.material.snackbar.Snackbar

class PesananFragment : Fragment() {

    private var _binding: FragmentPesananBinding? = null
    private val binding get() = _binding!!

    private val adapter = PesananAdapter()

    // Sumber data dummy
    private val daftarPesanan = mutableListOf(
        ModelPesanan("LE-001", "Cuci & Setrika", "Sedang Dicuci", "Estimasi selesai: Besok, 17.00"),
        ModelPesanan("LE-002", "Express", "Sedang Dicuci", "Estimasi selesai: Hari ini, 20.00"),
        ModelPesanan("LE-003", "Cuci Kering", "Sedang Disetrika", "Estimasi selesai: Besok, 10.00"),
        ModelPesanan("LE-004", "Setrika Saja", "Sedang Dicuci", "Estimasi selesai: 3 Nov, 15.00")
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

    // Kirim salinan list ke adapter + atur teks "kosong"
    private fun tampilkanDaftar() {
        adapter.submitList(daftarPesanan.toList())
        binding.tvKosong.visibility = if (daftarPesanan.isEmpty()) View.VISIBLE else View.GONE
    }

    // ===== Swipe to Delete: geser ke kiri, muncul latar merah + ikon + "Delete" =====
    private fun setupSwipeToDelete() {
        val density = resources.displayMetrics.density

        val bgPaint = Paint().apply { color = Color.parseColor("#EF4444") }
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 14 * density
            typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
                ?: Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
        val iconSize = (22 * density).toInt()
        val label = "Delete"

        // 1. Callback: drag = 0 (tidak dipakai), swipe = LEFT
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posisi = viewHolder.adapterPosition
                if (posisi == RecyclerView.NO_POSITION) return

                // Simpan dulu supaya bisa di-undo
                val dihapus = daftarPesanan[posisi]
                hapusPesanan(dihapus)
                tampilkanSnackbar(dihapus, posisi)
            }

            // Gambar area merah di belakang kartu yang digeser
            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                if (dX < 0) {
                    val item = viewHolder.itemView
                    val radius = 16 * density
                    val padding = 20 * density

                    val area = RectF(
                        item.right + dX, item.top.toFloat(),
                        item.right.toFloat(), item.bottom.toFloat()
                    )

                    c.save()
                    c.clipRect(area)
                    c.drawRoundRect(area, radius, radius, bgPaint)

                    val centerY = item.top + item.height / 2f

                    // Teks "Delete" di kanan
                    val textWidth = textPaint.measureText(label)
                    val textX = item.right - padding - textWidth
                    val textY = centerY - (textPaint.descent() + textPaint.ascent()) / 2f
                    c.drawText(label, textX, textY, textPaint)

                    // Ikon tong sampah di kiri teks
                    val iconRight = (textX - 8 * density).toInt()
                    val iconTop = (centerY - iconSize / 2f).toInt()
                    icon.setBounds(iconRight - iconSize, iconTop, iconRight, iconTop + iconSize)
                    icon.draw(c)

                    c.restore()
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        // 2. Pasang ke RecyclerView
        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(binding.rvPesanan)
    }

    private fun hapusPesanan(pesanan: ModelPesanan) {
        daftarPesanan.remove(pesanan)
        tampilkanDaftar()
    }

    private fun tampilkanSnackbar(pesanan: ModelPesanan, posisi: Int) {
        Snackbar.make(binding.root, "Pesanan ${pesanan.id} dihapus", Snackbar.LENGTH_LONG)
            .setAction("URUNGKAN") { urungkanHapus(pesanan, posisi) }
            .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            .show()
    }

    private fun urungkanHapus(pesanan: ModelPesanan, posisi: Int) {
        daftarPesanan.add(posisi.coerceAtMost(daftarPesanan.size), pesanan)
        tampilkanDaftar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}