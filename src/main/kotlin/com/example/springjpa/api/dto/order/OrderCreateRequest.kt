package com.example.springjpa.api.dto.order

import jakarta.validation.constraints.Size

data class OrderCreateRequest(
    val userId: Long? = null,

    @field:Size(min = 1, message = "Dish IDs must contain at least one dish")
    val dishIds: List<Long>,
)
