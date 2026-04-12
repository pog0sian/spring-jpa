package com.example.springjpa.application.service

import com.example.springjpa.application.exception.BadRequestException
import com.example.springjpa.application.exception.InvalidOrderStateException
import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.domain.model.Order
import com.example.springjpa.domain.port.DishRepositoryPort
import com.example.springjpa.domain.port.OrderRepositoryPort
import com.example.springjpa.domain.port.UserRepositoryPort
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import com.example.springjpa.infrastructure.persistence.jpa.UserJpaRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val repo: OrderRepositoryPort,
    private val userRepo: UserRepositoryPort,
    private val dishRepo: DishRepositoryPort,
    private val userJpaRepository: UserJpaRepository
) {

    private val logger = KotlinLogging.logger {}

    fun list(userId: Long? = null, status: OrderStatus? = null): List<Order> =
        repo.findAll(userId = userId, status = status)

    fun get(id: Long): Order =
        repo.findByIdWithUserAndDishes(id) ?: run {
            logger.warn { "Order not found id=$id" }
            throw NotFoundException("Order with id=$id not found")
        }

    fun isOwner(orderId: Long, userId: Long): Boolean =
        repo.findByIdWithUserAndDishes(orderId)?.userId == userId


    fun create(userId: Long, dishIds: List<Long>): Order {
        val existingUser = userRepo.findById(userId) ?: throw BadRequestException("User with id=$userId not found")

        val dishes = dishRepo.findAllById(dishIds)

        if (dishes.size != dishIds.size) {
            throw BadRequestException("Some dishes not found")
        }

        val saved = repo.create(userId = userId, dishIds = dishIds)
        logger.info { "Created order with id=${saved.id}, userId=$userId" }
        return saved
    }

    fun createForUserEmail(userEmail: String, dishIds: List<Long>): Order {
        val user = userJpaRepository.findByEmail(userEmail)
            ?: throw BadRequestException("User with email=$userEmail not found")
        return create(user.id, dishIds)
    }

    fun updateStatus(id: Long, newStatus: OrderStatus): Order {
        val existing = repo.findByIdWithUserAndDishes(id)
            ?: run {
                logger.warn { "Order not found for status update id=$id" }
                throw NotFoundException("Order with id=$id not found")
            }

        if (!isValidTransition(existing.status, newStatus)) {
            throw InvalidOrderStateException("Invalid status transition: ${existing.status} -> $newStatus")
        }

        return repo.updateStatus(id, newStatus)
    }

    fun delete(id: Long) {
        val existing = repo.findByIdWithUserAndDishes(id) ?: run {
            logger.warn { "Attempt to delete non-existing order id=$id" }
            throw NotFoundException("Order with id=$id not found")
        }
        repo.delete(id)
        logger.info { "Deleted order with id=$id, status=${existing.status}" }
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
