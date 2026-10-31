// ─────────────────────────────────────────────────────────────
// ZeroWaste — Root build.gradle.kts
// استراتيجية: استخدام Version Catalog (libs.versions.toml)
// لتفادي تعارض الإصدارات بين المكتبات
// ─────────────────────────────────────────────────────────────

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
}
