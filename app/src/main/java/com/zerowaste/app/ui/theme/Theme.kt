package com.zerowaste.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * ZeroWaste — Material 3 Color Scheme
 *
 * استراتيجية:
 * 1. على Android 12+ (API 31+): نستخدم dynamicColor (Material You)
 *    أي الألوان تُستخرج من خلفية الجهاز تلقائياً.
 * 2. على الأقدم: نستخدم نظام ألوان ثابت (Light/Dark) مخصص.
 *
 * يُتحكَّم في تفعيل Material You من الإعدادات لاحقاً.
 */

private val LightColorScheme = lightColorScheme(
    primary = md_primary_light,
    onPrimary = md_onPrimary_light,
    primaryContainer = md_primaryContainer_light,
    onPrimaryContainer = md_onPrimaryContainer_light,
    secondary = md_secondary_light,
    onSecondary = md_onSecondary_light,
    tertiary = md_tertiary_light,
    onTertiary = md_onTertiary_light,
    error = md_error_light,
    background = md_background_light,
    onBackground = md_onBackground_light,
    surface = md_surface_light,
    onSurface = md_onSurface_light,
    surfaceVariant = md_surfaceVariant_light,
    outline = md_outline_light,
)

private val DarkColorScheme = darkColorScheme(
    primary = md_primary_dark,
    onPrimary = md_onPrimary_dark,
    primaryContainer = md_primaryContainer_dark,
    onPrimaryContainer = md_onPrimaryContainer_dark,
    secondary = md_secondary_dark,
    onSecondary = md_onSecondary_dark,
    tertiary = md_tertiary_dark,
    onTertiary = md_onTertiary_dark,
    error = md_error_dark,
    background = md_background_dark,
    onBackground = md_onBackground_dark,
    surface = md_surface_dark,
    onSurface = md_onSurface_dark,
    surfaceVariant = md_surfaceVariant_dark,
    outline = md_outline_dark,
)

/**
 * الثيم الرئيسي للتطبيق
 *
 * @param darkTheme هل نحن في الوضع الليلي؟ (مأخوذ تلقائياً من النظام)
 * @param dynamicColor هل نفعل Material You؟ الافتراضي true على Android 12+
 */
@Composable
fun ZeroWasteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // سيتم تعديله لاحقاً من الإعدادات
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        // Android 12+ مع Material You مُفعّل
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        // الوضع الليلي بدون Material You
        darkTheme -> DarkColorScheme
        // الوضع النهاري بدون Material You
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZeroWasteTypography,
        content = content
    )
}
