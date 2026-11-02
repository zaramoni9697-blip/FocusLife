package com.example.ui.navigation

enum class RootScreen {
    SPLASH,
    ONBOARDING,
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD,
    MAIN
}

enum class MainTab(val testTag: String) {
    DASHBOARD("tab_dashboard"),
    TASKS("tab_tasks"),
    CALENDAR("tab_calendar"),
    HABITS("tab_habits"),
    POMODORO("tab_pomodoro"),
    NOTES("tab_notes"),
    AI_ASSISTANT("tab_ai_assistant"),
    STATISTICS("tab_statistics"),
    NOTIFICATIONS("tab_notifications"),
    PROFILE("tab_profile"),
    SETTINGS("tab_settings")
}
