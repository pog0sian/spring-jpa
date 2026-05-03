package com.example.springjpa.security

import com.example.springjpa.application.service.OrderService
import com.example.springjpa.infrastructure.persistence.jpa.UserJpaRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component("orderAccessService")
class OrderAccessService(
    private val orderService: OrderService,
    private val userRepository: UserJpaRepository
) {

    fun canViewOrder(authentication: Authentication?, orderId: Long): Boolean {
        if (authentication == null || !authentication.isAuthenticated) {
            return false
        }

        if (authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            return true
        }

        val user = userRepository.findByEmail(authentication.name) ?: return false
        return orderService.isOwner(orderId, user.id)
    }
}
