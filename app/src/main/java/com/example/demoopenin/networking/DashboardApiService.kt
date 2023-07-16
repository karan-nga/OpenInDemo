package com.example.demoopenin.networking


import DashBoardModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DashboardApiService {
    @GET("dashboardNew")
    suspend fun getDashboardData(
        @Header("Authorization") token: String
    ): Response<DashBoardModel>
}
