package com.example.kostkita.data.remote.api

import com.example.kostkita.data.remote.dto.LoginRequest
import com.example.kostkita.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}