package com.zerowaste.app.ui.screens.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zerowaste.app.R
import com.zerowaste.app.data.repository.AppLanguage
import com.zerowaste.app.data.repository.AppThemeMode

/**
 * SettingsScreen — شاشة الإعدادات الكاملة (مُحدَّثة)
 *
 * ✅ معلومات التطبيق في الأعلى
 * ✅ كل قسم في Card ملوّن
 * ✅ dialogs للّغة والثيم والأيام
 * ✅ وقت التذكير بأرقام بسيطة
 * ✅ حذف Material You toggle
 * ✅ recreate عند تبديل اللغة/الثيم
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showDaysDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm1 by remember { mutableStateOf(false) }
    var showDeleteConfirm2 by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ─── معلومات التطبيق في الأعلى ───
            AppInfoHeader()

            // ─── 1) عام (Card أخضر) ───
            SettingsCard(
                title = stringResource(R.string.settings_section_general),
                headerColor = MaterialTheme.colorScheme.primary,
            ) {
                SettingsRow(
                    icon = Icons.Filled.Language,
                    title = stringResource(R.string.settings_language),
                    subtitle = stringResource(state.language.labelResId),
                    onClick = { showLanguageDialog = true },
                )
            }

            // ─── 2) المظهر (Card تركوازي) ───
            SettingsCard(
                title = stringResource(R.string.settings_section_appearance),
                headerColor = MaterialTheme.colorScheme.secondary,
            ) {
                SettingsRow(
                    icon = Icons.Filled.Brightness6,
                    title = stringResource(R.string.settings_theme),
                    subtitle = stringResource(state.themeMode.labelResId),
                    onClick = { showThemeDialog = true },
                )
            }

            // ─── 3) التنبيهات (Card برتقالي) ───
            SettingsCard(
                title = stringResource(R.string.settings_section_notifications),
                headerColor = MaterialTheme.colorScheme.tertiary,
            ) {
                SettingsSwitchRow(
                    icon = Icons.Filled.Notifications,
                    title = stringResource(R.string.settings_notifications_enabled),
                    isChecked = state.areNotificationsEnabled,
                    onCheckedChange = viewModel::setNotificationsEnabled,
                )
                SettingsRow(
                    icon = Icons.Filled.Notifications,
                    title = stringResource(R.string.settings_notify_days_before),
                    subtitle = "${state.notifyDaysBefore} ${stringResource(R.string.days)}",
                    onClick = { showDaysDialog = true },
                    enabled = state.areNotificationsEnabled,
                )
                SettingsRow(
                    icon = Icons.Filled.Schedule,
                    title = stringResource(R.string.settings_notify_time),
                    subtitle = String.format(java.util.Locale.US, "%02d:%02d", state.dailyReminderTime.first, state.dailyReminderTime.second),
                    onClick = { showTimeDialog = true },
                    enabled = state.areNotificationsEnabled,
                )
                SettingsSwitchRow(
                    icon = Icons.Filled.Vibration,
                    title = stringResource(R.string.settings_notify_vibration),
                    isChecked = state.isVibrationEnabled,
                    onCheckedChange = viewModel::setVibrationEnabled,
                    enabled = state.areNotificationsEnabled,
                )
            }

            // ─── 4) حول التطبيق (Card رمادي) ───
            SettingsCard(
                title = stringResource(R.string.settings_section_about),
                headerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                SettingsRow(
                    icon = Icons.Filled.Info,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    title = stringResource(R.string.settings_about_version),
                    subtitle = "1.0.0",
                    onClick = {},
                    showExpandIcon = false,  // ✅ معلومة فقط
                )
                SettingsRow(
                    icon = Icons.Filled.Star,
                    iconTint = Color(0xFFFFA726),
                    title = stringResource(R.string.settings_about_rate),
                    onClick = {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.zerowaste.app"))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    },
                    showExpandIcon = false,  // ✅ إجراء مباشر
                )
                SettingsRow(
                    icon = Icons.Filled.Share,
                    title = stringResource(R.string.settings_about_share),
                    onClick = {
                        context.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "جرّب تطبيق ZeroWaste!")
                                },
                                "مشاركة"
                            )
                        )
                    },
                    showExpandIcon = false,  // ✅ إجراء مباشر
                )
            }

            // ─── 5) Feedback (Card أخضر) ───
            SettingsCard(
                title = stringResource(R.string.settings_section_feedback),
                headerColor = MaterialTheme.colorScheme.primary,
            ) {
                SettingsRow(
                    icon = Icons.Filled.Email,
                    title = stringResource(R.string.settings_feedback),
                    subtitle = stringResource(R.string.settings_feedback_desc),
                    onClick = {
                        context.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:feedback@zerowaste.app")
                                    putExtra(Intent.EXTRA_SUBJECT, "ملاحظات ZeroWaste")
                                },
                                "إرسال"
                            )
                        )
                    },
                    showExpandIcon = false,  // ✅ إجراء مباشر
                )
            }

            // ─── 6) منطقة الخطر (Card أحمر) ───
            SettingsCard(
                title = stringResource(R.string.settings_section_danger),
                headerColor = MaterialTheme.colorScheme.error,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.settings_danger_warning),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                    )
                    Button(
                        onClick = { showDeleteConfirm1 = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.settings_delete_all), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // ═════════════ Dialogs ═════════════

    if (showLanguageDialog) {
        SelectionDialog(
            title = stringResource(R.string.settings_language),
            options = AppLanguage.values().map { it to stringResource(it.labelResId) },
            selectedCode = state.language.code,
            onSelect = {
                viewModel.setLanguage(it as AppLanguage)
                // ✅ recreate لتطبيق اللغة فوراً
                (context as? Activity)?.recreate()
            },
            onDismiss = { showLanguageDialog = false },
        )
    }

    if (showThemeDialog) {
        SelectionDialog(
            title = stringResource(R.string.settings_theme),
            options = AppThemeMode.values().map { it to stringResource(it.labelResId) },
            selectedCode = state.themeMode.code,
            onSelect = {
                viewModel.setThemeMode(it as AppThemeMode)
                (context as? Activity)?.recreate()
            },
            onDismiss = { showThemeDialog = false },
        )
    }

    if (showDaysDialog) {
        val daysOptions = listOf(1, 2, 3, 5, 7, 14)
        AlertDialog(
            onDismissRequest = { showDaysDialog = false },
            title = { Text(stringResource(R.string.settings_notify_days_before)) },
            text = {
                Column {
                    daysOptions.forEach { days ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setNotifyDaysBefore(days)
                                    showDaysDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "$days ${stringResource(R.string.days)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (days == state.notifyDaysBefore)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (days == state.notifyDaysBefore)
                                    FontWeight.Bold else FontWeight.Normal,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (days == state.notifyDaysBefore) {
                                Text("✓", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDaysDialog = false }) {
                    Text(stringResource(R.string.action_close))
                }
            },
        )
    }

    // ✅ وقت التذكير بأرقام بسيطة (ساعات + دقائق)
    if (showTimeDialog) {
        SimpleTimePickerDialog(
            currentHour = state.dailyReminderTime.first,
            currentMinute = state.dailyReminderTime.second,
            onSelect = { hour, minute ->
                viewModel.setDailyReminderTime(hour, minute)
            },
            onDismiss = { showTimeDialog = false },
        )
    }

    // تأكيد حذف (مرحلتان)
    if (showDeleteConfirm1) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm1 = false },
            title = { Text(stringResource(R.string.confirm_delete_all_title_1)) },
            text = { Text(stringResource(R.string.confirm_delete_all_message_1)) },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm1 = false; showDeleteConfirm2 = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.continue_anyway), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm1 = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
    if (showDeleteConfirm2) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm2 = false },
            title = { Text(stringResource(R.string.confirm_delete_all_title_2)) },
            text = {
                Text(
                    stringResource(R.string.confirm_delete_all_message_2),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm2 = false; viewModel.deleteAllData() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.settings_delete_all), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm2 = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  AppInfoHeader — معلومات التطبيق (مستطيل صغير بجملة واحدة)
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun AppInfoHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "🌱",
                fontSize = 24.sp,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  SettingsCard — Card ملوّن لكل قسم
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SettingsCard(
    title: String,
    headerColor: Color,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = headerColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, headerColor.copy(alpha = 0.2f)
            ),
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
            ) {
                content()
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  SimpleTimePickerDialog — اختيار الوقت بأرقام بسيطة
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SimpleTimePickerDialog(
    currentHour: Int,
    currentMinute: Int,
    onSelect: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedHour by remember { mutableStateOf(currentHour) }
    var selectedMinute by remember { mutableStateOf(currentMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_notify_time)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // عرض الوقت الحالي
                Text(
                    text = String.format(java.util.Locale.US, "%02d:%02d", selectedHour, selectedMinute),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                // اختيار الساعة
                Text(
                    text = "الساعة:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val hours = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23)
                SimpleNumberRow(
                    numbers = hours,
                    selected = selectedHour,
                    onSelect = { selectedHour = it },
                )

                Spacer(modifier = Modifier.height(8.dp))

                // اختيار الدقائق
                Text(
                    text = "الدقائق:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val minutes = listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)
                SimpleNumberRow(
                    numbers = minutes,
                    selected = selectedMinute,
                    onSelect = { selectedMinute = it },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSelect(selectedHour, selectedMinute)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.action_confirm), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

@Composable
private fun SimpleNumberRow(
    numbers: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    // عرض الأرقام في صفوف (6 في كل صف)
    numbers.chunked(6).forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            row.forEach { num ->
                val isSelected = num == selected
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .clickable { onSelect(num) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = String.format(java.util.Locale.US, "%d", num),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  SelectionDialog — dialog عام للاختيار
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun <T> SelectionDialog(
    title: String,
    options: List<Pair<T, String>>,
    selectedCode: String,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { (value, label) ->
                    val code = when (value) {
                        is AppLanguage -> value.code
                        is AppThemeMode -> value.code
                        else -> ""
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(value)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (code == selectedCode)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (code == selectedCode)
                                FontWeight.Bold else FontWeight.Normal,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (code == selectedCode) {
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        },
    )
}

// ═══════════════════════════════════════════════════════════════════
//  SettingsRow + SettingsSwitchRow
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    showExpandIcon: Boolean = true,  // ✅ جديد: تحكم في إظهار أيقونة ↓
) {
    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null,
             tint = if (enabled) iconTint else iconTint.copy(alpha = 0.5f),
             modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge,
                 color = textColor, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        // ✅ فقط إذا showExpandIcon = true
        if (showExpandIcon) {
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    iconTint: Color = MaterialTheme.colorScheme.primary,
) {
    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null,
             tint = if (enabled) iconTint else iconTint.copy(alpha = 0.5f),
             modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge,
                 color = textColor, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(checked = isChecked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}
