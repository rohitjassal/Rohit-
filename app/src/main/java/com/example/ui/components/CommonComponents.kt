package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * Modern Category Badge with customizable label and colors.
 */
@Composable
fun CategoryBadge(
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            letterSpacing = 1.sp
        )
    }
}

/**
 * Dynamic Category Falling Back image generator.
 */
@Composable
fun CategoryFallbackThumbnail(
    category: String,
    modifier: Modifier = Modifier
) {
    val cleanCat = category.trim().uppercase()
    
    val gradientColors = when {
        cleanCat.contains("NEWS") || cleanCat.contains("TECH") -> listOf(Color(0xFF3F51B5), Color(0xFF283593), Color(0xFF1A237E))
        cleanCat.contains("TREND") -> listOf(Color(0xFFE91E63), Color(0xFFC2185B), Color(0xFF880E4F))
        cleanCat.contains("AI") -> listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2), Color(0xFF4A148C))
        cleanCat.contains("JOB") || cleanCat.contains("CAREER") || cleanCat.contains("REMOTE") || cleanCat.contains("WORK") -> listOf(Color(0xFF0D9488), Color(0xFF0F766E), Color(0xFF004D40))
        cleanCat.contains("START") || cleanCat.contains("BUSINESS") -> listOf(Color(0xFFE65100), Color(0xFFF57C00), Color(0xFFBF360C))
        cleanCat.contains("REEL") || cleanCat.contains("MEDIA") || cleanCat.contains("VIDEO") || cleanCat.contains("PLAY") -> listOf(Color(0xFFEC407A), Color(0xFFD81B60), Color(0xFF880E4F))
        cleanCat.contains("EVENT") || cleanCat.contains("SOCIAL") || cleanCat.contains("RADAR") -> listOf(Color(0xFF4CAF50), Color(0xFF388E3C), Color(0xFF1B5E20))
        cleanCat.contains("BOOK") || cleanCat.contains("EDU") -> listOf(Color(0xFF03A9F4), Color(0xFF0288D1), Color(0xFF01579B))
        cleanCat.contains("PODCAST") || cleanCat.contains("MIC") || cleanCat.contains("AUDIO") -> listOf(Color(0xFFFF9800), Color(0xFFF57C00), Color(0xFFE65100))
        cleanCat.contains("QUOTE") || cleanCat.contains("WRITE") -> listOf(Color(0xFF795548), Color(0xFF5D4037), Color(0xFF3E2723))
        cleanCat.contains("COUNTRY") || cleanCat.contains("GEOG") || cleanCat.contains("MAP") -> listOf(Color(0xFF673AB7), Color(0xFF512DA8), Color(0xFF311B92))
        cleanCat.contains("API") || cleanCat.contains("DEV") || cleanCat.contains("CODE") -> listOf(Color(0xFF009688), Color(0xFF00796B), Color(0xFF004D40))
        cleanCat.contains("ANIMAL") || cleanCat.contains("PET") || cleanCat.contains("ZOO") -> listOf(Color(0xFF8BC34A), Color(0xFF689F38), Color(0xFF33691E))
        cleanCat.contains("HISTORY") || cleanCat.contains("DATE") -> listOf(Color(0xFFFF5722), Color(0xFFE64A19), Color(0xFFDD2C00))
        cleanCat.contains("ISS") || cleanCat.contains("SPACE") || cleanCat.contains("ORBIT") -> listOf(Color(0xFF607D8B), Color(0xFF455A64), Color(0xFF263238))
        cleanCat.contains("ART") || cleanCat.contains("CREATIVE") -> listOf(Color(0xFFE040FB), Color(0xFFD500F9), Color(0xFF4A148C))
        cleanCat.contains("RECIPE") || cleanCat.contains("FOOD") || cleanCat.contains("COOK") || cleanCat.contains("LIFE") -> listOf(Color(0xFF00C853), Color(0xFF388E3C), Color(0xFF004D40))
        cleanCat.contains("POLL") || cleanCat.contains("TRIVIA") -> listOf(Color(0xFFFFC107), Color(0xFFFFA000), Color(0xFFFF6F00))
        cleanCat.contains("BRAIN") || cleanCat.contains("FACT") -> listOf(Color(0xFF00E5FF), Color(0xFF00B8D4), Color(0xFF006064))
        cleanCat.contains("CURRENCY") || cleanCat.contains("FX") || cleanCat.contains("FIN") || cleanCat.contains("MONEY") -> listOf(Color(0xFFFF3D00), Color(0xFFDD2C00), Color(0xFF3E2723))
        else -> listOf(Color(0xFF4B5563), Color(0xFF374151), Color(0xFF1F2937)) // Slate Grey/Dark Slate
    }
    
    val sticker = when {
        cleanCat.contains("NEWS") || cleanCat.contains("TECH") -> "📰"
        cleanCat.contains("TREND") -> "📈"
        cleanCat.contains("AI") -> "🤖"
        cleanCat.contains("JOB") || cleanCat.contains("CAREER") || cleanCat.contains("REMOTE") || cleanCat.contains("WORK") -> "💼"
        cleanCat.contains("START") || cleanCat.contains("BUSINESS") -> "🚀"
        cleanCat.contains("REEL") || cleanCat.contains("MEDIA") || cleanCat.contains("VIDEO") || cleanCat.contains("PLAY") -> "🎬"
        cleanCat.contains("EVENT") || cleanCat.contains("SOCIAL") || cleanCat.contains("RADAR") -> "📅"
        cleanCat.contains("BOOK") || cleanCat.contains("EDU") -> "📚"
        cleanCat.contains("PODCAST") || cleanCat.contains("MIC") || cleanCat.contains("AUDIO") -> "🎙️"
        cleanCat.contains("QUOTE") || cleanCat.contains("WRITE") -> "✍️"
        cleanCat.contains("COUNTRY") || cleanCat.contains("GEOG") || cleanCat.contains("MAP") -> "🌍"
        cleanCat.contains("API") || cleanCat.contains("DEV") || cleanCat.contains("CODE") -> "💻"
        cleanCat.contains("ANIMAL") || cleanCat.contains("PET") || cleanCat.contains("ZOO") -> "🦁"
        cleanCat.contains("HISTORY") || cleanCat.contains("DATE") -> "⏳"
        cleanCat.contains("ISS") || cleanCat.contains("SPACE") || cleanCat.contains("ORBIT") -> "🛰️"
        cleanCat.contains("ART") || cleanCat.contains("CREATIVE") -> "🎨"
        cleanCat.contains("RECIPE") || cleanCat.contains("FOOD") || cleanCat.contains("COOK") || cleanCat.contains("LIFE") -> "🍳"
        cleanCat.contains("POLL") || cleanCat.contains("TRIVIA") -> "🗳️"
        cleanCat.contains("BRAIN") || cleanCat.contains("FACT") -> "🧠"
        cleanCat.contains("CURRENCY") || cleanCat.contains("FX") || cleanCat.contains("FIN") || cleanCat.contains("MONEY") -> "💵"
        else -> "📰"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors = gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        // Subtle abstract background pattern for extra visual layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = sticker,
                fontSize = 42.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = category.uppercase(),
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }
    }
}

/**
 * Shimmering Skeletal placeholder for loading states.
 */
@Composable
fun ShimmerLoadingItem(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = translateAnim, y = translateAnim)
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

/**
 * Universal Premium Content Card
 */
@Composable
fun DoraContentCard(
    title: String,
    description: String?,
    sourceName: String,
    publishedDate: String?,
    timeLabel: String?,
    category: String,
    imageUrl: String?,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    modifier: Modifier = Modifier,
    actionSlot: @Composable (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Content Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                )
                            )
                    )
                } else {
                    CategoryFallbackThumbnail(category = category)
                }

                CategoryBadge(
                    label = category,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sourceName.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dateText = publishedDate ?: "Recent"
                        Text(
                            text = dateText,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        if (!timeLabel.isNullOrEmpty()) {
                            Text(
                                text = "•",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Text(
                                text = timeLabel,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (actionSlot != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    actionSlot()
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curated Insight",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                try {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, title)
                                        putExtra(Intent.EXTRA_TEXT, "$title\n\nFind details at: $sourceName")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share"))
                                } catch (e: Exception) {}
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onSaveToggle,
                            modifier = Modifier.size(36.dp).testTag("card_save_btn_${title.hashCode()}")
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkAdd,
                                contentDescription = "Save",
                                tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Keep original card for safe back-compatibility
 */
@Composable
fun DoraArticleCard(
    title: String,
    description: String?,
    sourceName: String,
    author: String?,
    url: String,
    imageUrl: String?,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier,
    badgeLabel: String = "TECH HUB",
    badgeContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    badgeContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    DoraContentCard(
        title = title,
        description = description,
        sourceName = sourceName,
        publishedDate = "Recent",
        timeLabel = author ?: "Anonymous",
        category = badgeLabel,
        imageUrl = imageUrl,
        isSaved = isBookmarked,
        onSaveToggle = onBookmarkToggle,
        modifier = modifier,
        actionSlot = {
            val context = LocalContext.current
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (e: Exception) {}
                },
                modifier = Modifier.fillMaxWidth().height(40.dp)
            ) {
                Text("Read Article", fontSize = 12.sp)
            }
        }
    )
}
