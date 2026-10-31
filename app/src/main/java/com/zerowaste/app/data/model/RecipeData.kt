package com.zerowaste.app.data.model

import com.zerowaste.app.R

/**
 * RecipeData — 10 وصفات جزائرية أصيلة 🇩🇿
 *
 * 1. الشخشوخة الجزائرية
 * 2. الطاجين الجزائري بالدجاج
 * 3. الحريرة الجزائرية
 * 4. البوراك باللحم
 * 5. الشطيطة بالحمص
 * 6. المتوّم
 * 7. شربة فريك
 * 8. الكسكسي الجزائري
 * 9. البربوشة (حلوى)
 * 10. الزلابية (حلوى)
 *
 * المطابقة مع food IDs من FoodCategory.kt:
 *  - "lamb", "beef" → لحم
 *  - "chicken", "turkey" → دجاج
 *  - "minced_meat" → لحم مفروم
 *  - "rice", "couscous", "quinoa", "oats" → حبوب
 *  - "flour" → دقيق
 *  - "chickpeas", "beans", "lentils" → بقوليات
 *  - "tomato", "onion", "garlic", "potato", "carrot" → خضار
 *  - "egg" / "eggs" → بيض
 *  - "lemon" → ليمون
 *  - "honey", "sugar" → حلويات
 *  - "olive_oil" → زيت
 *  - "nuts" → مكسرات
 */
object RecipeData {

    val allRecipes: List<Recipe> = listOf(

        // 1) الشخشوخة الجزائرية
        Recipe(
            id = "chakhchoukha",
            nameResId = R.string.recipe_chakhchoukha,
            emoji = "🍲",
            descriptionResId = R.string.recipe_chakhchoukha_desc,
            durationMinutes = 120,
            servings = 6,
            categoryColorArgb = 0xFFEF6C00,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_lamb, "🍖", listOf("lamb", "beef")),
                RecipeIngredient(R.string.ingredient_flour, "🌾", listOf("flour")),
                RecipeIngredient(R.string.ingredient_chickpeas, "🥫", listOf("chickpeas")),
                RecipeIngredient(R.string.ingredient_tomato, "🍅", listOf("tomato")),
                RecipeIngredient(R.string.ingredient_onion, "🧅", listOf("onion")),
                RecipeIngredient(R.string.ingredient_olive_oil, "🫒", listOf("olive_oil")),
                RecipeIngredient(R.string.ingredient_spices, "🧂", listOf("salt"), isOptional = true),
            ),
            stepsResIds = listOf(
                R.string.recipe_chakhchoukha_step_1,
                R.string.recipe_chakhchoukha_step_2,
                R.string.recipe_chakhchoukha_step_3,
                R.string.recipe_chakhchoukha_step_4,
            ),
        ),

        // 2) الطاجين الجزائري بالدجاج
        Recipe(
            id = "tajine_algerian",
            nameResId = R.string.recipe_tajine_algerian,
            emoji = "🍗",
            descriptionResId = R.string.recipe_tajine_algerian_desc,
            durationMinutes = 75,
            servings = 4,
            categoryColorArgb = 0xFFEF6C00,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_chicken, "🍗", listOf("chicken", "turkey")),
                RecipeIngredient(R.string.ingredient_potato, "🥔", listOf("potato")),
                RecipeIngredient(R.string.ingredient_eggs, "🥚", listOf("eggs")),
                RecipeIngredient(R.string.ingredient_tomato, "🍅", listOf("tomato")),
                RecipeIngredient(R.string.ingredient_onion, "🧅", listOf("onion")),
                RecipeIngredient(R.string.ingredient_olive_oil, "🫒", listOf("olive_oil")),
                RecipeIngredient(R.string.ingredient_spices, "🧂", listOf("salt"), isOptional = true),
            ),
            stepsResIds = listOf(
                R.string.recipe_tajine_algerian_step_1,
                R.string.recipe_tajine_algerian_step_2,
                R.string.recipe_tajine_algerian_step_3,
            ),
        ),

        // 3) الحريرة الجزائرية
        Recipe(
            id = "harira",
            nameResId = R.string.recipe_harira,
            emoji = "🥣",
            descriptionResId = R.string.recipe_harira_desc,
            durationMinutes = 60,
            servings = 6,
            categoryColorArgb = 0xFFA1887F,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_lamb, "🍖", listOf("lamb", "beef")),
                RecipeIngredient(R.string.ingredient_lentils, "🥣", listOf("lentils", "beans")),
                RecipeIngredient(R.string.ingredient_chickpeas, "🥫", listOf("chickpeas")),
                RecipeIngredient(R.string.ingredient_tomato, "🍅", listOf("tomato")),
                RecipeIngredient(R.string.ingredient_onion, "🧅", listOf("onion")),
                RecipeIngredient(R.string.ingredient_couscous, "🍚", listOf("couscous", "rice"), isOptional = true),
            ),
            stepsResIds = listOf(
                R.string.recipe_harira_step_1,
                R.string.recipe_harira_step_2,
                R.string.recipe_harira_step_3,
            ),
        ),

        // 4) البوراك باللحم
        Recipe(
            id = "bourek",
            nameResId = R.string.recipe_bourek,
            emoji = "🥟",
            descriptionResId = R.string.recipe_bourek_desc,
            durationMinutes = 40,
            servings = 4,
            categoryColorArgb = 0xFFFFCA28,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_minced_meat, "🥩", listOf("minced_meat", "beef", "lamb")),
                RecipeIngredient(R.string.ingredient_onion, "🧅", listOf("onion")),
                RecipeIngredient(R.string.ingredient_eggs, "🥚", listOf("eggs")),
                RecipeIngredient(R.string.ingredient_flour, "🌾", listOf("flour")),
                RecipeIngredient(R.string.ingredient_olive_oil, "🫒", listOf("olive_oil")),
            ),
            stepsResIds = listOf(
                R.string.recipe_bourek_step_1,
                R.string.recipe_bourek_step_2,
                R.string.recipe_bourek_step_3,
            ),
        ),

        // 5) الشطيطة بالحمص
        Recipe(
            id = "chtitha",
            nameResId = R.string.recipe_cthitha,
            emoji = "🌶️",
            descriptionResId = R.string.recipe_cthitha_desc,
            durationMinutes = 50,
            servings = 4,
            categoryColorArgb = 0xFFEF5350,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_chicken, "🍗", listOf("chicken", "turkey", "lamb")),
                RecipeIngredient(R.string.ingredient_chickpeas, "🥫", listOf("chickpeas")),
                RecipeIngredient(R.string.ingredient_garlic, "🧄", listOf("garlic")),
                RecipeIngredient(R.string.ingredient_tomato, "🍅", listOf("tomato")),
                RecipeIngredient(R.string.ingredient_olive_oil, "🫒", listOf("olive_oil")),
                RecipeIngredient(R.string.ingredient_spices, "🧂", listOf("salt"), isOptional = true),
            ),
            stepsResIds = listOf(
                R.string.recipe_cthitha_step_1,
                R.string.recipe_cthitha_step_2,
                R.string.recipe_cthitha_step_3,
            ),
        ),

        // 6) المتوّم
        Recipe(
            id = "mtewem",
            nameResId = R.string.recipe_mtewem,
            emoji = "🧄",
            descriptionResId = R.string.recipe_mtewem_desc,
            durationMinutes = 45,
            servings = 4,
            categoryColorArgb = 0xFFA1887F,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_minced_meat, "🥩", listOf("minced_meat", "beef", "lamb")),
                RecipeIngredient(R.string.ingredient_chickpeas, "🥫", listOf("chickpeas")),
                RecipeIngredient(R.string.ingredient_garlic, "🧄", listOf("garlic")),
                RecipeIngredient(R.string.ingredient_nuts, "🥜", listOf("nuts")),
                RecipeIngredient(R.string.ingredient_lemon, "🍋", listOf("lemon")),
                RecipeIngredient(R.string.ingredient_olive_oil, "🫒", listOf("olive_oil")),
            ),
            stepsResIds = listOf(
                R.string.recipe_mtewem_step_1,
                R.string.recipe_mtewem_step_2,
                R.string.recipe_mtewem_step_3,
            ),
        ),

        // 7) شربة فريك
        Recipe(
            id = "frik_soup",
            nameResId = R.string.recipe_frik_soup,
            emoji = "🍲",
            descriptionResId = R.string.recipe_frik_soup_desc,
            durationMinutes = 60,
            servings = 6,
            categoryColorArgb = 0xFFA1887F,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_lamb, "🍖", listOf("lamb", "beef", "chicken")),
                RecipeIngredient(R.string.ingredient_couscous, "🍚", listOf("couscous", "rice", "oats", "quinoa")),
                RecipeIngredient(R.string.ingredient_chickpeas, "🥫", listOf("chickpeas")),
                RecipeIngredient(R.string.ingredient_tomato, "🍅", listOf("tomato")),
                RecipeIngredient(R.string.ingredient_onion, "🧅", listOf("onion")),
            ),
            stepsResIds = listOf(
                R.string.recipe_frik_soup_step_1,
                R.string.recipe_frik_soup_step_2,
                R.string.recipe_frik_soup_step_3,
            ),
        ),

        // 8) الكسكسي الجزائري
        Recipe(
            id = "couscous_algerian",
            nameResId = R.string.recipe_couscous_algerian,
            emoji = "🍚",
            descriptionResId = R.string.recipe_couscous_algerian_desc,
            durationMinutes = 90,
            servings = 6,
            categoryColorArgb = 0xFFEF6C00,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_couscous, "🍚", listOf("couscous", "rice")),
                RecipeIngredient(R.string.ingredient_lamb, "🍖", listOf("lamb", "beef", "chicken")),
                RecipeIngredient(R.string.ingredient_vegetables, "🥕", listOf("carrot", "potato", "eggplant", "spinach")),
                RecipeIngredient(R.string.ingredient_chickpeas, "🥫", listOf("chickpeas")),
                RecipeIngredient(R.string.ingredient_onion, "🧅", listOf("onion")),
                RecipeIngredient(R.string.ingredient_olive_oil, "🫒", listOf("olive_oil")),
            ),
            stepsResIds = listOf(
                R.string.recipe_couscous_algerian_step_1,
                R.string.recipe_couscous_algerian_step_2,
                R.string.recipe_couscous_algerian_step_3,
            ),
        ),

        // 9) البربوشة (حلوى السميد)
        Recipe(
            id = "barbouche",
            nameResId = R.string.recipe_barbouche,
            emoji = "🍯",
            descriptionResId = R.string.recipe_barbouche_desc,
            durationMinutes = 45,
            servings = 8,
            categoryColorArgb = 0xFFFFCA28,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_flour, "🌾", listOf("flour")),
                RecipeIngredient(R.string.ingredient_honey, "🍯", listOf("honey")),
                RecipeIngredient(R.string.ingredient_olive_oil, "🫒", listOf("olive_oil")),
                RecipeIngredient(R.string.ingredient_sugar, "🍬", listOf("sugar"), isOptional = true),
            ),
            stepsResIds = listOf(
                R.string.recipe_barbouche_step_1,
                R.string.recipe_barbouche_step_2,
                R.string.recipe_barbouche_step_3,
            ),
        ),

        // 10) الزلابية
        Recipe(
            id = "zlabia",
            nameResId = R.string.recipe_zlabia,
            emoji = "🍩",
            descriptionResId = R.string.recipe_zlabia_desc,
            durationMinutes = 30,
            servings = 10,
            categoryColorArgb = 0xFFFFCA28,
            ingredients = listOf(
                RecipeIngredient(R.string.ingredient_flour, "🌾", listOf("flour")),
                RecipeIngredient(R.string.ingredient_honey, "🍯", listOf("honey", "sugar")),
                RecipeIngredient(R.string.ingredient_olive_oil, "🫒", listOf("olive_oil")),
                RecipeIngredient(R.string.ingredient_sugar, "🍬", listOf("sugar")),
            ),
            stepsResIds = listOf(
                R.string.recipe_zlabia_step_1,
                R.string.recipe_zlabia_step_2,
                R.string.recipe_zlabia_step_3,
            ),
        ),
    )

    /** الوصول لوصفة بمعرّفها */
    fun getById(id: String): Recipe? = allRecipes.find { it.id == id }
}
