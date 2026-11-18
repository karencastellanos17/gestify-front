package com.example.inventario.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
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

data class SaleCartLine(
    @SerializedName("productId", alternate = ["idProducto", "id_producto"])
    val productId: Int = 0,

    @SerializedName("productName", alternate = ["nombreProducto", "name"])
    val productName: String = "",

    @SerializedName("quantity", alternate = ["cantidad"])
    val quantity: Int = 0,

    @SerializedName("unitPrice", alternate = ["precioUnitario", "price"])
    val unitPrice: Double = 0.0,

    @SerializedName("subtotal")
    val subtotal: Double = 0.0
)

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
    val unitPrice: Double? = 0.0,

    @SerializedName("total", alternate = ["montoTotal", "totalVenta"])
    val total: Double = 0.0,

    @SerializedName("time", alternate = ["hora"])
    val time: String = "",

    @SerializedName("date", alternate = ["fecha", "fechaVenta", "createdAt"])
    val date: String = "",

    @SerializedName("customerName", alternate = ["clienteNombre"])
    val customerName: String? = null,

    @SerializedName("customerDocument", alternate = ["clienteDocumento"])
    val customerDocument: String? = null,

    @SerializedName("customerPhone", alternate = ["clienteTelefono"])
    val customerPhone: String? = null,

    @SerializedName("customerEmail", alternate = ["clienteCorreo"])
    val customerEmail: String? = null,

    @SerializedName("items", alternate = ["detalles"])
    val items: List<SaleCartLine> = emptyList()
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
            subtitle = "Registra ventas de uno o varios productos con datos opcionales del cliente.",
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
                    Text("Nueva Venta", style = MaterialTheme.typography.titleSmall, maxLines = 1, softWrap = false)
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
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
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
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
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
                            color = colors.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
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
                                SalesTableHeaderCell("ID", modifier = Modifier.weight(0.6f))
                                SalesTableHeaderCell("PRODUCTOS", modifier = Modifier.weight(2.2f))
                                SalesTableHeaderCell("CLIENTE", modifier = Modifier.weight(1.4f))
                                SalesTableHeaderCell("CANT.", modifier = Modifier.weight(0.8f))
                                SalesTableHeaderCell("TOTAL", modifier = Modifier.weight(1.2f))
                                SalesTableHeaderCell("FECHA", modifier = Modifier.weight(1.4f))
                                SalesTableHeaderCell("ESTADO", modifier = Modifier.weight(1.1f))
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
            onConfirm = { cartLines, customerName, customerDoc, customerPhone, customerEmail ->
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                val first = cartLines.first()
                val totalAmount = cartLines.sumOf { it.subtotal }
                val totalQty = cartLines.sumOf { it.quantity }

                val newSale = SaleItem(
                    id = 0,
                    productId = first.productId,
                    productName = first.productName,
                    quantity = totalQty,
                    unitPrice = if (cartLines.size == 1) first.unitPrice else null,
                    total = totalAmount,
                    date = formatter.format(Date()),
                    customerName = customerName.trim().ifEmpty { null },
                    customerDocument = customerDoc.trim().ifEmpty { null },
                    customerPhone = customerPhone.trim().ifEmpty { null },
                    customerEmail = customerEmail.trim().ifEmpty { null },
                    items = cartLines
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
    onConfirm: (
        cartLines: List<SaleCartLine>,
        customerName: String,
        customerDocument: String,
        customerPhone: String,
        customerEmail: String
    ) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var selectedProductId by remember(availableProducts) { mutableIntStateOf(availableProducts.first().id) }
    val selectedProduct = availableProducts.find { it.id == selectedProductId } ?: availableProducts.first()

    // Carrito: productId -> cantidad
    val cartMap = remember { mutableStateMapOf<Int, Int>() }
    val alreadyInCart = cartMap[selectedProduct.id] ?: 0
    val remainingStock = (selectedProduct.stock - alreadyInCart).coerceAtLeast(0)
    var pickerQuantity by remember(selectedProductId, remainingStock) {
        mutableIntStateOf(if (remainingStock > 0) 1 else 0)
    }

    // Datos opcionales del cliente
    var showCustomerFields by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("") }
    var customerDocument by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }

    val cartLines: List<SaleCartLine> = cartMap.mapNotNull { (prodId, qty) ->
        val prod = availableProducts.find { it.id == prodId } ?: return@mapNotNull null
        SaleCartLine(
            productId = prod.id,
            productName = prod.name,
            quantity = qty,
            unitPrice = prod.price,
            subtotal = prod.price * qty
        )
    }

    val grandTotal = cartLines.sumOf { it.subtotal }
    val totalItemsCount = cartLines.sumOf { it.quantity }

    ModernDialog(
        title = "Registrar venta",
        subtitle = "Agrega uno o varios productos al carrito y confirma el cobro.",
        icon = Icons.Default.PointOfSale,
        confirmText = if (isSaving) "Registrando..." else "Cobrar ($${String.format(Locale.US, "%,.0f", grandTotal)})",
        confirmEnabled = !isSaving && cartLines.isNotEmpty(),
        onConfirm = {
            onConfirm(cartLines, customerName, customerDocument, customerPhone, customerEmail)
        },
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

        // 1. Selector de producto para agregar al carrito
        DialogSection(title = "1. Selecciona productos", icon = Icons.Default.Inventory2) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableProducts.forEach { product ->
                    val isSelected = product.id == selectedProduct.id
                    val inCartQty = cartMap[product.id] ?: 0
                    val availLeft = (product.stock - inCartQty).coerceAtLeast(0)
                    val borderColor = if (isSelected) colors.primary else colors.outlineVariant.copy(alpha = 0.4f)
                    val bgColor = if (isSelected) colors.primary.copy(alpha = 0.08f) else colors.surfaceVariant.copy(alpha = 0.35f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(bgColor)
                            .border(BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor), RoundedCornerShape(14.dp))
                            .clickable { selectedProductId = product.id }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            ProductImage(
                                imageUri = product.imageUri,
                                fallbackLabel = product.name.take(1).uppercase(),
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%,.0f", product.price)} · Disp: $availLeft un.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (inCartQty > 0) {
                            Surface(
                                color = colors.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${inCartQty}x",
                                    color = colors.onPrimary,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedProductId = product.id }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Controles de cantidad + botón Agregar al carrito
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = { if (pickerQuantity > 1) pickerQuantity-- },
                            enabled = pickerQuantity > 1,
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Menos")
                        }
                        Text(
                            text = "$pickerQuantity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                        FilledTonalIconButton(
                            onClick = { if (pickerQuantity < remainingStock) pickerQuantity++ },
                            enabled = pickerQuantity < remainingStock,
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Más")
                        }
                    }

                    Button(
                        onClick = {
                            if (pickerQuantity in 1..remainingStock) {
                                cartMap[selectedProduct.id] = alreadyInCart + pickerQuantity
                            }
                        },
                        enabled = remainingStock > 0 && pickerQuantity > 0,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Agregar", maxLines = 1, softWrap = false)
                    }
                }
            }
        }

        // 2. Carrito actual
        DialogSection(title = "2. Carrito (${cartLines.size} prod. / $totalItemsCount un.)", icon = Icons.Default.ShoppingCart) {
            if (cartLines.isEmpty()) {
                Text(
                    text = "Aún no has agregado productos al carrito.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cartLines.forEach { line ->
                        val prod = availableProducts.find { it.id == line.productId }
                        val maxStock = prod?.stock ?: line.quantity

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = line.productName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${line.quantity} × $${String.format(Locale.US, "%,.0f", line.unitPrice)} = $${String.format(Locale.US, "%,.0f", line.subtotal)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (line.quantity > 1) {
                                                cartMap[line.productId] = line.quantity - 1
                                            } else {
                                                cartMap.remove(line.productId)
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Restar", modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            if (line.quantity < maxStock) {
                                                cartMap[line.productId] = line.quantity + 1
                                            }
                                        },
                                        enabled = line.quantity < maxStock,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Sumar", modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { cartMap.remove(line.productId) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Quitar",
                                            tint = colors.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Datos opcionales del cliente
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = colors.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCustomerFields = !showCustomerFields },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = colors.primary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Datos del cliente (opcional)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                    }
                    Icon(
                        imageVector = if (showCustomerFields) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant
                    )
                }

                AnimatedVisibility(visible = showCustomerFields) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Nombre del comprador") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = customerDocument,
                            onValueChange = { customerDocument = it },
                            label = { Text("Cédula o NIT") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Teléfono / WhatsApp") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = customerEmail,
                            onValueChange = { customerEmail = it },
                            label = { Text("Correo electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Resumen total
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
                    "$${String.format(Locale.US, "%,.0f", grandTotal)}",
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
    val detailText = if (sale.items.size > 1) {
        sale.items.joinToString(" · ") { "${it.quantity}x ${it.productName}" }
    } else {
        val unit = sale.unitPrice ?: (if (sale.quantity > 0) sale.total / sale.quantity else 0.0)
        "${sale.quantity} un. × $${String.format(Locale.US, "%,.0f", unit)}"
    }

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
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sale.productName.ifEmpty { "Venta #${sale.id}" },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = detailText,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!sale.customerName.isNullOrBlank()) {
                            Text(
                                text = "Cliente: ${sale.customerName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$${String.format(Locale.US, "%,.0f", sale.total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    maxLines = 1,
                    softWrap = false
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = listOf(sale.date, sale.time).filter { it.isNotBlank() }.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
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
    val detailTooltip = if (sale.items.size > 1) {
        sale.items.joinToString(", ") { "${it.quantity}x ${it.productName}" }
    } else {
        sale.productName.ifEmpty { "Producto #${sale.productId}" }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "#${sale.id}",
            modifier = Modifier.weight(0.6f),
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
                text = detailTooltip,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = sale.customerName?.ifBlank { null } ?: "Mostrador",
            modifier = Modifier.weight(1.4f),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            "${sale.quantity} un.",
            modifier = Modifier.weight(0.8f),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurface
        )
        Text(
            "$${String.format(Locale.US, "%,.0f", sale.total)}",
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )
        Text(
            listOf(sale.date, sale.time).filter { it.isNotBlank() }.joinToString(" "),
            modifier = Modifier.weight(1.4f),
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant
        )
        Box(modifier = Modifier.weight(1.1f)) {
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