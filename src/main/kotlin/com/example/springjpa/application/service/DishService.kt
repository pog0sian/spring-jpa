package com.example.springjpa.application.service

import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.domain.model.Dish
import com.example.springjpa.domain.port.DishRepositoryPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class DishService(
    private val repo: DishRepositoryPort
) {

    private val logger = KotlinLogging.logger {}

    fun list(namePart: String? = null) =
        if (namePart.isNullOrBlank()) repo.findAll(namePart = null) else repo.findAll(namePart = namePart)

    fun get(id: Long) =
        repo.findById(id) ?: run {
            logger.warn { "Dish not found id=$id" }
            throw NotFoundException("Dish with id=$id not found")
        }

    fun create(restaurantId: Long, cmd: Dish): Pair<Dish, Boolean> {
        val saved = repo.create(restaurantId, cmd.copy(id = 0))
        logger.info {
            "Created dish with id=${saved.id}, name=${saved.name}, restaurantId=$restaurantId"
        }
        return saved to true
    }

    fun update(id: Long, cmd: Dish): Dish {
        if (repo.findById(id) == null) {
            logger.warn { "Dish not found for update id=$id" }
            throw NotFoundException("Dish with id=$id not found")
        }

        return repo.update(cmd.copy(id = id))
    }

    fun delete(id: Long) {
        val existing = repo.findById(id) ?: run {
            logger.warn { "Attempt to delete non-existing dish id=$id" }
            throw NotFoundException("Dish with id=$id not found")
        }

        repo.delete(id)
        logger.info { "Deleted dish with id=$id, name=${existing.name}" }
    }

}
