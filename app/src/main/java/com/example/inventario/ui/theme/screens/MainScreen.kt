package com.example.inventario.ui.theme.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.ui.theme.Temas.InventarioTheme
import com.example.inventario.ui.theme.components.BrandBackdrop
import com.example.inventario.ui.theme.components.GestifyMark
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import com.example.inventario.utils.AppThemeColor

// Entrada del menú lateral
private data class NavEntry(
    val tab: Int,
    val label: String,
    val description: String,
    val icon: ImageVector
)

private val topNavItems = listOf(
    NavEntry(0, "Inicio", "Resumen general de tu negocio", Icons.Default.Home),
    NavEntry(1, "Stock", "Tus productos e inventario", Icons.AutoMirrored.Filled.List),
    NavEntry(2, "Ventas", "Caja y transacciones", Icons.Default.ShoppingCart),
    NavEntry(3, "Alertas", "Stock bajo y pedidos", Icons.Default.Info),
    NavEntry(5, "Proveedores", "Quién te surte cada producto", Icons.Default.LocalShipping)
)

private val bottomNavItems = listOf(
    NavEntry(6, "Guía", "Quiénes somos y cómo usar la app", Icons.AutoMirrored.Filled.MenuBook),
    NavEntry(4, "Ajustes", "Paleta de color y apariencia", Icons.Default.Settings)
)

@Composable
fun MainScreen(
    onLogout: () -> Unit = {},
    viewModel: GestifyViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentThemeColor by remember { mutableStateOf(AppThemeColor.BLUE) }
    var isDarkMode by remember { mutableStateOf(false) }

    // ─── AQUÍ ESTABA EL DETALLE: Se declaran y recolectan del ViewModel ───
    val products by viewModel.products.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()
    // ─────────────────────────────────────────────────────────────────────

    InventarioTheme(selectedColor = currentThemeColor, darkTheme = isDarkMode) {
        val colors = MaterialTheme.colorScheme

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            // Menú lateral
            // Variable para saber si la ventana es angosta y compactar el menú
            val compactMenu = LocalConfiguration.current.screenWidthDp < 1100
            GestifySidebar(
                selectedTab = selectedTab,
                onSelect = { selectedTab = it },
                isDarkMode = isDarkMode,
                onDarkModeChange = { isDarkMode = it },
                compact = compactMenu
            )

            if (isDarkMode) {
                VerticalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
            }

            // Contenido principal
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            listOf(colors.primary.copy(alpha = 0.08f), colors.background)
                        )
                    )
            ) {
                BrandBackdrop(
                    modifier = Modifier.fillMaxSize(),
                    tint = colors.primary,
                    scale = 0.9f,
                    alphaScale = 0.6f
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Crossfade(
                        targetState = selectedTab,
                        animationSpec = tween(durationMillis = 250),
                        label = "ScreenTransition"
                    ) { tab ->
                        when (tab) {
                            0 -> DashboardScreen(
                                onLogout = onLogout,
                                onGoToSales = { selectedTab = 2 },
                                onGoToStock = { selectedTab = 1 },
                                onGoToAlerts = { selectedTab = 3 }
                            )
                            1 -> InventoryScreen()
                            2 -> SalesScreen()
                            3 -> AlertsScreen(
                                products = products,
                                suppliers = suppliers,
                                onGoToSuppliers = { selectedTab = 5 }
                            )

                            4 -> SettingsScreen(
                                selectedThemeColor = currentThemeColor,
                                onThemeSelected = { newColor -> currentThemeColor = newColor }
                            )
                            5 -> SuppliersScreen()
                            6 -> AboutScreen()
                        }
                    }
                }
            }
        }
    }
}

// Colores del menú según el modo
private data class SidebarPalette(
    val content: Color,
    val contentMuted: Color,
    val accent: Color,
    val chipBackground: Color,
    val selectedBackground: Color,
    val selectedText: Color,
    val iconSelectedBackground: Color,
    val iconSelectedTint: Color,
    val iconIdleBackground: Color,
    val iconIdleTint: Color
)

@Composable
private fun sidebarPalette(isDarkMode: Boolean): SidebarPalette {
    val c = MaterialTheme.colorScheme
    return if (isDarkMode) {
        SidebarPalette(
            content = c.onSurface,
            contentMuted = c.onSurfaceVariant,
            accent = c.primary,
            chipBackground = c.primary.copy(alpha = 0.12f),
            selectedBackground = c.primary.copy(alpha = 0.18f),
            selectedText = c.primary,
            iconSelectedBackground = c.primary,
            iconSelectedTint = c.onPrimary,
            iconIdleBackground = c.onSurface.copy(alpha = 0.08f),
            iconIdleTint = c.onSurfaceVariant
        )
    } else {
        SidebarPalette(
            content = c.onPrimary,
            contentMuted = c.onPrimary.copy(alpha = 0.8f),
            accent = c.onPrimary,
            chipBackground = c.onPrimary.copy(alpha = 0.16f),
            selectedBackground = c.onPrimary.copy(alpha = 0.24f),
            selectedText = c.onPrimary,
            iconSelectedBackground = c.onPrimary,
            iconSelectedTint = c.primary,
            iconIdleBackground = c.onPrimary.copy(alpha = 0.14f),
            iconIdleTint = c.onPrimary
        )
    }
}

@Composable
private fun GestifySidebar(
    selectedTab: Int,
    onSelect: (Int) -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    compact: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val palette = sidebarPalette(isDarkMode)

    val allItems = topNavItems + bottomNavItems
    val position = allItems.indexOfFirst { it.tab == selectedTab }.coerceAtLeast(0)
    val current = allItems[position]

    val width by animateDpAsState(
        targetValue = if (compact) 84.dp else 232.dp,
        animationSpec = tween(durationMillis = 300),
        label = "SidebarWidth"
    )

    val shift by animateFloatAsState(
        targetValue = position / (allItems.size - 1).toFloat(),
        animationSpec = tween(durationMillis = 700),
        label = "SidebarShift"
    )

    val baseModifier = if (isDarkMode) {
        Modifier.background(colors.surface)
    } else {
        Modifier.background(
            Brush.verticalGradient(
                0f to colors.primary,
                (0.25f + 0.5f * shift) to colors.secondary,
                1f to colors.primary
            )
        )
    }

    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .then(baseModifier)
    ) {
        if (isDarkMode) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0f to colors.primary.copy(alpha = 0.08f),
                            (0.25f + 0.5f * shift) to colors.secondary.copy(alpha = 0.20f),
                            1f to colors.primary.copy(alpha = 0.08f)
                        )
                    )
            )
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val cy = h * (0.12f + 0.70f * shift)

            drawCircle(
                color = palette.accent.copy(alpha = 0.10f),
                radius = 130.dp.toPx(),
                center = Offset(w * 0.95f, cy)
            )
            drawCircle(
                color = palette.accent.copy(alpha = 0.16f),
                radius = 170.dp.toPx(),
                center = Offset(w * 0.95f, cy),
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = palette.accent.copy(alpha = 0.08f),
                radius = 100.dp.toPx(),
                center = Offset(0f, h * (0.90f - 0.55f * shift))
            )
            drawCircle(
                color = palette.accent.copy(alpha = 0.12f),
                radius = 60.dp.toPx(),
                center = Offset(w * 0.15f, h * (0.30f + 0.25f * shift)),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compact) 18.dp else 14.dp, vertical = 20.dp)
        ) {
            if (compact) {
                GestifyMark(size = 48.dp, onGradient = !isDarkMode)
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    GestifyMark(size = 44.dp, onGradient = !isDarkMode)
                    Column {
                        Text(
                            text = "Gestify",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = palette.content
                        )
                        Text(
                            text = "Gestionar mejor, crecer más",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.contentMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!compact) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.chipBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Crossfade(
                        targetState = current,
                        animationSpec = tween(durationMillis = 350),
                        label = "CurrentSection"
                    ) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = entry.icon,
                                contentDescription = null,
                                tint = palette.accent,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = entry.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.content
                                )
                                Text(
                                    text = entry.description,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = palette.contentMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                topNavItems.forEach { entry ->
                    SidebarItem(
                        entry = entry,
                        isSelected = selectedTab == entry.tab,
                        compact = compact,
                        palette = palette,
                        onClick = { onSelect(entry.tab) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                bottomNavItems.forEach { entry ->
                    SidebarItem(
                        entry = entry,
                        isSelected = selectedTab == entry.tab,
                        compact = compact,
                        palette = palette,
                        onClick = { onSelect(entry.tab) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            DarkModeToggle(
                isDarkMode = isDarkMode,
                onChange = onDarkModeChange,
                compact = compact,
                palette = palette
            )
        }
    }
}

@Composable
private fun SidebarItem(
    entry: NavEntry,
    isSelected: Boolean,
    compact: Boolean,
    palette: SidebarPalette,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (isSelected) palette.selectedBackground else palette.selectedBackground.copy(alpha = 0f),
        animationSpec = tween(durationMillis = 250),
        label = "ItemBackground"
    )
    val iconBackground by animateColorAsState(
        targetValue = if (isSelected) palette.iconSelectedBackground else palette.iconIdleBackground,
        animationSpec = tween(durationMillis = 250),
        label = "ItemIconBackground"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) palette.iconSelectedTint else palette.iconIdleTint,
        animationSpec = tween(durationMillis = 250),
        label = "ItemIconTint"
    )

    val iconBox: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(iconBackground, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = entry.icon,
                contentDescription = entry.label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (compact) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(background)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            iconBox()
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            iconBox()

            Text(
                text = entry.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) palette.selectedText else palette.content,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = palette.selectedText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun DarkModeToggle(
    isDarkMode: Boolean,
    onChange: (Boolean) -> Unit,
    compact: Boolean,
    palette: SidebarPalette
) {
    val colors = MaterialTheme.colorScheme

    if (compact) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(palette.chipBackground)
                .clickable { onChange(!isDarkMode) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = "Cambiar entre modo claro y oscuro",
                tint = palette.content,
                modifier = Modifier.size(22.dp)
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(palette.chipBackground)
                .clickable { onChange(!isDarkMode) }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null,
                tint = palette.content,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = if (isDarkMode) "Modo oscuro" else "Modo claro",
                style = MaterialTheme.typography.titleSmall,
                color = palette.content,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isDarkMode,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = if (isDarkMode) colors.onPrimary else colors.primary,
                    checkedTrackColor = if (isDarkMode) colors.primary else colors.onPrimary,
                    checkedBorderColor = if (isDarkMode) colors.primary else colors.onPrimary,
                    uncheckedThumbColor = palette.content,
                    uncheckedTrackColor = palette.content.copy(alpha = 0.25f),
                    uncheckedBorderColor = palette.content.copy(alpha = 0.6f)
                )
            )
        }
    }
}