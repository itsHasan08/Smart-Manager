package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mess_profile")
data class MessProfile(
    @PrimaryKey val id: Int = 1,
    val messName: String = "Green Valley Bachelor Mess",
    val messAddress: String = "House #42, Road #7, Sector 4, Uttara, Dhaka",
    val managerName: String = "Md. Tareq Rahman",
    val managerPhone: String = "01712-345678",
    val currencySymbol: String = "৳",
    val monthStartDate: Int = 1,
    val activeMonth: String = "2026-09",
    val noticePin: String = "Reminder: Please clear all dues before the 5th of next month."
)

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val roomNumber: String = "101",
    val bedNumber: String = "Bed-1",
    val role: String = "MEMBER", // "ADMIN" or "MEMBER"
    val pin: String = "1234",
    val status: String = "ACTIVE", // "ACTIVE", "INACTIVE", "LEFT"
    val joiningDate: Long = System.currentTimeMillis(),
    val initialBalance: Double = 0.0, // positive = advance, negative = due
    val customRent: Double = 0.0, // 0.0 means equal split
    val notes: String = ""
)

@Entity(tableName = "meal_entries")
data class MealEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val month: String, // "YYYY-MM"
    val memberId: Long,
    val breakfast: Double = 0.0,
    val lunch: Double = 1.0,
    val dinner: Double = 1.0,
    val guestMeals: Double = 0.0,
    val note: String = ""
) {
    val totalMeals: Double
        get() = breakfast + lunch + dinner + guestMeals
}

@Entity(tableName = "bazar_entries")
data class BazarEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val month: String, // "YYYY-MM"
    val buyerMemberId: Long,
    val itemsSummary: String, // e.g. "Rice (5kg), Fish (2kg), Egg (30pcs), Vegetables"
    val totalAmount: Double,
    val category: String = "GROCERY", // GROCERY, VEGETABLES, MEAT_FISH, SPICES, OTHER
    val note: String = "",
    val isVoided: Boolean = false
)

@Entity(tableName = "expenses")
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // RENT, ELECTRICITY, GAS, WATER, INTERNET, CLEANING, MAINTENANCE, OTHER
    val amount: Double,
    val date: String, // "YYYY-MM-DD"
    val month: String, // "YYYY-MM"
    val status: String = "PAID", // PAID, PENDING
    val note: String = "",
    val isVoided: Boolean = false
)

@Entity(tableName = "deposits")
data class DepositEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val amount: Double,
    val date: String, // "YYYY-MM-DD"
    val month: String, // "YYYY-MM"
    val paymentType: String = "CASH", // Strictly CASH manual recording
    val note: String = "Cash deposit",
    val receiptNumber: String = "",
    val isVoided: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationNotice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String = "ANNOUNCEMENT", // ANNOUNCEMENT, DUE_REMINDER, MEAL_UPDATE, SETTLEMENT, NOTICE
    val targetMemberId: Long? = null, // null = all members
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "in_app_messages")
data class InAppMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: Long, // 0 for Admin, or MemberId
    val receiverId: Long, // 0 for Admin, or MemberId, or -1 for All
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val details: String,
    val performedBy: String = "Admin",
    val timestamp: Long = System.currentTimeMillis()
)
