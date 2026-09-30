package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ModernCleanColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandOnPrimaryContainer,
    secondary = BrandPrimaryLight,
    onSecondary = PureWhite,
    secondaryContainer = BrandPrimaryContainer,
    onSecondaryContainer = BrandOnPrimaryContainer,
    tertiary = DepositGreen,
    onTertiary = PureWhite,
    tertiaryContainer = DepositGreenContainer,
    onTertiaryContainer = DepositGreen,
    background = OffWhite,
    onBackground = DarkText,
    surface = PureWhite,
    onSurface = DarkText,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = GrayText,
    outline = BorderGray,
    outlineVariant = BorderGray,
    error = ExpenseCoral,
    errorContainer = ExpenseCoralContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ModernCleanColorScheme,
        typography = Typography,
        content = content
    )
}
