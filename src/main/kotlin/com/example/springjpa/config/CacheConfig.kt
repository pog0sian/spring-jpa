package com.example.springjpa.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

@Configuration
class CacheConfig {

    @Bean
    fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
        val typeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("com.example.springjpa")
            .allowIfSubType("java.lang")
            .allowIfSubType("java.math")
            .allowIfSubType("java.util")
            .build()
        val objectMapper = ObjectMapper()
            .registerKotlinModule()
            .activateDefaultTyping(
                typeValidator,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
            )
        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)
        val serializationPair = RedisSerializationContext.SerializationPair
            .fromSerializer<Any>(serializer)

        fun config(ttl: Duration) = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl)
            .serializeValuesWith(serializationPair)
            .disableCachingNullValues()

        return RedisCacheManagerBuilderCustomizer { builder ->
            builder
                .withCacheConfiguration("restaurants", config(Duration.ofMinutes(10)))
                .withCacheConfiguration("dishes", config(Duration.ofMinutes(10)))
                .cacheDefaults(config(Duration.ofMinutes(5)))
        }
    }
}
