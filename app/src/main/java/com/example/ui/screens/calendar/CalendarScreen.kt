package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.CalendarEventEntity
import com.example.data.model.TaskEntity
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    currentLanguage: Language,
    events: List<CalendarEventEntity>,
    tasks: List<TaskEntity>,
    onCreateEvent: (title: String, desc: String, startMs: Long, endMs: Long, loc: String, colorHex: String) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit
) {
    var calendarMonthOffset by remember { mutableIntStateOf(0) }
    var selectedDayCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var showAddEventDialog by remember { mutableStateOf(false) }

    val displayedCal = remember(calendarMonthOffset) {
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, calendarMonthOffset)
        c.set(Calendar.DAY_OF_MONTH, 1)
        c
    }

    val monthTitle = remember(displayedCal, currentLanguage) {
        val pattern = if (currentLanguage == Language.BENGALI) "MMMM yyyy" else "MMMM yyyy"
        SimpleDateFormat(pattern, Locale.getDefault()).format(displayedCal.time)
    }

    val daysInMonth = displayedCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (displayedCal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7 // Monday = 0

    val todayCalendar = remember { Calendar.getInstance() }
    val isCurrentMonthToday = todayCalendar.get(Calendar.YEAR) == displayedCal.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.MONTH) == displayedCal.get(Calendar.MONTH)

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEventDialog = true },
                containerColor = BlueElectric,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("calendar_add_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .testTag("calendar_screen"),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.get("calendar_title", currentLanguage),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceElevatedDark,
                        modifier = Modifier.clickable {
                            calendarMonthOffset = 0
                            selectedDayCalendar = Calendar.getInstance()
                        }
                    ) {
                        Text(
                            text = AppStrings.get("today_indicator", currentLanguage),
                            color = BlueElectric,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Month Navigation Row
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderDark, RoundedCornerShape(22.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { calendarMonthOffset-- }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = Color.White)
                            }
                            Text(
                                text = monthTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(onClick = { calendarMonthOffset++ }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Days of week header (M, T, W, T, F, S, S)
                        val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            dayNames.forEach { name ->
                                Text(
                                    text = name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMutedDark,
                                    modifier = Modifier.width(36.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Grid of days
                        val totalSlots = firstDayOfWeek + daysInMonth
                        val rows = (totalSlots + 6) / 7

                        for (r in 0 until rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (col in 0..6) {
                                    val index = r * 7 + col
                                    val dayNum = index - firstDayOfWeek + 1
                                    if (dayNum in 1..daysInMonth) {
                                        val isToday = isCurrentMonthToday && todayCalendar.get(Calendar.DAY_OF_MONTH) == dayNum
                                        val isSelected = selectedDayCalendar.get(Calendar.YEAR) == displayedCal.get(Calendar.YEAR) &&
                                                selectedDayCalendar.get(Calendar.MONTH) == displayedCal.get(Calendar.MONTH) &&
                                                selectedDayCalendar.get(Calendar.DAY_OF_MONTH) == dayNum

                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isSelected -> PurplePrimary
                                                        isToday -> BlueElectric.copy(alpha = 0.25f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .clickable {
                                                    val newCal = Calendar.getInstance()
                                                    newCal.set(
                                                        displayedCal.get(Calendar.YEAR),
                                                        displayedCal.get(Calendar.MONTH),
                                                        dayNum
                                                    )
                                                    selectedDayCalendar = newCal
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else if (isToday) BlueElectric else Color.White
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(36.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Selected Day Header
            item {
                val selectedDayFmt = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(selectedDayCalendar.time)
                Text(
                    text = "Schedule • $selectedDayFmt",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Events List
            if (events.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.EventAvailable, contentDescription = null, tint = BlueElectric, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (currentLanguage == Language.BENGALI) "এই দিনে কোনো ইভেন্ট নির্ধারিত নেই।" else "No scheduled events for this date.",
                                color = TextSecondaryDark,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(events, key = { it.id }) { event ->
                    EventCard(event = event, onDelete = { onDeleteEvent(event) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showAddEventDialog) {
        AddEventDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showAddEventDialog = false },
            onAdd = { title, desc, startMs, endMs, loc, col ->
                onCreateEvent(title, desc, startMs, endMs, loc, col)
                showAddEventDialog = false
            }
        )
    }
}

@Composable
fun EventCard(event: CalendarEventEntity, onDelete: () -> Unit) {
    val eventColor = try {
        Color(android.graphics.Color.parseColor(event.colorHex))
    } catch (_: Exception) {
        BlueElectric
    }

    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val startStr = timeFormat.format(Date(event.startEpochMs))
    val endStr = timeFormat.format(Date(event.endEpochMs))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(eventColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (event.description.isNotBlank()) {
                    Text(
                        text = event.description,
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$startStr - $endStr",
                        fontSize = 11.sp,
                        color = eventColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (event.location.isNotBlank()) {
                        Text(
                            text = " • 📍 ${event.location}",
                            fontSize = 11.sp,
                            color = TextMutedDark
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedPriority, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun AddEventDialog(
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onAdd: (title: String, desc: String, startMs: Long, endMs: Long, loc: String, colorHex: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var colorHex by remember { mutableStateOf("#38BDF8") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = AppStrings.get("add_event", currentLanguage),
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
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(AppStrings.get("event_title_hint", currentLanguage)) },
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
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(if (currentLanguage == Language.BENGALI) "বিবরণ" else "Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (currentLanguage == Language.BENGALI) "স্থান বা লিংক" else "Location or Virtual Link") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val now = System.currentTimeMillis()
                        onAdd(title, desc, now, now + 3600000L, location, colorHex)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueElectric)
            ) {
                Text(if (currentLanguage == Language.BENGALI) "যোগ করুন" else "Add Event", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLanguage == Language.BENGALI) "বাতিল" else "Cancel", color = TextSecondaryDark)
            }
        }
    )
}
