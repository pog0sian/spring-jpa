package com.example.springjpa.application.service

import com.example.springjpa.domain.model.User
import org.springframework.stereotype.Service
import com.example.springjpa.domain.port.UserRepositoryPort

@Service
class UserService(
    private val repo: UserRepositoryPort
) {
    fun list() = repo.findAll()

    fun get(id: Long): User =
        repo.findById(id) ?: throw NotFoundException("User with id=$id not found")

    fun create(cmd: User): Pair<User, Boolean> {
        val existing = repo.findByEmail(cmd.email)
        return if (existing != null) {
            existing to false
        } else {
            repo.save(cmd.copy(id = 0)) to true
        }
    }

    fun update(id: Long, cmd: User): User {
        if (repo.findById(id) == null) {
            throw NotFoundException("User with id=$id not found")
        }

        return repo.save(cmd.copy(id = id))
    }

    fun delete(id: Long) {
        if (repo.findById(id) == null) {
            throw NotFoundException("User with id=$id not found")
        }

        repo.delete(id)
    }
}


class NotFoundException(message: String) : RuntimeException(message)