package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = WorkGreenPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = WorkGreenContainer,
    onPrimaryContainer = WorkOnGreenContainer,
    secondary = WorkAccentBlue,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = WorkAccentBlueContainer,
    onSecondaryContainer = WorkAccentBlue,
    background = NeutralLightBackground,
    onBackground = TextPrimary,
    surface = NeutralSurface,
    onSurface = TextPrimary,
    surfaceVariant = NeutralSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = RedError,
    errorContainer = RedErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = WorkGreenLight,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = WorkGreenDark,
    onPrimaryContainer = WorkGreenContainer,
    secondary = WorkAccentBlueContainer,
    background = androidx.compose.ui.graphics.Color(0xFF121413),
    surface = androidx.compose.ui.graphics.Color(0xFF1E211F)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For this work photography app, prefer high-clarity light theme or user setting
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
