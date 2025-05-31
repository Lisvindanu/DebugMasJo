package com.example.kostkita.data.remote.dto

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val full_name: String,
    val role: String
)