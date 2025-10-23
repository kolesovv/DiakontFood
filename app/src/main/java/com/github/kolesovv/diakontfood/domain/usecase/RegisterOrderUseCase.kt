package com.github.kolesovv.diakontfood.domain.usecase

import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.entity.PayMethod
import com.github.kolesovv.diakontfood.domain.repository.DiakontFoodRepository
import javax.inject.Inject

class RegisterOrderUseCase @Inject constructor(
    private val diakontFoodRepository: DiakontFoodRepository
) {

    suspend operator fun invoke(dishIds: List<Int>, payMethod: PayMethod, cardNumber: String) {

        dishIds.forEach { dishId ->
            val order = Order(dishId = dishId, cardNumber = cardNumber)
            diakontFoodRepository.saveOrderToRemoteDb(order)
        }
    }
}
