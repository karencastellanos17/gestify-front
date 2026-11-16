package com.example.inventario.ui.theme.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.inventario.ui.theme.components.GestifyMark
import com.example.inventario.ui.theme.components.ModuleBanner
import com.example.inventario.ui.theme.components.WaveFooter

private data class InfoCardData(val icon: ImageVector, val title: String, val description: String)
private data class GuideStep(val title: String, val description: String)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutScreen() {
    val colors = MaterialTheme.colorScheme
    var selectedSection by remember { mutableIntStateOf(0) }
    val isPhone = LocalConfiguration.current.screenWidthDp < 700

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModuleBanner(
            title = "Acerca de Gestify",
            subtitle = "Gestionar mejor, crecer más.",
            icon = Icons.Default.Lightbulb
        )

        val chipColors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.primary.copy(alpha = 0.15f),
            selectedLabelColor = colors.primary,
            selectedLeadingIconColor = colors.primary
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple("Quiénes somos", Icons.Default.Groups, 0),
                Triple("Qué ofrecemos", Icons.Default.Star, 1),
                Triple("Guía de uso", Icons.AutoMirrored.Filled.MenuBook, 2)
            ).forEach { (label, icon, index) ->
                FilterChip(
                    selected = selectedSection == index,
                    onClick = { selectedSection = index },
                    label = { Text(label) },
                    leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    colors = chipColors,
                    shape = RoundedCornerShape(50)
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            Crossfade(targetState = selectedSection, label = "AboutSection") { section ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (section) {
                        0 -> WhoWeAreSection(isPhone = isPhone)
                        1 -> WhatWeOfferSection(isPhone = isPhone)
                        else -> GuideSection()
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
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
                color = colors.primary.copy(alpha = 0.05f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        }
    }
}

@Composable
private fun WhoWeAreSection(isPhone: Boolean) {
    val colors = MaterialTheme.colorScheme

    SectionCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GestifyMark(size = 56.dp)
            Column {
                Text(
                    text = "Gestify",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = colors.primary
                )
                Text(
                    text = "Gestionar mejor, crecer más",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }

        Text(
            text = "Gestify es una aplicación de gestión pensada para emprendedores y dueños de pequeños negocios. " +
                    "Reúne en un solo lugar lo que necesitas para llevar tu negocio al día: tu inventario con fotos, " +
                    "tus ventas y caja, las alertas de stock bajo y el directorio de tus proveedores.",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurface
        )
        Text(
            text = "Creemos que llevar las cuentas no debería ser complicado ni consumir tu tiempo. " +
                    "Por eso Gestify te muestra lo importante de un vistazo y te avisa cuando algo necesita tu atención.",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant
        )
    }

    val cards = listOf(
        InfoCardData(
            Icons.Default.Flag,
            "Nuestra misión",
            "Ayudar a los emprendedores a gestionar su negocio de forma simple, ordenada y sin complicaciones."
        ),
        InfoCardData(
            Icons.Default.Visibility,
            "Nuestra visión",
            "Ser la herramienta de confianza de los pequeños negocios para tomar mejores decisiones y crecer."
        ),
        InfoCardData(
            Icons.Default.Star,
            "Nuestros valores",
            "Sencillez, claridad en la información, cuidado de tus datos y mejora continua."
        )
    )

    if (isPhone) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            cards.forEach { item ->
                InfoCard(item, Modifier.fillMaxWidth())
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            cards.forEach { item ->
                InfoCard(item, Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun WhatWeOfferSection(isPhone: Boolean) {
    val features = listOf(
        InfoCardData(
            Icons.Default.Inventory2,
            "Inventario con fotos",
            "Registra tus productos con foto, categoría, precio y stock. Edítalos cuando quieras y desactívalos sin perder su historial."
        ),
        InfoCardData(
            Icons.Default.PointOfSale,
            "Ventas y caja",
            "Registra cada venta con fecha y hora, y el stock se descuenta automáticamente. Mira tu total del día y tu ticket promedio."
        ),
        InfoCardData(
            Icons.Default.Notifications,
            "Alertas inteligentes",
            "Te avisamos cuando un producto tiene poco stock y te sugerimos cuántas unidades pedir."
        ),
        InfoCardData(
            Icons.Default.LocalShipping,
            "Directorio de proveedores",
            "Guarda a quién le compras y qué te distribuye. Cuando el stock baje, verás a quién pedirle con sus datos de contacto."
        ),
        InfoCardData(
            Icons.Default.Dashboard,
            "Panel con gráficos",
            "Un resumen de tus ventas y de tu stock por categoría, con tarjetas que te llevan directo a cada sección."
        ),
        InfoCardData(
            Icons.Default.Palette,
            "Hecha a tu estilo",
            "Elige entre seis paletas de color y cambia entre modo claro y oscuro para trabajar cómodo a cualquier hora."
        )
    )

    SectionCard {
        Text(
            text = "Todo lo que necesitas para tu negocio",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Estas son las herramientas que Gestify pone a tu disposición:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (isPhone) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            features.forEach { item ->
                InfoCard(item, Modifier.fillMaxWidth())
            }
        }
    } else {
        features.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    InfoCard(item, Modifier.weight(1f).fillMaxHeight())
                }
            }
        }
    }
}

@Composable
private fun GuideSection() {
    val colors = MaterialTheme.colorScheme

    val steps = listOf(
        GuideStep(
            "Registra tus productos",
            "Ve a Stock y pulsa «Nuevo Producto». Completa nombre, categoría, precio y stock, y si quieres sube una foto. Puedes cambiar la vista entre Lista y Galería."
        ),
        GuideStep(
            "Agrega tus proveedores",
            "En Proveedores pulsa «Nuevo Proveedor». Escribe sus datos y los productos que distribuye, o elígelos de las sugerencias de tu inventario."
        ),
        GuideStep(
            "Registra tus ventas",
            "En Ventas pulsa «Registrar Venta», elige el producto y la cantidad. Se guarda con fecha y hora, y el stock se descuenta solo."
        ),
        GuideStep(
            "Revisa las alertas",
            "Cuando un producto baja de $LOW_STOCK_LIMIT unidades aparece una alerta, y es crítica con $CRITICAL_STOCK_LIMIT o menos. Allí verás el proveedor recomendado y cuántas unidades pedir."
        ),
        GuideStep(
            "Mira tu panel de inicio",
            "En Inicio verás tus ventas del día, tu stock y tus alertas. Toca cada tarjeta para ir directo a esa sección."
        ),
        GuideStep(
            "Personaliza tu app",
            "En Ajustes elige la paleta de color que más te guste. El modo claro u oscuro se cambia desde la barra superior."
        )
    )

    SectionCard {
        Text(
            text = "Cómo usar Gestify en 6 pasos",
            style = MaterialTheme.typography.titleLarge,
            color = colors.primary
        )

        steps.forEachIndexed { index, step ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Brush.linearGradient(listOf(colors.primary, colors.secondary)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface
                    )
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }
    }

    SectionCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = colors.primary
            )
            Text(
                text = "Recomendaciones",
                style = MaterialTheme.typography.titleLarge,
                color = colors.primary
            )
        }

        listOf(
            "Desactiva en lugar de borrar: así conservas el historial de tus productos y proveedores y puedes restaurarlos.",
            "Asocia cada producto a un proveedor: así, cuando el stock baje, sabrás a quién pedirle sin buscar.",
            "Agrega fotos a tus productos: la vista de Galería se ve mucho más clara y atractiva.",
            "Revisa las alertas al empezar el día para pedir mercancía a tiempo."
        ).forEach { tip ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(8.dp)
                        .background(colors.primary, CircleShape)
                )
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun InfoCard(data: InfoCardData, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Brush.linearGradient(listOf(colors.primary, colors.secondary)), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
            Text(
                text = data.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }
    }
}