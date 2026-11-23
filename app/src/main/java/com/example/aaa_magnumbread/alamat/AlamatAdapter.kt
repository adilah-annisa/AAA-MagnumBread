package com.example.aaa_magnumbread.alamat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aaa_magnumbread.data.model.AlamatModel
import com.example.aaa_magnumbread.databinding.ItemAlamatBinding

class AlamatAdapter(
    private val onEdit: (AlamatModel) -> Unit,
    private val onDelete: (AlamatModel) -> Unit
) : ListAdapter<AlamatModel, AlamatAdapter.AlamatViewHolder>(DIFF_CALLBACK) {

    class AlamatViewHolder(val binding: ItemAlamatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlamatViewHolder {
        val binding = ItemAlamatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlamatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlamatViewHolder, position: Int) {
        val alamat = getItem(position)
        holder.binding.apply {
            tvLabel.text = alamat.label
            tvAlamat.text = alamat.alamatLengkap
            tvCatatan.text = "Catatan: ${alamat.catatan}"
            tvCatatan.visibility = if (alamat.catatan.isNullOrBlank()) View.GONE else View.VISIBLE

            btnEdit.setOnClickListener { onEdit(alamat) }
            btnHapus.setOnClickListener { onDelete(alamat) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AlamatModel>() {
            override fun areItemsTheSame(old: AlamatModel, new: AlamatModel) = old.id == new.id
            override fun areContentsTheSame(old: AlamatModel, new: AlamatModel) = old == new
        }
    }
}
