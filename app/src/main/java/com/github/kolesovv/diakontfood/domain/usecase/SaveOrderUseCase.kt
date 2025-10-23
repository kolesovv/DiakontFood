package com.github.kolesovv.diakontfood.domain.usecase

import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.repository.DiakontFoodRepository
import javax.inject.Inject

class SaveOrderUseCase @Inject constructor(
    private val diakontFoodRepository: DiakontFoodRepository
) {

    suspend operator fun invoke(
        dishId: Int,
        cardNumber: String
    ) {
        val order = Order(dishId = dishId, cardNumber = cardNumber)
        diakontFoodRepository.saveOrderToLocalDb(order)
    }
}
