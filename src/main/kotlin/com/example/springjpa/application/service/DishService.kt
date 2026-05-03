package com.example.springjpa.application.service

import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.domain.model.Dish
import com.example.springjpa.domain.port.DishRepositoryPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service

@Service
class DishService(
    private val repo: DishRepositoryPort,
    private val cacheManager: CacheManager
) {

    private val logger = KotlinLogging.logger {}

    @Cacheable(cacheNames = ["dishes"], key = "'all:' + (#namePart ?: '')")
    fun list(namePart: String? = null): List<Dish> {
        logger.info { "Loading dishes from DB, namePart=$namePart" }
        return if (namePart.isNullOrBlank()) repo.findAll(null) else repo.findAll(namePart)
    }

    @Cacheable(cacheNames = ["dishes"], key = "'dish:' + #id")
    fun get(id: Long): Dish {
        logger.info { "Loading dish id=$id from DB" }
        return repo.findById(id) ?: throw NotFoundException("Dish with id=$id not found")
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["dishes"], key = "#restaurantId"),
        CacheEvict(cacheNames = ["dishes"], allEntries = true),
        CacheEvict(cacheNames = ["restaurants"], allEntries = true)
    ])
    fun create(restaurantId: Long, cmd: Dish): Pair<Dish, Boolean> {
        val saved = repo.create(restaurantId, cmd.copy(id = 0))
        logger.info {
            "Created dish with id=${saved.id}, name=${saved.name}, restaurantId=$restaurantId"
        }
        return saved to true
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["dishes"], key = "#result.restaurantId"),
        CacheEvict(cacheNames = ["dishes"], key = "'dish:' + #id"),
        CacheEvict(cacheNames = ["dishes"], allEntries = true),
        CacheEvict(cacheNames = ["restaurants"], allEntries = true)
    ])
    fun update(id: Long, cmd: Dish): Dish {
        if (repo.findById(id) == null) {
            logger.warn { "Dish not found for update id=$id" }
            throw NotFoundException("Dish with id=$id not found")
        }

        return repo.update(cmd.copy(id = id))
    }

    fun delete(id: Long) {
        val existing = repo.findById(id) ?: throw NotFoundException("Dish with id=$id not found")
        repo.delete(id)

        cacheManager.getCache("dishes")?.evict(existing.restaurantId)
        cacheManager.getCache("dishes")?.evict("dish:$id")
        cacheManager.getCache("dishes")?.clear()
        cacheManager.getCache("restaurants")?.clear()
        logger.info { "Deleted dish with id=$id, name=${existing.name}" }
    }
}
