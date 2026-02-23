package com.example.springjpa.api.dto

import com.example.springjpa.domain.model.User

data class UserUpdateRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val active: Boolean
) {
    fun toDomain(id: Long) = User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        active = active
    )
}
