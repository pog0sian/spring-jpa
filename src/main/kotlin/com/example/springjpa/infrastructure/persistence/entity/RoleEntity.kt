package com.example.springjpa.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "roles")
open class RoleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    @Column(nullable = false, unique = true)
    open val name: String = ""

)
