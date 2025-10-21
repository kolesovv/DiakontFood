package com.github.kolesovv.diakontfood.di

import android.content.Context
import androidx.room.Room
import com.github.kolesovv.diakontfood.data.local.DiakontFoodDao
import com.github.kolesovv.diakontfood.data.local.DiakontFoodDatabase
import com.github.kolesovv.diakontfood.data.remote.DiakontFoodApiService
import com.github.kolesovv.diakontfood.data.repository.DiakontFoodRepositoryImpl
import com.github.kolesovv.diakontfood.domain.repository.DiakontFoodRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindOrderRepository(orderRepositoryImpl: DiakontFoodRepositoryImpl): DiakontFoodRepository

    companion object {

        @Singleton
        @Provides
        fun provideJson(): Json {

            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Singleton
        @Provides
        fun provideConverter(json: Json): Converter.Factory {

            return json.asConverterFactory("application/json".toMediaType())
        }

        @Singleton
        @Provides
        fun provideRetrofit(converter: Converter.Factory): Retrofit {

            return Retrofit.Builder()
                .baseUrl("https://sheetdb.io/api/v1/")
                .addConverterFactory(converter)
                .build()
        }

        @Singleton
        @Provides
        fun provideApiService(retrofit: Retrofit): DiakontFoodApiService {

            return retrofit.create()
        }

        @Singleton
        @Provides
        fun provideNewsDatabase(@ApplicationContext context: Context): DiakontFoodDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = DiakontFoodDatabase::class.java,
                name = "diakont_food.db"
            ).fallbackToDestructiveMigration(true).build()
        }

        @Singleton
        @Provides
        fun providesDiakontFoodDao(database: DiakontFoodDatabase): DiakontFoodDao {
            return database.DiakontFoodDao()
        }
    }
}
