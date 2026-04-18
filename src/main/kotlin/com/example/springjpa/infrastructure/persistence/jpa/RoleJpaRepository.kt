package com.example.springjpa.infrastructure.persistence.jpa

import com.example.springjpa.infrastructure.persistence.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RoleJpaRepository : JpaRepository<RoleEntity, Long> {
    fun findByName(name: String): RoleEntity?
}