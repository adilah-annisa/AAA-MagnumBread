package com.example.aaa_magnumbread.notifikasi

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Worker = tugas yang dijalankan WorkManager di background,
 * walaupun aplikasi sedang ditutup.
 * Judul & pesan dikirim lewat inputData saat dijadwalkan.
 */
class NotificationWorker(appContext: Context, params: WorkerParameters) :
    Worker(appContext, params) {

    override fun doWork(): Result {
        val judul = inputData.getString(KEY_JUDUL) ?: "Laundry Express"
        val pesan = inputData.getString(KEY_PESAN) ?: "Ada pembaruan pesanan Anda"

        NotificationHelper.show(applicationContext, judul, pesan)
        return Result.success()
    }

    companion object {
        const val KEY_JUDUL = "judul"
        const val KEY_PESAN = "pesan"
    }
}