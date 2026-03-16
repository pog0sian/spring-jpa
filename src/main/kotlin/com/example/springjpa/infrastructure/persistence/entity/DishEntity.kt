package com.example.springjpa.infrastructure.persistence.entity

import com.example.springjpa.domain.model.Dish
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "dishes")
open class DishEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    @Column(name = "name", nullable = false)
    open val name: String = "",

    @Column(name = "description", nullable = false)
    open val description: String = "",

    @Column(name = "price", nullable = false)
    open val price: BigDecimal = BigDecimal.ZERO,

    @Column(name = "is_available", nullable = false)
    open val isAvailable: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    open val restaurant: RestaurantEntity = RestaurantEntity()

) {
    fun toDomain() = Dish(id, name, description, price, isAvailable, restaurant.id)

    companion object {
        fun fromDomain(d: Dish, restaurant: RestaurantEntity) =
            DishEntity(d.id, d.name, d.description, d.price, d.isAvailable, restaurant)
    }
}