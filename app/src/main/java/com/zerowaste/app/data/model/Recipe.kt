package com.zerowaste.app.data.model

/**
 * Recipe — وصفة عربية
 *
 * @param id معرّف فريد (مثل "mansaf")
 * @param nameResId مفتاح اسم الوصفة
 * @param emoji إيموجي تعبيري
 * @param descriptionResId مفتاح وصف قصير
 * @param durationMinutes مدة التحضير بالدقائق
 * @param servings عدد الأشخاص
 * @param ingredients قائمة المكوّنات (مع المطابقة الذكية)
 * @param stepsResIds قائمة مفاتيح الخطوات
 * @param categoryColorArgb لون مميز للبطاقة
 */
data class Recipe(
    val id: String,
    val nameResId: Int,
    val emoji: String,
    val descriptionResId: Int,
    val durationMinutes: Int,
    val servings: Int,
    val ingredients: List<RecipeIngredient>,
    val stepsResIds: List<Int>,
    val categoryColorArgb: Long,
) {
    /**
     * عدد المكوّنات الإجمالي (يُستعمل لعرض "5/7")
     */
    val totalIngredientsCount: Int get() = ingredients.size
}

/**
 * مكوّن وصفة + قائمة الأطعمة المطابقة
 *
 * مثال: مكوّن "حمص" يطابق:
 *   - chickpeas (حمص حب)
 *   - chickpeas_canned (حمص معلّب)
 *   - hummus (حمص جاهز)
 *
 * ✅ مطابقة مرنة: يكفي وجود أحد الأطعمة في matchingFoodIds
 * ✅ تجاهل الكمية: لا نتحقق إن كان لديك كمية كافية
 *
 * @param isOptional المكوّن اختياري (لا يُحتسب في النسبة المئوية للمطابقة)
 */
data class RecipeIngredient(
    val nameResId: Int,
    val emoji: String,
    val matchingFoodIds: List<String>,
    val isOptional: Boolean = false,
)

/**
 * نتيجة مطابقة وصفة مع مخزون المستخدم
 */
data class RecipeMatchResult(
    val recipe: Recipe,
    val matchedIngredients: List<RecipeIngredient>,    // ما يملكه
    val missingIngredients: List<RecipeIngredient>,    // ما ينقصه
    val matchStatus: RecipeMatchStatus,
) {
    /** نسبة المطابقة: matched / total (مع تجاهل الاختياري) */
    val matchPercentage: Int
        get() {
            val required = recipe.ingredients.filter { !it.isOptional }
            if (required.isEmpty()) return 100
            val matchedRequired = matchedIngredients.count { !it.isOptional }
            return (matchedRequired * 100 / required.size)
        }

    /** "5/7" - عدد المطابق / الإجمالي المطلوب */
    val matchLabel: String
        get() {
            val required = recipe.ingredients.filter { !it.isOptional }
            val matchedRequired = matchedIngredients.count { !it.isOptional }
            return "$matchedRequired/${required.size}"
        }
}

enum class RecipeMatchStatus {
    FULL,       // مطابقة كاملة (100%)
    PARTIAL,    // ناقصة (1-99%)
    NONE;       // لا تملك أي مكوّن

    val emoji: String
        get() = when (this) {
            FULL -> "🟢"
            PARTIAL -> "🟡"
            NONE -> "🔴"
        }

    val labelResId: Int
        get() = when (this) {
            FULL -> com.zerowaste.app.R.string.recipes_match_full
            PARTIAL -> com.zerowaste.app.R.string.recipes_match_partial
            NONE -> com.zerowaste.app.R.string.recipes_match_none
        }
}
