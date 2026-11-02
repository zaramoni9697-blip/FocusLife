package com.example.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*
import com.example.ui.viewmodel.ProductivityStats

@Composable
fun StatisticsScreen(
    currentLanguage: Language,
    stats: ProductivityStats
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp)
            .testTag("statistics_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = if (currentLanguage == Language.BENGALI) "উৎপাদনশীলতা ও অ্যানালিটিক্স" else "Productivity Analytics",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentLanguage == Language.BENGALI) "আপনার কাজের অগ্রগতি ও ধারাবাহিকতার বিস্তারিত চিত্র" else "Visual insights on your habits, focus time, and tasks",
                fontSize = 13.sp,
                color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Weekly Output Bar Chart
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderDark, RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (currentLanguage == Language.BENGALI) "সাপ্তাহিক সম্পন্ন কাজ" else "Weekly Task Completion",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val weekData = listOf(
                        "Mon" to 4,
                        "Tue" to 7,
                        "Wed" to 5,
                        "Thu" to 8,
                        "Fri" to 6,
                        "Sat" to 3,
                        "Sun" to (stats.completedTasks.coerceAtLeast(4))
                    )
                    val maxVal = 10

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weekData.forEach { (day, count) ->
                            val heightFraction = (count.toFloat() / maxVal).coerceIn(0.1f, 1f)
                            val isToday = day == "Sun"

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = "$count",
                                    fontSize = 11.sp,
                                    color = if (isToday) GreenNeon else TextSecondaryDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isToday) GreenNeon else PurplePrimary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    color = TextMutedDark
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3 Key Insight Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Tasks Rate",
                    value = if (stats.totalTasks > 0) "${((stats.completedTasks.toFloat() / stats.totalTasks) * 100).toInt()}%" else "100%",
                    icon = Icons.Default.CheckCircle,
                    color = GreenNeon,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Focus Time",
                    value = stats.focusHoursFormatted,
                    icon = Icons.Default.Timer,
                    color = BlueElectric,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Habits Streak",
                    value = "${stats.habitCompletionPercent}%",
                    icon = Icons.Default.LocalFireDepartment,
                    color = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Achievements & Badges
        item {
            Text(
                text = if (currentLanguage == Language.BENGALI) "সাফল্য ও ব্যাজ (Achievements)" else "Achievements & Milestones",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BadgeRow(
                    emoji = "🏆",
                    title = if (currentLanguage == Language.BENGALI) "ডিপ ওয়ার্ক মাস্টার" else "Deep Work Master",
                    desc = if (currentLanguage == Language.BENGALI) "১০টির বেশি পোমোডোরো সেশন সম্পন্ন করেছেন" else "Completed 10+ Pomodoro focus sessions",
                    isUnlocked = true
                )
                BadgeRow(
                    emoji = "🔥",
                    title = if (currentLanguage == Language.BENGALI) "৭ দিনের স্ট্রিক চ্যাম্পিয়ন" else "7-Day Streak Legend",
                    desc = if (currentLanguage == Language.BENGALI) "এক সপ্তাহ টানা দৈনিক অভ্যাস বজায় রেখেছেন" else "Maintained all daily habits for a full week",
                    isUnlocked = stats.habitCompletionPercent >= 50
                )
                BadgeRow(
                    emoji = "🎯",
                    title = if (currentLanguage == Language.BENGALI) "লক্ষ্যভেদী যোদ্ধা" else "Goal Crusher",
                    desc = if (currentLanguage == Language.BENGALI) "উচ্চ অগ্রাধিকারের সব কাজ সময়মত সম্পন্ন করেছেন" else "Completed all High-Priority tasks before due date",
                    isUnlocked = stats.completedTasks > 0
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = modifier.border(1.dp, BorderDark, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = title, fontSize = 11.sp, color = TextSecondaryDark)
        }
    }
}

@Composable
fun BadgeRow(emoji: String, title: String, desc: String, isUnlocked: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isUnlocked) PurpleLight.copy(alpha = 0.5f) else BorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) PurplePrimary.copy(alpha = 0.2f) else SurfaceElevatedDark),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isUnlocked) Color.White else TextMutedDark)
                Text(text = desc, fontSize = 12.sp, color = TextSecondaryDark)
            }
            if (isUnlocked) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GreenNeon.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "UNLOCKED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenNeon,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
