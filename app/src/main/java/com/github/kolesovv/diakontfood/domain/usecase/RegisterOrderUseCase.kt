package com.github.kolesovv.diakontfood.domain.usecase

import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.repository.OrderRepository
import javax.inject.Inject

class RegisterOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(dishId: Int, rfid: String) {
        val order = Order(dishId = dishId, rfid = rfid)
        orderRepository.registerOrder(order)
    }
}
