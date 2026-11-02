package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    currentLanguage: Language,
    isDarkTheme: Boolean,
    notificationsEnabled: Boolean,
    hapticsEnabled: Boolean,
    onSetLanguage: (Language) -> Unit,
    onSetDarkTheme: (Boolean) -> Unit,
    onExportData: suspend () -> String
) {
    val coroutineScope = rememberCoroutineScope()
    var exportResult by remember { mutableStateOf<String?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) BackgroundDark else BackgroundLight)
            .padding(horizontal = 20.dp)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = AppStrings.get("settings_title", currentLanguage),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else TextPrimaryLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentLanguage == Language.BENGALI) "অ্যাপের পছন্দসমূহ ও কনফিগারেশন" else "Preferences, appearance, language & backup",
                fontSize = 13.sp,
                color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Language
        item {
            SettingsSectionHeader(title = AppStrings.get("language_setting", currentLanguage), isDark = isDarkTheme)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) SurfaceDark else SurfaceLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isDarkTheme) BorderDark else BorderLight, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSetLanguage(Language.ENGLISH) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == Language.ENGLISH,
                            onClick = { onSetLanguage(Language.ENGLISH) },
                            colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "English", fontWeight = FontWeight.Bold, color = if (isDarkTheme) Color.White else TextPrimaryLight)
                            Text(text = "Default International", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                    }

                    HorizontalDivider(color = if (isDarkTheme) BorderDark else BorderLight)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSetLanguage(Language.BENGALI) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == Language.BENGALI,
                            onClick = { onSetLanguage(Language.BENGALI) },
                            colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "বাংলা (Bengali)", fontWeight = FontWeight.Bold, color = if (isDarkTheme) Color.White else TextPrimaryLight)
                            Text(text = "বাংলা ভাষা ও অনুবাদ", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Appearance
        item {
            SettingsSectionHeader(title = AppStrings.get("theme_setting", currentLanguage), isDark = isDarkTheme)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) SurfaceDark else SurfaceLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isDarkTheme) BorderDark else BorderLight, RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (isDarkTheme) AppStrings.get("theme_dark", currentLanguage) else AppStrings.get("theme_light", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else TextPrimaryLight
                            )
                            Text(
                                text = if (isDarkTheme) "Deep charcoal & neon accents" else "Clean light aesthetic",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onSetDarkTheme,
                        colors = SwitchDefaults.colors(checkedThumbColor = PurplePrimary)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Data Backup & Export
        item {
            SettingsSectionHeader(title = AppStrings.get("backup_restore", currentLanguage), isDark = isDarkTheme)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) SurfaceDark else SurfaceLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isDarkTheme) BorderDark else BorderLight, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (currentLanguage == Language.BENGALI)
                            "আপনার সমস্ত কাজ, অভ্যাস এবং নোট অফলাইনে নিরাপদ থাকে।"
                        else "All your tasks, habits, and notes are securely stored on your local device.",
                        fontSize = 13.sp,
                        color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                exportResult = onExportData()
                                showExportDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_export_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("export_json", currentLanguage),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: About & Privacy
        item {
            SettingsSectionHeader(title = AppStrings.get("about_focuslife", currentLanguage), isDark = isDarkTheme)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) SurfaceDark else SurfaceLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isDarkTheme) BorderDark else BorderLight, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎯", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "FocusLife v1.0", fontWeight = FontWeight.Bold, color = if (isDarkTheme) Color.White else TextPrimaryLight)
                            Text(text = AppStrings.get("tagline", currentLanguage), fontSize = 12.sp, color = BlueElectric)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = AppStrings.get("privacy_desc", currentLanguage),
                        fontSize = 12.sp,
                        color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    if (showExportDialog && exportResult != null) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = SurfaceDark,
            title = {
                Text(
                    text = AppStrings.get("data_exported", currentLanguage),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenNeon
                )
            },
            text = {
                Column {
                    Text(
                        text = if (currentLanguage == Language.BENGALI)
                            "আপনার ডাটা সফলভাবে JSON ফরম্যাটে তৈরি হয়েছে:"
                        else "Your data is packaged into structured JSON:",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BackgroundDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Text(
                            text = exportResult!!,
                            color = Color(0xFF67E8F9),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String, isDark: Boolean) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) PurpleLight else PurplePrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
