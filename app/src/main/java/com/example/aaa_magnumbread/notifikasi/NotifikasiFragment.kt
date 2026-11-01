package com.example.aaa_magnumbread.notifikasi

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aaa_magnumbread.databinding.FragmentNotifikasiBinding
import java.util.concurrent.TimeUnit

class NotifikasiFragment : Fragment() {

    private var _binding: FragmentNotifikasiBinding? = null
    private val binding get() = _binding!!

    private val adapter = NotifikasiAdapter()

    // Daftar riwayat otomatis ter-refresh saat ada notifikasi baru tersimpan
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == NotificationHelper.KEY_HISTORY) {
            activity?.runOnUiThread { muatRiwayat() }
        }
    }

    // Meminta izin POST_NOTIFICATIONS (wajib di Android 13+)
    private val izinLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { diberikan ->
            if (!diberikan) {
                Toast.makeText(
                    requireContext(),
                    "Izin notifikasi ditolak. Pesan tetap tercatat di riwayat.",
                    Toast.LENGTH_LONG
                ).show()
            }
            jadwalkanNotifikasi(10)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotifikasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNotifikasi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifikasi.adapter = adapter

        binding.btnSimulasi.setOnClickListener {
            val perluIzin = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

            if (perluIzin) {
                izinLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                jadwalkanNotifikasi(10)
            }
        }

        muatRiwayat()
    }

    // ===== FITUR: jadwalkan notifikasi dengan WorkManager =====
    private fun jadwalkanNotifikasi(delayDetik: Long) {
        val data = Data.Builder()
            .putString(NotificationWorker.KEY_JUDUL, "Pesanan LE-001")
            .putString(NotificationWorker.KEY_PESAN, "Cucian Anda sudah selesai! Siap diantar.")
            .build()

        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayDetik, TimeUnit.SECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(requireContext()).enqueue(request)

        Toast.makeText(
            requireContext(), "Notifikasi akan muncul dalam $delayDetik detik", Toast.LENGTH_SHORT
        ).show()
    }

    private fun muatRiwayat() {
        if (_binding == null) return
        val riwayat = NotificationHelper.getHistory(requireContext())
        adapter.submitList(riwayat)
        binding.tvKosong.visibility = if (riwayat.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onStart() {
        super.onStart()
        NotificationHelper.prefs(requireContext()).registerOnSharedPreferenceChangeListener(prefsListener)
        muatRiwayat()
    }

    override fun onStop() {
        super.onStop()
        NotificationHelper.prefs(requireContext()).unregisterOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}