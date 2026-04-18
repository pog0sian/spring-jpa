package com.example.springjpa.api.dto.order

import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import jakarta.validation.constraints.NotNull

data class OrderStatusUpdateRequest(
    @field:NotNull(message = "Status cannot be null")
    var status: OrderStatus,
)