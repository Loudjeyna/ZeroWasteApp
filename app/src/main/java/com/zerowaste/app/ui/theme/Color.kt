package com.zerowaste.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * ZeroWaste — Color Palette
 *
 * نظام ألوان مزدوج:
 *  1) Material You ديناميكي (يُفعَّل على Android 12+ في Theme.kt)
 *  2) بديل ثابت (Light/Dark) للأجهزة الأقدم
 *
 * اللون الأساسي: أخضر صحي (يحيل إلى البيئة + مكافحة الهدر)
 * اللون الثانوي: تركواز (مرتبط بالطبيعة)
 * لون الإشارة: برتقالي دافئ (للتنبيهات الحرجة)
 */

// ─── Light Theme ───────────────────────────────────────────
val md_primary_light        = Color(0xFF2E7D32)   // أخضر داكن
val md_onPrimary_light      = Color(0xFFFFFFFF)
val md_primaryContainer_light = Color(0xFFB8F5BA) // أخضر فاتح
val md_onPrimaryContainer_light = Color(0xFF00390F)
val md_secondary_light      = Color(0xFF4F7C50)  // أخضر-رمادي
val md_onSecondary_light    = Color(0xFFFFFFFF)
val md_tertiary_light       = Color(0xFFE65100)  // برتقالي إشارة
val md_onTertiary_light     = Color(0xFFFFFFFF)
val md_error_light          = Color(0xFFB3261E)
val md_background_light     = Color(0xFFFAFBF5)   // خلفية كريمية ناعمة
val md_onBackground_light   = Color(0xFF1A1C18)
val md_surface_light        = Color(0xFFFFFDF7)
val md_onSurface_light      = Color(0xFF1A1C18)
val md_surfaceVariant_light = Color(0xFFE1E8DC)
val md_outline_light        = Color(0xFF727971)

// ─── Dark Theme ────────────────────────────────────────────
val md_primary_dark         = Color(0xFF81C784)
val md_onPrimary_dark       = Color(0xFF00390F)
val md_primaryContainer_dark = Color(0xFF005321)
val md_onPrimaryContainer_dark = Color(0xFFB8F5BA)
val md_secondary_dark       = Color(0xFFB7CCB1)
val md_onSecondary_dark      = Color(0xFF223A26)
val md_tertiary_dark        = Color(0xFFFFB74D)
val md_onTertiary_dark      = Color(0xFF4A2800)
val md_error_dark           = Color(0xFFFFB4AB)
val md_background_dark      = Color(0xFF101510)
val md_onBackground_dark     = Color(0xFFE0E4D9)
val md_surface_dark         = Color(0xFF121711)
val md_onSurface_dark       = Color(0xFFE0E4D9)
val md_surfaceVariant_dark  = Color(0xFF424A40)
val md_outline_dark         = Color(0xFF8C9387)

// ─── ألوان دلالية خاصة بالطعام ───────────────────────────
val foodFresh    = Color(0xFF4CAF50)  // طازج (أخضر)
val foodSoon     = Color(0xFFFFA726)  // قريبة الانتهاء (برتقالي)
val foodExpired  = Color(0xFFEF5350)  // منتهية (أحمر)
val foodFrozen   = Color(0xFF42A5F5)  // مجمّد (أزرق)

// ─── تدرّجات لونية (Gradients) للشاشات ─────────────────────
// تدرّج رأس الشاشة (Header Gradient) — أخضر صحي
val primaryGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2E7D32),
        Color(0xFF4CAF50)
    )
)

// تدرّج زر الإضافة العائم (FAB)
val fabGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF66BB6A),
        Color(0xFF2E7D32)
    )
)

// تدرّج بطاقة الطعام المنتهي
val expiredGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFEF5350),
        Color(0xFFB71C1C)
    )
)

// تدرّج بطاقة الطعام القريب
val soonGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFA726),
        Color(0xFFEF6C00)
    )
)

// تدرّج بطاقة الطعام الطازج
val freshGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF66BB6A),
        Color(0xFF2E7D32)
    )
)

// تدرّج شاشة التنقل السفلي (Background)
val bottomBarGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF1B5E20).copy(alpha = 0.95f),
        Color(0xFF2E7D32).copy(alpha = 0.95f)
    )
)
