package com.example.springjpa.infrastructure.persistence.entity

import com.example.springjpa.domain.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
open class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    @Column(name = "email", unique = true, nullable = false)
    open val email: String = "",

    @Column(name = "first_name", nullable = false)
    open val firstName: String = "",

    @Column(name = "last_name", nullable = false)
    open val lastName: String = "",

    @Column(name = "is_active", nullable = false)
    open val isActive: Boolean = true

) {

    fun toDomain() = User(id, email, firstName, lastName, isActive)

    companion object {
        fun fromDomain(u: User) =
            UserEntity(u.id, u.email, u.firstName, u.lastName, u.isActive)

    }
}