package com.example.majorproject.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DoctorClient {
    private const val BASE_URL = "https://api.jsonbin.io/v3/"

    val apiService: DoctorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DoctorApiService::class.java)
    }
}