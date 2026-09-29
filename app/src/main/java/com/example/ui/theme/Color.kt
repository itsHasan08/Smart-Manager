package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Modern White with Light Red Accent and Semantic Highlighting
val PureWhite = Color(0xFFFFFFFF)
val OffWhite = Color(0xFFFAFAFA)
val SurfaceGray = Color(0xFFF9FAFB)
val LightGray = Color(0xFFF9FAFB)
val BorderGray = Color(0xFFE5E7EB)
val DarkText = Color(0xFF111827)
val GrayText = Color(0xFF6B7280)

// Brand Red Accent (Light-to-medium coral red)
val RedPrimary = Color(0xFFDC2626)
val RedPrimaryDark = Color(0xFFB91C1C)
val RedOnPrimary = Color(0xFFFFFFFF)
val RedPrimaryContainer = Color(0xFFFFF1F2)
val RedOnPrimaryContainer = Color(0xFF9F1239)

// Semantic Accents for Contextual Clarity:
// 1. Advance / Cash Deposit / Positive Balance -> Fresh Emerald Green
val AdvanceGreen = Color(0xFF16A34A)
val AdvanceGreenContainer = Color(0xFFF0FDF4)
val AdvanceGreenBorder = Color(0xFFBBF7D0)

// 2. Due / Pending Debt -> Soft Alert Red
val DueRed = Color(0xFFDC2626)
val DueRedContainer = Color(0xFFFEF2F2)
val DueRedBorder = Color(0xFFFECACA)

// 3. Meals & Meal Rate -> Warm Amber/Gold
val MealAmber = Color(0xFFD97706)
val MealAmberContainer = Color(0xFFFFFBEB)
val MealAmberBorder = Color(0xFFFDE68A)

// 4. Rent & Utility Bills -> Modern Indigo
val UtilityIndigo = Color(0xFF4F46E5)
val UtilityIndigoContainer = Color(0xFFEEF2FF)
val UtilityIndigoBorder = Color(0xFFC7D2FE)

// Legacy aliases for theme compatibility
val EmeraldPrimary = RedPrimary
val EmeraldPrimaryContainer = RedPrimaryContainer
val TealSecondary = RedPrimaryDark
val TealSecondaryContainer = RedPrimaryContainer
val TealOnSecondaryContainer = RedOnPrimaryContainer
val AmberTertiary = MealAmber
val AmberTertiaryContainer = MealAmberContainer
val InfoBlue = UtilityIndigo
val InfoBlueContainer = UtilityIndigoContainer
val WarningOrange = MealAmber
val WarningOrangeContainer = MealAmberContainer
val OnDueRed = PureWhite
val OnAdvanceGreen = PureWhite
