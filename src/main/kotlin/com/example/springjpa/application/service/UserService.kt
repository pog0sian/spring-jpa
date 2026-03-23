package com.example.springjpa.application.service

import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.domain.model.User
import com.example.springjpa.domain.port.UserRepositoryPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repo: UserRepositoryPort
) {

    private val logger = KotlinLogging.logger {}

    fun list() = repo.findAll()

    fun get(id: Long): User =
        repo.findById(id) ?: run {
            logger.warn { "User not found id=$id" }
            throw NotFoundException("User with id=$id not found")
        }

    fun create(cmd: User): Pair<User, Boolean> {
        val existing = repo.findByEmail(cmd.email)
        return if (existing != null) {
            existing to false
        } else {
            val saved = repo.save(cmd.copy(id = 0))
            logger.info { "Created user with id=${saved.id}, email=${saved.email}" }
            saved to true
        }
    }

    fun update(id: Long, cmd: User): User {
        if (repo.findById(id) == null) {
            logger.warn { "User not found for update id=$id" }
            throw NotFoundException("User with id=$id not found")
        }

        return repo.save(cmd.copy(id = id))
    }

    fun delete(id: Long) {
        val existing = repo.findById(id) ?: run {
            logger.warn { "Attempt to delete non-existing user id=$id" }
            throw NotFoundException("User with id=$id not found")
        }

        repo.delete(id)
        logger.info { "Deleted user with id=$id, email=${existing.email}" }
    }
}

