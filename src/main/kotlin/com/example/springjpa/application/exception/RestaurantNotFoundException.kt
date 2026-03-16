package com.example.springjpa.application.exception

class RestaurantNotFoundException(id: Long) : RuntimeException("Restaurant with id $id not found")