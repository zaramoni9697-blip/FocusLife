package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    currentLanguage: Language,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("forgot_password_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("forgot_password_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.get("reset_password", currentLanguage),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (isSubmitted) {
                // Success state
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = GreenNeon,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (currentLanguage == Language.BENGALI)
                                "পাসওয়ার্ড রিসেট লিংক পাঠানো হয়েছে!"
                            else "Password Reset Link Sent!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (currentLanguage == Language.BENGALI)
                                "$email ঠিকানায় একটি পাসওয়ার্ড রিসেট লিংক পাঠানো হয়েছে। আপনার ইনবক্স চেক করুন।"
                            else "We have sent password recovery instructions to $email. Please check your inbox.",
                            fontSize = 14.sp,
                            color = TextSecondaryDark,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text(
                        text = if (currentLanguage == Language.BENGALI) "লগ ইন-এ ফিরুন" else "Back to Login",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = if (currentLanguage == Language.BENGALI)
                        "আপনার নিবন্ধিত ইমেইল ঠিকানা দিন। আমরা আপনাকে পাসওয়ার্ড রিসেট করার লিংক পাঠাব।"
                    else "Enter your registered email address and we'll send you instructions to reset your password.",
                    fontSize = 14.sp,
                    color = TextSecondaryDark,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                        .testTag("forgot_password_email_input")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = RedPriority,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || !email.contains("@")) {
                            errorMessage = if (currentLanguage == Language.BENGALI)
                                "অনুগ্রহ করে সঠিক ইমেইল ঠিকানা দিন" else "Please enter a valid email address"
                        } else {
                            isSubmitted = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("forgot_password_submit_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text(
                        text = AppStrings.get("reset_password", currentLanguage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
