package com.schoolfinder.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Maroon = Color(0xFF800020)
private val MaroonDark = Color(0xFF5A0017)
private val Amber = Color(0xFFE0A51E)

private val LightColors = lightColorScheme(
    primary = Maroon,
    onPrimary = Color.White,
    secondary = Amber,
    onSecondary = Color.Black,
    background = Color.White,
    surface = Color.White,
    primaryContainer = Color(0xFFF0D6DB),
    onPrimaryContainer = MaroonDark,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD98A9A),
    onPrimary = Color.Black,
    secondary = Amber,
)

@Composable
fun SchoolFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content,
    )
}
