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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Member
import com.example.data.model.MessProfile
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
    onMenuClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onToggleLanguage: () -> Unit,
    onRoleChange: (CurrentRole, Long) -> Unit,
    onSettlementClick: () -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = messName.ifBlank { Strings.appName(currentLanguage) },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
                    color = DarkText,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showRoleMenu = true }
                ) {
                    val roleLabel = if (currentRole == CurrentRole.ADMIN) {
                        if (currentLanguage == AppLanguage.BN) "ম্যানেজার মোড" else "Manager Mode"
                    } else {
                        if (currentLanguage == AppLanguage.BN) "সদস্য: $currentMemberName" else "Member: $currentMemberName"
                    }

                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary)
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
                                color = if (currentRole == CurrentRole.ADMIN) BrandPrimary else DarkText
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
        navigationIcon = {
            // Stylized Drawer Menu Hamburger Button
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .testTag("appbar_drawer_btn")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "মেনু খুলুন",
                        tint = BrandPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        actions = {
            // Language Toggle Pill (বাং / EN)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BrandPrimaryContainer,
                border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clickable { onToggleLanguage() }
                    .testTag("language_toggle_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = BrandPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "বাংলা" else "EN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = BrandPrimary
                    )
                }
            }

            // Mess / Manager Profile Setup Button
            IconButton(
                onClick = onEditProfileClick,
                modifier = Modifier.testTag("appbar_profile_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "প্রোফাইল তথ্য",
                    tint = DarkText,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Quick Monthly Statement Slip Button
            IconButton(
                onClick = onSettlementClick,
                modifier = Modifier.testTag("appbar_settlement_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = "মাসিক হিসাব",
                    tint = BrandPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PureWhite
        )
    )
}

@Composable
fun AppDrawerSheet(
    profile: MessProfile?,
    currentLanguage: AppLanguage,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onEditProfileClick: () -> Unit,
    onSettlementClick: () -> Unit,
    onRecycleBinClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerContainerColor = PureWhite,
        drawerTonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Profile Card Header (Mess & Manager Info)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandPrimaryContainer),
                border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PureWhite,
                            border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.25f)),
                            modifier = Modifier.clickable {
                                onEditProfileClick()
                                onCloseDrawer()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentLanguage == AppLanguage.BN) "এডিট" else "Edit",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BrandPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = profile?.messName?.ifBlank { Strings.appName(currentLanguage) } ?: Strings.appName(currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DarkText,
                        maxLines = 1
                    )
                    Text(
                        text = "${if (currentLanguage == AppLanguage.BN) "ম্যানেজার: " else "Manager: "}${profile?.managerName ?: "Manager"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayText
                    )
                    if (!profile?.managerPhone.isNullOrBlank()) {
                        Text(
                            text = "📞 ${profile?.managerPhone}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrayText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderGray)
            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Items (Clean stylized buttons)
            DrawerNavItem(
                icon = Icons.Default.Dashboard,
                label = Strings.drawerHome(currentLanguage),
                isSelected = selectedTab == 0,
                onClick = {
                    onSelectTab(0)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.Restaurant,
                label = Strings.drawerMeals(currentLanguage),
                isSelected = selectedTab == 1,
                onClick = {
                    onSelectTab(1)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.ShoppingCart,
                label = Strings.drawerBazar(currentLanguage),
                isSelected = selectedTab == 2,
                onClick = {
                    onSelectTab(2)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.AccountBalanceWallet,
                label = Strings.drawerDeposits(currentLanguage),
                isSelected = selectedTab == 3,
                onClick = {
                    onSelectTab(3)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.People,
                label = Strings.navMembers(currentLanguage),
                isSelected = selectedTab == 4,
                onClick = {
                    onSelectTab(4)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.ReceiptLong,
                label = Strings.drawerSettlement(currentLanguage),
                isSelected = false,
                onClick = {
                    onSettlementClick()
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.RestoreFromTrash,
                label = if (currentLanguage == AppLanguage.BN) "রিসাইকেল বিন ও ব্যাকআপ" else "Recycle Bin & Backup",
                isSelected = false,
                onClick = {
                    onRecycleBinClick()
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = BorderGray)
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Logout button (Clean and prominent)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onLogoutClick()
                        onCloseDrawer()
                    },
                shape = RoundedCornerShape(10.dp),
                color = ExpenseCoralContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = DueRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "লগআউট করুন" else "Log Out",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = DueRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Smart Mess Manager v2.2",
                style = MaterialTheme.typography.labelSmall,
                color = GrayText.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun DrawerNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) BrandPrimaryContainer else PureWhite,
        border = if (isSelected) BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.25f)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) BrandPrimary else GrayText,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) BrandPrimary else DarkText
            )
        }
    }
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
            tonalElevation = 0.dp,
            modifier = Modifier.height(64.dp)
        ) {
            NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                icon = {
                    Icon(
                        if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = Strings.navDashboard(currentLanguage),
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { 
                    Text(
                        Strings.navDashboard(currentLanguage), 
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandPrimary,
                    selectedTextColor = BrandPrimary,
                    indicatorColor = BrandPrimaryContainer,
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
                        contentDescription = Strings.navMeals(currentLanguage),
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { 
                    Text(
                        Strings.navMeals(currentLanguage), 
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandPrimary,
                    selectedTextColor = BrandPrimary,
                    indicatorColor = BrandPrimaryContainer,
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
                        contentDescription = Strings.navBazar(currentLanguage),
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { 
                    Text(
                        Strings.navBazar(currentLanguage), 
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandPrimary,
                    selectedTextColor = BrandPrimary,
                    indicatorColor = BrandPrimaryContainer,
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
                        if (selectedTab == 3) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                        contentDescription = Strings.navCash(currentLanguage),
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { 
                    Text(
                        Strings.navCash(currentLanguage), 
                        fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal, 
                        fontSize = 11.sp
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandPrimary,
                    selectedTextColor = BrandPrimary,
                    indicatorColor = BrandPrimaryContainer,
                    unselectedIconColor = GrayText,
                    unselectedTextColor = GrayText
                ),
                modifier = Modifier.testTag("nav_cash")
            )

            NavigationBarItem(
                selected = selectedTab == 4,
                onClick = { onTabSelected(4) },
                icon = {
                    Icon(
                        if (selectedTab == 4) Icons.Filled.People else Icons.Outlined.People,
                        contentDescription = Strings.navMembers(currentLanguage),
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { 
                    Text(
                        Strings.navMembers(currentLanguage), 
                        fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal, 
                        fontSize = 11.sp
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandPrimary,
                    selectedTextColor = BrandPrimary,
                    indicatorColor = BrandPrimaryContainer,
                    unselectedIconColor = GrayText,
                    unselectedTextColor = GrayText
                ),
                modifier = Modifier.testTag("nav_members")
            )
        }
    }
}
