package com.example.kostkita.data.repository

import android.content.Context
import com.example.kostkita.data.remote.api.AuthApiService
import com.example.kostkita.data.remote.dto.LoginRequest
import com.example.kostkita.domain.model.User
import com.example.kostkita.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = authApiService.login(LoginRequest(username, password))
            val user = User(
                id = response.user.id,
                username = response.user.username,
                email = response.user.email,
                fullName = response.user.full_name,
                role = response.user.role,
                token = response.token
            )
            saveToken(response.token)
            saveUserData(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        prefs.edit().clear().apply()
    }

    override suspend fun getCurrentUser(): User? {
        val token = getToken() ?: return null
        val id = prefs.getString("user_id", null) ?: return null
        val username = prefs.getString("username", null) ?: return null
        val email = prefs.getString("email", null) ?: return null
        val fullName = prefs.getString("full_name", null) ?: return null
        val role = prefs.getString("role", null) ?: return null

        return User(id, username, email, fullName, role, token)
    }

    override suspend fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    override suspend fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    private fun saveUserData(user: User) {
        prefs.edit().apply {
            putString("user_id", user.id)
            putString("username", user.username)
            putString("email", user.email)
            putString("full_name", user.fullName)
            putString("role", user.role)
            apply()
        }
    }
}