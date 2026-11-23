package com.example.aaa_magnumbread.util


import java.text.NumberFormat
import java.util.Locale

/** Tarif layanan (sama dengan daftar di halaman Pilih Layanan) dan format rupiah. */
object HargaUtil {

    const val ONGKIR = 5_000L

    // layanan -> (harga per satuan, satuan)
    private val tarif = mapOf(
        "Cuci & Setrika" to (8_000L to "kg"),
        "Cuci Kering" to (6_000L to "kg"),
        "Setrika Saja" to (4_000L to "kg"),
        "Dry Cleaning" to (25_000L to "pcs"),
        "Bulky Laundry" to (35_000L to "item"),
        "Express" to (12_000L to "kg")
    )

    fun hargaSatuan(layanan: String): Long = tarif[layanan]?.first ?: 8_000L
    fun satuan(layanan: String): String = tarif[layanan]?.second ?: "kg"

    fun rupiah(angka: Long): String =
        "Rp " + NumberFormat.getInstance(Locale("id", "ID")).format(angka)
}
