package com.example.aaa_magnumbread.util

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Hash password sebelum dikirim ke Supabase (password asli TIDAK pernah disimpan).
 * Format yang disimpan di kolom password:  salt:hash   (SHA-256 + salt acak)
 */
object PasswordHasher {

    fun hash(password: String): String {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return "${toHex(salt)}:${sha256(salt, password)}"
    }

    // Dipakai kalau nanti Login memeriksa ke Supabase
    fun verify(password: String, tersimpan: String): Boolean {
        val bagian = tersimpan.split(":")
        if (bagian.size != 2) return false
        val salt = fromHex(bagian[0]) ?: return false
        return MessageDigest.isEqual(
            sha256(salt, password).toByteArray(),
            bagian[1].toByteArray()
        )
    }

    private fun sha256(salt: ByteArray, password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        return toHex(digest.digest(password.toByteArray(Charsets.UTF_8)))
    }

    private fun toHex(bytes: ByteArray): String =
        bytes.joinToString("") { "%02x".format(it) }

    private fun fromHex(hex: String): ByteArray? {
        if (hex.length % 2 != 0) return null
        return try {
            ByteArray(hex.length / 2) { hex.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
        } catch (e: NumberFormatException) {
            null
        }
    }
}
