package com.example.springjpa.infrastructure.persistence.jpa

import com.example.springjpa.infrastructure.persistence.entity.OrderEntity
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface OrderJpaRepository : JpaRepository<OrderEntity, Long> {

    @EntityGraph(attributePaths = ["user", "dishes"])
    fun findWithUserAndDishesById(id: Long): OrderEntity?

    fun findAllByUser_Id(userId: Long): List<OrderEntity>

    fun findAllByStatus(status: OrderStatus): List<OrderEntity>

    fun findAllByUser_IdAndStatus(userId: Long, status: OrderStatus): List<OrderEntity>

}