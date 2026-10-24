package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TelePrimary,
    onPrimary = TelePrimaryForeground,
    primaryContainer = TelePrimarySoft,
    onPrimaryContainer = TelePrimary,
    secondary = TeleSecondary,
    onSecondary = TeleSecondaryForeground,
    secondaryContainer = TeleSecondary,
    tertiary = TeleAccent,
    onTertiary = TeleAccentForeground,
    tertiaryContainer = TeleAccentSoft,
    background = TeleBackground,
    onBackground = TeleCardForeground,
    surface = TeleCard,
    onSurface = TeleCardForeground,
    surfaceVariant = TeleMuted,
    onSurfaceVariant = TeleTextSecondary,
    outline = TeleBorder,
    error = TeleDestructive,
    onError = TeleDestructiveForeground
)

private val DarkColorScheme = darkColorScheme(
    primary = TelePrimaryDark,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF164E63),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = Color(0xFF1E293B),
    onSecondary = Color(0xFFE2E8F0),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    outline = Color(0xFF475569)
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
