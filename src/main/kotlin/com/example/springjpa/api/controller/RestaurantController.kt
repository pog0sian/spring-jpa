package com.example.springjpa.api.controller

import com.example.springjpa.api.dto.dish.DishCreateRequest
import com.example.springjpa.api.dto.dish.DishResponse
import com.example.springjpa.api.dto.restaurant.RestaurantCreateRequest
import com.example.springjpa.api.dto.restaurant.RestaurantResponse
import com.example.springjpa.api.dto.restaurant.RestaurantUpdateRequest
import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.application.service.DishService
import com.example.springjpa.application.service.RestaurantService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/restaurants")
@Validated
class RestaurantController(
    private val service: RestaurantService,
    private val dishService: DishService
) {

    @GetMapping
    fun list(): List<RestaurantResponse> =
        service.list().map(RestaurantResponse::fromDomain)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): RestaurantResponse =
        RestaurantResponse.fromDomain(service.get(id))

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(
        @Valid @RequestBody req: RestaurantCreateRequest
    ): ResponseEntity<RestaurantResponse> {
        val (restaurant, created) = service.create(req.toDomain())
        return if (created) {
            ResponseEntity.status(201).body(RestaurantResponse.fromDomain(restaurant))
        } else {
            ResponseEntity.ok(RestaurantResponse.fromDomain(restaurant))
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: RestaurantUpdateRequest
    ): RestaurantResponse =
        RestaurantResponse.fromDomain(service.update(id, req.toDomain(id)))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{restaurantId}/dishes")
    fun listDishes(@PathVariable restaurantId: Long): List<DishResponse> =
        service.getWithDishes(restaurantId).dishes.map(DishResponse::fromDomain)

    @PostMapping("/{restaurantId}/dishes")
    @PreAuthorize("hasRole('ADMIN')")
    fun addDish(
        @PathVariable restaurantId: Long,
        @Valid @RequestBody req: DishCreateRequest
    ): ResponseEntity<DishResponse> {
        val (dish, created) = dishService.create(restaurantId, req.toDomain(restaurantId))
        return if (created) {
            ResponseEntity.status(201).body(DishResponse.fromDomain(dish))
        } else {
            throw NotFoundException("Restaurant with id=$restaurantId not found")
        }
    }
}
