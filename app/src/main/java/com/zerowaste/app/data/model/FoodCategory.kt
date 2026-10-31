package com.zerowaste.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.OilBarrel
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.ui.graphics.vector.ImageVector
import com.zerowaste.app.R

/**
 * فئات الطعام — 11 فئة مع أيقونات Material Icons
 *
 * كل فئة تحتوي على:
 *  - أيقونة للعرض
 *  - مفتاح المورد للاسم العربي/الإنجليزي
 *  - لون مميز للفئة
 *  - قائمة أطعمة شائعة جاهزة (لا حاجة للكتابة)
 */
enum class FoodCategory(
    val labelResId: Int,
    val icon: ImageVector,
    val colorArgb: Long,
    val commonFoods: List<CommonFood>,
) {
    FRUITS(
        labelResId = R.string.category_fruits,
        icon = Icons.Filled.Grass,
        colorArgb = 0xFFEF5350,
        commonFoods = listOf(
            CommonFood("apple", R.string.food_apple),
            CommonFood("banana", R.string.food_banana),
            CommonFood("orange", R.string.food_orange),
            CommonFood("strawberry", R.string.food_strawberry),
            CommonFood("grapes", R.string.food_grapes),
            CommonFood("watermelon", R.string.food_watermelon),
            CommonFood("lemon", R.string.food_lemon),
            CommonFood("avocado", R.string.food_avocado),
            CommonFood("mango", R.string.food_mango),
            CommonFood("pear", R.string.food_pear),
        )
    ),
    VEGETABLES(
        labelResId = R.string.category_vegetables,
        icon = Icons.Filled.Grass,
        colorArgb = 0xFF66BB6A,
        commonFoods = listOf(
            CommonFood("tomato", R.string.food_tomato),
            CommonFood("cucumber", R.string.food_cucumber),
            CommonFood("carrot", R.string.food_carrot),
            CommonFood("onion", R.string.food_onion),
            CommonFood("potato", R.string.food_potato),
            CommonFood("lettuce", R.string.food_lettuce),
            CommonFood("pepper", R.string.food_pepper),
            CommonFood("eggplant", R.string.food_eggplant),
            CommonFood("spinach", R.string.food_spinach),
            CommonFood("garlic", R.string.food_garlic),
        )
    ),
    DAIRY(
        labelResId = R.string.category_dairy,
        icon = Icons.Filled.BreakfastDining,
        colorArgb = 0xFFFFCA28,
        commonFoods = listOf(
            CommonFood("milk", R.string.food_milk),
            CommonFood("yogurt", R.string.food_yogurt),
            CommonFood("cheese_white", R.string.food_cheese_white),
            CommonFood("cheese_yellow", R.string.food_cheese_yellow),
            CommonFood("butter", R.string.food_butter),
            CommonFood("cream", R.string.food_cream),
            CommonFood("labneh", R.string.food_labneh),
            CommonFood("eggs", R.string.food_eggs),
        )
    ),
    MEAT(
        labelResId = R.string.category_meat,
        icon = Icons.Filled.SetMeal,
        colorArgb = 0xFFEF6C00,
        commonFoods = listOf(
            CommonFood("chicken", R.string.food_chicken),
            CommonFood("beef", R.string.food_beef),
            CommonFood("lamb", R.string.food_lamb),
            CommonFood("fish", R.string.food_fish),
            CommonFood("shrimp", R.string.food_shrimp),
            CommonFood("minced_meat", R.string.food_minced_meat),
            CommonFood("turkey", R.string.food_turkey),
        )
    ),
    BAKERY(
        labelResId = R.string.category_bakery,
        icon = Icons.Filled.BakeryDining,
        colorArgb = 0xFF8D6E63,
        commonFoods = listOf(
            CommonFood("bread_white", R.string.food_bread_white),
            CommonFood("bread_arabic", R.string.food_bread_arabic),
            CommonFood("bread_whole", R.string.food_bread_whole),
            CommonFood("croissant", R.string.food_croissant),
            CommonFood("baguette", R.string.food_baguette),
            CommonFood("tortilla", R.string.food_tortilla),
            CommonFood("bun", R.string.food_bun),
        )
    ),
    GRAINS(
        labelResId = R.string.category_grains,
        icon = Icons.Filled.Grass,
        colorArgb = 0xFFA1887F,
        commonFoods = listOf(
            CommonFood("rice", R.string.food_rice),
            CommonFood("pasta", R.string.food_pasta),
            CommonFood("flour", R.string.food_flour),
            CommonFood("oats", R.string.food_oats),
            CommonFood("lentils", R.string.food_lentils),
            CommonFood("chickpeas", R.string.food_chickpeas),
            CommonFood("beans", R.string.food_beans),
            CommonFood("quinoa", R.string.food_quinoa),
            CommonFood("couscous", R.string.food_couscous),
        )
    ),
    BEVERAGES(
        labelResId = R.string.category_beverages,
        icon = Icons.Filled.LocalDrink,
        colorArgb = 0xFF42A5F5,
        commonFoods = listOf(
            CommonFood("water", R.string.food_water),
            CommonFood("juice_orange", R.string.food_juice_orange),
            CommonFood("juice_apple", R.string.food_juice_apple),
            CommonFood("soda", R.string.food_soda),
            CommonFood("tea", R.string.food_tea),
            CommonFood("coffee", R.string.food_coffee_drink),
            CommonFood("milk_drink", R.string.food_milk_drink),
        )
    ),
    SNACKS(
        labelResId = R.string.category_snacks,
        icon = Icons.Filled.Fastfood,
        colorArgb = 0xFFFF7043,
        commonFoods = listOf(
            CommonFood("chips", R.string.food_chips),
            CommonFood("chocolate", R.string.food_chocolate),
            CommonFood("candy", R.string.food_candy),
            CommonFood("cookies", R.string.food_cookies),
            CommonFood("crackers", R.string.food_crackers),
            CommonFood("nuts", R.string.food_nuts),
            CommonFood("popcorn", R.string.food_popcorn),
        )
    ),
    CONDIMENTS(
        labelResId = R.string.category_condiments,
        icon = Icons.Filled.OilBarrel,
        colorArgb = 0xFF8E24AA,
        commonFoods = listOf(
            CommonFood("salt", R.string.food_salt),
            CommonFood("sugar", R.string.food_sugar),
            CommonFood("olive_oil", R.string.food_olive_oil),
            CommonFood("vinegar", R.string.food_vinegar),
            CommonFood("ketchup", R.string.food_ketchup),
            CommonFood("mayo", R.string.food_mayo),
            CommonFood("mustard", R.string.food_mustard),
            CommonFood("soy_sauce", R.string.food_soy_sauce),
            CommonFood("honey", R.string.food_honey),
            CommonFood("jam", R.string.food_jam),
        )
    ),
    FROZEN(
        labelResId = R.string.category_frozen,
        icon = Icons.Filled.AcUnit,
        colorArgb = 0xFF26C6DA,
        commonFoods = listOf(
            CommonFood("ice_cream", R.string.food_ice_cream),
            CommonFood("frozen_veg", R.string.food_frozen_veg),
            CommonFood("frozen_fries", R.string.food_frozen_fries),
            CommonFood("frozen_pizza", R.string.food_frozen_pizza),
            CommonFood("frozen_burger", R.string.food_frozen_burger),
        )
    ),
    OTHER(
        labelResId = R.string.category_other,
        icon = Icons.Filled.NoFood,
        colorArgb = 0xFF607D8B,
        commonFoods = listOf(
            CommonFood("custom", R.string.food_custom),
        )
    );

    companion object {
        val default: FoodCategory = FRUITS
    }
}

/**
 * طعام جاهز ضمن فئة
 * @param id معرّف فريد
 * @param labelResId مفتاح النص
 */
data class CommonFood(
    val id: String,
    val labelResId: Int,
)
