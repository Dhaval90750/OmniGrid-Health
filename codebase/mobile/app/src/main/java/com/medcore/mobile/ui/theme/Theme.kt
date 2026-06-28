package com.medcore.mobile.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryTeal,
    tertiary = PrimaryBlue,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceDarkVariant,
    onPrimary = SurfaceLight,
    onSecondary = SurfaceLight,
    onTertiary = SurfaceLight,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed,
    onError = SurfaceLight
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    secondary = SecondaryTeal,
    tertiary = PrimaryBlue,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceLightVariant,
    onPrimary = SurfaceLight,
    onSecondary = SurfaceLight,
    onTertiary = SurfaceLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    error = ErrorRed,
    onError = SurfaceLight
)

@Composable
fun MedCoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to enforce our clinical premium theme
    content: @Composable () -> Unit
) {
    // Hardcode to Light Theme (White Only) throughout the app
    val colorScheme = LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
