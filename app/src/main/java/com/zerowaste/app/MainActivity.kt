package com.zerowaste.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.zerowaste.app.ui.components.BadgesViewModel
import com.zerowaste.app.ui.components.ZeroWasteBottomBar
import com.zerowaste.app.ui.navigation.Route
import com.zerowaste.app.ui.navigation.ZeroWasteNavGraph
import com.zerowaste.app.ui.theme.ZeroWasteTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity — نقطة الدخول الوحيدة للتطبيق
 *
 * تستضيف:
 *   - Theme (Material 3 + Material You + Dark/Light)
 *   - Scaffold مع شريط التنقل السفلي المخصص (ZeroWasteBottomBar)
 *   - NavHost للتنقل بين 5 شاشات
 *   - BadgesViewModel لعرض البادجات الحقيقية على التبويبات
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-Edge: ملء الشاشة بالكامل
        enableEdgeToEdge()
        setContent {
            ZeroWasteTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    badgesViewModel: BadgesViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf(Route.Inventory.route) }

    // البادجات الحقيقية من Room
    val badges by badgesViewModel.badges.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ZeroWasteBottomBar(
                currentRoute = currentRoute,
                onTabSelected = { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                badges = badges,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ZeroWasteNavGraph(
                navController = navController,
                onRouteChanged = { newRoute -> currentRoute = newRoute },
            )
        }
    }
}
