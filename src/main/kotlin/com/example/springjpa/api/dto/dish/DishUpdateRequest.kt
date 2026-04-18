package com.example.springjpa.api.dto.dish

import com.example.springjpa.domain.model.Dish
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class DishUpdateRequest(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 1, message = "Name must be at least 1 character long")
    val name: String,

    @field:NotBlank(message = "Description cannot be blank")
    @field:Size(min = 1, message = "Description must be at least 1 character long")
    val description: String,

    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    val price: BigDecimal,

    @field:NotNull(message = "Availability must not be null")
    @field:JsonProperty("isAvailable")
    var isAvailable: Boolean,

) {
    fun toDomain(id: Long) = Dish(
        id = id,
        name = name,
        description = description,
        price = price,
        isAvailable = isAvailable,
        restaurantId = 0L
    )
}