package com.github.kolesovv.diakontfood.di

import com.github.kolesovv.diakontfood.data.repository.OrderRepositoryImpl
import com.github.kolesovv.diakontfood.domain.repository.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindOrderRepository(orderRepositoryImpl: OrderRepositoryImpl): OrderRepository
}
