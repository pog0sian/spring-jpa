package com.example.springjpa.infrastructure.persistence.adapter

import com.example.springjpa.domain.model.Order
import com.example.springjpa.domain.port.OrderRepositoryPort
import com.example.springjpa.infrastructure.persistence.entity.OrderEntity
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import com.example.springjpa.infrastructure.persistence.jpa.DishJpaRepository
import com.example.springjpa.infrastructure.persistence.jpa.OrderJpaRepository
import com.example.springjpa.infrastructure.persistence.jpa.UserJpaRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Profile("db")
class OrderJpaAdapter(
    private val repo: OrderJpaRepository,
    private val userRepo: UserJpaRepository,
    private val dishRepo: DishJpaRepository
): OrderRepositoryPort {

    override fun findAll(userId: Long?, status: OrderStatus?): List<Order> {
        val entities = when {
            userId != null && status != null -> repo.findAllByUser_IdAndStatus(userId, status)
            userId != null -> repo.findAllByUser_Id(userId)
            status != null -> repo.findAllByStatus(status)
            else -> repo.findAll()
        }
        return entities.map { it.toDomain() }
    }

    override fun findByIdWithUserAndDishes(id: Long): Order? =
        repo.findWithUserAndDishesById(id)?.toDomain()

    @Transactional
    override fun create(userId: Long, dishIds: List<Long>): Order {
        val userOpt = userRepo.findById(userId)
        if (userOpt.isEmpty) {
            throw IllegalArgumentException("User with id=$userId not found")
        }
        val user = userOpt.get()

        if (dishIds.isEmpty()) {
            throw IllegalArgumentException("dishIds must not be empty")
        }

        val dishes = dishRepo.findAllById(dishIds).toList()
        val foundIds = dishes.map { it.id }.toSet()
        val missing = dishIds.distinct().filter { it !in foundIds }
        if (missing.isNotEmpty()) {
            throw IllegalArgumentException("Dishes not found: $missing")
        }

        val entity = OrderEntity(
            id = 0,
            status = OrderStatus.PENDING,
            createdAt = LocalDateTime.now(),
            user = user,
            dishes = dishes.toMutableList()
        )

        return repo.save(entity).toDomain()
    }

    @Transactional
    override fun updateStatus(id: Long, status: OrderStatus): Order {
        val existing = repo.findById(id).orElseThrow {
            IllegalArgumentException("Order with id=$id not found")
        }

        existing.status = status
        return repo.save(existing).toDomain()
    }

    override fun delete(id: Long) =
        repo.deleteById(id)
}