package com.example.inventario.ui.theme.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.data.model.Expense
import com.example.inventario.data.model.SupplierOrder
import com.example.inventario.ui.theme.components.DialogSection
import com.example.inventario.ui.theme.components.EmptyState
import com.example.inventario.ui.theme.components.ModernDialog
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.ProductImage
import com.example.inventario.ui.theme.components.WaveFooter
import com.example.inventario.ui.theme.components.formFieldColors
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import java.io.File
import java.util.Locale

private sealed class OutflowItem {
    data class ManualExpense(val expense: Expense) : OutflowItem()
    data class SupplierOrderExpense(val order: SupplierOrder) : OutflowItem()

    val date: String
        get() = when (this) {
            is ManualExpense -> expense.date
            is SupplierOrderExpense -> order.id.toString() // Usamos ID o fecha genérica si SupplierOrder no maneja campo date directo
        }

    val amount: Double
        get() = when (this) {
            is ManualExpense -> expense.amount
            is SupplierOrderExpense -> order.totalCost
        }
}

@Composable
fun ExpensesScreen(
    viewModel: GestifyViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val expenses by viewModel.expenses.collectAsState()
    val supplierOrders by viewModel.supplierOrders.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val manualTotal = expenses.sumOf { it.amount }
    val supplierTotal = supplierOrders.sumOf { it.totalCost }
    val grandTotalOutflows = manualTotal + supplierTotal

    val unifiedOutflows = remember(expenses, supplierOrders) {
        val list1 = expenses.map { OutflowItem.ManualExpense(it) }
        val list2 = supplierOrders.map { OutflowItem.SupplierOrderExpense(it) }
        list1 + list2
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModuleBanner(
            title = "Gastos y Egresos de la Tienda",
            subtitle = "Control unificado de compras operativas, materiales y pedidos a proveedores.",
            icon = Icons.Default.ReceiptLong,
            action = {
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.onPrimary,
                        contentColor = colors.primary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo Gasto", style = MaterialTheme.typography.titleSmall)
                }
            }
        )

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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Total Egresos Consolidados", style = MaterialTheme.typography.titleLarge, color = colors.primary)
                    Text(
                        "-$${String.format(Locale.US, "%,.0f", grandTotalOutflows)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB91C1C)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• Gastos operativos manuales: -$${String.format(Locale.US, "%,.0f", manualTotal)}", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                        Text("• Pedidos a proveedores: -$${String.format(Locale.US, "%,.0f", supplierTotal)}", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Historial General de Egresos (Gastos + Proveedores)", style = MaterialTheme.typography.titleLarge, color = colors.primary)

                if (unifiedOutflows.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Payments,
                        title = "Sin egresos registrados",
                        message = "Registra gastos o pedidos para llevar el control total de salidas de dinero.",
                        actionText = "Registrar Gasto",
                        onAction = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth().height(220.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        unifiedOutflows.forEach { item ->
                            when (item) {
                                is OutflowItem.ManualExpense -> ExpenseItemRow(expense = item.expense)
                                is OutflowItem.SupplierOrderExpense -> SupplierOrderExpenseRow(order = item.order)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ExpenseFormDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { concept, amount, imageUriString ->
                val imageFile = imageUriString?.let { uriString ->
                    if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
                        uriToSquareFile(context, Uri.parse(uriString))
                    } else null
                }
                viewModel.addExpense(concept, amount, imageFile?.absolutePath ?: imageUriString)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ExpenseItemRow(expense: Expense) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (expense.imageUri != null) {
                ProductImage(
                    imageUri = expense.imageUri,
                    fallbackLabel = expense.concept.take(1).uppercase(),
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(colors.error.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = Color(0xFFB91C1C))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = expense.concept, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.onSurface)
                Text(text = "Gasto Operativo · ${expense.date}", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }

            Text(
                text = "-$${String.format(Locale.US, "%,.0f", expense.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB91C1C)
            )
        }
    }
}

@Composable
private fun SupplierOrderExpenseRow(order: SupplierOrder) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.primaryContainer.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(colors.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = colors.primary)
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "Pedido a Proveedor: ${order.supplierName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.onSurface)
                Text(text = "Estado: ${order.status} · Ref #${order.id}", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }

            Text(
                text = "-$${String.format(Locale.US, "%,.0f", order.totalCost)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB91C1C)
            )
        }
    }
}

@Composable
private fun ExpenseFormDialog(
    onDismiss: () -> Unit,
    onConfirm: (concept: String, amount: Double, imageUri: String?) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var concept by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) imageUri = uri.toString()
    }

    val amountValue = amount.toDoubleOrNull()
    val isValid = concept.isNotBlank() && amountValue != null && amountValue > 0.0

    ModernDialog(
        title = "Registrar Gasto o Egreso",
        subtitle = "Indica el motivo y opcionalmente añade una foto o comprobante.",
        icon = Icons.Default.ReceiptLong,
        confirmText = "Guardar egreso",
        confirmEnabled = isValid,
        onConfirm = { onConfirm(concept.trim(), amountValue!!, imageUri) },
        onDismiss = onDismiss
    ) {
        DialogSection(title = "Comprobante o foto (opcional)", icon = Icons.Default.AddPhotoAlternate) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.primary.copy(alpha = 0.06f))
                    .border(BorderStroke(1.dp, colors.primary.copy(alpha = 0.4f)), RoundedCornerShape(14.dp))
                    .clickable { picker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    ProductImage(
                        imageUri = imageUri,
                        fallbackLabel = "F",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = colors.primary, modifier = Modifier.size(32.dp))
                        Text("Añadir foto o recibo", style = MaterialTheme.typography.bodyMedium, color = colors.primary)
                    }
                }
            }
        }

        DialogSection(title = "Detalles del Gasto", icon = Icons.Default.Payments) {
            OutlinedTextField(
                value = concept,
                onValueChange = { concept = it },
                label = { Text("Motivo o concepto (ej. Decoración, Materiales) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = formFieldColors()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Monto gastado *") },
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = formFieldColors()
            )
        }
    }
}

private fun uriToSquareFile(context: Context, uri: Uri): File? {
    return try {
        val original = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        } ?: return null
        val side = minOf(original.width, original.height)
        val cropped = Bitmap.createBitmap(original, (original.width - side) / 2, (original.height - side) / 2, side, side)
        val file = File(context.cacheDir, "expense_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { out -> cropped.compress(Bitmap.CompressFormat.JPEG, 85, out) }
        file
    } catch (e: Exception) {
        null
    }
}