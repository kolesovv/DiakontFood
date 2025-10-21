package com.github.kolesovv.diakontfood.domain.usecase

import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.repository.DiakontFoodRepository
import javax.inject.Inject

class RegisterOrderUseCase @Inject constructor(
    private val diakontFoodRepository: DiakontFoodRepository
) {

    suspend operator fun invoke(dishId: Int, rfid: String) {
        val order = Order(dishId = dishId, cardNumber = rfid)
        diakontFoodRepository.saveOrderToLocalDb(order)
    }
}
