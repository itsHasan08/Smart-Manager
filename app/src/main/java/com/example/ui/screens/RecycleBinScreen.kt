package com.example.ui.screens

import android.widget.Toast
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
import com.example.data.model.BazarEntry
import com.example.data.model.DepositEntry
import com.example.data.model.Member
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    voidedBazars: List<BazarEntry>,
    voidedDeposits: List<DepositEntry>,
    members: List<Member>,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onRestoreBazar: (BazarEntry) -> Unit,
    onRestoreDeposit: (DepositEntry) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val memberMap = members.associateBy { it.id }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (currentLanguage == AppLanguage.BN) "রিসাইকেল বিন ও হিস্টোরি রিস্টোর" else "Recycle Bin & History Restore",
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrandPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .background(PureWhite)
                .testTag("recycle_bin_screen")
        ) {
            // Notice Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandPrimaryContainer),
                border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = BrandPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.BN)
                            "ভুলবশত কোনো বাজার বা জমা ডিলিট হয়ে গেলে এখান থেকে এক ক্লিকে রিস্টোর করে পুনরায় হিসাবে ফিরিয়ে নিতে পারবেন।"
                        else
                            "If any bazar or deposit was deleted mistakenly, restore it here back into active calculations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkText
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switch Tabs (মুছে ফেলা বাজার / মুছে ফেলা জমা)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceGray)
                    .padding(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 0 },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedTab == 0) PureWhite else SurfaceGray,
                    border = if (selectedTab == 0) BorderStroke(1.dp, BorderGray) else null
                ) {
                    Text(
                        text = "মুছে ফেলা বাজার (${voidedBazars.size})",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedTab == 0) BrandPrimary else GrayText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 1 },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedTab == 1) PureWhite else SurfaceGray,
                    border = if (selectedTab == 1) BorderStroke(1.dp, BorderGray) else null
                ) {
                    Text(
                        text = "মুছে ফেলা জমা (${voidedDeposits.size})",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedTab == 1) BrandPrimary else GrayText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Voided Bazars
                if (voidedBazars.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.DeleteSweep,
                        title = "কোনো মুছে ফেলা বাজার নেই",
                        description = "মুছে ফেলা সকল বাজার রেকর্ড এখানে সংরক্ষিত থাকবে"
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {
                        items(voidedBazars, key = { it.id }) { item ->
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
                                        Text(item.itemsSummary, fontWeight = FontWeight.Bold, color = DarkText)
                                        Text("${item.date} • বাজারকারী: $buyerName", style = MaterialTheme.typography.bodySmall, color = GrayText)
                                        Text("৳${String.format(Locale.US, "%,.0f", item.totalAmount)}", fontWeight = FontWeight.Bold, color = ExpenseCoral)
                                    }

                                    Button(
                                        onClick = {
                                            onRestoreBazar(item)
                                            Toast.makeText(context, "বাজার সফলভাবে রিস্টোর হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DepositGreen, contentColor = PureWhite)
                                    ) {
                                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("রিস্টোর", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Voided Deposits
                if (voidedDeposits.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.DeleteSweep,
                        title = "কোনো মুছে ফেলা জমা নেই",
                        description = "মুছে ফেলা সকল নগদ জমার রেকর্ড এখানে সংরক্ষিত থাকবে"
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {
                        items(voidedDeposits, key = { it.id }) { item ->
                            val memberName = memberMap[item.memberId]?.name ?: "Member"
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
                                        Text(memberName, fontWeight = FontWeight.Bold, color = DarkText)
                                        Text("${item.date} • ${item.note.ifBlank { "নগদ জমা" }}", style = MaterialTheme.typography.bodySmall, color = GrayText)
                                        Text("+৳${String.format(Locale.US, "%,.0f", item.amount)}", fontWeight = FontWeight.Bold, color = DepositGreen)
                                    }

                                    Button(
                                        onClick = {
                                            onRestoreDeposit(item)
                                            Toast.makeText(context, "জমা সফলভাবে রিস্টোর হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DepositGreen, contentColor = PureWhite)
                                    ) {
                                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("রিস্টোর", fontWeight = FontWeight.Bold)
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
