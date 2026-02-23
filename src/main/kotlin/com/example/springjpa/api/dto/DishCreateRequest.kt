package com.example.springjpa.api.dto

import com.example.springjpa.domain.model.Dish
import java.math.BigDecimal

data class DishCreateRequest(
    val name: String,
    val description: String,
    val price: BigDecimal,
    val isAvailable: Boolean = true
) {
    fun toDomain() = Dish(
        id = 0,
        name = name,
        description = description,
        price = price,
        isAvailable = isAvailable
    )
}