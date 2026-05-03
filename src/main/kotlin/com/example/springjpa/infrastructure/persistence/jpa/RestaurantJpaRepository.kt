package com.example.springjpa.infrastructure.persistence.jpa

import com.example.springjpa.infrastructure.persistence.entity.RestaurantEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface RestaurantJpaRepository: JpaRepository<RestaurantEntity, Long> {
    fun findByName(name: String): RestaurantEntity?

    @EntityGraph(attributePaths = ["dishes"])
    fun findWithDishesById(id: Long): RestaurantEntity?
}
