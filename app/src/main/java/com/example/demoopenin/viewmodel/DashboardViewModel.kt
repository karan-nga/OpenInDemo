package com.example.demoopenin.viewmodel

import DashBoardModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoopenin.networking.ApiResponse
import com.example.demoopenin.repository.DashboardRepository
import kotlinx.coroutines.launch


class DashboardViewModel : ViewModel() {
    private val repository: DashboardRepository = DashboardRepository()

    private val _dashboardData = MutableLiveData<ApiResponse<DashBoardModel>>()
    val dashboardData: LiveData<ApiResponse<DashBoardModel>> = _dashboardData


    fun getDashboardData(token: String) {
        _dashboardData.value = ApiResponse.Loading

        viewModelScope.launch {
            try {
                val response = repository.getDashboardData(token)
                _dashboardData.value = response
            } catch (e: Exception) {
                _dashboardData.value = ApiResponse.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
