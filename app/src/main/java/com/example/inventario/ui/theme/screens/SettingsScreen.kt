package com.example.inventario.ui.theme.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.ui.theme.Temas.AmberPrimary
import com.example.inventario.ui.theme.Temas.AmberSecondary
import com.example.inventario.ui.theme.Temas.BlackPrimary
import com.example.inventario.ui.theme.Temas.BlackSecondary
import com.example.inventario.ui.theme.Temas.BluePrimary
import com.example.inventario.ui.theme.Temas.BlueSecondary
import com.example.inventario.ui.theme.Temas.OrangePrimary
import com.example.inventario.ui.theme.Temas.OrangeSecondary
import com.example.inventario.ui.theme.Temas.TealPrimary
import com.example.inventario.ui.theme.Temas.TealSecondary
import com.example.inventario.ui.theme.Temas.WinePrimary
import com.example.inventario.ui.theme.Temas.WineSecondary
import com.example.inventario.ui.theme.components.GestifyMark
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.StatusBadge
import com.example.inventario.ui.theme.components.StatusBadgeStyle
import com.example.inventario.ui.theme.components.WaveFooter
import com.example.inventario.utils.AppThemeColor
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel

@Composable
fun SettingsScreen(
    selectedThemeColor: AppThemeColor,
    onThemeSelected: (AppThemeColor) -> Unit,
    isDarkMode: Boolean = false,
    viewModel: GestifyViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme

    // Helper para aplicar localmente y persistir en el backend
    fun handleColorSelection(color: AppThemeColor) {
        onThemeSelected(color)
        viewModel.saveThemePreferences(color.name, isDarkMode)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ───────── Banner ─────────
        ModuleBanner(
            title = "Configuración del Emprendimiento",
            subtitle = "Personaliza la identidad visual de tu negocio. El color seleccionado se guarda en la nube.",
            icon = Icons.Default.Palette
        )

        // ───────── Selección de paleta ─────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box {
                WaveFooter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter),
                    color = colors.primary.copy(alpha = 0.06f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Paleta de Colores del Tema",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.primary
                        )

                        StatusBadge(
                            text = "Sincronizado",
                            style = StatusBadgeStyle.INFO,
                            icon = Icons.Default.Palette
                        )
                    }

                    HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ColorOptionCard(
                            title = "Azul",
                            color = BluePrimary,
                            secondaryColor = BlueSecondary,
                            isSelected = selectedThemeColor == AppThemeColor.BLUE,
                            onClick = { handleColorSelection(AppThemeColor.BLUE) },
                            modifier = Modifier.weight(1f)
                        )
                        ColorOptionCard(
                            title = "Naranja",
                            color = OrangePrimary,
                            secondaryColor = OrangeSecondary,
                            isSelected = selectedThemeColor == AppThemeColor.ORANGE,
                            onClick = { handleColorSelection(AppThemeColor.ORANGE) },
                            modifier = Modifier.weight(1f)
                        )
                        ColorOptionCard(
                            title = "Teal",
                            color = TealPrimary,
                            secondaryColor = TealSecondary,
                            isSelected = selectedThemeColor == AppThemeColor.TEAL,
                            onClick = { handleColorSelection(AppThemeColor.TEAL) },
                            modifier = Modifier.weight(1f)
                        )
                        ColorOptionCard(
                            title = "Vinotinto",
                            color = WinePrimary,
                            secondaryColor = WineSecondary,
                            isSelected = selectedThemeColor == AppThemeColor.WINE,
                            onClick = { handleColorSelection(AppThemeColor.WINE) },
                            modifier = Modifier.weight(1f)
                        )
                        ColorOptionCard(
                            title = "Amarillo",
                            color = AmberPrimary,
                            secondaryColor = AmberSecondary,
                            isSelected = selectedThemeColor == AppThemeColor.AMBER,
                            onClick = { handleColorSelection(AppThemeColor.AMBER) },
                            modifier = Modifier.weight(1f)
                        )
                        ColorOptionCard(
                            title = "Negro",
                            color = BlackPrimary,
                            secondaryColor = BlackSecondary,
                            isSelected = selectedThemeColor == AppThemeColor.BLACK,
                            onClick = { handleColorSelection(AppThemeColor.BLACK) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // ───────── Vista previa del tema ─────────
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
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Vista previa",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.primary
                    )
                    Text(
                        text = "Así se ve Gestify con la paleta que elegiste.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1.6f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(16.dp),
                        color = colors.surface,
                        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.5f)),
                        shadowElevation = 2.dp
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Brush.horizontalGradient(listOf(colors.primary, colors.secondary)))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GestifyMark(size = 32.dp, onGradient = true)
                                Text(
                                    text = "Gestify",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = colors.onPrimary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Gestionar mejor, crecer más",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.onPrimary.copy(alpha = 0.9f)
                                )
                            }

                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {},
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("Botón principal") }
                                OutlinedButton(
                                    onClick = {},
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("Secundario") }
                                StatusBadge(text = "Activo", style = StatusBadgeStyle.SUCCESS)
                                StatusBadge(text = "Info", style = StatusBadgeStyle.INFO)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ColorSwatch(
                            label = "PRIMARIO",
                            background = colors.primary,
                            content = colors.onPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        ColorSwatch(
                            label = "SECUNDARIO",
                            background = colors.secondary,
                            content = colors.onSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    label: String,
    background: Color,
    content: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = content
        )
    }
}

@Composable
fun ColorOptionCard(
    title: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryColor: Color = color
) {
    val colors = MaterialTheme.colorScheme

    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 1.dp,
        animationSpec = tween(durationMillis = 200),
        label = "CardElevation"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) color else colors.outlineVariant.copy(alpha = 0.4f),
        animationSpec = tween(durationMillis = 200),
        label = "BorderColor"
    )

    val animatedTint by animateColorAsState(
        targetValue = color.copy(alpha = if (isSelected) 0.14f else 0.05f),
        animationSpec = tween(durationMillis = 200),
        label = "TintColor"
    )

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, animatedBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(animatedTint)
                .padding(vertical = 20.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Brush.linearGradient(listOf(color, secondaryColor)), CircleShape)
                    .then(
                        if (isSelected) Modifier.border(3.dp, colors.surface, CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seleccionado",
                        tint = colors.surface,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) color else colors.onSurfaceVariant
            )
        }
    }
}