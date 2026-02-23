package com.example.springjpa.api.dto

import com.example.springjpa.domain.model.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserCreateRequest(
    @field:Email @field:NotBlank
    val email: String,
    @field:NotBlank
    val firstName: String,
    @field:NotBlank
    val lastName: String,
    val active: Boolean = true
) {
    fun toDomain() = User(
        id = 0,
        email = email,
        firstName = firstName,
        lastName = lastName,
        active = active
    )
}
