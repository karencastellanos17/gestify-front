package com.example.inventario.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.inventario.data.model.Product
import com.example.inventario.data.model.Supplier
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

@Composable
fun SuppliersScreen(
    viewModel: GestifyViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val isPhone = LocalConfiguration.current.screenWidthDp < 700

    val suppliers by viewModel.suppliers.collectAsState()
    val products by viewModel.products.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var supplierToEdit by remember { mutableStateOf<Supplier?>(null) }
    var supplierToToggle by remember { mutableStateOf<Supplier?>(null) }
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
            title = "Proveedores",
            subtitle = "Directorio conectado al backend para gestionar tus abastecedores.",
            icon = Icons.Default.LocalShipping,
            action = {
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo Proveedor", style = MaterialTheme.typography.titleSmall)
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
                    name = name,
                    contactName = contactName,
                    phone = phone,
                    category = category,
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
                    name = name,
                    contactName = contactName,
                    phone = phone,
                    category = category,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SupplierCard(
    supplier: Supplier,
    items: List<String>,
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
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
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