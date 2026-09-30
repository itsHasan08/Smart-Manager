package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun DashboardScreen(
    summary: MessSummary,
    profile: MessProfile?,
    memberStatements: List<MemberStatement>,
    currentMonth: String,
    currentLanguage: AppLanguage,
    onMonthSelected: (String) -> Unit,
    onQuickDeposit: () -> Unit,
    onQuickBazar: () -> Unit,
    onQuickMeal: () -> Unit,
    onMemberClick: (Long) -> Unit,
    onViewSettlement: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Month Filter Chips (Compact)
        MonthFilterChips(
            selectedMonth = currentMonth,
            onMonthSelected = onMonthSelected
        )

        // 2. Hero Cash Balance Card (Clean, focused, no clutter)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mess_balance_hero_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = BorderStroke(1.dp, BorderGray),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Strings.managerBalance(currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = GrayText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "৳${String.format(Locale.US, "%,.0f", summary.currentMessBalance)}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 34.sp
                            ),
                            color = BrandPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(BrandPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderGray)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Total Cash Deposit (Green)
                    Column {
                        Text(
                            text = Strings.totalDeposits(currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = GrayText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "৳${String.format(Locale.US, "%,.0f", summary.totalDeposits)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DepositGreen
                        )
                    }

                    // Total Expenses (Coral Red)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = Strings.totalExpenses(currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = GrayText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "৳${String.format(Locale.US, "%,.0f", summary.totalExpense)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ExpenseCoral
                        )
                    }
                }
            }
        }

        // 3. Quick Action Hub (3 Big, tactile, spring-bounce buttons)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Cash Deposit
            BouncyDashboardActionButton(
                icon = Icons.Default.AddCard,
                label = Strings.quickDeposit(currentLanguage),
                bgColor = DepositGreenContainer,
                iconColor = DepositGreen,
                borderColor = DepositGreenBorder,
                onClick = onQuickDeposit,
                modifier = Modifier.weight(1f),
                testTag = "btn_quick_deposit"
            )

            // Bazar Cost
            BouncyDashboardActionButton(
                icon = Icons.Default.ShoppingCart,
                label = Strings.quickBazar(currentLanguage),
                bgColor = ExpenseCoralContainer,
                iconColor = ExpenseCoral,
                borderColor = ExpenseCoralBorder,
                onClick = onQuickBazar,
                modifier = Modifier.weight(1f),
                testTag = "btn_quick_bazar"
            )

            // Daily Meals
            BouncyDashboardActionButton(
                icon = Icons.Default.Restaurant,
                label = Strings.quickMeal(currentLanguage),
                bgColor = MealAmberContainer,
                iconColor = MealAmber,
                borderColor = MealAmberBorder,
                onClick = onQuickMeal,
                modifier = Modifier.weight(1f),
                testTag = "btn_quick_meal"
            )
        }

        // 4. Quick Monthly Overview (Compact 2x2 Clean Metrics Grid)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (currentLanguage == AppLanguage.BN) "চলতি মাসের হিসাব এক নজরে" else "Monthly Overview",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = DarkText
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // Meal Rate
                    DashboardMiniMetric(
                        icon = Icons.Default.Calculate,
                        iconTint = MealAmber,
                        iconBg = MealAmberContainer,
                        label = Strings.mealRate(currentLanguage),
                        value = "৳${String.format(Locale.US, "%.2f", summary.mealRate)}"
                    )

                    // Total Meals
                    DashboardMiniMetric(
                        icon = Icons.Default.RestaurantMenu,
                        iconTint = BrandPrimary,
                        iconBg = BrandPrimaryContainer,
                        label = Strings.totalMeals(currentLanguage),
                        value = "${String.format(Locale.US, "%.0f", summary.monthlyMealCount)} ${Strings.mealUnit(currentLanguage)}"
                    )
                }

                HorizontalDivider(color = BorderGray)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // Total Bazar
                    DashboardMiniMetric(
                        icon = Icons.Default.ShoppingBasket,
                        iconTint = ExpenseCoral,
                        iconBg = ExpenseCoralContainer,
                        label = Strings.monthlyBazar(currentLanguage),
                        value = "৳${String.format(Locale.US, "%,.0f", summary.monthlyBazar)}"
                    )

                    // Active Members
                    DashboardMiniMetric(
                        icon = Icons.Default.People,
                        iconTint = DepositGreen,
                        iconBg = DepositGreenContainer,
                        label = Strings.navMembers(currentLanguage),
                        value = "${memberStatements.size} জন"
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardMiniMetric(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = GrayText
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = DarkText
            )
        }
    }
}

@Composable
private fun BouncyDashboardActionButton(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    iconColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "btn_bounce"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = iconColor),
                onClick = onClick
            )
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PureWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = DarkText,
                maxLines = 1
            )
        }
    }
}
