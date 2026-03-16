package com.example.springjpa.domain.port

import com.example.springjpa.domain.model.Order
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus

interface OrderRepositoryPort {
    fun findAll(userId: Long? = null, status: OrderStatus? = null): List<Order>
    fun findByIdWithUserAndDishes(id: Long): Order?
    fun create(userId: Long, dishIds: List<Long>): Order
    fun updateStatus(id: Long, status: OrderStatus): Order
    fun delete(id: Long)
}