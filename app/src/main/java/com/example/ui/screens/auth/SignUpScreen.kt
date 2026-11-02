package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*

@Composable
fun SignUpScreen(
    currentLanguage: Language,
    onSignUpSuccess: (String, String, Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableIntStateOf(1) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val avatarList = listOf(
        1 to "🚀",
        2 to "⚡",
        3 to "🔥",
        4 to "🧘",
        5 to "🧠",
        6 to "🌟"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("signup_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("signup_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.get("signup", currentLanguage),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar Selection
            Text(
                text = if (currentLanguage == Language.BENGALI) "আপনার অ্যাভাটার নির্বাচন করুন" else "Choose Your Avatar",
                fontSize = 14.sp,
                color = TextSecondaryDark,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(avatarList.size) { index ->
                    val (id, emoji) = avatarList[index]
                    val isSelected = selectedAvatarId == id
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PurplePrimary else SurfaceElevatedDark)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) PurpleLight else BorderDark,
                                shape = CircleShape
                            )
                            .clickable { selectedAvatarId = id },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Full Name Input
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = null
                },
                label = { Text(AppStrings.get("full_name", currentLanguage)) },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = PurpleLight)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderDark,
                    focusedLabelColor = PurplePrimary,
                    unfocusedLabelColor = TextSecondaryDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_name_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = { Text(AppStrings.get("email", currentLanguage)) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = PurpleLight)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderDark,
                    focusedLabelColor = PurplePrimary,
                    unfocusedLabelColor = TextSecondaryDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_email_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text(AppStrings.get("password", currentLanguage)) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = PurpleLight)
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderDark,
                    focusedLabelColor = PurplePrimary,
                    unfocusedLabelColor = TextSecondaryDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_password_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Confirm Password Input
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text(AppStrings.get("confirm_password", currentLanguage)) },
                leadingIcon = {
                    Icon(Icons.Default.LockReset, contentDescription = null, tint = PurpleLight)
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderDark,
                    focusedLabelColor = PurplePrimary,
                    unfocusedLabelColor = TextSecondaryDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_confirm_password_input")
            )

            // Error Display
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = RedPriority,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        errorMessage = if (currentLanguage == Language.BENGALI)
                            "অনুগ্রহ করে আপনার নাম লিখুন" else "Please enter your name"
                    } else if (email.isBlank() || !email.contains("@")) {
                        errorMessage = if (currentLanguage == Language.BENGALI)
                            "সঠিক ইমেইল ঠিকানা দিন" else "Please enter a valid email address"
                    } else if (password.length < 6) {
                        errorMessage = if (currentLanguage == Language.BENGALI)
                            "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে" else "Password must be at least 6 characters"
                    } else if (password != confirmPassword) {
                        errorMessage = if (currentLanguage == Language.BENGALI)
                            "পাসওয়ার্ড দুটি মিলছে না" else "Passwords do not match"
                    } else {
                        onSignUpSuccess(fullName, email, selectedAvatarId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("signup_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text(
                    text = AppStrings.get("signup", currentLanguage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
