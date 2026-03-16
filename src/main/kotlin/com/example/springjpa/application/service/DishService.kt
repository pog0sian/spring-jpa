package com.example.springjpa.application.service

import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.domain.model.Dish
import com.example.springjpa.domain.port.DishRepositoryPort
import org.springframework.stereotype.Service

@Service
class DishService(
    private val repo: DishRepositoryPort
) {
    fun list(namePart: String? = null) =
        if (namePart.isNullOrBlank()) repo.findAll(namePart = null) else repo.findAll(namePart = namePart)

    fun get(id: Long) =
        repo.findById(id) ?: throw NotFoundException("Dish with id=$id not found")

    fun create(restaurantId: Long, cmd: Dish): Pair<Dish, Boolean> {
        return repo.create(restaurantId, cmd.copy(id = 0)) to true
    }

    fun update(id: Long, cmd: Dish): Dish {
        if (repo.findById(id) == null) {
            throw NotFoundException("Dish with id=$id not found")
        }

        return repo.update(cmd.copy(id = id))
    }

    fun delete(id: Long) {
        if (repo.findById(id) == null) {
            throw NotFoundException("Dish with id=$id not found")
        }

        repo.delete(id)
    }

}