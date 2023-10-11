package com.example.demo.config

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration


@Component
class RedisLock(
    private val redisTemplate: StringRedisTemplate
) {

    fun lock(key: String): Boolean? {
        return redisTemplate
            .opsForValue()
            .setIfAbsent(key, "LOCK", Duration.ofMillis(5_000))
    }

    fun unLock(key: String): Boolean {
        return redisTemplate
            .delete(key)
    }
}