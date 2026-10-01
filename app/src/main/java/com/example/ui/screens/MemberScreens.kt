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
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun MemberManagementScreen(
    memberStatements: List<MemberStatement>,
    currentLanguage: AppLanguage,
    onMemberClick: (Long) -> Unit,
    onAddMemberClick: () -> Unit,
    onRecordDepositClick: (Member) -> Unit = {},
    onEditMemberClick: (Member) -> Unit = {},
    onDeleteMemberClick: (Member) -> Unit = {}
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
                containerColor = BrandPrimary,
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

            // Prominent Top Add Member Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddMemberClick() },
                shape = RoundedCornerShape(14.dp),
                color = BrandPrimaryContainer,
                border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = BrandPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "+ নতুন সদস্য যুক্ত করুন",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BrandPrimary
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BrandPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (currentLanguage == AppLanguage.BN) "সদস্যের নাম বা রুম নম্বর দিয়ে খুঁজুন..." else "Search member name or room..."
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
                    .testTag("member_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Group,
                    title = if (currentLanguage == AppLanguage.BN) "কোনো সদস্য পাওয়া যায়নি" else "No Members Found",
                    description = if (currentLanguage == AppLanguage.BN) "উপরে '+ নতুন সদস্য যুক্ত করুন' বাটনে চাপ দিয়ে সদস্য যোগ করুন" else "Add members to your mess"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredList, key = { it.member.id }) { stmt ->
                        MemberAccountCard(
                            statement = stmt,
                            currentLanguage = currentLanguage,
                            onCardClick = { onMemberClick(stmt.member.id) },
                            onEditClick = { onEditMemberClick(stmt.member) },
                            onDeleteClick = { onDeleteMemberClick(stmt.member) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemberAccountCard(
    statement: MemberStatement,
    currentLanguage: AppLanguage,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val member = statement.member

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("member_card_${member.id}"),
        shape = RoundedCornerShape(16.dp),
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BrandPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.name.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (member.role == "ADMIN") {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandPrimaryContainer
                                ) {
                                    Text(
                                        text = "ম্যানেজার",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = BrandPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "রুম: ${member.roomNumber} (${member.bedNumber}) • 📞 ${member.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayText
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Edit button
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandPrimary, modifier = Modifier.size(18.dp))
                    }

                    // Delete button (Disabled for Admin)
                    if (member.role != "ADMIN") {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = DueRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderGray)
            Spacer(modifier = Modifier.height(10.dp))

            // Account details: PIN and Meals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔑 লগইন পিন: ${member.pin}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayText
                )

                Text(
                    text = "মোট মিল: ${String.format(Locale.US, "%.0f", statement.mealCount)} টি",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = DarkText
                )
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
    onRecordDeposit: () -> Unit = {}
) {
    if (statement == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("সদস্য পাওয়া যায়নি")
        }
        return
    }

    val member = statement.member
    val context = LocalContext.current

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = { Text(member.name, fontWeight = FontWeight.Bold, color = DarkText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrandPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareText = """
                            👤 মেস সদস্য প্রোফাইল: ${member.name}
                            রুম: ${member.roomNumber} (${member.bedNumber})
                            মোবাইল: ${member.phone}
                            লগইন পিন: ${member.pin}
                            মোট মিল: ${statement.mealCount}
                        """.trimIndent()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Profile"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = BrandPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
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
            // Profile Card (NO DEPOSIT BUTTON)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandPrimaryContainer),
                    border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = DarkText
                                )
                                Text(
                                    text = if (member.role == "ADMIN") "👑 মেস ম্যানেজার" else "👤 মেস সদস্য",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrayText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BrandPrimary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("📞 মোবাইল: ${member.phone}", style = MaterialTheme.typography.bodyMedium, color = DarkText)
                        Text("🚪 রুম নম্বর: ${member.roomNumber} (${member.bedNumber})", style = MaterialTheme.typography.bodyMedium, color = DarkText)
                        Text("🔑 লগইন পিন (PIN): ${member.pin}", style = MaterialTheme.typography.bodyMedium, color = DarkText)
                        Text("🍽️ চলতি মাসের মোট মিল: ${String.format(Locale.US, "%.0f", statement.mealCount)} টি", style = MaterialTheme.typography.bodyMedium, color = DarkText)
                    }
                }
            }

            // Note Banner explaining deposit location
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = BrandPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "টাকা জমা দেওয়া বা রেকর্ড করার জন্য নিচের 'নগদ জমা' ট্যাব ব্যবহার করুন।",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayText
                        )
                    }
                }
            }
        }
    }
}
