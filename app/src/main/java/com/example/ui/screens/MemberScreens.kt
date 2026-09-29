package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Member
import com.example.data.model.MemberStatement
import com.example.ui.components.BalanceBadge
import com.example.ui.components.CostRow
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun MemberManagementScreen(
    memberStatements: List<MemberStatement>,
    currentLanguage: AppLanguage,
    onMemberClick: (Long) -> Unit,
    onAddMemberClick: () -> Unit,
    onRecordDepositClick: (Member) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = memberStatements.filter { s ->
        s.member.name.contains(searchQuery, ignoreCase = true) ||
                s.member.phone.contains(searchQuery) ||
                s.member.roomNumber.contains(searchQuery)
    }

    Scaffold(
        containerColor = PureWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMemberClick,
                containerColor = RedPrimary,
                contentColor = PureWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("add_member_fab")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = Strings.addMember(currentLanguage))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .background(PureWhite)
                .testTag("member_management_screen")
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (currentLanguage == AppLanguage.BN) "সদস্যের নাম বা রুম নম্বর দিয়ে খুঁজুন..." else "Search member name or room..."
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RedPrimary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPrimary,
                    unfocusedBorderColor = BorderGray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("member_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.People,
                    title = if (currentLanguage == AppLanguage.BN) "কোনো সদস্য পাওয়া যায়নি" else "No Members Found",
                    description = if (currentLanguage == AppLanguage.BN) "সদস্য যোগ করতে নিচের লাল '+' বাটনে চাপুন" else "Tap '+' button below to add member"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredList, key = { it.member.id }) { stmt ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMemberClick(stmt.member.id) }
                                .testTag("member_card_${stmt.member.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(if (stmt.isDue) RedPrimaryContainer else AdvanceGreenContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = stmt.member.name.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (stmt.isDue) RedPrimary else AdvanceGreen
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = stmt.member.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = DarkText
                                            )
                                            Text(
                                                text = "${Strings.room(currentLanguage)} ${stmt.member.roomNumber} (${stmt.member.bedNumber}) • ${stmt.member.phone}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = GrayText
                                            )
                                        }
                                    }

                                    BalanceBadge(
                                        isDue = stmt.isDue,
                                        amount = if (stmt.isDue) stmt.dueAmount else stmt.advanceAmount
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = BorderGray)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${Strings.mealsCount(currentLanguage)}: ${String.format(Locale.US, "%.1f", stmt.mealCount)} ${Strings.mealUnit(currentLanguage)} • খরচ: ৳${String.format(Locale.US, "%,.0f", stmt.totalCost)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkText
                                    )

                                    Button(
                                        onClick = { onRecordDepositClick(stmt.member) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AdvanceGreen, contentColor = PureWhite),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            Strings.quickDeposit(currentLanguage),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailScreen(
    statement: MemberStatement?,
    mealRate: Double,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onRecordDeposit: () -> Unit
) {
    if (statement == null) {
        Box(modifier = Modifier.fillMaxSize().background(PureWhite), contentAlignment = Alignment.Center) {
            Text(if (currentLanguage == AppLanguage.BN) "সদস্যের তথ্য পাওয়া যায়নি" else "Member not found")
        }
        return
    }

    val context = LocalContext.current

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = { Text(statement.member.name, fontWeight = FontWeight.Bold, color = DarkText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RedPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        shareMemberStatement(context, statement, mealRate, currentLanguage)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = RedPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            Surface(
                color = PureWhite,
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { shareMemberStatement(context, statement, mealRate, currentLanguage) },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, RedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(Strings.shareSlip(currentLanguage), color = RedPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onRecordDeposit,
                        colors = ButtonDefaults.buttonColors(containerColor = AdvanceGreen, contentColor = PureWhite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AddCard, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(Strings.recordDeposit(currentLanguage), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .background(PureWhite),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card (White with Red Accent)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (statement.isDue) RedPrimaryContainer else AdvanceGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = statement.member.name.take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (statement.isDue) RedPrimary else AdvanceGreen
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = statement.member.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DarkText
                            )
                            Text(
                                text = "📱 ${statement.member.phone}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayText
                            )
                            Text(
                                text = "🏠 ${Strings.room(currentLanguage)}: ${statement.member.roomNumber} (${statement.member.bedNumber})",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrayText
                            )
                        }
                    }
                }
            }

            // Clean Financial Breakdown Slip Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.BN) "ব্যক্তিগত হিসাব স্লিপ" else "Personal Statement Slip",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DarkText
                            )
                            BalanceBadge(
                                isDue = statement.isDue,
                                amount = if (statement.isDue) statement.dueAmount else statement.advanceAmount
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        CostRow(
                            label = if (currentLanguage == AppLanguage.BN) {
                                "মিল খরচ (${String.format(Locale.US, "%.1f", statement.mealCount)} x ৳${String.format(Locale.US, "%.2f", mealRate)})"
                            } else {
                                "Meal Cost (${String.format(Locale.US, "%.1f", statement.mealCount)} x ৳${String.format(Locale.US, "%.2f", mealRate)})"
                            },
                            amount = statement.mealCost
                        )
                        CostRow(
                            label = if (currentLanguage == AppLanguage.BN) "বাসা ভাড়া শেয়ার" else "House Rent Share",
                            amount = statement.rentShare
                        )
                        CostRow(
                            label = if (currentLanguage == AppLanguage.BN) "বিদ্যুৎ ও গ্যাস বিল শেয়ার" else "Utility Bills Share",
                            amount = statement.utilityShare
                        )
                        CostRow(
                            label = if (currentLanguage == AppLanguage.BN) "অন্যান্য খরচ শেয়ার" else "Other Expenses Share",
                            amount = statement.otherShare
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderGray)

                        CostRow(
                            label = if (currentLanguage == AppLanguage.BN) "মোট খরচ" else "Total Cost",
                            amount = statement.totalCost,
                            isBold = true
                        )
                        CostRow(
                            label = if (currentLanguage == AppLanguage.BN) "মোট নগদ জমা" else "Total Cash Deposited",
                            amount = statement.totalPaid,
                            isBold = true,
                            amountColor = AdvanceGreen
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderGray)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (statement.isDue) {
                                    if (currentLanguage == AppLanguage.BN) "🔴 বাকি (Payable Due)" else "🔴 Payable Due"
                                } else {
                                    if (currentLanguage == AppLanguage.BN) "🟢 জমা ব্যালেন্স (Advance)" else "🟢 Advance Balance"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (statement.isDue) RedPrimary else AdvanceGreen
                            )
                            Text(
                                text = "৳${String.format(Locale.US, "%,.2f", if (statement.isDue) statement.dueAmount else statement.advanceAmount)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (statement.isDue) RedPrimary else AdvanceGreen
                            )
                        }
                    }
                }
            }

            // Deposit History List
            item {
                Text(
                    text = if (currentLanguage == AppLanguage.BN) {
                        "টাকা জমা দেওয়ার তালিকা (${statement.deposits.size} বার)"
                    } else {
                        "Deposit History (${statement.deposits.size})"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = DarkText
                )
            }

            if (statement.deposits.isEmpty()) {
                item {
                    Text(
                        if (currentLanguage == AppLanguage.BN) "এই মাসে এখনো কোনো টাকা জমা দেওয়া হয়নি" else "No deposits recorded for this month yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayText
                    )
                }
            } else {
                items(statement.deposits) { dep ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PureWhite,
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${Strings.date(currentLanguage)}: ${dep.date}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = DarkText
                                )
                                Text(text = dep.note, style = MaterialTheme.typography.bodySmall, color = GrayText)
                            }
                            Text(
                                text = "+৳${String.format(Locale.US, "%,.0f", dep.amount)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AdvanceGreen
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

private fun shareMemberStatement(context: Context, stmt: MemberStatement, mealRate: Double, lang: AppLanguage) {
    val text = if (lang == AppLanguage.BN) {
        """
        🧾 মেস হিসাব স্লিপ: ${stmt.member.name}
        রুম: ${stmt.member.roomNumber} (${stmt.member.bedNumber})
        
        🍚 মোট মিল: ${String.format(Locale.US, "%.1f", stmt.mealCount)} টি x ৳${String.format(Locale.US, "%.2f", mealRate)} = ৳${String.format(Locale.US, "%.2f", stmt.mealCost)}
        🏠 বাসা ভাড়া: ৳${String.format(Locale.US, "%.2f", stmt.rentShare)}
        ⚡ বিল ও অন্যান্য: ৳${String.format(Locale.US, "%.2f", stmt.utilityShare + stmt.otherShare)}
        -----------------------------------------
        মোট খরচ: ৳${String.format(Locale.US, "%.2f", stmt.totalCost)}
        মোট জমা: ৳${String.format(Locale.US, "%.2f", stmt.totalPaid)}
        -----------------------------------------
        বর্তমান অবস্থা: ${if (stmt.isDue) "🔴 বাকি: ৳" + String.format(Locale.US, "%.2f", stmt.dueAmount) else "🟢 জমা: ৳" + String.format(Locale.US, "%.2f", stmt.advanceAmount)}
        
        স্মার্ট মেস ম্যানেজার দ্বারা তৈরি
        """.trimIndent()
    } else {
        """
        🧾 MESS STATEMENT: ${stmt.member.name}
        Room: ${stmt.member.roomNumber} (${stmt.member.bedNumber})
        
        🍚 Total Meals: ${String.format(Locale.US, "%.1f", stmt.mealCount)} x ৳${String.format(Locale.US, "%.2f", mealRate)} = ৳${String.format(Locale.US, "%.2f", stmt.mealCost)}
        🏠 House Rent Share: ৳${String.format(Locale.US, "%.2f", stmt.rentShare)}
        ⚡ Utility & Bills Share: ৳${String.format(Locale.US, "%.2f", stmt.utilityShare + stmt.otherShare)}
        -----------------------------------------
        Total Cost: ৳${String.format(Locale.US, "%.2f", stmt.totalCost)}
        Total Paid: ৳${String.format(Locale.US, "%.2f", stmt.totalPaid)}
        -----------------------------------------
        STATUS: ${if (stmt.isDue) "🔴 DUE: ৳" + String.format(Locale.US, "%.2f", stmt.dueAmount) else "🟢 ADVANCE: ৳" + String.format(Locale.US, "%.2f", stmt.advanceAmount)}
        
        Generated by Smart Mess Manager
        """.trimIndent()
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Mess Statement - ${stmt.member.name}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, if (lang == AppLanguage.BN) "স্লিপ শেয়ার করুন" else "Share Statement"))
}
