package com.example.aaa_magnumbread.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aaa_magnumbread.ChooseCatalogActivity
import com.example.aaa_magnumbread.WebViewActivity
import com.example.aaa_magnumbread.chat.ChatActivity
import com.example.aaa_magnumbread.databinding.FragmentHomeBinding
import com.example.aaa_magnumbread.layanan.LayananActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentHomeBinding.inflate(inflater, container,
                false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Beranda"
        }
        binding.lihatSemua.setOnClickListener {
            val intent = Intent(requireContext(),
                ChooseCatalogActivity::class.java)
            startActivity(intent)
        }
        binding.btncucidansetrika.setOnClickListener {
            val intent = Intent(requireContext(),
                ChooseCatalogActivity::class.java)
            startActivity(intent)
        }
        binding.btnCuciKering.setOnClickListener {
            val intent = Intent(requireContext(),
                ChooseCatalogActivity::class.java)
            startActivity(intent)
        }
        binding.btnSetrikaSaja.setOnClickListener {
            val intent = Intent(requireContext(),
                ChooseCatalogActivity::class.java)
            startActivity(intent)
        }
        binding.btnExpress.setOnClickListener {
            val intent = Intent(requireContext(),
                ChooseCatalogActivity::class.java)
            startActivity(intent)
        }
        binding.btnWebView.setOnClickListener {
            val intent = Intent(requireContext(),
                WebViewActivity::class.java)
            startActivity(intent)
        }
        binding.btnMenuLayanan.setOnClickListener {
            startActivity(Intent(requireContext(), LayananActivity::class.java))
        }
        binding.btnMenuPesan.setOnClickListener {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }
        binding.btnMenuPesanan.setOnClickListener {
            Toast.makeText(requireContext(), "Buka tab Pesanan", Toast.LENGTH_SHORT).show()
        }
        binding.btnMenuJadwal.setOnClickListener {
            Toast.makeText(requireContext(), "Menu Jadwal", Toast.LENGTH_SHORT).show()
        }
        binding.btnMenuTracking.setOnClickListener {
            Toast.makeText(requireContext(), "Menu Tracking", Toast.LENGTH_SHORT).show()
        }
        binding.btnMenuBayar.setOnClickListener {
            Toast.makeText(requireContext(), "Menu Pembayaran", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}