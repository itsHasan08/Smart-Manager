package com.example.data.model

data class MessSummary(
    val totalMembers: Int = 0,
    val activeMembers: Int = 0,
    val todayMealCount: Double = 0.0,
    val monthlyMealCount: Double = 0.0,
    val monthlyBazar: Double = 0.0,
    val mealRate: Double = 0.0,
    val houseRent: Double = 0.0,
    val electricityBill: Double = 0.0,
    val gasBill: Double = 0.0,
    val waterBill: Double = 0.0,
    val internetBill: Double = 0.0,
    val otherBills: Double = 0.0,
    val totalUtilityBills: Double = 0.0,
    val otherExpenses: Double = 0.0,
    val totalExpense: Double = 0.0,
    val totalDeposits: Double = 0.0,
    val currentMessBalance: Double = 0.0,
    val totalDue: Double = 0.0,
    val totalAdvance: Double = 0.0
)

data class MemberStatement(
    val member: Member,
    val mealCount: Double = 0.0,
    val mealCost: Double = 0.0,
    val rentShare: Double = 0.0,
    val utilityShare: Double = 0.0,
    val otherShare: Double = 0.0,
    val totalCost: Double = 0.0,
    val totalPaid: Double = 0.0,
    val netBalance: Double = 0.0, // positive = advance, negative = due
    val deposits: List<DepositEntry> = emptyList(),
    val mealEntries: List<MealEntry> = emptyList()
) {
    val isDue: Boolean get() = netBalance < -0.01
    val dueAmount: Double get() = if (isDue) -netBalance else 0.0
    val advanceAmount: Double get() = if (netBalance > 0.01) netBalance else 0.0
}

enum class TransactionType {
    DEPOSIT,
    BAZAR,
    RENT,
    UTILITY,
    OTHER_EXPENSE
}

data class TransactionItem(
    val id: Long,
    val type: TransactionType,
    val title: String,
    val description: String,
    val amount: Double,
    val isIncome: Boolean,
    val date: String,
    val memberName: String? = null,
    val isVoided: Boolean = false,
    val originalId: Long
)
