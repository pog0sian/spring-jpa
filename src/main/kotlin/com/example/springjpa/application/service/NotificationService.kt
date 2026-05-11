package com.example.springjpa.application.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import kotlinx.coroutines.CancellationException

@Service
class NotificationService(
    private val mailSender: JavaMailSender,
    private val applicationScope: CoroutineScope
) {

    private val logger = KotlinLogging.logger {}

    fun sendOrderStatusUpdate(to: String, orderId: Long, status: String) {
        applicationScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    mailSender.send(
                        SimpleMailMessage().apply {
                            setTo(to)
                            subject = "Order #$orderId status updated"
                            text = "Your order #$orderId status changed to $status"
                        }
                    )
                }

                logger.info { "Order status notification sent to $to for orderId=$orderId" }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.error(e) { "Error while sending order notification to $to for orderId=$orderId" }
            }
        }
    }
}