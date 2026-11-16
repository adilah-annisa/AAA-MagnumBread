package com.example.aaa_magnumbread.chat.tutorial

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aaa_magnumbread.R
import com.example.aaa_magnumbread.databinding.ActivityTutorialChatBinding

class TutorialChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pages = listOf(
            TutorialPageFragment.newInstance(
                R.drawable.ic_laundry, "Pilih Layanan",
                "Pilih jenis layanan laundry sesuai kebutuhanmu, dari cuci kering sampai dry cleaning."
            ),
            TutorialPageFragment.newInstance(
                R.drawable.ic_schedule, "Jadwalkan Pickup",
                "Tentukan waktu penjemputan dan kurir akan datang ke rumahmu."
            ),
            TutorialPageFragment.newInstance(
                R.drawable.ic_delivery, "Lacak Pesanan",
                "Pantau status laundry dari diterima, diproses, sampai diantar kembali."
            )
        )

        binding.tutorialViewPager.adapter = TutorialFragmentAdapter(this, pages)
        binding.dotIndicator.attachTo(binding.tutorialViewPager)
    }
}