package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val RedWhiteColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = RedOnPrimary,
    primaryContainer = RedPrimaryContainer,
    onPrimaryContainer = RedOnPrimaryContainer,
    secondary = RedPrimaryDark,
    onSecondary = PureWhite,
    secondaryContainer = RedPrimaryContainer,
    onSecondaryContainer = RedOnPrimaryContainer,
    tertiary = RedPrimary,
    onTertiary = PureWhite,
    background = PureWhite,
    onBackground = DarkText,
    surface = PureWhite,
    onSurface = DarkText,
    surfaceVariant = LightGray,
    onSurfaceVariant = GrayText,
    outline = BorderGray,
    outlineVariant = BorderGray,
    error = DueRed,
    errorContainer = DueRedContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RedWhiteColorScheme,
        typography = Typography,
        content = content
    )
}
