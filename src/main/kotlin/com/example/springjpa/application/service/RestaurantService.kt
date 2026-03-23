package com.example.springjpa.application.service

import com.example.springjpa.application.exception.AlreadyExistsException
import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.domain.model.Restaurant
import com.example.springjpa.domain.port.RestaurantRepositoryPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class RestaurantService(
    private val repo: RestaurantRepositoryPort
) {
    private val logger = KotlinLogging.logger {}

    fun list() = repo.findAll()

    fun get(id: Long): Restaurant =
        repo.findById(id) ?: run {
            logger.warn { "Restaurant not found id=$id" }
            throw NotFoundException("Restaurant with id=$id not found")
        }

    fun create(cmd: Restaurant): Pair<Restaurant, Boolean> {
        val existing = repo.findByName(cmd.name)
        if (existing != null) {
            logger.warn { "Attempt to create duplicate restaurant with name=${cmd.name}" }
            throw AlreadyExistsException("Restaurant with name=${cmd.name} already exists")
        }

        val saved = repo.save(cmd.copy(id = 0))
        logger.info { "Created restaurant with id=${saved.id}, name=${saved.name}" }

        return saved to true
        }

    fun update(id: Long, cmd: Restaurant): Restaurant {
        if (repo.findById(id) == null) {
            throw NotFoundException("Restaurant with id=$id not found")
        }
        return repo.save(cmd.copy(id = id))
    }

    fun delete(id: Long) {
        val existing = repo.findById(id)
            ?: run {
                logger.warn { "Attempt to delete non-existing restaurant id=$id" }
                throw NotFoundException("Restaurant with id=$id not found")
            }

        repo.delete(id)
        logger.info { "Deleted restaurant with id=$id, name=${existing.name}" }
    }

    fun getWithDishes(id: Long) =
        repo.findByIdWithDishes(id) ?: run {
            logger.warn { "Restaurant with dishes not found id=$id" }
            throw NotFoundException("Restaurant with id=$id not found")
        }
}
