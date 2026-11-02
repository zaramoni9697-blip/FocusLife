package com.example.ui.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteEntity
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*

@Composable
fun NotesScreen(
    currentLanguage: Language,
    notes: List<NoteEntity>,
    onSaveNote: (id: Long, title: String, content: String, colorHex: String, isPinned: Boolean, tag: String) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    onTogglePin: (NoteEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<NoteEntity?>(null) }

    val filteredNotes = notes.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true) ||
                it.tag.contains(searchQuery, ignoreCase = true)
    }

    val pinnedNotes = filteredNotes.filter { it.isPinned }
    val otherNotes = filteredNotes.filter { !it.isPinned }

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = PurplePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("notes_add_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .testTag("notes_screen")
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.get("notes_title", currentLanguage),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceElevatedDark
                ) {
                    Text(
                        text = "${notes.size} Notes",
                        color = PurpleLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(AppStrings.get("search_notes", currentLanguage), color = TextMutedDark) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondaryDark)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NoteAlt,
                            contentDescription = null,
                            tint = PurpleLight,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (currentLanguage == Language.BENGALI) "কোনো নোট পাওয়া যায়নি" else "No notes found",
                            color = TextSecondaryDark,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pinned Section
                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            Text(
                                text = "📌 ${if (currentLanguage == Language.BENGALI) "পিন করা নোট" else "Pinned Notes"}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        items(pinnedNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onEdit = { editingNote = note },
                                onDelete = { onDeleteNote(note) },
                                onTogglePin = { onTogglePin(note) }
                            )
                        }
                    }

                    // Other Notes Section
                    if (otherNotes.isNotEmpty()) {
                        if (pinnedNotes.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (currentLanguage == Language.BENGALI) "অন্যান্য নোট" else "All Notes",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondaryDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                        items(otherNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onEdit = { editingNote = note },
                                onDelete = { onDeleteNote(note) },
                                onTogglePin = { onTogglePin(note) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog || editingNote != null) {
        val noteToEdit = editingNote
        NoteEditDialog(
            currentLanguage = currentLanguage,
            initialNote = noteToEdit,
            onDismiss = {
                showCreateDialog = false
                editingNote = null
            },
            onSave = { title, content, colorHex, isPinned, tag ->
                onSaveNote(noteToEdit?.id ?: 0L, title, content, colorHex, isPinned, tag)
                showCreateDialog = false
                editingNote = null
            }
        )
    }
}

@Composable
fun NoteCard(
    note: NoteEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit
) {
    val noteColor = try {
        Color(android.graphics.Color.parseColor(note.colorHex))
    } catch (_: Exception) {
        PurplePrimary
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
            .clickable { onEdit() }
            .testTag("note_item_${note.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = noteColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = note.tag,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = noteColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onTogglePin, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (note.isPinned) Icons.Default.PushPin else Icons.Default.PinDrop,
                            contentDescription = "Pin Note",
                            tint = if (note.isPinned) AmberWarning else TextMutedDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Note",
                            tint = RedPriority,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.content,
                fontSize = 13.sp,
                color = TextSecondaryDark,
                lineHeight = 18.sp,
                maxLines = 4
            )
        }
    }
}

@Composable
fun NoteEditDialog(
    currentLanguage: Language,
    initialNote: NoteEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, colorHex: String, isPinned: Boolean, tag: String) -> Unit
) {
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var content by remember { mutableStateOf(initialNote?.content ?: "") }
    var tag by remember { mutableStateOf(initialNote?.tag ?: "Ideas") }
    var colorHex by remember { mutableStateOf(initialNote?.colorHex ?: "#8B5CF6") }
    var isPinned by remember { mutableStateOf(initialNote?.isPinned ?: false) }

    val colorList = listOf(
        "#8B5CF6", "#38BDF8", "#10B981", "#F59E0B", "#F43F5E"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = if (initialNote == null)
                    AppStrings.get("new_note", currentLanguage)
                else (if (currentLanguage == Language.BENGALI) "নোট সম্পাদনা" else "Edit Note"),
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
                    label = { Text(AppStrings.get("note_title_hint", currentLanguage)) },
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
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(AppStrings.get("note_content_hint", currentLanguage)) },
                    minLines = 4,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Tag / Category") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Color Labels
                Text(text = "Color Label", fontSize = 12.sp, color = TextSecondaryDark)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colorList.forEach { hex ->
                        val isSel = colorHex == hex
                        val c = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(if (isSel) 2.dp else 0.dp, Color.White, CircleShape)
                                .clickable { colorHex = hex }
                        )
                    }
                }

                // Pin toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isPinned = !isPinned }
                ) {
                    Checkbox(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        colors = CheckboxDefaults.colors(checkedColor = AmberWarning)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.get("pin_note", currentLanguage),
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() || content.isNotBlank()) {
                        onSave(title, content, colorHex, isPinned, tag)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text(AppStrings.get("save_note", currentLanguage), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLanguage == Language.BENGALI) "বাতিল" else "Cancel", color = TextSecondaryDark)
            }
        }
    )
}
