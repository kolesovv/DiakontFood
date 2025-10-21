package com.github.kolesovv.diakontfood.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    @SerialName("confType")
    val confType: Int,
    @SerialName("cardNumber")
    val cardNumber: Int,
    @SerialName("cost")
    val price: Int,
    @SerialName("rvalue")
    val rvalue: Int,
    @SerialName("rcode")
    val rcode: Int,
    @SerialName("rmsg")
    val rmsg: String
)
