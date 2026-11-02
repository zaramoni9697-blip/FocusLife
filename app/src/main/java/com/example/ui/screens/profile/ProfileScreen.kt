package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfileEntity
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.navigation.MainTab
import com.example.ui.theme.*
import com.example.ui.viewmodel.ProductivityStats

@Composable
fun ProfileScreen(
    currentLanguage: Language,
    userProfile: UserProfileEntity?,
    stats: ProductivityStats,
    onUpdateProfile: (String, String, Int) -> Unit,
    onLogout: () -> Unit,
    onNavigateTab: (MainTab) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    val avatarEmoji = when (userProfile?.avatarId ?: 1) {
        1 -> "🚀"
        2 -> "⚡"
        3 -> "🔥"
        4 -> "🧘"
        5 -> "🧠"
        else -> "🌟"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = AppStrings.get("profile", currentLanguage),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Avatar Card
        item {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary.copy(alpha = 0.2f))
                    .border(2.dp, PurplePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = avatarEmoji, fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = userProfile?.fullName ?: "Productivity Achiever",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = userProfile?.email ?: "guest@focuslife.app",
                fontSize = 14.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (userProfile?.isGuest == true) SurfaceElevatedDark else GreenNeon.copy(alpha = 0.2f)
            ) {
                Text(
                    text = if (userProfile?.isGuest == true)
                        (if (currentLanguage == Language.BENGALI) "অফলাইন গেস্ট মোড" else "Offline Guest Mode")
                    else (if (currentLanguage == Language.BENGALI) "অ্যাকাউন্ট ভেরিফাইড" else "Verified Account"),
                    color = if (userProfile?.isGuest == true) AmberWarning else GreenNeon,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { showEditDialog = true },
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PurpleLight)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = PurpleLight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (currentLanguage == Language.BENGALI) "প্রোফাইল পরিবর্তন" else "Edit Profile",
                    color = PurpleLight,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(26.dp))
        }

        // Stats Overview Row
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${stats.completedTasks}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenNeon)
                        Text(text = "Tasks Done", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${stats.habitCompletionPercent}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                        Text(text = "Streak Rate", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stats.focusHoursFormatted, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BlueElectric)
                        Text(text = "Focus Hours", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Account Options
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.Settings,
                        label = AppStrings.get("settings_title", currentLanguage),
                        onClick = { onNavigateTab(MainTab.SETTINGS) }
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.BarChart,
                        label = AppStrings.get("nav_stats", currentLanguage),
                        onClick = { onNavigateTab(MainTab.STATISTICS) }
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.Notifications,
                        label = AppStrings.get("notifications_title", currentLanguage),
                        onClick = { onNavigateTab(MainTab.NOTIFICATIONS) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Logout Button
        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("profile_logout_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevatedDark)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = RedPriority, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.get("logout", currentLanguage),
                    color = RedPriority,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentLanguage = currentLanguage,
            initialName = userProfile?.fullName ?: "",
            initialEmail = userProfile?.email ?: "",
            initialAvatarId = userProfile?.avatarId ?: 1,
            onDismiss = { showEditDialog = false },
            onSave = { name, email, avatarId ->
                onUpdateProfile(name, email, avatarId)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ProfileMenuRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PurpleLight, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun EditProfileDialog(
    currentLanguage: Language,
    initialName: String,
    initialEmail: String,
    initialAvatarId: Int,
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var selectedAvatar by remember { mutableIntStateOf(initialAvatarId) }

    val emojis = listOf(1 to "🚀", 2 to "⚡", 3 to "🔥", 4 to "🧘", 5 to "🧠", 6 to "🌟")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = if (currentLanguage == Language.BENGALI) "প্রোফাইল তথ্য সম্পাদনা" else "Edit Profile Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Choose Avatar", fontSize = 12.sp, color = TextSecondaryDark)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(emojis.size) { i ->
                        val (id, emoji) = emojis[i]
                        val isSel = selectedAvatar == id
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSel) PurplePrimary else SurfaceElevatedDark)
                                .clickable { selectedAvatar = id },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, email, selectedAvatar) },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text(if (currentLanguage == Language.BENGALI) "সংরক্ষণ করুন" else "Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLanguage == Language.BENGALI) "বাতিল" else "Cancel", color = TextSecondaryDark)
            }
        }
    )
}
