package com.example.aaa_magnumbread.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {

    private const val BASE_URL = "https://bxwoxlpeksyohtsgjwiw.supabase.co/rest/v1/"
    private const val API_KEY = "sb_publishable_2wbafCAAWe8YatHQBgtAPA_s1962UFV"

    // Interceptor: menambahkan header apikey ke SETIAP request
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", API_KEY)
                .build()
            chain.proceed(request)
        }
        .build()

    val api: SupabaseApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }
}