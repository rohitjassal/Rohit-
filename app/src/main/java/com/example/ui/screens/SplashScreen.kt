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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LogoConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation state variables
    val scale = remember { Animatable(0.85f) }
    val opacity = remember { Animatable(0f) }
    val glowProgress = remember { Animatable(0f) }
    
    // Staggered title slides
    val titleOpacity = remember { Animatable(0f) }
    val titleSlideY = remember { Animatable(40f) }

    // Start premium animation sequence
    LaunchedEffect(Unit) {
        // 1. Smooth fade-in
        opacity.animateTo(
            targetValue = 1f,
            animationSpec = tween(900, easing = FastOutSlowInEasing)
        )
        // 2. Slow zoom-in physics
        scale.animateTo(
            targetValue = 1.05f,
            animationSpec = tween(1400, easing = LinearOutSlowInEasing)
        )
        // 3. Staggered subtle glow pulsing
        glowProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    // Stagger text entrance
    LaunchedEffect(Unit) {
        delay(400)
        launch {
            titleOpacity.animateTo(
                targetValue = 1f,
                animationSpec = tween(800, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            titleSlideY.animateTo(
                targetValue = 0f,
                animationSpec = tween(900, easing = FastOutSlowInEasing)
            )
        }
    }

    // Timer to trigger navigation transition
    LaunchedEffect(Unit) {
        delay(2600) // Duration: ~2.6 seconds
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
                .size(280.dp * scale.value)
                .alpha(opacity.value * 0.15f * (1f - glowProgress.value * 0.3f))
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
            // High fidelity custom logo
            LogoConfig.DoraLogoIcon(size = 92.dp, cornerRadius = 20.dp)

            Spacer(modifier = Modifier.height(20.dp))

            // Premium animated custom wordmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .alpha(titleOpacity.value)
                    .graphicsLayer {
                        translationY = titleSlideY.value
                    }
            ) {
                Text(
                    text = "DO",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    fontSize = 44.sp,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "R",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 44.sp,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "Δ",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    fontSize = 42.sp,
                    color = Color.White,
                    letterSpacing = 4.sp,
                    modifier = Modifier.offset(y = (-2).dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "L I B R A R Y",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.65f),
                letterSpacing = 3.sp,
                modifier = Modifier
                    .alpha(titleOpacity.value)
                    .graphicsLayer {
                        translationY = titleSlideY.value
                    }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated glowing tag line text below
            Text(
                text = "KNOWLEDGE OBSERVER",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f + (glowProgress.value * 0.2f)),
                letterSpacing = 4.3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(if (titleOpacity.value > 0.5f) 0.8f else 0f)
            )
        }
    }
}
