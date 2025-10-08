package com.github.kolesovv.diakontfood.domain.entity

data class Order(
    val id: Int = 0,
    val dishId: Int,
    val rfid: String
)
