package com.example.springjpa.application.service

import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.domain.model.Restaurant
import com.example.springjpa.domain.port.RestaurantRepositoryPort
import org.springframework.stereotype.Service

@Service
class RestaurantService(
    private val repo: RestaurantRepositoryPort
) {
    fun list() = repo.findAll()

    fun get(id: Long) =
        repo.findById(id) ?: throw NotFoundException("Restaurant with id=$id not found")

    fun create(cmd: Restaurant): Pair<Restaurant, Boolean> {
        return repo.save(cmd.copy(id = 0)) to true

        }

    fun update(id: Long, cmd: Restaurant): Restaurant {
        if (repo.findById(id) == null) {
            throw NotFoundException("Restaurant with id=$id not found")
        }
        return repo.save(cmd.copy(id = id))
    }

    fun delete(id: Long) {
        if (repo.findById(id) == null) {
            throw NotFoundException("Restaurant with id=$id not found")
        }

        repo.delete(id)
    }

    fun getWithDishes(id: Long) =
        repo.findByIdWithDishes(id) ?: throw NotFoundException("Restaurant with id=$id not found")
}