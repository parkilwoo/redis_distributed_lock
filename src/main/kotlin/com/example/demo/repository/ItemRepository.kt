package com.example.demo.repository

import com.example.demo.domain.entity.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository: JpaRepository<Item, Long> {
    fun findItemByName(name: String): Item
}