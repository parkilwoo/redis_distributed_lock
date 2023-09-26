package com.example.demo.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Item(
    @Id @GeneratedValue val id: Long? = null,
    @Column(unique = true) val name: String = "",
    var stock: Int = 0
) {
    fun checkRemainingStock(): Boolean {
        return stock > 0
    }
}
