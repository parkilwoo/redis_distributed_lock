package com.example.demo.service

import com.example.demo.config.RedisLock
import com.example.demo.domain.entity.Item
import com.example.demo.repository.ItemRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val redisLock: RedisLock,
    private val redissonClient: RedissonClient
) {
    private var failCount: Int = 0
    fun purchaseItem(name: String) {
        // 함수의 실행시간을 늘리기 위함
        Thread.sleep(20L)
        val item: Item = itemRepository.findItemByName(name)

        if (!item.checkRemainingStock()) {
            println("$name Stock Zero. Fail Purchase")
            failCount += 1
            return
        }

        item.stock -= 1
        itemRepository.save(item)
    }

    fun purchaseItemWithSpinLock(name: String) {
        while(redisLock.lock(name) == false) {}
        try {
            purchaseItem(name)
        }
        finally {
            redisLock.unLock(name)
        }
    }

    fun purchaseItemWithRedisson(name: String) {
        val lock: RLock = redissonClient.getLock(name)
        try {
            val available: Boolean = lock.tryLock(10, 2, TimeUnit.SECONDS)
            if (!available) {
                println("Lock 획득에 실패 하였습니다.")
                return
            }
            purchaseItem(name)
        }
        finally {
            try {
                lock.unlock()
            }
            catch(exception: IllegalMonitorStateException) {
                println("Redisson Lock Already Unlock $name")
            }
        }
    }

    fun getItemStock(name: String) : Int {
        val item: Item = itemRepository.findItemByName(name)
        return item.stock
    }

    fun getFailCount() : Int {
        return failCount
    }
}