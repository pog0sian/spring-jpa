package com.example.springjpa.infrastructure.persistence.adapter

import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.application.exception.RestaurantNotFoundException
import com.example.springjpa.domain.model.Dish
import com.example.springjpa.domain.port.DishRepositoryPort
import com.example.springjpa.infrastructure.persistence.entity.DishEntity
import com.example.springjpa.infrastructure.persistence.jpa.DishJpaRepository
import com.example.springjpa.infrastructure.persistence.jpa.RestaurantJpaRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("db")
class DishJpaAdapter(
    private val repo: DishJpaRepository,
    private val restaurantRepo: RestaurantJpaRepository
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

    override fun create(restaurantId: Long, dish: Dish): Dish {
        val restaurant = restaurantRepo.findById(restaurantId)
            .orElseThrow { RestaurantNotFoundException(restaurantId) }

        val entity = DishEntity.fromDomain(dish, restaurant)

        return repo.save(entity).toDomain()
    }

    override fun update(dish: Dish): Dish {
        val existing = repo.findById(dish.id)
            .orElseThrow { NotFoundException("Dish with id=${dish.id} not found") }

        val restaurant = restaurantRepo.findById(existing.restaurant.id)
            .orElseThrow { RestaurantNotFoundException(existing.restaurant.id) }

        val entity = DishEntity.fromDomain(dish, restaurant)

        return repo.save(entity).toDomain()
    }


    override fun delete(id: Long) =
        repo.deleteById(id)
}