# ─────────────────────────────────────────────────────────────
# ZeroWaste — ProGuard Rules
# ─────────────────────────────────────────────────────────────

# ── Kotlin Metadata (مهم لـ Compose و Reflection) ──
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# ── Hilt (Dependency Injection) ──
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }
-keep,allowobfuscation,allowshrinking class * extends androidx.lifecycle.ViewModel
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# ── Room (Entities + Daos) ──
-keep class com.zerowaste.app.data.model.** { *; }
-keep class com.zerowaste.app.data.db.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# ── Compose ──
-dontwarn androidx.compose.**

# ── Coroutines ──
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlin.coroutines.Continuation

# ── WorkManager ──
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(...);
}
