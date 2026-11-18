package com.example.inventario.ui.theme.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.LruCache
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
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

private object ImageMemoryCache {
    private val cache = object : LruCache<String, ImageBitmap>(30) {}
    fun get(key: String): ImageBitmap? = cache.get(key)
    fun put(key: String, bitmap: ImageBitmap) {
        cache.put(key, bitmap)
    }
}

@Composable
fun ProductImage(
    imageUri: String?,
    fallbackLabel: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    val bitmap by produceState<ImageBitmap?>(
        initialValue = imageUri?.let { ImageMemoryCache.get(it) },
        imageUri
    ) {
        if (imageUri.isNullOrBlank()) {
            value = null
            return@produceState
        }
        ImageMemoryCache.get(imageUri)?.let {
            value = it
            return@produceState
        }
        val loaded = withContext(Dispatchers.IO) {
            if (imageUri.startsWith("http://") || imageUri.startsWith("https://")) {
                decodeNetworkBitmap(imageUri)
            } else {
                decodeSampledBitmap(context, Uri.parse(imageUri), 800)
            }
        }
        if (loaded != null) {
            ImageMemoryCache.put(imageUri, loaded)
        }
        value = loaded
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
                alignment = Alignment.Center,
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

private fun decodeNetworkBitmap(urlString: String): ImageBitmap? = try {
    // Si es Cloudinary, pedir transformación cuadrada centrada 600x600 para carga rápida y encuadre uniforme
    val optimizedUrl = if (urlString.contains("res.cloudinary.com") && urlString.contains("/upload/")) {
        urlString.replace("/upload/", "/upload/c_fill,g_auto,w_600,h_600,q_auto/")
    } else {
        urlString
    }
    URL(optimizedUrl).openStream().use { stream ->
        BitmapFactory.decodeStream(stream)?.asImageBitmap()
    }
} catch (_: Exception) {
    null
}

private fun decodeSampledBitmap(context: Context, uri: Uri, maxSide: Int): ImageBitmap? = try {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

    var sample = 1
    while (bounds.outWidth / sample > maxSide || bounds.outHeight / sample > maxSide) sample *= 2

    val options = BitmapFactory.Options().apply { inSampleSize = sample }
    val raw = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    } ?: return null

    val rotation = context.contentResolver.openInputStream(uri)?.use { input ->
        when (ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    } ?: 0f

    val rotated = if (rotation != 0f) {
        val matrix = Matrix().apply { postRotate(rotation) }
        Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, true)
    } else raw

    rotated.asImageBitmap()
} catch (_: Exception) {
    null
}