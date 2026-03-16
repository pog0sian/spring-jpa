package com.example.springjpa.application.service

import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.domain.model.Order
import com.example.springjpa.domain.port.OrderRepositoryPort
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val repo: OrderRepositoryPort
) {

    fun list(userId: Long? = null, status: OrderStatus? = null): List<Order> =
        repo.findAll(userId = userId, status = status)

    fun get(id: Long): Order =
        repo.findByIdWithUserAndDishes(id) ?: throw NotFoundException("Order with id=$id not found")


    fun create(userId: Long, dishIds: List<Long>): Order =
        repo.create(userId = userId, dishIds = dishIds)

    fun updateStatus(id: Long, newStatus: OrderStatus): Order {
        val existing = repo.findByIdWithUserAndDishes(id)
            ?: throw NotFoundException("Order with id=$id not found")

        if (!isValidTransition(existing.status, newStatus)) {
            throw IllegalArgumentException("Invalid status transition: ${existing.status} -> $newStatus")
        }

        return repo.updateStatus(id, newStatus)
    }

    fun delete(id: Long) {
        if (repo.findByIdWithUserAndDishes(id) == null) {
            throw NotFoundException("Order with id=$id not found")
        }
        repo.delete(id)
    }

    private fun isValidTransition(old: OrderStatus, new: OrderStatus): Boolean {
        if (old == new) return true
        return when (old) {
            OrderStatus.PENDING -> new == OrderStatus.CONFIRMED || new == OrderStatus.CANCELLED
            OrderStatus.CONFIRMED -> new == OrderStatus.DELIVERED || new == OrderStatus.CANCELLED
            OrderStatus.DELIVERED -> false
            OrderStatus.CANCELLED -> false
        }
    }
}