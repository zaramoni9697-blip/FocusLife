package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.localization.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("focuslife_user_prefs", Context.MODE_PRIVATE)

    private val _onboardingCompleted =
        MutableStateFlow(prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false))
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _language = MutableStateFlow(
        if (prefs.getString(KEY_LANGUAGE, "en") == "bn") Language.BENGALI else Language.ENGLISH
    )
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _darkTheme =
        MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME, true)) // Default dark mode
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _notificationsEnabled =
        MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _hapticsEnabled =
        MutableStateFlow(prefs.getBoolean(KEY_HAPTICS_ENABLED, true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
        _onboardingCompleted.value = completed
    }

    fun setLanguage(lang: Language) {
        prefs.edit().putString(KEY_LANGUAGE, lang.code).apply()
        _language.value = lang
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, isDark).apply()
        _darkTheme.value = isDark
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
        _notificationsEnabled.value = enabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, enabled).apply()
        _hapticsEnabled.value = enabled
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_DARK_THEME = "dark_theme_enabled"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
    }
}
