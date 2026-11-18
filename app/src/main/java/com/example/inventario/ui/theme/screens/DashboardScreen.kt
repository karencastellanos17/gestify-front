package com.example.inventario.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.ui.theme.components.BrandBackdrop
import com.example.inventario.ui.theme.components.GestifyMark
import com.example.inventario.ui.theme.components.InventoryLogo
import com.example.inventario.ui.theme.components.SalesBarChart
import com.example.inventario.ui.theme.components.StatusBadge
import com.example.inventario.ui.theme.components.StatusBadgeStyle
import com.example.inventario.ui.theme.components.StockDonutChart
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

    val salesTotal = sales.sumOf { it.total }
    val activeProducts = products.filter { it.isActive }
    val stockUnits = activeProducts.sumOf { it.stock }
    val categoryCount = activeProducts.map { it.category }.distinct().size

    val stockAlerts = buildStockAlerts(products)
    val criticalAlerts = stockAlerts.count { it.type == AlertType.CRITICAL }

    val salesChartData = sales.take(6).reversed().map { it.productName.substringBefore(' ') to it.total }
    val stockChartData = activeProducts
        .groupBy { it.category }
        .map { (category, items) -> category to items.sumOf { it.stock } }
        .filter { it.second > 0 }

    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val screenHeight = config.screenHeightDp

    // Si la pantalla es angosta (vertical) o el alto es muy pequeño (celular horizontal), apilamos en vertical
    val isCompact = screenWidth < 700 || screenHeight < 550
    val showLogo = screenWidth >= 1100

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(colors.primary, colors.secondary)))
        ) {
            BrandBackdrop(
                modifier = Modifier.matchParentSize(),
                tint = colors.onPrimary,
                scale = 0.6f
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isCompact) 20.dp else 32.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GestifyMark(size = 36.dp, onGradient = true)
                        Text(
                            text = "Gestify",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            fontWeight = FontWeight.Black,
                            color = colors.onPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.logout()
                            onLogout()
                        },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, colors.onPrimary.copy(alpha = 0.7f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colors.onPrimary.copy(alpha = 0.15f),
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Salir",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Text(
                    text = "¡Bienvenido a Gestify!",
                    style = if (isCompact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineLarge,
                    color = colors.onPrimary
                )
                Text(
                    text = "Resumen ejecutivo del estado actual de tu negocio en tiempo real.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onPrimary.copy(alpha = 0.9f)
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = colors.onPrimary.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "GESTIONAR MEJOR, CRECER MÁS",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            if (showLogo) {
                InventoryLogo(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 48.dp, bottom = 20.dp),
                    logoSize = 96.dp
                )
            }
        }

        // Tarjetas de Métricas Superiores (Responsivas sin IntrinsicSize)
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Ventas del Día",
                    value = "$${String.format(Locale.US, "%,.0f", salesTotal)}",
                    subtitle = "${sales.size} transacciones",
                    badgeStyle = StatusBadgeStyle.SUCCESS,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onGoToSales
                )
                MetricCard(
                    title = "Stock Activo",
                    value = "$stockUnits Unidades",
                    subtitle = "$categoryCount Categorías",
                    badgeStyle = StatusBadgeStyle.INFO,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onGoToStock
                )
                MetricCard(
                    title = "Alertas Activas",
                    value = "${stockAlerts.size} Avisos",
                    subtitle = "$criticalAlerts Stock Crítico",
                    badgeStyle = if (criticalAlerts > 0) StatusBadgeStyle.DANGER else StatusBadgeStyle.SUCCESS,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onGoToAlerts
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(
                    title = "Ventas del Día",
                    value = "$${String.format(Locale.US, "%,.0f", salesTotal)}",
                    subtitle = "${sales.size} transacciones",
                    badgeStyle = StatusBadgeStyle.SUCCESS,
                    modifier = Modifier.weight(1f),
                    onClick = onGoToSales
                )
                MetricCard(
                    title = "Stock Activo",
                    value = "$stockUnits Unidades",
                    subtitle = "$categoryCount Categorías",
                    badgeStyle = StatusBadgeStyle.INFO,
                    modifier = Modifier.weight(1f),
                    onClick = onGoToStock
                )
                MetricCard(
                    title = "Alertas Activas",
                    value = "${stockAlerts.size} Avisos",
                    subtitle = "$criticalAlerts Stock Crítico",
                    badgeStyle = if (criticalAlerts > 0) StatusBadgeStyle.DANGER else StatusBadgeStyle.SUCCESS,
                    modifier = Modifier.weight(1f),
                    onClick = onGoToAlerts
                )
            }
        }

        // Gráficos (Responsivos sin restricciones forzadas de alto)
        if (isCompact) {
            ChartCard(
                title = "Ventas recientes",
                subtitle = "Total por transacción",
                modifier = Modifier.fillMaxWidth()
            ) {
                SalesBarChart(
                    data = salesChartData,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ChartCard(
                title = "Stock por categoría",
                subtitle = "Unidades de productos activos",
                modifier = Modifier.fillMaxWidth()
            ) {
                StockDonutChart(
                    data = stockChartData,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChartCard(
                    title = "Ventas recientes",
                    subtitle = "Total por transacción",
                    modifier = Modifier.weight(1.5f)
                ) {
                    SalesBarChart(
                        data = salesChartData,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                ChartCard(
                    title = "Stock por categoría",
                    subtitle = "Unidades de productos activos",
                    modifier = Modifier.weight(1f)
                ) {
                    StockDonutChart(
                        data = stockChartData,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box {
            WaveFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.BottomCenter),
                color = colors.primary.copy(alpha = 0.06f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.primary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    badgeStyle: StatusBadgeStyle,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme

    Card(
        onClick = { onClick?.invoke() },
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .background(colors.primary.copy(alpha = 0.06f), CircleShape)
            )

            WaveFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.BottomCenter),
                color = colors.primary.copy(alpha = 0.07f)
            )

            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(Brush.horizontalGradient(listOf(colors.primary, colors.secondary)))
                )

                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(colors.primary, CircleShape)
                        )
                        Text(
                            text = title.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        if (onClick != null) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Ver detalle",
                                tint = colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )

                    StatusBadge(
                        text = subtitle,
                        style = badgeStyle
                    )
                }
            }
        }
    }
}