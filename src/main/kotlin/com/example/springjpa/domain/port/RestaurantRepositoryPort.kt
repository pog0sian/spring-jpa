package com.example.springjpa.domain.port

import com.example.springjpa.domain.model.Restaurant

interface RestaurantRepositoryPort {
    fun findAll(): List<Restaurant>
    fun findById(id: Long): Restaurant?
    fun findByAddress(address: String): Restaurant?
    fun findByIdWithDishes(id: Long): Restaurant?
    fun save(restaurant: Restaurant): Restaurant
    fun delete(id: Long)
}