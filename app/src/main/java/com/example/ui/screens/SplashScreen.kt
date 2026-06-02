package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LogoConfig
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation state variables
    val scale = remember { Animatable(0.88f) }
    val opacity = remember { Animatable(0f) }
    val glowProgress = remember { Animatable(0f) }

    // Start premium animation sequence
    LaunchedEffect(Unit) {
        // 1. Smooth fade-in
        opacity.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
        // 2. Slow zoom-in physics
        scale.animateTo(
            targetValue = 1.05f,
            animationSpec = tween(1200, easing = LinearOutSlowInEasing)
        )
        // 3. Staggered subtle glow pulsing
        glowProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }

    // Timer to trigger navigation transition
    LaunchedEffect(Unit) {
        delay(2500) // Duration: ~2.5 seconds
        // Outro quick fade-out
        opacity.animateTo(
            targetValue = 0f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
        onSplashFinished()
    }

    // Cozy deep premium cosmic slate background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0C20), // Premium cosmic navy
                        Color(0xFF05050C)  // Pure deep black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Ambient Light Ring in the center background
        Box(
            modifier = Modifier
                .size(260.dp * scale.value)
                .alpha(opacity.value * 0.12f * (1f - glowProgress.value * 0.3f))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Center Content Container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(opacity.value)
                .scale(scale.value)
        ) {
            // High fidelity logo wordmark
            LogoConfig.DoraWordmarkLogo(
                iconSize = 100.dp,
                textSize = 50.sp,
                subtitleSize = 15.sp,
                isCentered = true,
                textColor = Color.White
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Animated glowing tag line text below
            Text(
                text = "KNOWLEDGE OBSERVER",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f + (glowProgress.value * 0.2f)),
                letterSpacing = 4.3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(if (opacity.value > 0.5f) 0.8f else 0f)
            )
        }
    }
}
