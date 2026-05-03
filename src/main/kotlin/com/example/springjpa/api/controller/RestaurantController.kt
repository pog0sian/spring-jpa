package com.example.springjpa.api.controller

import com.example.springjpa.api.dto.dish.DishCreateRequest
import com.example.springjpa.api.dto.dish.DishResponse
import com.example.springjpa.api.dto.restaurant.RestaurantCreateRequest
import com.example.springjpa.api.dto.restaurant.RestaurantResponse
import com.example.springjpa.api.dto.restaurant.RestaurantUpdateRequest
import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.application.service.DishService
import com.example.springjpa.application.service.RestaurantService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
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
@Tag(name = "Restaurants", description = "Управление ресторанами")
class RestaurantController(
    private val service: RestaurantService,
    private val dishService: DishService
) {

    @GetMapping
    @Operation(summary = "Получить список всех ресторанов")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешный ответ с списком ресторанов"),
        ]
    )
    fun list(): List<RestaurantResponse> =
        service.list().map(RestaurantResponse::fromDomain)

    @GetMapping("/{id}")
    @Operation(summary = "Получить ресторан по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешный ответ с рестораном"),
            ApiResponse(responseCode = "404", description = "Ресторан с таким ID не найден")
        ]
    )
    fun get(@PathVariable id: Long): RestaurantResponse =
        RestaurantResponse.fromDomain(service.get(id))

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новый ресторан. Если ресторан с таким именем уже существует, будет возвращен существующий ресторан.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Ресторан успешно создан"),
            ApiResponse(responseCode = "200", description = "Ресторан с таким именем уже существует, возвращен существующий ресторан")
        ]
    )
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
    @Operation(summary = "Обновить ресторан по ID. Если ресторан с новым именем уже существует, будет возвращен существующий ресторан с этим именем.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Ресторан успешно обновлен или ресторан с новым именем уже существует, возвращен существующий ресторан"),
            ApiResponse(responseCode = "404", description = "Ресторан с таким ID не найден")
        ]
    )
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: RestaurantUpdateRequest
    ): RestaurantResponse =
        RestaurantResponse.fromDomain(service.update(id, req.toDomain(id)))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить ресторан по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Ресторан успешно удален"),
            ApiResponse(responseCode = "404", description = "Ресторан с таким ID не найден")
        ]
    )
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{restaurantId}/dishes")
    @Operation(summary = "Получить список всех блюд ресторана по ID ресторана")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешный ответ с списком блюд ресторана"),
            ApiResponse(responseCode = "404", description = "Ресторан с таким ID не найден")
        ]
    )
    fun listDishes(@PathVariable restaurantId: Long): List<DishResponse> =
        service.getWithDishes(restaurantId).dishes.map(DishResponse::fromDomain)

    @PostMapping("/{restaurantId}/dishes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Добавить новое блюдо в ресторан по ID ресторана. Если блюдо с таким именем уже существует в этом ресторане, будет возвращено существующее блюдо.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Блюдо успешно добавлено в ресторан"),
            ApiResponse(responseCode = "200", description = "Блюдо с таким именем уже существует в этом ресторане, возвращено существующее блюдо"),
            ApiResponse(responseCode = "404", description = "Ресторан с таким ID не найден")
        ]
    )
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
