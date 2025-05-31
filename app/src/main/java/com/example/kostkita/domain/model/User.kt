package com.example.kostkita.domain.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val role: String,
    val token: String? = null
)