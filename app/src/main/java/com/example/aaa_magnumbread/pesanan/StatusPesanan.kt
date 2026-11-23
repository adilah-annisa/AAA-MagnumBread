package com.example.aaa_magnumbread.pesanan

/** Alur status pesanan. Urutan list = urutan di Tracking. */
object StatusPesanan {
    const val MENUNGGU = "Menunggu Pembayaran"
    const val DIJEMPUT = "Dijemput"
    const val DICUCI = "Dicuci"
    const val DIANTAR = "Diantar"
    const val SELESAI = "Selesai"

    val urutan = listOf(MENUNGGU, DIJEMPUT, DICUCI, DIANTAR, SELESAI)

    val deskripsi = mapOf(
        MENUNGGU to "Pesanan dibuat, menunggu pembayaran.",
        DIJEMPUT to "Pembayaran diterima. Kurir menjemput cucianmu.",
        DICUCI to "Cucianmu sedang dicuci dan disetrika.",
        DIANTAR to "Kurir sedang mengantar cucian ke alamatmu.",
        SELESAI to "Pesanan selesai. Terima kasih!"
    )

    // Status sesudah status ini (null kalau sudah Selesai / status tidak dikenal)
    fun berikutnya(status: String): String? {
        val i = urutan.indexOf(status)
        return if (i < 0) null else urutan.getOrNull(i + 1)
    }
}