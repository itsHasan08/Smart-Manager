package com.example.ui.theme

enum class AppLanguage {
    BN,
    EN
}

object Strings {
    fun appName(lang: AppLanguage) = if (lang == AppLanguage.BN) "স্মার্ট ম্যানেজার" else "Smart Manager"
    fun appTagline(lang: AppLanguage) = if (lang == AppLanguage.BN) "সহজ ও স্বচ্ছ মেস ব্যবস্থাপনা" else "Simple & Transparent Mess Manager"
    fun managerBalance(lang: AppLanguage) = if (lang == AppLanguage.BN) "ম্যানেজারের হাতে নগদ ব্যালেন্স" else "Manager Cash Balance"
    fun totalDeposits(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট নগদ জমা" else "Total Collected"
    fun totalExpenses(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট মেস খরচ" else "Total Expenses"
    fun totalDue(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট বকেয়া বাকি" else "Total Pending Due"
    fun mealRate(lang: AppLanguage) = if (lang == AppLanguage.BN) "বর্তমান মিল রেট" else "Current Meal Rate"
    fun totalMeals(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট মিল" else "Total Meals"
    fun monthlyBazar(lang: AppLanguage) = if (lang == AppLanguage.BN) "চলতি মাসের বাজার" else "Monthly Food Bazar"
    fun rentAndBills(lang: AppLanguage) = if (lang == AppLanguage.BN) "ভাড়া ও বিল" else "Rent & Utilities"
    fun quickDeposit(lang: AppLanguage) = if (lang == AppLanguage.BN) "টাকা জমা" else "Cash Deposit"
    fun quickBazar(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাজার খরচ" else "Bazar Cost"
    fun quickMeal(lang: AppLanguage) = if (lang == AppLanguage.BN) "আজকের মিল" else "Daily Meals"
    fun membersSummary(lang: AppLanguage) = if (lang == AppLanguage.BN) "সদস্যদের হিসাব সারাংশ" else "Member Account Summary"
    fun viewMonthlySlip(lang: AppLanguage) = if (lang == AppLanguage.BN) "মাসিক হিসাব ও স্লিপ দেখুন" else "Monthly Settlement & Slips"
    fun viewMonthlySlipDesc(lang: AppLanguage) = if (lang == AppLanguage.BN) "সব সদস্যের হিসাব স্লিপ একসাথে ডাউনলোড ও শেয়ার করুন" else "View & share all member audit slips"
    fun due(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাকি" else "Due"
    fun advance(lang: AppLanguage) = if (lang == AppLanguage.BN) "জমা" else "Advance"
    fun room(lang: AppLanguage) = if (lang == AppLanguage.BN) "রুম" else "Room"
    fun bed(lang: AppLanguage) = if (lang == AppLanguage.BN) "বেড" else "Bed"
    fun mealsCount(lang: AppLanguage) = if (lang == AppLanguage.BN) "মিল" else "Meals"
    fun mealUnit(lang: AppLanguage) = if (lang == AppLanguage.BN) "টি" else "meals"
    fun date(lang: AppLanguage) = if (lang == AppLanguage.BN) "তারিখ" else "Date"
    fun morning(lang: AppLanguage) = if (lang == AppLanguage.BN) "সকাল" else "Morning"
    fun noon(lang: AppLanguage) = if (lang == AppLanguage.BN) "দুপুর" else "Lunch"
    fun night(lang: AppLanguage) = if (lang == AppLanguage.BN) "রাত" else "Dinner"
    fun guest(lang: AppLanguage) = if (lang == AppLanguage.BN) "গেস্ট" else "Guest"
    fun saveMeals(lang: AppLanguage) = if (lang == AppLanguage.BN) "মিল সেভ করুন" else "Save Meals"
    fun allLunchDinner(lang: AppLanguage) = if (lang == AppLanguage.BN) "সবার দুপুর ও রাত (১)" else "All Lunch & Dinner (1)"
    fun clearAllMeals(lang: AppLanguage) = if (lang == AppLanguage.BN) "সব মিল বন্ধ (০)" else "Clear All Meals (0)"
    fun addBazar(lang: AppLanguage) = if (lang == AppLanguage.BN) "নতুন বাজার যোগ করুন" else "Add Bazar Entry"
    fun recordDeposit(lang: AppLanguage) = if (lang == AppLanguage.BN) "নগদ জমা নিন" else "Record Deposit"
    fun addMember(lang: AppLanguage) = if (lang == AppLanguage.BN) "নতুন সদস্য যোগ করুন" else "Add New Member"
    fun shareSlip(lang: AppLanguage) = if (lang == AppLanguage.BN) "স্লিপ শেয়ার করুন" else "Share Slip"
    fun navDashboard(lang: AppLanguage) = if (lang == AppLanguage.BN) "ড্যাশবোর্ড" else "Dashboard"
    fun navMeals(lang: AppLanguage) = if (lang == AppLanguage.BN) "মিল হিসাব" else "Meals"
    fun navBazar(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাজার" else "Bazar"
    fun navCash(lang: AppLanguage) = if (lang == AppLanguage.BN) "নগদ জমা" else "Cash"
    fun navMembers(lang: AppLanguage) = if (lang == AppLanguage.BN) "সদস্যবৃন্দ" else "Members"
    fun cancel(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাতিল" else "Cancel"
    fun save(lang: AppLanguage) = if (lang == AppLanguage.BN) "সেভ করুন" else "Save"
    fun buyer(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাজারকারী" else "Market Buyer"
    fun items(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাজারের বিবরণ" else "Purchased Items"
    fun amount(lang: AppLanguage) = if (lang == AppLanguage.BN) "টাকার পরিমাণ (৳)" else "Amount (৳)"
    fun member(lang: AppLanguage) = if (lang == AppLanguage.BN) "সদস্য" else "Member"
    fun note(lang: AppLanguage) = if (lang == AppLanguage.BN) "নোট" else "Note"
    fun phone(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোবাইল নম্বর" else "Mobile Number"
    fun name(lang: AppLanguage) = if (lang == AppLanguage.BN) "নাম" else "Full Name"
    fun rentBillsTab(lang: AppLanguage) = if (lang == AppLanguage.BN) "মেসের বিল ও ভাড়া" else "Rent & Utilities"
    fun depositsTab(lang: AppLanguage) = if (lang == AppLanguage.BN) "সদস্যদের নগদ জমা" else "Cash Deposits"

    // Itemized Bazar Strings
    fun itemizedBazarTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "পণ্যের তালিকা ও দরদাম" else "Itemized Bazar Items & Prices"
    fun itemQuantityExample(lang: AppLanguage) = if (lang == AppLanguage.BN) "যেমন: ১ কেজি আলু" else "e.g. 1kg Potato"
    fun itemPriceExample(lang: AppLanguage) = if (lang == AppLanguage.BN) "টাকা (২০)" else "Price (20)"
    fun addItemRow(lang: AppLanguage) = if (lang == AppLanguage.BN) "+ আরো জিনিস যোগ করুন" else "+ Add More Item"
    fun calculatedTotal(lang: AppLanguage) = if (lang == AppLanguage.BN) "স্বয়ংক্রিয় মোট হিসাব" else "Calculated Total"

    // Drawer Strings
    fun drawerHome(lang: AppLanguage) = if (lang == AppLanguage.BN) "ড্যাশবোর্ড" else "Dashboard"
    fun drawerMeals(lang: AppLanguage) = if (lang == AppLanguage.BN) "মিল হিসাব ও হাজিরা" else "Daily Meal Sheet"
    fun drawerBazar(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাজার ও খরচের তালিকা" else "Bazar & Food Expenses"
    fun drawerDeposits(lang: AppLanguage) = if (lang == AppLanguage.BN) "নগদ টাকা জমার খাতা" else "Cash Deposits Ledger"
    fun drawerBills(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাসা ভাড়া ও ইউটিলিটি বিল" else "Flat Rent & Utility Bills"
    fun drawerSettlement(lang: AppLanguage) = if (lang == AppLanguage.BN) "মাসিক হিসাব ও ফাইনাল স্লিপ" else "Monthly Settlement Slip"
    fun drawerLanguage(lang: AppLanguage) = if (lang == AppLanguage.BN) "ভাষা পরিবর্তন (Language)" else "Change Language (ভাষা)"
    fun drawerSwitchRole(lang: AppLanguage) = if (lang == AppLanguage.BN) "ভিউ পরিবর্তন করুন" else "Switch View / Role"

    // Splash
    fun splashLoading(lang: AppLanguage) = if (lang == AppLanguage.BN) "স্মার্ট ম্যানেজার প্রস্তুত হচ্ছে..." else "Loading Smart Manager..."
}
