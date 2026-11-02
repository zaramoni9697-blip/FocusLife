package com.example.ui.screens.pomodoro

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionEntity
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*
import com.example.ui.viewmodel.PomodoroMode
import com.example.ui.viewmodel.TimerState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PomodoroScreen(
    currentLanguage: Language,
    timerState: TimerState,
    focusSessions: List<FocusSessionEntity>,
    onSetMode: (PomodoroMode) -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit
) {
    val totalSeconds = timerState.mode.defaultMinutes * 60
    val progress = (timerState.secondsRemaining.toFloat() / totalSeconds).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "timerProgress"
    )

    val minutes = timerState.secondsRemaining / 60
    val seconds = timerState.secondsRemaining % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    val modeColor = when (timerState.mode) {
        PomodoroMode.FOCUS -> PurplePrimary
        PomodoroMode.SHORT_BREAK -> BlueElectric
        PomodoroMode.LONG_BREAK -> GreenNeon
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp)
            .testTag("pomodoro_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        item {
            Text(
                text = if (currentLanguage == Language.BENGALI) "পোমোডোরো ফোকাস টাইমার" else "Pomodoro Focus Timer",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentLanguage == Language.BENGALI) "২৫ মিনিট গভীর মনোযোগ • ৫ মিনিট বিশ্রাম" else "25m Deep Work • 5m Rest • Max Output",
                fontSize = 13.sp,
                color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Mode Switcher Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PomodoroMode.values().forEach { mode ->
                    val isSelected = timerState.mode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) modeColor else Color.Transparent)
                            .clickable { onSetMode(mode) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentLanguage == Language.BENGALI) mode.labelBn else mode.labelEn,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondaryDark
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }

        // Big Circular Countdown Ring
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                Canvas(modifier = Modifier.size(230.dp)) {
                    // Background track
                    drawArc(
                        color = SurfaceDark,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Progress arc
                    drawArc(
                        brush = Brush.sweepGradient(listOf(modeColor, modeColor.copy(alpha = 0.5f), modeColor)),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = modeColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (timerState.isRunning) "IN PROGRESS" else "READY",
                            color = modeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
        }

        // Controls (Start / Pause / Reset)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                IconButton(
                    onClick = onResetTimer,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevatedDark)
                        .border(1.dp, BorderDark, CircleShape)
                        .testTag("pomodoro_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Main Play/Pause Button
                Button(
                    onClick = {
                        if (timerState.isRunning) onPauseTimer() else onStartTimer()
                    },
                    modifier = Modifier
                        .height(58.dp)
                        .width(180.dp)
                        .testTag("pomodoro_toggle_button"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = modeColor)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (timerState.isRunning)
                                AppStrings.get("timer_pause", currentLanguage)
                            else
                                AppStrings.get("timer_start", currentLanguage),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(26.dp))
        }

        // Session Counter Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🍅", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = AppStrings.get("session_count", currentLanguage),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (currentLanguage == Language.BENGALI) "প্রতি ৪ সেশনে দীর্ঘ বিরতি নিন" else "Take a long break every 4 sessions",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Text(
                        text = "${timerState.completedSessions}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleLight
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Recent Sessions History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = if (currentLanguage == Language.BENGALI) "সম্প্রতি সম্পন্ন ফোকাস সেশন" else "Recent Focus Sessions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        val recentSessions = focusSessions.take(4)
        if (recentSessions.isEmpty()) {
            item {
                Text(
                    text = if (currentLanguage == Language.BENGALI) "আজ এখনো কোনো সেশন সম্পন্ন হয়নি।" else "No focus sessions logged yet today.",
                    color = TextMutedDark,
                    fontSize = 13.sp
                )
            }
        } else {
            items(recentSessions, key = { it.id }) { session ->
                val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(session.completedAtEpochMs))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = BlueElectric, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${session.durationMinutes} mins • ${session.label}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(text = timeStr, color = TextSecondaryDark, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
