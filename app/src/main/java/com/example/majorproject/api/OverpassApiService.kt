package com.example.majorproject.api

import com.example.majorproject.dataModel.HospitalResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OverpassApiService {
    @GET("api/interpreter")
    suspend fun getHospitals(
        @Query("data") query: String
    ): HospitalResponse
}