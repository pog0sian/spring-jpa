package com.example.springjpa.api.dto.restaurant

import com.example.springjpa.domain.model.Restaurant
import jakarta.validation.constraints.NotBlank

data class RestaurantUpdateRequest(
    @field:NotBlank(message = "Name must not be blank")
    val name: String,

    @field:NotBlank(message = "Address must not be blank")
    val address: String
) {
    fun toDomain(id: Long) = Restaurant(
        id = id,
        name = name,
        address = address,
        dishes = emptyList()
    )
}
