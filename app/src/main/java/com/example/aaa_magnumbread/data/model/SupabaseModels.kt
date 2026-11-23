package com.example.aaa_magnumbread.data.model

import com.google.gson.annotations.SerializedName

// ===== USERS =====
// Dikirim saat Register (password SUDAH berupa hash)
data class UserRequest(
    val nama: String,
    @SerializedName("no_hp") val noHp: String,
    val username: String,
    val password: String
)

// Diterima dari Supabase
data class UserModel(
    val id: Long,
    val nama: String,
    @SerializedName("no_hp") val noHp: String,
    val username: String
)

// ===== ALAMAT =====
data class AlamatModel(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    val label: String,
    @SerializedName("alamat_lengkap") val alamatLengkap: String,
    val catatan: String?
)

// Dipakai untuk Create dan Update. Field bernilai null tidak ikut dikirim.
data class AlamatRequest(
    val label: String,
    @SerializedName("alamat_lengkap") val alamatLengkap: String,
    val catatan: String = "",
    @SerializedName("user_id") val userId: Long? = null
)

// ===== PESANAN =====
// Potongan data alamat yang ikut terbawa saat membaca pesanan (relasi)
data class AlamatRingkas(
    val label: String?,
    @SerializedName("alamat_lengkap") val alamatLengkap: String?
)

data class PesananModel(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("alamat_id") val alamatId: Long?,
    val layanan: String,
    @SerializedName("jadwal_jemput") val jadwalJemput: String?,
    val catatan: String?,
    val status: String,
    val jumlah: Int = 1,
    @SerializedName("total_harga") val totalHarga: Long = 0,
    @SerializedName("metode_bayar") val metodeBayar: String? = null,
    @SerializedName("dibayar_pada") val dibayarPada: String? = null,
    val alamat: AlamatRingkas?
) {
    // Kode tampilan: id 1 -> LE-001
    val kode: String get() = "LE-%03d".format(id)
}

// Dipakai untuk Create dan Update. Field bernilai null tidak ikut dikirim.
data class PesananRequest(
    @SerializedName("user_id") val userId: Long? = null,
    @SerializedName("alamat_id") val alamatId: Long? = null,
    val layanan: String? = null,
    @SerializedName("jadwal_jemput") val jadwalJemput: String? = null,
    val catatan: String? = null,
    val status: String? = null,
    val jumlah: Int? = null,
    @SerializedName("total_harga") val totalHarga: Long? = null,
    @SerializedName("metode_bayar") val metodeBayar: String? = null,
    @SerializedName("dibayar_pada") val dibayarPada: String? = null
)
