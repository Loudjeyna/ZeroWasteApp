package com.zerowaste.app.ui.screens.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zerowaste.app.R
import com.zerowaste.app.data.model.ExpiryPreset
import com.zerowaste.app.data.model.FoodCategory
import com.zerowaste.app.data.model.QuantityUnit
import com.zerowaste.app.data.model.StorageLocation
import com.zerowaste.app.ui.theme.fabGradient

/**
 * AddFoodScreen — الشاشة الكاملة لإضافة طعام جديد (نسخة محسّنة)
 *
 * ✅ 100% اختيار بالأيقونات والإيموجي — لا يوجد أي Text Field
 * ✅ بطاقات أصغر ومرتّبة (4 أعمدة بدل 3)
 * ✅ اختيار الكمية بالأرقام (1, 2, 3, 5, 10...) + أزرار +/- دقيقة
 * ✅ اقتراح ذكي لمكان التخزين حسب الفئة
 * ✅ 4 أماكن تخزين مرئية في تخطيط 2×2 (وليست الثلاجة فقط)
 *
 * 5 خطوات عمودية:
 *   1) اختيار الفئة (شبكة 4 أعمدة من الإيموجي)
 *   2) اختيار النوع (شبكة 4 أعمدة من الإيموجي)
 *   3) اختيار الكمية (أرقام + اختيار الوحدة)
 *   4) اختيار تاريخ الانتهاء (Chips)
 *   5) اختيار مكان التخزين (2×2 grid)
 *   6) زر الحفظ (في الأسفل)
 */
@Composable
fun AddFoodScreen(
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddFoodViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // العنوان
            Text(
                text = stringResource(R.string.add_food_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )

            // ──────────── الخطوة 1: الفئة ────────────
            StepHeader(
                stepText = stringResource(R.string.add_step_1),
                title = stringResource(R.string.add_food_step_category),
            )
            CategoryGrid(
                selected = state.category,
                onSelect = viewModel::onCategorySelected,
            )

            HorizontalDivider()

            // ──────────── الخطوة 2: النوع ────────────
            StepHeader(
                stepText = stringResource(R.string.add_step_2),
                title = stringResource(R.string.add_food_step_food_type),
            )
            FoodGrid(
                category = state.category,
                selectedFoodId = state.foodId,
                onSelect = viewModel::onFoodSelected,
            )

            HorizontalDivider()

            // ──────────── الخطوة 3: الكمية ────────────
            StepHeader(
                stepText = stringResource(R.string.add_step_3),
                title = stringResource(R.string.add_food_step_quantity),
            )
            QuantitySelector(
                quantity = state.quantity,
                unit = state.unit,
                onQuantitySelected = viewModel::onQuantitySelected,
                onUnitSelected = viewModel::onUnitSelected,
            )

            HorizontalDivider()

            // ──────────── الخطوة 4: تاريخ الانتهاء ────────────
            StepHeader(
                stepText = stringResource(R.string.add_step_4),
                title = stringResource(R.string.add_food_step_expiry),
            )
            ExpiryPresetSelector(
                selected = state.expiryPreset,
                onSelect = viewModel::onExpiryPresetSelected,
            )

            HorizontalDivider()

            // ──────────── الخطوة 5: مكان التخزين ────────────
            StepHeader(
                stepText = stringResource(R.string.add_step_5),
                title = stringResource(R.string.add_food_step_location),
            )
            // إشارة للاقتراح الذكي (إذا كان من الفئة الافتراضية)
            if (state.location == state.category.defaultLocation) {
                Text(
                    text = "✨ ${stringResource(R.string.smart_suggestion)}: ${stringResource(state.location.labelResId)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic,
                )
            }
            LocationSelector(
                selected = state.location,
                onSelect = viewModel::onLocationSelected,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ──────────── زر الحفظ ────────────
            SaveButton(
                enabled = state.isComplete,
                onSave = { viewModel.saveFood(onSaved) },
            )

            // مساحة إضافية للـ bottom bar
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  StepHeader — رأس كل خطوة
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun StepHeader(
    stepText: String,
    title: String,
) {
    Column {
        Text(
            text = stepText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  CategoryGrid — شبكة الفئات (4 أعمدة، بطاقات صغيرة بإيموجي)
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun CategoryGrid(
    selected: FoodCategory,
    onSelect: (FoodCategory) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth().height(220.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(FoodCategory.entries) { category ->
            EmojiCard(
                emoji = category.emoji,
                label = stringResource(category.labelResId),
                isSelected = category == selected,
                accentColor = Color(category.colorArgb),
                onClick = { onSelect(category) },
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  FoodGrid — شبكة الأطعمة (4 أعمدة، بطاقات صغيرة بإيموجي)
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun FoodGrid(
    category: FoodCategory,
    selectedFoodId: String?,
    onSelect: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth().height(220.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(category.commonFoods) { food ->
            EmojiCard(
                emoji = food.emoji,
                label = stringResource(food.labelResId),
                isSelected = food.id == selectedFoodId,
                accentColor = Color(category.colorArgb),
                onClick = { onSelect(food.id) },
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  EmojiCard — بطاقة صغيرة بإيموجي + عنوان
//  ⭐ محسّنة: أصغر، أنيقة، بدون aspect ratio يكسر التخطيط
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun EmojiCard(
    emoji: String,
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) accentColor.copy(alpha = 0.18f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val borderColor = if (isSelected) accentColor else Color.Transparent
    val textColor = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (isSelected) 2.dp else 0.dp, borderColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // الإيموجي (أكبر من أيقونة Material)
            Text(
                text = emoji,
                fontSize = 28.sp,
                color = Color.Unspecified,  // إيموجي بألوانه الأصلية
            )
            Spacer(modifier = Modifier.height(4.dp))
            // العنوان
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  QuantitySelector — اختيار الكمية والوحدة
//  🎯 التصميم يطابق الصورة المرجعية:
//   - صف واحد من الأزرار الرقمية: 1, 2, 3, 4, 5, 10
//   - صف واحد من أزرار الوحدات بنفس الشكل
//   - مربعات صغيرة بزوايا دائرية (ليست دوائر)
//   - المُحدَّد: أخضر / غير المُحدَّد: أبيض فاتح
//   - بدون إيموجي على الوحدات (نص فقط)
//   - بدون عرض القيمة الحالية الكبير في الأعلى
// ═══════════════════════════════════════════════════════════════════

// الأرقام في صف واحد (تطابق الصورة)
private val QUICK_NUMBERS = listOf(1, 2, 3, 4, 5, 10)

@Composable
private fun QuantitySelector(
    quantity: Int,
    unit: QuantityUnit,
    onQuantitySelected: (Int) -> Unit,
    onUnitSelected: (QuantityUnit) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ─── صف الأرقام: 1, 2, 3, 4, 5, 10 ───
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QUICK_NUMBERS.forEach { value ->
                SmallSquareButton(
                    text = value.toString(),
                    isSelected = value == quantity,
                    onClick = { onQuantitySelected(value) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ─── عنوان الوحدة ───
        Text(
            text = stringResource(R.string.unit_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
        )

        // ─── صف الوحدات: نفس الشكل، نص فقط بدون إيموجي ───
        // نعرضهم في صف قابل للالتفاف تلقائياً (FlowRow-like عبر LazyRow)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(QuantityUnit.entries) { u ->
                SmallSquareButton(
                    text = stringResource(u.symbolResId),
                    isSelected = u == unit,
                    onClick = { onUnitSelected(u) },
                )
            }
        }
    }
}

/**
 * SmallSquareButton — زر مربع صغير بزوايا دائرية
 * 🎯 يطابق التصميم في الصورة المرجعية:
 *   - شكل: مربع بزوايا دائرية (RoundedCornerShape 8dp)
 *   - حجم: ارتفاع 44dp + عرض متغيّر (يملأ الوزن أو يلتف)
 *   - مُحدَّد: أخضر + نص أبيض
 *   - غير مُحدَّد: أبيض فاتح + نص رمادي
 *   - لا إيموجي، نص فقط
 */
@Composable
private fun SmallSquareButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun ChipWithEmoji(
    emoji: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = emoji,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  ExpiryPresetSelector — اختيار تاريخ الانتهاء
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ExpiryPresetSelector(
    selected: ExpiryPreset,
    onSelect: (ExpiryPreset) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(ExpiryPreset.entries) { preset ->
            ChipWithEmoji(
                emoji = preset.emoji,
                label = stringResource(preset.labelResId),
                isSelected = preset == selected,
                onClick = { onSelect(preset) },
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  LocationSelector — اختيار مكان التخزين
//  ⭐ محسّن: شبكة 2×2 (4 خيارات كلها مرئية في نفس الوقت)
//  بدل Row الذي كان يكسر التخطيط ويُظهر الثلاجة فقط
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun LocationSelector(
    selected: StorageLocation,
    onSelect: (StorageLocation) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().height(160.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(StorageLocation.entries) { location ->
            LocationCard(
                emoji = location.emoji,
                label = stringResource(location.labelResId),
                isSelected = location == selected,
                accentColor = Color(location.colorArgb),
                onClick = { onSelect(location) },
            )
        }
    }
}

@Composable
private fun LocationCard(
    emoji: String,
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) accentColor.copy(alpha = 0.18f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val borderColor = if (isSelected) accentColor else Color.Transparent
    val textColor = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (isSelected) 2.dp else 0.dp, borderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  SaveButton — زر الحفظ
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SaveButton(
    enabled: Boolean,
    onSave: () -> Unit,
) {
    Button(
        onClick = onSave,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) fabGradient
                    else Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant,
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.add_food_save),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
