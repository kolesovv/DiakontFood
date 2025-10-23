package com.github.kolesovv.diakontfood.domain.repository

import com.github.kolesovv.diakontfood.domain.entity.Dish
import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.entity.OrderStatus
import kotlinx.coroutines.flow.Flow

interface DiakontFoodRepository {

    suspend fun saveOrderToLocalDb(order: Order)

    suspend fun saveOrderToRemoteDb(order: Order): OrderResponse<OrderStatus>

    suspend fun cleanupOldOrders()

    fun getOrders(): Flow<List<Order>>

    fun getDishes(): Flow<List<Dish>>

    suspend fun updateDishes()
}
