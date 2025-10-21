package com.github.kolesovv.diakontfood.domain.entity

data class Order(
    val orderId: Int = 0,
    val dishId: Int,
    val cardNumber: String
)
