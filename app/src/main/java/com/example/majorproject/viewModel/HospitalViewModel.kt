package com.example.majorproject.viewModel

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.majorproject.api.OverpassApiClient
import com.example.majorproject.dataModel.HospitalElement
import com.example.majorproject.helpers.getCurrentLocation
import kotlinx.coroutines.launch

class HospitalViewModel(private val context: Context) : ViewModel() {

    var hospitals by mutableStateOf<List<HospitalElement>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchHospitals() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            Log.d("HospitalViewModel", "Fetching hospitals...")

            val location: Location? = getCurrentLocation(context)

            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("HospitalViewModel", "Location: Lat=$latitude, Lon=$longitude")

                val query = """
                    [out:json];
                    (
                      node["amenity"="hospital"](around:5000,$latitude,$longitude);
                      way["amenity"="hospital"](around:5000,$latitude,$longitude);
                      relation["amenity"="hospital"](around:5000,$latitude,$longitude);
                    );
                    out body;
                """.trimIndent()

                try {
                    val response = OverpassApiClient.api.getHospitals(query)
                    hospitals = response.elements.filter { it.tags?.name != null }
                    Log.d("HospitalViewModel", "Hospitals fetched: ${hospitals.size}")
                    hospitals.forEach { hospital ->
                        Log.d(
                            "HospitalInfo",
                            "Hospital: ${hospital.tags?.name}, Lat: ${hospital.lat}, Lon: ${hospital.lon}"
                        )
                    }
                } catch (e: Exception) {
                    Log.e("HospitalViewModel", "Error fetching hospitals", e)
                    errorMessage = "Failed to fetch hospitals. Please try again."
                }
            } else {
                Log.w("HospitalViewModel", "Location is null. Cannot fetch hospitals.")
                errorMessage = "Unable to get location. Please enable location services."
            }

            isLoading = false
        }
    }

    fun retryFetchHospitals() {
        fetchHospitals()
    }
}