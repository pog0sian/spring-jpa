package com.example.springjpa

import com.example.springjpa.infrastructure.persistence.entity.DishEntity
import com.example.springjpa.infrastructure.persistence.entity.RestaurantEntity
import com.example.springjpa.infrastructure.persistence.jpa.DishJpaRepository
import com.example.springjpa.infrastructure.persistence.jpa.RestaurantJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.cache.CacheManager
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test", "db")
class RestaurantCacheTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Autowired
    private lateinit var restaurantRepository: RestaurantJpaRepository

    @Autowired
    private lateinit var dishRepository: DishJpaRepository

    @BeforeEach
    fun setUp() {
        cacheManager.cacheNames.forEach { cacheManager.getCache(it)?.clear() }
        dishRepository.deleteAll()
        restaurantRepository.deleteAll()
    }

    @Test
    fun `repeated restaurants request stores result in cache`() {
        restaurantRepository.save(RestaurantEntity(name = "Cache Place", address = "Main street"))

        mockMvc.perform(get("/api/v1/restaurants"))
            .andExpect(status().isOk)
        mockMvc.perform(get("/api/v1/restaurants"))
            .andExpect(status().isOk)

        assertThat(cacheManager.getCache("restaurants")?.get("all")).isNotNull
    }

    @Test
    fun `creating restaurant evicts restaurants cache`() {
        mockMvc.perform(get("/api/v1/restaurants"))
            .andExpect(status().isOk)
        assertThat(cacheManager.getCache("restaurants")?.get("all")).isNotNull

        mockMvc.perform(
            post("/api/v1/restaurants")
                .with(user("admin@test.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"New Cache Place","address":"Test street"}""")
        ).andExpect(status().isCreated)

        assertThat(cacheManager.getCache("restaurants")?.get("all")).isNull()
    }

    @Test
    fun `adding dish evicts restaurant menu cache`() {
        val restaurant = restaurantRepository.save(
            RestaurantEntity(name = "Menu Place", address = "Food street")
        )
        dishRepository.save(
            DishEntity(
                name = "Pizza",
                description = "Cheese",
                price = BigDecimal("12.50"),
                restaurant = restaurant
            )
        )

        mockMvc.perform(get("/api/v1/restaurants/${restaurant.id}/dishes"))
            .andExpect(status().isOk)
        assertThat(cacheManager.getCache("dishes")?.get(restaurant.id)).isNotNull

        mockMvc.perform(
            post("/api/v1/restaurants/${restaurant.id}/dishes")
                .with(user("admin@test.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "Pasta",
                      "description": "Tomato",
                      "price": 10.00,
                      "isAvailable": true
                    }
                    """.trimIndent()
                )
        ).andExpect(status().isCreated)

        assertThat(cacheManager.getCache("dishes")?.get(restaurant.id)).isNull()
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
