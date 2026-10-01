package com.example.data.repository

import com.example.data.dao.MessDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class MessRepository(private val dao: MessDao) {

    val messProfile: Flow<MessProfile?> = dao.getMessProfile()
    val allMembers: Flow<List<Member>> = dao.getAllMembers()
    val activeMembers: Flow<List<Member>> = dao.getActiveMembers()
    val notifications: Flow<List<NotificationNotice>> = dao.getAllNotifications()
    val messages: Flow<List<InAppMessage>> = dao.getAllMessages()
    val activityLogs: Flow<List<ActivityLog>> = dao.getActivityLogs()

    suspend fun getProfileSync(): MessProfile? = dao.getMessProfileSync()

    suspend fun updateMessProfile(profile: MessProfile) {
        dao.insertOrUpdateMessProfile(profile)
        logActivity("Settings Updated", "Mess details or monthly settings were updated", "Admin")
    }

    // Members
    fun getMemberById(id: Long): Flow<Member?> = dao.getMemberById(id)

    suspend fun addMember(member: Member): Long {
        val id = dao.insertMember(member)
        logActivity("Member Added", "New member '${member.name}' added to room ${member.roomNumber}", "Admin")
        return id
    }

    suspend fun updateMember(member: Member) {
        dao.updateMember(member)
        logActivity("Member Updated", "Updated information for '${member.name}'", "Admin")
    }

    suspend fun deleteMember(member: Member) {
        dao.deleteMember(member)
        logActivity("Member Removed", "Removed member '${member.name}' from mess records", "Admin")
    }

    // Meals
    fun getMealsByMonth(month: String): Flow<List<MealEntry>> = dao.getMealsByMonth(month)
    fun getMealsByDate(date: String): Flow<List<MealEntry>> = dao.getMealsByDate(date)
    fun getMealsByMemberAndMonth(memberId: Long, month: String): Flow<List<MealEntry>> = dao.getMealsByMemberAndMonth(memberId, month)

    suspend fun saveMealEntry(meal: MealEntry) {
        dao.insertMealEntry(meal)
    }

    suspend fun saveMealEntries(meals: List<MealEntry>) {
        dao.insertMealEntries(meals)
        logActivity("Meals Recorded", "Updated meal sheet for ${meals.size} members", "Admin")
    }

    // Bazar
    fun getBazarByMonth(month: String): Flow<List<BazarEntry>> = dao.getBazarByMonth(month)

    suspend fun addBazar(bazar: BazarEntry): Long {
        val id = dao.insertBazarEntry(bazar)
        logActivity("Bazar Entry Added", "Added grocery cost of ৳${bazar.totalAmount} (${bazar.itemsSummary})", "Admin")
        return id
    }

    suspend fun updateBazar(bazar: BazarEntry) {
        dao.updateBazarEntry(bazar)
        logActivity("Bazar Updated", "Updated bazar entry #${bazar.id} (৳${bazar.totalAmount})", "Admin")
    }

    suspend fun deleteBazar(bazar: BazarEntry) {
        dao.deleteBazarEntry(bazar)
        logActivity("Bazar Deleted", "Deleted bazar entry #${bazar.id} (৳${bazar.totalAmount})", "Admin")
    }

    suspend fun setBazarVoided(id: Long, isVoided: Boolean) {
        dao.setBazarVoided(id, isVoided)
        val statusStr = if (isVoided) "Voided" else "Reinstated"
        logActivity("Bazar $statusStr", "Bazar transaction #$id was $statusStr", "Admin")
    }

    // Expenses (Rent, Utility, Other)
    fun getExpensesByMonth(month: String): Flow<List<ExpenseEntry>> = dao.getExpensesByMonth(month)

    suspend fun addExpense(expense: ExpenseEntry): Long {
        val id = dao.insertExpenseEntry(expense)
        logActivity("Expense Added", "Added ${expense.category}: ${expense.title} for ৳${expense.amount}", "Admin")
        return id
    }

    suspend fun setExpenseVoided(id: Long, isVoided: Boolean) {
        dao.setExpenseVoided(id, isVoided)
        val statusStr = if (isVoided) "Voided" else "Reinstated"
        logActivity("Expense $statusStr", "Expense #$id was $statusStr", "Admin")
    }

    // Deposits (Cash)
    fun getDepositsByMonth(month: String): Flow<List<DepositEntry>> = dao.getDepositsByMonth(month)
    fun getDepositsByMemberAndMonth(memberId: Long, month: String): Flow<List<DepositEntry>> = dao.getDepositsByMemberAndMonth(memberId, month)

    suspend fun addCashDeposit(deposit: DepositEntry, memberName: String): Long {
        val id = dao.insertDepositEntry(deposit)
        logActivity("Cash Deposit", "Received ৳${deposit.amount} cash from $memberName (${deposit.note})", "Admin")
        return id
    }

    suspend fun updateDeposit(deposit: DepositEntry) {
        dao.updateDepositEntry(deposit)
        logActivity("Deposit Updated", "Updated deposit #${deposit.id} (৳${deposit.amount})", "Admin")
    }

    suspend fun deleteDeposit(deposit: DepositEntry) {
        dao.deleteDepositEntry(deposit)
        logActivity("Deposit Deleted", "Deleted deposit #${deposit.id} (৳${deposit.amount})", "Admin")
    }

    suspend fun setDepositVoided(id: Long, isVoided: Boolean) {
        dao.setDepositVoided(id, isVoided)
        val statusStr = if (isVoided) "Voided" else "Reinstated"
        logActivity("Deposit $statusStr", "Deposit transaction #$id was $statusStr", "Admin")
    }

    // Notices & Messaging
    suspend fun addNotification(notice: NotificationNotice) {
        dao.insertNotification(notice)
        logActivity("Notice Broadcast", "Notice sent: '${notice.title}'", "Admin")
    }

    suspend fun markNotificationAsRead(id: Long) {
        dao.markNotificationAsRead(id)
    }

    suspend fun sendMessage(message: InAppMessage) {
        dao.insertMessage(message)
    }

    suspend fun markMessagesAsReadForUser(userId: Long) {
        dao.markMessagesAsReadForUser(userId)
    }

    // Activity Log
    private suspend fun logActivity(action: String, details: String, performedBy: String) {
        dao.insertActivityLog(
            ActivityLog(action = action, details = details, performedBy = performedBy)
        )
    }
}
