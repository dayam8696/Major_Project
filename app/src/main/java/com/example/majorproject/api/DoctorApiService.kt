package com.example.majorproject.api

import com.example.majorproject.dataModel.DepartmentResponse
import retrofit2.http.GET

interface DoctorApiService {
    @GET("b/67fb51f58561e97a50fe6a13?meta=false&X-JSON-Path=Departments")
    suspend fun getDepartments(): DepartmentResponse
}