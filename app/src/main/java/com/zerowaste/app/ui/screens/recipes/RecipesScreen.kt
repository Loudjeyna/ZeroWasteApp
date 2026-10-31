package com.zerowaste.app.ui.screens.recipes

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.zerowaste.app.data.model.RecipeMatchResult
import com.zerowaste.app.data.model.RecipeMatchStatus

/**
 * RecipesScreen — شاشة الوصفات الكاملة
 *
 * - بحث بالاسم العربي
 * - فلتر (الكل / ما يمكنني طبخه الآن / ناقصة)
 * - بطاقة لكل وصفة مع:
 *   - إيموجي + اسم + وصف
 *   - مدة + عدد أشخاص
 *   - بادج الحالة (🟢🟡🔴)
 *   - نسبة المطابقة (5/7)
 *   - قائمة المكوّنات الناقصة (إن وجدت)
 *   - زر "أضف الناقص لقائمة التسوق"
 */
@Composable
fun RecipesScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipesViewModel = hiltViewModel(),
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
            RecipesHeader(
                totalCount = state.totalCount,
                cookableCount = state.cookableCount,
                partialCount = state.partialCount,
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

            // ─────────── قائمة الوصفات ───────────
            RecipesList(
                matches = state.recipes,
                onAddMissing = viewModel::addMissingToShopping,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Header — العنوان + 3 عدّادات
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun RecipesHeader(
    totalCount: Int,
    cookableCount: Int,
    partialCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.recipes_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatCard(
                label = "${stringResource(R.string.recipes_title)}: $totalCount",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "🟢 $cookableCount",
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "🟡 $partialCount",
                color = Color(0xFFFFA726),
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
        placeholder = { Text(text = stringResource(R.string.recipes_search_hint)) },
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
    selected: RecipeFilter,
    onSelect: (RecipeFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(RecipeFilter.entries) { filter ->
            val labelResId = when (filter) {
                RecipeFilter.ALL -> R.string.recipes_filter_all
                RecipeFilter.COOKABLE_NOW -> R.string.recipes_filter_cookable
                RecipeFilter.PARTIAL -> R.string.recipes_filter_partial
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
//  RecipesList
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun RecipesList(
    matches: List<RecipeMatchResult>,
    onAddMissing: (RecipeMatchResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (matches.isEmpty()) {
        EmptyRecipesState(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(matches, key = { it.recipe.id }) { match ->
                RecipeCard(
                    match = match,
                    onAddMissing = { onAddMissing(match) },
                )
            }
            // مساحة للـ bottom bar
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  RecipeCard — بطاقة الوصفة
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun RecipeCard(
    match: RecipeMatchResult,
    onAddMissing: () -> Unit,
) {
    val recipe = match.recipe
    val statusColor = when (match.matchStatus) {
        RecipeMatchStatus.FULL -> Color(0xFF4CAF50)
        RecipeMatchStatus.PARTIAL -> Color(0xFFFFA726)
        RecipeMatchStatus.NONE -> Color(0xFFEF5350)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // ─── الصف العلوي: إيموجي + اسم + بادج الحالة ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // إيموجي كبير
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(recipe.categoryColorArgb).copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = recipe.emoji,
                        fontSize = 32.sp,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // الاسم + الوصف
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(recipe.nameResId),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(recipe.descriptionResId),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // بادج الحالة
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = match.matchStatus.emoji + " " + match.matchLabel,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // ─── الصف الثاني: مدة + عدد أشخاص ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.durationMinutes} ${stringResource(R.string.minutes)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.servings} ${stringResource(R.string.people)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // نسبة المطابقة
                Text(
                    text = "${match.matchPercentage}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                )
            }

            // ─── المكوّنات الناقصة (إن وجدت) ───
            if (match.missingIngredients.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    text = stringResource(R.string.recipes_missing_ingredients),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
                // قائمة المكوّنات الناقصة
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    match.missingIngredients.take(4).forEach { ingredient ->
                        MissingIngredientChip(
                            emoji = ingredient.emoji,
                            name = stringResource(ingredient.nameResId),
                        )
                    }
                    if (match.missingIngredients.size > 4) {
                        Text(
                            text = "+${match.missingIngredients.size - 4}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // زر "أضف الناقص لقائمة التسوق"
                TextButton(
                    onClick = onAddMissing,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.recipes_add_to_shopping),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            } else {
                // مطابقة كاملة - رسالة احتفال
                Text(
                    text = "✅ ${stringResource(R.string.recipes_match_full)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun MissingIngredientChip(
    emoji: String,
    name: String,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
            .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  EmptyState
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun EmptyRecipesState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = "🍽️", fontSize = 64.sp)
            Text(
                text = "لا توجد وصفات مطابقة",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "جرّب تغيير الفلتر أو أضف طعاماً للمخزون",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
