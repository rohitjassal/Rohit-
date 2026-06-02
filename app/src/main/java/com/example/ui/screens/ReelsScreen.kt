package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ReelItem
import com.example.ui.viewmodel.DoraViewModel
import com.example.ui.viewmodel.HubState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsScreen(
    viewModel: DoraViewModel,
    modifier: Modifier = Modifier
) {
    val reelsState by viewModel.reelsState.collectAsStateWithLifecycle()
    val likedReelIds by viewModel.likedReelIds.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Sticky Header for Reels Screen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tech Reels Hub",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "LIVE FEED",
                    color = Color.Cyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        when (val state = reelsState) {
            is HubState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is HubState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("Unable to load reels flow online.", color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.refreshAll() }) {
                            Text("Retry Local Fallback")
                        }
                    }
                }
            }
            is HubState.Success -> {
                val list = state.data
                if (list.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Reels available currently.", color = Color.LightGray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("reels_feed_lazy_column"),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        items(list) { reel ->
                            val isLiked = likedReelIds.contains(reel.id)
                            val isBookmarked = bookmarks.any { it.id == reel.id }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(480.dp)
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                                    .background(Color(0xFF121212), RoundedCornerShape(24.dp))
                                    .testTag("reel_card_${reel.id}")
                            ) {
                                // Real inline Video Player
                                VideoPlayer(
                                    videoUrl = reel.videoUrl,
                                    modifier = Modifier.matchParentSize()
                                )

                                // Linear dark screen gradient overlay for readability
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.85f)
                                                ),
                                                startY = 200f
                                            )
                                        )
                                )

                                // Interaction floating control sidebar
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Like icon
                                    IconButton(
                                        onClick = { viewModel.toggleLikeReel(reel.id) },
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                            .size(44.dp)
                                            .testTag("like_button_${reel.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Like reel",
                                            tint = if (isLiked) Color.Red else Color.White
                                        )
                                    }
                                    Text(
                                        text = "${reel.likesCount + if (isLiked) 1 else 0}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Bookmark/Save icon
                                    IconButton(
                                        onClick = { viewModel.toggleBookmarkReel(reel) },
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                            .size(44.dp)
                                            .testTag("bookmark_button_${reel.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Bookmark reel",
                                            tint = if (isBookmarked) Color.Yellow else Color.White
                                        )
                                    }
                                    Text(
                                        text = "${reel.savesCount + if (isBookmarked) 1 else 0}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Share button
                                    val context = LocalContext.current
                                    IconButton(
                                        onClick = {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, reel.title)
                                                putExtra(Intent.EXTRA_TEXT, "Check out this tech reel: ${reel.title} - ${reel.videoUrl}")
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share Tech Reel"))
                                        },
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                            .size(44.dp)
                                            .testTag("share_button_${reel.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share reel",
                                            tint = Color.White
                                        )
                                    }
                                }

                                // Narrative details overlay
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                        .fillMaxWidth(0.8f)
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = reel.category.uppercase(),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "@${reel.author}",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = reel.title,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (!reel.description.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = reel.description,
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 12.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inline VideoPlayer using Android native VideoView
 */
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    isMuted: Boolean = false
) {
    val context = LocalContext.current
    var isPrepared by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { ctx ->
                android.widget.VideoView(ctx).apply {
                    setVideoURI(Uri.parse(videoUrl))
                    setOnPreparedListener { mp ->
                        mp.isLooping = true
                        if (isMuted) {
                            mp.setVolume(0f, 0f)
                        } else {
                            mp.setVolume(1f, 1f)
                        }
                        start()
                        isPrepared = true
                    }
                }
            },
            update = { videoView ->
                // Updating volumes
            },
            modifier = Modifier.fillMaxSize()
        )

        if (!isPrepared) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
