package com.example.springjpa.domain.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class Dish(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    @get:JsonProperty("isAvailable")
    @param:JsonProperty("isAvailable")
    val isAvailable: Boolean,
    val restaurantId: Long
)
