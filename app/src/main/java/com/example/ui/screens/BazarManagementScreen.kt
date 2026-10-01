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
import com.example.data.model.BazarEntry
import com.example.data.model.Member
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun BazarManagementScreen(
    bazarList: List<BazarEntry>,
    members: List<Member>,
    totalBazar: Double,
    currentLanguage: AppLanguage,
    isManager: Boolean = true,
    onAddBazarClick: () -> Unit = {},
    onEditBazarClick: (BazarEntry) -> Unit = {},
    onDeleteBazarClick: (BazarEntry) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val memberMap = members.associateBy { it.id }

    val filteredList = bazarList.filter { b ->
        val buyerName = memberMap[b.buyerMemberId]?.name ?: ""
        b.itemsSummary.contains(searchQuery, ignoreCase = true) ||
                buyerName.contains(searchQuery, ignoreCase = true) ||
                b.date.contains(searchQuery)
    }

    Scaffold(
        containerColor = PureWhite,
        floatingActionButton = {
            if (isManager) {
                FloatingActionButton(
                    onClick = onAddBazarClick,
                    containerColor = ExpenseCoral,
                    contentColor = PureWhite,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_bazar_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Bazar")
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
                .testTag("bazar_management_screen")
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Total Bazar Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("total_bazar_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ExpenseCoralContainer),
                border = BorderStroke(1.dp, ExpenseCoralBorder)
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
                            text = Strings.monthlyBazar(currentLanguage),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = ExpenseCoral
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "৳${String.format(Locale.US, "%,.0f", totalBazar)}",
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
                            .background(ExpenseCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
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
                        if (currentLanguage == AppLanguage.BN) "বাজারের জিনিস বা বাজারকারী খুঁজুন..." else "Search items or buyer..."
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = BorderGray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bazar_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ShoppingBasket,
                    title = if (currentLanguage == AppLanguage.BN) "কোনো বাজার তালিকা নেই" else "No Bazar Records",
                    description = if (currentLanguage == AppLanguage.BN) "নিচের '+' বাটনে চাপ দিয়ে বাজার যোগ করুন" else "Tap '+' button below to add food market cost"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        val buyerName = memberMap[item.buyerMemberId]?.name ?: "Member"
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.itemsSummary,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = DarkText
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${Strings.buyer(currentLanguage)}: $buyerName • ${Strings.date(currentLanguage)}: ${item.date}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrayText
                                    )
                                    if (item.note.isNotBlank()) {
                                        Text(
                                            text = "নোট: ${item.note}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GrayText
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "৳${String.format(Locale.US, "%,.0f", item.totalAmount)}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp
                                        ),
                                        color = ExpenseCoral
                                    )

                                    if (isManager) {
                                        Spacer(modifier = Modifier.width(6.dp))

                                        // Edit Button
                                        IconButton(
                                            onClick = { onEditBazarClick(item) },
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
                                            onClick = { onDeleteBazarClick(item) },
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
