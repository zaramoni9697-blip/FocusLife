package com.example.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.localization.Language
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class AiAssistantService(private val context: Context) {

    private val TAG = "AiAssistantService"

    private val geminiModel by lazy {
        try {
            Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    temperature = 0.7f
                }
            )
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AI not yet provisioned, fallback engine active", e)
            null
        }
    }

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun getResponse(userPrompt: String, language: Language): String = withContext(Dispatchers.IO) {
        val trimmed = userPrompt.trim()

        if (isOnline()) {
            try {
                val model = geminiModel
                if (model != null) {
                    val systemInstruction = if (language == Language.BENGALI) {
                        "তুমি FocusLife অ্যাপের একজন বিশেষজ্ঞ প্রোডাক্টিভিটি কোচ ও সহকারী। ব্যবহারকারীকে বাংলা ভাষায় বন্ধুত্বপূর্ণ, সংক্ষিপ্ত, কার্যকরী এবং অত্যন্ত অনুপ্রেরণামূলক পরামর্শ দাও। বুলেট পয়েন্ট এবং সুন্দর ফরম্যাটে উত্তর দাও।"
                    } else {
                        "You are an expert productivity coach and executive planner inside FocusLife app. Provide actionable, concise, inspiring, and clear guidance with bullet points and clean structure."
                    }

                    val fullPrompt = "$systemInstruction\n\nUser Question: $trimmed"
                    val response = withTimeout(15_000L) {
                        model.generateContent(fullPrompt)
                    }
                    val generatedText = response.text
                    if (!generatedText.isNullOrBlank()) {
                        return@withContext generatedText.trim()
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Online Gemini call failed or offline, using smart local engine: ${e.message}")
            }
        }

        // Contextual Intelligent Offline Engine (Bengali & English)
        return@withContext generateSmartLocalResponse(trimmed, language)
    }

    private fun generateSmartLocalResponse(prompt: String, language: Language): String {
        val lower = prompt.lowercase()
        val isBengali = language == Language.BENGALI || prompt.any { it in '\u0980'..'\u09FF' }

        if (isBengali) {
            return when {
                lower.contains("পরিকল্পনা") || lower.contains("plan") || lower.contains("দিন") -> """
🌟 **FocusLife দৈনিক উৎপাদনশীল পরিকল্পনা:**

1. **সকাল (৮:০০ - ১২:০০): ডিপ ওয়ার্ক ও উচ্চ অগ্রাধিকার কাজ**
   • সব ধরণের নোটিফিকেশন বন্ধ রাখুন।
   • দিনের সবচেয়ে কঠিন ও গুরুত্বপূর্ণ কাজটি (Eat The Frog) প্রথমে শেষ করুন।
   • ২৫ মিনিট ফোকাস + ৫ মিনিট বিরতির পোমোডোরো টেকনিক প্রয়োগ করুন।

2. **দুপুর (১২:৩০ - ৩:০০): সহযোগী কাজ ও ইমেইল**
   • মিটিং, ইমেইল রিপ্লাই এবং সাধারণ যোগাযোগের কাজ সম্পন্ন করুন।

3. **বিকাল (৪:০০ - ৬:০০): ব্যক্তিগত উন্নয়ন ও অভ্যাস**
   • ২০ মিনিট হাঁটা বা ব্যায়াম করুন।
   • নতুন কোনো দক্ষতা বা বই পড়ার অভ্যাসে সময় দিন।

4. **রাত (৯:০০ - ১০:০০): পর্যালোচনা ও প্রস্তুতি**
   • আজকের সম্পন্ন কাজগুলো টিক দিন।
   • আগামীকালের জন্য শীর্ষ ৩টি লক্ষ্য ঠিক করে রাখুন।
                """.trimIndent()

                lower.contains("পড়া") || lower.contains("study") || lower.contains("ছাত্র") -> """
📚 **পড়াশোনায় সর্বোচ্চ মনোযোগ ধরে রাখার কৌশল:**

• **ফাইনম্যান টেকনিক (Feynman Technique):** যেকোনো জটিল বিষয় এমনভাবে বোঝার চেষ্টা করুন যেন আপনি একজন শিশুকে তা শেখাচ্ছেন।
• **স্পেসড রিপিটিশন (Spaced Repetition):** ১ম দিন, ৩য় দিন, ৭ম দিন ও ২১তম দিনে রিভিশন দিন।
• **পমোডোরো সেশন:** একটানা পড়ার বদলে ২৫ মিনিট নিবিড় পাঠ এবং ৫ মিনিট চোখের বিশ্রাম নিন।
• **নোট নিন:** প্যাসিভ পড়ার বদলে মূল সারমর্ম নিজের ভাষায় FocusLife নোটসে লিখে রাখুন।
                """.trimIndent()

                lower.contains("অভ্যাস") || lower.contains("habit") || lower.contains("ধারাবাহিক") -> """
🌱 **অভ্যাস দীর্ঘস্থায়ী করার বৈজ্ঞানিক উপায়:**

১. **২-মিনিট নিয়ম:** নতুন অভ্যাসটিকে এতোটাই ছোট করুন যেন করতে মাত্র ২ মিনিট লাগে (যেমন: ১ পৃষ্ঠা পড়া, ২টি পুশ-আপ)।
২. **হ্যাবিট স্ট্যাকিং:** পুরোনো অভ্যাসের সাথে নতুন অভ্যাস জোড়া লাগান (যেমন: সকালের কফির পর ১০ মিনিট পরিকল্পনা)।
৩. **স্ট্রিক না ভাঙা:** FocusLife ট্র্যাকার দিয়ে স্ট্রিক বজায় রাখুন। পরপর দুই দিন কখনই বাদ দেবেন না।
                """.trimIndent()

                lower.contains("লক্ষ্য") || lower.contains("goal") || lower.contains("ভেঙে") -> """
🎯 **বড় লক্ষ্যকে ছোট পদক্ষেপে ভাগ করার গাইড:**

১. **চূড়ান্ত ফলাফল নির্ধারণ:** আগামী ৩ মাসে আপনি ঠিক কি অর্জন করতে চান তা স্পষ্ট করুন।
২. **সাপ্তাহিক মাইলফলক:** লক্ষ্যটিকে ৪টি প্রধান ভাগে বিভক্ত করুন।
৩. **দৈনিক ক্ষুদ্র কাজ (Daily Micro-actions):** প্রতিদিন অন্তত একটি নির্দিষ্ট কাজ করুন যা আপনার লক্ষ্যকে এগিয়ে নিয়ে যায়।
৪. **অগ্রগতি পরিমাপ:** প্রতি রবিবার FocusLife অ্যানালিটিক্স দেখে নিজের পারফরম্যান্স মূল্যায়ন করুন।
                """.trimIndent()

                else -> """
💡 **FocusLife প্রোডাক্টিভিটি টিপ:**

• আপনার শক্তির সর্বোচ্চ স্তরে কাজ করুন।
• মাল্টিটাস্কিং এড়িয়ে একটি সময়ে একটি কাজেই সম্পূর্ণ মনোযোগ দিন।
• প্রতিদিন অন্তত ২৫ মিনিটের ২টি পোমোডোরো সেশন শেষ করার চ্যালেঞ্জ নিন।
• যেকোনো নতুন লক্ষ্য বা চিন্তাভাবনা নোটস সেকশনে ড্রাফট করে রাখুন।

আপনার দিনটি চমৎকার এবং সফল হোক! 🚀
                """.trimIndent()
            }
        } else {
            return when {
                lower.contains("plan") || lower.contains("today") || lower.contains("day") -> """
🌟 **FocusLife Daily Master Plan:**

1. **Deep Work Block (Morning 9:00 AM - 12:00 PM)**
   • Tackle your highest priority task with zero distractions.
   • Use two 25-minute Pomodoro sessions back-to-back.

2. **Administrative & Collaborative Tasks (1:30 PM - 3:30 PM)**
   • Reply to emails, conduct quick syncs, and clear smaller inbox to-dos.

3. **Learning & Health Habit Block (4:30 PM - 6:00 PM)**
   • 30-minute cardio/movement session.
   • 20 minutes of dedicated reading or skill sharpening.

4. **Evening Review & Reset (8:30 PM - 9:00 PM)**
   • Check off completed habits in your tracker.
   • Define your Top 3 priorities for tomorrow in FocusLife Tasks.
                """.trimIndent()

                lower.contains("goal") || lower.contains("break down") || lower.contains("project") -> """
🎯 **Deconstructing Big Goals into Actionable Milestones:**

1. **Clarify the Outcome:** Define the exact finish line with measurable criteria.
2. **Reverse Engineer:** Work backward from the target date to establish 3 key phases.
3. **Atomic Tasks:** Convert each phase into single-sitting tasks taking under 45 minutes.
4. **Schedule Deadlines:** Assign each item to your FocusLife Calendar with reminder notifications.
                """.trimIndent()

                lower.contains("habit") || lower.contains("streak") || lower.contains("discipline") -> """
🌱 **The Habit Architecture System:**

• **Friction Reduction:** Prepare everything the night before (clothes, books, workstation).
• **Habit Stacking:** Anchor your new habit to an existing routine (e.g. *After morning coffee, I will plan 3 tasks*).
• **Never Miss Twice:** Life happens, but missing two consecutive days breaks compounding momentum.
• **Visual Proof:** Use the FocusLife Habit Tracker daily to reinforce your identity as a disciplined achiever.
                """.trimIndent()

                lower.contains("focus") || lower.contains("timer") || lower.contains("pomodoro") -> """
⚡ **Maximizing Your Pomodoro Efficiency:**

• **Pre-Session Setup:** Clear your physical desk and close unnecessary browser tabs before hitting Start.
• **Micro-Break Discipline:** During the 5-minute break, stand up, hydrate, and look at a distant object—do NOT check social media.
• **Long Break Reward:** After 4 sessions (100 mins of pure focus), enjoy a full 15-20 minute recharge.
                """.trimIndent()

                else -> """
💡 **FocusLife High-Performance Principle:**

"Discipline is simply choosing between what you want now and what you want most."

• Focus on high-leverage activities that move the needle.
• Group related small tasks together to minimize context switching costs.
• Celebrate daily wins—every checked box in FocusLife builds unstoppable momentum! 🚀
                """.trimIndent()
            }
        }
    }
}
