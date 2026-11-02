package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val titleKey: String,
    val descKey: String,
    val tag: String,
    val tagBn: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isHeroPage: Boolean = false
)

@Composable
fun OnboardingScreen(
    currentLanguage: Language,
    onToggleLanguage: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 5 })

    val pages = remember {
        listOf(
            OnboardingPageData(
                titleKey = "onboarding_title_1",
                descKey = "onboarding_desc_1",
                tag = "TASK MANAGEMENT",
                tagBn = "কাজের ব্যবস্থাপনা",
                icon = Icons.Default.CheckCircle,
                accentColor = GreenNeon
            ),
            OnboardingPageData(
                titleKey = "onboarding_title_2",
                descKey = "onboarding_desc_2",
                tag = "HABIT TRACKER",
                tagBn = "অভ্যাস ট্র্যাকার",
                icon = Icons.Default.LocalFireDepartment,
                accentColor = AmberWarning
            ),
            OnboardingPageData(
                titleKey = "onboarding_title_3",
                descKey = "onboarding_desc_3",
                tag = "POMODORO FOCUS",
                tagBn = "পোমোডোরো ফোকাস",
                icon = Icons.Default.Timer,
                accentColor = BlueElectric
            ),
            OnboardingPageData(
                titleKey = "onboarding_title_4",
                descKey = "onboarding_desc_4",
                tag = "CALENDAR & NOTES",
                tagBn = "ক্যালেন্ডার ও নোটস",
                icon = Icons.Default.CalendarMonth,
                accentColor = PurpleLight
            ),
            OnboardingPageData(
                titleKey = "onboarding_title_5",
                descKey = "onboarding_desc_5",
                tag = "AI ASSISTANT & ANALYTICS",
                tagBn = "এআই সহকারী ও অ্যানালিটিক্স",
                icon = Icons.Default.AutoAwesome,
                accentColor = PurplePrimary,
                isHeroPage = true
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("onboarding_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar: Language Switcher & Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language switcher pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceElevatedDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                        .clickable { onToggleLanguage() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = PurplePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentLanguage == Language.ENGLISH) "বাংলা" else "English",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Skip Button
                TextButton(
                    onClick = onFinishOnboarding,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = AppStrings.get("skip", currentLanguage),
                        color = TextSecondaryDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = pages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Feature visual illustration / card
                    if (page.isHeroPage) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .border(1.dp, page.accentColor.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.onboarding_hero_1793625755942),
                                contentDescription = "FocusLife Productivity Workspace",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        // Dynamic feature showcase card
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                                .border(1.dp, page.accentColor.copy(alpha = 0.35f), RoundedCornerShape(28.dp)),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                page.accentColor.copy(alpha = 0.18f),
                                                Color.Transparent
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(page.accentColor.copy(alpha = 0.2f))
                                            .border(1.5.dp, page.accentColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = page.icon,
                                            contentDescription = null,
                                            tint = page.accentColor,
                                            modifier = Modifier.size(42.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Dynamic mini mock preview
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = SurfaceElevatedDark
                                        ) {
                                            Text(
                                                text = if (currentLanguage == Language.BENGALI) "✓ সম্পন্ন" else "✓ Completed",
                                                color = GreenNeon,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = SurfaceElevatedDark
                                        ) {
                                            Text(
                                                text = if (currentLanguage == Language.BENGALI) "🔥 ৭ দিনের স্ট্রিক" else "🔥 7 Day Streak",
                                                color = AmberWarning,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = SurfaceElevatedDark
                                        ) {
                                            Text(
                                                text = "⏱️ 25:00",
                                                color = BlueElectric,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Category tag pill
                    Text(
                        text = if (currentLanguage == Language.BENGALI) page.tagBn else page.tag,
                        color = page.accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title
                    Text(
                        text = AppStrings.get(page.titleKey, currentLanguage),
                        color = TextPrimaryDark,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = AppStrings.get(page.descKey, currentLanguage),
                        color = TextSecondaryDark,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            // Bottom Controls: Indicator Dots & Action Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    repeat(5) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width = if (isSelected) 26.dp else 8.dp
                        val color = if (isSelected) PurplePrimary else BorderDark
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(width)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                        )
                    }
                }

                // Main CTA Button
                val isLastPage = pagerState.currentPage == 4
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinishOnboarding()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLastPage)
                                AppStrings.get("get_started", currentLanguage)
                            else
                                AppStrings.get("next", currentLanguage),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Guest mode skip hint
                TextButton(
                    onClick = onFinishOnboarding,
                    modifier = Modifier.testTag("onboarding_guest_button")
                ) {
                    Text(
                        text = AppStrings.get("guest_mode", currentLanguage),
                        color = BlueElectric,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
