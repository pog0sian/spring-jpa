package com.example.springjpa.domain.port

import com.example.springjpa.domain.model.User

interface UserRepositoryPort {
    fun findAll(): List<User>
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun save(user: User): User
    fun delete(id: Long)
}