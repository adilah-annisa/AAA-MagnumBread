package com.example.aaa_magnumbread.notifikasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aaa_magnumbread.databinding.ItemNotifikasiBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifikasiAdapter :
    ListAdapter<ModelNotifikasi, NotifikasiAdapter.NotifViewHolder>(DIFF_CALLBACK) {

    class NotifViewHolder(val binding: ItemNotifikasiBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val formatWaktu = SimpleDateFormat("dd MMM yyyy, HH.mm", Locale("id", "ID"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val binding = ItemNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            tvJudulNotif.text = item.judul
            tvPesanNotif.text = item.pesan
            tvWaktuNotif.text = formatWaktu.format(Date(item.waktu))
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ModelNotifikasi>() {
            override fun areItemsTheSame(old: ModelNotifikasi, new: ModelNotifikasi) =
                old.waktu == new.waktu && old.judul == new.judul
            override fun areContentsTheSame(old: ModelNotifikasi, new: ModelNotifikasi) = old == new
        }
    }
}