package com.example.inventario.ui.theme.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale

/** Gráfico de barras animado: lista de (etiqueta, valor). */
@Composable
fun SalesBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 190.dp
) {
    val colors = MaterialTheme.colorScheme

    if (data.isEmpty()) {
        Box(modifier = modifier.height(chartHeight), contentAlignment = Alignment.Center) {
            Text(
                text = "Aún no hay ventas registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }
        return
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 900))
    }

    val maxValue = (data.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)
    val barArea = chartHeight - 52.dp

    Box(modifier = modifier.height(chartHeight)) {
        // Líneas guía punteadas
        Canvas(modifier = Modifier.fillMaxSize().padding(bottom = 24.dp)) {
            val lines = 4
            for (i in 0..lines) {
                val y = size.height * i / lines
                drawLine(
                    color = colors.outlineVariant.copy(alpha = 0.5f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (label, value) ->
                val fraction = (value / maxValue).toFloat()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "$" + compactNumber(value),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(34.dp)
                            .height(barArea * (fraction * progress.value))
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(Brush.verticalGradient(listOf(colors.primary, colors.secondary)))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.height(18.dp)
                    )
                }
            }
        }
    }
}

/** Gráfico de dona animado responsivo: coloca la leyenda abajo si la tarjeta es angosta. */
@Composable
fun StockDonutChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    if (data.isEmpty()) {
        Box(modifier = modifier.height(190.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "Sin stock disponible",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }
        return
    }

    val sliceColors = listOf(
        colors.primary,
        colors.secondary,
        colors.tertiary,
        colors.primary.copy(alpha = 0.55f),
        colors.secondary.copy(alpha = 0.55f)
    )

    val progress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 900))
    }

    val total = data.sumOf { it.second }.coerceAtLeast(1)

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val stackVertically = maxWidth < 330.dp
        val donutSize = if (stackVertically) 136.dp else 160.dp

        if (stackVertically) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DonutRing(
                    data = data,
                    total = total,
                    progress = progress.value,
                    sliceColors = sliceColors,
                    donutSize = donutSize
                )
                DonutLegend(
                    data = data,
                    sliceColors = sliceColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                DonutRing(
                    data = data,
                    total = total,
                    progress = progress.value,
                    sliceColors = sliceColors,
                    donutSize = donutSize
                )
                DonutLegend(
                    data = data,
                    sliceColors = sliceColors,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DonutRing(
    data: List<Pair<String, Int>>,
    total: Int,
    progress: Float,
    sliceColors: List<Color>,
    donutSize: Dp
) {
    val colors = MaterialTheme.colorScheme
    Box(modifier = Modifier.size(donutSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 24.dp.toPx()
            val inset = strokeWidth / 2f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val gap = if (data.size > 1) 3f else 0f
            var start = -90f

            data.forEachIndexed { index, item ->
                val sweep = 360f * item.second / total * progress
                drawArc(
                    color = sliceColors[index % sliceColors.size],
                    startAngle = start,
                    sweepAngle = (sweep - gap).coerceAtLeast(0.1f),
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
                start += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
            Text(
                text = "UNIDADES",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DonutLegend(
    data: List<Pair<String, Int>>,
    sliceColors: List<Color>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(sliceColors[index % sliceColors.size], CircleShape)
                )
                Text(
                    text = item.first,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${item.second} un.",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

private fun compactNumber(value: Double): String = when {
    value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000)
    value >= 1_000 -> "${(value / 1_000).toInt()}k"
    else -> value.toInt().toString()
}