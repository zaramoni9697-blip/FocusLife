package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    language: Language,
    isOnboardingCompleted: Boolean,
    onNavigateNext: (Boolean) -> Unit
) {
    val scale = remember { Animatable(0.7f) }
    val glowAlpha = remember { Animatable(0.2f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        glowAlpha.animateTo(
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(1800)
        onNavigateNext(isOnboardingCompleted)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(130.dp)
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(scale.value * 1.1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    PurplePrimary.copy(alpha = glowAlpha.value),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // App Icon
                Image(
                    painter = painterResource(id = R.drawable.focuslife_icon_1793625662418),
                    contentDescription = "FocusLife Logo",
                    modifier = Modifier
                        .size(96.dp)
                        .scale(scale.value)
                        .clip(RoundedCornerShape(26.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = AppStrings.get("app_name", language),
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = AppStrings.get("tagline", language),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = BlueElectric,
                letterSpacing = 2.sp
            )
        }
    }
}
