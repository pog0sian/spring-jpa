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
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val email: String = "",

    val firstName: String = "",
    val lastName: String = "",
    val active: Boolean = true

) {

    fun toDomain() = User(id, email, firstName, lastName, active)

    companion object {
        fun fromDomain(u: User) =
            UserEntity(u.id, u.email, u.firstName, u.lastName, u.active)

    }
}