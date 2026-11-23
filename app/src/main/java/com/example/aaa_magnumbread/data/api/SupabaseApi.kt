package com.example.aaa_magnumbread.data.api

import com.example.aaa_magnumbread.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Semua endpoint Supabase (PostgREST).
 * Filter ditulis dengan format  kolom=eq.nilai  , contoh: user_id = "eq.1"
 */
interface SupabaseApi {

    // ===== USERS : REGISTER =====
    @Headers("Prefer: return=representation")
    @POST("users")
    suspend fun register(@Body body: UserRequest): Response<List<UserModel>>

    // ===== ALAMAT : CRUD =====
    @GET("alamat")                                            // READ
    suspend fun getAlamat(
        @Query("user_id") userId: String,
        @Query("order") order: String = "id.desc"
    ): List<AlamatModel>

    @Headers("Prefer: return=representation")
    @POST("alamat")                                           // CREATE
    suspend fun createAlamat(@Body body: AlamatRequest): Response<List<AlamatModel>>

    @PATCH("alamat")                                          // UPDATE
    suspend fun updateAlamat(@Query("id") id: String, @Body body: AlamatRequest): Response<Unit>

    @DELETE("alamat")                                         // DELETE
    suspend fun deleteAlamat(@Query("id") id: String): Response<Unit>

    // ===== PESANAN : CRUD =====
    @GET("pesanan")                                           // READ (ikut membawa label alamat)
    suspend fun getPesanan(
        @Query("user_id") userId: String,
        @Query("select") select: String = "*,alamat(label,alamat_lengkap)",
        @Query("order") order: String = "id.desc"
    ): List<PesananModel>

    @GET("pesanan")                                           // READ satu pesanan (Pembayaran / Tracking)
    suspend fun getPesananById(
        @Query("id") id: String,
        @Query("select") select: String = "*,alamat(label,alamat_lengkap)"
    ): List<PesananModel>

    @Headers("Prefer: return=representation")
    @POST("pesanan")                                          // CREATE
    suspend fun createPesanan(@Body body: PesananRequest): Response<List<PesananModel>>

    @PATCH("pesanan")                                         // UPDATE
    suspend fun updatePesanan(@Query("id") id: String, @Body body: PesananRequest): Response<Unit>

    @DELETE("pesanan")                                        // DELETE
    suspend fun deletePesanan(@Query("id") id: String): Response<Unit>
}
