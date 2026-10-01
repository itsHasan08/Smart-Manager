package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BazarEntry
import com.example.data.model.DepositEntry
import com.example.data.model.Member
import com.example.data.model.MessProfile
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Strings
import com.example.ui.viewmodel.CurrentRole
import com.example.ui.viewmodel.MessViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppSubScreen {
    NONE,
    MEMBER_DETAIL,
    SETTLEMENT,
    RECYCLE_BIN
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Clean uniform solid white status bar & nav bar for absolute clarity
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            )
        )
        setContent {
            MyApplicationTheme {
                SmartMessApp()
            }
        }
    }
}

@Composable
fun SmartMessApp(viewModel: MessViewModel = viewModel()) {
    val messProfile by viewModel.messProfile.collectAsStateWithLifecycle()
    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()
    val activeMembers by viewModel.activeMembers.collectAsStateWithLifecycle()
    val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentMemberId by viewModel.currentMemberId.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    val messSummary by viewModel.messSummary.collectAsStateWithLifecycle()
    val memberStatements by viewModel.memberStatements.collectAsStateWithLifecycle()
    val mealsForMonth by viewModel.mealsForMonth.collectAsStateWithLifecycle()
    val bazarForMonth by viewModel.bazarForMonth.collectAsStateWithLifecycle()
    val depositsForMonth by viewModel.depositsForMonth.collectAsStateWithLifecycle()

    // Startup Loading Animation
    var isAppLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(500L)
        isAppLoading = false
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) }
    var currentSubScreen by remember { mutableStateOf(AppSubScreen.NONE) }
    var selectedDetailMemberId by remember { mutableStateOf<Long?>(null) }

    // Dialog States
    var showAddDepositDialog by remember { mutableStateOf(false) }
    var depositPreselectedMemberId by remember { mutableStateOf<Long?>(null) }
    var showAddBazarDialog by remember { mutableStateOf(false) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // Edit and Delete Dialog States
    var editingBazar by remember { mutableStateOf<BazarEntry?>(null) }
    var deletingBazar by remember { mutableStateOf<BazarEntry?>(null) }

    var editingDeposit by remember { mutableStateOf<DepositEntry?>(null) }
    var deletingDeposit by remember { mutableStateOf<DepositEntry?>(null) }

    var editingMember by remember { mutableStateOf<Member?>(null) }
    var deletingMember by remember { mutableStateOf<Member?>(null) }

    val activeMemberName = allMembers.find { it.id == currentMemberId }?.name ?: "সদস্য"
    val isManager = currentRole == CurrentRole.ADMIN

    // Back handling
    if (drawerState.isOpen) {
        BackHandler {
            coroutineScope.launch { drawerState.close() }
        }
    } else if (currentSubScreen != AppSubScreen.NONE) {
        BackHandler {
            currentSubScreen = AppSubScreen.NONE
            selectedDetailMemberId = null
        }
    } else if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(PureWhite)) {
        if (!isLoggedIn) {
            // Authentication: Login & Sign Up Flow + Mess Onboarding
            AuthScreen(
                profile = messProfile,
                members = allMembers,
                currentLanguage = currentLanguage,
                onLoginSuccess = { phone, pass ->
                    viewModel.loginWithPhonePassword(phone, pass)
                },
                onRegisterSuccess = { name, phone, pass ->
                    viewModel.registerUserAccount(name, phone, pass)
                },
                onGoogleAuth = {
                    viewModel.loginWithGoogle()
                },
                onCreateMess = { mName, mAddr, mPhone, photo ->
                    viewModel.createMess(mName, mAddr, mPhone, photo)
                },
                onJoinMess = { uid, pass ->
                    viewModel.joinMessWithCredentials(uid, pass) != null
                }
            )
        } else {
            // Logged In App UI
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = currentSubScreen == AppSubScreen.NONE,
                drawerContent = {
                    AppDrawerSheet(
                        profile = messProfile,
                        currentLanguage = currentLanguage,
                        selectedTab = selectedTab,
                        onSelectTab = { selectedTab = it },
                        onEditProfileClick = { showEditProfileDialog = true },
                        onSettlementClick = { currentSubScreen = AppSubScreen.SETTLEMENT },
                        onRecycleBinClick = { currentSubScreen = AppSubScreen.RECYCLE_BIN },
                        onLogoutClick = { viewModel.logout() },
                        onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
                    )
                }
            ) {
                Scaffold(
                    containerColor = PureWhite,
                    topBar = {
                        if (currentSubScreen == AppSubScreen.NONE) {
                            TopMessAppBar(
                                messName = messProfile?.messName ?: Strings.appName(currentLanguage),
                                currentRole = currentRole,
                                currentMemberName = activeMemberName,
                                members = allMembers,
                                currentLanguage = currentLanguage,
                                onMenuClick = {
                                    coroutineScope.launch { drawerState.open() }
                                },
                                onEditProfileClick = {
                                    showEditProfileDialog = true
                                },
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onRoleChange = { role, memberId ->
                                    viewModel.setRole(role, memberId)
                                    if (role == CurrentRole.MEMBER) {
                                        selectedDetailMemberId = memberId
                                        currentSubScreen = AppSubScreen.MEMBER_DETAIL
                                    }
                                },
                                onSettlementClick = {
                                    currentSubScreen = AppSubScreen.SETTLEMENT
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentSubScreen == AppSubScreen.NONE) {
                            MainBottomNav(
                                selectedTab = selectedTab,
                                currentLanguage = currentLanguage,
                                onTabSelected = { selectedTab = it }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PureWhite)
                            .padding(innerPadding)
                    ) {
                        when (currentSubScreen) {
                            AppSubScreen.MEMBER_DETAIL -> {
                                val stmt = memberStatements.find { it.member.id == (selectedDetailMemberId ?: currentMemberId) }
                                MemberDetailScreen(
                                    statement = stmt,
                                    mealRate = messSummary.mealRate,
                                    currentLanguage = currentLanguage,
                                    onBack = {
                                        currentSubScreen = AppSubScreen.NONE
                                        selectedDetailMemberId = null
                                    }
                                )
                            }

                            AppSubScreen.SETTLEMENT -> {
                                MonthlySettlementScreen(
                                    summary = messSummary,
                                    statements = memberStatements,
                                    profile = messProfile,
                                    currentMonth = currentMonth,
                                    currentLanguage = currentLanguage,
                                    onMonthSelected = { viewModel.setMonth(it) },
                                    onBack = { currentSubScreen = AppSubScreen.NONE },
                                    onGenerateReportText = { viewModel.generateMonthlyReportText() }
                                )
                            }

                            AppSubScreen.RECYCLE_BIN -> {
                                RecycleBinScreen(
                                    voidedBazars = bazarForMonth.filter { it.isVoided },
                                    voidedDeposits = depositsForMonth.filter { it.isVoided },
                                    members = allMembers,
                                    currentLanguage = currentLanguage,
                                    onBack = { currentSubScreen = AppSubScreen.NONE },
                                    onRestoreBazar = { viewModel.toggleVoidBazar(it.id, true) },
                                    onRestoreDeposit = { viewModel.toggleVoidDeposit(it.id, true) }
                                )
                            }

                            AppSubScreen.NONE -> {
                                AnimatedContent(
                                    targetState = selectedTab,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(100))
                                    },
                                    label = "tab_animation"
                                ) { targetTab ->
                                    when (targetTab) {
                                        0 -> DashboardScreen(
                                            summary = messSummary,
                                            profile = messProfile,
                                            memberStatements = memberStatements,
                                            currentMonth = currentMonth,
                                            currentLanguage = currentLanguage,
                                            onMonthSelected = { viewModel.setMonth(it) },
                                            onQuickDeposit = { showAddDepositDialog = true },
                                            onQuickBazar = { showAddBazarDialog = true },
                                            onQuickMeal = { selectedTab = 1 },
                                            onMemberClick = { memberId ->
                                                selectedDetailMemberId = memberId
                                                currentSubScreen = AppSubScreen.MEMBER_DETAIL
                                            },
                                            onViewSettlement = { currentSubScreen = AppSubScreen.SETTLEMENT },
                                            onToggleLanguage = { viewModel.toggleLanguage() }
                                        )

                                        1 -> MealManagementScreen(
                                            members = activeMembers,
                                            allMonthMeals = mealsForMonth,
                                            messSummary = messSummary,
                                            currentMonth = currentMonth,
                                            currentLanguage = currentLanguage,
                                            currentRole = currentRole,
                                            currentMemberId = currentMemberId,
                                            onSaveMeals = { viewModel.saveDailyMeals(it) },
                                            onSaveSelfMeal = { mId, d, b, l, din, g ->
                                                viewModel.saveMemberSelfMeals(mId, d, b, l, din, g)
                                            }
                                        )

                                        2 -> BazarManagementScreen(
                                            bazarList = bazarForMonth.filter { !it.isVoided },
                                            members = allMembers,
                                            totalBazar = messSummary.monthlyBazar,
                                            currentLanguage = currentLanguage,
                                            onAddBazarClick = { showAddBazarDialog = true },
                                            onEditBazarClick = { editingBazar = it },
                                            onDeleteBazarClick = { deletingBazar = it }
                                        )

                                        3 -> AccountsAndExpensesScreen(
                                            deposits = depositsForMonth.filter { !it.isVoided },
                                            members = allMembers,
                                            messSummary = messSummary,
                                            currentLanguage = currentLanguage,
                                            onAddDepositClick = { showAddDepositDialog = true },
                                            onEditDepositClick = { editingDeposit = it },
                                            onDeleteDepositClick = { deletingDeposit = it }
                                        )

                                        4 -> MemberManagementScreen(
                                            memberStatements = memberStatements,
                                            currentLanguage = currentLanguage,
                                            onMemberClick = { memberId ->
                                                selectedDetailMemberId = memberId
                                                currentSubScreen = AppSubScreen.MEMBER_DETAIL
                                            },
                                            onAddMemberClick = { showAddMemberDialog = true },
                                            onEditMemberClick = { editingMember = it },
                                            onDeleteMemberClick = { deletingMember = it }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Smooth Light Loading Screen Overlay on Startup
        AnimatedVisibility(
            visible = isAppLoading,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            AppSplashScreen(currentLanguage = currentLanguage)
        }
    }

    // Dialogs: Profile, Add, Edit, Delete
    if (showEditProfileDialog) {
        EditMessProfileDialog(
            profile = messProfile,
            currentLanguage = currentLanguage,
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { messName, managerName, phone ->
                val current = messProfile
                val updated = current?.copy(
                    messName = messName,
                    managerName = managerName,
                    managerPhone = phone
                ) ?: MessProfile(
                    id = 1,
                    messName = messName,
                    managerName = managerName,
                    managerPhone = phone,
                    activeMonth = currentMonth
                )
                viewModel.updateProfile(updated)
                showEditProfileDialog = false
            }
        )
    }

    if (showAddDepositDialog) {
        val targetMembers = if (depositPreselectedMemberId != null) {
            allMembers.filter { it.id == depositPreselectedMemberId }
        } else {
            allMembers
        }
        AddDepositDialog(
            members = if (targetMembers.isNotEmpty()) targetMembers else allMembers,
            currentLanguage = currentLanguage,
            onDismiss = {
                showAddDepositDialog = false
                depositPreselectedMemberId = null
            },
            onConfirm = { memberId, amt, date, note ->
                viewModel.addCashDeposit(memberId, amt, date, note)
                showAddDepositDialog = false
                depositPreselectedMemberId = null
            }
        )
    }

    if (showAddBazarDialog) {
        AddBazarDialog(
            members = allMembers,
            currentLanguage = currentLanguage,
            onDismiss = { showAddBazarDialog = false },
            onConfirm = { buyerId, items, amt, category, date, note ->
                viewModel.addBazar(buyerId, items, amt, category, date, note)
                showAddBazarDialog = false
            }
        )
    }

    if (showAddMemberDialog) {
        AddMemberDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { name, phone, email, room, bed, initBal, customRent, notes ->
                viewModel.addMember(name, phone, email, room, bed, initBal, customRent, notes)
                showAddMemberDialog = false
            }
        )
    }

    // Edit and Delete Dialogs (Soft-delete to Recycle Bin to prevent history loss)
    editingBazar?.let { b ->
        EditBazarDialog(
            bazar = b,
            members = allMembers,
            currentLanguage = currentLanguage,
            onDismiss = { editingBazar = null },
            onConfirm = { updated ->
                viewModel.updateBazar(updated)
                editingBazar = null
            }
        )
    }

    deletingBazar?.let { b ->
        ConfirmDeleteDialog(
            title = "বাজার মুছে ফেলবেন?",
            message = "'${b.itemsSummary}' (৳${b.totalAmount}) রিসাইকেল বিনে চলে যাবে। যেকোনো সময় রিস্টোর করা যাবে।",
            onDismiss = { deletingBazar = null },
            onConfirm = {
                viewModel.toggleVoidBazar(b.id, false)
                deletingBazar = null
            }
        )
    }

    editingDeposit?.let { d ->
        EditDepositDialog(
            deposit = d,
            members = allMembers,
            currentLanguage = currentLanguage,
            onDismiss = { editingDeposit = null },
            onConfirm = { updated ->
                viewModel.updateCashDeposit(updated)
                editingDeposit = null
            }
        )
    }

    deletingDeposit?.let { d ->
        ConfirmDeleteDialog(
            title = "নগদ জমা মুছে ফেলবেন?",
            message = "৳${d.amount} টাকার জমা রেকর্ডটি রিসাইকেল বিনে চলে যাবে। যেকোনো সময় রিস্টোর করা যাবে।",
            onDismiss = { deletingDeposit = null },
            onConfirm = {
                viewModel.toggleVoidDeposit(d.id, false)
                deletingDeposit = null
            }
        )
    }

    editingMember?.let { m ->
        EditMemberDialog(
            member = m,
            currentLanguage = currentLanguage,
            onDismiss = { editingMember = null },
            onConfirm = { updated ->
                viewModel.updateMember(updated)
                editingMember = null
            }
        )
    }

    deletingMember?.let { m ->
        ConfirmDeleteDialog(
            title = "সদস্য মুছে ফেলবেন?",
            message = "'${m.name}' মেস সদস্য তালিকা থেকে মুছে ফেলা হবে।",
            onDismiss = { deletingMember = null },
            onConfirm = {
                viewModel.deleteMember(m)
                deletingMember = null
            }
        )
    }
}
