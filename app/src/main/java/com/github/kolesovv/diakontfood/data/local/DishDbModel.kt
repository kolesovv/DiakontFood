package com.github.kolesovv.diakontfood.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class DishDbModel(
    @PrimaryKey
    val dishId: Int,
    val name: String,
    val price: Int
)
