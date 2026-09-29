package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.MessDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MessProfile::class,
        Member::class,
        MealEntry::class,
        BazarEntry::class,
        ExpenseEntry::class,
        DepositEntry::class,
        NotificationNotice::class,
        InAppMessage::class,
        ActivityLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun messDao(): MessDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_mess_manager.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.messDao())
                }
            }
        }

        suspend fun populateInitialData(dao: MessDao) {
            // 1. Mess Profile
            val defaultProfile = MessProfile(
                id = 1,
                messName = "Green Valley Bachelor Mess",
                messAddress = "House #42, Road #7, Sector 4, Uttara, Dhaka",
                managerName = "Md. Tareq Rahman (Manager)",
                managerPhone = "01712-345678",
                currencySymbol = "৳",
                monthStartDate = 1,
                activeMonth = "2026-09",
                noticePin = "📢 All members are requested to submit cash deposit by the 5th of each month."
            )
            dao.insertOrUpdateMessProfile(defaultProfile)

            // 2. Members (10 active members)
            val members = listOf(
                Member(id = 1, name = "Md. Tareq Rahman", phone = "01712345678", email = "tareq@example.com", roomNumber = "101", bedNumber = "Bed-1", role = "ADMIN", status = "ACTIVE"),
                Member(id = 2, name = "Rahim Uddin", phone = "01811223344", email = "rahim@example.com", roomNumber = "101", bedNumber = "Bed-2", role = "MEMBER", status = "ACTIVE"),
                Member(id = 3, name = "Hasan Mahmud", phone = "01911223344", email = "hasan@example.com", roomNumber = "102", bedNumber = "Bed-1", role = "MEMBER", status = "ACTIVE"),
                Member(id = 4, name = "Tanvir Ahmed", phone = "01722334455", email = "tanvir@example.com", roomNumber = "102", bedNumber = "Bed-2", role = "MEMBER", status = "ACTIVE"),
                Member(id = 5, name = "Sakib Al Hasan", phone = "01633445566", email = "sakib@example.com", roomNumber = "103", bedNumber = "Bed-1", role = "MEMBER", status = "ACTIVE"),
                Member(id = 6, name = "Arif Hossain", phone = "01544556677", email = "arif@example.com", roomNumber = "103", bedNumber = "Bed-2", role = "MEMBER", status = "ACTIVE"),
                Member(id = 7, name = "Mehedi Hasan", phone = "01855667788", email = "mehedi@example.com", roomNumber = "201", bedNumber = "Bed-1", role = "MEMBER", status = "ACTIVE"),
                Member(id = 8, name = "Faisal Karim", phone = "01766778899", email = "faisal@example.com", roomNumber = "201", bedNumber = "Bed-2", role = "MEMBER", status = "ACTIVE"),
                Member(id = 9, name = "Zubair Islam", phone = "01977889900", email = "zubair@example.com", roomNumber = "202", bedNumber = "Bed-1", role = "MEMBER", status = "ACTIVE"),
                Member(id = 10, name = "Kamrul Hasan", phone = "01688990011", email = "kamrul@example.com", roomNumber = "202", bedNumber = "Bed-2", role = "MEMBER", status = "ACTIVE")
            )
            for (m in members) {
                dao.insertMember(m)
            }

            // 3. Meals across September 2026 for members
            val currentMonth = "2026-09"
            val sampleDays = listOf("2026-09-25", "2026-09-26", "2026-09-27", "2026-09-28", "2026-09-29")
            val mealList = mutableListOf<MealEntry>()
            for (day in sampleDays) {
                for (m in members) {
                    val bf = if (m.id % 2L == 0L) 1.0 else 0.0
                    val ln = 1.0
                    val dn = 1.0
                    mealList.add(
                        MealEntry(
                            date = day,
                            month = currentMonth,
                            memberId = m.id,
                            breakfast = bf,
                            lunch = ln,
                            dinner = dn,
                            guestMeals = if (day == "2026-09-26" && m.id == 2L) 1.0 else 0.0
                        )
                    )
                }
            }
            dao.insertMealEntries(mealList)

            // 4. Bazar Entries (Cash purchases for food)
            val bazarEntries = listOf(
                BazarEntry(date = "2026-09-02", month = currentMonth, buyerMemberId = 2, itemsSummary = "Miniket Rice 25kg, Soybean Oil 5L, Lentils (Dal)", totalAmount = 3850.0, category = "GROCERY"),
                BazarEntry(date = "2026-09-06", month = currentMonth, buyerMemberId = 3, itemsSummary = "Fresh Rui Fish 3.5kg, Hilsha 2pcs", totalAmount = 2950.0, category = "MEAT_FISH"),
                BazarEntry(date = "2026-09-11", month = currentMonth, buyerMemberId = 4, itemsSummary = "Broiler Chicken 5kg, Beef 2kg, Onions & Garlic", totalAmount = 3700.0, category = "MEAT_FISH"),
                BazarEntry(date = "2026-09-16", month = currentMonth, buyerMemberId = 5, itemsSummary = "Potatoes 10kg, Seasonal Veggies, Eggs 90pcs", totalAmount = 1950.0, category = "VEGETABLES"),
                BazarEntry(date = "2026-09-21", month = currentMonth, buyerMemberId = 6, itemsSummary = "Spices, Mustard Oil, Tea leaves, Sugar", totalAmount = 1450.0, category = "SPICES"),
                BazarEntry(date = "2026-09-25", month = currentMonth, buyerMemberId = 7, itemsSummary = "Chinigura Rice, Chicken, Special Friday Spices", totalAmount = 2800.0, category = "GROCERY"),
                BazarEntry(date = "2026-09-29", month = currentMonth, buyerMemberId = 8, itemsSummary = "Fish, Vegetables, Milk & Eggs 60pcs", totalAmount = 1750.0, category = "GROCERY")
            )
            for (b in bazarEntries) {
                dao.insertBazarEntry(b)
            }

            // 5. Fixed Expenses & Utility Bills
            val expenses = listOf(
                ExpenseEntry(title = "Flat Rent (September)", category = "RENT", amount = 25000.0, date = "2026-09-05", month = currentMonth, status = "PAID", note = "Paid directly to landlord"),
                ExpenseEntry(title = "DESCO Electricity Bill", category = "ELECTRICITY", amount = 3450.0, date = "2026-09-10", month = currentMonth, status = "PAID", note = "Prepaid meter recharge"),
                ExpenseEntry(title = "Titas Gas Bill", category = "GAS", amount = 1080.0, date = "2026-09-12", month = currentMonth, status = "PAID"),
                ExpenseEntry(title = "WASA Water Bill", category = "WATER", amount = 950.0, date = "2026-09-14", month = currentMonth, status = "PAID"),
                ExpenseEntry(title = "Optical Fiber Broadband (50Mbps)", category = "INTERNET", amount = 1200.0, date = "2026-09-07", month = currentMonth, status = "PAID"),
                ExpenseEntry(title = "Bua & Cleaning Maid Allowance", category = "CLEANING", amount = 3500.0, date = "2026-09-08", month = currentMonth, status = "PAID"),
                ExpenseEntry(title = "Kitchen Filter Cartridge Replacement", category = "MAINTENANCE", amount = 650.0, date = "2026-09-18", month = currentMonth, status = "PAID")
            )
            for (e in expenses) {
                dao.insertExpenseEntry(e)
            }

            // 6. Cash Deposits (Handed to Manager in cash, manually entered)
            val deposits = listOf(
                DepositEntry(memberId = 1, amount = 5500.0, date = "2026-09-02", month = currentMonth, paymentType = "CASH", note = "September cash deposit"),
                DepositEntry(memberId = 2, amount = 5000.0, date = "2026-09-03", month = currentMonth, paymentType = "CASH", note = "Handed 5k cash to Tareq bhai"),
                DepositEntry(memberId = 3, amount = 5000.0, date = "2026-09-03", month = currentMonth, paymentType = "CASH", note = "Cash deposit"),
                DepositEntry(memberId = 4, amount = 4500.0, date = "2026-09-04", month = currentMonth, paymentType = "CASH", note = "First installment cash"),
                DepositEntry(memberId = 5, amount = 6000.0, date = "2026-09-04", month = currentMonth, paymentType = "CASH", note = "Cash payment for Sept"),
                DepositEntry(memberId = 6, amount = 5000.0, date = "2026-09-05", month = currentMonth, paymentType = "CASH", note = "Cash received"),
                DepositEntry(memberId = 7, amount = 3500.0, date = "2026-09-05", month = currentMonth, paymentType = "CASH", note = "Cash advance"),
                DepositEntry(memberId = 8, amount = 5000.0, date = "2026-09-05", month = currentMonth, paymentType = "CASH", note = "Cash deposit"),
                DepositEntry(memberId = 9, amount = 5500.0, date = "2026-09-06", month = currentMonth, paymentType = "CASH", note = "September full deposit"),
                DepositEntry(memberId = 10, amount = 4000.0, date = "2026-09-07", month = currentMonth, paymentType = "CASH", note = "Cash deposit")
            )
            for (d in deposits) {
                dao.insertDepositEntry(d)
            }

            // 7. Announcements / Notices
            val notices = listOf(
                NotificationNotice(
                    title = "📢 Special Friday Feast Announcement",
                    message = "This Friday we are having special Chicken Khichuri and salad. Please update your lunch count by Thursday night!",
                    type = "ANNOUNCEMENT",
                    timestamp = System.currentTimeMillis() - 86400000L * 2
                ),
                NotificationNotice(
                    title = "⚠️ September Due Clearance Reminder",
                    message = "Mess monthly settlement is approaching. Members with pending balances are requested to clear dues with Tareq Bhai.",
                    type = "DUE_REMINDER",
                    timestamp = System.currentTimeMillis() - 86400000L
                ),
                NotificationNotice(
                    title = "💡 Electricity Energy Saving",
                    message = "Please remember to switch off fans and lights when leaving the rooms.",
                    type = "NOTICE",
                    timestamp = System.currentTimeMillis() - 3600000L * 6
                )
            )
            for (n in notices) {
                dao.insertNotification(n)
            }

            // 8. In-App Messages
            val messages = listOf(
                InAppMessage(senderId = 1, receiverId = 2, senderName = "Tareq (Admin)", message = "Rahim, please remember to update today's dinner meal count before 7 PM.", timestamp = System.currentTimeMillis() - 3600000L * 12),
                InAppMessage(senderId = 2, receiverId = 1, senderName = "Rahim Uddin", message = "Yes Tareq bhai, I have updated it and I will do the weekend grocery tomorrow.", timestamp = System.currentTimeMillis() - 3600000L * 10),
                InAppMessage(senderId = 1, receiverId = 7, senderName = "Tareq (Admin)", message = "Mehedi, your remaining due for this month is ৳1,350. Please hand it over when convenient.", timestamp = System.currentTimeMillis() - 3600000L * 4),
                InAppMessage(senderId = 7, receiverId = 1, senderName = "Mehedi Hasan", message = "Noted Tareq bhai, I will pay in cash tomorrow evening after office.", timestamp = System.currentTimeMillis() - 3600000L * 2)
            )
            for (msg in messages) {
                dao.insertMessage(msg)
            }

            // 9. Activity Logs
            val logs = listOf(
                ActivityLog(action = "Deposit Entry", details = "Admin recorded cash deposit ৳5,000 from Rahim Uddin", performedBy = "Tareq (Manager)"),
                ActivityLog(action = "Bazar Entry", details = "Admin added grocery market bill ৳3,850 bought by Rahim", performedBy = "Tareq (Manager)"),
                ActivityLog(action = "Rent Payment", details = "Flat Rent ৳25,000 paid to house owner", performedBy = "Tareq (Manager)"),
                ActivityLog(action = "Utility Bill", details = "Electricity bill ৳3,450 paid to DESCO", performedBy = "Tareq (Manager)"),
                ActivityLog(action = "Notice Broadcast", details = "Sent announcement for Friday feast to all members", performedBy = "Tareq (Manager)")
            )
            for (l in logs) {
                dao.insertActivityLog(l)
            }
        }
    }
}
