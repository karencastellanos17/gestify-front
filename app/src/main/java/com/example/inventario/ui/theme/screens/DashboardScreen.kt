package com.example.inventario.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.StatCard
import com.example.inventario.ui.theme.components.WaveFooter
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
    onLogout: () -> Unit = {},
    onGoToSales: () -> Unit = {},
    onGoToStock: () -> Unit = {},
    onGoToAlerts: () -> Unit = {},
    viewModel: GestifyViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val products by viewModel.products.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val supplierOrders by viewModel.supplierOrders.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    val activeProducts = products.count { it.isActive }
    val lowStockCount = products.count { it.isActive && it.stock < 5 }
    val totalInventoryValue = products.filter { it.isActive }.sumOf { it.price * it.stock }

    val totalSales = sales.sumOf { it.total }
    val totalSupplierCosts = supplierOrders.sumOf { it.totalCost }
    val totalManualExpenses = expenses.sumOf { it.amount }
    val totalOutflows = totalSupplierCosts + totalManualExpenses
    val netProfit = totalSales - totalOutflows

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ModuleBanner(
            title = "Panel Principal",
            subtitle = "Resumen financiero, control de inventario y acceso rápido a tus módulos.",
            icon = Icons.Default.Inventory2
        )

        // Balance General Consolidado
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box {
                WaveFooter(
                    modifier = Modifier.fillMaxWidth().height(90.dp).align(Alignment.BottomCenter),
                    color = colors.primary.copy(alpha = 0.05f)
                )
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Balance General de la Tienda", style = MaterialTheme.typography.titleLarge, color = colors.primary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ingresos Totales (Ventas):", style = MaterialTheme.typography.bodyMedium)
                        Text("+$${String.format(Locale.US, "%,.0f", totalSales)}", fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Egresos Proveedores:", style = MaterialTheme.typography.bodyMedium)
                        Text("-$${String.format(Locale.US, "%,.0f", totalSupplierCosts)}", fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gastos Operativos / Tienda:", style = MaterialTheme.typography.bodyMedium)
                        Text("-$${String.format(Locale.US, "%,.0f", totalManualExpenses)}", fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                    }

                    HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.4f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Utilidad Neta Actual:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "$${String.format(Locale.US, "%,.0f", netProfit)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (netProfit >= 0) Color(0xFF15803D) else Color(0xFFB91C1C)
                        )
                    }
                }
            }
        }

        // Estadísticas Rápidas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                label = "Productos Activos",
                value = "$activeProducts",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Stock Bajo",
                value = "$lowStockCount",
                icon = Icons.Default.Warning,
                accent = colors.error,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Valor Inventario",
                value = "$${String.format(Locale.US, "%,.0f", totalInventoryValue)}",
                icon = Icons.Default.Payments,
                modifier = Modifier.weight(1f)
            )
        }

        // Accesos directos a módulos clave
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Accesos Directos", style = MaterialTheme.typography.titleLarge, color = colors.primary)

                Button(
                    onClick = onGoToSales,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ir al Módulo de Ventas y Caja", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }

                Button(
                    onClick = onGoToStock,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.secondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gestionar Inventario y Stock", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}