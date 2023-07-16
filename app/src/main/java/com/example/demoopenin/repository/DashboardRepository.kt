package com.example.demoopenin.repository

import DashBoardModel
import com.example.demoopenin.networking.ApiResponse
import com.example.demoopenin.networking.DashboardApiService
import com.example.demoopenin.networking.RetrofitClient

class DashboardRepository {
    private val apiService: DashboardApiService = RetrofitClient.apiService

    suspend fun getDashboardData(token: String): ApiResponse<DashBoardModel> {
        return try {
            val response = apiService.getDashboardData("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResponse.Success(body)
                } else {
                    ApiResponse.Error("Empty response body")
                }
            } else {
                ApiResponse.Error(response.message())
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }
}
