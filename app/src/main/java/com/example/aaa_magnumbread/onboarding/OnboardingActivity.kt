package com.example.aaa_magnumbread.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.aaa_magnumbread.RegistrationActivity
import com.example.aaa_magnumbread.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            OnboardingItem("🧺", "Pilih Layanan",
                "Pilih jenis layanan laundry sesuai kebutuhanmu, dari cuci kering sampai express."),
            OnboardingItem("🚚", "Jadwalkan Pickup",
                "Tentukan waktu penjemputan dan kurir akan datang ke rumahmu."),
            OnboardingItem("📍", "Lacak Pesanan",
                "Pantau status laundry dari diterima, diproses, sampai diantar kembali.")
        )

        val pager = binding.onboardingViewPager
        pager.adapter = OnboardingAdapter(items)
        TabLayoutMediator(binding.tabDots, pager) { _, _ -> }.attach()

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val last = position == items.lastIndex
                binding.btnNext.text = if (last) "Mulai" else "Lanjut"
                binding.btnSkip.visibility = if (last) View.INVISIBLE else View.VISIBLE
            }
        })

        binding.btnNext.setOnClickListener {
            if (pager.currentItem < items.lastIndex) pager.currentItem += 1
            else finishOnboarding()
        }
        binding.btnSkip.setOnClickListener { finishOnboarding() }
    }

    private fun finishOnboarding() {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
            .edit().putBoolean("onboarding_done", true).apply()
        startActivity(Intent(this, RegistrationActivity::class.java))
        finish()
    }
}