package com.example.aaa_magnumbread.util

import android.content.Context

/**
 * Menyimpan siapa user yang sedang dipakai (id dari tabel users).
 * id ini dipakai sebagai user_id saat membuat / membaca pesanan dan alamat.
 */
object SessionManager {
    private const val PREFS = "session_prefs"
    private const val KEY_ID = "user_id"
    private const val KEY_NAMA = "nama"

    fun saveUser(context: Context, id: Long, nama: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putLong(KEY_ID, id)
            .putString(KEY_NAMA, nama)
            .apply()
    }

    // -1 artinya belum ada user (belum register)
    fun getUserId(context: Context): Long =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_ID, -1L)

    fun getNama(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_NAMA, "") ?: ""

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
