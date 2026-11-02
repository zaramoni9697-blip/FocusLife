package com.example.ui.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.ai.ChatMessage
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(
    currentLanguage: Language,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    var promptInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val quickPrompts = if (currentLanguage == Language.BENGALI) {
        listOf(
            "আজকের দিনের একটি কার্যকরী রুটিন ও পরিকল্পনা তৈরি করে দাও",
            "কাজে অলসতা ও প্রোকাস্টিনেশন দূর করার উপায় কি?",
            "একটি বড় ৩ মাসের লক্ষ্যকে ছোট ছোট ধাপে কীভাবে ভাগ করব?",
            "প্রতিদিন পড়াশোনা ও বই পড়ার অভ্যাস বজায় রাখার টিপস"
        )
    } else {
        listOf(
            "Plan my high-productivity daily schedule for deep work",
            "How do I beat procrastination and maintain focus?",
            "Break down a 90-day major goal into weekly milestones",
            "Best science-backed techniques for consistent habit building"
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp)
            .testTag("ai_assistant_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary.copy(alpha = 0.2f))
                        .border(1.5.dp, PurplePrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Coach",
                        tint = PurplePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = AppStrings.get("ai_title", currentLanguage),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Gemini Powered • EN/বাংলা",
                        fontSize = 11.sp,
                        color = GreenNeon
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Clear chat
                IconButton(onClick = onClearChat) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = TextSecondaryDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Suggested Prompt Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                    modifier = Modifier
                        .clickable { onSendMessage(prompt) }
                        .height(38.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            color = PurpleLight,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(message = message)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = PurplePrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (currentLanguage == Language.BENGALI) "এআই চিন্তা করছে..." else "AI is thinking...",
                            fontSize = 13.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 70.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = {
                    Text(
                        text = AppStrings.get("ai_prompt_hint", currentLanguage),
                        color = TextMutedDark,
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_prompt_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (promptInput.isNotBlank()) {
                        val text = promptInput
                        promptInput = ""
                        onSendMessage(text)
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary)
                    .testTag("ai_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary.copy(alpha = 0.2f))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PurpleLight, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isUser) 18.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) PurpleDark else SurfaceDark
            ),
            modifier = Modifier
                .widthIn(max = 300.dp)
                .border(
                    1.dp,
                    if (message.isUser) PurplePrimary else BorderDark,
                    RoundedCornerShape(18.dp)
                )
        ) {
            Text(
                text = message.text,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}
