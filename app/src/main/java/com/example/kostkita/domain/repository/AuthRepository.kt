package com.example.kostkita.domain.repository

import com.example.kostkita.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
}