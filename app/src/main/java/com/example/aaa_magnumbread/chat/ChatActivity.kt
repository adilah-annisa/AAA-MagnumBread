package com.example.aaa_magnumbread.chat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aaa_magnumbread.R
import com.example.aaa_magnumbread.chat.tutorial.TutorialChatActivity
import com.example.aaa_magnumbread.databinding.ActivityChatBinding
import kotlin.jvm.java

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private val chatList = listOf(
        ChatModel("Admin Laundry", "Pesanan #LX-001 sedang dicuci", "https://avatarapi.runflare.run/public/1"),
        ChatModel("Kurir Budi", "Saya sudah di depan rumah untuk pickup", "https://avatarapi.runflare.run/public/2"),
        ChatModel("Admin Laundry", "Pembayaran Rp 56.000 sudah kami terima", "https://avatarapi.runflare.run/public/3"),
        ChatModel("Kurir Dika", "Pesanan Anda sedang diantar, estimasi 15 menit", "https://avatarapi.runflare.run/public/4"),
        ChatModel("Admin Laundry", "Pesanan #LX-002 sudah selesai disetrika", "https://avatarapi.runflare.run/public/5"),
        ChatModel("Kurir Citra", "Jadwal pickup besok jam 09.00 ya", "https://avatarapi.runflare.run/public/6")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Pesan"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.listChatItems.adapter = ChatAdapter(this, chatList)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chat_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_tutorial -> {
                startActivity(Intent(this, TutorialChatActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}