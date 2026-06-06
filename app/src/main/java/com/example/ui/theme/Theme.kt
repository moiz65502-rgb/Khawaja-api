package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LibasMaroon,
    secondary = LibasGold,
    tertiary = LibasRoseGold,
    background = DarkBg,
    surface = DarkSurface,
    onBackground = DarkOnBg,
    onSurface = DarkOnBg,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LibasMaroon,
    secondary = LibasGold,
    tertiary = LibasRoseGold,
    background = LightBg,
    surface = LightSurface,
    onBackground = LightOnBg,
    onSurface = LightOnBg,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LibasMaroon

    /* Other default colors to override
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    */
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
