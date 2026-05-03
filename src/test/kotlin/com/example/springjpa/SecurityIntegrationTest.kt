package com.example.springjpa

import com.example.springjpa.infrastructure.persistence.entity.DishEntity
import com.example.springjpa.infrastructure.persistence.entity.OrderEntity
import com.example.springjpa.infrastructure.persistence.entity.OrderStatus
import com.example.springjpa.infrastructure.persistence.entity.RestaurantEntity
import com.example.springjpa.infrastructure.persistence.entity.UserEntity
import com.example.springjpa.infrastructure.persistence.jpa.DishJpaRepository
import com.example.springjpa.infrastructure.persistence.jpa.OrderJpaRepository
import com.example.springjpa.infrastructure.persistence.jpa.RestaurantJpaRepository
import com.example.springjpa.infrastructure.persistence.jpa.RoleJpaRepository
import com.example.springjpa.infrastructure.persistence.jpa.UserJpaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.cache.CacheManager
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test", "db")
class SecurityIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserJpaRepository

    @Autowired
    private lateinit var roleRepository: RoleJpaRepository

    @Autowired
    private lateinit var restaurantRepository: RestaurantJpaRepository

    @Autowired
    private lateinit var dishRepository: DishJpaRepository

    @Autowired
    private lateinit var orderRepository: OrderJpaRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var cacheManager: CacheManager

    @BeforeEach
    fun cleanDatabase() {
        cacheManager.cacheNames.forEach { cacheManager.getCache(it)?.clear() }
        orderRepository.deleteAll()
        dishRepository.deleteAll()
        restaurantRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `protected endpoint without token returns 401`() {
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `public restaurants endpoint without token returns 200`() {
        mockMvc.perform(get("/api/v1/restaurants"))
            .andExpect(status().isOk)
    }

    @Test
    fun `user cannot access admin endpoint`() {
        mockMvc.perform(
            post("/api/v1/restaurants")
                .with(user("user@test.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Admin Place","address":"Main street"}""")
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `admin can access admin endpoint`() {
        mockMvc.perform(
            post("/api/v1/restaurants")
                .with(user("admin@test.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Admin Place","address":"Main street"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Admin Place"))
    }

    @Test
    fun `login with wrong password returns 401`() {
        val roleUser = roleRepository.findByName("ROLE_USER")!!
        userRepository.save(
            UserEntity(
                email = "login@test.com",
                firstName = "Login",
                lastName = "User",
                password = passwordEncoder.encode("correct-password")!!,
                roles = setOf(roleUser)
            )
        )

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"login@test.com","password":"wrong-password"}""")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("Неверный email или пароль"))
    }

    @Test
    fun `register with existing email returns 409`() {
        userRepository.save(
            UserEntity(
                email = "existing@test.com",
                firstName = "Existing",
                lastName = "User",
                password = passwordEncoder.encode("password123")!!
            )
        )

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"existing@test.com","password":"password123","name":"Test User"}""")
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
    }

    @Test
    fun `order owner can view own order`() {
        val order = createOrderFor("owner@test.com")

        mockMvc.perform(
            get("/api/v1/orders/${order.id}")
                .with(user("owner@test.com").roles("USER"))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(order.id))
    }

    @Test
    fun `non owner cannot view other order`() {
        val order = createOrderFor("owner@test.com")
        userRepository.save(
            UserEntity(
                email = "other@test.com",
                firstName = "Other",
                lastName = "User",
                password = passwordEncoder.encode("password123")!!
            )
        )

        mockMvc.perform(
            get("/api/v1/orders/${order.id}")
                .with(user("other@test.com").roles("USER"))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `admin can view any order`() {
        val order = createOrderFor("owner@test.com")

        mockMvc.perform(
            get("/api/v1/orders/${order.id}")
                .with(user("admin@test.com").roles("ADMIN"))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(order.id))
    }

    private fun createOrderFor(email: String): OrderEntity {
        val user = userRepository.save(
            UserEntity(
                email = email,
                firstName = "Order",
                lastName = "Owner",
                password = passwordEncoder.encode("password123")!!
            )
        )
        val restaurant = restaurantRepository.save(
            RestaurantEntity(
                name = "Test Restaurant",
                address = "Test address"
            )
        )
        val dish = dishRepository.save(
            DishEntity(
                name = "Pizza",
                description = "Test dish",
                price = java.math.BigDecimal("12.50"),
                restaurant = restaurant
            )
        )

        return orderRepository.save(
            OrderEntity(
                status = OrderStatus.PENDING,
                user = user,
                dishes = mutableListOf(dish)
            )
        )
    }

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")

        @Container
        @JvmStatic
        val redis: GenericContainer<*> = GenericContainer("redis:7-alpine")
            .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.firstMappedPort }
        }
    }
}
