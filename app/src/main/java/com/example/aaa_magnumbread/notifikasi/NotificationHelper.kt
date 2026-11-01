package com.example.aaa_magnumbread.notifikasi

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.aaa_magnumbread.MainActivity
import org.json.JSONArray
import org.json.JSONObject

/**
 * Pusat urusan notifikasi lokal:
 * 1) membuat NotificationChannel (wajib Android 8+)
 * 2) menampilkan notifikasi lewat NotificationManager
 * 3) menyimpan riwayat notifikasi ke SharedPreferences (tanpa Firebase)
 */
object NotificationHelper {

    private const val CHANNEL_ID = "laundry_express_channel"
    private const val CHANNEL_NAME = "Notifikasi Laundry Express"
    private const val PREFS_NAME = "notifikasi_prefs"
    private const val MAX_RIWAYAT = 50

    const val KEY_HISTORY = "history"

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Channel hanya perlu dibuat di Android 8.0 (API 26) ke atas
    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Info status pesanan laundry" }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Tampilkan notifikasi + catat ke riwayat.
     * Riwayat selalu tersimpan, walau izin notifikasi (Android 13+) belum diberikan.
     */
    fun show(context: Context, judul: String, pesan: String) {
        saveHistory(context, ModelNotifikasi(judul, pesan, System.currentTimeMillis()))

        val izinOk = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
        if (!izinOk) return

        createChannel(context)

        // Ketuk notifikasi -> buka aplikasi
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // ganti dengan ikon sendiri jika ada
            .setContentTitle(judul)
            .setContentText(pesan)
            .setStyle(NotificationCompat.BigTextStyle().bigText(pesan))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // ===== Riwayat notifikasi (JSON di SharedPreferences) =====

    @Synchronized
    private fun saveHistory(context: Context, item: ModelNotifikasi) {
        val list = getHistory(context).toMutableList()
        list.add(0, item) // terbaru di atas
        val array = JSONArray()
        list.take(MAX_RIWAYAT).forEach {
            array.put(
                JSONObject()
                    .put("judul", it.judul)
                    .put("pesan", it.pesan)
                    .put("waktu", it.waktu)
            )
        }
        prefs(context).edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    fun getHistory(context: Context): List<ModelNotifikasi> {
        val json = prefs(context).getString(KEY_HISTORY, null) ?: return emptyList()
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            ModelNotifikasi(o.getString("judul"), o.getString("pesan"), o.getLong("waktu"))
        }
    }
}