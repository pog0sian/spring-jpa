package com.example.springjpa.infrastructure.persistence.entity

import com.example.springjpa.domain.model.Restaurant
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "restaurants")
open class RestaurantEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    @Column(name = "name", nullable = false)
    open val name: String = "",

    @Column(name = "address", nullable = false)
    open val address: String = "",

    @OneToMany(mappedBy = "restaurant")
    open val dishes: MutableList<DishEntity> = mutableListOf()

) {

    fun toDomain() = Restaurant(id, name, address, dishes = mutableListOf())

    fun toDomainWithDishes() = Restaurant(id, name, address, dishes.map { it.toDomain() })

    companion object {
        fun fromDomain(r: Restaurant) =
            RestaurantEntity(r.id, r.name, r.address)

    }

}