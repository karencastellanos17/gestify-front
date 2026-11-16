package com.example.inventario.ui.theme.Temas

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.inventario.utils.AppThemeColor

private fun lightScheme(primary: Color, secondary: Color): ColorScheme = lightColorScheme(
    primary = primary,
    secondary = secondary,
    tertiary = TertiaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

private fun darkScheme(primary: Color, secondary: Color): ColorScheme = darkColorScheme(
    primary = primary,
    onPrimary = OnPrimaryDark,
    secondary = secondary,
    onSecondary = OnPrimaryDark,
    tertiary = TertiaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark
)

@Composable
fun InventarioTheme(
    selectedColor: AppThemeColor = AppThemeColor.BLUE, // Azul por defecto
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        when (selectedColor) {
            AppThemeColor.BLUE -> darkScheme(BlueDarkPrimary, BlueDarkSecondary)
            AppThemeColor.ORANGE -> darkScheme(OrangeDarkPrimary, OrangeDarkSecondary)
            AppThemeColor.TEAL -> darkScheme(TealDarkPrimary, TealDarkSecondary)
            AppThemeColor.WINE -> darkScheme(WineDarkPrimary, WineDarkSecondary)
            AppThemeColor.AMBER -> darkScheme(AmberDarkPrimary, AmberDarkSecondary)
            AppThemeColor.BLACK -> darkScheme(BlackDarkPrimary, BlackDarkSecondary)
        }
    } else {
        when (selectedColor) {
            AppThemeColor.BLUE -> lightScheme(BluePrimary, BlueSecondary)
            AppThemeColor.ORANGE -> lightScheme(OrangePrimary, OrangeSecondary)
            AppThemeColor.TEAL -> lightScheme(TealPrimary, TealSecondary)
            AppThemeColor.WINE -> lightScheme(WinePrimary, WineSecondary)
            AppThemeColor.AMBER -> lightScheme(AmberPrimary, AmberSecondary)
            AppThemeColor.BLACK -> lightScheme(BlackPrimary, BlackSecondary)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}