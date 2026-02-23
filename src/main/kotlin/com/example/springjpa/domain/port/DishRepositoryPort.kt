package com.example.springjpa.domain.port

import com.example.springjpa.domain.model.Dish

interface DishRepositoryPort {
    fun findAll(namePart: String?): List<Dish>
    fun findById(id: Long): Dish?
    fun findByName(name: String): Dish?
    fun save(dish: Dish): Dish
    fun delete(id: Long)
}