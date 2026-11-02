package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    currentLanguage: Language,
    onToggleLanguage: () -> Unit,
    onLoginSuccess: (String, String) -> Unit,
    onContinueAsGuest: () -> Unit,
    onNavigateSignUp: () -> Unit,
    onNavigateForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("login_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Language selector top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceElevatedDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                        .clickable { onToggleLanguage() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = PurplePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentLanguage == Language.ENGLISH) "বাংলা" else "English",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Branding Header
            Text(
                text = AppStrings.get("welcome", currentLanguage),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = AppStrings.get("welcome_sub", currentLanguage),
                fontSize = 14.sp,
                color = TextSecondaryDark,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Email Field
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
                    .testTag("login_email_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
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
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = TextSecondaryDark
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                    .testTag("login_password_input")
            )

            // Forgot Password Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onNavigateForgotPassword) {
                    Text(
                        text = AppStrings.get("forgot_password", currentLanguage),
                        color = BlueElectric,
                        fontSize = 13.sp
                    )
                }
            }

            // Error display
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = RedPriority,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login Button
            Button(
                onClick = {
                    if (email.isBlank() || !email.contains("@")) {
                        errorMessage = if (currentLanguage == Language.BENGALI)
                            "সঠিক ইমেইল ঠিকানা দিন" else "Please enter a valid email address"
                    } else if (password.length < 6) {
                        errorMessage = if (currentLanguage == Language.BENGALI)
                            "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে" else "Password must be at least 6 characters"
                    } else {
                        onLoginSuccess(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text(
                    text = AppStrings.get("login", currentLanguage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guest Mode Option (Offline First)
            OutlinedButton(
                onClick = onContinueAsGuest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_guest_button"),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(BlueElectric, GreenNeon))
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = GreenNeon,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("guest_mode", currentLanguage),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigate to Sign Up
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = if (currentLanguage == Language.BENGALI) "অ্যাকাউন্ট নেই? " else "Don't have an account? ",
                    color = TextSecondaryDark,
                    fontSize = 14.sp
                )
                Text(
                    text = AppStrings.get("signup", currentLanguage),
                    color = PurpleLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onNavigateSignUp() }
                        .padding(4.dp)
                        .testTag("login_navigate_signup")
                )
            }
        }
    }
}
