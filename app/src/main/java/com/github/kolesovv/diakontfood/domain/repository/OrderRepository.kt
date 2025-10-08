package com.github.kolesovv.diakontfood.domain.repository

import com.github.kolesovv.diakontfood.domain.entity.Dish
import com.github.kolesovv.diakontfood.domain.entity.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun registerOrder(order: Order)

    fun getOrders(): Flow<List<Order>>

    fun getDishes(): Flow<List<Dish>>
}
