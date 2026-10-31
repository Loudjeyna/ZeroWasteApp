package com.zerowaste.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Countertops
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.ui.graphics.vector.ImageVector
import com.zerowaste.app.R

/**
 * أماكن تخزين الطعام
 */
enum class StorageLocation(
    val labelResId: Int,
    val emoji: String,
    val icon: ImageVector,
    val colorArgb: Long,
) {
    FRIDGE(
        labelResId = R.string.location_fridge,
        emoji = "🧊",
        icon = Icons.Filled.Kitchen,
        colorArgb = 0xFF42A5F5,
    ),
    FREEZER(
        labelResId = R.string.location_freezer,
        emoji = "❄️",
        icon = Icons.Filled.AcUnit,
        colorArgb = 0xFF26C6DA,
    ),
    PANTRY(
        labelResId = R.string.location_pantry,
        emoji = "🚪",
        icon = Icons.Filled.Countertops,
        colorArgb = 0xFF8D6E63,
    ),
    COUNTER(
        labelResId = R.string.location_counter,
        emoji = "🍽️",
        icon = Icons.Filled.Countertops,
        colorArgb = 0xFFFFA726,
    );

    companion object {
        val default: StorageLocation = FRIDGE
    }
}

/**
 * وحدات الكمية (للاختيار بالأيقونات)
 */
enum class QuantityUnit(
    val labelResId: Int,
    val symbolResId: Int,
    val emoji: String,
) {
    PIECE(R.string.unit_piece, R.string.unit_piece_symbol, "🥄"),
    KILO(R.string.unit_kilo, R.string.unit_kilo_symbol, "⚖️"),
    GRAM(R.string.unit_gram, R.string.unit_gram_symbol, "⚖️"),
    LITER(R.string.unit_liter, R.string.unit_liter_symbol, "🧪"),
    MILLILITER(R.string.unit_milliliter, R.string.unit_milliliter_symbol, "🧪"),
    PACK(R.string.unit_pack, R.string.unit_pack_symbol, "📦"),
    BOTTLE(R.string.unit_bottle, R.string.unit_bottle_symbol, "🍶"),
    BOX(R.string.unit_box, R.string.unit_box_symbol, "📦");

    companion object {
        val default: QuantityUnit = PIECE
    }
}

/**
 * خيارات تاريخ انتهاء الصلاحية (سريعة)
 */
enum class ExpiryPreset(
    val labelResId: Int,
    val daysFromNow: Int,
    val emoji: String,
) {
    TODAY(R.string.expiry_today, 0, "⏰"),
    THREE_DAYS(R.string.expiry_3days, 3, "📅"),
    ONE_WEEK(R.string.expiry_1week, 7, "📆"),
    TWO_WEEKS(R.string.expiry_2weeks, 14, "🗓️"),
    ONE_MONTH(R.string.expiry_1month, 30, "📅"),
    THREE_MONTHS(R.string.expiry_3months, 90, "🗓️"),
    SIX_MONTHS(R.string.expiry_6months, 180, "📆"),
    ONE_YEAR(R.string.expiry_1year, 365, "🎯");

    companion object {
        val default: ExpiryPreset = ONE_WEEK
    }
}
