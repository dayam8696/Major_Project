package com.example.majorproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.majorproject.repository.GeminiRepository

data class AIResponse(val prompt: String, val response: String)

class GeminiViewModel(private val repository: GeminiRepository) : ViewModel() {

    private val _aiResponses = MutableLiveData<List<AIResponse>>(emptyList())
    val aiResponses: LiveData<List<AIResponse>> = _aiResponses

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchAIResponse(prompt: String) {
        if (prompt.isBlank()) return

        _isLoading.value = true  // Show loading indicator

        viewModelScope.launch {
            val response = repository.getAIResponse(prompt) ?: "No response"

            val newResponse = AIResponse(prompt, response)

            _aiResponses.value = (_aiResponses.value ?: emptyList()) + newResponse

            _isLoading.value = false  // Hide loading indicator
        }
    }
}
