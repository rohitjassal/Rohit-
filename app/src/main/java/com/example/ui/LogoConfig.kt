package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Single Configuration File for Dora Library Logo Assets and Placement.
 * 
 * Replace developer logos globally here:
 * 1. Icon-only logo: Replace the resource pointer below or overwrite com/example/R.drawable.img_app_logo_1780587892693
 * 2. Wordmark logo: If you have a physical png/jpeg file containing the full wordmark text logo,
 *    set [USE_PNG_FOR_WORDMARK] to true and update [LOGO_WORDMARK_IMAGE_RES].
 */
object LogoConfig {
    // 1. Icon-only Logo Resource pointer
    val LOGO_ICON_RES: Int = R.drawable.img_app_logo_1780587892693

    // 2. Wordmark Logo Image Resource pointer (if a physical image containing "Dora Library + Icon" in one is provided later)
    val LOGO_WORDMARK_IMAGE_RES: Int = R.drawable.img_app_logo_1780587892693 // Interchangeable pointer
    
    // Toggle whether the wordmark layout uses the physical image asset or the premium animated/dynamic Compose layout.
    const val USE_PNG_FOR_WORDMARK: Boolean = false

    /**
     * Globally used Dora Logo Icon component.
     */
    @Composable
    fun DoraLogoIcon(
        modifier: Modifier = Modifier,
        size: Dp = 48.dp,
        cornerRadius: Dp = 12.dp
    ) {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.Black), // Matches original black-background theme
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = LOGO_ICON_RES),
                contentDescription = "Dora Icon Mark",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(size * 0.12f),
                contentScale = ContentScale.Fit
            )
        }
    }

    /**
     * Highly polished dynamic Dora Library wordmark containing the Logo Icon and custom stylized texts.
     */
    @Composable
    fun DoraWordmarkLogo(
        modifier: Modifier = Modifier,
        iconSize: Dp = 64.dp,
        textSize: TextUnit = 38.sp,
        subtitleSize: TextUnit = 12.sp,
        isCentered: Boolean = true,
        textColor: Color = Color.White
    ) {
        if (USE_PNG_FOR_WORDMARK) {
            Image(
                painter = painterResource(id = LOGO_WORDMARK_IMAGE_RES),
                contentDescription = "Dora Library Logo",
                modifier = modifier,
                contentScale = ContentScale.Fit
            )
        } else {
            Column(
                modifier = modifier,
                horizontalAlignment = if (isCentered) Alignment.CenterHorizontally else Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                // Dora Winged-D Icon Mark
                DoraLogoIcon(size = iconSize)

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Stylized geometric "DO R A" Text in pure XML/Compose design
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (isCentered) Arrangement.Center else Arrangement.Start
                ) {
                    Text(
                        text = "DO",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        fontSize = textSize,
                        color = textColor,
                        letterSpacing = 4.sp
                    )
                    // The stylized R
                    Text(
                        text = "R",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = textSize,
                        color = textColor,
                        letterSpacing = 4.sp
                    )
                    // The triangle A
                    Text(
                        text = "Δ",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        fontSize = textSize * 0.95f,
                        color = textColor,
                        letterSpacing = 4.sp,
                        modifier = Modifier.offset(y = (-2).dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle text representing "LIBRARY"
                Text(
                    text = "L I B R A R Y",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = subtitleSize,
                    color = textColor.copy(alpha = 0.65f),
                    letterSpacing = 3.sp
                )
            }
        }
    }
}
