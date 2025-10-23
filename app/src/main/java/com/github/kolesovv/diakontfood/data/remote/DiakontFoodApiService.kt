package com.github.kolesovv.diakontfood.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DiakontFoodApiService {

    @GET("pejo4tnudohxk?sheet=dishes")
    suspend fun loadDishes(): List<DishDto>

    @POST("pejo4tnudohxk?sheet=orders")
    suspend fun sendOrder(@Body orderDto: OrderDto): Response<OrderDto>
}
