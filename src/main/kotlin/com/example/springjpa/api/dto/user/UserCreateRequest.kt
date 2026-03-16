package com.example.springjpa.api.dto.user

import com.example.springjpa.domain.model.User
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserCreateRequest(
    @field:Email(message = "Email should be valid")
    @field:NotBlank(message = "Email must not be blank")
    val email: String,

    @field:NotBlank(message = "First name must not be blank")
    @field:Size(min = 1, message = "First name must be at least 1 character long")
    val firstName: String,

    @field:NotBlank(message = "Last name must not be blank")
    @field:Size(min = 1, message = "Last name must be at least 1 character long")
    val lastName: String,

    @field:JsonProperty("isActive")
    val isActive: Boolean = true
) {
    fun toDomain() = User(
        id = 0,
        email = email,
        firstName = firstName,
        lastName = lastName,
        isActive = isActive
    )
}
