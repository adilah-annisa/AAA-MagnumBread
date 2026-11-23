package com.example.aaa_magnumbread.pesanan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aaa_magnumbread.data.model.PesananModel
import com.example.aaa_magnumbread.databinding.ItemPesananBinding
import com.example.aaa_magnumbread.util.DateUtil

/**
 * Adapter daftar pesanan (data dari Supabase).
 * onClick dipanggil saat kartu diketuk -> dipakai untuk UPDATE pesanan.
 */
class PesananAdapter(private val onClick: (PesananModel) -> Unit) :
    ListAdapter<PesananModel, PesananAdapter.PesananViewHolder>(DIFF_CALLBACK) {

    class PesananViewHolder(val binding: ItemPesananBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val binding = ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PesananViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = getItem(position)
        holder.binding.apply {
            tvIdPesanan.text = pesanan.kode
            tvStatus.text = pesanan.status
            tvLayanan.text = pesanan.layanan

            val label = pesanan.alamat?.label
            val jemput = "Jemput: ${DateUtil.tampilkan(pesanan.jadwalJemput)}"
            tvEstimasi.text = if (label.isNullOrBlank()) jemput else "$jemput  •  $label"

            root.setOnClickListener { onClick(pesanan) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PesananModel>() {
            override fun areItemsTheSame(old: PesananModel, new: PesananModel) = old.id == new.id
            override fun areContentsTheSame(old: PesananModel, new: PesananModel) = old == new
        }
    }
}
