package com.zerowaste.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.zerowaste.app.R

/**
 * ZeroWaste — Typography
 *
 * نستخدم خطوط Google Fonts المضمنة عبر Compose provider:
 *   - Cairo للعربية (RTL ممتاز + وضوح جيد)
 *   - Poppins للإنجليزية والأرقام (هندسي ومينيمال)
 *
 * ✅ لا تحتاج تنزيل أي ملف — Compose يجلبها تلقائياً عند أول استعمال.
 * ✅ يعمل offline بعد أول cache ناجح.
 *
 * إذا فشل الاتصال بالإنترنت في أول استعمال، يعود Compose إلى SansSerif الافتراضي.
 */
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val cairoGoogleFont = GoogleFont("Cairo")
private val poppinsGoogleFont = GoogleFont("Poppins")

// Cairo (للعربية) — Google Fonts Provider
val CairoFontFamily = FontFamily(
    Font(googleFont = cairoGoogleFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = cairoGoogleFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = cairoGoogleFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = cairoGoogleFont, fontProvider = provider, weight = FontWeight.Bold),
)

// Poppins (للإنجليزية والأرقام)
val PoppinsFontFamily = FontFamily(
    Font(googleFont = poppinsGoogleFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = poppinsGoogleFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = poppinsGoogleFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = poppinsGoogleFont, fontProvider = provider, weight = FontWeight.Bold),
)

/**
 * نظام الخطوط الكامل — يستعمل Cairo لجميع النصوص (دعم RTL ممتاز)
 */
val ZeroWasteTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    ),
)
