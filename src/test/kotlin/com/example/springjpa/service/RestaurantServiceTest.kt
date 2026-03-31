package com.example.springjpa.service

import com.example.springjpa.application.service.RestaurantService
import com.example.springjpa.domain.port.RestaurantRepositoryPort
import com.example.springjpa.infrastructure.persistence.entity.RestaurantEntity
import com.example.springjpa.infrastructure.persistence.jpa.RestaurantJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class RestaurantServiceTest {

    @Mock
    lateinit var repo: RestaurantRepositoryPort

    @InjectMocks
    lateinit var service: RestaurantService

    @Test
    fun `getById возвращает ресторан, если он существует`() {

    }
}