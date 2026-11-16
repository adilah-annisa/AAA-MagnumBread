package com.example.aaa_magnumbread.layanan

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.aaa_magnumbread.R
import com.example.aaa_magnumbread.databinding.ActivityLayananBinding

class LayananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLayananBinding

    private val serviceList = listOf(
        ServiceModel("Cuci & Setrika", "Rp 8.000 / kg", "https://picsum.photos/seed/laundry1/400/300"),
        ServiceModel("Cuci Kering", "Rp 6.000 / kg", "https://picsum.photos/seed/laundry2/400/300"),
        ServiceModel("Setrika Saja", "Rp 4.000 / kg", "https://picsum.photos/seed/laundry3/400/300"),
        ServiceModel("Dry Cleaning", "Rp 25.000 / pcs", "https://picsum.photos/seed/laundry4/400/300"),
        ServiceModel("Bulky Laundry", "Rp 35.000 / item", "https://picsum.photos/seed/laundry5/400/300"),
        ServiceModel("Express", "Rp 12.000 / kg", "https://picsum.photos/seed/laundry6/400/300")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLayananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Pilih Layanan"
            setDisplayHomeAsUpEnabled(true)
        }

        val adapter = ServiceAdapter(serviceList) { selected ->
            Toast.makeText(this, "Kamu memilih ${selected.name}", Toast.LENGTH_SHORT).show()
        }
        binding.rvServices.layoutManager = GridLayoutManager(this, 2)
        binding.rvServices.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}