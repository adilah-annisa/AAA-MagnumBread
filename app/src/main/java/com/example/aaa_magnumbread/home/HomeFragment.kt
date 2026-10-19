package com.example.aaa_magnumbread.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aaa_magnumbread.ChooseCatalogActivity
import com.example.aaa_magnumbread.WebViewActivity
import com.example.aaa_magnumbread.databinding.FragmentHomeBinding
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
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}