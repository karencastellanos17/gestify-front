package com.example.inventario.ui.theme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Logo de inventario de Gestify.
 * [onGradient] = true: para ponerlo sobre un fondo de color (banners, panel del login).
 * [onGradient] = false: para fondos claros/oscuros normales.
 */
@Composable
fun InventoryLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 132.dp,
    onGradient: Boolean = true,
    showLabel: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val ink = if (onGradient) colors.onPrimary else colors.primary
    val cardShape = RoundedCornerShape(logoSize * 0.18f)

    val cardModifier = if (onGradient) {
        Modifier
            .background(ink.copy(alpha = 0.20f), cardShape)
            .border(2.dp, ink.copy(alpha = 0.7f), cardShape)
    } else {
        Modifier.background(Brush.linearGradient(listOf(colors.primary, colors.secondary)), cardShape)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(logoSize),
            contentAlignment = Alignment.Center
        ) {
            // Anillos
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = this.size.minDimension / 2f
                drawCircle(color = ink.copy(alpha = 0.10f), radius = radius)
                drawCircle(
                    color = ink.copy(alpha = 0.35f),
                    radius = radius * 0.92f,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f))
                    )
                )
            }

            // Tarjeta central con la caja de inventario
            Box(
                modifier = Modifier
                    .size(logoSize * 0.60f)
                    .then(cardModifier),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = "Inventario",
                    tint = if (onGradient) ink else colors.onPrimary,
                    modifier = Modifier.size(logoSize * 0.36f)
                )
            }

            // Insignia de crecimiento
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = logoSize * 0.06f, bottom = logoSize * 0.06f)
                    .size(logoSize * 0.28f)
                    .background(colors.surface, CircleShape)
                    .padding(logoSize * 0.05f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (showLabel) {
            Surface(
                shape = RoundedCornerShape(50),
                color = ink.copy(alpha = 0.18f)
            ) {
                Text(
                    text = "INVENTARIO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ink,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}