package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.MessRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class CurrentRole {
    ADMIN,
    MEMBER
}

class MessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MessRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = MessRepository(database.messDao())
    }

    // Current Active Month (e.g., "2026-09")
    private val _currentMonth = MutableStateFlow("2026-09")
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()

    // Current Role & Active Logged In Member ID
    private val _currentRole = MutableStateFlow(CurrentRole.ADMIN)
    val currentRole: StateFlow<CurrentRole> = _currentRole.asStateFlow()

    private val _currentMemberId = MutableStateFlow<Long>(2L) // default to Rahim Uddin for Member view
    val currentMemberId: StateFlow<Long> = _currentMemberId.asStateFlow()

    // Language Support (Bangla & English)
    private val _currentLanguage = MutableStateFlow(com.example.ui.theme.AppLanguage.BN)
    val currentLanguage: StateFlow<com.example.ui.theme.AppLanguage> = _currentLanguage.asStateFlow()

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == com.example.ui.theme.AppLanguage.BN) {
            com.example.ui.theme.AppLanguage.EN
        } else {
            com.example.ui.theme.AppLanguage.BN
        }
    }

    fun setLanguage(lang: com.example.ui.theme.AppLanguage) {
        _currentLanguage.value = lang
    }

    // Today's date string
    val todayDateString: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Base flows
    val messProfile: StateFlow<MessProfile?> = repository.messProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allMembers: StateFlow<List<Member>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMembers: StateFlow<List<Member>> = repository.activeMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationNotice>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messages: StateFlow<List<InAppMessage>> = repository.messages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLog>> = repository.activityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Month-specific data flows
    val mealsForMonth: StateFlow<List<MealEntry>> = _currentMonth.flatMapLatest { month ->
        repository.getMealsByMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bazarForMonth: StateFlow<List<BazarEntry>> = _currentMonth.flatMapLatest { month ->
        repository.getBazarByMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expensesForMonth: StateFlow<List<ExpenseEntry>> = _currentMonth.flatMapLatest { month ->
        repository.getExpensesByMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val depositsForMonth: StateFlow<List<DepositEntry>> = _currentMonth.flatMapLatest { month ->
        repository.getDepositsByMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class MonthFinancialData(
        val meals: List<MealEntry>,
        val bazars: List<BazarEntry>,
        val expenses: List<ExpenseEntry>,
        val deposits: List<DepositEntry>
    )

    private val monthFinancialData = combine(
        mealsForMonth,
        bazarForMonth,
        expensesForMonth,
        depositsForMonth
    ) { meals, bazars, expenses, deposits ->
        MonthFinancialData(meals, bazars, expenses, deposits)
    }

    // Calculation: Mess Summary
    val messSummary: StateFlow<MessSummary> = combine(
        allMembers,
        activeMembers,
        monthFinancialData
    ) { members, activeM, finance ->
        calculateMessSummary(members, activeM, finance.meals, finance.bazars, finance.expenses, finance.deposits)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MessSummary())

    // Calculation: Member Statements
    val memberStatements: StateFlow<List<MemberStatement>> = combine(
        allMembers,
        activeMembers,
        monthFinancialData
    ) { members, activeM, finance ->
        calculateMemberStatements(members, activeM, finance.meals, finance.bazars, finance.expenses, finance.deposits)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered unified transactions
    val transactionLedger: StateFlow<List<TransactionItem>> = combine(
        depositsForMonth,
        bazarForMonth,
        expensesForMonth,
        allMembers
    ) { deposits, bazars, expenses, members ->
        val memberMap = members.associateBy { it.id }
        val list = mutableListOf<TransactionItem>()

        // Deposits
        deposits.forEach { d ->
            val mName = memberMap[d.memberId]?.name ?: "Member #${d.memberId}"
            list.add(
                TransactionItem(
                    id = d.id,
                    type = TransactionType.DEPOSIT,
                    title = "Deposit: $mName",
                    description = "${d.paymentType} • ${d.note}",
                    amount = d.amount,
                    isIncome = true,
                    date = d.date,
                    memberName = mName,
                    isVoided = d.isVoided,
                    originalId = d.id
                )
            )
        }

        // Bazar
        bazars.forEach { b ->
            val buyerName = memberMap[b.buyerMemberId]?.name ?: "Member #${b.buyerMemberId}"
            list.add(
                TransactionItem(
                    id = b.id,
                    type = TransactionType.BAZAR,
                    title = "Bazar: ${b.itemsSummary}",
                    description = "Buyer: $buyerName • Category: ${b.category}",
                    amount = b.totalAmount,
                    isIncome = false,
                    date = b.date,
                    memberName = buyerName,
                    isVoided = b.isVoided,
                    originalId = b.id
                )
            )
        }

        // Expenses
        expenses.forEach { e ->
            val type = when (e.category) {
                "RENT" -> TransactionType.RENT
                "ELECTRICITY", "GAS", "WATER", "INTERNET", "CLEANING" -> TransactionType.UTILITY
                else -> TransactionType.OTHER_EXPENSE
            }
            list.add(
                TransactionItem(
                    id = e.id,
                    type = type,
                    title = "${e.category}: ${e.title}",
                    description = "Status: ${e.status} • ${e.note}",
                    amount = e.amount,
                    isIncome = false,
                    date = e.date,
                    memberName = null,
                    isVoided = e.isVoided,
                    originalId = e.id
                )
            )
        }

        list.sortedByDescending { it.date }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setMonth(month: String) {
        _currentMonth.value = month
    }

    fun setRole(role: CurrentRole, memberId: Long = 2L) {
        _currentRole.value = role
        _currentMemberId.value = memberId
    }

    // Auth & Session
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun loginAsManager(phone: String, pin: String): Boolean {
        _currentRole.value = CurrentRole.ADMIN
        _isLoggedIn.value = true
        return true
    }

    fun loginAsMember(memberId: Long, pin: String): Boolean {
        _currentRole.value = CurrentRole.MEMBER
        _currentMemberId.value = memberId
        _isLoggedIn.value = true
        return true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun createMessAccount(
        messName: String,
        managerName: String,
        phone: String,
        pin: String
    ) = viewModelScope.launch {
        val newProfile = MessProfile(
            id = 1,
            messName = messName,
            managerName = managerName,
            managerPhone = phone,
            activeMonth = _currentMonth.value
        )
        repository.updateMessProfile(newProfile)

        val managerMember = Member(
            id = 1L,
            name = managerName,
            phone = phone,
            roomNumber = "101",
            bedNumber = "Manager Bed",
            role = "ADMIN",
            pin = pin,
            status = "ACTIVE"
        )
        repository.addMember(managerMember)

        _currentRole.value = CurrentRole.ADMIN
        _currentMemberId.value = 1L
        _isLoggedIn.value = true
    }

    // Calculations helper (Clean Pure Mess System: No rent, no utilities)
    private fun calculateMessSummary(
        allM: List<Member>,
        activeM: List<Member>,
        meals: List<MealEntry>,
        bazars: List<BazarEntry>,
        expenses: List<ExpenseEntry>,
        deposits: List<DepositEntry>
    ): MessSummary {
        val totalFoodExpense = bazars.filter { !it.isVoided }.sumOf { it.totalAmount }
        val totalMealCount = meals.sumOf { it.totalMeals }
        val todayMeals = meals.filter { it.date == todayDateString }.sumOf { it.totalMeals }
        val mealRate = if (totalMealCount > 0) totalFoodExpense / totalMealCount else 0.0

        val totalDep = deposits.filter { !it.isVoided }.sumOf { it.amount }
        val messBalance = totalDep - totalFoodExpense

        // Calculate member statements to get accurate total due and total advance
        val statements = calculateMemberStatements(allM, activeM, meals, bazars, expenses, deposits)
        val totalDue = statements.filter { it.isDue }.sumOf { it.dueAmount }
        val totalAdvance = statements.filter { !it.isDue }.sumOf { it.advanceAmount }

        return MessSummary(
            totalMembers = allM.size,
            activeMembers = activeM.size,
            todayMealCount = todayMeals,
            monthlyMealCount = totalMealCount,
            monthlyBazar = totalFoodExpense,
            mealRate = mealRate,
            houseRent = 0.0,
            electricityBill = 0.0,
            gasBill = 0.0,
            waterBill = 0.0,
            internetBill = 0.0,
            otherBills = 0.0,
            totalUtilityBills = 0.0,
            otherExpenses = 0.0,
            totalExpense = totalFoodExpense,
            totalDeposits = totalDep,
            currentMessBalance = messBalance,
            totalDue = totalDue,
            totalAdvance = totalAdvance
        )
    }

    private fun calculateMemberStatements(
        allM: List<Member>,
        activeM: List<Member>,
        meals: List<MealEntry>,
        bazars: List<BazarEntry>,
        expenses: List<ExpenseEntry>,
        deposits: List<DepositEntry>
    ): List<MemberStatement> {
        val totalFoodExpense = bazars.filter { !it.isVoided }.sumOf { it.totalAmount }
        val totalMealCount = meals.sumOf { it.totalMeals }
        val mealRate = if (totalMealCount > 0) totalFoodExpense / totalMealCount else 0.0

        val mealsByMember = meals.groupBy { it.memberId }
        val depositsByMember = deposits.filter { !it.isVoided }.groupBy { it.memberId }

        return allM.map { member ->
            val memberMeals = mealsByMember[member.id] ?: emptyList()
            val memberDeposits = depositsByMember[member.id] ?: emptyList()

            val mealCount = memberMeals.sumOf { it.totalMeals }
            val mealCost = mealCount * mealRate
            val totalCost = mealCost // Pure Food/Meal cost!

            val totalPaid = memberDeposits.sumOf { it.amount } + member.initialBalance
            val netBalance = totalPaid - totalCost

            MemberStatement(
                member = member,
                mealCount = mealCount,
                mealCost = mealCost,
                rentShare = 0.0,
                utilityShare = 0.0,
                otherShare = 0.0,
                totalCost = totalCost,
                totalPaid = totalPaid,
                netBalance = netBalance,
                deposits = memberDeposits,
                mealEntries = memberMeals
            )
        }
    }

    // --- Member Actions ---
    fun addMember(
        name: String,
        phone: String,
        email: String,
        roomNumber: String,
        bedNumber: String,
        initialBalance: Double,
        customRent: Double,
        notes: String
    ) = viewModelScope.launch {
        val member = Member(
            name = name,
            phone = phone,
            email = email,
            roomNumber = roomNumber,
            bedNumber = bedNumber,
            initialBalance = initialBalance,
            customRent = customRent,
            notes = notes
        )
        repository.addMember(member)
    }

    fun updateMember(member: Member) = viewModelScope.launch {
        repository.updateMember(member)
    }

    fun toggleMemberStatus(member: Member) = viewModelScope.launch {
        val newStatus = if (member.status == "ACTIVE") "INACTIVE" else "ACTIVE"
        repository.updateMember(member.copy(status = newStatus))
    }

    // --- Meals Actions ---
    fun saveDailyMeals(entries: List<MealEntry>) = viewModelScope.launch {
        repository.saveMealEntries(entries)
    }

    // --- Bazar Actions ---
    fun addBazar(
        buyerMemberId: Long,
        itemsSummary: String,
        amount: Double,
        category: String,
        date: String,
        note: String
    ) = viewModelScope.launch {
        val entry = BazarEntry(
            date = date,
            month = _currentMonth.value,
            buyerMemberId = buyerMemberId,
            itemsSummary = itemsSummary,
            totalAmount = amount,
            category = category,
            note = note
        )
        repository.addBazar(entry)
    }

    fun toggleVoidBazar(id: Long, currentVoid: Boolean) = viewModelScope.launch {
        repository.setBazarVoided(id, !currentVoid)
    }

    fun updateBazar(bazar: BazarEntry) = viewModelScope.launch {
        repository.updateBazar(bazar)
    }

    fun deleteBazar(bazar: BazarEntry) = viewModelScope.launch {
        repository.deleteBazar(bazar)
    }

    // --- Expense Actions ---
    fun addExpense(
        title: String,
        category: String,
        amount: Double,
        date: String,
        status: String,
        note: String
    ) = viewModelScope.launch {
        val expense = ExpenseEntry(
            title = title,
            category = category,
            amount = amount,
            date = date,
            month = _currentMonth.value,
            status = status,
            note = note
        )
        repository.addExpense(expense)
    }

    fun toggleVoidExpense(id: Long, currentVoid: Boolean) = viewModelScope.launch {
        repository.setExpenseVoided(id, !currentVoid)
    }

    // --- Deposit Actions ---
    fun addCashDeposit(
        memberId: Long,
        amount: Double,
        date: String,
        note: String
    ) = viewModelScope.launch {
        val member = allMembers.value.find { it.id == memberId }
        val memberName = member?.name ?: "Member #$memberId"
        val deposit = DepositEntry(
            memberId = memberId,
            amount = amount,
            date = date,
            month = _currentMonth.value,
            paymentType = "CASH",
            note = note
        )
        repository.addCashDeposit(deposit, memberName)
    }

    fun toggleVoidDeposit(id: Long, currentVoid: Boolean) = viewModelScope.launch {
        repository.setDepositVoided(id, !currentVoid)
    }

    fun updateCashDeposit(deposit: DepositEntry) = viewModelScope.launch {
        repository.updateDeposit(deposit)
    }

    fun deleteCashDeposit(deposit: DepositEntry) = viewModelScope.launch {
        repository.deleteDeposit(deposit)
    }

    fun deleteMember(member: Member) = viewModelScope.launch {
        repository.deleteMember(member)
    }

    // Member self-service meal recording
    fun saveMemberSelfMeals(
        memberId: Long,
        date: String,
        breakfast: Double,
        lunch: Double,
        dinner: Double,
        guest: Double
    ) = viewModelScope.launch {
        val entry = MealEntry(
            date = date,
            month = _currentMonth.value,
            memberId = memberId,
            breakfast = breakfast,
            lunch = lunch,
            dinner = dinner,
            guestMeals = guest,
            note = "Member self-recorded"
        )
        repository.saveMealEntry(entry)
    }

    // --- Notifications & Messaging ---
    fun broadcastNotice(title: String, message: String, type: String) = viewModelScope.launch {
        val notice = NotificationNotice(
            title = title,
            message = message,
            type = type,
            timestamp = System.currentTimeMillis()
        )
        repository.addNotification(notice)
    }

    fun sendDirectMessage(receiverId: Long, messageText: String, senderName: String) = viewModelScope.launch {
        val senderId = if (_currentRole.value == CurrentRole.ADMIN) 0L else _currentMemberId.value
        val msg = InAppMessage(
            senderId = senderId,
            receiverId = receiverId,
            senderName = senderName,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )
        repository.sendMessage(msg)
    }

    fun updateProfile(profile: MessProfile) = viewModelScope.launch {
        repository.updateMessProfile(profile)
    }

    // Generate Shareable / Exportable Monthly Statement Text
    fun generateMonthlyReportText(): String {
        val summary = messSummary.value
        val statements = memberStatements.value
        val profile = messProfile.value

        val sb = StringBuilder()
        sb.append("=========================================\n")
        sb.append("🏢 ${profile?.messName ?: "Mess Report"}\n")
        sb.append("📍 ${profile?.messAddress ?: ""}\n")
        sb.append("👤 Manager: ${profile?.managerName ?: ""} (${profile?.managerPhone ?: ""})\n")
        sb.append("📅 Month: ${_currentMonth.value}\n")
        sb.append("=========================================\n\n")

        sb.append("📊 MESS OVERVIEW:\n")
        sb.append("• Total Members: ${summary.totalMembers} (Active: ${summary.activeMembers})\n")
        sb.append("• Total Meals: ${String.format(Locale.US, "%.1f", summary.monthlyMealCount)}\n")
        sb.append("• Monthly Bazar: ৳${String.format(Locale.US, "%.2f", summary.monthlyBazar)}\n")
        sb.append("• Calculated Meal Rate: ৳${String.format(Locale.US, "%.2f", summary.mealRate)}\n")
        sb.append("• House Rent: ৳${String.format(Locale.US, "%.2f", summary.houseRent)}\n")
        sb.append("• Total Utility Bills: ৳${String.format(Locale.US, "%.2f", summary.totalUtilityBills)}\n")
        sb.append("• Other Expenses: ৳${String.format(Locale.US, "%.2f", summary.otherExpenses)}\n")
        sb.append("• Total Mess Expense: ৳${String.format(Locale.US, "%.2f", summary.totalExpense)}\n")
        sb.append("• Total Cash Deposits: ৳${String.format(Locale.US, "%.2f", summary.totalDeposits)}\n")
        sb.append("• Cash Balance in Hand: ৳${String.format(Locale.US, "%.2f", summary.currentMessBalance)}\n")
        sb.append("• Total Pending Dues: ৳${String.format(Locale.US, "%.2f", summary.totalDue)}\n\n")

        sb.append("👥 INDIVIDUAL MEMBER STATEMENTS:\n")
        sb.append("-----------------------------------------\n")
        statements.forEachIndexed { i, s ->
            sb.append("${i + 1}. ${s.member.name} (Room ${s.member.roomNumber})\n")
            sb.append("   Meals: ${String.format(Locale.US, "%.1f", s.mealCount)} x ৳${String.format(Locale.US, "%.2f", summary.mealRate)} = ৳${String.format(Locale.US, "%.2f", s.mealCost)}\n")
            sb.append("   Rent: ৳${String.format(Locale.US, "%.2f", s.rentShare)} | Bills: ৳${String.format(Locale.US, "%.2f", s.utilityShare)} | Other: ৳${String.format(Locale.US, "%.2f", s.otherShare)}\n")
            sb.append("   Total Cost: ৳${String.format(Locale.US, "%.2f", s.totalCost)} | Paid: ৳${String.format(Locale.US, "%.2f", s.totalPaid)}\n")
            if (s.isDue) {
                sb.append("   STATUS: 🔴 DUE ৳${String.format(Locale.US, "%.2f", s.dueAmount)}\n\n")
            } else {
                sb.append("   STATUS: 🟢 ADVANCE ৳${String.format(Locale.US, "%.2f", s.advanceAmount)}\n\n")
            }
        }
        sb.append("=========================================\n")
        sb.append("Report Generated by Smart Mess Manager\n")
        return sb.toString()
    }
}
