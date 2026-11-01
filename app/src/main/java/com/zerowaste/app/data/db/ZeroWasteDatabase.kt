package com.zerowaste.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zerowaste.app.data.model.FoodItem
import com.zerowaste.app.data.model.ShoppingItem

/**
 * ZeroWasteDatabase — Room Database
 *
 * - version = 3 (مُحدَّث بعد إضافة كيان ShoppingItem)
 *   ⚠️ عند كل تغيير لـ schema، ارفع رقم الإصدار وإلا سيُفشل Room الفتح
 * - exportSchema = true (نُصدّر schema للتوثيق)
 * - fallbackToDestructiveMigration: نحذف البيانات عند ترقية بسيطة
 *   (في الإنتاج، استخدم Migration حقيقي)
 *
 * الكيانات:
 *   - FoodItem: عناصر المخزون
 *   - ShoppingItem: عناصر قائمة التسوق
 */
@Database(
    entities = [FoodItem::class, ShoppingItem::class],
    version = 3,           // ⬅️ مرفوع من 2 إلى 3 (إضافة ShoppingItem)
    exportSchema = true
)
abstract class ZeroWasteDatabase : RoomDatabase() {

    abstract fun foodItemDao(): FoodItemDao
    abstract fun shoppingItemDao(): ShoppingItemDao  // ⬅️ جديد

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
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
