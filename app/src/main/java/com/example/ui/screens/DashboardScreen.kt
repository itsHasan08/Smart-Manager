package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Month Selector
        item {
            MonthFilterChips(
                selectedMonth = currentMonth,
                onMonthSelected = onMonthSelected
            )
        }

        // Hero Cash Balance Card (Light Rose/Red Background with Crisp Typography)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mess_balance_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = RedPrimaryContainer),
                border = BorderStroke(1.dp, RedPrimary.copy(alpha = 0.25f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Strings.managerBalance(currentLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = RedOnPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "৳${String.format(Locale.US, "%,.2f", summary.currentMessBalance)}",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 32.sp
                                ),
                                color = DarkText
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(RedPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = RedPrimary.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Total Cash Collected (Green badge)
                        Column {
                            Text(
                                text = Strings.totalDeposits(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = GrayText
                            )
                            Text(
                                text = "৳${String.format(Locale.US, "%,.0f", summary.totalDeposits)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AdvanceGreen
                            )
                        }

                        // Total Expenses (Red)
                        Column {
                            Text(
                                text = Strings.totalExpenses(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = GrayText
                            )
                            Text(
                                text = "৳${String.format(Locale.US, "%,.0f", summary.totalExpense)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DueRed
                            )
                        }

                        // Total Due
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = Strings.totalDue(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = GrayText
                            )
                            Text(
                                text = "৳${String.format(Locale.US, "%,.0f", summary.totalDue)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DueRed
                            )
                        }
                    }
                }
            }
        }

        // 3 Big Action Buttons with Contextual Colors
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Deposit -> Green
                DashboardActionButton(
                    icon = Icons.Default.AddCard,
                    label = Strings.quickDeposit(currentLanguage),
                    bgColor = AdvanceGreenContainer,
                    iconColor = AdvanceGreen,
                    borderColor = AdvanceGreenBorder,
                    onClick = onQuickDeposit,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_quick_deposit"
                )

                // 2. Bazar -> Light Coral Red
                DashboardActionButton(
                    icon = Icons.Default.ShoppingCart,
                    label = Strings.quickBazar(currentLanguage),
                    bgColor = RedPrimaryContainer,
                    iconColor = RedPrimary,
                    borderColor = RedPrimary.copy(alpha = 0.2f),
                    onClick = onQuickBazar,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_quick_bazar"
                )

                // 3. Meals -> Warm Amber
                DashboardActionButton(
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
        }

        // Two Key Metrics: Meal Rate (Amber) and Monthly Bazar (Red)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Meal Rate Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, MealAmberBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = Strings.mealRate(currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = GrayText
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MealAmberContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Calculate, contentDescription = null, tint = MealAmber, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "৳${String.format(Locale.US, "%.2f", summary.mealRate)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                            color = DarkText
                        )
                        Text(
                            text = "${Strings.totalMeals(currentLanguage)}: ${String.format(Locale.US, "%.0f", summary.monthlyMealCount)} ${Strings.mealUnit(currentLanguage)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MealAmber
                        )
                    }
                }

                // Total Bazar Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, RedPrimary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = Strings.monthlyBazar(currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = GrayText
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(RedPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ShoppingBasket, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "৳${String.format(Locale.US, "%,.0f", summary.monthlyBazar)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                            color = DarkText
                        )
                        Text(
                            text = "${Strings.rentAndBills(currentLanguage)}: ৳${String.format(Locale.US, "%,.0f", summary.houseRent + summary.totalUtilityBills)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = RedPrimary
                        )
                    }
                }
            }
        }

        // Settle & Slip Banner (Indigo/Purple Tint)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewSettlement() },
                shape = RoundedCornerShape(16.dp),
                color = UtilityIndigoContainer,
                border = BorderStroke(1.dp, UtilityIndigoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(UtilityIndigo),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = Strings.viewMonthlySlip(currentLanguage),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = DarkText
                            )
                            Text(
                                text = Strings.viewMonthlySlipDesc(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = GrayText
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = UtilityIndigo
                    )
                }
            }
        }

        // Member Accounting List Header
        item {
            SectionHeader(
                title = "${Strings.membersSummary(currentLanguage)} (${memberStatements.size})"
            )
        }

        // Member Items
        items(memberStatements, key = { it.member.id }) { stmt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMemberClick(stmt.member.id) }
                    .testTag("dashboard_member_${stmt.member.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (stmt.isDue) RedPrimaryContainer else AdvanceGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stmt.member.name.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (stmt.isDue) RedPrimary else AdvanceGreen,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stmt.member.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = DarkText
                            )
                            Text(
                                text = "${Strings.room(currentLanguage)}: ${stmt.member.roomNumber} • ${Strings.mealsCount(currentLanguage)}: ${String.format(Locale.US, "%.1f", stmt.mealCount)} ${Strings.mealUnit(currentLanguage)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrayText
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        BalanceBadge(
                            isDue = stmt.isDue,
                            amount = if (stmt.isDue) stmt.dueAmount else stmt.advanceAmount
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${Strings.advance(currentLanguage)}: ৳${String.format(Locale.US, "%,.0f", stmt.totalPaid)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrayText
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun DashboardActionButton(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    iconColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = bgColor,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
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
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = DarkText,
                maxLines = 1
            )
        }
    }
}
