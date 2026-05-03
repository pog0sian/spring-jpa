package com.example.springjpa.api.dto.user

import com.example.springjpa.domain.model.User
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserUpdateRequest(
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "First name cannot be blank")
    @field:Size(min = 1, message = "First name must be at least 1 character long")
    val firstName: String,

    @field:NotBlank(message = "Last name cannot be blank")
    @field:Size(min = 1, message = "Last name must be at least 1 character long")
    val lastName: String,

    @field:JsonProperty("isActive")
    val isActive: Boolean
) {
    fun toDomain(id: Long) = User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        isActive = isActive
    )
}
