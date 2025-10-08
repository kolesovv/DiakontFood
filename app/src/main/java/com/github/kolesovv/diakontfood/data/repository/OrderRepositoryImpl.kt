package com.github.kolesovv.diakontfood.data.repository

import com.github.kolesovv.diakontfood.domain.entity.Dish
import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor() : OrderRepository {

    private val orders = MutableStateFlow<List<Order>>(emptyList())
    private val dishes = MutableStateFlow<List<Dish>>(emptyList())

    init {
        dishes.value = listOf(
            Dish(0, "Обед 1", 100),
            Dish(1, "Обед 2", 200),
            Dish(2, "Обед 3", 300)
        )
    }

    override suspend fun registerOrder(order: Order) {
        orders.update {
            it + order
        }
    }

    override fun getOrders(): Flow<List<Order>> {
        return orders
    }

    override fun getDishes(): Flow<List<Dish>> {
        return dishes
    }
}