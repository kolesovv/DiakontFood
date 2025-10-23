@file:JvmName("OrderMapperKt")

package com.github.kolesovv.diakontfood.data.mapper

import com.github.kolesovv.diakontfood.data.local.OrderDbModel
import com.github.kolesovv.diakontfood.data.mapper.OrderMapperConstants.DEFAULT_CONF_TYPE
import com.github.kolesovv.diakontfood.data.mapper.OrderMapperConstants.DEFAULT_R_CODE
import com.github.kolesovv.diakontfood.data.mapper.OrderMapperConstants.DEFAULT_R_MESSAGE
import com.github.kolesovv.diakontfood.data.mapper.OrderMapperConstants.DEFAULT_R_VALUE
import com.github.kolesovv.diakontfood.data.remote.OrderDto
import com.github.kolesovv.diakontfood.domain.entity.Order
import com.github.kolesovv.diakontfood.domain.entity.OrderStatus

fun Order.toDbModel(timestamp: Long): OrderDbModel {
    return OrderDbModel(
        orderId = orderId,
        dishId = dishId,
        cardNumber = cardNumber,
        timestamp = timestamp
    )
}

fun Order.toDto(price: Int): OrderDto {
    return OrderDto(
        confType = DEFAULT_CONF_TYPE,
        cardNumber = cardNumber.toInt(),
        price = price,
        rvalue = DEFAULT_R_VALUE,
        rcode = DEFAULT_R_CODE,
        rmsg = DEFAULT_R_MESSAGE
    )
}

fun OrderDbModel.toEntity(): Order {
    return Order(
        orderId = orderId,
        dishId = dishId,
        cardNumber = cardNumber
    )
}

fun OrderDto.toOrderStatus(): OrderStatus {
    return OrderStatus(
        code = rcode,
        message = rmsg,
        cardNumber = cardNumber.toString()
    )
}

object OrderMapperConstants {
    const val DEFAULT_CONF_TYPE = 1
    const val DEFAULT_R_VALUE = 0
    const val DEFAULT_R_CODE = 0
    const val DEFAULT_R_MESSAGE = ""
}
