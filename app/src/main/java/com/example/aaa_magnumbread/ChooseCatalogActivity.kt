package com.example.aaa_magnumbread

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aaa_magnumbread.databinding.ActivityChoosecatalogBinding

class ChooseCatalogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChoosecatalogBinding

    // Fitur apabila Layanan dipilih (null = kondisi apabila card belum dipilih)
    private var selectedService: String? = null

    // Daftar semua card layanan, ini dipakai untuk mereset efek card terpilih
    private lateinit var serviceCards: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosecatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Metode supaya toolbar tidak tertutup status bar/navbar
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        setupBackButton()
        setupServiceCards()
        setupContinueButton()
    }

    // Back button di toolbar
    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    // Efek card layanan yang diselect (Hanya satu kartu yang aktif)
    private fun setupServiceCards(){
        serviceCards = listOf(
            binding.btnCuciDanSetrika,
            binding.btnCuciKering,
            binding.btnSetrikaSaja,
            binding.btnDryCleaning,
            binding.btnBulkyLaundry,
            binding.btnExpress
        )

        // Kondisi Awal yang dimana belum ada card yang terpilih
        selectedService = null

        // Kondisi apabila tiap card di-select
        binding.btnCuciDanSetrika.setOnClickListener {selectService(it, "Cuci & Setrika")}
        binding.btnCuciKering.setOnClickListener {selectService(it, "Cuci Kering")}
        binding.btnSetrikaSaja.setOnClickListener {selectService(it, "Setrika saja")}
        binding.btnDryCleaning.setOnClickListener {selectService(it, "Dry Cleaning")}
        binding.btnBulkyLaundry.setOnClickListener {selectService(it, "Bulky Laundry")}
        binding.btnExpress.setOnClickListener {selectService(it, "Express")}
    }

    private fun selectService(card: View, serviceName: String ) {
        serviceCards.forEach {
            it.isSelected = false
        }
        card.isSelected = true
        selectedService = serviceName
    }

    private fun setupContinueButton() {
        binding.btnLanjut.setOnClickListener {
            val service = selectedService
            // Kondisi apabila belum ada card yang dipilih
            if (service == null) {
                Toast.makeText(this, "Pilih layanan terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Ganti ke halaman berikutnya
            Toast.makeText(this, "Layanan dipilih: $service", Toast.LENGTH_SHORT).show()
        }
    }

}