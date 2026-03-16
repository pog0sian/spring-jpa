package com.example.springjpa.api.dto.order

import com.example.springjpa.api.dto.dish.DishResponse
import com.example.springjpa.domain.model.Order
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val status: OrderStatus,
    val createdAt: String,
    val dishes: List<DishResponse>,
) {

    companion object {
        fun fromDomain(o: Order) =
            OrderResponse(o.id, o.userId, o.status, o.createdAt.toString(), o.dishes.map { DishResponse.fromDomain(it) })
    }

}
