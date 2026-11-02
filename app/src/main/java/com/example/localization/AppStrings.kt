package com.example.localization

enum class Language(val code: String, val label: String) {
    ENGLISH("en", "English"),
    BENGALI("bn", "বাংলা")
}

object AppStrings {
    fun get(key: String, lang: Language): String {
        val map = if (lang == Language.BENGALI) bengaliMap else englishMap
        return map[key] ?: englishMap[key] ?: key
    }

    private val englishMap = mapOf(
        "app_name" to "FocusLife",
        "tagline" to "Plan • Do • Grow",
        "skip" to "Skip",
        "next" to "Next",
        "get_started" to "Get Started",
        "continue_btn" to "Continue",
        "welcome" to "Welcome to FocusLife",
        "welcome_sub" to "Master your time, achieve your goals, and build lasting habits.",
        "guest_mode" to "Continue as Guest",
        "guest_mode_hint" to "Use all productivity features completely offline without creating an account.",
        "login" to "Log In",
        "signup" to "Sign Up",
        "email" to "Email Address",
        "password" to "Password",
        "confirm_password" to "Confirm Password",
        "full_name" to "Full Name",
        "forgot_password" to "Forgot Password?",
        "reset_password" to "Reset Password",
        "logout" to "Log Out",
        "profile" to "Profile",
        "account_settings" to "Account Settings",

        // Onboarding
        "onboarding_title_1" to "Plan Everything Clearly",
        "onboarding_desc_1" to "Organize tasks with priorities, due dates, categories, and smart reminders.",
        "onboarding_title_2" to "Build Unbreakable Habits",
        "onboarding_desc_2" to "Track daily streaks, completion rates, and celebrate personal growth.",
        "onboarding_title_3" to "Deep Work Focus Timer",
        "onboarding_desc_3" to "Eliminate distractions with customized Pomodoro sessions and focus analytics.",
        "onboarding_title_4" to "Smart Calendar & Notes",
        "onboarding_desc_4" to "Sync your daily schedules, pin important thoughts, and keep ideas organized.",
        "onboarding_title_5" to "AI Assistant & Analytics",
        "onboarding_desc_5" to "Get personalized productivity tips and actionable daily plans in Bengali & English.",

        // Navigation
        "nav_dashboard" to "Dashboard",
        "nav_tasks" to "Tasks",
        "nav_calendar" to "Calendar",
        "nav_habits" to "Habits",
        "nav_timer" to "Timer",
        "nav_notes" to "Notes",
        "nav_ai" to "AI Assistant",
        "nav_stats" to "Analytics",
        "nav_notifications" to "Alerts",
        "nav_settings" to "Settings",

        // Dashboard
        "greeting_morning" to "Good Morning",
        "greeting_afternoon" to "Good Afternoon",
        "greeting_evening" to "Good Evening",
        "productivity_score" to "Productivity Score",
        "today_progress" to "Today's Progress",
        "tasks_summary" to "Tasks Overview",
        "completed" to "Completed",
        "pending" to "Pending",
        "total_tasks" to "Total Tasks",
        "habit_streak" to "Active Streak",
        "focus_time" to "Focus Time",
        "upcoming_reminders" to "Upcoming Reminders",
        "quick_actions" to "Quick Actions",
        "add_task" to "Add Task",
        "start_focus" to "Start Focus",
        "new_note" to "New Note",

        // Tasks
        "all_tasks" to "All",
        "today_tasks" to "Today",
        "priority_high" to "High",
        "priority_med" to "Medium",
        "priority_low" to "Low",
        "category_work" to "Work",
        "category_study" to "Study",
        "category_personal" to "Personal",
        "category_health" to "Health",
        "task_title_hint" to "What needs to be done?",
        "task_desc_hint" to "Add description or subtasks...",
        "due_date" to "Due Date",
        "set_reminder" to "Set Native Notification Reminder",
        "save_task" to "Save Task",
        "delete_task" to "Delete Task",
        "task_completed_msg" to "Great job! Task completed.",

        // Notes
        "notes_title" to "Quick Notes & Ideas",
        "note_title_hint" to "Title",
        "note_content_hint" to "Write your thoughts...",
        "pin_note" to "Pin Note",
        "search_notes" to "Search notes...",
        "save_note" to "Save Note",

        // Calendar
        "calendar_title" to "Smart Calendar",
        "add_event" to "New Event",
        "event_title_hint" to "Event Title",
        "today_indicator" to "Today",

        // Habits
        "habits_title" to "Habit Tracker",
        "add_habit" to "Create Habit",
        "habit_name_hint" to "Habit Name (e.g., Read 20 mins)",
        "streak_days" to "Days Streak",
        "completion_rate" to "Consistency",

        // Pomodoro
        "timer_focus" to "Focus Time",
        "timer_short_break" to "Short Break",
        "timer_long_break" to "Long Break",
        "timer_start" to "Start",
        "timer_pause" to "Pause",
        "timer_resume" to "Resume",
        "timer_reset" to "Reset",
        "session_count" to "Sessions Completed",
        "sound_vibrate" to "Sound & Haptics",

        // AI Assistant
        "ai_title" to "AI Productivity Coach",
        "ai_tagline" to "Powered by Gemini Intelligence • Bilingual en/bn",
        "ai_prompt_hint" to "Ask in English or বাংলা...",
        "ai_btn_send" to "Send",
        "ai_quick_plan" to "📅 Plan My Day",
        "ai_quick_plan_bn" to "📅 আজকের দিনের পরিকল্পনা করো",
        "ai_quick_tips" to "⚡ Productivity Boost",
        "ai_quick_tips_bn" to "⚡ কাজের গতি বাড়ানোর উপায়",
        "ai_quick_breakdown" to "🎯 Break Down a Complex Goal",
        "ai_quick_breakdown_bn" to "🎯 লক্ষ্য অর্জনের ছোট ধাপ",
        "ai_quick_habit" to "🌱 Habit Consistency Guide",
        "ai_quick_habit_bn" to "🌱 অভ্যাসের ধারাবাহিকতা বজায় রাখা",

        // Notifications
        "notifications_title" to "Notification Center",
        "notif_permission_request" to "Enable native lock screen and banner reminders",
        "grant_permission" to "Enable Notifications",
        "test_notification" to "Send Test Notification",
        "channel_tasks" to "Task Reminders",
        "channel_habits" to "Habit Check-ins",
        "channel_pomodoro" to "Pomodoro Alerts",
        "channel_planning" to "Daily Planning",

        // Settings
        "settings_title" to "Settings",
        "language_setting" to "App Language (ভাষা)",
        "theme_setting" to "Appearance",
        "theme_dark" to "Premium Dark",
        "theme_light" to "Clean Light",
        "backup_restore" to "Data Backup & Export",
        "export_json" to "Export Data (JSON)",
        "data_exported" to "Data successfully exported!",
        "privacy_policy" to "Privacy & Offline Mode",
        "privacy_desc" to "All your tasks, habits, and notes are securely stored on your device.",
        "about_focuslife" to "About FocusLife v1.0"
    )

    private val bengaliMap = mapOf(
        "app_name" to "FocusLife",
        "tagline" to "পরিকল্পনা • কাজ • সাফল্য",
        "skip" to "এড়িয়ে যান",
        "next" to "পরবর্তী",
        "get_started" to "শুরু করুন",
        "continue_btn" to "চালিয়ে যান",
        "welcome" to "FocusLife-এ স্বাগতম",
        "welcome_sub" to "সময় নিয়ন্ত্রণ করুন, লক্ষ্য অর্জন করুন এবং গড়ে তুলুন নতুন অভ্যাস।",
        "guest_mode" to "গেস্ট হিসেবে ব্যবহার করুন",
        "guest_mode_hint" to "অ্যাকাউন্ট ছাড়াই সম্পূর্ণ অফলাইনে সমস্ত ফিচার ব্যবহার করুন।",
        "login" to "লগ ইন",
        "signup" to "সাইন আপ",
        "email" to "ইমেইল ঠিকানা",
        "password" to "পাসওয়ার্ড",
        "confirm_password" to "পাসওয়ার্ড নিশ্চিত করুন",
        "full_name" to "পুরো নাম",
        "forgot_password" to "পাসওয়ার্ড ভুলে গেছেন?",
        "reset_password" to "পাসওয়ার্ড রিসেট",
        "logout" to "লগ আউট",
        "profile" to "প্রোফাইল",
        "account_settings" to "অ্যাকাউন্ট সেটিংস",

        // Onboarding
        "onboarding_title_1" to "সুনির্দিষ্ট পরিকল্পনা করুন",
        "onboarding_desc_1" to "গুরুত্ব, নির্দিষ্ট সময়সীমা, বিভাগ ও স্মার্ট রিমাইন্ডার দিয়ে কাজগুলো সাজিয়ে নিন।",
        "onboarding_title_2" to "দৃঢ় অভ্যাস গড়ে তুলুন",
        "onboarding_desc_2" to "দৈনিক অগ্রগতি ট্র্যাক করুন, ধারাবাহিকতা বজায় রাখুন এবং নিজের উন্নতি উদযাপন করুন।",
        "onboarding_title_3" to "ডিপ ওয়ার্ক ফোকাস টাইমার",
        "onboarding_desc_3" to "পোমোডোরো সেশনের মাধ্যমে মনোযোগ বাড়ান এবং কাজের ফলাফল ট্র্যাক করুন।",
        "onboarding_title_4" to "স্মার্ট ক্যালেন্ডার ও নোটস",
        "onboarding_desc_4" to "দৈনন্দিন সময়সূচী সমন্বয় করুন এবং গুরুত্বপূর্ণ চিন্তাভাবনা সহজেই পিন করে রাখুন।",
        "onboarding_title_5" to "এআই সহকারী ও অ্যানালিটিক্স",
        "onboarding_desc_5" to "বাংলা ও ইংরেজিতে ব্যক্তিগত প্রোডাক্টিভিটি টিপস এবং কার্যকরী দৈনিক পরিকল্পনা পান।",

        // Navigation
        "nav_dashboard" to "ড্যাশবোর্ড",
        "nav_tasks" to "কাজসমূহ",
        "nav_calendar" to "ক্যালেন্ডার",
        "nav_habits" to "অভ্যাস",
        "nav_timer" to "টাইমার",
        "nav_notes" to "নোটস",
        "nav_ai" to "এআই সহকারী",
        "nav_stats" to "পরিসংখ্যান",
        "nav_notifications" to "নোটিফিকেশন",
        "nav_settings" to "সেটিংস",

        // Dashboard
        "greeting_morning" to "শুভ সকাল",
        "greeting_afternoon" to "শুভ দুপুর",
        "greeting_evening" to "শুভ সন্ধ্যা",
        "productivity_score" to "উৎপাদনশীলতা স্কোর",
        "today_progress" to "আজকের অগ্রগতি",
        "tasks_summary" to "কাজের সারসংক্ষেপ",
        "completed" to "সম্পন্ন",
        "pending" to "বাকি আছে",
        "total_tasks" to "মোট কাজ",
        "habit_streak" to "ধারাবাহিকতা",
        "focus_time" to "ফোকাস সময়",
        "upcoming_reminders" to "আসন্ন রিমাইন্ডার",
        "quick_actions" to "দ্রুত অ্যাকশন",
        "add_task" to "নতুন কাজ",
        "start_focus" to "ফোকাস শুরু",
        "new_note" to "নতুন নোট",

        // Tasks
        "all_tasks" to "সব কাজ",
        "today_tasks" to "আজকের কাজ",
        "priority_high" to "জরুরি",
        "priority_med" to "মাঝারি",
        "priority_low" to "সাধারণ",
        "category_work" to "কর্মক্ষেত্র",
        "category_study" to "পড়াশোনা",
        "category_personal" to "ব্যক্তিগত",
        "category_health" to "স্বাস্থ্য",
        "task_title_hint" to "কি কাজ করতে চান?",
        "task_desc_hint" to "কাজের বিবরণ বা উপ-কাজ যোগ করুন...",
        "due_date" to "শেষ তারিখ",
        "set_reminder" to "রিমাইন্ডার নোটিফিকেশন দিন",
        "save_task" to "কাজ সংরক্ষণ করুন",
        "delete_task" to "মুছে ফেলুন",
        "task_completed_msg" to "অভিনন্দন! কাজটি সম্পন্ন হয়েছে।",

        // Notes
        "notes_title" to "নোটস ও আইডিয়া",
        "note_title_hint" to "শিরোনাম",
        "note_content_hint" to "আপনার ভাবনাগুলো লিখুন...",
        "pin_note" to "পিন করুন",
        "search_notes" to "নোট খুঁজুন...",
        "save_note" to "সংরক্ষণ করুন",

        // Calendar
        "calendar_title" to "স্মার্ট ক্যালেন্ডার",
        "add_event" to "নতুন ইভেন্ট",
        "event_title_hint" to "ইভেন্টের নাম",
        "today_indicator" to "আজ",

        // Habits
        "habits_title" to "অভ্যাস ট্র্যাকার",
        "add_habit" to "নতুন অভ্যাস",
        "habit_name_hint" to "অভ্যাসের নাম (যেমন: ২০ মিনিট পড়া)",
        "streak_days" to "দিনের ধারাবাহিকতা",
        "completion_rate" to "সাফল্যের হার",

        // Pomodoro
        "timer_focus" to "ফোকাস সময়",
        "timer_short_break" to "ছোট বিরতি",
        "timer_long_break" to "দীর্ঘ বিরতি",
        "timer_start" to "শুরু",
        "timer_pause" to "থামান",
        "timer_resume" to "চালান",
        "timer_reset" to "রিসেট",
        "session_count" to "সম্পন্ন সেশন",
        "sound_vibrate" to "সাউন্ড ও ভাইব্রেশন",

        // AI Assistant
        "ai_title" to "এআই প্রোডাক্টিভিটি কোচ",
        "ai_tagline" to "জেমিনি বুদ্ধিমত্তা দ্বারা পরিচালিত • বাংলা ও ইংরেজি সমর্থিত",
        "ai_prompt_hint" to "বাংলা অথবা ইংরেজিতে জিজ্ঞেস করুন...",
        "ai_btn_send" to "পাঠান",
        "ai_quick_plan" to "📅 Plan My Day",
        "ai_quick_plan_bn" to "📅 আজকের দিনের পরিকল্পনা করো",
        "ai_quick_tips" to "⚡ Productivity Boost",
        "ai_quick_tips_bn" to "⚡ কাজের গতি বাড়ানোর উপায়",
        "ai_quick_breakdown" to "🎯 Break Down a Complex Goal",
        "ai_quick_breakdown_bn" to "🎯 লক্ষ্য অর্জনের ছোট ধাপ",
        "ai_quick_habit" to "🌱 Habit Consistency Guide",
        "ai_quick_habit_bn" to "🌱 অভ্যাসের ধারাবাহিকতা বজায় রাখা",

        // Notifications
        "notifications_title" to "নোটিফিকেশন সেন্টার",
        "notif_permission_request" to "লক স্ক্রিন এবং ব্যানার রিমাইন্ডার চালু করুন",
        "grant_permission" to "নোটিফিকেশন অনুমতি দিন",
        "test_notification" to "টেস্ট নোটিফিকেশন পাঠান",
        "channel_tasks" to "কাজের রিমাইন্ডার",
        "channel_habits" to "অভ্যাস চেক-ইন",
        "channel_pomodoro" to "পোমোডোরো অ্যালার্ট",
        "channel_planning" to "দৈনিক পরিকল্পনা",

        // Settings
        "settings_title" to "সেটিংস",
        "language_setting" to "অ্যাপের ভাষা (Language)",
        "theme_setting" to "থিম",
        "theme_dark" to "ডার্ক থিম",
        "theme_light" to "লাইট থিম",
        "backup_restore" to "ডাটা ব্যাকআপ ও এক্সপোর্ট",
        "export_json" to "ডাটা এক্সপোর্ট (JSON)",
        "data_exported" to "ডাটা সফলভাবে এক্সপোর্ট হয়েছে!",
        "privacy_policy" to "গোপনীয়তা ও অফলাইন সুবিধা",
        "privacy_desc" to "আপনার সমস্ত কাজ, অভ্যাস এবং নোট সম্পূর্ণ নিরাপদভাবে আপনার ফোনেই সংরক্ষিত থাকে।",
        "about_focuslife" to "FocusLife v1.0 সম্পর্কে"
    )
}
