package com.zerowaste.app.ui.screens.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerowaste.app.R
import com.zerowaste.app.data.model.FoodCategory
import com.zerowaste.app.data.model.QuantityUnit

/**
 * QuickAddBottomSheet — إضافة سريعة لعنصر تسوق
 *
 * نفس فلسفة AddFoodScreen: 100% أيقونات، صفر كتابة
 *
 * خطوات:
 *  1) اختيار الفئة (شبكة 4 أعمدة بإيموجي)
 *  2) اختيار النوع (شبكة 4 أعمدة بإيموجي)
 *  3) اختيار الكمية (أزرار مربعة: 1, 2, 3, 4, 5, 10)
 *  4) اختيار الوحدة (مربعات: حبة، كغ، غ، ل، مل، علبة...)
 *  5) زر حفظ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddBottomSheet(
    onAdd: (FoodCategory, String, Int, QuantityUnit) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // الحالة
    var selectedCategory by remember { mutableStateOf(FoodCategory.default) }
    var selectedFoodId by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableIntStateOf(1) }
    var selectedUnit by remember { mutableStateOf(QuantityUnit.PIECE) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // العنوان + زر إغلاق
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.shopping_add_quick),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // ─── الخطوة 1: اختيار الفئة ───
            SectionLabel(text = stringResource(R.string.shopping_step_category))
            CategoryGrid(
                selected = selectedCategory,
                onSelect = {
                    selectedCategory = it
                    selectedFoodId = null  // إعادة ضبط
                },
            )

            // ─── الخطوة 2: اختيار النوع ───
            SectionLabel(text = stringResource(R.string.shopping_step_food))
            FoodGrid(
                category = selectedCategory,
                selectedFoodId = selectedFoodId,
                onSelect = { selectedFoodId = it },
            )

            // ─── الخطوة 3: الكمية ───
            SectionLabel(text = stringResource(R.string.shopping_step_quantity))
            QuantityRow(
                quantity = quantity,
                onQuantitySelected = { quantity = it },
            )

            // ─── الخطوة 4: الوحدة ───
            SectionLabel(text = stringResource(R.string.shopping_step_unit))
            UnitRow(
                selectedUnit = selectedUnit,
                onUnitSelected = { selectedUnit = it },
            )

            // ─── زر الحفظ ───
            Button(
                onClick = {
                    val foodId = selectedFoodId ?: return@Button
                    onAdd(selectedCategory, foodId, quantity, selectedUnit)
                    onDismiss()
                },
                enabled = selectedFoodId != null,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.shopping_add_to_list),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  مكوّنات مساعدة
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
    )
}

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
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

@Composable
private fun QuantityRow(
    quantity: Int,
    onQuantitySelected: (Int) -> Unit,
) {
    val quantities = listOf(1, 2, 3, 4, 5, 10)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        quantities.forEach { value ->
            SquareButton(
                text = value.toString(),
                isSelected = value == quantity,
                onClick = { onQuantitySelected(value) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun UnitRow(
    selectedUnit: QuantityUnit,
    onUnitSelected: (QuantityUnit) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(QuantityUnit.entries) { unit ->
            SquareButton(
                text = stringResource(unit.symbolResId),
                isSelected = unit == selectedUnit,
                onClick = { onUnitSelected(unit) },
            )
        }
    }
}

@Composable
private fun SquareButton(
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
