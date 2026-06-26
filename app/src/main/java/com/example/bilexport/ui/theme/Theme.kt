package com.example.bilexport.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    primaryContainer = Blue800,
    onPrimaryContainer = Color.White,
    secondary = Blue600,
    onSecondary = Color.White,
    secondaryContainer = Blue700,
    onSecondaryContainer = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = Red500,
    onError = Color.White,
    outline = Divider,
    outlineVariant = Divider
)

@Composable
fun BiliExportTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}