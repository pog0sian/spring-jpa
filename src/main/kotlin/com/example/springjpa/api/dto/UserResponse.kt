package com.example.springjpa.api.dto

import com.example.springjpa.domain.model.User

data class UserResponse(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val active: Boolean
) {
    companion object {
        fun fromDomain(u: User) =
            UserResponse(u.id, u.email, u.firstName, u.lastName, u.active)
    }
}
