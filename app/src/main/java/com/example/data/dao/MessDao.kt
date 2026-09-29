package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessDao {

    // --- Mess Profile ---
    @Query("SELECT * FROM mess_profile WHERE id = 1 LIMIT 1")
    fun getMessProfile(): Flow<MessProfile?>

    @Query("SELECT * FROM mess_profile WHERE id = 1 LIMIT 1")
    suspend fun getMessProfileSync(): MessProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMessProfile(profile: MessProfile)

    // --- Members ---
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE status = 'ACTIVE' ORDER BY name ASC")
    fun getActiveMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    fun getMemberById(id: Long): Flow<Member?>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberByIdSync(id: Long): Member?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    // --- Meals ---
    @Query("SELECT * FROM meal_entries WHERE month = :month ORDER BY date DESC")
    fun getMealsByMonth(month: String): Flow<List<MealEntry>>

    @Query("SELECT * FROM meal_entries WHERE date = :date")
    fun getMealsByDate(date: String): Flow<List<MealEntry>>

    @Query("SELECT * FROM meal_entries WHERE memberId = :memberId AND month = :month ORDER BY date DESC")
    fun getMealsByMemberAndMonth(memberId: Long, month: String): Flow<List<MealEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealEntry(meal: MealEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealEntries(meals: List<MealEntry>)

    @Update
    suspend fun updateMealEntry(meal: MealEntry)

    @Delete
    suspend fun deleteMealEntry(meal: MealEntry)

    // --- Bazar ---
    @Query("SELECT * FROM bazar_entries WHERE month = :month ORDER BY date DESC, id DESC")
    fun getBazarByMonth(month: String): Flow<List<BazarEntry>>

    @Query("SELECT * FROM bazar_entries ORDER BY date DESC, id DESC")
    fun getAllBazar(): Flow<List<BazarEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBazarEntry(bazar: BazarEntry): Long

    @Update
    suspend fun updateBazarEntry(bazar: BazarEntry)

    @Query("UPDATE bazar_entries SET isVoided = :isVoided WHERE id = :id")
    suspend fun setBazarVoided(id: Long, isVoided: Boolean)

    @Delete
    suspend fun deleteBazarEntry(bazar: BazarEntry)

    // --- Expenses (Rent, Utility, Other) ---
    @Query("SELECT * FROM expenses WHERE month = :month ORDER BY date DESC, id DESC")
    fun getExpensesByMonth(month: String): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseEntry(expense: ExpenseEntry): Long

    @Update
    suspend fun updateExpenseEntry(expense: ExpenseEntry)

    @Query("UPDATE expenses SET isVoided = :isVoided WHERE id = :id")
    suspend fun setExpenseVoided(id: Long, isVoided: Boolean)

    @Delete
    suspend fun deleteExpenseEntry(expense: ExpenseEntry)

    // --- Deposits ---
    @Query("SELECT * FROM deposits WHERE month = :month ORDER BY date DESC, id DESC")
    fun getDepositsByMonth(month: String): Flow<List<DepositEntry>>

    @Query("SELECT * FROM deposits ORDER BY date DESC, id DESC")
    fun getAllDeposits(): Flow<List<DepositEntry>>

    @Query("SELECT * FROM deposits WHERE memberId = :memberId AND month = :month ORDER BY date DESC")
    fun getDepositsByMemberAndMonth(memberId: Long, month: String): Flow<List<DepositEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepositEntry(deposit: DepositEntry): Long

    @Update
    suspend fun updateDepositEntry(deposit: DepositEntry)

    @Query("UPDATE deposits SET isVoided = :isVoided WHERE id = :id")
    suspend fun setDepositVoided(id: Long, isVoided: Boolean)

    @Delete
    suspend fun deleteDepositEntry(deposit: DepositEntry)

    // --- Notifications & Announcements ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationNotice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationNotice): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Delete
    suspend fun deleteNotification(notification: NotificationNotice)

    // --- In-App Messages ---
    @Query("SELECT * FROM in_app_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<InAppMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: InAppMessage): Long

    @Query("UPDATE in_app_messages SET isRead = 1 WHERE receiverId = :userId")
    suspend fun markMessagesAsReadForUser(userId: Long)

    // --- Activity Logs ---
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 100")
    fun getActivityLogs(): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLog): Long
}
