package com.github.kolesovv.diakontfood.domain.usecase

import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.repository.DiakontFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val diakontFoodRepository: DiakontFoodRepository
) {

    operator fun invoke(): Flow<List<Order>> {
        return diakontFoodRepository.getOrders()
    }
}
