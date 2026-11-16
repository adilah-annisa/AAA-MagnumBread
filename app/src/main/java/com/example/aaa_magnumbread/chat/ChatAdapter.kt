package com.example.aaa_magnumbread.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.aaa_magnumbread.databinding.ItemChatBinding
import com.google.android.material.snackbar.Snackbar

class ChatAdapter(
    context: Context,
    private val items: List<ChatModel>
) : ArrayAdapter<ChatModel>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(context), parent, false)
        val data = items[position]

        Glide.with(context).load(data.avatarUrl).circleCrop().into(binding.avatarImg)
        binding.textSender.text = data.senderName
        binding.textMessage.text = data.messageText

        // Event OonClick
        binding.root.setOnClickListener {
            Snackbar.make(parent, "Pesan dari ${data.senderName}: ${data.messageText}", Snackbar.LENGTH_SHORT)
        }
        return binding.root
    }
}