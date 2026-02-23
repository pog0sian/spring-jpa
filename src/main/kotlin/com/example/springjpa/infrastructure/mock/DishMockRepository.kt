package com.example.springjpa.infrastructure.mock

import com.example.springjpa.domain.model.Dish
import com.example.springjpa.domain.port.DishRepositoryPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("mock")
class DishMockRepository : DishRepositoryPort {

    private val storage = mutableMapOf<Long, Dish>()
    private val seq = 1L

    override fun findAll(namePart: String?): List<Dish> =
        storage.values
            .filter { dish ->
                namePart.isNullOrBlank() || dish.name.contains(namePart, ignoreCase = true)
            }

    override fun findById(id: Long): Dish? =
        storage[id]

    override fun findByName(name: String): Dish? =
        storage.values.firstOrNull { it.name.equals(name, ignoreCase = true) }

    override fun save(dish: Dish): Dish {
        val id = if (dish.id == 0L) seq else dish.id
        val saved = dish.copy(id = id)
        storage[id] = saved
        return saved
    }

    override fun delete(id: Long) {
        storage.remove(id)
    }
}