package com.example.wedding_planner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wedding_planner.data.model.Expense
import com.example.wedding_planner.data.model.enums.BudgetCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    expenseToEdit: Expense?,
    onDismiss: () -> Unit,
    onConfirm: (Expense) -> Unit
) {
    var title by remember { mutableStateOf(expenseToEdit?.title ?: "") }
    var estimated by remember { mutableStateOf(expenseToEdit?.estimatedCost?.toString() ?: "") }
    var actual by remember { mutableStateOf(expenseToEdit?.actualCost?.toString() ?: "") }
    var paid by remember { mutableStateOf(expenseToEdit?.paidAmount?.toString() ?: "") }
    var category by remember { mutableStateOf(expenseToEdit?.category ?: BudgetCategory.GENERAL) }
    var categoryExpanded by remember { mutableStateOf(false) }

    fun isValidDecimalInput(input: String): Boolean {
        if (input.isEmpty()) return true
        
        return input.all { it.isDigit() || it == '.' || it == ',' } &&
                input.count { it == '.' || it == ',' } <= 1
    }

    WeddingBaseDialog(
        onDismiss = onDismiss,
        title = if (expenseToEdit == null) "Nova Despesa" else "Editar Despesa",
        icon = Icons.Default.AttachMoney
    ) {
        Column {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = actual,
                    onValueChange = {
                        if (isValidDecimalInput(it)) actual = it
                    },
                    label = { Text("Valor Final") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal, 
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                )
                OutlinedTextField(
                    value = paid,
                    onValueChange = {
                        if (isValidDecimalInput(it)) paid = it
                    },
                    label = { Text("Valor Pago") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal, 
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor(
                        MenuAnchorType.PrimaryEditable, true
                    ).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    BudgetCategory.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.label) },
                            onClick = { category = cat; categoryExpanded = false }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val finalEstimated = estimated.replace(',', '.').toDoubleOrNull() ?: 0.0
                    val finalActual = actual.replace(',', '.').toDoubleOrNull() ?: 0.0
                    val finalPaid = paid.replace(',', '.').toDoubleOrNull() ?: 0.0

                    val newExpense = (expenseToEdit ?: Expense()).copy(
                        title = title,
                        estimatedCost = finalEstimated,
                        actualCost = finalActual,
                        paidAmount = finalPaid,
                        category = category
                    )
                    onConfirm(newExpense)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Salvar")
            }
        }
    }
}