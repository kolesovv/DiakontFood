package com.github.kolesovv.diakontfood.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DishDbModel::class, OrderDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class DiakontFoodDatabase : RoomDatabase()  {

    abstract fun DiakontFoodDao(): DiakontFoodDao
}
