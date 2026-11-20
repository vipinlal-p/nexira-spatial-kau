package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KAUlightColorScheme = lightColorScheme(
    primary = Color(0xFF1B5E20),
    onPrimary = Color.White,
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    tertiary = Color(0xFFFFB300),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Force Light Mode Only - ignore system dark theme
    val colorScheme = KAUlightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
