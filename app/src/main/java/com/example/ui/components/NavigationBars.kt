package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Member
import com.example.ui.theme.*
import com.example.ui.viewmodel.CurrentRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopMessAppBar(
    messName: String,
    currentRole: CurrentRole,
    currentMemberName: String,
    members: List<Member>,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onRoleChange: (CurrentRole, Long) -> Unit,
    onSettlementClick: () -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = messName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = DarkText,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showRoleMenu = true }
                ) {
                    val roleLabel = if (currentRole == CurrentRole.ADMIN) {
                        if (currentLanguage == AppLanguage.BN) "ম্যানেজার মোড (Admin)" else "Manager Mode (Admin)"
                    } else {
                        if (currentLanguage == AppLanguage.BN) "সদস্য: $currentMemberName" else "Member: $currentMemberName"
                    }

                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(RedPrimary)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = roleLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = GrayText
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Switch Role",
                        modifier = Modifier.size(16.dp),
                        tint = GrayText
                    )
                }

                DropdownMenu(
                    expanded = showRoleMenu,
                    onDismissRequest = { showRoleMenu = false },
                    modifier = Modifier.background(PureWhite)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (currentLanguage == AppLanguage.BN) "👑 ম্যানেজার ভিউ (Admin)" else "👑 Manager View (Admin)",
                                fontWeight = if (currentRole == CurrentRole.ADMIN) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentRole == CurrentRole.ADMIN) RedPrimary else DarkText
                            )
                        },
                        onClick = {
                            onRoleChange(CurrentRole.ADMIN, 1L)
                            showRoleMenu = false
                        }
                    )
                    HorizontalDivider(color = BorderGray)
                    Text(
                        if (currentLanguage == AppLanguage.BN) "সদস্যের ভিউ দেখুন:" else "Switch to Member View:",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = GrayText
                    )
                    members.forEach { m ->
                        DropdownMenuItem(
                            text = { Text("👤 ${m.name} (${Strings.room(currentLanguage)} ${m.roomNumber})", color = DarkText) },
                            onClick = {
                                onRoleChange(CurrentRole.MEMBER, m.id)
                                showRoleMenu = false
                            }
                        )
                    }
                }
            }
        },
        actions = {
            // Elegant Language Toggle Pill (বাং / EN)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = RedPrimaryContainer,
                border = BorderStroke(1.dp, RedPrimary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clickable { onToggleLanguage() }
                    .testTag("language_toggle_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = RedPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "বাংলা" else "English",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = RedPrimary
                    )
                }
            }

            // Quick Monthly Statement Slip Button
            IconButton(
                onClick = onSettlementClick,
                modifier = Modifier.testTag("appbar_settlement_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = "মাসিক হিসাব",
                    tint = RedPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PureWhite
        )
    )
}

@Composable
fun MainBottomNav(
    selectedTab: Int,
    currentLanguage: AppLanguage,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        color = PureWhite,
        border = BorderStroke(1.dp, BorderGray)
    ) {
        NavigationBar(
            containerColor = PureWhite,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                icon = {
                    Icon(
                        if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = Strings.navDashboard(currentLanguage)
                    )
                },
                label = { Text(Strings.navDashboard(currentLanguage), fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RedPrimary,
                    selectedTextColor = RedPrimary,
                    indicatorColor = RedPrimaryContainer,
                    unselectedIconColor = GrayText,
                    unselectedTextColor = GrayText
                ),
                modifier = Modifier.testTag("nav_home")
            )

            NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                icon = {
                    Icon(
                        if (selectedTab == 1) Icons.Filled.Restaurant else Icons.Outlined.Restaurant,
                        contentDescription = Strings.navMeals(currentLanguage)
                    )
                },
                label = { Text(Strings.navMeals(currentLanguage), fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RedPrimary,
                    selectedTextColor = RedPrimary,
                    indicatorColor = RedPrimaryContainer,
                    unselectedIconColor = GrayText,
                    unselectedTextColor = GrayText
                ),
                modifier = Modifier.testTag("nav_meals")
            )

            NavigationBarItem(
                selected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                icon = {
                    Icon(
                        if (selectedTab == 2) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = Strings.navBazar(currentLanguage)
                    )
                },
                label = { Text(Strings.navBazar(currentLanguage), fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RedPrimary,
                    selectedTextColor = RedPrimary,
                    indicatorColor = RedPrimaryContainer,
                    unselectedIconColor = GrayText,
                    unselectedTextColor = GrayText
                ),
                modifier = Modifier.testTag("nav_bazar")
            )

            NavigationBarItem(
                selected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                icon = {
                    Icon(
                        if (selectedTab == 3) Icons.Filled.People else Icons.Outlined.People,
                        contentDescription = Strings.navMembers(currentLanguage)
                    )
                },
                label = { Text(Strings.navMembers(currentLanguage), fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RedPrimary,
                    selectedTextColor = RedPrimary,
                    indicatorColor = RedPrimaryContainer,
                    unselectedIconColor = GrayText,
                    unselectedTextColor = GrayText
                ),
                modifier = Modifier.testTag("nav_members")
            )
        }
    }
}
