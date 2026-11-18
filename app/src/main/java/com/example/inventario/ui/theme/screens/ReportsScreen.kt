package com.example.inventario.ui.theme.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.data.model.SupplierOrder
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.WaveFooter
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import java.io.File
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: GestifyViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val sales by viewModel.sales.collectAsState()
    val supplierOrders by viewModel.supplierOrders.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    val totalSales = sales.sumOf { it.total }
    val totalSupplierCosts = supplierOrders.sumOf { it.totalCost }
    val totalExpenses = expenses.sumOf { it.amount }
    val totalOutflows = totalSupplierCosts + totalExpenses
    val netProfit = totalSales - totalOutflows

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ModuleBanner(
            title = "Reportes y Hojas de Cálculo",
            subtitle = "Consulta el consolidado de tu negocio y exporta la información en formato compatible con Excel.",
            icon = Icons.Default.InsertChart
        )

        // Resumen Financiero Rápido
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box {
                WaveFooter(
                    modifier = Modifier.fillMaxWidth().height(80.dp).align(Alignment.BottomCenter),
                    color = colors.primary.copy(alpha = 0.05f)
                )
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Consolidado Financiero Actual", style = MaterialTheme.typography.titleLarge, color = colors.primary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ingresos Totales (Ventas):", style = MaterialTheme.typography.bodyMedium)
                        Text("+$${String.format(Locale.US, "%,.0f", totalSales)}", fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Egresos Totales (Proveedores + Gastos):", style = MaterialTheme.typography.bodyMedium)
                        Text("-$${String.format(Locale.US, "%,.0f", totalOutflows)}", fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                    }
                    HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.4f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Utilidad Neta:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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

        // Tarjeta de Exportación de Datos
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Exportar Datos a Excel / CSV", style = MaterialTheme.typography.titleLarge, color = colors.primary)
                Text(
                    "Genera un archivo CSV con el historial completo de ventas, pedidos y gastos para abrir en Excel o Google Sheets.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )

                Button(
                    onClick = { exportMasterReportToCsv(context, sales, supplierOrders, expenses) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Descargar Reporte Consolidado (CSV)", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

private fun exportMasterReportToCsv(
    context: Context,
    sales: List<SaleItem>,
    orders: List<SupplierOrder>,
    expenses: List<com.example.inventario.data.model.Expense>
) {
    val csvContent = buildString {
        append("--- REPORTE DE VENTAS ---\n")
        append("ID,Producto,Cantidad,Total,Fecha\n")
        sales.forEach { append("${it.id},\"${it.productName}\",${it.quantity},${it.total},${it.date}\n") }

        append("\n--- REPORTE DE PEDIDOS A PROVEEDORES ---\n")
        append("ID,Proveedor,Costo Total,Estado\n")
        orders.forEach { append("${it.id},\"${it.supplierName}\",${it.totalCost},${it.status}\n") }

        append("\n--- REPORTE DE GASTOS OPERATIVOS ---\n")
        append("ID,Concepto,Monto,Fecha\n")
        expenses.forEach { append("${it.id},\"${it.concept}\",${it.amount},${it.date}\n") }
    }

    val file = File(context.cacheDir, "Reporte_Consolidado_Gestify.csv")
    file.writeText(csvContent)

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir reporte con:"))
}