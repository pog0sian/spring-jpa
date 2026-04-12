package com.example.springjpa.api.dto.user

import com.example.springjpa.domain.model.User
import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    @JsonProperty("isActive")
    val isActive: Boolean
) {
    companion object {
        fun fromDomain(u: User) =
            UserResponse(u.id, u.email, u.firstName, u.lastName, u.isActive)
    }
}
