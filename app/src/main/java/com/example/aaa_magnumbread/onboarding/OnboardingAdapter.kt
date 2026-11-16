package com.example.aaa_magnumbread.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aaa_magnumbread.databinding.ItemOnboardingBinding

data class OnboardingItem(val icon: String, val title: String, val desc: String)

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.VH>() {

    class VH(val binding: ItemOnboardingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.tvIcon.text = item.icon
        holder.binding.tvTitle.text = item.title
        holder.binding.tvDesc.text = item.desc
    }

    override fun getItemCount() = items.size
}