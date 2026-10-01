package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
    expenses: List<ExpenseEntry> = emptyList(),
    members: List<Member>,
    messSummary: MessSummary,
    currentLanguage: AppLanguage,
    isManager: Boolean = true,
    onAddDepositClick: () -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    onEditDepositClick: (DepositEntry) -> Unit = {},
    onDeleteDepositClick: (DepositEntry) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val memberMap = members.associateBy { it.id }

    val filteredDeposits = deposits.filter { d ->
        val mName = memberMap[d.memberId]?.name ?: ""
        mName.contains(searchQuery, ignoreCase = true) ||
                d.date.contains(searchQuery) ||
                d.note.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = PureWhite,
        floatingActionButton = {
            if (isManager) {
                FloatingActionButton(
                    onClick = onAddDepositClick,
                    containerColor = DepositGreen,
                    contentColor = PureWhite,
                    shape = CircleShape,
                    modifier = Modifier.testTag("accounts_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "নগদ জমা নিন"
                    )
                }
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

            // Total Collected Cash Hero Card (Green)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DepositGreenContainer),
                border = BorderStroke(1.dp, DepositGreenBorder)
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
                            color = DepositGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "৳${String.format(Locale.US, "%,.0f", messSummary.totalDeposits)}",
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
                            .background(DepositGreen),
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (currentLanguage == AppLanguage.BN) "সদস্যের নাম বা তারিখ দিয়ে জমা খুঁজুন..." else "Search deposit by member or date..."
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DepositGreen,
                    unfocusedBorderColor = BorderGray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("deposit_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredDeposits.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Savings,
                    title = if (currentLanguage == AppLanguage.BN) "কোনো জমার হিসাব পাওয়া যায়নি" else "No Deposits Recorded",
                    description = if (currentLanguage == AppLanguage.BN) "নিচের '+' বাটনে চাপ দিয়ে সদস্যের নগদ জমা রেকর্ড করুন" else "Tap '+' button below to record member cash deposit"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredDeposits, key = { it.id }) { item ->
                        val member = memberMap[item.memberId]
                        val memberName = member?.name ?: "সদস্য #${item.memberId}"

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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(DepositGreenContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            tint = DepositGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = memberName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = DarkText
                                        )
                                        Text(
                                            text = "${item.date} • ${item.note.ifBlank { "নগদ জমা" }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GrayText
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+৳${String.format(Locale.US, "%,.0f", item.amount)}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp
                                        ),
                                        color = DepositGreen
                                    )

                                    if (isManager) {
                                        Spacer(modifier = Modifier.width(6.dp))

                                        // Edit Button
                                        IconButton(
                                            onClick = { onEditDepositClick(item) },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = BrandPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Delete Button
                                        IconButton(
                                            onClick = { onDeleteDepositClick(item) },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.DeleteOutline,
                                                contentDescription = "Delete",
                                                tint = DueRed,
                                                modifier = Modifier.size(18.dp)
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
    }
}
