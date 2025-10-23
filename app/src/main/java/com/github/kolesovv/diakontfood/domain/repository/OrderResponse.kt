package com.github.kolesovv.diakontfood.domain.repository

sealed class OrderResponse<out T> {

    data class Success<out T>(val data: T) : OrderResponse<T>()

    data class Error(
        val message: String,
        val code: Int? = null
    ) : OrderResponse<Nothing>()
}