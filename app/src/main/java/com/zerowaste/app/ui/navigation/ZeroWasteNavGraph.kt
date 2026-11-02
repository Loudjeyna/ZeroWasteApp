package com.zerowaste.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zerowaste.app.ui.screens.add.AddFoodScreen
import com.zerowaste.app.ui.screens.inventory.InventoryScreen
import com.zerowaste.app.ui.screens.recipes.RecipesScreen
import com.zerowaste.app.ui.screens.settings.SettingsScreen
import com.zerowaste.app.ui.screens.shopping.ShoppingScreen

/**
 * ZeroWasteNavGraph — NavHost يحوي 5 وجهات
 *
 * يدير التنقل بين الشاشات الخمس
 */
@Composable
fun ZeroWasteNavGraph(
    navController: NavHostController = rememberNavController(),
    onRouteChanged: (String) -> Unit,
) {
    // استمع لتغيّر الوجهة بدلاً من استدعاء onRouteChanged داخل كل composable
    // (هذا أفضل لأنه يلتقط حتى تنقّلات الـ back button)
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            destination.route?.let { onRouteChanged(it) }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Route.Inventory.route,
    ) {
        // 1) المخزون
        composable(Route.Inventory.route) {
            InventoryScreen(
                onAddFoodClick = { navController.navigate(Route.Add.route) },
                onNavigateToSettings = {
                    // ✅ نستعمل نفس خيارات الـ bottom bar للتنقل الصحيح
                    navController.navigate(Route.Settings.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }

        // 2) الوصفات
        composable(Route.Recipes.route) {
            RecipesScreen()
        }

        // 3) الإضافة
        composable(Route.Add.route) {
            AddFoodScreen(
                onSaved = {
                    // بعد الحفظ، نعود للمخزون
                    navController.navigate(Route.Inventory.route) {
                        popUpTo(Route.Inventory.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 4) التسوق
        composable(Route.Shopping.route) {
            ShoppingScreen()
        }

        // 5) الإعدادات
        composable(Route.Settings.route) {
            SettingsScreen()
        }
    }
}
