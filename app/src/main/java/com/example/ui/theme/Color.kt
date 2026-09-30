package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Clean Palette (Royal Slate & Vibrant Semantic Accents)
val PureWhite = Color(0xFFFFFFFF)
val OffWhite = Color(0xFFF8FAFC)
val SurfaceGray = Color(0xFFF1F5F9)
val LightGray = Color(0xFFF8FAFC)
val BorderGray = Color(0xFFE2E8F0)
val DarkText = Color(0xFF0F172A)
val GrayText = Color(0xFF64748B)

// Core Brand Primary: Elegant Royal Navy / Smart Blue (No harsh red!)
val BrandPrimary = Color(0xFF1E40AF)
val BrandPrimaryLight = Color(0xFF3B82F6)
val BrandPrimaryContainer = Color(0xFFEFF6FF)
val BrandOnPrimary = Color(0xFFFFFFFF)
val BrandOnPrimaryContainer = Color(0xFF1E3A8A)

// Semantic Accents (Clear & Intuitive)
// 1. Advance / Cash Deposit -> Emerald Green
val DepositGreen = Color(0xFF059669)
val DepositGreenContainer = Color(0xFFECFDF5)
val DepositGreenBorder = Color(0xFFA7F3D0)

// 2. Bazar / Due / Expense -> Soft Rose Coral (Used ONLY for spending/due!)
val ExpenseCoral = Color(0xFFE11D48)
val ExpenseCoralContainer = Color(0xFFFFF1F2)
val ExpenseCoralBorder = Color(0xFFFECDD3)

// 3. Balance -> Royal Indigo
val BalanceBlue = Color(0xFF2563EB)
val BalanceBlueContainer = Color(0xFFEFF6FF)
val BalanceBlueBorder = Color(0xFFBFDBFE)

// 4. Meals & Meal Rate -> Warm Amber Gold
val MealAmber = Color(0xFFD97706)
val MealAmberContainer = Color(0xFFFFFBEB)
val MealAmberBorder = Color(0xFFFDE68A)

// 5. Rent & Utility Bills -> Modern Indigo
val UtilityIndigo = Color(0xFF4F46E5)
val UtilityIndigoContainer = Color(0xFFEEF2FF)
val UtilityIndigoBorder = Color(0xFFC7D2FE)

// Semantic compatibility aliases
val RedPrimary = BrandPrimary
val RedPrimaryDark = Color(0xFF1E3A8A)
val RedOnPrimary = BrandOnPrimary
val RedPrimaryContainer = BrandPrimaryContainer
val RedOnPrimaryContainer = BrandOnPrimaryContainer

val AdvanceGreen = DepositGreen
val AdvanceGreenContainer = DepositGreenContainer
val AdvanceGreenBorder = DepositGreenBorder

val DueRed = ExpenseCoral
val DueRedContainer = ExpenseCoralContainer
val DueRedBorder = ExpenseCoralBorder

val EmeraldPrimary = DepositGreen
val EmeraldPrimaryContainer = DepositGreenContainer
val TealSecondary = BrandPrimary
val TealSecondaryContainer = BrandPrimaryContainer
val TealOnSecondaryContainer = BrandOnPrimaryContainer
val AmberTertiary = MealAmber
val AmberTertiaryContainer = MealAmberContainer
val InfoBlue = BalanceBlue
val InfoBlueContainer = BalanceBlueContainer
val WarningOrange = MealAmber
val WarningOrangeContainer = MealAmberContainer
val OnDueRed = PureWhite
val OnAdvanceGreen = PureWhite
