package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemberStatement
import com.example.data.model.MessProfile
import com.example.data.model.NotificationNotice
import com.example.ui.components.BalanceBadge
import com.example.ui.components.CostRow
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun MemberDashboardScreen(
    statement: MemberStatement?,
    mealRate: Double,
    profile: MessProfile?,
    notices: List<NotificationNotice>,
    currentMonth: String,
    onNavigateToMeals: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    if (statement == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Member record not found")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("member_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome, ${statement.member.name} 👋",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Room ${statement.member.roomNumber} (${statement.member.bedNumber}) • $currentMonth",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                BalanceBadge(
                    isDue = statement.isDue,
                    amount = if (statement.isDue) statement.dueAmount else statement.advanceAmount
                )
            }
        }

        // Hero Statement Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (statement.isDue) DueRedContainer.copy(alpha = 0.5f) else AdvanceGreenContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (statement.isDue) "🔴 Pending Due to Manager" else "🟢 Current Advance Balance",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (statement.isDue) DueRed else AdvanceGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "৳${String.format(Locale.US, "%,.2f", if (statement.isDue) statement.dueAmount else statement.advanceAmount)}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        ),
                        color = if (statement.isDue) DueRed else AdvanceGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Meal", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "${String.format(Locale.US, "%.1f", statement.mealCount)} 🍚",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Column {
                            Text("Total Cost", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "৳${String.format(Locale.US, "%,.0f", statement.totalCost)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Column {
                            Text("Cash Paid", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "৳${String.format(Locale.US, "%,.0f", statement.totalPaid)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AdvanceGreen
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToMeals,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("My Meals")
                }

                OutlinedButton(
                    onClick = onNavigateToAccount,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Statement")
                }

                Button(
                    onClick = onNavigateToChat,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat")
                }
            }
        }

        // Cost Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "My Cost Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    CostRow(
                        label = "Food / Meal (${String.format(Locale.US, "%.1f", statement.mealCount)} x ৳${String.format(Locale.US, "%.2f", mealRate)})",
                        amount = statement.mealCost
                    )
                    CostRow(label = "House Rent Share", amount = statement.rentShare)
                    CostRow(label = "Utilities Share (Bills)", amount = statement.utilityShare)
                    CostRow(label = "Other Mess Share", amount = statement.otherShare)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    CostRow(label = "Total Monthly Cost", amount = statement.totalCost, isBold = true)
                    CostRow(label = "Total Cash Handed to Manager", amount = statement.totalPaid, isBold = true, amountColor = AdvanceGreen)
                }
            }
        }

        // Recent Notices for Member
        item {
            Text(
                text = "📢 Latest Mess Announcements",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (notices.isEmpty()) {
            item {
                Text(
                    text = "No recent notices",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(notices.take(3)) { notice ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = notice.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = notice.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
