package com.zerowaste.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.zerowaste.app.ui.screens.settings.SettingsViewModel
import com.zerowaste.app.ui.theme.ZeroWasteTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

/**
 * MainActivity — نقطة الدخول الوحيدة للتطبيق
 *
 * ✅ محدّث:
 *  - attachBaseContext: تطبيق اللغة المحفوظة قبل إنشاء الواجهة
 *  - onCreate: قراءة الثيم بشكل متزامن من SharedPreferences (لا يعتمد على ViewModel)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * ✅ تطبيق اللغة المحفوظة قبل أي شيء
     */
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("zerowaste_settings", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("language", "system") ?: "system"

        if (languageCode != "system") {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = android.content.res.Configuration(newBase.resources.configuration)
            config.setLocale(locale)
            val context = newBase.createConfigurationContext(config)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ قراءة الثيم من SharedPreferences (بشكل متزامن)
        val prefs = getSharedPreferences("zerowaste_settings", Context.MODE_PRIVATE)
        val themeCode = prefs.getString("theme_mode", "system") ?: "system"

        setContent {
            // ✅ isSystemInDarkTheme() يجب أن تكون داخل @Composable
            val darkTheme = when (themeCode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()  // ✅ الآن داخل @Composable
            }

            ZeroWasteTheme(
                darkTheme = darkTheme,
                dynamicColor = false,
            ) {
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
