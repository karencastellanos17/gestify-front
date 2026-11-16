package com.example.inventario.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.data.model.Product
import com.example.inventario.ui.theme.components.EmptyState
import com.example.inventario.ui.theme.components.ModernDialog
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.ProductImage
import com.example.inventario.ui.theme.components.StatCard
import com.example.inventario.ui.theme.components.StatusBadge
import com.example.inventario.ui.theme.components.StatusBadgeStyle
import com.example.inventario.ui.theme.components.WaveFooter
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Modelo de ticket o venta realizada
data class SaleItem(
    val id: Int,
    val productName: String,
    val quantity: Int,
    val total: Double,
    val time: String,
    val date: String = ""
)

@Composable
fun SalesScreen(
    viewModel: GestifyViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme

    val salesList by viewModel.sales.collectAsState()
    val products by viewModel.products.collectAsState()

    var showSaleDialog by remember { mutableStateOf(false) }

    // Cálculo del total de ingresos del día
    val totalRevenue = salesList.sumOf { it.total }
    val averageTicket = if (salesList.isEmpty()) 0.0 else totalRevenue / salesList.size
    val unitsSold = salesList.sumOf { it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ───────── Banner del módulo con botón de venta ─────────
        ModuleBanner(
            title = "Módulo de Ventas y Caja",
            subtitle = "Registro de transacciones con bloqueo pesimista atómico en servidor.",
            icon = Icons.Default.PointOfSale,
            action = {
                Button(
                    onClick = { showSaleDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.onPrimary,
                        contentColor = colors.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Venta", style = MaterialTheme.typography.titleMedium)
                }
            }
        )

        // ───────── Resumen de caja del día ─────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                label = "Total ingresos del día",
                value = "$${String.format(Locale.US, "%,.0f", totalRevenue)}",
                icon = Icons.Default.AttachMoney,
                modifier = Modifier.weight(1.3f).fillMaxHeight()
            )
            StatCard(
                label = "Ticket promedio",
                value = "$${String.format(Locale.US, "%,.0f", averageTicket)}",
                icon = Icons.Default.Payments,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            StatCard(
                label = "Unidades vendidas",
                value = "$unitsSold un.",
                icon = Icons.Default.ShoppingCart,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // ───────── Historial de transacciones ─────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                WaveFooter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter),
                    color = colors.primary.copy(alpha = 0.05f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial de Transacciones",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.primary
                        )
                        StatusBadge(
                            text = "${salesList.size} Transacciones",
                            style = StatusBadgeStyle.SUCCESS,
                            icon = Icons.Default.PointOfSale
                        )
                    }

                    if (salesList.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.PointOfSale,
                            title = "Aún no hay ventas registradas",
                            message = "Cuando registres tu primera venta aparecerá aquí con su fecha, hora y valor.",
                            actionText = "Registrar Venta",
                            onAction = { showSaleDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.primary.copy(alpha = 0.10f))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SalesHeaderCell("ID TICKET", modifier = Modifier.weight(0.8f))
                            SalesHeaderCell("PRODUCTO VENDIDO", modifier = Modifier.weight(2.5f))
                            SalesHeaderCell("CANT.", modifier = Modifier.weight(1f))
                            SalesHeaderCell("TOTAL", modifier = Modifier.weight(1.2f))
                            SalesHeaderCell("FECHA Y HORA", modifier = Modifier.weight(1.5f))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(salesList, key = { it.id }) { sale ->
                                Column {
                                    SaleTableRow(sale = sale)
                                    HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ───────── Ventana: registrar venta ─────────
    if (showSaleDialog) {
        RegisterSaleDialog(
            availableProducts = products.filter { it.isActive && it.stock > 0 },
            onDismiss = { showSaleDialog = false },
            onConfirm = { product, quantity ->
                val now = Date()
                val locale = Locale.forLanguageTag("es-CO")
                val sale = SaleItem(
                    id = 0,
                    productName = product.name,
                    quantity = quantity,
                    total = product.price * quantity, // Corregido el operador de multiplicación
                    time = SimpleDateFormat("hh:mm a", locale).format(now),
                    date = SimpleDateFormat("dd/MM/yyyy", locale).format(now)
                )
                viewModel.createSale(sale) {
                    showSaleDialog = false
                }
            }
        )
    }
}

@Composable
private fun RegisterSaleDialog(
    availableProducts: List<Product>,
    onDismiss: () -> Unit,
    onConfirm: (product: Product, quantity: Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    var selectedId by remember { mutableStateOf<Int?>(null) }
    var quantity by remember { mutableIntStateOf(1) }

    val selected = availableProducts.firstOrNull { it.id == selectedId }
    val totalToCharge = if (selected != null) selected.price * quantity else 0.0 // Corregido el operador de multiplicación

    ModernDialog(
        title = "Registrar venta",
        subtitle = "Elige el producto y la cantidad vendida.",
        icon = Icons.Default.PointOfSale,
        confirmText = "Registrar venta",
        confirmEnabled = selected != null,
        onConfirm = { if (selected != null) onConfirm(selected, quantity) },
        onDismiss = onDismiss
    ) {
        if (availableProducts.isEmpty()) {
            Text(
                text = "No hay productos activos con stock disponible para vender.",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant
            )
        } else {
            Text(
                text = "PRODUCTO",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )

            availableProducts.forEach { product ->
                SaleProductOption(
                    product = product,
                    isSelected = product.id == selectedId,
                    onClick = {
                        selectedId = product.id
                        quantity = 1
                    }
                )
            }

            // Selector de cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CANTIDAD",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = if (selected != null) "Disponibles: ${selected.stock} un." else "Selecciona un producto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = selected != null && quantity > 1,
                        modifier = Modifier.background(colors.primary.copy(alpha = 0.10f), CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Menos", tint = colors.primary)
                    }
                    Text(
                        text = "$quantity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = colors.onSurface,
                        modifier = Modifier.width(44.dp)
                    )
                    IconButton(
                        onClick = { quantity++ },
                        enabled = selected != null && quantity < selected.stock,
                        modifier = Modifier.background(colors.primary.copy(alpha = 0.10f), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Más", tint = colors.primary)
                    }
                }
            }

            // Total a cobrar
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.primary.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL A COBRAR",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format(Locale.US, "%,.0f", totalToCharge)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun SaleProductOption(
    product: Product,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isSelected) colors.primary.copy(alpha = 0.10f) else colors.primary.copy(alpha = 0.03f))
            .border(
                BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) colors.primary else colors.outlineVariant.copy(alpha = 0.5f)
                ),
                shape
            )
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProductImage(
            imageUri = product.imageUri,
            fallbackLabel = product.name.take(1).uppercase(),
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            Text(
                text = "$${String.format(Locale.US, "%,.0f", product.price)} · ${product.stock} un. disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }
        RadioButton(selected = isSelected, onClick = null)
    }
}

@Composable
private fun SalesHeaderCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
private fun SaleTableRow(sale: SaleItem) {
    val colors = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${sale.id}",
                modifier = Modifier.weight(0.8f),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.weight(2.5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(colors.primary, colors.secondary))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PointOfSale,
                        contentDescription = null,
                        tint = colors.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = sale.productName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = colors.primary.copy(alpha = 0.10f)
                ) {
                    Text(
                        text = "${sale.quantity} un.",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primary,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Text(
                text = "$${String.format(Locale.US, "%,.0f", sale.total)}",
                modifier = Modifier.weight(1.2f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            Column(modifier = Modifier.weight(1.5f)) {
                Text(
                    text = sale.date.ifBlank { sale.time },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                if (sale.date.isNotBlank()) {
                    Text(
                        text = sale.time,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}