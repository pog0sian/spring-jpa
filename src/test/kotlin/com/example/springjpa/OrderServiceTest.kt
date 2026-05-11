package com.example.springjpa

import com.example.springjpa.application.service.NotificationService
import com.example.springjpa.application.service.OrderService
import com.example.springjpa.domain.model.Dish
import com.example.springjpa.domain.model.Order
import com.example.springjpa.domain.model.User
import com.example.springjpa.domain.port.DishRepositoryPort
import com.example.springjpa.domain.port.OrderRepositoryPort
import com.example.springjpa.domain.port.UserRepositoryPort
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import com.example.springjpa.infrastructure.persistence.jpa.UserJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDateTime

class OrderServiceTest {

    @Test
    fun `cancelStuckPreparingOrders cancels stuck orders and sends notifications`() {
        val createdAt = LocalDateTime.now().minusHours(2)
        val createdBefore = LocalDateTime.now().minusHours(1)

        val orderRepository = FakeOrderRepository(
            Order(
                id = 1,
                status = OrderStatus.PREPARING,
                createdAt = createdAt,
                userId = 10,
                dishes = emptyList()
            ),
            Order(
                id = 2,
                status = OrderStatus.PREPARING,
                createdAt = createdAt,
                userId = 10,
                dishes = emptyList()
            )
        )
        val userRepository = FakeUserRepository(
            User(
                id = 10,
                email = "owner@test.com",
                firstName = "Order",
                lastName = "Owner",
                isActive = true
            )
        )
        val notificationService = mock(NotificationService::class.java)

        val service = OrderService(
            repo = orderRepository,
            userRepo = userRepository,
            dishRepo = FakeDishRepository(),
            userJpaRepository = mock(UserJpaRepository::class.java),
            notificationService = notificationService
        )

        val cancelledCount = service.cancelStuckPreparingOrders(createdBefore)

        assertThat(cancelledCount).isEqualTo(2)
        assertThat(orderRepository.findByIdWithUserAndDishes(1)?.status).isEqualTo(OrderStatus.CANCELLED)
        assertThat(orderRepository.findByIdWithUserAndDishes(2)?.status).isEqualTo(OrderStatus.CANCELLED)

        verify(notificationService).sendOrderStatusUpdate("owner@test.com", 1, "CANCELLED")
        verify(notificationService).sendOrderStatusUpdate("owner@test.com", 2, "CANCELLED")
    }

    private class FakeOrderRepository(
        vararg orders: Order
    ) : OrderRepositoryPort {

        private val ordersById = orders.associateBy { it.id }.toMutableMap()

        override fun findAll(userId: Long?, status: OrderStatus?): List<Order> =
            ordersById.values.filter {
                (userId == null || it.userId == userId) &&
                        (status == null || it.status == status)
            }

        override fun findByIdWithUserAndDishes(id: Long): Order? =
            ordersById[id]

        override fun findStuckPreparingOrders(createdBefore: LocalDateTime): List<Order> =
            ordersById.values.filter {
                it.status == OrderStatus.PREPARING && it.createdAt.isBefore(createdBefore)
            }

        override fun create(userId: Long, dishIds: List<Long>): Order =
            error("Not needed in this test")

        override fun updateStatus(id: Long, status: OrderStatus): Order {
            val existing = ordersById[id] ?: error("Order with id=$id not found")
            val updated = existing.copy(status = status)
            ordersById[id] = updated
            return updated
        }

        override fun delete(id: Long) {
            ordersById.remove(id)
        }
    }

    private class FakeUserRepository(
        private val user: User
    ) : UserRepositoryPort {

        override fun findAll(): List<User> = listOf(user)

        override fun findById(id: Long): User? =
            user.takeIf { it.id == id }

        override fun findByEmail(email: String): User? =
            user.takeIf { it.email == email }

        override fun existsByEmail(email: String): Boolean =
            user.email == email

        override fun save(user: User): User = user

        override fun delete(id: Long) = Unit
    }

    private class FakeDishRepository : DishRepositoryPort {
        override fun findAll(namePart: String?): List<Dish> = emptyList()
        override fun findById(id: Long): Dish? = null
        override fun findAllById(ids: List<Long>): List<Dish> = emptyList()
        override fun findByName(name: String): Dish? = null
        override fun create(restaurantId: Long, dish: Dish): Dish = dish
        override fun update(dish: Dish): Dish = dish
        override fun delete(id: Long) = Unit
    }
}
