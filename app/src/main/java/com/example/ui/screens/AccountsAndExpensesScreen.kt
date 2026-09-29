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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun AccountsAndExpensesScreen(
    deposits: List<DepositEntry>,
    expenses: List<ExpenseEntry>,
    members: List<Member>,
    messSummary: MessSummary,
    currentLanguage: AppLanguage,
    onAddDepositClick: () -> Unit,
    onAddExpenseClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val memberMap = members.associateBy { it.id }

    Scaffold(
        containerColor = PureWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) onAddDepositClick() else onAddExpenseClick()
                },
                containerColor = RedPrimary,
                contentColor = PureWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("accounts_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .background(PureWhite)
                .testTag("accounts_screen")
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Two Simple, Clean Tabs (Deposits vs Bills)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton(
                    label = "${Strings.depositsTab(currentLanguage)} (${deposits.size})",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    label = "${Strings.rentBillsTab(currentLanguage)} (${expenses.size})",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Deposits Tab (Crisp Green Banner)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AdvanceGreenContainer),
                    border = BorderStroke(1.dp, AdvanceGreenBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Strings.totalDeposits(currentLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = AdvanceGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "৳${String.format(Locale.US, "%,.2f", messSummary.totalDeposits)}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp
                                ),
                                color = DarkText
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AdvanceGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddCard, contentDescription = null, tint = PureWhite, modifier = Modifier.size(24.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (deposits.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = if (currentLanguage == AppLanguage.BN) "কোনো জমা রেকর্ড নেই" else "No Deposits Recorded",
                        description = if (currentLanguage == AppLanguage.BN) "নিচের লাল '+' বাটনে চাপ দিয়ে নগদ জমা যোগ করুন" else "Tap '+' button below to record cash deposit"
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(deposits, key = { it.id }) { dep ->
                            val member = memberMap[dep.memberId]
                            val name = member?.name ?: "Member"

                            Card(
                                modifier = Modifier.fillMaxWidth(),
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
                                    Column {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = DarkText
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${Strings.date(currentLanguage)}: ${dep.date} • ${dep.note}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GrayText
                                        )
                                    }

                                    Text(
                                        text = "+৳${String.format(Locale.US, "%,.0f", dep.amount)}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ),
                                        color = AdvanceGreen
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Bills & Rent Tab (Indigo Banner)
                val totalBills = messSummary.houseRent + messSummary.totalUtilityBills + messSummary.otherExpenses
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = UtilityIndigoContainer),
                    border = BorderStroke(1.dp, UtilityIndigoBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Strings.rentBillsTab(currentLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = UtilityIndigo
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "৳${String.format(Locale.US, "%,.2f", totalBills)}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp
                                ),
                                color = DarkText
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(UtilityIndigo),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = PureWhite, modifier = Modifier.size(24.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (expenses.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.ReceiptLong,
                        title = if (currentLanguage == AppLanguage.BN) "কোনো বিল এন্ট্রি নেই" else "No Bills Found",
                        description = if (currentLanguage == AppLanguage.BN) "নিচের লাল '+' বাটনে চাপ দিয়ে বাসা ভাড়া বা বিল যোগ করুন" else "Tap '+' button below to add rent or bills"
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(expenses, key = { it.id }) { exp ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
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
                                    Column {
                                        Text(
                                            text = exp.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = DarkText
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${Strings.date(currentLanguage)}: ${exp.date}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GrayText
                                        )
                                    }

                                    Text(
                                        text = "৳${String.format(Locale.US, "%,.0f", exp.amount)}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ),
                                        color = UtilityIndigo
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) RedPrimary else PureWhite,
        border = BorderStroke(1.dp, if (isSelected) RedPrimary else BorderGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) PureWhite else DarkText
            )
        }
    }
}
