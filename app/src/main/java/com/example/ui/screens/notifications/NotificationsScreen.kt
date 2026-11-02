package com.example.ui.screens.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.core.content.ContextCompat
import com.example.data.model.NotificationEntity
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    currentLanguage: Language,
    notifications: List<NotificationEntity>,
    onSendTestNotification: () -> Unit,
    onMarkAllRead: () -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    var tasksChannelEnabled by remember { mutableStateOf(true) }
    var habitsChannelEnabled by remember { mutableStateOf(true) }
    var pomodoroChannelEnabled by remember { mutableStateOf(true) }
    var planningChannelEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp)
            .testTag("notifications_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Text(
                text = AppStrings.get("notifications_title", currentLanguage),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentLanguage == Language.BENGALI) "নেটিভ অ্যান্ড্রয়েড লক স্ক্রিন ও ব্যানার নোটিফিকেশন" else "Native Android lock screen & banner alert channels",
                fontSize = 13.sp,
                color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Permission Card (If not granted)
        if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AmberWarning, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = AmberWarning)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Permission Required",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = AppStrings.get("notif_permission_request", currentLanguage),
                            fontSize = 13.sp,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberWarning),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(AppStrings.get("grant_permission", currentLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Test Notification Action Card
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentLanguage == Language.BENGALI) "নোটিফিকেশন পরীক্ষা করুন" else "Test Native Alert",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (currentLanguage == Language.BENGALI) "একটি রিয়েল অ্যান্ড্রয়েড নোটিফিকেশন পাঠাবে" else "Sends a live heads-up & lock screen notification",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }

                    Button(
                        onClick = onSendTestNotification,
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("notifications_send_test_button")
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BENGALI) "পাঠান" else "Send Alert",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Notification Channels Config
        item {
            Text(
                text = if (currentLanguage == Language.BENGALI) "নোটিফিকেশন চ্যানেল ও ক্যাটাগরি" else "Notification Channels",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(10.dp))

            ChannelRow(
                title = AppStrings.get("channel_tasks", currentLanguage),
                desc = "High priority reminders with 'Mark Done' action",
                icon = Icons.Default.TaskAlt,
                color = GreenNeon,
                isEnabled = tasksChannelEnabled,
                onToggle = { tasksChannelEnabled = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChannelRow(
                title = AppStrings.get("channel_habits", currentLanguage),
                desc = "Streak alerts and daily completion reminders",
                icon = Icons.Default.LocalFireDepartment,
                color = AmberWarning,
                isEnabled = habitsChannelEnabled,
                onToggle = { habitsChannelEnabled = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChannelRow(
                title = AppStrings.get("channel_pomodoro", currentLanguage),
                desc = "Focus completion and break chime alerts",
                icon = Icons.Default.Timer,
                color = BlueElectric,
                isEnabled = pomodoroChannelEnabled,
                onToggle = { pomodoroChannelEnabled = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChannelRow(
                title = AppStrings.get("channel_planning", currentLanguage),
                desc = "Morning plan and evening productivity review",
                icon = Icons.Default.AutoAwesome,
                color = PurplePrimary,
                isEnabled = planningChannelEnabled,
                onToggle = { planningChannelEnabled = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Notification History Log
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLanguage == Language.BENGALI) "নোটিফিকেশন ইতিহাস" else "Notification History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (notifications.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text(if (currentLanguage == Language.BENGALI) "সব মুছুন" else "Clear All", color = RedPriority, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (notifications.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (currentLanguage == Language.BENGALI) "কোনো নোটিফিকেশন নেই।" else "No notification history.",
                            color = TextSecondaryDark,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(notifications, key = { it.id }) { item ->
                NotificationHistoryRow(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ChannelRow(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = desc, fontSize = 11.sp, color = TextSecondaryDark)
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedThumbColor = color)
            )
        }
    }
}

@Composable
fun NotificationHistoryRow(item: NotificationEntity) {
    val timeStr = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault()).format(Date(item.timestampEpochMs))
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (item.isRead) TextMutedDark else PurplePrimary)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = item.body, fontSize = 12.sp, color = TextSecondaryDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = timeStr, fontSize = 10.sp, color = TextMutedDark)
            }
        }
    }
}
