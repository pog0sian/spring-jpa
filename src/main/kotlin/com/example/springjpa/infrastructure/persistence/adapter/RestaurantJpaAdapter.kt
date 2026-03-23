package com.example.springjpa.infrastructure.persistence.adapter

import com.example.springjpa.domain.model.Restaurant
import com.example.springjpa.domain.port.RestaurantRepositoryPort
import com.example.springjpa.infrastructure.persistence.entity.RestaurantEntity
import com.example.springjpa.infrastructure.persistence.jpa.RestaurantJpaRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("db")
class RestaurantJpaAdapter(
    private val repo: RestaurantJpaRepository
) : RestaurantRepositoryPort {

    override fun findAll(): List<Restaurant> =
        repo.findAll().map { it.toDomain() }

    override fun findById(id: Long): Restaurant? =
        repo.findById(id).orElse(null)?.toDomain()

    override fun findByName(name: String): Restaurant? =
        repo.findByName(name)?.toDomain()

    override fun findByIdWithDishes(id: Long): Restaurant? =
        repo.findWithDishesById(id)?.toDomainWithDishes()

    override fun save(restaurant: Restaurant): Restaurant =
        repo.save(RestaurantEntity.fromDomain(restaurant)).toDomain()

    override fun delete(id: Long) =
        repo.deleteById(id)
}