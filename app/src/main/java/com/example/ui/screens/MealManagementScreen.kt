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
import com.example.data.model.MealEntry
import com.example.data.model.Member
import com.example.data.model.MessSummary
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MealManagementScreen(
    members: List<Member>,
    allMonthMeals: List<MealEntry>,
    messSummary: MessSummary,
    currentMonth: String,
    currentLanguage: AppLanguage,
    onSaveMeals: (List<MealEntry>) -> Unit
) {
    val context = LocalContext.current
    var selectedDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

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
            .background(PureWhite)
            .padding(horizontal = 16.dp)
            .testTag("meal_management_screen")
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Date Bar
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = PureWhite,
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { selectedDate = adjustDateBy(selectedDate, -1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = RedPrimary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedDate,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DarkText
                    )
                }

                IconButton(onClick = { selectedDate = adjustDateBy(selectedDate, 1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = RedPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Batch Toggle row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    members.forEach { m ->
                        val current = dailyMealsState[m.id] ?: MealEntry(date = selectedDate, month = currentMonth, memberId = m.id)
                        dailyMealsState[m.id] = current.copy(lunch = 1.0, dinner = 1.0)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimaryContainer, contentColor = RedOnPrimaryContainer),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text(
                    Strings.allLunchDinner(currentLanguage),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            OutlinedButton(
                onClick = {
                    members.forEach { m ->
                        val current = dailyMealsState[m.id] ?: MealEntry(date = selectedDate, month = currentMonth, memberId = m.id)
                        dailyMealsState[m.id] = current.copy(breakfast = 0.0, lunch = 0.0, dinner = 0.0, guestMeals = 0.0)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderGray),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text(
                    Strings.clearAllMeals(currentLanguage),
                    style = MaterialTheme.typography.labelSmall.copy(color = GrayText)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Members List with Smooth Toggles
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(members, key = { it.id }) { m ->
                val entry = dailyMealsState[m.id] ?: MealEntry(date = selectedDate, month = currentMonth, memberId = m.id)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${m.name} (${Strings.room(currentLanguage)} ${m.roomNumber})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = DarkText
                            )

                            Text(
                                text = "${Strings.totalMeals(currentLanguage)}: ${String.format(Locale.US, "%.1f", entry.totalMeals)} ${Strings.mealUnit(currentLanguage)}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = RedPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Meal Toggles (সকাল, দুপুর, রাত, গেস্ট)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RedMealToggle(
                                label = Strings.morning(currentLanguage),
                                value = entry.breakfast,
                                onClick = {
                                    val next = if (entry.breakfast == 0.0) 1.0 else 0.0
                                    dailyMealsState[m.id] = entry.copy(breakfast = next)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            RedMealToggle(
                                label = Strings.noon(currentLanguage),
                                value = entry.lunch,
                                onClick = {
                                    val next = if (entry.lunch == 1.0) 0.0 else 1.0
                                    dailyMealsState[m.id] = entry.copy(lunch = next)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            RedMealToggle(
                                label = Strings.night(currentLanguage),
                                value = entry.dinner,
                                onClick = {
                                    val next = if (entry.dinner == 1.0) 0.0 else 1.0
                                    dailyMealsState[m.id] = entry.copy(dinner = next)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Guest Stepper
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceGray,
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = "${Strings.guest(currentLanguage)}:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GrayText
                                    )
                                    IconButton(
                                        onClick = {
                                            val g = (entry.guestMeals - 1.0).coerceAtLeast(0.0)
                                            dailyMealsState[m.id] = entry.copy(guestMeals = g)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(12.dp))
                                    }
                                    Text(
                                        text = "${entry.guestMeals.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = DarkText
                                    )
                                    IconButton(
                                        onClick = {
                                            val g = entry.guestMeals + 1.0
                                            dailyMealsState[m.id] = entry.copy(guestMeals = g)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Big Red Save Button
        val dayTotal = dailyMealsState.values.sumOf { it.totalMeals }
        Button(
            onClick = {
                onSaveMeals(dailyMealsState.values.toList())
                val toastMsg = if (currentLanguage == AppLanguage.BN) {
                    "$selectedDate-এর মিল হিসাব সফলভাবে সেভ করা হয়েছে!"
                } else {
                    "Meals for $selectedDate saved successfully!"
                }
                Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary, contentColor = PureWhite),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_meals_button")
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${Strings.saveMeals(currentLanguage)} (${String.format(Locale.US, "%.1f", dayTotal)} ${Strings.mealUnit(currentLanguage)})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun RedMealToggle(
    label: String,
    value: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = value > 0.0
    val bg = if (isSelected) RedPrimary else PureWhite
    val content = if (isSelected) PureWhite else DarkText
    val border = if (isSelected) null else BorderStroke(1.dp, BorderGray)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = border,
        modifier = modifier
            .height(36.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$label: ${if (value > 0) "1" else "0"}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = content
            )
        }
    }
}

private fun adjustDateBy(dateStr: String, days: Int): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.time = sdf.parse(dateStr) ?: Date()
        cal.add(Calendar.DAY_OF_YEAR, days)
        sdf.format(cal.time)
    } catch (e: Exception) {
        dateStr
    }
}
