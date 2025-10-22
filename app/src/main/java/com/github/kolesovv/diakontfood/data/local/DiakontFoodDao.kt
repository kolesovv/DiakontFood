package com.github.kolesovv.diakontfood.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DiakontFoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrder(orderDbModel: OrderDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDishes(dishDbModel: List<DishDbModel>): List<Long>

    @Query("SELECT * FROM orders")
    fun getOrders(): Flow<List<OrderDbModel>>

    @Query("SELECT * FROM dishes")
    fun getDishes(): Flow<List<DishDbModel>>

    @Query("SELECT * FROM dishes WHERE dishId =:dishId")
    fun getDishById(dishId: Int): Flow<DishDbModel>

    @Delete
    suspend fun deleteOrder(orderDbModel: OrderDbModel)

    @Transaction
    @Delete
    suspend fun deleteDish(dishDbModel: DishDbModel)
}
