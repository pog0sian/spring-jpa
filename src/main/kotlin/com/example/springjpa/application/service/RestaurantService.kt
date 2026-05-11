package com.example.springjpa.application.service

import com.example.springjpa.application.exception.AlreadyExistsException
import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.domain.model.Restaurant
import com.example.springjpa.domain.port.RestaurantRepositoryPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service

@Service
class RestaurantService(
    private val repo: RestaurantRepositoryPort
) {
    private val logger = KotlinLogging.logger {}

    @Cacheable(cacheNames = ["restaurants"], key = "'all'")
    fun list(): List<Restaurant> {
        logger.info { "Loading all restaurants from DB" }
        return repo.findAll()
    }


    @Cacheable(cacheNames = ["restaurants"], key = "#id")
    fun get(id: Long): Restaurant {
        logger.info { "Loading restaurant id=$id from DB" }
        return repo.findById(id) ?: throw NotFoundException("Restaurant with id=$id not found")
    }

    @CacheEvict(cacheNames = ["restaurants"], allEntries = true)
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

    @Caching(evict = [
        CacheEvict(cacheNames = ["restaurants"], allEntries = true),
        CacheEvict(cacheNames = ["dishes"], key = "#id")
    ])
    fun update(id: Long, cmd: Restaurant): Restaurant {
        if (repo.findById(id) == null) {
            throw NotFoundException("Restaurant with id=$id not found")
        }
        return repo.save(cmd.copy(id = id))
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["restaurants"], allEntries = true),
        CacheEvict(cacheNames = ["dishes"], allEntries = true)
    ])
    fun delete(id: Long) {
        val existing = repo.findById(id)
            ?: run {
                logger.warn { "Attempt to delete non-existing restaurant id=$id" }
                throw NotFoundException("Restaurant with id=$id not found")
            }

        repo.delete(id)
        logger.info { "Deleted restaurant with id=$id, name=${existing.name}" }
    }

    @Cacheable(cacheNames = ["dishes"], key = "#id")
    fun getWithDishes(id: Long): Restaurant {
        logger.info { "Loading restaurant menu id=$id from DB" }
        return repo.findByIdWithDishes(id) ?: throw NotFoundException("Restaurant with id=$id not found")
    }
}
