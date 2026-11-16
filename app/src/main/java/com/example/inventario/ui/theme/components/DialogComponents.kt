package com.example.inventario.ui.theme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ModernDialog(
    title: String,
    subtitle: String,
    icon: ImageVector,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    danger: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val headerBrush = if (danger)
        Brush.linearGradient(listOf(colors.error, colors.error.copy(alpha = 0.75f)))
    else
        Brush.linearGradient(listOf(colors.primary, colors.secondary))
    val onHeader = if (danger) colors.onError else colors.onPrimary
    val bodyTint = if (danger) colors.error else colors.primary

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .width(520.dp)
                .heightIn(max = 660.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column {
                // Encabezado con degradado y figuras
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerBrush)
                ) {
                    BrandBackdrop(
                        modifier = Modifier.matchParentSize(),
                        tint = onHeader,
                        scale = 0.4f
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(onHeader.copy(alpha = 0.20f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = onHeader,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = onHeader
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = onHeader.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // Cuerpo con tinte de color y ola decorativa
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .background(bodyTint.copy(alpha = 0.05f))
                ) {
                    WaveFooter(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .align(Alignment.BottomCenter),
                        color = bodyTint.copy(alpha = 0.07f)
                    )
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        content = content
                    )
                }

                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.5f))

                // Botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = colors.onSurfaceVariant)
                    }
                    Button(
                        onClick = onConfirm,
                        enabled = confirmEnabled,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (danger) colors.error else colors.primary,
                            contentColor = if (danger) colors.onError else colors.onPrimary
                        )
                    ) {
                        Text(confirmText, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }
    }
}

/** Tarjeta que agrupa campos de un formulario, con título e ícono. */
@Composable
fun DialogSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.18f)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(colors.primary.copy(alpha = 0.14f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
            content()
        }
    }
}

/** Colores de los campos de texto de los formularios, con la paleta de la app. */
@Composable
fun formFieldColors(): TextFieldColors {
    val colors = MaterialTheme.colorScheme
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = colors.primary.copy(alpha = 0.35f),
        focusedLabelColor = colors.primary,
        cursorColor = colors.primary,
        focusedContainerColor = colors.primary.copy(alpha = 0.04f),
        unfocusedContainerColor = colors.primary.copy(alpha = 0.02f)
    )
}