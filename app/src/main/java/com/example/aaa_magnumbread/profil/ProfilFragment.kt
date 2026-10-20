package com.example.aaa_magnumbread.profil
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aaa_magnumbread.AuthActivity
import com.example.aaa_magnumbread.databinding.FragmentProfilBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? =
        null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilBinding.inflate(inflater,
            container, false)
        return binding.root
    }
    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Profil"
        }

        val sharedPref = requireContext().getSharedPreferences("user_pref", MODE_PRIVATE)
            binding.btnLogout.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin logout?")
                    .setPositiveButton("Ya") { dialog, _ ->

                        val editor = sharedPref.edit()
                        editor.clear()
                        editor.apply()
                        dialog.dismiss()

                        val intent = Intent(requireContext(),
                            AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                    }
                    .setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                        Log.e("Info Dialog", "Anda memilih Batal!")
                    }
                    .show()
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}