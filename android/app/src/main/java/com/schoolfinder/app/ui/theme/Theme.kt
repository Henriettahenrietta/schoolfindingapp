package com.schoolfinder.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Teal = Color(0xFF0D6E6E)
private val TealDark = Color(0xFF09504F)
private val Amber = Color(0xFFF2A900)

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    secondary = Amber,
    onSecondary = Color.Black,
    primaryContainer = Color(0xFFB8E6E4),
    onPrimaryContainer = TealDark,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF5FC9C7),
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
