package com.zerowaste.app.ui.screens.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zerowaste.app.R
import com.zerowaste.app.data.model.FoodItem
import com.zerowaste.app.data.model.FoodStatus
import com.zerowaste.app.data.model.QuantityUnit
import com.zerowaste.app.data.model.StorageLocation
import com.zerowaste.app.ui.theme.expiredGradient
import com.zerowaste.app.ui.theme.freshGradient
import com.zerowaste.app.ui.theme.soonGradient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * InventoryScreen — شاشة المخزون الكاملة
 *
 * - قائمة بالأطعمة مرتبة حسب تاريخ الانتهاء
 * - بطاقات ملوّنة حسب الحالة (طازج/قريب/منتهي)
 * - فلتر + بحث
 * - عدّادات في الأعلى
 * - زر حذف على كل بطاقة
 * - حالة فارغة (empty state)
 */
@Composable
fun InventoryScreen(
    modifier: Modifier = Modifier,
    onAddFoodClick: () -> Unit = {},
    viewModel: InventoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // ─────────── العنوان + عدّادات ───────────
            InventoryHeader(
                totalCount = state.totalCount,
                expiringCount = state.expiringCount,
                expiredCount = state.expiredCount,
            )

            // ─────────── البحث ───────────
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchChanged,
            )

            // ─────────── الفلاتر ───────────
            FilterChips(
                selected = state.filter,
                onSelect = viewModel::onFilterChanged,
            )

            // ─────────── القائمة ───────────
            if (state.isEmpty) {
                EmptyState(onAddClick = onAddFoodClick)
            } else {
                FoodList(
                    items = state.items,
                    onDelete = viewModel::deleteItem,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  InventoryHeader — العنوان + 3 عدّادات
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun InventoryHeader(
    totalCount: Int,
    expiringCount: Int,
    expiredCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.inventory_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatCard(
                label = stringResource(R.string.inventory_count_total, totalCount),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = stringResource(R.string.inventory_count_expiring, expiringCount),
                color = Color(0xFFFFA726),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = stringResource(R.string.inventory_count_expired, expiredCount),
                color = Color(0xFFEF5350),
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
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
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
        placeholder = {
            Text(text = stringResource(R.string.inventory_search_hint))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
        ),
    )
}

// ═══════════════════════════════════════════════════════════════════
//  FilterChips
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun FilterChips(
    selected: InventoryFilter,
    onSelect: (InventoryFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(InventoryFilter.entries) { filter ->
            val labelResId = when (filter) {
                InventoryFilter.ALL -> R.string.inventory_filter_all
                InventoryFilter.EXPIRING -> R.string.inventory_filter_expiring
                InventoryFilter.EXPIRED -> R.string.inventory_filter_expired
                InventoryFilter.FRESH -> R.string.inventory_filter_fresh
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
//  FoodList
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun FoodList(
    items: List<Pair<FoodItem, FoodStatus>>,
    onDelete: (FoodItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items, key = { it.first.id }) { (item, status) ->
            FoodCard(
                item = item,
                status = status,
                onDelete = { onDelete(item) },
            )
        }
        // مساحة للـ bottom bar
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  FoodCard — بطاقة الطعام
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun FoodCard(
    item: FoodItem,
    status: FoodStatus,
    onDelete: () -> Unit,
) {
    // اختيار التدرّج اللوني حسب الحالة
    val gradient = when (status) {
        FoodStatus.FRESH -> freshGradient
        FoodStatus.SOON -> soonGradient
        FoodStatus.EXPIRED -> expiredGradient
    }

    // لون البادج
    val statusColor = when (status) {
        FoodStatus.FRESH -> Color(0xFF4CAF50)
        FoodStatus.SOON -> Color(0xFFFFA726)
        FoodStatus.EXPIRED -> Color(0xFFEF5350)
    }

    val statusText = when (status) {
        FoodStatus.FRESH -> "طازج"
        FoodStatus.SOON -> "قريب"
        FoodStatus.EXPIRED -> "منتهي"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = statusColor.copy(alpha = 0.3f)
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ─── أيقونة الفئة (دائرة متدرّجة) ───
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(gradient),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.toCategory().icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ─── الاسم + الكمية + التاريخ ───
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // الاسم
                Text(
                    text = stringResource(item.foodLabelResId),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // الكمية + المكان
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // الكمية
                    Text(
                        text = "${item.quantity} ${stringResource(item.toUnit().symbolResId)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    // فاصل
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    // المكان
                    val location = item.toLocation()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = location.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = stringResource(location.labelResId),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // تاريخ الانتهاء
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Text(
                    text = "⏰ ${dateFormat.format(Date(item.expiryTimestamp))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            // ─── بادج الحالة ───
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // ─── زر الحذف ───
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp),
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
            // أيقونة كبيرة
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Kitchen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp),
                )
            }

            // النص
            Text(
                text = stringResource(R.string.inventory_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            // زر الإضافة
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        )
                    ))
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
                        text = stringResource(R.string.tab_add),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
