package com.example.springjpa.infrastructure.persistence.entity

import com.example.springjpa.domain.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
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

    @Column(name = "password", nullable = false)
    open val password: String = "",

    @Column(name = "is_active", nullable = false)
    open val isActive: Boolean = true,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    open val roles: Set<RoleEntity> = emptySet()

) {

    fun toDomain() = User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        isActive = isActive,
        password = password,
        roles = roles.map { it.name }.toSet()
    )

    companion object {
        fun fromDomain(u: User) =
            UserEntity(
                id = u.id,
                email = u.email,
                firstName = u.firstName,
                lastName = u.lastName,
                password = u.password,
                isActive = u.isActive,
                roles = u.roles.map { RoleEntity(name = it) }.toSet()
            )
    }

}
