package com.example.inventario.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class StatusBadgeStyle {
    SUCCESS,  // Verde pastel
    DANGER,   // Rojo suave
    WARNING,  // Naranja / Amarillo pastel
    INFO,     // Azul / Primario
    NEUTRAL   // Gris sutil
}

@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: StatusBadgeStyle = StatusBadgeStyle.INFO,
    icon: ImageVector? = null,
    showDot: Boolean = true
) {
    val (backgroundColor, contentColor, dotColor) = when (style) {
        StatusBadgeStyle.SUCCESS -> Triple(
            Color(0xFFDCFCE7), // Verde pastel suave
            Color(0xFF15803D), // Verde oscuro
            Color(0xFF22C55E)  // Punto verde brillante
        )
        StatusBadgeStyle.DANGER -> Triple(
            Color(0xFFFEE2E2), // Rojo suave
            Color(0xFFB91C1C), // Rojo oscuro
            Color(0xFFEF4444)  // Punto rojo
        )
        StatusBadgeStyle.WARNING -> Triple(
            Color(0xFFFEF3C7), // Naranja/Amarillo pastel
            Color(0xFFB45309), // Marrón/Naranja oscuro
            Color(0xFFF59E0B)  // Punto naranja
        )
        StatusBadgeStyle.INFO -> Triple(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.onPrimaryContainer,
            MaterialTheme.colorScheme.primary
        )
        StatusBadgeStyle.NEUTRAL -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.outline
        )
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = contentColor
                )
            } else if (showDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }

            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
