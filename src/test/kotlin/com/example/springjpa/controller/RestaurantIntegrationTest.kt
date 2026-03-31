package com.example.springjpa.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class RestaurantIntegrationTest {

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:17").apply {
            withDatabaseName("integration-tests-db")
            withUsername("test")
            withPassword("test")
        }
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `POST restaurant возвращает 201 и создаёт запись`() {
        mockMvc.perform(
            post("/api/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "New Place", "address": "ул. Тестовая, 1"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("New Place"))
            .andExpect(jsonPath("$.address").value("ул. Тестовая, 1"))
    }

}
