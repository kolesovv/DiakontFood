package com.github.kolesovv.diakontfood.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = DishDbModel::class,
            parentColumns = ["dishId"],
            childColumns = ["dishId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dishId")]
)
data class OrderDbModel(
    @PrimaryKey(autoGenerate = true)
    val orderId: Int,
    val dishId: Int,
    val cardNumber: String
)
