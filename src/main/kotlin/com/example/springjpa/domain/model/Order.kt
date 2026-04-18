package com.example.springjpa.domain.model

import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import java.time.LocalDateTime

data class Order(
    val id: Long,
    val status: OrderStatus,
    val createdAt: LocalDateTime,
    val userId: Long,
    val dishes: List<Dish>,
)
