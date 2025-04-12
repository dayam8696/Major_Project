package com.example.majorproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.majorproject.repository.GeminiRepository

class GeminiViewModelFactory(private val repository: GeminiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeminiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GeminiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
