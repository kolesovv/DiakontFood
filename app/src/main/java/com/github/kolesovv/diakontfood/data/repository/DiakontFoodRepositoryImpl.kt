package com.github.kolesovv.diakontfood.data.repository

import com.github.kolesovv.diakontfood.data.local.DiakontFoodDao
import com.github.kolesovv.diakontfood.data.local.DishDbModel
import com.github.kolesovv.diakontfood.data.mapper.toDbModel
import com.github.kolesovv.diakontfood.data.mapper.toDishDbModel
import com.github.kolesovv.diakontfood.data.mapper.toDto
import com.github.kolesovv.diakontfood.data.mapper.toEntity
import com.github.kolesovv.diakontfood.data.remote.DiakontFoodApiService
import com.github.kolesovv.diakontfood.domain.entity.Dish
import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.repository.DiakontFoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.CancellationException
import javax.inject.Inject

class DiakontFoodRepositoryImpl @Inject constructor(
    private val diakontFoodDao: DiakontFoodDao,
    private val diakontFoodApiService: DiakontFoodApiService
) : DiakontFoodRepository {

    override suspend fun saveOrderToLocalDb(order: Order) {
        diakontFoodDao.addOrder(order.toDbModel())
    }

    override suspend fun saveOrderToRemoteDb(order: Order) {
        val dish = diakontFoodDao.getDishById(order.dishId).first()
        val orderDto = order.toDto(dish.price)
        diakontFoodApiService.sendOrder(orderDto)
    }

    override fun getOrders(): Flow<List<Order>> {
        return diakontFoodDao.getOrders().map { orderDbModels ->
            orderDbModels.map { it.toEntity() }
        }
    }

    override fun getDishes(): Flow<List<Dish>> {
        return diakontFoodDao.getDishes().map { dishDbModels ->
            dishDbModels.map { it.toEntity() }
        }
    }

    override suspend fun updateDishes() {
        val dishes = loadDishes()
        if (dishes.isNotEmpty()) {
            diakontFoodDao.addDishes(dishes)
        }
    }

    private suspend fun loadDishes(): List<DishDbModel> {
        return try {
            diakontFoodApiService.loadDishes().map { it.toDishDbModel() }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            } else {
                throw RuntimeException("Не удалось загрузить меню", e)
            }
        }
    }
}