package com.example.springjpa.application.service

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

    fun create(cmd: Dish): Pair<Dish, Boolean> {
        val existing = repo.findByName(cmd.name)
        return if (existing != null) {
            existing to false
        } else {
            repo.save(cmd.copy(id = 0)) to true
        }
    }

    fun update(id: Long, cmd: Dish): Dish {
        if (repo.findById(id) == null) {
            throw NotFoundException("Dish with id=$id not found")
        }

        return repo.save(cmd.copy(id = id))
    }

    fun delete(id: Long) {
        if (repo.findById(id) == null) {
            throw NotFoundException("Dish with id=$id not found")
        }

        repo.delete(id)
    }

}