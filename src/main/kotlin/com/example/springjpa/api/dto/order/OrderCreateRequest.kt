package com.example.springjpa.api.dto.order

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class OrderCreateRequest(
    @field:NotNull(message = "User ID must not be blank")
    var userId: Long,

    @field:NotNull(message = "Dish IDs must not be blank")
    @field:Size(min = 1, message = "Dish IDs must contain at least one dish")
    var dishIds: List<Long>,
)