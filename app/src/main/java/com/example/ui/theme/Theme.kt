package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MahimaDarkColorScheme = darkColorScheme(
    primary = NeonPink,
    secondary = NeonPurple,
    tertiary = SoftPink,
    background = DarkBackground,
    surface = DeepPurple,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextLight,
    onSurface = TextLight,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextMuted
)

private val MahimaLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = NeonPink,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF211322),
    onSurface = Color(0xFF211322),
    surfaceVariant = Color(0xFFFFD1DC),
    onSurfaceVariant = Color(0xFF5A2A46)
)

@Composable
fun MahimaTheme(
    darkTheme: Boolean = true, // Default to futuristic dark mode for romantic ambiance
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MahimaDarkColorScheme else MahimaLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
