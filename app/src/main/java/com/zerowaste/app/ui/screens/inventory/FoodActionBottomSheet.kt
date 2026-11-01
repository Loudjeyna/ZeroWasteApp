package com.zerowaste.app.ui.screens.inventory

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerowaste.app.R
import com.zerowaste.app.data.model.FoodExpiryStatus
import com.zerowaste.app.data.model.FoodItem
import com.zerowaste.app.data.model.FoodItemStatus
import com.zerowaste.app.data.model.StorageLocation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.zerowaste.app.util.NumberUtils

/**
 * FoodActionBottomSheet — النافذة المنبثقة لإجراءات الطعام (محدّثة E2)
 *
 * 6 إجراءات في مجموعتين:
 *
 * ── الإجراءات الأساسية (للنشطة فقط) ──
 *   🥄 Use (استهلاك جزئي) → يفتح اختيار النسبة
 *   ✓ Finish (انتهى كاملاً)
 *   🗑️ Discard (تخلص منه)
 *
 * ── إجراءات إضافية (لكل العناصر) ──
 *   ✏️ Edit (تعديل) → يفتح قسم تعديل داخل الـ Bottom Sheet
 *   🛒 Repurchase (إعادة شراء) → ينشئ عنصراً جديداً بنفس الفئة
 *   ❌ Delete (حذف نهائي) → يفتح نافذة تأكيد
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodActionBottomSheet(
    foodItem: FoodItem,
    expiryStatus: FoodExpiryStatus,
    onUse: (Int) -> Unit,
    onFinish: () -> Unit,
    onDiscard: () -> Unit,
    onEdit: (Int, Long, String) -> Unit,    // (newQuantity, newExpiry, newLocationId)
    onRepurchase: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeSection by remember { mutableStateOf(BottomSheetSection.MAIN) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        BottomSheetContent(
            foodItem = foodItem,
            expiryStatus = expiryStatus,
            activeSection = activeSection,
            onSectionChange = { activeSection = it },
            onUse = { amount ->
                onUse(amount)
                onDismiss()
            },
            onFinish = {
                onFinish()
                onDismiss()
            },
            onDiscard = {
                onDiscard()
                onDismiss()
            },
            onEdit = { qty, expiry, loc ->
                onEdit(qty, expiry, loc)
                onDismiss()
            },
            onRepurchase = {
                onRepurchase()
                onDismiss()
            },
            onDeleteRequested = { showDeleteDialog = true },
        )
    }

    // ✅ نافذة تأكيد الحذف
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.confirm_delete_title)) },
            text = { Text(stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.action_delete), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

/**
 * أقسام الـ Bottom Sheet
 */
private enum class BottomSheetSection {
    MAIN,   // الإجراءات الأساسية + الإضافية
    USE,    // قسم اختيار كمية الاستهلاك
    EDIT,   // قسم تعديل بيانات الطعام
}

// ═══════════════════════════════════════════════════════════════════
//  المحتوى الكامل للـ Bottom Sheet
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun BottomSheetContent(
    foodItem: FoodItem,
    expiryStatus: FoodExpiryStatus,
    activeSection: BottomSheetSection,
    onSectionChange: (BottomSheetSection) -> Unit,
    onUse: (Int) -> Unit,
    onFinish: () -> Unit,
    onDiscard: () -> Unit,
    onEdit: (Int, Long, String) -> Unit,
    onRepurchase: () -> Unit,
    onDeleteRequested: () -> Unit,
) {
    val status = foodItem.toStatus()
    val category = foodItem.toCategory()
    val location = foodItem.toLocation()
    val unit = foodItem.toUnit()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ─── رأس البطاقة: إيموجي + اسم + بادج الحالة ───
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(category.colorArgb).copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = category.emoji, fontSize = 40.sp)
        }

        Text(
            text = stringResource(foodItem.foodLabelResId),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        StatusBadge(status = status)

        // ─── تفاصيل ───
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DetailRow(
                label = stringResource(R.string.quantity_remaining),
                value = "${NumberUtils.formatPair(foodItem.remainingQuantity, foodItem.quantity)} ${stringResource(unit.symbolResId)}",
            )
            DetailRow(
                label = stringResource(R.string.expiry_date),
                value = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                    .format(Date(foodItem.expiryTimestamp)),
            )
            DetailRow(
                label = stringResource(R.string.storage_location_label),
                value = "${location.emoji} ${stringResource(location.labelResId)}",
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // ─── المحتوى حسب القسم النشط ───
        when (activeSection) {
            BottomSheetSection.MAIN -> {
                MainActions(
                    status = status,
                    onUse = { onSectionChange(BottomSheetSection.USE) },
                    onFinish = onFinish,
                    onDiscard = onDiscard,
                    onEdit = { onSectionChange(BottomSheetSection.EDIT) },
                    onRepurchase = onRepurchase,
                    onDelete = onDeleteRequested,
                )
            }
            BottomSheetSection.USE -> {
                UseSection(
                    foodItem = foodItem,
                    onUse = onUse,
                    onBack = { onSectionChange(BottomSheetSection.MAIN) },
                )
            }
            BottomSheetSection.EDIT -> {
                EditSection(
                    foodItem = foodItem,
                    onEdit = onEdit,
                    onBack = { onSectionChange(BottomSheetSection.MAIN) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════
//  MainActions — 3 أزرار رئيسية + 3 أزرار إضافية
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun MainActions(
    status: FoodItemStatus,
    onUse: () -> Unit,
    onFinish: () -> Unit,
    onDiscard: () -> Unit,
    onEdit: () -> Unit,
    onRepurchase: () -> Unit,
    onDelete: () -> Unit,
) {
    // الإجراءات الأساسية تظهر فقط للعناصر النشطة (AVAILABLE + IN_USE)
    val showMainActions = status == FoodItemStatus.AVAILABLE || status == FoodItemStatus.IN_USE

    if (showMainActions) {
        Text(
            text = stringResource(R.string.section_main_actions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActionButton(
                icon = Icons.Filled.Restaurant,
                label = stringResource(R.string.action_use),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onUse,
            )
            ActionButton(
                icon = Icons.Filled.Check,
                label = stringResource(R.string.action_finish),
                backgroundColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                onClick = onFinish,
            )
            ActionButton(
                icon = Icons.Filled.Delete,
                label = stringResource(R.string.action_discard),
                backgroundColor = Color(0xFFEF5350),
                contentColor = Color.White,
                onClick = onDiscard,
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }

    // ─── إجراءات إضافية (تظهر لكل العناصر) ───
    Text(
        text = stringResource(R.string.section_extra_actions),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth(),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // ✏️ تعديل
        SecondaryActionButton(
            icon = Icons.Filled.Edit,
            label = stringResource(R.string.action_edit),
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f),
            onClick = onEdit,
        )
        // 🛒 إعادة شراء
        SecondaryActionButton(
            icon = Icons.Filled.ShoppingCart,
            label = stringResource(R.string.action_repurchase),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f),
            onClick = onRepurchase,
        )
        // ❌ حذف نهائي
        SecondaryActionButton(
            icon = Icons.Filled.Delete,
            label = stringResource(R.string.action_delete),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
            onClick = onDelete,
        )
    }
}

@Composable
private fun SecondaryActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  StatusBadge
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun StatusBadge(status: FoodItemStatus) {
    val color = Color(status.colorArgb)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = "${status.emoji} ${stringResource(status.labelResId)}",
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  DetailRow
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  ActionButton — زر رئيسي كبير
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  UseSection — اختيار كمية الاستهلاك
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun UseSection(
    foodItem: FoodItem,
    onUse: (Int) -> Unit,
    onBack: () -> Unit,
) {
    var amount by remember { mutableIntStateOf(1) }
    val remaining = foodItem.remainingQuantity
    val unit = foodItem.toUnit()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.choose_use_amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = "${NumberUtils.formatInt(amount)} ${stringResource(unit.symbolResId)}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        // نسب جاهزة
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val quarter = (foodItem.quantity + 3) / 4
            val half = (foodItem.quantity + 1) / 2
            val threeQuarters = (foodItem.quantity * 3 + 3) / 4
            val full = foodItem.quantity

            listOf(
                "¼" to quarter.coerceAtLeast(1),
                "½" to half.coerceAtLeast(1),
                "¾" to threeQuarters.coerceAtLeast(1),
                stringResource(R.string.use_full) to full,
            ).forEach { (label, value) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (amount == value) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .clickable { amount = value.coerceAtMost(remaining) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        color = if (amount == value) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }

        // تحكّم دقيق
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { if (amount > 1) amount-- },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "−", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = NumberUtils.formatInt(amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { if (amount < remaining) amount++ },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "+", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Button(
            onClick = { onUse(amount) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = amount > 0 && amount <= remaining,
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.confirm_use),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = stringResource(R.string.back),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(onClick = onBack),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  EditSection — تعديل بيانات الطعam
//  ✅ تعديل: الكمية الأصلية + تاريخ الانتهاء + المكان
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun EditSection(
    foodItem: FoodItem,
    onEdit: (Int, Long, String) -> Unit,
    onBack: () -> Unit,
) {
    var quantity by remember { mutableIntStateOf(foodItem.quantity) }
    var selectedLocation by remember { mutableStateOf(foodItem.toLocation()) }
    var daysToAdd by remember { mutableIntStateOf(7) }  // افتراضي: أسبوع من اليوم

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.edit_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        // ─── الكمية الأصلية ───
        Text(
            text = stringResource(R.string.quantity_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // −1
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { if (quantity > 1) quantity-- },
                contentAlignment = Alignment.Center,
            ) {
                Text("−", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = NumberUtils.formatInt(quantity),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(16.dp))
            // +1
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { if (quantity < 999) quantity++ },
                contentAlignment = Alignment.Center,
            ) {
                Text("+", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // ─── تاريخ الانتهاء (شيبس أيام) ───
        Text(
            text = stringResource(R.string.expiry_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        val expiryOptions = listOf(0, 3, 7, 14, 30, 90, 180, 365)
        val expiryLabels = listOf(
            stringResource(R.string.expiry_today),
            stringResource(R.string.expiry_3days),
            stringResource(R.string.expiry_1week),
            stringResource(R.string.expiry_2weeks),
            stringResource(R.string.expiry_1month),
            stringResource(R.string.expiry_3months),
            stringResource(R.string.expiry_6months),
            stringResource(R.string.expiry_1year),
        )
        // نعرضها في صفّين
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            expiryOptions.chunked(4).forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEachIndexed { index, days ->
                        val label = expiryLabels[rowIndex * 4 + index]
                        val isSelected = daysToAdd == days
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .clickable { daysToAdd = days },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }

        // ─── المكان (شبكة 2×2) ───
        Text(
            text = stringResource(R.string.location_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StorageLocation.entries.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { location ->
                        val isSelected = location == selectedLocation
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) Color(location.colorArgb).copy(alpha = 0.18f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .border(
                                    if (isSelected) 2.dp else 0.dp,
                                    Color(location.colorArgb),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedLocation = location },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "${location.emoji} ${stringResource(location.labelResId)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color(location.colorArgb)
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }

        // ─── زر حفظ التعديل ───
        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                onEdit(quantity, calendar.timeInMillis, selectedLocation.name)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.save_edit),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = stringResource(R.string.back),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(onClick = onBack),
        )
    }
}
