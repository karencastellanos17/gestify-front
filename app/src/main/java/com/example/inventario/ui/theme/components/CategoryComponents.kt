package com.example.inventario.ui.theme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.inventario.ui.theme.Temas.CategoryPalette

private val DefaultCategories = listOf(
    "Bebidas", "Alimentos", "Ropa", "Papelería", "Hogar", "Tecnología", "Belleza", "Otros"
)

/** Color estable para cada categoría (siempre el mismo para el mismo nombre). */
fun categoryColor(name: String): Color {
    val index = (name.trim().lowercase().hashCode() and 0x7fffffff) % CategoryPalette.size
    return CategoryPalette[index]
}

/** Etiqueta de categoría con punto de color. */
@Composable
fun CategoryTag(name: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val tint = categoryColor(name)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = tint.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(tint, CircleShape)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Campo de categoría + chips de colores para elegir rápido. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryPicker(
    value: String,
    onValueChange: (String) -> Unit,
    existingCategories: List<String>,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val colors = MaterialTheme.colorScheme
    val options = (existingCategories + DefaultCategories)
        .filter { it.isNotBlank() }
        .distinctBy { it.trim().lowercase() }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Categoría *") },
            leadingIcon = if (value.isNotBlank()) {
                {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(categoryColor(value), CircleShape)
                    )
                }
            } else null,
            isError = isError,
            supportingText = if (isError && errorMessage != null) {
                { Text(errorMessage) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = formFieldColors()
        )

        Text(
            text = "ELIGE UNA O ESCRIBE LA TUYA",
            style = MaterialTheme.typography.labelSmall,
            color = colors.onSurfaceVariant
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val tint = categoryColor(option)
                val selected = option.trim().equals(value.trim(), ignoreCase = true)

                Surface(
                    onClick = { onValueChange(option) },
                    shape = RoundedCornerShape(50),
                    color = tint.copy(alpha = if (selected) 0.30f else 0.12f),
                    border = BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = tint.copy(alpha = if (selected) 1f else 0.45f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = colors.onSurface,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(tint, CircleShape)
                            )
                        }
                        Text(
                            text = option,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = colors.onSurface
                        )
                    }
                }
            }
        }
    }
}