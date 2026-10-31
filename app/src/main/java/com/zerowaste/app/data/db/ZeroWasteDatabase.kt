package com.zerowaste.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zerowaste.app.data.model.FoodItem

/**
 * ZeroWasteDatabase — Room Database
 *
 * - version = 1 (النسخة الأولى)
 * - exportSchema = true (نُصدّر schema للتوثيق)
 * - fallbackToDestructiveMigration: نحذف البيانات عند ترقية بسيطة
 *   (في الإنتاج، استخدم Migration حقيقي)
 */
@Database(
    entities = [FoodItem::class],
    version = 2,
    exportSchema = true
)
abstract class ZeroWasteDatabase : RoomDatabase() {

    abstract fun foodItemDao(): FoodItemDao

    companion object {
        @Volatile
        private var INSTANCE: ZeroWasteDatabase? = null

        fun getInstance(context: Context): ZeroWasteDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ZeroWasteDatabase::class.java,
                    "zerowaste.db"
                )
                    // في الإنتاج: استخدم Migration حقيقي
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
