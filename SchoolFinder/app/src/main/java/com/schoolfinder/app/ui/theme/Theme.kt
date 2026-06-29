package com.schoolfinder.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MaroonPrimary = Color(0xFF800000)
private val MaroonDark = Color(0xFF5C0000)
private val MaroonLight = Color(0xFFB23A48)
private val Amber = Color(0xFFFFB300)

private val LightColors = lightColorScheme(
    primary = MaroonPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = MaroonLight,
    onSecondary = Color.White,
    tertiary = Amber,
    background = Color.White,
    onBackground = Color(0xFF201A1A),
    surface = Color.White,
    onSurface = Color(0xFF201A1A),
    surfaceVariant = Color(0xFFF4E7E7),
    onSurfaceVariant = Color(0xFF6B5656)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB3AB),
    onPrimary = Color(0xFF5F1212),
    primaryContainer = MaroonDark,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFFE8A0A0),
    onSecondary = Color(0xFF44292A),
    tertiary = Amber,
    background = Color(0xFF1A1110),
    onBackground = Color(0xFFF1DEDE),
    surface = Color(0xFF221817),
    onSurface = Color(0xFFF1DEDE),
    surfaceVariant = Color(0xFF534343),
    onSurfaceVariant = Color(0xFFD8C2C2)
)

private val AppTypography = Typography()

@Composable
fun SchoolFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
