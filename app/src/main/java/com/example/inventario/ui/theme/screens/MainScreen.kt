package com.example.inventario.ui.theme.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.ui.theme.Temas.InventarioTheme
import com.example.inventario.ui.theme.components.BrandBackdrop
import com.example.inventario.ui.theme.components.GestifyMark
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import com.example.inventario.utils.AppThemeColor

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

private fun parseThemeColor(name: String?): AppThemeColor =
    AppThemeColor.entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: AppThemeColor.BLUE

@Composable
fun MainScreen(
    onLogout: () -> Unit = {},
    viewModel: GestifyViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var currentThemeColor by remember { mutableStateOf(parseThemeColor(viewModel.savedThemeColor())) }
    var isDarkMode by remember { mutableStateOf(viewModel.savedDarkMode()) }

    fun changeDarkMode(value: Boolean) {
        isDarkMode = value
        viewModel.saveThemePreferences(currentThemeColor.name, value)
    }

    val products by viewModel.products.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()

    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val screenHeight = config.screenHeightDp

    // Celular vertical (< 600dp de ancho) -> Barra superior + Barra inferior
    // Celular horizontal (ancho >= 600dp y alto < 500dp) o Tablet -> Barra lateral con scroll
    val isPortraitPhone = screenWidth < 600
    val isLandscapePhone = !isPortraitPhone && screenHeight < 500
    val compactMenu = screenWidth < 1100 || isLandscapePhone

    InventarioTheme(selectedColor = currentThemeColor, darkTheme = isDarkMode) {
        val colors = MaterialTheme.colorScheme

        if (isPortraitPhone) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    Surface(
                        color = colors.primary,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                GestifyMark(size = 32.dp, onGradient = true)
                                Text(
                                    text = "Gestify",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = colors.onPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { selectedTab = 6 }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = "Guía",
                                        tint = if (selectedTab == 6) colors.onPrimary else colors.onPrimary.copy(alpha = 0.75f)
                                    )
                                }
                                IconButton(onClick = { selectedTab = 4 }) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Ajustes",
                                        tint = if (selectedTab == 4) colors.onPrimary else colors.onPrimary.copy(alpha = 0.75f)
                                    )
                                }
                                IconButton(onClick = { changeDarkMode(!isDarkMode) }) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = "Tema",
                                        tint = colors.onPrimary
                                    )
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = colors.surface,
                        tonalElevation = 8.dp
                    ) {
                        topNavItems.forEach { entry ->
                            NavigationBarItem(
                                selected = selectedTab == entry.tab,
                                onClick = { selectedTab = entry.tab },
                                icon = {
                                    Icon(
                                        imageVector = entry.icon,
                                        contentDescription = entry.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = entry.label,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                alwaysShowLabel = true
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
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

                    ScreenContent(
                        selectedTab = selectedTab,
                        onSelectTab = { selectedTab = it },
                        onLogout = onLogout,
                        products = products,
                        suppliers = suppliers,
                        currentThemeColor = currentThemeColor,
                        onThemeSelected = { currentThemeColor = it },
                        isDarkMode = isDarkMode,
                        syncError = viewModel.syncError,
                        onRetry = {
                            viewModel.clearSyncError()
                            viewModel.refreshAllData()
                        }
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                GestifySidebar(
                    selectedTab = selectedTab,
                    onSelect = { selectedTab = it },
                    isDarkMode = isDarkMode,
                    onDarkModeChange = { changeDarkMode(it) },
                    compact = compactMenu,
                    shortScreen = isLandscapePhone
                )

                if (isDarkMode) {
                    VerticalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
                }

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
                            .padding(if (isLandscapePhone) 8.dp else 24.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        ScreenContent(
                            selectedTab = selectedTab,
                            onSelectTab = { selectedTab = it },
                            onLogout = onLogout,
                            products = products,
                            suppliers = suppliers,
                            currentThemeColor = currentThemeColor,
                            onThemeSelected = { currentThemeColor = it },
                            isDarkMode = isDarkMode,
                            syncError = viewModel.syncError,
                            onRetry = {
                                viewModel.clearSyncError()
                                viewModel.refreshAllData()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onLogout: () -> Unit,
    products: List<com.example.inventario.data.model.Product>,
    suppliers: List<com.example.inventario.data.model.Supplier>,
    currentThemeColor: AppThemeColor,
    onThemeSelected: (AppThemeColor) -> Unit,
    isDarkMode: Boolean,
    syncError: String?,
    onRetry: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxSize()) {
        if (syncError != null) {
            Surface(
                color = colors.errorContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = syncError,
                        color = colors.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onRetry) {
                        Text(
                            text = "Reintentar",
                            color = colors.onErrorContainer,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(durationMillis = 250),
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    0 -> DashboardScreen(
                        onLogout = onLogout,
                        onGoToSales = { onSelectTab(2) },
                        onGoToStock = { onSelectTab(1) },
                        onGoToAlerts = { onSelectTab(3) }
                    )
                    1 -> InventoryScreen()
                    2 -> SalesScreen()
                    3 -> AlertsScreen(
                        products = products,
                        suppliers = suppliers,
                        onGoToSuppliers = { onSelectTab(5) }
                    )
                    4 -> SettingsScreen(
                        selectedThemeColor = currentThemeColor,
                        onThemeSelected = onThemeSelected,
                        isDarkMode = isDarkMode
                    )
                    5 -> SuppliersScreen()
                    6 -> AboutScreen()
                }
            }
        }
    }
}

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
    compact: Boolean,
    shortScreen: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val palette = sidebarPalette(isDarkMode)

    val allItems = topNavItems + bottomNavItems
    val position = allItems.indexOfFirst { it.tab == selectedTab }.coerceAtLeast(0)
    val current = allItems[position]

    val width by animateDpAsState(
        targetValue = when {
            shortScreen -> 72.dp
            compact -> 84.dp
            else -> 232.dp
        },
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

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    horizontal = if (shortScreen) 12.dp else if (compact) 18.dp else 14.dp,
                    vertical = if (shortScreen) 10.dp else 20.dp
                ),
            horizontalAlignment = if (compact) Alignment.CenterHorizontally else Alignment.Start
        ) {
            if (compact) {
                GestifyMark(size = if (shortScreen) 40.dp else 48.dp, onGradient = !isDarkMode)
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
                            color = palette.content,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Gestionar mejor, crecer más",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.contentMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (shortScreen) 10.dp else 20.dp))

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
                                    color = palette.content,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = entry.description,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = palette.contentMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(if (shortScreen) 4.dp else 6.dp)) {
                topNavItems.forEach { entry ->
                    SidebarItem(
                        entry = entry,
                        isSelected = selectedTab == entry.tab,
                        compact = compact,
                        shortScreen = shortScreen,
                        palette = palette,
                        onClick = { onSelect(entry.tab) }
                    )
                }
            }

            if (shortScreen) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    color = palette.content.copy(alpha = 0.25f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(if (shortScreen) 4.dp else 6.dp)) {
                bottomNavItems.forEach { entry ->
                    SidebarItem(
                        entry = entry,
                        isSelected = selectedTab == entry.tab,
                        compact = compact,
                        shortScreen = shortScreen,
                        palette = palette,
                        onClick = { onSelect(entry.tab) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (shortScreen) 6.dp else 10.dp))

            DarkModeToggle(
                isDarkMode = isDarkMode,
                onChange = onDarkModeChange,
                compact = compact,
                shortScreen = shortScreen,
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
    shortScreen: Boolean = false,
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

    val boxSize = if (shortScreen) 30.dp else 34.dp
    val iconSize = if (shortScreen) 18.dp else 20.dp

    val iconBox: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(boxSize)
                .background(iconBackground, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = entry.icon,
                contentDescription = entry.label,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
    }

    if (compact) {
        Box(
            modifier = Modifier
                .size(if (shortScreen) 42.dp else 48.dp)
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
    shortScreen: Boolean = false,
    palette: SidebarPalette
) {
    val colors = MaterialTheme.colorScheme

    if (compact) {
        Box(
            modifier = Modifier
                .size(if (shortScreen) 42.dp else 48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(palette.chipBackground)
                .clickable { onChange(!isDarkMode) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = "Cambiar entre modo claro y oscuro",
                tint = palette.content,
                modifier = Modifier.size(if (shortScreen) 20.dp else 22.dp)
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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