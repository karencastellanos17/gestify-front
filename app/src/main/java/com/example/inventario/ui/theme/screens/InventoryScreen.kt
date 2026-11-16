package com.example.inventario.ui.theme.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.data.model.Product
import com.example.inventario.ui.theme.components.CategoryPicker
import com.example.inventario.ui.theme.components.CategoryTag
import com.example.inventario.ui.theme.components.DialogSection
import com.example.inventario.ui.theme.components.EmptyState
import com.example.inventario.ui.theme.components.ModernDialog
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.ProductImage
import com.example.inventario.ui.theme.components.StatCard
import com.example.inventario.ui.theme.components.StatusBadge
import com.example.inventario.ui.theme.components.StatusBadgeStyle
import com.example.inventario.ui.theme.components.WaveFooter
import com.example.inventario.ui.theme.components.formFieldColors
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import com.example.inventario.utils.ValidationUtils
import java.io.File
import java.util.Locale

@Composable
fun InventoryScreen(
    viewModel: GestifyViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val isPhone = LocalConfiguration.current.screenWidthDp < 700

    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var productToToggle by remember { mutableStateOf<Product?>(null) }
    var isGalleryView by remember { mutableStateOf(isPhone) }

    val activeCount = products.count { it.isActive }
    val lowStockCount = products.count { it.isActive && it.stock < 5 }
    val inventoryValue = products.filter { it.isActive }.sumOf { it.price * it.stock }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModuleBanner(
            title = "Control de Inventario y Stock",
            subtitle = "Gestión de productos conectada al backend con Cloudinary.",
            icon = Icons.Default.Inventory2,
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
                    Icon(Icons.Default.Add, contentDescription = "Agregar", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo Producto", style = MaterialTheme.typography.titleSmall)
                }
            }
        )

        if (isPhone) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    label = "Productos activos",
                    value = "$activeCount de ${products.size}",
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.fillMaxWidth()
                )
                StatCard(
                    label = "Stock bajo",
                    value = "$lowStockCount productos",
                    icon = Icons.Default.Warning,
                    accent = colors.error,
                    modifier = Modifier.fillMaxWidth()
                )
                StatCard(
                    label = "Valor del inventario",
                    value = "$${String.format(Locale.US, "%,.0f", inventoryValue)}",
                    icon = Icons.Default.Payments,
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
                    label = "Productos activos",
                    value = "$activeCount de ${products.size}",
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                StatCard(
                    label = "Stock bajo",
                    value = "$lowStockCount productos",
                    icon = Icons.Default.Warning,
                    accent = colors.error,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                StatCard(
                    label = "Valor del inventario",
                    value = "$${String.format(Locale.US, "%,.0f", inventoryValue)}",
                    icon = Icons.Default.Payments,
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
                            text = "Catálogo de productos",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.primary,
                            modifier = Modifier.weight(1f)
                        )

                        if (!isPhone) {
                            val chipColors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.primary.copy(alpha = 0.15f),
                                selectedLabelColor = colors.primary,
                                selectedLeadingIconColor = colors.primary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = !isGalleryView,
                                    onClick = { isGalleryView = false },
                                    label = { Text("Lista") },
                                    leadingIcon = {
                                        Icon(Icons.AutoMirrored.Filled.ViewList, contentDescription = null, modifier = Modifier.size(18.dp))
                                    },
                                    colors = chipColors,
                                    shape = RoundedCornerShape(50)
                                )
                                FilterChip(
                                    selected = isGalleryView,
                                    onClick = { isGalleryView = true },
                                    label = { Text("Galería") },
                                    leadingIcon = {
                                        Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(18.dp))
                                    },
                                    colors = chipColors,
                                    shape = RoundedCornerShape(50)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (products.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Inventory2,
                            title = "Aún no tienes productos",
                            message = "Agrega tu primer producto para empezar a controlar tu stock.",
                            actionText = "Nuevo Producto",
                            onAction = {
                                viewModel.clearError()
                                showAddDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )
                    } else if (isPhone || isGalleryView) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            products.forEach { product ->
                                ProductGalleryCard(
                                    product = product,
                                    onEditRequest = {
                                        viewModel.clearError()
                                        productToEdit = product
                                    },
                                    onToggleRequest = { productToToggle = product }
                                )
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
                                TableHeaderCell("ID", modifier = Modifier.weight(0.6f))
                                TableHeaderCell("PRODUCTO", modifier = Modifier.weight(2.2f))
                                TableHeaderCell("CATEGORÍA", modifier = Modifier.weight(1.5f))
                                TableHeaderCell("PRECIO", modifier = Modifier.weight(1.1f))
                                TableHeaderCell("STOCK", modifier = Modifier.weight(1f))
                                TableHeaderCell("ESTADO", modifier = Modifier.weight(1.2f))
                                TableHeaderCell("ACCIONES", modifier = Modifier.weight(1.3f))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            products.forEach { product ->
                                ProductTableRow(
                                    product = product,
                                    onEditRequest = {
                                        viewModel.clearError()
                                        productToEdit = product
                                    },
                                    onToggleRequest = { productToToggle = product }
                                )
                                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductFormDialog(
            initial = null,
            existingCategories = products.map { it.category }.distinct(),
            isSaving = viewModel.isLoading,
            serverError = viewModel.errorMessage,
            onDismiss = {
                viewModel.clearError()
                showAddDialog = false
            },
            onConfirm = { name, category, price, stock, imageUriString ->
                val imageFile = imageUriString?.let { uriString ->
                    if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
                        uriToFile(context, Uri.parse(uriString))
                    } else null
                }
                val newProduct = Product(
                    id = 0,
                    name = name,
                    category = category,
                    price = price,
                    stock = stock,
                    isActive = true,
                    imageUri = if (imageUriString?.startsWith("http") == true) imageUriString else null
                )
                viewModel.createProductWithImage(newProduct, imageFile) {
                    showAddDialog = false
                }
            }
        )
    }

    productToEdit?.let { target ->
        ProductFormDialog(
            initial = target,
            existingCategories = products.map { it.category }.distinct(),
            isSaving = viewModel.isLoading,
            serverError = viewModel.errorMessage,
            onDismiss = {
                viewModel.clearError()
                productToEdit = null
            },
            onConfirm = { name, category, price, stock, imageUriString ->
                val imageFile = imageUriString?.let { uriString ->
                    if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
                        uriToFile(context, Uri.parse(uriString))
                    } else null
                }
                val updatedProduct = target.copy(
                    name = name,
                    category = category,
                    price = price,
                    stock = stock,
                    imageUri = if (imageUriString?.startsWith("http") == true) imageUriString else target.imageUri
                )
                viewModel.createProductWithImage(updatedProduct, imageFile) {
                    productToEdit = null
                }
            }
        )
    }

    productToToggle?.let { target ->
        val deactivating = target.isActive
        ModernDialog(
            title = if (deactivating) "¿Desactivar producto?" else "¿Restaurar producto?",
            subtitle = if (deactivating) "Esta acción aplica Soft Delete." else "El producto volverá a estar disponible.",
            icon = if (deactivating) Icons.Default.Delete else Icons.Default.Restore,
            confirmText = if (deactivating) "Sí, desactivar" else "Sí, restaurar",
            danger = deactivating,
            onConfirm = {
                viewModel.toggleProduct(target.id)
                productToToggle = null
            },
            onDismiss = { productToToggle = null }
        ) {
            Text(
                text = if (deactivating)
                    "«${target.name}» dejará de estar disponible para la venta, pero su historial se conserva y podrás restaurarlo cuando quieras."
                else
                    "«${target.name}» volverá a aparecer como producto activo en tu inventario.",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant
            )
        }
    }
}

private fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
private fun ProductFormDialog(
    initial: Product?,
    existingCategories: List<String>,
    isSaving: Boolean,
    serverError: String?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, price: Double, stock: Int, imageUri: String?) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isEditing = initial != null

    var name by remember { mutableStateOf(initial?.name ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var price by remember {
        mutableStateOf(
            initial?.price?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: ""
        )
    }
    var stock by remember { mutableStateOf(initial?.stock?.toString() ?: "") }
    var imageUri by remember { mutableStateOf(initial?.imageUri) }

    var nameTouched by remember { mutableStateOf(false) }
    var categoryTouched by remember { mutableStateOf(false) }
    var priceTouched by remember { mutableStateOf(false) }
    var stockTouched by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) imageUri = uri.toString()
    }

    val nameError = ValidationUtils.validateProductName(name)
    val priceError = ValidationUtils.validatePrice(price)
    val stockError = ValidationUtils.validateStock(stock)

    val priceValue = price.toDoubleOrNull()
    val stockValue = stock.toIntOrNull()

    val isValid = !isSaving &&
            nameError == null &&
            category.isNotBlank() &&
            priceError == null &&
            stockError == null &&
            priceValue != null &&
            stockValue != null

    ModernDialog(
        title = if (isEditing) "Editar producto" else "Nuevo producto",
        subtitle = if (isEditing) "Modifica los datos o cambia la foto." else "Completa los datos para agregarlo al inventario.",
        icon = if (isEditing) Icons.Default.Edit else Icons.Default.Inventory2,
        confirmText = if (isSaving) "Guardando..." else if (isEditing) "Guardar cambios" else "Guardar producto",
        confirmEnabled = isValid,
        onConfirm = { onConfirm(name.trim(), category.trim(), priceValue!!, stockValue!!, imageUri) },
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

        DialogSection(title = "Foto del producto", icon = Icons.Default.AddPhotoAlternate) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.primary.copy(alpha = 0.06f))
                    .border(BorderStroke(1.5.dp, colors.primary.copy(alpha = 0.4f)), RoundedCornerShape(16.dp))
                    .clickable { picker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    ProductImage(
                        imageUri = imageUri,
                        fallbackLabel = name.take(1).uppercase(),
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        onClick = { imageUri = null },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp),
                        shape = RoundedCornerShape(50),
                        color = colors.surface.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "Quitar foto",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.error,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                        shape = RoundedCornerShape(50),
                        color = colors.surface.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "Cambiar foto",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Subir foto del producto",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.primary
                        )
                        Text(
                            text = "Opcional · JPG o PNG",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }

        DialogSection(title = "Información", icon = Icons.Default.Inventory2) {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 80) { name = it; nameTouched = true } },
                label = { Text("Nombre del producto *") },
                isError = nameTouched && nameError != null,
                supportingText = if (nameTouched && nameError != null) {
                    { Text(nameError) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = formFieldColors()
            )
        }

        DialogSection(title = "Categoría", icon = Icons.Default.Category) {
            CategoryPicker(
                value = category,
                onValueChange = { category = it; categoryTouched = true },
                existingCategories = existingCategories,
                isError = categoryTouched && category.isBlank(),
                errorMessage = "La categoría es obligatoria"
            )
        }

        DialogSection(title = "Precio y stock", icon = Icons.Default.Payments) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' }; priceTouched = true },
                    label = { Text("Precio *") },
                    prefix = { Text("$") },
                    isError = priceTouched && priceError != null,
                    supportingText = if (priceTouched && priceError != null) {
                        { Text(priceError) }
                    } else null,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = formFieldColors()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it.filter { c -> c.isDigit() }; stockTouched = true },
                    label = { Text(if (isEditing) "Stock *" else "Stock inicial *") },
                    isError = stockTouched && stockError != null,
                    supportingText = if (stockTouched && stockError != null) {
                        { Text(stockError) }
                    } else null,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = formFieldColors()
                )
            }
        }
    }
}

@Composable
private fun ProductTableRow(
    product: Product,
    onEditRequest: () -> Unit,
    onToggleRequest: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val rowBgColor by animateColorAsState(
        targetValue = if (product.isActive)
            colors.surface
        else
            colors.errorContainer.copy(alpha = 0.18f),
        animationSpec = tween(durationMillis = 250),
        label = "RowBg"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = rowBgColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "#${product.id}",
                modifier = Modifier.weight(0.6f),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )

            Row(
                modifier = Modifier.weight(2.2f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProductImage(
                    imageUri = product.imageUri,
                    fallbackLabel = product.name.take(1).uppercase(),
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .alpha(if (product.isActive) 1f else 0.5f)
                )
                Text(
                    product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (product.isActive) colors.onSurface else colors.onSurface.copy(alpha = 0.6f)
                )
            }

            Box(modifier = Modifier.weight(1.5f)) {
                CategoryTag(name = product.category)
            }

            Text(
                "$${String.format(Locale.US, "%,.0f", product.price)}",
                modifier = Modifier.weight(1.1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )

            Text(
                "${product.stock} un.",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (product.stock < 5) FontWeight.Bold else FontWeight.Normal,
                color = if (product.stock < 5) colors.error else colors.onSurface
            )

            Box(modifier = Modifier.weight(1.2f)) {
                if (product.isActive) {
                    StatusBadge(text = "Activo", style = StatusBadgeStyle.SUCCESS)
                } else {
                    StatusBadge(text = "Eliminado", style = StatusBadgeStyle.DANGER)
                }
            }

            Row(
                modifier = Modifier.weight(1.3f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditActionButton(onClick = onEditRequest)
                ToggleActionButton(isActive = product.isActive, onClick = onToggleRequest)
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = colors.primary
    )
}

@Composable
private fun EditActionButton(onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar",
            tint = colors.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ToggleActionButton(isActive: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp)
    ) {
        Icon(
            imageVector = if (isActive) Icons.Default.Delete else Icons.Default.Restore,
            contentDescription = if (isActive) "Desactivar" else "Restaurar",
            tint = if (isActive) colors.error else colors.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ProductGalleryCard(
    product: Product,
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
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                ProductImage(
                    imageUri = product.imageUri,
                    fallbackLabel = product.name.take(1).uppercase(),
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (product.isActive) 1f else 0.45f),
                    textStyle = MaterialTheme.typography.headlineLarge
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    if (product.isActive) {
                        StatusBadge(text = "Activo", style = StatusBadgeStyle.SUCCESS)
                    } else {
                        StatusBadge(text = "Eliminado", style = StatusBadgeStyle.DANGER)
                    }
                }
            }

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryTag(name = product.category)
                    Text(
                        "${product.stock} un.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (product.stock < 5) FontWeight.Bold else FontWeight.Normal,
                        color = if (product.stock < 5) colors.error else colors.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$${String.format(Locale.US, "%,.0f", product.price)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EditActionButton(onClick = onEditRequest)
                        ToggleActionButton(isActive = product.isActive, onClick = onToggleRequest)
                    }
                }
            }
        }
    }
}