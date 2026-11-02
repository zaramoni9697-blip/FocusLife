package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val FocusLifeDarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = Color.White,
    secondary = BlueElectric,
    onSecondary = Color.Black,
    secondaryContainer = SurfaceElevatedDark,
    onSecondaryContainer = BlueElectric,
    tertiary = GreenNeon,
    onTertiary = Color.Black,
    tertiaryContainer = GreenEmerald,
    onTertiaryContainer = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    error = RedPriority,
    onError = Color.White
)

val FocusLifeLightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleLight,
    onPrimaryContainer = Color.Black,
    secondary = BluePrimary,
    onSecondary = Color.White,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = BluePrimary,
    tertiary = GreenEmerald,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = RedPriority,
    onError = Color.White
)

@Composable
fun FocusLifeTheme(
    darkTheme: Boolean = true, // Default to premium dark as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FocusLifeDarkColorScheme else FocusLifeLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
