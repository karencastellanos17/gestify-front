package com.example.inventario.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.data.model.Product
import com.example.inventario.ui.theme.components.DialogSection
import com.example.inventario.ui.theme.components.EmptyState
import com.example.inventario.ui.theme.components.ModernDialog
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.ProductImage
import com.example.inventario.ui.theme.components.StatCard
import com.example.inventario.ui.theme.components.StatusBadge
import com.example.inventario.ui.theme.components.StatusBadgeStyle
import com.example.inventario.ui.theme.components.WaveFooter
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SaleItem(
    @SerializedName("id", alternate = ["idVenta", "id_venta"])
    val id: Int = 0,

    @SerializedName("idNegocio", alternate = ["id_negocio", "businessId"])
    val idNegocio: Long? = null,

    @SerializedName("productId", alternate = ["idProducto", "id_producto", "productoId"])
    val productId: Int = 0,

    @SerializedName("productName", alternate = ["nombreProducto", "producto", "name"])
    val productName: String = "",

    @SerializedName("quantity", alternate = ["cantidad"])
    val quantity: Int = 0,

    @SerializedName("unitPrice", alternate = ["precioUnitario", "precio", "price"])
    val unitPrice: Double = 0.0,

    @SerializedName("total", alternate = ["montoTotal", "totalVenta"])
    val total: Double = 0.0,

    @SerializedName("date", alternate = ["fecha", "fechaVenta", "createdAt"])
    val date: String = ""
)

@Composable
fun SalesScreen(
    viewModel: GestifyViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val products by viewModel.products.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val isPhone = LocalConfiguration.current.screenWidthDp < 700
    val availableProducts = products.filter { it.isActive && it.stock > 0 }

    var showSaleDialog by remember { mutableStateOf(false) }

    val totalRevenue = sales.sumOf { it.total }
    val totalUnits = sales.sumOf { it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModuleBanner(
            title = "Módulo de Ventas y Facturación",
            subtitle = "Registra transacciones rápidas con cálculo automático y descuento de stock.",
            icon = Icons.Default.PointOfSale,
            action = {
                Button(
                    onClick = {
                        viewModel.clearError()
                        showSaleDialog = true
                    },
                    enabled = availableProducts.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.onPrimary,
                        contentColor = colors.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Venta", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nueva Venta", style = MaterialTheme.typography.titleSmall)
                }
            }
        )

        if (isPhone) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    label = "Ingresos totales",
                    value = "$${String.format(Locale.US, "%,.0f", totalRevenue)}",
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.fillMaxWidth()
                )
                StatCard(
                    label = "Transacciones",
                    value = "${sales.size}",
                    icon = Icons.Default.Receipt,
                    modifier = Modifier.fillMaxWidth()
                )
                StatCard(
                    label = "Unidades vendidas",
                    value = "$totalUnits un.",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    label = "Ingresos totales",
                    value = "$${String.format(Locale.US, "%,.0f", totalRevenue)}",
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                StatCard(
                    label = "Transacciones",
                    value = "${sales.size}",
                    icon = Icons.Default.Receipt,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                StatCard(
                    label = "Unidades vendidas",
                    value = "$totalUnits un.",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                WaveFooter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter),
                    color = colors.primary.copy(alpha = 0.05f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial de Transacciones",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.primary
                        )
                        StatusBadge(
                            text = "${sales.size} VENTAS",
                            style = StatusBadgeStyle.SUCCESS
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (sales.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.PointOfSale,
                            title = "Aún no hay ventas",
                            message = "Registra tu primera venta para ver el historial y actualizar el inventario.",
                            actionText = if (availableProducts.isNotEmpty()) "Nueva Venta" else null,
                            onAction = {
                                viewModel.clearError()
                                showSaleDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )
                    } else if (isPhone) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            sales.forEach { sale ->
                                val product = products.find { it.id == sale.productId || it.name.equals(sale.productName, ignoreCase = true) }
                                SaleMobileCard(sale = sale, imageUri = product?.imageUri)
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.primary.copy(alpha = 0.10f))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SalesTableHeaderCell("ID", modifier = Modifier.weight(0.7f))
                                SalesTableHeaderCell("PRODUCTO", modifier = Modifier.weight(2.2f))
                                SalesTableHeaderCell("CANT.", modifier = Modifier.weight(0.9f))
                                SalesTableHeaderCell("PRECIO UNIT.", modifier = Modifier.weight(1.3f))
                                SalesTableHeaderCell("TOTAL", modifier = Modifier.weight(1.3f))
                                SalesTableHeaderCell("FECHA", modifier = Modifier.weight(1.4f))
                                SalesTableHeaderCell("ESTADO", modifier = Modifier.weight(1.2f))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            sales.forEach { sale ->
                                val product = products.find { it.id == sale.productId || it.name.equals(sale.productName, ignoreCase = true) }
                                SaleTableRow(sale = sale, imageUri = product?.imageUri)
                                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSaleDialog && availableProducts.isNotEmpty()) {
        RegisterSaleDialog(
            availableProducts = availableProducts,
            isSaving = viewModel.isLoading,
            serverError = viewModel.errorMessage,
            onDismiss = {
                viewModel.clearError()
                showSaleDialog = false
            },
            onConfirm = { product, qty ->
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                val newSale = SaleItem(
                    id = 0,
                    productId = product.id,
                    productName = product.name,
                    quantity = qty,
                    unitPrice = product.price,
                    total = product.price * qty,
                    date = formatter.format(Date())
                )
                viewModel.createSale(newSale) {
                    showSaleDialog = false
                }
            }
        )
    }
}

@Composable
private fun RegisterSaleDialog(
    availableProducts: List<Product>,
    isSaving: Boolean,
    serverError: String?,
    onDismiss: () -> Unit,
    onConfirm: (Product, Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var selectedProductId by remember(availableProducts) { mutableIntStateOf(availableProducts.first().id) }
    val selectedProduct = availableProducts.find { it.id == selectedProductId } ?: availableProducts.first()
    var quantity by remember(selectedProductId) { mutableIntStateOf(1) }

    val subtotal = selectedProduct.price * quantity

    ModernDialog(
        title = "Registrar venta",
        subtitle = "Elige el producto y la cantidad vendida.",
        icon = Icons.Default.PointOfSale,
        confirmText = if (isSaving) "Registrando..." else "Registrar venta",
        confirmEnabled = !isSaving && quantity in 1..selectedProduct.stock,
        onConfirm = { onConfirm(selectedProduct, quantity) },
        onDismiss = onDismiss
    ) {
        serverError?.let { error ->
            Surface(
                color = colors.errorContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = colors.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        DialogSection(title = "Producto", icon = Icons.Default.Inventory2) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableProducts.forEach { product ->
                    val isSelected = product.id == selectedProduct.id
                    val borderColor = if (isSelected) colors.primary else colors.outlineVariant.copy(alpha = 0.4f)
                    val bgColor = if (isSelected) colors.primary.copy(alpha = 0.08f) else colors.surfaceVariant.copy(alpha = 0.35f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(bgColor)
                            .border(BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor), RoundedCornerShape(14.dp))
                            .clickable { selectedProductId = product.id }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            ProductImage(
                                imageUri = product.imageUri,
                                fallbackLabel = product.name.take(1).uppercase(),
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Column {
                                Text(
                                    product.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.onSurface
                                )
                                Text(
                                    "$${String.format(Locale.US, "%,.0f", product.price)} · ${product.stock} un. disponibles",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedProductId = product.id }
                        )
                    }
                }
            }
        }

        DialogSection(title = "Cantidad", icon = Icons.Default.ShoppingCart) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Disponibles: ${selectedProduct.stock} un.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1,
                        shape = CircleShape,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Menos")
                    }
                    Text(
                        text = "$quantity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    FilledTonalIconButton(
                        onClick = { if (quantity < selectedProduct.stock) quantity++ },
                        enabled = quantity < selectedProduct.stock,
                        shape = CircleShape,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Más")
                    }
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colors.primary.copy(alpha = 0.10f),
            border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TOTAL A COBRAR",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
                Text(
                    "$${String.format(Locale.US, "%,.0f", subtotal)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }
    }
}

@Composable
private fun SaleMobileCard(sale: SaleItem, imageUri: String?) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    ProductImage(
                        imageUri = imageUri,
                        fallbackLabel = sale.productName.take(1).uppercase().ifEmpty { "V" },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Column {
                        Text(
                            sale.productName.ifEmpty { "Producto #${sale.productId}" },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                        Text(
                            "${sale.quantity} un. × $${String.format(Locale.US, "%,.0f", sale.unitPrice)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
                Text(
                    "$${String.format(Locale.US, "%,.0f", sale.total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    sale.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
                StatusBadge(text = "Completada", style = StatusBadgeStyle.SUCCESS)
            }
        }
    }
}

@Composable
private fun SaleTableRow(sale: SaleItem, imageUri: String?) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "#${sale.id}",
            modifier = Modifier.weight(0.7f),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant
        )
        Row(
            modifier = Modifier.weight(2.2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProductImage(
                imageUri = imageUri,
                fallbackLabel = sale.productName.take(1).uppercase().ifEmpty { "V" },
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Text(
                sale.productName.ifEmpty { "Producto #${sale.productId}" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
        }
        Text(
            "${sale.quantity} un.",
            modifier = Modifier.weight(0.9f),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurface
        )
        Text(
            "$${String.format(Locale.US, "%,.0f", sale.unitPrice)}",
            modifier = Modifier.weight(1.3f),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant
        )
        Text(
            "$${String.format(Locale.US, "%,.0f", sale.total)}",
            modifier = Modifier.weight(1.3f),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )
        Text(
            sale.date,
            modifier = Modifier.weight(1.4f),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant
        )
        Box(modifier = Modifier.weight(1.2f)) {
            StatusBadge(text = "Completada", style = StatusBadgeStyle.SUCCESS)
        }
    }
}

@Composable
private fun SalesTableHeaderCell(text: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = colors.primary
    )
}