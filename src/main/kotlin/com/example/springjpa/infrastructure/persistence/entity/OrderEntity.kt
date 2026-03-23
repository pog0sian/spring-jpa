package com.example.springjpa.infrastructure.persistence.entity

import com.example.springjpa.domain.model.Order
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
open class OrderEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    open var status: OrderStatus = OrderStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    open val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: UserEntity = UserEntity(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "order_dishes",
        joinColumns = [JoinColumn(name = "order_id")],
        inverseJoinColumns = [JoinColumn(name = "dish_id")]
    )
    open val dishes: MutableList<DishEntity> = mutableListOf()
) {

    fun toDomain() = Order(
        id = id,
        status = status,
        createdAt = createdAt,
        userId = user.id,
        dishes = dishes.map { it.toDomain() }
    )
}