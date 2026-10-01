package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemberStatement
import com.example.data.model.MessProfile
import com.example.data.model.MessSummary
import com.example.ui.components.BalanceBadge
import com.example.ui.components.CostRow
import com.example.ui.components.MonthFilterChips
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySettlementScreen(
    summary: MessSummary,
    statements: List<MemberStatement>,
    profile: MessProfile?,
    currentMonth: String,
    currentLanguage: AppLanguage,
    onMonthSelected: (String) -> Unit,
    onBack: () -> Unit,
    onGenerateReportText: () -> String
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (currentLanguage == AppLanguage.BN) "মাসিক হিসাব ও স্লিপ" else "Monthly Statement & Audit",
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrandPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val text = onGenerateReportText()
                            shareReport(context, text, currentMonth, currentLanguage)
                        },
                        modifier = Modifier.testTag("share_settlement_report_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export Report", tint = BrandPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            Surface(
                color = PureWhite,
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            val text = onGenerateReportText()
                            shareReport(context, text, currentMonth, currentLanguage)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("export_report_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = PureWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (currentLanguage == AppLanguage.BN) "সম্পূর্ণ মেস হিসাব স্লিপ শেয়ার করুন" else "Share Full Settlement Report",
                            fontWeight = FontWeight.Bold
                        )
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
            item {
                MonthFilterChips(
                    selectedMonth = currentMonth,
                    onMonthSelected = onMonthSelected
                )
            }

            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandPrimaryContainer),
                    border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.BN) "মেসের মাসিক হিসাব সারাংশ" else "Monthly Mess Overview",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = BrandPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${Strings.mealRate(currentLanguage)}: ৳${String.format(Locale.US, "%.2f", summary.mealRate)} / ${if (currentLanguage == AppLanguage.BN) "মিল" else "meal"}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            ),
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = BrandPrimary.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(Strings.totalMeals(currentLanguage), style = MaterialTheme.typography.labelSmall, color = GrayText)
                                Text(
                                    "${String.format(Locale.US, "%.0f", summary.monthlyMealCount)} ${Strings.mealUnit(currentLanguage)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = DarkText
                                )
                            }
                            Column {
                                Text(Strings.monthlyBazar(currentLanguage), style = MaterialTheme.typography.labelSmall, color = GrayText)
                                Text(
                                    "৳${String.format(Locale.US, "%,.0f", summary.monthlyBazar)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ExpenseCoral
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(Strings.totalDeposits(currentLanguage), style = MaterialTheme.typography.labelSmall, color = GrayText)
                                Text(
                                    "৳${String.format(Locale.US, "%,.0f", summary.totalDeposits)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = DepositGreen
                                )
                            }
                        }
                    }
                }
            }

            // Member Settlement Slips
            item {
                Text(
                    text = "${Strings.membersSummary(currentLanguage)} (${statements.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = DarkText
                )
            }

            items(statements) { stmt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stmt.member.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = DarkText
                                )
                                Text(
                                    text = "${Strings.room(currentLanguage)}: ${stmt.member.roomNumber} • ${Strings.mealsCount(currentLanguage)}: ${String.format(Locale.US, "%.1f", stmt.mealCount)} ${Strings.mealUnit(currentLanguage)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrayText
                                )
                            }

                            BalanceBadge(
                                isDue = stmt.isDue,
                                amount = if (stmt.isDue) stmt.dueAmount else stmt.advanceAmount
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        CostRow(
                            label = if (currentLanguage == AppLanguage.BN) {
                                "মিল বাবদ (${String.format(Locale.US, "%.1f", stmt.mealCount)} x ৳${String.format(Locale.US, "%.2f", summary.mealRate)})"
                            } else {
                                "Meal Cost (${String.format(Locale.US, "%.1f", stmt.mealCount)} x ৳${String.format(Locale.US, "%.2f", summary.mealRate)})"
                            },
                            amount = stmt.mealCost
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = BorderGray)

                        CostRow(
                            label = if (currentLanguage == AppLanguage.BN) "মোট খরচ" else "Total Cost",
                            amount = stmt.totalCost,
                            isBold = true
                        )
                        CostRow(
                            label = if (currentLanguage == AppLanguage.BN) "মোট নগদ জমা" else "Total Cash Paid",
                            amount = stmt.totalPaid,
                            isBold = true,
                            amountColor = DepositGreen
                        )
                    }
                }
            }
        }
    }
}

private fun shareReport(context: Context, text: String, month: String, currentLanguage: AppLanguage) {
    val title = if (currentLanguage == AppLanguage.BN) "মেস হিসাব $month" else "Mess Report $month"
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TITLE, title)
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, title))
}
