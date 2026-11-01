package com.example.aaa_magnumbread.notifikasi

/** Satu catatan riwayat notifikasi (disimpan lokal di SharedPreferences). */
data class ModelNotifikasi(
    val judul: String,
    val pesan: String,
    val waktu: Long // waktu kejadian dalam milidetik
)