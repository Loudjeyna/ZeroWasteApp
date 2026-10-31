package com.zerowaste.app.ui.screens.inventory

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.zerowaste.app.data.model.FoodExpiryStatus
import com.zerowaste.app.data.model.FoodItem
import com.zerowaste.app.data.model.FoodItemStatus
import com.zerowaste.app.ui.theme.expiredGradient
import com.zerowaste.app.ui.theme.freshGradient
import com.zerowaste.app.ui.theme.soonGradient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * InventoryScreen — شاشة المخزون الكاملة (محدّثة D6)
 *
 * ✅ بطاقات قابلة للضغط → تفتح FoodActionBottomSheet
 * ✅ بادج الحالة على كل بطاقة (متاح/قيد الاستخدام/منتهي/إلخ)
 * ✅ عرض الكمية المتبقية/الأصلية (مثل: 3/5)
 * ✅ فلتر HISTORY لعرض السجل
 */
@Composable
fun InventoryScreen(
    modifier: Modifier = Modifier,
    onAddFoodClick: () -> Unit = {},
    viewModel: InventoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    // ✅ العنصر المُحدَّد لفتح الـ Bottom Sheet
    var selectedItem by remember { mutableStateOf<Pair<FoodItem, FoodExpiryStatus>?>(null) }

    // ✅ بانر التلميح: يظهر في الجلسة الحالية حتى يضغط المستخدم على أي بطاقة
    // rememberSaveable يحافظ عليه عبر تغيير orientation
    var showHint by rememberSaveable { mutableStateOf(true) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // العنوان + عدّادات
            InventoryHeader(
                totalCount = state.totalCount,
                activeCount = state.activeCount,
                expiredCount = state.expiredCount,
            )

            // ✅ بانر التلميح — يظهر فقط إذا:
            //   1) showHint = true (لم يضغط على بطاقة بعد)
            //   2) المخزون غير فارغ (لا فائدة من التلميح إن لم توجد بطاقات)
            if (showHint && !state.isEmpty) {
                HintBanner(onDismiss = { showHint = false })
            }

            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchChanged,
            )

            FilterChips(
                selected = state.filter,
                onSelect = viewModel::onFilterChanged,
            )

            if (state.isEmpty) {
                EmptyState(onAddClick = onAddFoodClick)
            } else {
                FoodList(
                    items = state.items,
                    onItemClick = { item, expiryStatus ->
                        // ✅ أول ضغطة → إخفاء البانر
                        showHint = false
                        selectedItem = item to expiryStatus
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    // ✅ عرض الـ Bottom Sheet عند تحديد عنصر
    selectedItem?.let { (item, expiryStatus) ->
        FoodActionBottomSheet(
            foodItem = item,
            expiryStatus = expiryStatus,
            onUse = { amount -> viewModel.useAmount(item, amount) },
            onFinish = { viewModel.finish(item) },
            onDiscard = { viewModel.discard(item) },
            onEdit = { qty, expiry, loc -> viewModel.edit(item, qty, expiry, loc) },
            onRepurchase = { viewModel.repurchase(item) },
            onDelete = { viewModel.delete(item) },
            onDismiss = { selectedItem = null },
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  HintBanner — بانر تلميح مؤقت يختفي عند أول ضغطة
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun HintBanner(onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 💡 إيموجي الإضاءة
            Text(
                text = "💡",
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            // النص
            Text(
                text = stringResource(R.string.hint_tap_card),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            // زر الإغلاق ×
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.action_close),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Header
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun InventoryHeader(
    totalCount: Int,
    activeCount: Int,
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
                label = "🟢 $activeCount",
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "⏰ $expiredCount",
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
        placeholder = { Text(text = stringResource(R.string.inventory_search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
    )
}

// ═══════════════════════════════════════════════════════════════════
//  FilterChips — محدّث (D4)
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
                InventoryFilter.HISTORY -> R.string.inventory_filter_history
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
    items: List<Pair<FoodItem, FoodExpiryStatus>>,
    onItemClick: (FoodItem, FoodExpiryStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items, key = { it.first.id }) { (item, expiryStatus) ->
            FoodCard(
                item = item,
                expiryStatus = expiryStatus,
                onClick = { onItemClick(item, expiryStatus) },
            )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  FoodCard — محدّثة (D6)
// ✅ قابلة للضغط
// ✅ بادج الحالة (متاح/قيد الاستخدام/منتهي/إلخ)
// ✅ عرض الكمية المتبقية/الأصلية
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun FoodCard(
    item: FoodItem,
    expiryStatus: FoodExpiryStatus,
    onClick: () -> Unit,
) {
    val status = item.toStatus()
    val category = item.toCategory()
    val location = item.toLocation()
    val unit = item.toUnit()

    // التدرّج اللوني حسب حالة الصلاحية (للأيقونة)
    val gradient = when (expiryStatus) {
        FoodExpiryStatus.FRESH -> freshGradient
        FoodExpiryStatus.SOON -> soonGradient
        FoodExpiryStatus.EXPIRED -> expiredGradient
    }

    // لون الحالة (للبادج)
    val statusColor = Color(status.colorArgb)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),  // ✅ قابل للضغط
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
            // أيقونة الفئة (دائرة متدرّجة)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(gradient),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = category.emoji,
                    fontSize = 24.sp,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

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

                // الكمية المتبقية / الأصلية + المكان
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // ✅ عرض الكمية المتبقية/الأصلية
                    Text(
                        text = "${item.remainingQuantity}/${item.quantity} ${stringResource(unit.symbolResId)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    // المكان
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = location.emoji,
                            fontSize = 12.sp,
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

            // ✅ بادج الحالة (متاح/قيد الاستخدام/منتهي/إلخ)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = status.emoji,
                    fontSize = 14.sp,
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
                    imageVector = Icons.Filled.Kitchen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp),
                )
            }

            Text(
                text = stringResource(R.string.inventory_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

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
