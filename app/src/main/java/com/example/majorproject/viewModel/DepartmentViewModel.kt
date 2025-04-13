package com.example.majorproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.majorproject.api.DoctorClient
import com.example.majorproject.dataModel.Department
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch





class DepartmentViewModel : ViewModel() {
    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departmentsFlow: StateFlow<List<Department>> = _departments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoadingFlow: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchDepartments()
    }

    private fun fetchDepartments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = DoctorClient.apiService.getDepartments()
                _departments.value = response.Departments
            } catch (e: Exception) {
                Log.e("DepartmentViewModel", "Error fetching departments: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}