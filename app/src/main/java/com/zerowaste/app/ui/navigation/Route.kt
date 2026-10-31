package com.zerowaste.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.zerowaste.app.R

/**
 * ZeroWaste — Routes (Type-Safe Navigation)
 *
 * 5 تبويبات رئيسية:
 *   1) Inventory  (المخزون)
 *   2) Recipes    (الوصفات)
 *   3) Add        (إضافة - في الوسط بـ FAB مخصص)
 *   4) Shopping   (التسوق)
 *   5) Settings   (الإعدادات)
 */
sealed class Route(val route: String) {
    data object Inventory : Route("inventory")
    data object Recipes : Route("recipes")
    data object Add     : Route("add")
    data object Shopping: Route("shopping")
    data object Settings: Route("settings")
}

/**
 * معلومات كل تبويب: الأيقونة (مفعّل/عادي) + مفتاح المورد
 */
enum class BottomTab(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isCenterFAB: Boolean = false,
) {
    INVENTORY(
        route = Route.Inventory.route,
        labelResId = R.string.tab_inventory,
        selectedIcon = Icons.Filled.Kitchen,
        unselectedIcon = Icons.Outlined.Kitchen,
    ),
    RECIPES(
        route = Route.Recipes.route,
        labelResId = R.string.tab_recipes,
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook,
    ),
    ADD(
        route = Route.Add.route,
        labelResId = R.string.tab_add,
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Filled.Add,
        isCenterFAB = true, // ← هذا الزر يصبح FAB مخصص
    ),
    SHOPPING(
        route = Route.Shopping.route,
        labelResId = R.string.tab_shopping,
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
    ),
    SETTINGS(
        route = Route.Settings.route,
        labelResId = R.string.tab_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    );

    companion object {
        /** القائمة المرتبة للعرض (من اليمين إلى اليسار في RTL) */
        val ordered: List<BottomTab> = listOf(
            INVENTORY, RECIPES, ADD, SHOPPING, SETTINGS
        )
    }
}
