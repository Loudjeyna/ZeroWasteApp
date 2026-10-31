package com.zerowaste.app.ui.screens.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * AddFoodScreen — الشاشة الكاملة لإضافة طعام جديد
 *
 * ✅ 100% اختيار بالأيقونات — لا يوجد أي Text Field
 *
 * 5 خطوات عمودية:
 *   1) اختيار الفئة (شبكة 3 أعمدة من الأيقونات)
 *   2) اختيار النوع (شبكة 3 أعمدة من الأيقونات)
 *   3) اختيار الكمية (+/- + شبكة وحدات)
 *   4) اختيار تاريخ الانتهاء (Chips)
 *   5) اختيار مكان التخزين (4 أيقونات)
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
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
                onIncrease = viewModel::onQuantityIncrease,
                onDecrease = viewModel::onQuantityDecrease,
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
//  مكوّنات قابلة لإعادة الاستخدام
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

@Composable
private fun CategoryGrid(
    selected: FoodCategory,
    onSelect: (FoodCategory) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth().height(280.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(FoodCategory.entries) { category ->
            IconCard(
                icon = category.icon,
                label = stringResource(category.labelResId),
                isSelected = category == selected,
                accentColor = Color(category.colorArgb),
                onClick = { onSelect(category) },
            )
        }
    }
}

@Composable
private fun FoodGrid(
    category: FoodCategory,
    selectedFoodId: String?,
    onSelect: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth().height(280.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(category.commonFoods) { food ->
            IconCard(
                icon = category.icon, // نعيد استخدام أيقونة الفئة
                label = stringResource(food.labelResId),
                isSelected = food.id == selectedFoodId,
                accentColor = Color(category.colorArgb),
                onClick = { onSelect(food.id) },
            )
        }
    }
}

@Composable
private fun IconCard(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) accentColor.copy(alpha = 0.15f)
                          else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val borderColor = if (isSelected) accentColor else Color.Transparent
    val iconColor = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelected) 2.dp else 0.dp, borderColor),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    unit: QuantityUnit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onUnitSelected: (QuantityUnit) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // عدّاد +/-
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // زر -
            CircularIconButton(
                icon = Icons.Filled.Remove,
                onClick = onDecrease,
                enabled = quantity > 1,
            )
            Spacer(modifier = Modifier.width(24.dp))
            // الكمية
            Text(
                text = "$quantity ${stringResource(unit.symbolResId)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(24.dp))
            // زر +
            CircularIconButton(
                icon = Icons.Filled.Add,
                onClick = onIncrease,
                enabled = quantity < 999,
            )
        }

        // اختيار الوحدة
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
        ) {
            items(QuantityUnit.entries) { u ->
                UnitChip(
                    label = stringResource(u.labelResId),
                    isSelected = u == unit,
                    onClick = { onUnitSelected(u) },
                )
            }
        }
    }
}

@Composable
private fun CircularIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(
                if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onPrimary
                   else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun UnitChip(
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

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
            UnitChip(
                label = stringResource(preset.labelResId),
                isSelected = preset == selected,
                onClick = { onSelect(preset) },
            )
        }
    }
}

@Composable
private fun LocationSelector(
    selected: StorageLocation,
    onSelect: (StorageLocation) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StorageLocation.entries.forEach { location ->
            val weight = 1f
            IconCard(
                icon = location.icon,
                label = stringResource(location.labelResId),
                isSelected = location == selected,
                accentColor = Color(location.colorArgb),
                onClick = { onSelect(location) },
            )
        }
    }
}

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
