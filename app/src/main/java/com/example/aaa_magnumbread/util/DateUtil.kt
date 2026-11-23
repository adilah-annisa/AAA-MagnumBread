package com.example.aaa_magnumbread.util


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Konversi tanggal antara aplikasi (Date) dan Supabase (teks ISO-8601). */
object DateUtil {

    // Date -> "2026-11-24T09:00:00+07:00" (dikirim ke Supabase)
    fun toIso(date: Date): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(date)

    // Teks dari Supabase -> Date (null jika kosong / gagal dibaca)
    fun parseIso(text: String?): Date? {
        if (text.isNullOrBlank()) return null
        return try {
            val bersih = text.replace(Regex("\\.\\d+"), "") // buang pecahan detik
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).parse(bersih)
        } catch (e: Exception) {
            null
        }
    }

    // Teks dari Supabase -> "24 Nov 2026, 09.00"
    fun tampilkan(text: String?): String {
        val date = parseIso(text) ?: return "-"
        return SimpleDateFormat("dd MMM yyyy, HH.mm", Locale("id", "ID")).format(date)
    }
}
