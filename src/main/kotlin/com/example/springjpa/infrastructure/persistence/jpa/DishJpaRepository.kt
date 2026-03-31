package com.example.springjpa.infrastructure.persistence.jpa

import com.example.springjpa.infrastructure.persistence.entity.DishEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DishJpaRepository : JpaRepository<DishEntity, Long> {
    fun findByName(name: String): DishEntity?
}