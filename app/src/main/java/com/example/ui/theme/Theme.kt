package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LavenderAccent,
    secondary = CuratedTeal,
    tertiary = SoftPurple,
    background = PremiumBgDark,
    surface = PremiumSurfaceDark,
    outline = PremiumBorderDark,
    onPrimary = DeepPurple,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = SlateLight,
    onSurface = SlateLight
)

private val LightColorScheme = lightColorScheme(
    primary = PremiumPrimaryLight,
    secondary = CuratedTeal,
    tertiary = DeepPurple,
    background = PremiumBgLight,
    surface = PremiumSurfaceLight,
    outline = PremiumBorderLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = PremiumBgDark,
    onSurface = PremiumBgDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
