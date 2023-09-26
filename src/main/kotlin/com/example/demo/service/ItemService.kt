package com.example.demo.service

import com.example.demo.config.RedisLock
import com.example.demo.domain.entity.Item
import com.example.demo.repository.ItemRepository
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val redisLock: RedisLock
) {
    fun purchaseItemWithNoLock(name: String) {
        val item: Item = itemRepository.findItemByName(name)

        if (!item.checkRemainingStock()) {
            print("$name Stock Zero. Fail Purchase")
            return
        }

        item.stock -= 1
        itemRepository.save(item)
    }

    fun purchaseItemWithSpinLock(name: String) {
        // Redis 부하를 막기 위해 대기
        while(redisLock.lock(name) == false) {
            Thread.sleep(300)
        }

        purchaseItemWithNoLock(name)
    }

    fun printItemStock(name: String) {
        val item: Item = itemRepository.findItemByName(name)
        println("$name 남은 갯수: ${item.stock}")
    }
}