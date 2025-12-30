package com.example.wedding_planner.ui.screen.budget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wedding_planner.data.model.Expense
import com.example.wedding_planner.ui.components.AddExpenseDialog
import com.example.wedding_planner.ui.components.ExpenseHeader
import com.example.wedding_planner.ui.components.ExpenseItem
import com.example.wedding_planner.ui.components.HomeBackground
import com.example.wedding_planner.ui.components.OrgInputDialog
import com.example.wedding_planner.ui.components.WeddingConfirmationDialog

@Composable
fun BudgetScreenRoute(viewModel: BudgetViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BudgetScreen(uiState = state, onEvent = viewModel::onEvent)
}

@Composable
fun BudgetScreen(uiState: BudgetUiState, onEvent: (BudgetUiEvent) -> Unit) {
    var showExpenseDialog by remember { mutableStateOf(false) }
    var showLimitDialog by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredExpenses = remember(uiState.expenses, uiState.searchQuery) {
        if (uiState.searchQuery.isBlank()) uiState.expenses
        else uiState.expenses.filter { it.title.contains(uiState.searchQuery, ignoreCase = true) }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (!isSearchActive) {
                FloatingActionButton(
                    onClick = {
                        expenseToEdit = null
                        showExpenseDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { padding ->
        HomeBackground(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                ExpenseHeader(
                    limit = uiState.budgetLimit,
                    spent = uiState.totalSpent,
                    paid = uiState.totalPaid,
                    searchQuery = uiState.searchQuery,
                    isSearchActive = isSearchActive,
                    onSearchQueryChange = { query ->
                        onEvent(BudgetUiEvent.UpdateSearchQuery(query))
                    },
                    onSearchModeChange = { isActive ->
                        isSearchActive = isActive
                        if (!isActive) onEvent(BudgetUiEvent.UpdateSearchQuery(""))
                    },
                    onShowLimitDialog = { showLimitDialog = true }
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (filteredExpenses.isEmpty() && !uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Nenhuma despesa registrada.",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp)
                    ) {
                        val grouped = filteredExpenses.groupBy { it.category }
                        grouped.forEach { (category, expenses) ->
                            item {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                            items(expenses, key = { it.id }) { expense ->
                                ExpenseItem(
                                    expense = expense,
                                    onClick = {
                                        expenseToEdit = expense
                                        showExpenseDialog = true
                                    },
                                    onDelete = { expenseToDelete = expense }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
        if (showExpenseDialog) {
            AddExpenseDialog(
                expenseToEdit = expenseToEdit,
                onDismiss = { showExpenseDialog = false },
                onConfirm = { onEvent(BudgetUiEvent.SaveExpense(it)); showExpenseDialog = false }
            )
        }
        if (showLimitDialog) {
            OrgInputDialog(
                title = "Definir Orçamento Total",
                label = "Valor total disponível (R$)",
                confirmText = "Salvar",
                icon = Icons.Default.AttachMoney,
                keyboardType = KeyboardType.Decimal,
                onDismiss = { showLimitDialog = false },
                onConfirm = { inputString ->
                    val sanitizedInput = inputString.replace(",", ".")
                    val value = sanitizedInput.toDoubleOrNull() ?: 0.0
                    onEvent(BudgetUiEvent.UpdateBudgetLimit(value))
                    showLimitDialog = false
                }
            )
        }
        if (expenseToDelete != null) {
            WeddingConfirmationDialog(
                title = "Excluir Despesa?",
                message = "Remover \"${expenseToDelete?.title}\"?",
                isDestructive = true,
                onDismiss = { expenseToDelete = null },
                onConfirm = {
                    expenseToDelete?.let { onEvent(BudgetUiEvent.DeleteExpense(it.id)) }
                    expenseToDelete = null
                }
            )
        }
    }
}