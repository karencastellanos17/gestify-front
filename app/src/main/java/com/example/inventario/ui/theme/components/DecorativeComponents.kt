package com.example.inventario.ui.theme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Fondo decorativo: anillos, cuadrícula de puntos, siluetas de barras y olas.
 * [tint] debe venir del tema (por ejemplo onPrimary sobre degradado, o primary sobre fondo claro).
 */
@Composable
fun BrandBackdrop(
    modifier: Modifier = Modifier,
    tint: Color,
    scale: Float = 1f,
    alphaScale: Float = 1f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Anillos concéntricos (arriba-izquierda)
        val ringStroke = Stroke(width = 2.dp.toPx())
        listOf(110.dp, 190.dp, 270.dp).forEachIndexed { i, r ->
            drawCircle(
                color = tint.copy(alpha = (0.16f - i * 0.04f) * alphaScale),
                radius = r.toPx() * scale,
                center = Offset(w * 0.10f, h * 0.12f),
                style = ringStroke
            )
        }

        // Cuadrícula de puntos (arriba-derecha)
        val step = 18.dp.toPx() * scale
        val dotRadius = 2.dp.toPx() * scale
        val margin = 36.dp.toPx() * scale
        for (row in 0 until 6) {
            for (col in 0 until 8) {
                drawCircle(
                    color = tint.copy(alpha = 0.28f * alphaScale),
                    radius = dotRadius,
                    center = Offset(w - margin - col * step, margin + row * step)
                )
            }
        }

        // Siluetas de barras ascendentes (crecimiento)
        val barWidth = 30.dp.toPx() * scale
        val gap = 14.dp.toPx() * scale
        val heights = listOf(0.14f, 0.20f, 0.17f, 0.28f, 0.24f, 0.36f)
        val startX = w - margin - (barWidth + gap) * heights.size
        heights.forEachIndexed { i, fraction ->
            val barHeight = h * fraction
            drawRoundRect(
                color = tint.copy(alpha = 0.10f * alphaScale),
                topLeft = Offset(startX + i * (barWidth + gap), h - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f)
            )
        }

        // Olas inferiores
        drawWave(tint.copy(alpha = 0.10f * alphaScale), baseY = h * 0.80f, amp = h * 0.10f)
        drawWave(tint.copy(alpha = 0.14f * alphaScale), baseY = h * 0.90f, amp = -h * 0.07f)
    }
}

/** Ola suave para el pie de una tarjeta. */
@Composable
fun WaveFooter(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        drawWave(color, baseY = size.height * 0.45f, amp = size.height * 0.30f)
    }
}

/** Logo de Gestify: cuadro redondeado con flecha de crecimiento. */
@Composable
fun GestifyMark(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    onGradient: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(size * 0.28f)
    val backgroundModifier = if (onGradient) {
        Modifier.background(colors.onPrimary.copy(alpha = 0.20f), shape)
    } else {
        Modifier.background(Brush.linearGradient(listOf(colors.primary, colors.secondary)), shape)
    }

    Box(
        modifier = modifier
            .size(size)
            .then(backgroundModifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = colors.onPrimary,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/**
 * Banner de cabecera de módulo: degradado del tema, figuras decorativas,
 * ícono, título, subtítulo y (opcional) un botón de acción a la derecha.
 */
@Composable
fun ModuleBanner(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(colors.primary, colors.secondary)))
    ) {
        BrandBackdrop(
            modifier = Modifier.matchParentSize(),
            tint = colors.onPrimary,
            scale = 0.5f
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(colors.onPrimary.copy(alpha = 0.20f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.onPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onPrimary.copy(alpha = 0.9f)
                )
            }

            if (action != null) action()
        }
    }
}

/** Tarjeta de métrica compacta con ícono en círculo y ola decorativa. */
@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box {
            WaveFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .align(Alignment.BottomCenter),
                color = accent.copy(alpha = 0.07f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(accent.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawWave(color: Color, baseY: Float, amp: Float) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(0f, baseY)
        cubicTo(w * 0.25f, baseY - amp, w * 0.45f, baseY + amp, w * 0.70f, baseY + amp * 0.1f)
        cubicTo(w * 0.85f, baseY - amp * 0.5f, w * 0.95f, baseY - amp * 0.8f, w, baseY - amp * 0.4f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(path = path, color = color)
}