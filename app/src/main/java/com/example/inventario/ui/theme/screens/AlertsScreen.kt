package com.example.inventario.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.inventario.data.model.Product
import com.example.inventario.data.model.Supplier
import com.example.inventario.data.model.distributes
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.StatCard
import com.example.inventario.ui.theme.components.StatusBadge
import com.example.inventario.ui.theme.components.StatusBadgeStyle
import com.example.inventario.ui.theme.components.WaveFooter

// Umbrales de stock (ajústalos a tu negocio)
const val LOW_STOCK_LIMIT = 5        // menos de 5 unidades genera alerta
const val CRITICAL_STOCK_LIMIT = 2   // 2 o menos es crítica
const val RESTOCK_TARGET = 15        // stock al que se recomienda volver

// Modelo para las alertas del sistema
data class AlertItem(
    val id: Int,
    val title: String,
    val description: String,
    val type: AlertType, // Urgente (Crítica) o Advertencia
    val suppliers: List<Supplier> = emptyList(), // Proveedores activos que distribuyen el producto
    val suggestedQuantity: Int = 0
)

enum class AlertType {
    CRITICAL, WARNING
}

/** Genera las alertas a partir del stock real de los productos activos. */
fun buildStockAlerts(
    products: List<Product>,
    suppliers: List<Supplier> = emptyList()
): List<AlertItem> =
    products
        .filter { it.isActive && it.stock < LOW_STOCK_LIMIT }
        .sortedBy { it.stock }
        .map { product ->
            val isCritical = product.stock <= CRITICAL_STOCK_LIMIT
            AlertItem(
                id = product.id,
                title = when {
                    product.stock == 0 -> "Stock Agotado: ${product.name}"
                    isCritical -> "Stock Crítico: ${product.name}"
                    else -> "Stock Bajo: ${product.name}"
                },
                description = if (product.stock == 0)
                    "El producto no tiene unidades disponibles."
                else
                    "Quedan solo ${product.stock} unidades en inventario.",
                type = if (isCritical) AlertType.CRITICAL else AlertType.WARNING,
                suppliers = suppliers.filter { it.isActive && it.distributes(product) },
                suggestedQuantity = RESTOCK_TARGET - product.stock
            )
        }

@Composable
fun AlertsScreen(
    products: List<Product>,
    suppliers: List<Supplier>,
    onGoToSuppliers: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    val alertsList = remember(products, suppliers) { buildStockAlerts(products, suppliers) }
    val criticalCount = alertsList.count { it.type == AlertType.CRITICAL }
    val warningCount = alertsList.count { it.type == AlertType.WARNING }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ───────── Banner del módulo ─────────
        ModuleBanner(
            title = "Centro de Alertas y Notificaciones",
            subtitle = "Monitoreo en tiempo real de stock mínimo y recomendación de proveedor.",
            icon = Icons.Default.Notifications
        )

        // ───────── Resumen por tipo de alerta ─────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                label = "Críticas",
                value = "$criticalCount alertas",
                icon = Icons.Default.Warning,
                accent = colors.error,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            StatCard(
                label = "Avisos",
                value = "$warningCount alertas",
                icon = Icons.Default.Info,
                accent = colors.secondary,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            StatCard(
                label = "Total pendientes",
                value = "${alertsList.size} alertas",
                icon = Icons.Default.Notifications,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // ───────── Listado de alertas ─────────
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
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Avisos Pendientes de Atención",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.primary
                        )

                        StatusBadge(
                            text = "${alertsList.size} Pendientes",
                            style = if (alertsList.isEmpty()) StatusBadgeStyle.SUCCESS else StatusBadgeStyle.WARNING
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (alertsList.isEmpty()) {
                        // Estado vacío: todo en orden
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Todo en orden",
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.onSurface
                            )
                            Text(
                                text = "Ningún producto activo tiene stock bajo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(alertsList, key = { it.id }) { alert ->
                                AlertCard(alert = alert, onGoToSuppliers = onGoToSuppliers)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(
    alert: AlertItem,
    onGoToSuppliers: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isCritical = alert.type == AlertType.CRITICAL
    // Colores 100% del tema: error para críticas, secondary para avisos
    val accent = if (isCritical) colors.error else colors.secondary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(accent.copy(alpha = 0.06f))
        ) {
            // Barra lateral de color
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(accent)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(accent.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCritical) Icons.Default.Warning else Icons.Default.Info,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isCritical) colors.error else colors.onSurface
                        )
                        Text(
                            text = alert.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    StatusBadge(
                        text = if (isCritical) "Urgente" else "Aviso",
                        style = if (isCritical) StatusBadgeStyle.DANGER else StatusBadgeStyle.WARNING,
                        icon = if (isCritical) Icons.Default.Warning else Icons.Default.Info
                    )
                }

                SupplierSuggestion(alert = alert, onGoToSuppliers = onGoToSuppliers)
            }
        }
    }
}

@Composable
private fun SupplierSuggestion(
    alert: AlertItem,
    onGoToSuppliers: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "PROVEEDOR RECOMENDADO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }

            if (alert.suppliers.isEmpty()) {
                Text(
                    text = "Este producto no tiene un proveedor asociado todavía.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
                TextButton(onClick = onGoToSuppliers) {
                    Text("Ir a Proveedores", color = colors.primary)
                }
            } else {
                alert.suppliers.forEach { supplier ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(colors.primary, colors.secondary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = supplier.name.take(1).uppercase(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.onPrimary
                            )
                        }
                        Column {
                            Text(
                                text = supplier.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onSurface
                            )
                            Text(
                                text = "Contacto: ${supplier.contactName.ifBlank { "—" }}  ·  Tel: ${supplier.phone}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )
                            if (supplier.category.isNotBlank()) {
                                Text(
                                    text = "Distribuye: ${supplier.category}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Sugerencia: pedir ${alert.suggestedQuantity} unidades para volver a $RESTOCK_TARGET en stock.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface
                )
            }
        }
    }
}