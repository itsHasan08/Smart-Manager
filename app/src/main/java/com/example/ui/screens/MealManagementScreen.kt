package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealEntry
import com.example.data.model.Member
import com.example.data.model.MessSummary
import com.example.ui.theme.*
import com.example.ui.viewmodel.CurrentRole
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MealManagementScreen(
    members: List<Member>,
    allMonthMeals: List<MealEntry>,
    messSummary: MessSummary,
    currentMonth: String,
    currentLanguage: AppLanguage,
    currentRole: CurrentRole = CurrentRole.ADMIN,
    currentMemberId: Long = 1L,
    onSaveMeals: (List<MealEntry>) -> Unit,
    onSaveSelfMeal: (memberId: Long, date: String, breakfast: Double, lunch: Double, dinner: Double, guest: Double) -> Unit = { _, _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val todayDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val tomorrowDateStr = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    var selectedDate by remember { mutableStateOf(todayDateStr) }

    // If logged in as MEMBER: Show Self-Service Meal Control!
    if (currentRole == CurrentRole.MEMBER) {
        val loggedInMember = members.find { it.id == currentMemberId } ?: members.firstOrNull()
        val existingEntry = allMonthMeals.find { it.date == selectedDate && it.memberId == (loggedInMember?.id ?: 0L) }

        var breakfast by remember(selectedDate, existingEntry) { mutableStateOf(existingEntry?.breakfast ?: 0.0) }
        var lunch by remember(selectedDate, existingEntry) { mutableStateOf(existingEntry?.lunch ?: 1.0) }
        var dinner by remember(selectedDate, existingEntry) { mutableStateOf(existingEntry?.dinner ?: 1.0) }
        var guest by remember(selectedDate, existingEntry) { mutableStateOf(existingEntry?.guestMeals ?: 0.0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(16.dp)
                .testTag("member_meal_control_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MealAmberContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = MealAmber, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "আমার মিল কন্ট্রোল (${loggedInMember?.name ?: "সদস্য"})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DarkText
                        )
                        Text(
                            text = "আপনার খাবার বন্ধ বা চালু করুন",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayText
                        )
                    }
                }
            }

            // Date Tabs (আজ / আগামীকাল)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceGray)
                    .padding(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedDate = todayDateStr },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedDate == todayDateStr) PureWhite else SurfaceGray,
                    border = if (selectedDate == todayDateStr) BorderStroke(1.dp, BorderGray) else null
                ) {
                    Text(
                        text = "আজকের খাবার ($todayDateStr)",
                        modifier = Modifier.padding(vertical = 10.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedDate == todayDateStr) BrandPrimary else GrayText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedDate = tomorrowDateStr },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedDate == tomorrowDateStr) PureWhite else SurfaceGray,
                    border = if (selectedDate == tomorrowDateStr) BorderStroke(1.dp, BorderGray) else null
                ) {
                    Text(
                        text = "আগামীকালের খাবার",
                        modifier = Modifier.padding(vertical = 10.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedDate == tomorrowDateStr) BrandPrimary else GrayText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Meal Toggles Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MemberMealCounterRow(
                        title = "সকালের খাবার (Breakfast)",
                        value = breakfast,
                        onIncrement = { if (breakfast < 5.0) breakfast += 1.0 },
                        onDecrement = { if (breakfast > 0.0) breakfast -= 1.0 },
                        onToggle = { breakfast = if (breakfast > 0.0) 0.0 else 1.0 }
                    )

                    HorizontalDivider(color = BorderGray)

                    MemberMealCounterRow(
                        title = "দুপুরের খাবার (Lunch)",
                        value = lunch,
                        onIncrement = { if (lunch < 5.0) lunch += 1.0 },
                        onDecrement = { if (lunch > 0.0) lunch -= 1.0 },
                        onToggle = { lunch = if (lunch > 0.0) 0.0 else 1.0 }
                    )

                    HorizontalDivider(color = BorderGray)

                    MemberMealCounterRow(
                        title = "রাতের খাবার (Dinner)",
                        value = dinner,
                        onIncrement = { if (dinner < 5.0) dinner += 1.0 },
                        onDecrement = { if (dinner > 0.0) dinner -= 1.0 },
                        onToggle = { dinner = if (dinner > 0.0) 0.0 else 1.0 }
                    )

                    HorizontalDivider(color = BorderGray)

                    MemberMealCounterRow(
                        title = "মেহমান / গেস্ট মিল (Guest)",
                        value = guest,
                        onIncrement = { if (guest < 5.0) guest += 1.0 },
                        onDecrement = { if (guest > 0.0) guest -= 1.0 },
                        onToggle = { guest = if (guest > 0.0) 0.0 else 1.0 }
                    )
                }
            }

            // Quick Toggle Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        lunch = 1.0
                        dinner = 1.0
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("সব মিল চালু (১+১)")
                }

                OutlinedButton(
                    onClick = {
                        breakfast = 0.0
                        lunch = 0.0
                        dinner = 0.0
                        guest = 0.0
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DueRed)
                ) {
                    Text("খাবার বন্ধ (০)")
                }
            }

            // Save Meal Button
            Button(
                onClick = {
                    if (loggedInMember != null) {
                        onSaveSelfMeal(loggedInMember.id, selectedDate, breakfast, lunch, dinner, guest)
                        Toast.makeText(context, "$selectedDate তারিখের মিল সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("আমার খাবার সেভ করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    } else {
        // MANAGER (ADMIN) FULL MEAL SHEET
        val dailyMealsState = remember { mutableStateMapOf<Long, MealEntry>() }

        LaunchedEffect(selectedDate, allMonthMeals, members) {
            dailyMealsState.clear()
            val mealsForDate = allMonthMeals.filter { it.date == selectedDate }.associateBy { it.memberId }

            members.forEach { m ->
                val existing = mealsForDate[m.id]
                if (existing != null) {
                    dailyMealsState[m.id] = existing
                } else {
                    dailyMealsState[m.id] = MealEntry(
                        date = selectedDate,
                        month = currentMonth,
                        memberId = m.id,
                        breakfast = 0.0,
                        lunch = 1.0,
                        dinner = 1.0,
                        guestMeals = 0.0
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(PureWhite)
                .testTag("admin_meal_screen")
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Date Picker Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceGray,
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BrandPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "তারিখ: $selectedDate",
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedDate == todayDateStr,
                            onClick = { selectedDate = todayDateStr },
                            label = { Text("আজ") }
                        )
                        FilterChip(
                            selected = selectedDate == tomorrowDateStr,
                            onClick = { selectedDate = tomorrowDateStr },
                            label = { Text("কাল") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dedicated Card for Manager's Own Meal
            val managerMember = members.find { it.role == "ADMIN" || it.id == currentMemberId } ?: members.firstOrNull()
            if (managerMember != null) {
                val mgrMeal = dailyMealsState[managerMember.id] ?: MealEntry(
                    date = selectedDate,
                    month = currentMonth,
                    memberId = managerMember.id
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandPrimaryContainer),
                    border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "👑 ম্যানেজারের নিজের মিল (${managerMember.name})",
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "মোট: ${String.format(Locale.US, "%.1f", mgrMeal.totalMeals)} মিল",
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MiniMealItem("সকাল", mgrMeal.breakfast) {
                                dailyMealsState[managerMember.id] = mgrMeal.copy(breakfast = if (mgrMeal.breakfast > 0) 0.0 else 1.0)
                            }
                            MiniMealItem("দুপুর", mgrMeal.lunch) {
                                dailyMealsState[managerMember.id] = mgrMeal.copy(lunch = if (mgrMeal.lunch > 0) 0.0 else 1.0)
                            }
                            MiniMealItem("রাত", mgrMeal.dinner) {
                                dailyMealsState[managerMember.id] = mgrMeal.copy(dinner = if (mgrMeal.dinner > 0) 0.0 else 1.0)
                            }
                            MiniMealItem("গেস্ট", mgrMeal.guestMeals) {
                                dailyMealsState[managerMember.id] = mgrMeal.copy(guestMeals = if (mgrMeal.guestMeals > 0) 0.0 else 1.0)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Quick Batch Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        members.forEach { m ->
                            val current = dailyMealsState[m.id] ?: return@forEach
                            dailyMealsState[m.id] = current.copy(lunch = 1.0, dinner = 1.0)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("সবার (১+১)", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        members.forEach { m ->
                            val current = dailyMealsState[m.id] ?: return@forEach
                            dailyMealsState[m.id] = current.copy(breakfast = 0.0, lunch = 0.0, dinner = 0.0, guestMeals = 0.0)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DueRed)
                ) {
                    Text("সবার বন্ধ (০)", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        onSaveMeals(dailyMealsState.values.toList())
                        Toast.makeText(context, "$selectedDate তারিখের মিল শিট সেভ হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite)
                ) {
                    Text("সব সেভ করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Member list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(members, key = { it.id }) { member ->
                    val meal = dailyMealsState[member.id] ?: MealEntry(
                        date = selectedDate,
                        month = currentMonth,
                        memberId = member.id
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = member.name,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Text(
                                    text = "মোট: ${String.format(Locale.US, "%.1f", meal.totalMeals)} মিল",
                                    fontWeight = FontWeight.Bold,
                                    color = MealAmber
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MiniMealItem("সকাল", meal.breakfast) {
                                    dailyMealsState[member.id] = meal.copy(breakfast = if (meal.breakfast > 0) 0.0 else 1.0)
                                }
                                MiniMealItem("দুপুর", meal.lunch) {
                                    dailyMealsState[member.id] = meal.copy(lunch = if (meal.lunch > 0) 0.0 else 1.0)
                                }
                                MiniMealItem("রাত", meal.dinner) {
                                    dailyMealsState[member.id] = meal.copy(dinner = if (meal.dinner > 0) 0.0 else 1.0)
                                }
                                MiniMealItem("গেস্ট", meal.guestMeals) {
                                    dailyMealsState[member.id] = meal.copy(guestMeals = if (meal.guestMeals > 0) 0.0 else 1.0)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberMealCounterRow(
    title: String,
    value: Double,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = DarkText, fontSize = 14.sp)
            Text(
                text = if (value > 0.0) "খাবার চালু আছে" else "খাবার বন্ধ",
                style = MaterialTheme.typography.labelSmall,
                color = if (value > 0.0) DepositGreen else DueRed
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledIconButton(
                onClick = onDecrement,
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceGray, contentColor = DarkText)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(16.dp))
            }

            Text(
                text = String.format(Locale.US, "%.0f", value),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp),
                color = if (value > 0.0) BrandPrimary else GrayText
            )

            FilledIconButton(
                onClick = onIncrement,
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = BrandPrimaryContainer, contentColor = BrandPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun MiniMealItem(label: String, count: Double, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (count > 0) BrandPrimaryContainer else SurfaceGray,
        border = BorderStroke(1.dp, if (count > 0) BrandPrimary.copy(alpha = 0.3f) else BorderGray),
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = GrayText)
            Text(
                text = String.format(Locale.US, "%.0f", count),
                fontWeight = FontWeight.Bold,
                color = if (count > 0) BrandPrimary else GrayText
            )
        }
    }
}
