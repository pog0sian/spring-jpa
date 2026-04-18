package com.example.springjpa.api.controller

import com.example.springjpa.api.dto.auth.AuthResponse
import com.example.springjpa.api.dto.auth.LoginRequest
import com.example.springjpa.api.dto.auth.RegisterRequest
import com.example.springjpa.application.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Validated
@Tag(name = "Auth", description = "Регистрация и аутентификация пользователей")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            ApiResponse(responseCode = "400", description = "Некорректные данные для регистрации")
        ]
    )
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request))
    }

    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя и получение JWT токена")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешная аутентификация, возвращен JWT токен"),
            ApiResponse(responseCode = "401", description = "Неверные учетные данные")
        ]
    )
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.login(request))
    }
}