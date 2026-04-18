package com.example.springjpa.application.service

import com.example.springjpa.api.dto.auth.AuthResponse
import com.example.springjpa.api.dto.auth.LoginRequest
import com.example.springjpa.api.dto.auth.RegisterRequest
import com.example.springjpa.application.exception.AlreadyExistsException
import com.example.springjpa.infrastructure.persistence.entity.UserEntity
import com.example.springjpa.infrastructure.persistence.jpa.RoleJpaRepository
import com.example.springjpa.infrastructure.persistence.jpa.UserJpaRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserJpaRepository,
    private val roleRepository: RoleJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw AlreadyExistsException("Пользователь с таким email уже существует")
        }

        val userRole = roleRepository.findByName("ROLE_USER")
            ?: throw IllegalStateException("Required role ROLE_USER not found")

        val user = UserEntity(
            email = request.email,
            firstName = request.name,
            lastName = "",
            password = passwordEncoder.encode(request.password)!!,
            roles = setOf(userRole)
        )

        val savedUser = userRepository.save(user)
        val token = jwtService.generateToken(savedUser)

        return AuthResponse(
            token = token,
            email = savedUser.email,
            roles = savedUser.roles.map { it.name }
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
        } catch (_: BadCredentialsException) {
            throw BadCredentialsException("Неверный email или пароль")
        }

        val user = userRepository.findByEmail(request.email)
            ?: throw BadCredentialsException("Неверный email или пароль")

        return AuthResponse(
            token = jwtService.generateToken(user),
            email = user.email,
            roles = user.roles.map { it.name }
        )
    }

}
