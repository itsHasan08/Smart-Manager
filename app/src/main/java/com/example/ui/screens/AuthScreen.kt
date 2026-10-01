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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Member
import com.example.data.model.MessProfile
import com.example.ui.theme.*

enum class AuthStage {
    ACCOUNT_AUTH, // Step 1: Login or Sign Up
    MESS_GATE     // Step 2: Create Mess or Enter Mess
}

enum class AccountMode {
    LOGIN,
    SIGN_UP
}

enum class MessAction {
    CREATE_MESS,
    ENTER_MESS
}

@Composable
fun AuthScreen(
    profile: MessProfile?,
    members: List<Member>,
    currentLanguage: AppLanguage,
    onLoginSuccess: (phone: String, pass: String) -> Unit,
    onRegisterSuccess: (name: String, phone: String, pass: String) -> Unit,
    onGoogleAuth: () -> Unit,
    onCreateMess: (messName: String, address: String, phone: String, photoUri: String?) -> Unit,
    onJoinMess: (userId: String, pass: String) -> Boolean
) {
    val context = LocalContext.current

    // If mess already exists and user wants to switch / login, stay on stage
    var currentStage by remember { mutableStateOf(AuthStage.ACCOUNT_AUTH) }
    var accountMode by remember { mutableStateOf(AccountMode.LOGIN) }
    var messAction by remember { mutableStateOf(MessAction.CREATE_MESS) }

    // Step 1: Login / Sign Up Fields
    var authPhone by remember { mutableStateOf("") }
    var authPassword by remember { mutableStateOf("") }
    var authName by remember { mutableStateOf("") }
    var authConfirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Step 2: Create Mess Fields
    var messName by remember { mutableStateOf("") }
    var messAddress by remember { mutableStateOf("") }
    var messPhone by remember { mutableStateOf(authPhone.ifBlank { profile?.managerPhone ?: "" }) }
    var selectedPhotoPreset by remember { mutableStateOf("apartment") }

    // Step 2: Enter Mess Fields
    var enterUserId by remember { mutableStateOf("") }
    var enterPassword by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("auth_screen"),
        color = PureWhite
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
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Modern App Logo Badge
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(BrandPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (currentStage == AuthStage.ACCOUNT_AUTH) Icons.Default.LockPerson else Icons.Default.Apartment,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (currentStage == AuthStage.ACCOUNT_AUTH) {
                            if (accountMode == AccountMode.LOGIN) "স্মার্ট মেসে লগইন করুন" else "নতুন অ্যাকাউন্ট তৈরি করুন"
                        } else {
                            if (messAction == MessAction.CREATE_MESS) "নতুন মেস তৈরি করুন" else "মেসে প্রবেশ করুন"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 21.sp
                        ),
                        color = DarkText
                    )

                    Text(
                        text = if (currentStage == AuthStage.ACCOUNT_AUTH)
                            "সহজ, স্বচ্ছ ও স্মার্ট মেস হিসাব ব্যবস্থা"
                        else
                            "আপনার মেস বেছে নিন অথবা আইডিসহ প্রবেশ করুন",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ---------------- STEP 1: ACCOUNT AUTH (LOGIN OR SIGN UP) ----------------
                    if (currentStage == AuthStage.ACCOUNT_AUTH) {
                        // Clean Tab Switcher (লগইন / অ্যাকাউন্ট খুলুন)
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
                                    .clickable { accountMode = AccountMode.LOGIN },
                                shape = RoundedCornerShape(10.dp),
                                color = if (accountMode == AccountMode.LOGIN) PureWhite else SurfaceGray,
                                border = if (accountMode == AccountMode.LOGIN) BorderStroke(1.dp, BorderGray) else null
                            ) {
                                Text(
                                    text = "লগইন করুন",
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (accountMode == AccountMode.LOGIN) BrandPrimary else GrayText,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { accountMode = AccountMode.SIGN_UP },
                                shape = RoundedCornerShape(10.dp),
                                color = if (accountMode == AccountMode.SIGN_UP) PureWhite else SurfaceGray,
                                border = if (accountMode == AccountMode.SIGN_UP) BorderStroke(1.dp, BorderGray) else null
                            ) {
                                Text(
                                    text = "অ্যাকাউন্ট খুলুন",
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (accountMode == AccountMode.SIGN_UP) BrandPrimary else GrayText,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (accountMode == AccountMode.SIGN_UP) {
                            // Name Field
                            OutlinedTextField(
                                value = authName,
                                onValueChange = { authName = it },
                                label = { Text("আপনার পুরো নাম *") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = BorderGray
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("signup_name_field")
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Phone Number
                        OutlinedTextField(
                            value = authPhone,
                            onValueChange = { authPhone = it },
                            label = { Text("মোবাইল নম্বর *") },
                            placeholder = { Text("017XXXXXXXX") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPrimary,
                                unfocusedBorderColor = BorderGray
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_phone_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password
                        OutlinedTextField(
                            value = authPassword,
                            onValueChange = { authPassword = it },
                            label = { Text("পাসওয়ার্ড *") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = GrayText
                                    )
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPrimary,
                                unfocusedBorderColor = BorderGray
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_password_field")
                        )

                        if (accountMode == AccountMode.SIGN_UP) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // Confirm Password
                            OutlinedTextField(
                                value = authConfirmPassword,
                                onValueChange = { authConfirmPassword = it },
                                label = { Text("কনফার্ম পাসওয়ার্ড *") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = BorderGray
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("auth_confirm_password_field")
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (accountMode == AccountMode.LOGIN) {
                                    if (authPhone.isNotBlank() && authPassword.isNotBlank()) {
                                        onLoginSuccess(authPhone.trim(), authPassword.trim())
                                        currentStage = AuthStage.MESS_GATE
                                    } else {
                                        Toast.makeText(context, "অনুগ্রহ করে নম্বর ও পাসওয়ার্ড দিন", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    if (authName.isBlank() || authPhone.isBlank() || authPassword.isBlank()) {
                                        Toast.makeText(context, "সকল তথ্য পূরণ করুন", Toast.LENGTH_SHORT).show()
                                    } else if (authPassword != authConfirmPassword) {
                                        Toast.makeText(context, "পাসওয়ার্ড দুটি মেলেনি", Toast.LENGTH_SHORT).show()
                                    } else {
                                        onRegisterSuccess(authName.trim(), authPhone.trim(), authPassword.trim())
                                        currentStage = AuthStage.MESS_GATE
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_auth_submit"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite)
                        ) {
                            Icon(
                                imageVector = if (accountMode == AccountMode.LOGIN) Icons.Default.Login else Icons.Default.PersonAdd,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (accountMode == AccountMode.LOGIN) "লগইন করুন" else "অ্যাকাউন্ট তৈরি করুন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Divider with OR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGray)
                            Text(" অথবা ", style = MaterialTheme.typography.labelSmall, color = GrayText)
                            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGray)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Google Sign-In Button
                        OutlinedButton(
                            onClick = {
                                onGoogleAuth()
                                currentStage = AuthStage.MESS_GATE
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_google_auth"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = BrandPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "গুগল দিয়ে প্রবেশ করুন (Google)",
                                fontWeight = FontWeight.Bold,
                                color = DarkText,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // ---------------- STEP 2: MESS GATE (CREATE MESS OR ENTER MESS) ----------------
                    else {
                        // Switch between Create Mess and Enter Mess
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
                                    .clickable { messAction = MessAction.CREATE_MESS },
                                shape = RoundedCornerShape(10.dp),
                                color = if (messAction == MessAction.CREATE_MESS) PureWhite else SurfaceGray,
                                border = if (messAction == MessAction.CREATE_MESS) BorderStroke(1.dp, BorderGray) else null
                            ) {
                                Text(
                                    text = "মেস তৈরি করুন",
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (messAction == MessAction.CREATE_MESS) BrandPrimary else GrayText,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { messAction = MessAction.ENTER_MESS },
                                shape = RoundedCornerShape(10.dp),
                                color = if (messAction == MessAction.ENTER_MESS) PureWhite else SurfaceGray,
                                border = if (messAction == MessAction.ENTER_MESS) BorderStroke(1.dp, BorderGray) else null
                            ) {
                                Text(
                                    text = "মেসে প্রবেশ করুন",
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (messAction == MessAction.ENTER_MESS) BrandPrimary else GrayText,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (messAction == MessAction.CREATE_MESS) {
                            // CREATE MESS OPTION
                            OutlinedTextField(
                                value = messName,
                                onValueChange = { messName = it },
                                label = { Text("মেসের নাম *") },
                                placeholder = { Text("যেমন: শান্তিনিকেতন মেস") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = BorderGray
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("input_create_mess_name")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = messAddress,
                                onValueChange = { messAddress = it },
                                label = { Text("মেসের ঠিকানা *") },
                                placeholder = { Text("বাড়ি নং, রোড, এলাকা, শহর") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = BorderGray
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("input_create_mess_address")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = messPhone,
                                onValueChange = { messPhone = it },
                                label = { Text("ম্যানেজারের ফোন নম্বর *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = BorderGray
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("input_create_mess_phone")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Optional Mess Photo Preset Selector
                            Text(
                                text = "মেসের প্রোফাইল ফটো (অপশনাল):",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = DarkText,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "apartment" to "ভবন",
                                    "home" to "বাড়ি",
                                    "location_city" to "হোস্টেল"
                                ).forEach { (key, label) ->
                                    FilterChip(
                                        selected = selectedPhotoPreset == key,
                                        onClick = { selectedPhotoPreset = key },
                                        label = { Text(label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrandPrimaryContainer,
                                            selectedLabelColor = BrandPrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (messName.isNotBlank() && messAddress.isNotBlank()) {
                                        onCreateMess(messName.trim(), messAddress.trim(), messPhone.trim(), selectedPhotoPreset)
                                        Toast.makeText(context, "মেস সফলভাবে তৈরি হয়েছে!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "মেসের নাম ও ঠিকানা পূরণ করুন", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_complete_create_mess"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = PureWhite)
                            ) {
                                Icon(Icons.Default.AddBusiness, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("মেস তৈরি সম্পন্ন করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        } else {
                            // ENTER MESS AS MEMBER OPTION
                            OutlinedTextField(
                                value = enterUserId,
                                onValueChange = { enterUserId = it },
                                label = { Text("ইউজার আইডি (User ID) *") },
                                placeholder = { Text("ম্যানেজার কর্তৃক প্রদত্ত আইডি (যেমন: USER-102)") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = BorderGray
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("input_enter_user_id")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = enterPassword,
                                onValueChange = { enterPassword = it },
                                label = { Text("পাসওয়ার্ড (Password) *") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = BorderGray
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("input_enter_password")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BrandPrimaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ম্যানেজার যখন আপনাকে সদস্য হিসেবে যুক্ত করবে, তখন একটি ইউজার আইডি ও পাসওয়ার্ড তৈরি হবে। সেটি দিয়ে এখানে প্রবেশ করুন।",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (enterUserId.isNotBlank() && enterPassword.isNotBlank()) {
                                        val success = onJoinMess(enterUserId.trim(), enterPassword.trim())
                                        if (!success) {
                                            Toast.makeText(context, "ইউজার আইডি বা পাসওয়ার্ড সঠিক নয়", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "ইউজার আইডি ও পাসওয়ার্ড লিখুন", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_enter_mess_submit"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DepositGreen, contentColor = PureWhite)
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("মেসে প্রবেশ করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        TextButton(onClick = { currentStage = AuthStage.ACCOUNT_AUTH }) {
                            Text("← লগইন পেজে ফিরে যান", color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
