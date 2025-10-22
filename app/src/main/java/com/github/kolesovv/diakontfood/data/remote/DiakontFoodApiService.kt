package com.github.kolesovv.diakontfood.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DiakontFoodApiService {

    @GET("pejo4tnudohxk?sheet=dishes")
    suspend fun loadDishes(): List<DishDto>

    @POST("pejo4tnudohxk?sheet=orders")
    suspend fun sendOrder(@Body orderDto: OrderDto)

    companion object {
        const val OK = "OK"
        const val CARD_BLOCKED = "CARD_BLOCKED"
        const val CARD_NOT_FOUND = "CARD_NOT_FOUND"
        const val USER_BLOCKED = "USER_BLOCKED"
    }
}
