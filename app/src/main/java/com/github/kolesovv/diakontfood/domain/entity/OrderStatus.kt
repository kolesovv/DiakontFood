package com.github.kolesovv.diakontfood.domain.entity

data class OrderStatus(
    val code: Int,
    val message: String,
    val cardNumber: String
)
