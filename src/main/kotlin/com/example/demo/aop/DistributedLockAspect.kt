package com.example.demo.aop

import com.example.demo.annotation.DistributedLock
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

@Aspect
@Component
class DistributedLockAspect(
    private val redissonClient: RedissonClient
) {

    @Around(value = "@annotation(distributedLock)")
    fun distributedLock(joinPoint: ProceedingJoinPoint, distributedLock: DistributedLock) : Any? {
        val lockName: String = joinPoint.args.joinToString(separator = "", limit = 1)
        val lock: RLock = redissonClient.getLock(lockName)
        return try {
            if (lock.tryLock(10, 2, TimeUnit.SECONDS)) {
                try {
                    joinPoint.proceed()
                } finally {
                    lock.unlock()
                }
            }
            else {
                println("Lock 획득에 실패 하였습니다.")
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Lock acquisition interrupted", e)
        }
    }
}