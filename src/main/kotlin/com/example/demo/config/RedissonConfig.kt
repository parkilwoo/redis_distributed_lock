package com.example.demo.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig {
    @Value("\${spring.data.redis.host}")
    private lateinit var redisHost: String
    @Value("\${spring.data.redis.port}")
    private var redisPort: Int = 6309

    private val REDISSON_HOST_PREFIX: String = "redis://"

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().setAddress("$REDISSON_HOST_PREFIX$redisHost:$redisPort")
        return Redisson.create(config)
    }
}