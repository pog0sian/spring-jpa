package com.example.springjpa.api.dto.dish

import com.example.springjpa.domain.model.Dish
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class DishCreateRequest(
    @field:NotBlank(message = "Name must not be blank")
    @field:Size(min = 1, message = "Name must be at least 1 character long")
    val name: String,

    @field:NotBlank(message = "Description must not be blank")
    @field:Size(min = 1, message = "Description must be at least 1 character long")
    val description: String,

    @field:NotNull(message = "Price must not be null")
    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    var price: BigDecimal,

    @field:NotNull(message = "Availability must not be null")
    @field:JsonProperty("isAvailable")
    var isAvailable: Boolean,

) {
    fun toDomain(restaurantId: Long) = Dish(
        id = 0,
        name = name,
        description = description,
        price = price,
        isAvailable = isAvailable,
        restaurantId = restaurantId
    )
}