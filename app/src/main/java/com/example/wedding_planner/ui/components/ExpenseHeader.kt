package com.example.wedding_planner.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpenseHeader(
    limit: Double,
    spent: Double,
    paid: Double,
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchModeChange: (Boolean) -> Unit,
    onShowLimitDialog: () -> Unit
) {
    AnimatedContent(
        targetState = isSearchActive,
        label = "HeaderTransition"
    ) { active ->
        if (active) {
            
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onClose = {
                    onSearchQueryChange("")
                    onSearchModeChange(false)
                }
            )
        } else {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FINANCEIRO",
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row {
                        IconButton(onClick = { onSearchModeChange(true) }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar Despesa",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(onClick = onShowLimitDialog) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = "Definir Orçamento",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                BudgetDashboard(
                    limit = limit,
                    spent = spent,
                    paid = paid
                )
            }
        }
    }
}