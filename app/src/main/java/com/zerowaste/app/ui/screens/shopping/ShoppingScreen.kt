package com.zerowaste.app.ui.screens.shopping

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.zerowaste.app.R
import com.zerowaste.app.data.model.ShoppingItem
import com.zerowaste.app.util.NumberUtils

/**
 * ShoppingScreen — شاشة قائمة التسوق الكاملة
 *
 * ✅ تجميع تلقائي حسب الفئة
 * ✅ Check/Uncheck للعناصر المُشتراة
 * ✅ بحث + فلتر
 * ✅ عدّادات (الكل + غير مشترى + مشترى)
 * ✅ زر إضافة سريعة (يفتح Bottom Sheet)
 * ✅ زر حذف المُشترى كله
 */
@Composable
fun ShoppingScreen(
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    // ✅ التحكم في عرض Bottom Sheet للإضافة السريعة
    var showQuickAdd by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // العنوان + عدّادات + أزرار التحكم
            ShoppingHeader(
                totalCount = state.totalCount,
                uncheckedCount = state.uncheckedCount,
                checkedCount = state.checkedCount,
                onClearChecked = viewModel::deleteChecked,
                onQuickAdd = { showQuickAdd = true },  // ⬅️ زر "+" ثابت دائماً
            )

            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchChanged,
            )

            FilterChips(
                selected = state.filter,
                onSelect = viewModel::onFilterChanged,
            )

            if (state.isEmpty) {
                EmptyState(onAddClick = { showQuickAdd = true })
            } else {
                ShoppingList(
                    groups = state.groups,
                    onToggleChecked = viewModel::toggleChecked,
                    onDelete = viewModel::delete,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    // ✅ عرض Bottom Sheet للإضافة السريعة
    if (showQuickAdd) {
        QuickAddBottomSheet(
            onAdd = { category, foodId, quantity, unit ->
                viewModel.quickAdd(category, foodId, quantity, unit)
            },
            onDismiss = { showQuickAdd = false },
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Header — العنوان + 3 عدّادات + زر حذف المُشترى
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ShoppingHeader(
    totalCount: Int,
    uncheckedCount: Int,
    checkedCount: Int,
    onClearChecked: () -> Unit,
    onQuickAdd: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // العنوان
            Text(
                text = stringResource(R.string.shopping_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            // أزرار التحكم (يمين)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // زر حذف المُشترى كله (يظهر فقط عند وجود عناصر مشتراة)
                if (checkedCount > 0) {
                    TextButton(onClick = onClearChecked) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.shopping_clear_checked),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
                // ⬅️ زر "+" ثابت دائماً (للإضافة السريعة)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onQuickAdd),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.shopping_add_quick),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatCard(
                label = NumberUtils.toLatin(stringResource(R.string.shopping_count_total, totalCount)),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "🟡 " + NumberUtils.formatInt(uncheckedCount),
                color = Color(0xFFFFA726),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "✅ " + NumberUtils.formatInt(checkedCount),
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  SearchBar
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        placeholder = { Text(text = stringResource(R.string.shopping_search_hint)) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
    )
}

// ═══════════════════════════════════════════════════════════════════
//  FilterChips
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun FilterChips(
    selected: ShoppingFilter,
    onSelect: (ShoppingFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(ShoppingFilter.entries) { filter ->
            val labelResId = when (filter) {
                ShoppingFilter.ALL -> R.string.shopping_filter_all
                ShoppingFilter.UNCHECKED -> R.string.shopping_filter_unchecked
                ShoppingFilter.CHECKED -> R.string.shopping_filter_checked
            }
            val isSelected = filter == selected
            val bgColor = if (isSelected) MaterialTheme.colorScheme.primary
                          else MaterialTheme.colorScheme.surfaceVariant
            val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                               else MaterialTheme.colorScheme.onSurfaceVariant
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(bgColor)
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(labelResId),
                    color = contentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  ShoppingList — قائمة مجمّعة حسب الفئة
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ShoppingList(
    groups: List<ShoppingViewModel.ShoppingGroup>,
    onToggleChecked: (ShoppingItem) -> Unit,
    onDelete: (ShoppingItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        groups.forEach { group ->
            // عنوان الفئة
            item(key = "header_${group.category.name}") {
                CategoryHeader(group)
            }
            // عناصر الفئة
            items(group.items, key = { it.id }) { item ->
                ShoppingItemCard(
                    item = item,
                    onToggleChecked = { onToggleChecked(item) },
                    onDelete = { onDelete(item) },
                )
            }
        }
        // مساحة للـ bottom bar
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun CategoryHeader(group: ShoppingViewModel.ShoppingGroup) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // إيموجي الفئة
        Text(
            text = group.category.emoji,
            fontSize = 20.sp,
        )
        // اسم الفئة
        Text(
            text = stringResource(group.category.labelResId),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        // عدد العناصر في الفئة
        Text(
            text = "(${NumberUtils.formatInt(group.items.size)})",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ShoppingItemCard(
    item: ShoppingItem,
    onToggleChecked: () -> Unit,
    onDelete: () -> Unit,
) {
    val category = item.toCategory()
    val unit = item.toUnit()

    val cardColor = if (item.isChecked)
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    else
        MaterialTheme.colorScheme.surface

    val textColor = if (item.isChecked)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ✅ Checkbox دائري
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isChecked) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = if (item.isChecked) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable(onClick = onToggleChecked),
                contentAlignment = Alignment.Center,
            ) {
                if (item.isChecked) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // إيموجي الطعام + الاسم + الكمية
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    // إيموجي الفئة
                    Text(
                        text = category.emoji,
                        fontSize = 18.sp,
                    )
                    // الاسم
                    Text(
                        text = stringResource(item.foodLabelResId),
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
                // الكمية + الوحدة
                Text(
                    text = "${NumberUtils.formatInt(item.quantity)} ${stringResource(unit.symbolResId)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // زر الحذف
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  EmptyState
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun EmptyState(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp),
                )
            }

            Text(
                text = stringResource(R.string.shopping_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            // زر إضافة سريعة
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onAddClick)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.shopping_add_quick),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
