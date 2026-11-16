package com.example.aaa_magnumbread.layanan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aaa_magnumbread.databinding.ItemServiceBinding

class ServiceAdapter(
    private val list: List<ServiceModel>,
    private val onItemClick: (ServiceModel) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            tvServiceName.text = item.name
            tvServicePrice.text = item.price
            Glide.with(holder.itemView.context).load(item.imageUrl).into(imgService)
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size
}