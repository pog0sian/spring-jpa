package com.example.springjpa.infrastructure.persistence.entity

import com.example.springjpa.domain.model.Dish
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "dishes")
open class DishEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String = "",
    val description: String = "",
    val price: BigDecimal = BigDecimal.ZERO,
    val isAvailable: Boolean = true

) {
    fun toDomain() = Dish(id, name, description, price, isAvailable)

    companion object {
        fun fromDomain(d: Dish) =
            DishEntity(d.id, d.name, d.description, d.price, d.isAvailable)
    }
}