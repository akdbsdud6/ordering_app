package com.example.orderingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import androidx.compose.ui.tooling.preview.Preview
import com.example.orderingapp.ui.theme.Ordering_appTheme

// Temporary model for a barebones cart screen (replace later with your real models)
data class CartLineItem(
    val name: String,
    val unitPrice: Double,
    val quantity: Int
)

@Composable
fun CartScreen(
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Hardcoded test items for now
    val items = remember {
        mutableStateListOf(
            CartLineItem("Pho Tai", 12.99, 1),
            CartLineItem("Spring Rolls", 6.50, 2),
            CartLineItem("Thai Iced Tea", 4.75, 1)
        )
    }

    val currency = remember { NumberFormat.getCurrencyInstance() }
    val subtotal = items.sumOf { it.unitPrice * it.quantity }
    val taxRate = 0.085 // fake tax for now
    val tax = subtotal * taxRate
    val total = subtotal + tax

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Cart",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        // Cart list
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cart is empty")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items) { item ->
                        CartItemRow(item = item, currency = currency)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Summary
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                SummaryRow("Subtotal", currency.format(subtotal))
                SummaryRow("Tax (8.5%)", currency.format(tax))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SummaryRow("Total", currency.format(total), bold = true)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Place order
        Button(
            onClick = onPlaceOrder,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = items.isNotEmpty()
        ) {
            Text("Place Order")
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartLineItem,
    currency: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${currency.format(item.unitPrice)} × ${item.quantity}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = currency.format(item.unitPrice * item.quantity),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    bold: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true, widthDp = 1000, heightDp = 600)
@Composable
private fun CartScreenPreview() {
    Ordering_appTheme {
        CartScreen(onPlaceOrder = {})
    }
}

