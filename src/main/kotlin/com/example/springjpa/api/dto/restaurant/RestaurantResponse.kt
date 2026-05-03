package com.example.springjpa.api.dto.restaurant

import com.example.springjpa.domain.model.Restaurant

data class RestaurantResponse(
    val id: Long,
    val name: String,
    val address: String,
) {
    companion object {
        fun fromDomain(r: Restaurant) =
            RestaurantResponse(r.id, r.name, r.address)
    }
}
