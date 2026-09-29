package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Member
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class ItemizedBazarRow(
    var nameWithQty: String = "",
    var priceStr: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBazarDialog(
    members: List<Member>,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (buyerId: Long, items: String, amount: Double, category: String, date: String, note: String) -> Unit
) {
    var buyerId by remember { mutableStateOf(members.firstOrNull()?.id ?: 0L) }
    val defaultDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var date by remember { mutableStateOf(defaultDate) }
    var buyerDropdownExpanded by remember { mutableStateOf(false) }

    // Itemized list rows: name & price
    val itemRows = remember {
        mutableStateListOf(
            ItemizedBazarRow(if (currentLanguage == AppLanguage.BN) "১ কেজি আলু" else "1kg Potato", "20"),
            ItemizedBazarRow("", "")
        )
    }

    // Auto-calculated sum of all item prices
    val totalCalculated = itemRows.sumOf { it.priceStr.toDoubleOrNull() ?: 0.0 }
    val selectedBuyer = members.find { it.id == buyerId }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RedPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    Strings.addBazar(currentLanguage),
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Buyer Dropdown
                ExposedDropdownMenuBox(
                    expanded = buyerDropdownExpanded,
                    onExpandedChange = { buyerDropdownExpanded = !buyerDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedBuyer?.name ?: (if (currentLanguage == AppLanguage.BN) "কে বাজার করেছে?" else "Select Buyer"),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(Strings.buyer(currentLanguage)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = buyerDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPrimary,
                            unfocusedBorderColor = BorderGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = buyerDropdownExpanded,
                        onDismissRequest = { buyerDropdownExpanded = false },
                        modifier = Modifier.background(PureWhite)
                    ) {
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m.name, color = DarkText) },
                                onClick = {
                                    buyerId = m.id
                                    buyerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Date Field
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("${Strings.date(currentLanguage)} (YYYY-MM-DD)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = BorderGray)

                // Itemized Header
                Text(
                    text = Strings.itemizedBazarTitle(currentLanguage),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = DarkText
                )

                // Itemized Input Rows (Item name with quantity & Price)
                itemRows.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = row.nameWithQty,
                            onValueChange = { itemRows[index] = row.copy(nameWithQty = it) },
                            placeholder = { Text(Strings.itemQuantityExample(currentLanguage), fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RedPrimary,
                                unfocusedBorderColor = BorderGray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(2f)
                        )

                        OutlinedTextField(
                            value = row.priceStr,
                            onValueChange = { itemRows[index] = row.copy(priceStr = it) },
                            placeholder = { Text(Strings.itemPriceExample(currentLanguage), fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RedPrimary,
                                unfocusedBorderColor = BorderGray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.2f)
                        )

                        if (itemRows.size > 1) {
                            IconButton(
                                onClick = { itemRows.removeAt(index) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = GrayText, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Add More Row Button
                TextButton(
                    onClick = { itemRows.add(ItemizedBazarRow("", "")) },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Strings.addItemRow(currentLanguage), color = RedPrimary, fontWeight = FontWeight.Bold)
                }

                // Auto-Calculated Total Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RedPrimaryContainer,
                    border = BorderStroke(1.dp, RedPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings.calculatedTotal(currentLanguage),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = RedOnPrimaryContainer
                        )
                        Text(
                            text = "৳${String.format(Locale.US, "%,.0f", totalCalculated)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = RedPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val validRows = itemRows.filter { it.nameWithQty.isNotBlank() && (it.priceStr.toDoubleOrNull() ?: 0.0) > 0 }
                    val summary = if (validRows.isNotEmpty()) {
                        validRows.joinToString(", ") { "${it.nameWithQty.trim()} (৳${it.priceStr.trim()})" }
                    } else {
                        "বাজার সামগ্রী"
                    }
                    if (buyerId > 0 && totalCalculated > 0) {
                        onConfirm(buyerId, summary, totalCalculated, "GROCERY", date, "")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_bazar_button")
            ) {
                Text(Strings.save(currentLanguage), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(Strings.cancel(currentLanguage), color = GrayText) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDepositDialog(
    members: List<Member>,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (memberId: Long, amount: Double, date: String, note: String) -> Unit
) {
    var selectedMemberId by remember { mutableStateOf(members.firstOrNull()?.id ?: 0L) }
    var amountStr by remember { mutableStateOf("") }
    val defaultDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var date by remember { mutableStateOf(defaultDate) }
    var note by remember { mutableStateOf(if (currentLanguage == AppLanguage.BN) "নগদ টাকা জমা" else "Cash Deposit") }
    var expanded by remember { mutableStateOf(false) }

    val selectedMember = members.find { it.id == selectedMemberId }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AdvanceGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = null, tint = AdvanceGreen, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    if (currentLanguage == AppLanguage.BN) "টাকা জমা রেকর্ড করুন" else "Record Cash Deposit",
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Member Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedMember?.name ?: (if (currentLanguage == AppLanguage.BN) "সদস্য নির্বাচন করুন" else "Select Member"),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(Strings.member(currentLanguage)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AdvanceGreen,
                            unfocusedBorderColor = BorderGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(PureWhite)
                    ) {
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = { Text("${m.name} (${Strings.room(currentLanguage)} ${m.roomNumber})", color = DarkText) },
                                onClick = {
                                    selectedMemberId = m.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text(Strings.amount(currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AdvanceGreen,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("deposit_amount_input")
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("${Strings.date(currentLanguage)} (YYYY-MM-DD)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AdvanceGreen,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(Strings.note(currentLanguage)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AdvanceGreen,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (selectedMemberId > 0 && amt > 0) {
                        onConfirm(selectedMemberId, amt, date, note)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AdvanceGreen, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_deposit_button")
            ) {
                Text(Strings.save(currentLanguage), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.cancel(currentLanguage), color = GrayText)
            }
        }
    )
}

@Composable
fun AddMemberDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, email: String, room: String, bed: String, initBalance: Double, customRent: Double, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("101") }
    var bed by remember { mutableStateOf("Bed-1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RedPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(Strings.addMember(currentLanguage), fontWeight = FontWeight.Bold, color = DarkText, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Strings.name(currentLanguage)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("member_name_input")
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(Strings.phone(currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("member_phone_input")
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text(Strings.room(currentLanguage)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPrimary,
                            unfocusedBorderColor = BorderGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bed,
                        onValueChange = { bed = it },
                        label = { Text(Strings.bed(currentLanguage)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPrimary,
                            unfocusedBorderColor = BorderGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(name, phone, "", room, bed, 0.0, 0.0, "")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_member_button")
            ) {
                Text(Strings.save(currentLanguage), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(Strings.cancel(currentLanguage), color = GrayText) }
        }
    )
}

@Composable
fun AddExpenseDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, amount: Double, date: String, status: String, note: String) -> Unit
) {
    var title by remember { mutableStateOf(if (currentLanguage == AppLanguage.BN) "বাসা ভাড়া" else "House Rent") }
    var amountStr by remember { mutableStateOf("") }
    val defaultDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var date by remember { mutableStateOf(defaultDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(UtilityIndigoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = UtilityIndigo, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    if (currentLanguage == AppLanguage.BN) "বিল বা ভাড়া এন্ট্রি করুন" else "Add Rent / Utility Bill",
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text(
                            if (currentLanguage == AppLanguage.BN) "খরচের নাম (যেমন: বাসা ভাড়া / বিদ্যুৎ / ওয়াইফাই) *" else "Bill Title (e.g. Rent, Electricity, WiFi) *"
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UtilityIndigo,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text(Strings.amount(currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UtilityIndigo,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("${Strings.date(currentLanguage)} (YYYY-MM-DD)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UtilityIndigo,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        onConfirm(title, "RENT", amt, date, "PAID", "")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = UtilityIndigo, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(Strings.save(currentLanguage), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(Strings.cancel(currentLanguage), color = GrayText) }
        }
    )
}
