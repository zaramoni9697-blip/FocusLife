package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.RootScreen
import com.example.ui.screens.MainScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.auth.ForgotPasswordScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.SignUpScreen
import com.example.ui.theme.FocusLifeTheme
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory App Check configuration before UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val aiViewModel: AiViewModel = viewModel()

            val isDarkTheme by mainViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val currentLanguage by mainViewModel.language.collectAsStateWithLifecycle()
            val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

            var currentRootScreen by remember { mutableStateOf(RootScreen.SPLASH) }

            // Back handling between root screens
            BackHandler(enabled = currentRootScreen != RootScreen.MAIN && currentRootScreen != RootScreen.SPLASH) {
                when (currentRootScreen) {
                    RootScreen.SIGNUP, RootScreen.FORGOT_PASSWORD -> currentRootScreen = RootScreen.LOGIN
                    RootScreen.LOGIN -> currentRootScreen = RootScreen.ONBOARDING
                    else -> {}
                }
            }

            FocusLifeTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentRootScreen) {
                        RootScreen.SPLASH -> {
                            SplashScreen(
                                language = currentLanguage,
                                isOnboardingCompleted = isOnboardingCompleted,
                                onNavigateNext = { completed ->
                                    currentRootScreen = if (completed) RootScreen.MAIN else RootScreen.ONBOARDING
                                }
                            )
                        }
                        RootScreen.ONBOARDING -> {
                            OnboardingScreen(
                                currentLanguage = currentLanguage,
                                onToggleLanguage = { mainViewModel.toggleLanguage() },
                                onFinishOnboarding = {
                                    mainViewModel.completeOnboarding()
                                    currentRootScreen = RootScreen.MAIN
                                }
                            )
                        }
                        RootScreen.LOGIN -> {
                            LoginScreen(
                                currentLanguage = currentLanguage,
                                onToggleLanguage = { mainViewModel.toggleLanguage() },
                                onLoginSuccess = { email, _ ->
                                    val name = email.substringBefore("@").replace(".", " ")
                                        .replaceFirstChar { it.uppercase() }
                                    mainViewModel.updateProfile(name, email, 1)
                                    mainViewModel.completeOnboarding()
                                    currentRootScreen = RootScreen.MAIN
                                },
                                onContinueAsGuest = {
                                    mainViewModel.completeOnboarding()
                                    currentRootScreen = RootScreen.MAIN
                                },
                                onNavigateSignUp = { currentRootScreen = RootScreen.SIGNUP },
                                onNavigateForgotPassword = { currentRootScreen = RootScreen.FORGOT_PASSWORD }
                            )
                        }
                        RootScreen.SIGNUP -> {
                            SignUpScreen(
                                currentLanguage = currentLanguage,
                                onSignUpSuccess = { name, email, avatarId ->
                                    mainViewModel.updateProfile(name, email, avatarId)
                                    mainViewModel.completeOnboarding()
                                    currentRootScreen = RootScreen.MAIN
                                },
                                onNavigateBack = { currentRootScreen = RootScreen.LOGIN }
                            )
                        }
                        RootScreen.FORGOT_PASSWORD -> {
                            ForgotPasswordScreen(
                                currentLanguage = currentLanguage,
                                onNavigateBack = { currentRootScreen = RootScreen.LOGIN }
                            )
                        }
                        RootScreen.MAIN -> {
                            MainScreen(
                                mainViewModel = mainViewModel,
                                aiViewModel = aiViewModel,
                                onLogoutToAuth = { currentRootScreen = RootScreen.LOGIN }
                            )
                        }
                    }
                }
            }
        }
    }
}
