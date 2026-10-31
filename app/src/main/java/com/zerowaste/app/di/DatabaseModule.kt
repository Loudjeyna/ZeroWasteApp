package com.zerowaste.app.di

import android.content.Context
import com.zerowaste.app.data.db.FoodItemDao
import com.zerowaste.app.data.db.ZeroWasteDatabase
import com.zerowaste.app.data.repository.FoodRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DatabaseModule — وحدة Hilt لإدارة حقن قاعدة البيانات
 *
 * توفر:
 *   - ZeroWasteDatabase (singleton)
 *   - FoodItemDao
 *   - FoodRepository (singleton)
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideZeroWasteDatabase(
        @ApplicationContext context: Context
    ): ZeroWasteDatabase {
        return ZeroWasteDatabase.getInstance(context)
    }

    @Provides
    fun provideFoodItemDao(
        database: ZeroWasteDatabase
    ): FoodItemDao {
        return database.foodItemDao()
    }

    @Provides
    @Singleton
    fun provideFoodRepository(
        foodItemDao: FoodItemDao
    ): FoodRepository {
        return FoodRepository(foodItemDao)
    }
}
