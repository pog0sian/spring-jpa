package com.example.springjpa.api.dto

import com.example.springjpa.domain.model.Dish
import java.math.BigDecimal

data class DishUpdateRequest(
    val name: String,
    val description: String,
    val price: BigDecimal,
    val isAvailable: Boolean
) {
    fun toDomain(id: Long) = Dish(
        id = id,
        name = name,
        description = description,
        price = price,
        isAvailable = isAvailable
    )
}