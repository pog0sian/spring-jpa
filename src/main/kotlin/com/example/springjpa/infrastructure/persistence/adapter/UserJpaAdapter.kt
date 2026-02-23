package com.example.springjpa.infrastructure.persistence.adapter

import com.example.springjpa.domain.model.User
import com.example.springjpa.domain.port.UserRepositoryPort
import com.example.springjpa.infrastructure.persistence.entity.UserEntity
import com.example.springjpa.infrastructure.persistence.jpa.UserJpaRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("db")
class UserJpaAdapter(
    private val repo: UserJpaRepository
) : UserRepositoryPort {

    override fun findAll() =
        repo.findAll().map { it.toDomain() }

    override fun findById(id: Long): User? =
        repo.findById(id).orElse(null)?.toDomain()

    override fun findByEmail(email: String): User? =
        repo.findByEmail(email)?.toDomain()

    override fun save(user: User): User =
        repo.save(UserEntity.fromDomain(user)).toDomain()

    override fun delete(id: Long) =
        repo.deleteById(id)
}