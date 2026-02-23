package com.example.springjpa.api.dto

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)
