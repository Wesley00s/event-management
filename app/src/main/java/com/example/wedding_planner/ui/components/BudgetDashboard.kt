package com.example.wedding_planner.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetDashboard(limit: Double, spent: Double, paid: Double) {
    val remaining = limit - spent
    val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Saldo Restante (Orçamento)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            Text(
                text = format.format(remaining),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (remaining >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            BudgetProgressBar("Comprometido", spent, limit, MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(12.dp))
            BudgetProgressBar("Pago Efetivado", paid, spent, MaterialTheme.colorScheme.tertiary)
        }
    }
}