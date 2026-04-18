package com.example.springjpa.api.controller

import com.example.springjpa.api.dto.order.OrderCreateRequest
import com.example.springjpa.api.dto.order.OrderResponse
import com.example.springjpa.api.dto.order.OrderStatusUpdateRequest
import com.example.springjpa.application.service.OrderService
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
@Validated
@Tag(name = "Orders", description = "Управление заказами")
class OrderController(
    private val service: OrderService
) {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список заказов. Администраторы могут фильтровать заказы по ID пользователя и статусу.")
    fun list(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: OrderStatus?,
    ): List<OrderResponse> =
        service.list(userId = userId, status = status).map(OrderResponse::fromDomain)

    @GetMapping("/{id}")
    @PreAuthorize("@orderAccessService.canViewOrder(authentication, #id)")
    @Operation(summary = "Получить заказ по ID. Администраторы могут получить любой заказ, пользователи - только свои заказы.")
    fun get(@PathVariable id: Long): OrderResponse =
        OrderResponse.fromDomain(service.get(id))

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Создать новый заказ. Пользователи могут создавать заказы только для себя, администраторы - для любого пользователя.")
    fun create(
        @Valid @RequestBody req: OrderCreateRequest,
        authentication: Authentication
    ): ResponseEntity<OrderResponse> {
        val principal = authentication.name
        val created = service.createForUserEmail(userEmail = principal, dishIds = req.dishIds)
        return ResponseEntity.status(201).body(OrderResponse.fromDomain(created))
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить статус заказа. Только администраторы могут обновлять статус заказа.")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody req: OrderStatusUpdateRequest
    ): OrderResponse =
        OrderResponse.fromDomain(service.updateStatus(id, req.status))
}
