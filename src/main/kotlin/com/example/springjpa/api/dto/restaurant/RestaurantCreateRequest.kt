package com.example.springjpa.api.dto.restaurant

import com.example.springjpa.domain.model.Restaurant
import jakarta.validation.constraints.NotBlank

data class RestaurantCreateRequest(
    @field:NotBlank(message = "Name must not be blank")
    val name: String,

    @field:NotBlank(message = "Address must not be blank")
    val address: String,
) {

    fun toDomain() = Restaurant(
        id = 0,
        name = name,
        address = address,
        dishes = emptyList()
    )
}
