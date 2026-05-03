package com.example.springjpa.api.dto.dish

import com.example.springjpa.domain.model.Dish
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class DishResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    @get:JsonProperty("isAvailable")
    @param:JsonProperty("isAvailable")
    val isAvailable: Boolean,
    val restaurantId: Long
) {
    companion object {
        fun fromDomain(d: Dish) =
            DishResponse(d.id, d.name, d.description, d.price, d.isAvailable, d.restaurantId)
    }
}
