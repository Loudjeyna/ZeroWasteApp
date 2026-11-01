package com.zerowaste.app.di

import android.content.Context
import com.zerowaste.app.data.db.FoodItemDao
import com.zerowaste.app.data.db.ShoppingItemDao
import com.zerowaste.app.data.db.ZeroWasteDatabase
import com.zerowaste.app.data.repository.FoodRepository
import com.zerowaste.app.data.repository.SettingsRepository
import com.zerowaste.app.data.repository.ShoppingRepository
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
 *   - FoodItemDao + ShoppingItemDao
 *   - FoodRepository + ShoppingRepository (singletons)
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
    fun provideShoppingItemDao(
        database: ZeroWasteDatabase
    ): ShoppingItemDao {
        return database.shoppingItemDao()
    }

    @Provides
    @Singleton
    fun provideFoodRepository(
        foodItemDao: FoodItemDao
    ): FoodRepository {
        return FoodRepository(foodItemDao)
    }

    @Provides
    @Singleton
    fun provideShoppingRepository(
        shoppingItemDao: ShoppingItemDao
    ): ShoppingRepository {
        return ShoppingRepository(shoppingItemDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepository(context)
    }
}
