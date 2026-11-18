package com.example.inventario.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.data.model.CreateSupplierOrderItemRequest
import com.example.inventario.data.model.CreateSupplierOrderRequest
import com.example.inventario.data.model.PricingTier
import com.example.inventario.data.model.Product
import com.example.inventario.data.model.Supplier
import com.example.inventario.data.model.SupplierOrder
import com.example.inventario.data.model.distributes
import com.example.inventario.ui.theme.components.DialogSection
import com.example.inventario.ui.theme.components.EmptyState
import com.example.inventario.ui.theme.components.ModernDialog
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.StatCard
import com.example.inventario.ui.theme.components.StatusBadge
import com.example.inventario.ui.theme.components.StatusBadgeStyle
import com.example.inventario.ui.theme.components.WaveFooter
import com.example.inventario.ui.theme.components.formFieldColors
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import com.example.inventario.utils.ValidationUtils
import java.util.Locale
import kotlin.math.ceil

@Composable
fun SuppliersScreen(
    viewModel: GestifyViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val isPhone = LocalConfiguration.current.screenWidthDp < 700

    val suppliers by viewModel.suppliers.collectAsState()
    val products by viewModel.products.collectAsState()
    val orders by viewModel.supplierOrders.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var supplierToEdit by remember { mutableStateOf<Supplier?>(null) }
    var supplierToToggle by remember { mutableStateOf<Supplier?>(null) }
    var supplierToOrder by remember { mutableStateOf<Supplier?>(null) }
    var query by remember { mutableStateOf("") }

    val activeSuppliers = suppliers.filter { it.isActive }
    val activeProducts = products.filter { it.isActive }
    val coveredCount = activeProducts.count { product -> activeSuppliers.any { it.distributes(product) } }
    val uncoveredCount = (activeProducts.size - coveredCount).coerceAtLeast(0)

    val filtered = suppliers.filter { supplier ->
        query.isBlank() ||
                supplier.name.contains(query, ignoreCase = true) ||
                supplier.contactName.contains(query, ignoreCase = true) ||
                supplier.category.contains(query, ignoreCase = true) ||
                supplier.extraItems.any { it.contains(query, ignoreCase = true) } ||
                products.any { it.id in supplier.productIds && it.name.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModuleBanner(
            title = "Proveedores y Abastecimiento",
            subtitle = "Gestiona tus abastecedores, pide mercancía por unidad, decena o docena y calcula tu equilibrio de ganancia.",
            icon = Icons.Default.LocalShipping,
            action = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (activeSuppliers.isNotEmpty() && activeProducts.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                viewModel.clearError()
                                supplierToOrder = activeSuppliers.first()
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, colors.onPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onPrimary)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pedir stock", style = MaterialTheme.typography.titleSmall, maxLines = 1, softWrap = false)
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.clearError()
                            showAddDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.onPrimary,
                            contentColor = colors.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nuevo Proveedor", style = MaterialTheme.typography.titleSmall, maxLines = 1, softWrap = false)
                    }
                }
            }
        )

        viewModel.errorMessage?.let { error ->
            Surface(
                color = colors.errorContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Error del servidor: $error",
                    color = colors.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        if (isPhone) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    label = "Proveedores activos",
                    value = "${activeSuppliers.size}",
                    icon = Icons.Default.LocalShipping,
                    modifier = Modifier.fillMaxWidth()
                )
                StatCard(
                    label = "Productos con proveedor",
                    value = "$coveredCount de ${activeProducts.size}",
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.fillMaxWidth()
                )
                StatCard(
                    label = "Sin proveedor",
                    value = "$uncoveredCount productos",
                    icon = Icons.Default.Warning,
                    accent = if (uncoveredCount > 0) colors.error else colors.primary,
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
                    label = "Proveedores activos",
                    value = "${activeSuppliers.size}",
                    icon = Icons.Default.LocalShipping,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                StatCard(
                    label = "Productos con proveedor",
                    value = "$coveredCount de ${activeProducts.size}",
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                StatCard(
                    label = "Sin proveedor",
                    value = "$uncoveredCount productos",
                    icon = Icons.Default.Warning,
                    accent = if (uncoveredCount > 0) colors.error else colors.primary,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }

        // Directorio de proveedores
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isPhone) {
                        Text(
                            text = "Directorio de proveedores",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.primary
                        )
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Buscar proveedor o producto") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Directorio de proveedores",
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.primary
                            )

                            OutlinedTextField(
                                value = query,
                                onValueChange = { query = it },
                                placeholder = { Text("Buscar proveedor o producto") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.width(320.dp)
                            )
                        }
                    }

                    when {
                        suppliers.isEmpty() -> EmptyState(
                            icon = Icons.Default.LocalShipping,
                            title = "Aún no tienes proveedores",
                            message = "Registra a quién le compras y qué te distribuye. Así la app podrá recomendarte a quién pedirle cuando el stock baje.",
                            actionText = "Nuevo Proveedor",
                            onAction = {
                                viewModel.clearError()
                                showAddDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )

                        filtered.isEmpty() -> EmptyState(
                            icon = Icons.Default.Search,
                            title = "Sin resultados",
                            message = "No encontramos proveedores para «$query». Prueba con otro nombre o producto.",
                            actionText = "Limpiar búsqueda",
                            onAction = { query = "" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )

                        else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            filtered.forEach { supplier ->
                                SupplierCard(
                                    supplier = supplier,
                                    items = (products.filter { it.id in supplier.productIds }.map { it.name } +
                                            supplier.extraItems).distinctBy { it.lowercase() },
                                    canOrder = supplier.isActive && activeProducts.isNotEmpty(),
                                    onOrderRequest = {
                                        viewModel.clearError()
                                        supplierToOrder = supplier
                                    },
                                    onEditRequest = {
                                        viewModel.clearError()
                                        supplierToEdit = supplier
                                    },
                                    onToggleRequest = { supplierToToggle = supplier }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Historial de pedidos de reposición
        if (orders.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pedidos y Reposiciones",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        StatusBadge(
                            text = "${orders.size} PEDIDOS",
                            style = StatusBadgeStyle.INFO
                        )
                    }

                    orders.forEach { order ->
                        SupplierOrderCard(
                            order = order,
                            products = products,
                            onMarkReceived = { viewModel.receiveSupplierOrder(order.id) }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de Pedido / Compra de Material con Equilibrio de Ganancia
    supplierToOrder?.let { selectedSupplier ->
        CreateSupplierOrderDialog(
            initialSupplier = selectedSupplier,
            suppliers = activeSuppliers,
            products = activeProducts,
            isSaving = viewModel.isLoading,
            serverError = viewModel.errorMessage,
            onDismiss = {
                viewModel.clearError()
                supplierToOrder = null
            },
            onConfirm = { request ->
                viewModel.createSupplierOrder(request) {
                    supplierToOrder = null
                }
            }
        )
    }

    if (showAddDialog) {
        SupplierFormDialog(
            initial = null,
            products = products,
            isSaving = viewModel.isLoading,
            serverError = viewModel.errorMessage,
            onDismiss = {
                viewModel.clearError()
                showAddDialog = false
            },
            onConfirm = { name, contactName, phone, category, productIds, extraItems ->
                val newSupplier = Supplier(
                    id = 0,
                    _name = name,
                    _contactName = contactName,
                    _phone = phone,
                    _category = category,
                    _productIds = productIds,
                    _extraItems = extraItems,
                    isActive = true
                )
                viewModel.createSupplier(newSupplier) {
                    showAddDialog = false
                }
            }
        )
    }

    supplierToEdit?.let { target ->
        SupplierFormDialog(
            initial = target,
            products = products,
            isSaving = viewModel.isLoading,
            serverError = viewModel.errorMessage,
            onDismiss = {
                viewModel.clearError()
                supplierToEdit = null
            },
            onConfirm = { name, contactName, phone, category, productIds, extraItems ->
                val updatedSupplier = target.copy(
                    _name = name,
                    _contactName = contactName,
                    _phone = phone,
                    _category = category,
                    _productIds = productIds,
                    _extraItems = extraItems
                )
                viewModel.createSupplier(updatedSupplier) {
                    supplierToEdit = null
                }
            }
        )
    }

    supplierToToggle?.let { target ->
        val deactivating = target.isActive
        ModernDialog(
            title = if (deactivating) "¿Desactivar proveedor?" else "¿Restaurar proveedor?",
            subtitle = if (deactivating) "Esta acción aplica Soft Delete." else "Volverá a estar disponible en las recomendaciones.",
            icon = if (deactivating) Icons.Default.Delete else Icons.Default.Restore,
            confirmText = if (deactivating) "Sí, desactivar" else "Sí, restaurar",
            danger = deactivating,
            onConfirm = {
                viewModel.toggleSupplier(target.id)
                supplierToToggle = null
            },
            onDismiss = { supplierToToggle = null }
        ) {
            Text(
                text = if (deactivating)
                    "«${target.name}» dejará de aparecer como proveedor recomendado en las alertas, pero su información se conserva y podrás restaurarlo cuando quieras."
                else
                    "«${target.name}» volverá a aparecer como proveedor recomendado cuando el stock de sus productos baje.",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant
            )
        }
    }
}

private data class DraftOrderLine(
    val productId: Int,
    val productName: String,
    val salePrice: Double,
    val tier: PricingTier,
    val packQuantity: Int,
    val tierPrice: Double
) {
    val totalUnits: Int get() = packQuantity * tier.multiplier
    val subtotal: Double get() = packQuantity * tierPrice
    val baseUnitCost: Double get() = tierPrice / tier.multiplier
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateSupplierOrderDialog(
    initialSupplier: Supplier,
    suppliers: List<Supplier>,
    products: List<Product>,
    isSaving: Boolean,
    serverError: String?,
    onDismiss: () -> Unit,
    onConfirm: (CreateSupplierOrderRequest) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    var selectedSupplierId by remember(initialSupplier) { mutableIntStateOf(initialSupplier.id) }
    val currentSupplier = suppliers.find { it.id == selectedSupplierId } ?: initialSupplier

    val suggestedProducts = remember(currentSupplier, products) {
        val linked = products.filter { it.id in currentSupplier.productIds }
        val others = products.filter { it.id !in currentSupplier.productIds }
        linked + others
    }

    var selectedProductId by remember(suggestedProducts) {
        mutableIntStateOf(suggestedProducts.firstOrNull()?.id ?: 0)
    }
    val selectedProduct = suggestedProducts.find { it.id == selectedProductId } ?: suggestedProducts.first()

    var selectedTier by remember { mutableStateOf(PricingTier.DOCENA) }
    var packQtyText by remember { mutableStateOf("1") }
    var tierPriceText by remember { mutableStateOf("") }
    var shippingText by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }
    var receiveImmediately by remember { mutableStateOf(true) }

    val draftLines = remember { mutableStateListOf<DraftOrderLine>() }

    val packQty = packQtyText.toIntOrNull() ?: 0
    val tierPrice = tierPriceText.toDoubleOrNull() ?: 0.0
    val shippingCost = shippingText.toDoubleOrNull() ?: 0.0

    val subtotalProducts = draftLines.sumOf { it.subtotal }
    val totalOrderCost = subtotalProducts + shippingCost
    val grandTotalUnits = draftLines.sumOf { it.totalUnits }
    val shippingPerUnit = if (grandTotalUnits > 0) shippingCost / grandTotalUnits else 0.0

    val projectedRevenue = draftLines.sumOf { it.totalUnits * it.salePrice }
    val projectedNetProfit = projectedRevenue - totalOrderCost
    val profitMarginPct = if (projectedRevenue > 0) (projectedNetProfit / projectedRevenue) * 100.0 else 0.0

    ModernDialog(
        title = "Pedir material y calcular equilibrio",
        subtitle = "Cotiza por unidad, decena o docena más envío para asegurar tu margen.",
        icon = Icons.Default.AddShoppingCart,
        confirmText = if (isSaving) "Guardando..." else "Registrar pedido ($${String.format(Locale.US, "%,.0f", totalOrderCost)})",
        confirmEnabled = !isSaving && draftLines.isNotEmpty(),
        onConfirm = {
            onConfirm(
                CreateSupplierOrderRequest(
                    supplierId = currentSupplier.id,
                    shippingCost = shippingCost,
                    notes = notes.trim().ifEmpty { null },
                    receiveImmediately = receiveImmediately,
                    items = draftLines.map {
                        CreateSupplierOrderItemRequest(
                            productId = it.productId,
                            pricingTier = it.tier.name,
                            packQuantity = it.packQuantity,
                            tierPrice = it.tierPrice
                        )
                    }
                )
            )
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

        // 1. Proveedor
        DialogSection(title = "1. Proveedor", icon = Icons.Default.LocalShipping) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suppliers.forEach { sup ->
                    FilterChip(
                        selected = sup.id == currentSupplier.id,
                        onClick = { selectedSupplierId = sup.id },
                        label = { Text(sup.name, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }
        }

        // 2. Agregar producto y escala de precio
        DialogSection(title = "2. Producto y tarifa del proveedor", icon = Icons.Default.Inventory) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestedProducts.forEach { prod ->
                    val lowTag = if (prod.stock < 5) " (${prod.stock} un.)" else ""
                    FilterChip(
                        selected = prod.id == selectedProduct.id,
                        onClick = { selectedProductId = prod.id },
                        label = { Text("${prod.name}$lowTag", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            Text(
                text = "Tu precio de venta actual: $${String.format(Locale.US, "%,.0f", selectedProduct.price)} / un. · Stock: ${selectedProduct.stock}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.primary,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PricingTier.entries.forEach { tier ->
                    FilterChip(
                        selected = selectedTier == tier,
                        onClick = { selectedTier = tier },
                        label = { Text(tier.label, maxLines = 1, softWrap = false) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = packQtyText,
                    onValueChange = { packQtyText = it.filter { c -> c.isDigit() }.take(4) },
                    label = {
                        Text(
                            when (selectedTier) {
                                PricingTier.UNIDAD -> "Unidades"
                                PricingTier.DECENA -> "Decenas"
                                PricingTier.DOCENA -> "Docenas"
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(0.9f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formFieldColors()
                )
                OutlinedTextField(
                    value = tierPriceText,
                    onValueChange = { tierPriceText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = {
                        Text(
                            when (selectedTier) {
                                PricingTier.UNIDAD -> "Precio x unidad"
                                PricingTier.DECENA -> "Precio x decena"
                                PricingTier.DOCENA -> "Precio x docena"
                            }
                        )
                    },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formFieldColors()
                )
            }

            if (packQty > 0 && tierPrice > 0.0) {
                val previewUnits = packQty * selectedTier.multiplier
                val previewUnitCost = tierPrice / selectedTier.multiplier
                Text(
                    text = "Equivale a $previewUnits unidades a $${String.format(Locale.US, "%,.0f", previewUnitCost)} c/u (sin envío)",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    if (packQty > 0 && tierPrice > 0.0) {
                        draftLines.add(
                            DraftOrderLine(
                                productId = selectedProduct.id,
                                productName = selectedProduct.name,
                                salePrice = selectedProduct.price,
                                tier = selectedTier,
                                packQuantity = packQty,
                                tierPrice = tierPrice
                            )
                        )
                        packQtyText = "1"
                        tierPriceText = ""
                    }
                },
                enabled = packQty > 0 && tierPrice > 0.0,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Agregar al pedido", maxLines = 1, softWrap = false)
            }
        }

        // 3. Envío y lista de ítems + Equilibrio de ganancia
        DialogSection(title = "3. Envío y equilibrio de ganancia", icon = Icons.AutoMirrored.Filled.TrendingUp) {
            OutlinedTextField(
                value = shippingText,
                onValueChange = { shippingText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Costo total de envío / domicilio") },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            if (draftLines.isEmpty()) {
                Text(
                    text = "Agrega uno o más productos arriba para ver el cálculo de equilibrio.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    draftLines.forEachIndexed { index, line ->
                        val realUnitCost = line.baseUnitCost + shippingPerUnit
                        val unitProfit = line.salePrice - realUnitCost
                        val breakEvenUnits = if (line.salePrice > 0) ceil((line.subtotal + (shippingPerUnit * line.totalUnits)) / line.salePrice).toInt() else 0
                        val isLoss = unitProfit <= 0

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isLoss) colors.errorContainer.copy(alpha = 0.45f) else colors.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "${line.productName} · ${line.packQuantity} ${line.tier.name.lowercase()} (${line.totalUnits} un.)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Costo real con envío: $${String.format(Locale.US, "%,.0f", realUnitCost)}/un. · Venta: $${String.format(Locale.US, "%,.0f", line.salePrice)}/un.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (isLoss)
                                            "¡Alerta! Pierdes $${String.format(Locale.US, "%,.0f", -unitProfit)} por unidad"
                                        else
                                            "Ganancia: +$${String.format(Locale.US, "%,.0f", unitProfit)}/un. · Equilibrio: $breakEvenUnits un.",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLoss) colors.error else colors.primary
                                    )
                                }
                                IconButton(onClick = { draftLines.removeAt(index) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Quitar", tint = colors.error)
                                }
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (projectedNetProfit >= 0) colors.primary.copy(alpha = 0.10f) else colors.errorContainer,
                        border = BorderStroke(1.dp, if (projectedNetProfit >= 0) colors.primary.copy(alpha = 0.3f) else colors.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "EQUILIBRIO FINANCIERO DEL PEDIDO",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (projectedNetProfit >= 0) colors.primary else colors.error
                            )
                            Text(
                                text = "Inversión total (mercancía + envío): $${String.format(Locale.US, "%,.0f", totalOrderCost)} ($grandTotalUnits un.)",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Venta proyectada al público: $${String.format(Locale.US, "%,.0f", projectedRevenue)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Utilidad neta estimada: $${String.format(Locale.US, "%,.0f", projectedNetProfit)} (${String.format(Locale.US, "%.1f", profitMarginPct)}% margen)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (projectedNetProfit >= 0) colors.primary else colors.error
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { receiveImmediately = !receiveImmediately },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ingresar al stock de inmediato",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (receiveImmediately) "Se sumará al inventario apenas guardes" else "Quedará pendiente hasta que llegue el envío",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
                Switch(checked = receiveImmediately, onCheckedChange = { receiveImmediately = it })
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { if (it.length <= 200) notes = it },
                label = { Text("Nota o referencia (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )
        }
    }
}

@Composable
private fun SupplierOrderCard(
    order: SupplierOrder,
    products: List<Product>,
    onMarkReceived: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isPending = order.status.equals("PENDIENTE", ignoreCase = true)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pedido #${order.id} · ${order.supplierName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = order.orderDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
                StatusBadge(
                    text = order.status,
                    style = if (isPending) StatusBadgeStyle.WARNING else StatusBadgeStyle.SUCCESS
                )
            }

            order.items.forEach { item ->
                val currentProd = products.find { it.id == item.productId }
                val marginPerUnit = if (currentProd != null) currentProd.price - item.realUnitCost else 0.0
                Text(
                    text = "• ${item.productName}: ${item.packQuantity} ${item.pricingTier.lowercase()} (${item.totalUnits} un.) · Costo real: $${String.format(Locale.US, "%,.0f", item.realUnitCost)}/un." +
                            if (currentProd != null) " (Ganancia: $${String.format(Locale.US, "%,.0f", marginPerUnit)}/un.)" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: $${String.format(Locale.US, "%,.0f", order.totalCost)} (Envío: $${String.format(Locale.US, "%,.0f", order.shippingCost)})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )

                if (isPending) {
                    Button(
                        onClick = onMarkReceived,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Marcar recibido", style = MaterialTheme.typography.labelMedium, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SupplierCard(
    supplier: Supplier,
    items: List<String>,
    canOrder: Boolean,
    onOrderRequest: () -> Unit,
    onEditRequest: () -> Unit,
    onToggleRequest: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(colors.primary, colors.secondary)))
                        .alpha(if (supplier.isActive) 1f else 0.5f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = supplier.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = supplier.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (supplier.isActive) colors.onSurface else colors.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = supplier.category.ifBlank { "Sin categoría" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (supplier.isActive) {
                    StatusBadge(text = "Activo", style = StatusBadgeStyle.SUCCESS)
                } else {
                    StatusBadge(text = "Inactivo", style = StatusBadgeStyle.DANGER)
                }
            }

            HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.4f))

            InfoRow(icon = Icons.Default.Person, text = supplier.contactName.ifBlank { "Sin contacto" })
            InfoRow(icon = Icons.Default.Phone, text = supplier.phone)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "DISTRIBUYE",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
                if (items.isEmpty()) {
                    Text(
                        text = "Sin productos registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items.forEach { itemName ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = colors.primary.copy(alpha = 0.10f)
                            ) {
                                Text(
                                    text = itemName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.primary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canOrder) {
                    FilledTonalButton(
                        onClick = onOrderRequest,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pedir material", maxLines = 1, softWrap = false)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SupplierActionButton(
                        icon = Icons.Default.Edit,
                        description = "Editar",
                        tint = colors.primary,
                        onClick = onEditRequest
                    )
                    SupplierActionButton(
                        icon = if (supplier.isActive) Icons.Default.Delete else Icons.Default.Restore,
                        description = if (supplier.isActive) "Desactivar" else "Restaurar",
                        tint = if (supplier.isActive) colors.error else colors.primary,
                        onClick = onToggleRequest
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    val colors = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurface
        )
    }
}

@Composable
private fun SupplierActionButton(
    icon: ImageVector,
    description: String,
    tint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(tint.copy(alpha = 0.10f), RoundedCornerShape(50))
    ) {
        Icon(imageVector = icon, contentDescription = description, tint = tint)
    }
}

@Composable
private fun RemovableChip(text: String, onRemove: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(50),
        color = colors.primary.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Quitar $text",
                    tint = colors.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun SuggestionPill(text: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = colors.primary.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurface
            )
        }
    }
}

private fun applyTypedItem(
    text: String,
    products: List<Product>,
    ids: List<Int>,
    extras: List<String>
): Pair<List<Int>, List<String>> {
    val clean = text.trim()
    if (clean.isEmpty()) return ids to extras
    val match = products.firstOrNull { it.name.trim().equals(clean, ignoreCase = true) }
    return when {
        match != null -> (if (match.id in ids) ids else ids + match.id) to extras
        extras.any { it.equals(clean, ignoreCase = true) } -> ids to extras
        else -> ids to (extras + clean)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SupplierFormDialog(
    initial: Supplier?,
    products: List<Product>,
    isSaving: Boolean,
    serverError: String?,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        contactName: String,
        phone: String,
        category: String,
        productIds: List<Int>,
        extraItems: List<String>
    ) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isEditing = initial != null

    var name by remember { mutableStateOf(initial?.name ?: "") }
    var contactName by remember { mutableStateOf(initial?.contactName ?: "") }
    var phone by remember { mutableStateOf(initial?.phone?.filter { it.isDigit() }?.take(10) ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var linkedIds by remember { mutableStateOf(initial?.productIds ?: emptyList()) }
    var manualItems by remember { mutableStateOf(initial?.extraItems ?: emptyList()) }
    var itemInput by remember { mutableStateOf("") }

    var nameTouched by remember { mutableStateOf(false) }
    var contactTouched by remember { mutableStateOf(false) }
    var phoneTouched by remember { mutableStateOf(false) }

    val nameError = ValidationUtils.validatePersonOrCompanyName(name, "El nombre del proveedor")
    val contactError = ValidationUtils.validateContactName(contactName)
    val phoneError = ValidationUtils.validateColombiaPhone(phone)

    val isValid = !isSaving && nameError == null && contactError == null && phoneError == null

    fun addTypedItem() {
        val (ids, extras) = applyTypedItem(itemInput, products, linkedIds, manualItems)
        linkedIds = ids
        manualItems = extras
        itemInput = ""
    }

    val linkedProducts = products.filter { it.id in linkedIds }
    val suggestions = products.filter { it.isActive && it.id !in linkedIds }

    ModernDialog(
        title = if (isEditing) "Editar proveedor" else "Nuevo proveedor",
        subtitle = if (isEditing) "Actualiza sus datos o lo que distribuye." else "Registra quién te surte y qué productos distribuye.",
        icon = if (isEditing) Icons.Default.Edit else Icons.Default.LocalShipping,
        confirmText = if (isSaving) "Guardando..." else if (isEditing) "Guardar cambios" else "Guardar proveedor",
        confirmEnabled = isValid,
        onConfirm = {
            val (finalIds, finalExtras) = applyTypedItem(itemInput, products, linkedIds, manualItems)
            onConfirm(name.trim(), contactName.trim(), phone.trim(), category.trim(), finalIds, finalExtras)
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

        DialogSection(title = "Datos del proveedor", icon = Icons.Default.LocalShipping) {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 60) { name = it; nameTouched = true } },
                label = { Text("Nombre del proveedor o empresa *") },
                isError = nameTouched && nameError != null,
                supportingText = if (nameTouched && nameError != null) {
                    { Text(nameError) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = formFieldColors()
            )

            OutlinedTextField(
                value = contactName,
                onValueChange = { if (it.length <= 50) { contactName = it; contactTouched = true } },
                label = { Text("Persona de contacto (opcional)") },
                isError = contactTouched && contactError != null,
                supportingText = if (contactTouched && contactError != null) {
                    { Text(contactError) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = formFieldColors()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { input ->
                    phone = input.filter { it.isDigit() }.take(10)
                    phoneTouched = true
                },
                label = { Text("Teléfono Colombia (10 dígitos) *") },
                placeholder = { Text("3001234567") },
                prefix = { Text("+57 ") },
                isError = phoneTouched && phoneError != null,
                supportingText = {
                    if (phoneTouched && phoneError != null) {
                        Text(phoneError)
                    } else {
                        Text("${phone.length}/10 dígitos (celular inicia en 3, fijo en 60)")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = formFieldColors()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { if (it.length <= 40) category = it },
                label = { Text("Tipo de negocio o categoría (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = formFieldColors()
            )
        }

        DialogSection(title = "Productos que distribuye (opcional)", icon = Icons.Default.Inventory2) {
            OutlinedTextField(
                value = itemInput,
                onValueChange = { if (it.length <= 50) itemInput = it },
                label = { Text("Escribe un producto y pulsa +") },
                trailingIcon = {
                    IconButton(onClick = { addTypedItem() }, enabled = itemInput.isNotBlank()) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar producto", tint = colors.primary)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { addTypedItem() }),
                colors = formFieldColors()
            )

            if (linkedProducts.isEmpty() && manualItems.isEmpty()) {
                Text(
                    text = "Aún no has agregado productos. Escribe uno arriba o elige de las sugerencias.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    linkedProducts.forEach { product ->
                        RemovableChip(text = product.name, onRemove = { linkedIds = linkedIds - product.id })
                    }
                    manualItems.forEach { item ->
                        RemovableChip(text = item, onRemove = { manualItems = manualItems - item })
                    }
                }
            }

            if (suggestions.isNotEmpty()) {
                Text(
                    text = "SUGERENCIAS DE TU INVENTARIO",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestions.forEach { product ->
                        SuggestionPill(
                            text = product.name,
                            onClick = { linkedIds = linkedIds + product.id }
                        )
                    }
                }
            }
        }
    }
}