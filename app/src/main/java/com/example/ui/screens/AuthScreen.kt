package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Member
import com.example.data.model.MessProfile
import com.example.ui.theme.*

enum class AuthMode {
    LOGIN,
    SIGN_UP
}

enum class LoginType {
    MANAGER,
    MEMBER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    profile: MessProfile?,
    members: List<Member>,
    currentLanguage: AppLanguage,
    onLoginManager: (phone: String, pin: String) -> Unit,
    onLoginMember: (memberId: Long, pin: String) -> Unit,
    onCreateMess: (messName: String, managerName: String, phone: String, pin: String) -> Unit
) {
    val context = LocalContext.current
    var authMode by remember {
        mutableStateOf(if (profile == null) AuthMode.SIGN_UP else AuthMode.LOGIN)
    }
    var loginType by remember { mutableStateOf(LoginType.MANAGER) }

    // Sign Up Fields
    var signUpMessName by remember { mutableStateOf("") }
    var signUpManagerName by remember { mutableStateOf("") }
    var signUpPhone by remember { mutableStateOf("") }
    var signUpPin by remember { mutableStateOf("1234") }

    // Login Fields
    var loginPhone by remember { mutableStateOf(profile?.managerPhone ?: "") }
    var loginPin by remember { mutableStateOf("") }
    var selectedMemberId by remember { mutableStateOf(members.firstOrNull { it.role != "ADMIN" }?.id ?: members.firstOrNull()?.id ?: 0L) }
    var memberDropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
            .testTag("auth_screen"),
        color = OffWhite
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Logo Icon
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(BrandPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "স্মার্ট মেস ম্যানেজার" else "Smart Mess Manager",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        ),
                        color = DarkText
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "সহজ, স্বচ্ছ ও ঝামেলামুক্ত মেস হিসাব" else "Simple & Transparent Mess Accounts",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Mode Switch Tabs (লগইন / নতুন মেস একাউন্ট)
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
                                .clickable { authMode = AuthMode.LOGIN },
                            shape = RoundedCornerShape(10.dp),
                            color = if (authMode == AuthMode.LOGIN) PureWhite else SurfaceGray,
                            border = if (authMode == AuthMode.LOGIN) BorderStroke(1.dp, BorderGray) else null
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.BN) "লগইন করুন" else "Log In",
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (authMode == AuthMode.LOGIN) BrandPrimary else GrayText,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { authMode = AuthMode.SIGN_UP },
                            shape = RoundedCornerShape(10.dp),
                            color = if (authMode == AuthMode.SIGN_UP) PureWhite else SurfaceGray,
                            border = if (authMode == AuthMode.SIGN_UP) BorderStroke(1.dp, BorderGray) else null
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.BN) "নতুন মেস অ্যাকাউন্ট" else "New Mess",
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (authMode == AuthMode.SIGN_UP) BrandPrimary else GrayText,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (authMode == AuthMode.LOGIN) {
                        // Manager vs Member Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = loginType == LoginType.MANAGER,
                                onClick = { loginType = LoginType.MANAGER },
                                label = { Text(if (currentLanguage == AppLanguage.BN) "👑 ম্যানেজার লগইন" else "Manager Mode") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandPrimaryContainer,
                                    selectedLabelColor = BrandPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = loginType == LoginType.MEMBER,
                                onClick = { loginType = LoginType.MEMBER },
                                label = { Text(if (currentLanguage == AppLanguage.BN) "👤 সদস্য লগইন" else "Member Mode") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DepositGreenContainer,
                                    selectedLabelColor = DepositGreen
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (loginType == LoginType.MANAGER) {
                            // Manager Login Fields
                            OutlinedTextField(
                                value = loginPhone,
                                onValueChange = { loginPhone = it },
                                label = { Text(if (currentLanguage == AppLanguage.BN) "ম্যানেজারের মোবাইল নম্বর" else "Manager Phone Number") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("login_manager_phone")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = loginPin,
                                onValueChange = { loginPin = it },
                                label = { Text(if (currentLanguage == AppLanguage.BN) "৪-সংখ্যার পিন (ডিফল্ট: 1234)" else "4-digit PIN (default: 1234)") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("login_manager_pin")
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (loginPin.isNotBlank()) {
                                        onLoginManager(loginPhone, loginPin)
                                    } else {
                                        Toast.makeText(context, "অনুগ্রহ করে পিন লিখুন (যেমন: 1234)", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_manager_login"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite)
                            ) {
                                Icon(Icons.Default.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (currentLanguage == AppLanguage.BN) "ম্যানেজার হিসেবে প্রবেশ করুন" else "Log In as Manager",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        } else {
                            // Member Login Fields
                            val selectedMember = members.find { it.id == selectedMemberId } ?: members.firstOrNull()

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedMember?.name ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (currentLanguage == AppLanguage.BN) "সদস্য নির্বাচন করুন" else "Select Member") },
                                    trailingIcon = {
                                        IconButton(onClick = { memberDropdownExpanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { memberDropdownExpanded = true }
                                )

                                DropdownMenu(
                                    expanded = memberDropdownExpanded,
                                    onDismissRequest = { memberDropdownExpanded = false },
                                    modifier = Modifier.background(PureWhite)
                                ) {
                                    members.forEach { m ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(m.name, fontWeight = FontWeight.Bold, color = DarkText)
                                                    Text("রুম: ${m.roomNumber} • ${m.phone}", style = MaterialTheme.typography.labelSmall, color = GrayText)
                                                }
                                            },
                                            onClick = {
                                                selectedMemberId = m.id
                                                memberDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = loginPin,
                                onValueChange = { loginPin = it },
                                label = { Text(if (currentLanguage == AppLanguage.BN) "সদস্যের পিন (ডিফল্ট: 1234)" else "Member PIN (default: 1234)") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("login_member_pin")
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (selectedMember != null) {
                                        onLoginMember(selectedMember.id, loginPin)
                                    } else {
                                        Toast.makeText(context, "কোন সদস্য পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_member_login"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DepositGreen, contentColor = PureWhite)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (currentLanguage == AppLanguage.BN) "সদস্য হিসেবে প্রবেশ করুন" else "Log In as Member",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        // Sign Up / Create Mess
                        OutlinedTextField(
                            value = signUpMessName,
                            onValueChange = { signUpMessName = it },
                            label = { Text(if (currentLanguage == AppLanguage.BN) "মেসের নাম *" else "Mess Name *") },
                            placeholder = { Text(if (currentLanguage == AppLanguage.BN) "যেমন: শান্তি নিবাস মেস" else "e.g. Green Valley Mess") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_mess_name")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = signUpManagerName,
                            onValueChange = { signUpManagerName = it },
                            label = { Text(if (currentLanguage == AppLanguage.BN) "ম্যানেজারের পুরো নাম *" else "Manager Full Name *") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_manager_name")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = signUpPhone,
                            onValueChange = { signUpPhone = it },
                            label = { Text(if (currentLanguage == AppLanguage.BN) "ম্যানেজারের মোবাইল নম্বর *" else "Manager Mobile Number *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_phone")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = signUpPin,
                            onValueChange = { signUpPin = it },
                            label = { Text(if (currentLanguage == AppLanguage.BN) "৪-সংখ্যার সিকিউরিটি পিন *" else "4-digit Security PIN *") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_pin")
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (signUpMessName.isNotBlank() && signUpManagerName.isNotBlank() && signUpPhone.isNotBlank()) {
                                    onCreateMess(signUpMessName.trim(), signUpManagerName.trim(), signUpPhone.trim(), signUpPin.ifBlank { "1234" })
                                } else {
                                    Toast.makeText(context, "অনুগ্রহ করে সকল তথ্য পূরণ করুন", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_create_mess_account"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite)
                        ) {
                            Icon(Icons.Default.AppRegistration, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.BN) "মেস অ্যাকাউন্ট তৈরি করুন" else "Create Mess Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
