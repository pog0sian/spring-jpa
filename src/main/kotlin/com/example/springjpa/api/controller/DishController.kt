package com.example.springjpa.api.controller

import com.example.springjpa.api.dto.dish.DishResponse
import com.example.springjpa.api.dto.dish.DishUpdateRequest
import com.example.springjpa.application.service.DishService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/dishes")
@Validated
@Tag(name = "Dishes", description = "Управление блюдами")
class DishController(
    private val service: DishService
) {

    @GetMapping
    @Operation(summary = "Получить список всех блюд. Можно фильтровать по части названия блюда, используя параметр namePart.")
    fun list(@RequestParam(required = false) namePart: String?): List<DishResponse> =
        service.list(namePart).map(DishResponse::fromDomain)

    @GetMapping("/{id}")
    @Operation(summary = "Получить блюдо по ID")
    fun get(@PathVariable id: Long): DishResponse =
        DishResponse.fromDomain(service.get(id))

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить существующее блюдо по ID")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: DishUpdateRequest
    ): DishResponse =
        DishResponse.fromDomain(service.update(id, req.toDomain(id)))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить блюдо по ID")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
