package com.example.demo

import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.assertj.core.api.Assertions.assertThat

@SpringBootTest(classes = [DemoApplication::class])
class RedisConnectionTest(
    private val redisTemplate: StringRedisTemplate
) : DescribeSpec({
    describe("레디스 연결 테스트") {
        context("레디스 연결이 정상적으로 되었을 경우") {
            it("set값과 get값이 같다") {
                val ops: ValueOperations<String, String> = redisTemplate.opsForValue()

                val key = "testKey"
                val value = "testValue"

                ops.set(key, value)

                val fetchedValue: String? = ops.get(key)
                assertThat(fetchedValue).isEqualTo(value)
            }
        }
    }
})
