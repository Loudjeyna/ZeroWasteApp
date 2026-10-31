package com.zerowaste.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerowaste.app.ui.navigation.BottomTab
import com.zerowaste.app.ui.theme.bottomBarGradient
import com.zerowaste.app.ui.theme.fabGradient

/**
 * ZeroWasteBottomBar — شريط التنقل السفلي المخصص
 *
 * تصميم:
 *   ┌──────┬──────┬──────┬──────┬──────┐
 *   │ مخزون │ وصفات│ (FAB) │ تسوق │ إعدادات│
 *   └──────┴──────┴──────┴──────┴──────┘
 *                ↑
 *            زر دائري مرتفع
 *
 * - الزر الأوسط (FAB) يبرز 24dp فوق الشريط
 * - تدرّج لوني في الخلفية
 * - بادج على كل تبويب (عدد الأشياء)
 * - حركة scale عند التحديد
 */
@Composable
fun ZeroWasteBottomBar(
    currentRoute: String?,
    onTabSelected: (BottomTab) -> Unit,
    badges: Map<BottomTab, Int> = emptyMap(),
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        shadowElevation = 12.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(
                    brush = bottomBarGradient,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BottomTab.ordered.forEach { tab ->
                    val isSelected = currentRoute == tab.route

                    if (tab.isCenterFAB) {
                        // زر FAB في الوسط
                        CenterFab(
                            icon = tab.selectedIcon,
                            label = stringResource(tab.labelResId),
                            isSelected = isSelected,
                            onClick = { onTabSelected(tab) },
                            badgeCount = badges[tab] ?: 0,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        TabItem(
                            icon = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            label = stringResource(tab.labelResId),
                            isSelected = isSelected,
                            onClick = { onTabSelected(tab) },
                            badgeCount = badges[tab] ?: 0,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  TabItem — تبويب عادي
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun TabItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badgeCount: Int,
    modifier: Modifier = Modifier,
) {
    // حركة scale عند التحديد
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(),
        label = "tab_scale",
    )

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.18f)
                        else Color.Transparent
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) Color.White
                           else Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(22.dp),
                )
            }
            // البادج
            if (badgeCount > 0) {
                Badge(count = badgeCount)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) Color.White
                    else Color.White.copy(alpha = 0.75f),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  CenterFab — الزر الأوسط بتصميم FAB مخصص
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun CenterFab(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badgeCount: Int,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(),
        label = "fab_scale",
    )

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .offset(y = (-12).dp)
                    .size(52.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(fabGradient)
                    .border(width = 4.dp, color = Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
            // البادج على الـ FAB (مع إزاحة)
            if (badgeCount > 0) {
                Badge(
                    count = badgeCount,
                    modifier = Modifier.offset(x = (-8).dp, y = (-8).dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(0.dp))
        // لا نُظهر label للـ FAB (الأيقونة كافية)
        Text(
            text = label,
            color = if (isSelected) Color.White
                    else Color.White.copy(alpha = 0.85f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Badge — المؤشر الدائري على التبويب
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun Badge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Color(0xFFFF5252)) // أحمر بادج
            .border(width = 1.5.dp, color = Color.White, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count > 9) "9+" else count.toString(),
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
