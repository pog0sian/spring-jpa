package com.example.springjpa.api.controller

import com.example.springjpa.api.dto.order.OrderCreateRequest
import com.example.springjpa.api.dto.order.OrderResponse
import com.example.springjpa.api.dto.order.OrderStatusUpdateRequest
import com.example.springjpa.application.service.OrderService
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import jakarta.validation.Valid
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
class OrderController(
    private val service: OrderService
) {

    @GetMapping
    fun list(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: OrderStatus?,
    ): List<OrderResponse> =
        service.list(userId = userId, status = status).map(OrderResponse::fromDomain)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): OrderResponse =
        OrderResponse.fromDomain(service.get(id))

    @PostMapping
    fun create(@Valid @RequestBody req: OrderCreateRequest): ResponseEntity<OrderResponse> {
        val created = service.create(userId = req.userId, dishIds = req.dishIds)
        return ResponseEntity.status(201).body(OrderResponse.fromDomain(created))
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody req: OrderStatusUpdateRequest
    ): OrderResponse =
        OrderResponse.fromDomain(service.updateStatus(id, req.status))
}