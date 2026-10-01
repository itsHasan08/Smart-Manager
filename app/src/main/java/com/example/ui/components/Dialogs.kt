package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    onConfirm: (name: String, phone: String, room: String, homeAddress: String, userId: String, pass: String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("101") }
    var homeAddress by remember { mutableStateOf("") }

    var generatedUserId by remember { mutableStateOf("") }
    var generatedPassword by remember { mutableStateOf("") }
    var isLicenseGenerated by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BrandPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("নতুন সদস্য ও লাইসেন্স তৈরি", fontWeight = FontWeight.Bold, color = DarkText, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("সদস্যের পুরো নাম *") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("member_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("member_phone_input")
                )

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("রুম নম্বর *") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = homeAddress,
                    onValueChange = { homeAddress = it },
                    label = { Text("বাড়ির ঠিকানা (অপশনাল)") },
                    placeholder = { Text("জেলা, থানা, গ্রাম") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // License Generator Button
                if (!isLicenseGenerated) {
                    Button(
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank()) {
                                generatedUserId = "USER-${(1000..9999).random()}"
                                generatedPassword = "${(100..999).random()}${(10..99).random()}"
                                isLicenseGenerated = true
                                Toast.makeText(context, "লাইসেন্স কি তৈরি হয়েছে!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "অনুগ্রহ করে নাম ও মোবাইল নম্বর লিখুন", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🔑 লাইসেন্স কি জেনারেট করুন", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Generated License Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DepositGreenContainer),
                        border = BorderStroke(1.dp, DepositGreenBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✅ সদস্যের লাইসেন্স প্রস্তুত:", fontWeight = FontWeight.Bold, color = DepositGreen, fontSize = 13.sp)
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Member Credentials", "আইডি: $generatedUserId\nপাসওয়ার্ড: $generatedPassword")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "আইডি ও পাসওয়ার্ড কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = DepositGreen, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("👤 ইউজার আইডি: $generatedUserId", fontWeight = FontWeight.ExtraBold, color = DarkText, fontSize = 14.sp)
                            Text("🔑 পাসওয়ার্ড: $generatedPassword", fontWeight = FontWeight.ExtraBold, color = DarkText, fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "এই তথ্য দিয়ে সদস্য 'মেসে প্রবেশ করুন' অপশন থেকে মেস অ্যাপে লগইন করতে পারবেন।",
                                style = MaterialTheme.typography.labelSmall,
                                color = GrayText
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        val uid = if (generatedUserId.isNotBlank()) generatedUserId else "USER-${(1000..9999).random()}"
                        val pwd = if (generatedPassword.isNotBlank()) generatedPassword else "1234"
                        onConfirm(name.trim(), phone.trim(), room.trim(), homeAddress.trim(), uid, pwd)
                    } else {
                        Toast.makeText(context, "নাম ও ফোন নম্বর দিন", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_member_button")
            ) {
                Text("সদস্য সংরক্ষণ সম্পন্ন করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = GrayText) }
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

@Composable
fun EditMessProfileDialog(
    profile: com.example.data.model.MessProfile?,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (messName: String, managerName: String, phone: String) -> Unit
) {
    var messName by remember { mutableStateOf(profile?.messName ?: "") }
    var managerName by remember { mutableStateOf(profile?.managerName ?: "") }
    var phone by remember { mutableStateOf(profile?.managerPhone ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = Strings.editProfile(currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = DarkText
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
                Text(
                    text = if (currentLanguage == AppLanguage.BN) 
                        "আপনার মেসের নাম ও ম্যানেজারের মোবাইল নম্বর সেট করুন:" 
                    else 
                        "Configure your mess name and manager contact details:",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayText
                )

                OutlinedTextField(
                    value = messName,
                    onValueChange = { messName = it },
                    label = { Text(Strings.messNameLabel(currentLanguage)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_mess_name")
                )

                OutlinedTextField(
                    value = managerName,
                    onValueChange = { managerName = it },
                    label = { Text(Strings.managerNameLabel(currentLanguage)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_manager_name")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(Strings.managerPhoneLabel(currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_manager_phone")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (messName.isNotBlank() && managerName.isNotBlank()) {
                        onConfirm(messName.trim(), managerName.trim(), phone.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_save_profile")
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
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = DueRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, color = DarkText)
            }
        },
        text = {
            Text(message, color = GrayText, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = DueRed, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("মুছে ফেলুন (Delete)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = GrayText)
            }
        }
    )
}

@Composable
fun EditBazarDialog(
    bazar: com.example.data.model.BazarEntry,
    members: List<Member>,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (updated: com.example.data.model.BazarEntry) -> Unit
) {
    var buyerId by remember { mutableStateOf(bazar.buyerMemberId) }
    var itemsSummary by remember { mutableStateOf(bazar.itemsSummary) }
    var amountStr by remember { mutableStateOf(bazar.totalAmount.toString()) }
    var date by remember { mutableStateOf(bazar.date) }
    var note by remember { mutableStateOf(bazar.note) }
    var buyerDropdownExpanded by remember { mutableStateOf(false) }

    val selectedBuyer = members.find { it.id == buyerId }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                if (currentLanguage == AppLanguage.BN) "বাজারের তথ্য পরিবর্তন করুন" else "Edit Bazar Entry",
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Buyer Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedBuyer?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(Strings.buyer(currentLanguage)) },
                        trailingIcon = {
                            IconButton(onClick = { buyerDropdownExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().clickable { buyerDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = buyerDropdownExpanded,
                        onDismissRequest = { buyerDropdownExpanded = false }
                    ) {
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m.name) },
                                onClick = {
                                    buyerId = m.id
                                    buyerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = itemsSummary,
                    onValueChange = { itemsSummary = it },
                    label = { Text(Strings.items(currentLanguage)) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text(Strings.amount(currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text(Strings.date(currentLanguage)) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: bazar.totalAmount
                    if (amt > 0 && itemsSummary.isNotBlank()) {
                        onConfirm(
                            bazar.copy(
                                buyerMemberId = buyerId,
                                itemsSummary = itemsSummary,
                                totalAmount = amt,
                                date = date,
                                note = note
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("আপডেট করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = GrayText) }
        }
    )
}

@Composable
fun EditDepositDialog(
    deposit: com.example.data.model.DepositEntry,
    members: List<Member>,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (updated: com.example.data.model.DepositEntry) -> Unit
) {
    var memberId by remember { mutableStateOf(deposit.memberId) }
    var amountStr by remember { mutableStateOf(deposit.amount.toString()) }
    var date by remember { mutableStateOf(deposit.date) }
    var note by remember { mutableStateOf(deposit.note) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val selectedMember = members.find { it.id == memberId }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                if (currentLanguage == AppLanguage.BN) "নগদ জমার তথ্য পরিবর্তন করুন" else "Edit Cash Deposit",
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedMember?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(Strings.member(currentLanguage)) },
                        trailingIcon = {
                            IconButton(onClick = { dropdownExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().clickable { dropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m.name) },
                                onClick = {
                                    memberId = m.id
                                    dropdownExpanded = false
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
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text(Strings.date(currentLanguage)) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(Strings.note(currentLanguage)) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: deposit.amount
                    if (amt > 0) {
                        onConfirm(
                            deposit.copy(
                                memberId = memberId,
                                amount = amt,
                                date = date,
                                note = note
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DepositGreen, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("আপডেট করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = GrayText) }
        }
    )
}

@Composable
fun EditMemberDialog(
    member: Member,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (updated: Member) -> Unit
) {
    var name by remember { mutableStateOf(member.name) }
    var phone by remember { mutableStateOf(member.phone) }
    var room by remember { mutableStateOf(member.roomNumber) }
    var bed by remember { mutableStateOf(member.bedNumber) }
    var pin by remember { mutableStateOf(member.pin) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                if (currentLanguage == AppLanguage.BN) "সদস্যের তথ্য সম্পাদনা করুন" else "Edit Member Information",
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Strings.name(currentLanguage)) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(Strings.phone(currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text(Strings.room(currentLanguage)) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bed,
                        onValueChange = { bed = it },
                        label = { Text(Strings.bed(currentLanguage)) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("লগইন পিন (PIN)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            member.copy(
                                name = name.trim(),
                                phone = phone.trim(),
                                roomNumber = room.trim(),
                                bedNumber = bed.trim(),
                                pin = pin.trim().ifBlank { "1234" }
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("আপডেট করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = GrayText) }
        }
    )
}
