package com.github.kolesovv.diakontfood.data.remote

import retrofit2.http.GET

interface DiakontFoodApiService {

    @GET("pejo4tnudohxk?sheet=dishes")
    suspend fun loadDishes(): List<DishDto>
}
