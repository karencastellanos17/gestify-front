package com.example.inventario.ui.theme.components


import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Foto del producto; si no hay foto, degradado del tema con la inicial. */
@Composable
fun ProductImage(
    imageUri: String?,
    fallbackLabel: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    val bitmap by produceState<ImageBitmap?>(initialValue = null, imageUri) {
        value = if (imageUri == null) null
        else withContext(Dispatchers.IO) { decodeSampledBitmap(context, Uri.parse(imageUri), 700) }
    }

    Box(
        modifier = modifier.background(Brush.linearGradient(listOf(colors.primary, colors.secondary))),
        contentAlignment = Alignment.Center
    ) {
        val image = bitmap
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = fallbackLabel,
                style = textStyle,
                fontWeight = FontWeight.Bold,
                color = colors.onPrimary
            )
        }
    }
}

// Decodifica reducida para no gastar memoria con fotos grandes
private fun decodeSampledBitmap(context: Context, uri: Uri, maxSide: Int): ImageBitmap? = try {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

    var sample = 1
    while (bounds.outWidth / sample > maxSide || bounds.outHeight / sample > maxSide) sample *= 2

    val options = BitmapFactory.Options().apply { inSampleSize = sample }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }?.asImageBitmap()
} catch (e: Exception) {
    null
}