package com.example.springjpa.application.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderScheduler(
    private val orderService: OrderService,
    @Value("\${app.scheduler.stuck-order-threshold-hours}")
    private val stuckOrderThresholdHours: Long
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedDelayString = "\${app.scheduler.stuck-order-interval-ms}")
    fun cancelStuckOrders() {
        val createdBefore = LocalDateTime.now().minusHours(stuckOrderThresholdHours)

        logger.info { "Checking stuck PREPARING orders created before $createdBefore" }

        val cancelledCount = orderService.cancelStuckPreparingOrders(createdBefore)

        logger.info { "Cancelled $cancelledCount stuck PREPARING orders" }
    }
}