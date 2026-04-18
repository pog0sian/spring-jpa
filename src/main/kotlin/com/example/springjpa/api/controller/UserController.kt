package com.example.springjpa.api.controller

import com.example.springjpa.api.dto.user.UserCreateRequest
import com.example.springjpa.api.dto.user.UserResponse
import com.example.springjpa.api.dto.user.UserUpdateRequest
import com.example.springjpa.application.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "Users", description = "Управление пользователями")
class UserController(
    private val service: UserService
) {
    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешный ответ с списком пользователей"),
        ]
    )
    fun list(): List<UserResponse> =
        service.list().map(UserResponse::fromDomain)

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешный ответ с пользователем"),
            ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден")
        ]
    )
    fun get(@PathVariable id: Long): UserResponse =
        UserResponse.fromDomain(service.get(id))

    @PostMapping
    @Operation(summary = "Создать нового пользователя. Если пользователь с таким email уже существует, будет возвращен существующий пользователь.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            ApiResponse(responseCode = "200", description = "Пользователь с таким email уже существует, возвращен существующий пользователь")
        ]
    )
    fun create(
        @Valid @RequestBody req: UserCreateRequest
    ): ResponseEntity<UserResponse> {
        val (user, created) = service.create(req.toDomain())
        return if (created) {
            ResponseEntity.status(201).body(UserResponse.fromDomain(user))
        } else {
            ResponseEntity.ok(UserResponse.fromDomain(user))
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить существующего пользователя по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
            ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден")
        ]
    )
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: UserUpdateRequest
    ): UserResponse =
        UserResponse.fromDomain(service.update(id, req.toDomain(id)))

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден")
        ]
    )
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}