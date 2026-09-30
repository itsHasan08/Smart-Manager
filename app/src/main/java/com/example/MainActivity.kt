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
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Strings
import com.example.ui.viewmodel.CurrentRole
import com.example.ui.viewmodel.MessViewModel
import kotlinx.coroutines.delay

enum class AppSubScreen {
    NONE,
    MEMBER_DETAIL,
    SETTLEMENT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
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

    val messSummary by viewModel.messSummary.collectAsStateWithLifecycle()
    val memberStatements by viewModel.memberStatements.collectAsStateWithLifecycle()
    val mealsForMonth by viewModel.mealsForMonth.collectAsStateWithLifecycle()
    val bazarForMonth by viewModel.bazarForMonth.collectAsStateWithLifecycle()
    val expensesForMonth by viewModel.expensesForMonth.collectAsStateWithLifecycle()
    val depositsForMonth by viewModel.depositsForMonth.collectAsStateWithLifecycle()

    // Smooth App Startup Loading Animation State
    var isAppLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(900L)
        isAppLoading = false
    }

    var selectedTab by remember { mutableStateOf(0) }
    var currentSubScreen by remember { mutableStateOf(AppSubScreen.NONE) }
    var selectedDetailMemberId by remember { mutableStateOf<Long?>(null) }

    // Dialogs
    var showAddDepositDialog by remember { mutableStateOf(false) }
    var depositPreselectedMemberId by remember { mutableStateOf<Long?>(null) }
    var showAddBazarDialog by remember { mutableStateOf(false) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    val activeMemberName = allMembers.find { it.id == currentMemberId }?.name ?: "সদস্য"

    // Back handling
    if (currentSubScreen != AppSubScreen.NONE) {
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
                            },
                            onRecordDeposit = {
                                depositPreselectedMemberId = stmt?.member?.id
                                showAddDepositDialog = true
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

                    AppSubScreen.NONE -> {
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
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
                                    onViewSettlement = { currentSubScreen = AppSubScreen.SETTLEMENT }
                                )

                                1 -> MealManagementScreen(
                                    members = activeMembers,
                                    allMonthMeals = mealsForMonth,
                                    messSummary = messSummary,
                                    currentMonth = currentMonth,
                                    currentLanguage = currentLanguage,
                                    onSaveMeals = { viewModel.saveDailyMeals(it) }
                                )

                                2 -> BazarManagementScreen(
                                    bazarList = bazarForMonth,
                                    members = allMembers,
                                    totalBazar = messSummary.monthlyBazar,
                                    currentLanguage = currentLanguage,
                                    onAddBazarClick = { showAddBazarDialog = true }
                                )

                                3 -> AccountsAndExpensesScreen(
                                    deposits = depositsForMonth,
                                    expenses = expensesForMonth,
                                    members = allMembers,
                                    messSummary = messSummary,
                                    currentLanguage = currentLanguage,
                                    onAddDepositClick = { showAddDepositDialog = true },
                                    onAddExpenseClick = { showAddExpenseDialog = true }
                                )

                                4 -> MemberManagementScreen(
                                    memberStatements = memberStatements,
                                    currentLanguage = currentLanguage,
                                    onMemberClick = { memberId ->
                                        selectedDetailMemberId = memberId
                                        currentSubScreen = AppSubScreen.MEMBER_DETAIL
                                    },
                                    onAddMemberClick = { showAddMemberDialog = true },
                                    onRecordDepositClick = { member ->
                                        depositPreselectedMemberId = member.id
                                        showAddDepositDialog = true
                                    }
                                )
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
            exit = fadeOut(animationSpec = tween(350))
        ) {
            AppSplashScreen(currentLanguage = currentLanguage)
        }
    }

    // Clean Dialogs with Bilingual Support and Itemized Bazar
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

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showAddExpenseDialog = false },
            onConfirm = { title, cat, amt, date, status, note ->
                viewModel.addExpense(title, cat, amt, date, status, note)
                showAddExpenseDialog = false
            }
        )
    }
}
