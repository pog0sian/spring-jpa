package com.example.springjpa.infrastructure.persistence.adapter

import com.example.springjpa.domain.model.Dish
import com.example.springjpa.domain.port.DishRepositoryPort
import com.example.springjpa.infrastructure.persistence.entity.DishEntity
import com.example.springjpa.infrastructure.persistence.jpa.DishJpaRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("db")
class DishJpaAdapter(
    private val repo: DishJpaRepository
) : DishRepositoryPort {

    override fun findAll(namePart: String?): List<Dish> =
        repo.findAll()
            .map { it.toDomain() }
            .let { dishes ->
                if (namePart.isNullOrBlank()) dishes
                else dishes.filter { it.name.contains(namePart, ignoreCase = true) }
            }

    override fun findById(id: Long): Dish? =
        repo.findById(id).orElse(null)?.toDomain()

    override fun findByName(name: String): Dish? =
        repo.findByName(name)?.toDomain()

    override fun save(dish: Dish): Dish =
        repo.save(DishEntity.fromDomain(dish)).toDomain()

    override fun delete(id: Long) =
        repo.deleteById(id)
}