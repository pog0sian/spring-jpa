package com.example.springjpa.api.controller

import com.example.springjpa.api.dto.user.UserCreateRequest
import com.example.springjpa.api.dto.user.UserResponse
import com.example.springjpa.api.dto.user.UserUpdateRequest
import com.example.springjpa.application.service.UserService
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
class UserController(
    private val service: UserService
) {
    @GetMapping
    fun list(): List<UserResponse> =
        service.list().map(UserResponse::fromDomain)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): UserResponse =
        UserResponse.fromDomain(service.get(id))

    @PostMapping
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
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: UserUpdateRequest
    ): UserResponse =
        UserResponse.fromDomain(service.update(id, req.toDomain(id)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}