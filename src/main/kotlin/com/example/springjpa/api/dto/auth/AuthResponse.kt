package com.example.springjpa.api.dto.auth

data class AuthResponse(
    val token: String,
    val email: String,
    val roles: List<String>,
)