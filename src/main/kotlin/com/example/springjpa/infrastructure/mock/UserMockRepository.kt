package com.example.springjpa.infrastructure.mock

import com.example.springjpa.domain.model.User
import com.example.springjpa.domain.port.UserRepositoryPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("mock")
class UserMockRepository : UserRepositoryPort {

    private val storage = mutableMapOf<Long, User>()
    private var seq = 1L

    override fun findAll(): List<User> =
        storage.values.toList()

    override fun findById(id: Long): User? =
        storage[id]

    override fun findByEmail(email: String): User? =
        storage.values.find { it.email == email }

    override fun save(user: User): User {
        val id = if (user.id == 0L) seq++ else user.id
        val saved = user.copy(id = id)
        storage[id] = saved
        return saved
    }

    override fun delete(id: Long) {
        storage.remove(id)
    }
}