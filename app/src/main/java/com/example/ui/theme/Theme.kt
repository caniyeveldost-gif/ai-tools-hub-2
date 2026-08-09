package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2D1B69),
    onPrimaryContainer = NeonPurpleLight,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF083344),
    onSecondaryContainer = NeonCyanLight,
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF451A03),
    onTertiaryContainer = GoldGlow,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

@Composable
fun AiHubTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
