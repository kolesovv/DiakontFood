package com.github.kolesovv.diakontfood.data.mapper

import com.github.kolesovv.diakontfood.data.local.DishDbModel
import com.github.kolesovv.diakontfood.data.remote.DishDto
import com.github.kolesovv.diakontfood.domain.entity.Dish

fun DishDbModel.toEntity(): Dish {
    return Dish(
        dishId = dishId,
        name = name,
        price = price
    )
}

fun DishDto.toDishDbModel(): DishDbModel {
    return DishDbModel(
        dishId = id,
        name = name,
        price = cost
    )
}
