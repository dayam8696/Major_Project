package com.example.majorproject.repository

import GeminiApiService
import com.example.majorproject.api.ApiClient
import com.example.majorproject.dataModel.Content
import com.example.majorproject.dataModel.GeminiRequest
import com.example.majorproject.dataModel.Part

class GeminiRepository {
    private val apiService: GeminiApiService = ApiClient.retrofit.create(GeminiApiService::class.java)

    suspend fun getAIResponse(prompt: String): String? {
        val apiKey = "AIzaSyCcoeVnd0iAdLS8iwAy92esJxT6WCnq0Gs"
        val url = "v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        return try {
            val response = apiService.generateContent(url, request)
            if (response.isSuccessful) {
                response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            } else {
                "Error: ${response.message()}"
            }
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }
}
