package com.example.springjpa.api.controller

import com.example.springjpa.api.dto.DishCreateRequest
import com.example.springjpa.api.dto.DishResponse
import com.example.springjpa.api.dto.DishUpdateRequest
import com.example.springjpa.application.service.DishService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/dishes")
class DishController(
    private val service: DishService
) {

    @GetMapping
    fun list(@RequestParam(required = false) namePart: String?): List<DishResponse> =
        service.list(namePart).map(DishResponse::fromDomain)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): DishResponse =
        DishResponse.fromDomain(service.get(id))

    @PostMapping
    fun create(
        @Valid @RequestBody req: DishCreateRequest
    ): ResponseEntity<DishResponse> {
        val (dish, created) = service.create(req.toDomain())
        return if (created) {
            ResponseEntity.status(201).body(DishResponse.fromDomain(dish))
        } else {
            ResponseEntity.ok(DishResponse.fromDomain(dish))
        }
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: DishUpdateRequest
    ): DishResponse =
        DishResponse.fromDomain(service.update(id, req.toDomain(id)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}