package com.github.kolesovv.diakontfood.domain.usecase

import com.github.kolesovv.diakontfood.domain.entity.Dish
import com.github.kolesovv.diakontfood.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDishesUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    operator fun invoke(): Flow<List<Dish>> {
        return orderRepository.getDishes()
    }
}
