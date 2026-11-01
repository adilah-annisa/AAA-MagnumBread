package com.example.aaa_magnumbread.pesanan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aaa_magnumbread.databinding.ItemPesananBinding

// ListAdapter + DiffUtil supaya animasi hapus/undo halus
class PesananAdapter :
    ListAdapter<ModelPesanan, PesananAdapter.PesananViewHolder>(DIFF_CALLBACK) {

    class PesananViewHolder(val binding: ItemPesananBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val binding = ItemPesananBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PesananViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = getItem(position)
        holder.binding.tvIdPesanan.text = pesanan.id
        holder.binding.tvStatus.text = pesanan.status
        holder.binding.tvLayanan.text = pesanan.layanan
        holder.binding.tvEstimasi.text = pesanan.estimasi
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ModelPesanan>() {
            override fun areItemsTheSame(oldItem: ModelPesanan, newItem: ModelPesanan) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ModelPesanan, newItem: ModelPesanan) =
                oldItem == newItem
        }
    }
}