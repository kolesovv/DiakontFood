package com.github.kolesovv.diakontfood.domain.usecase

import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.entity.OrderStatus
import com.github.kolesovv.diakontfood.domain.entity.PayMethod
import com.github.kolesovv.diakontfood.domain.repository.DiakontFoodRepository
import com.github.kolesovv.diakontfood.domain.repository.OrderResponse
import javax.inject.Inject

class RegisterOrderUseCase @Inject constructor(
    private val diakontFoodRepository: DiakontFoodRepository
) {

    suspend operator fun invoke(
        dishIds: List<Int>,
        payMethod: PayMethod,
        cardNumber: String
    ): List<OrderResponse<OrderStatus>> {

        val orderResponses = mutableListOf<OrderResponse<OrderStatus>>()

        dishIds.forEach { dishId ->
            val order = Order(dishId = dishId, cardNumber = cardNumber)
            val response = diakontFoodRepository.saveOrderToRemoteDb(order)
            orderResponses.add(response)
        }
        return orderResponses
    }
}
