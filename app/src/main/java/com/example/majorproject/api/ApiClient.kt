package com.example.majorproject.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // ⏳ Increase connection timeout
        .readTimeout(30, TimeUnit.SECONDS) // ⏳ Increase read timeout
        .writeTimeout(30, TimeUnit.SECONDS) // ⏳ Increase write timeout
        .retryOnConnectionFailure(true) // 🔄 Enable automatic retry
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // Use custom OkHttpClient
            .build()
    }
}
