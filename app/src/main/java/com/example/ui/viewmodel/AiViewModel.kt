package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAssistantService
import com.example.ai.ChatMessage
import com.example.localization.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = AiAssistantService(application)

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Hello! I am your FocusLife AI Productivity Coach. I can help you plan your day, break down complex projects, prioritize tasks, and keep your habits strong. How can I help you today?\n\n(বাংলা এবং ইংরেজি উভয় ভাষায় আমাকে জিজ্ঞেস করতে পারেন!)",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(prompt: String, language: Language) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() || _isLoading.value) return

        val userMsg = ChatMessage(text = trimmed, isUser = true)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val responseText = aiService.getResponse(trimmed, language)
                val botMsg = ChatMessage(text = responseText, isUser = false)
                _messages.value = _messages.value + botMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    text = if (language == Language.BENGALI)
                        "দুঃখিত, কোনো একটি ত্রুটি ঘটেছে। অনুগ্রহ করে আবার চেষ্টা করুন।"
                    else "Sorry, could not process request right now. Please try again.",
                    isUser = false
                )
                _messages.value = _messages.value + errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat(language: Language) {
        val initialText = if (language == Language.BENGALI)
            "স্বাগতম! আমি আপনার FocusLife এআই প্রোডাক্টিভিটি সহকারী। আপনার লক্ষ্য এবং পরিকল্পনা সম্পর্কে জিজ্ঞেস করুন।"
        else
            "Welcome! I am your FocusLife AI coach. Ask me about your daily schedule, goals, or productivity habits."
        _messages.value = listOf(ChatMessage(text = initialText, isUser = false))
    }
}
