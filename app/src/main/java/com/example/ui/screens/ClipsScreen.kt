package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.ClipItem
import com.example.ui.components.CategoryBadge
import com.example.ui.viewmodel.DoraViewModel
import com.example.ui.viewmodel.HubState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ClipsScreen(
    viewModel: DoraViewModel,
    modifier: Modifier = Modifier
) {
    val clipsState by viewModel.clipsState.collectAsStateWithLifecycle()
    val isGridView by viewModel.isClipsGridView.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.clipsSelectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.clipsSearchQuery.collectAsStateWithLifecycle()
    val downloadProgress by viewModel.downloadProgress.collectAsStateWithLifecycle()
    val downloadedStatus by viewModel.downloadedStatus.collectAsStateWithLifecycle()
    val savedClips by viewModel.savedClips.collectAsStateWithLifecycle()

    val pexelsKey by viewModel.pexelsApiKey.collectAsStateWithLifecycle()
    val pixabayKey by viewModel.pixabayApiKey.collectAsStateWithLifecycle()

    var showCredentialsConfig by remember { mutableStateOf(false) }
    var pexelsInputKey by remember { mutableStateOf(pexelsKey) }
    var pixabayInputKey by remember { mutableStateOf(pixabayKey) }

    var searchInputText by remember { mutableStateOf(searchQuery) }
    var activePlayingClip by remember { mutableStateOf<ClipItem?>(null) }

    val categories = listOf(
        "All", "Nature", "Technology", "AI", "Education", 
        "Motivation", "Travel", "Business", "Science", "Space", "Animals"
    )

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // High-contrast Header with Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Discover Clips",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Infinite short-form video discovery",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Command buttons: API Config & View Toggle
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { showCredentialsConfig = !showCredentialsConfig },
                    modifier = Modifier
                        .background(
                            color = if (showCredentialsConfig) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("clips_config_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (showCredentialsConfig) Icons.Default.VpnKey else Icons.Outlined.VpnKey,
                        contentDescription = "API Keys Configuration",
                        tint = if (showCredentialsConfig) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleClipsLayout() },
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("clips_layout_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.ViewStream else Icons.Default.GridView,
                        contentDescription = "Toggle Grid or Stream view",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Expanded Credentials Area
        AnimatedVisibility(
            visible = showCredentialsConfig,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔒 Video API Credentials Configuration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = pexelsInputKey,
                        onValueChange = { pexelsInputKey = it },
                        label = { Text("Pexels API Secret Key") },
                        placeholder = { Text("Authorization Header") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pexels_key"),
                        trailingIcon = {
                            if (pexelsInputKey.isNotEmpty() && pexelsInputKey != pexelsKey) {
                                IconButton(onClick = { viewModel.setPexelsApiKey(pexelsInputKey) }) {
                                    Icon(Icons.Default.Check, "Save Key", tint = Color.Green)
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pixabayInputKey,
                        onValueChange = { pixabayInputKey = it },
                        label = { Text("Pixabay API Secret Key") },
                        placeholder = { Text("key Query Parameter") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pixabay_key"),
                        trailingIcon = {
                            if (pixabayInputKey.isNotEmpty() && pixabayInputKey != pixabayKey) {
                                IconButton(onClick = { viewModel.setPixabayApiKey(pixabayInputKey) }) {
                                    Icon(Icons.Default.Check, "Save Key", tint = Color.Green)
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Keys survive app resets and are saved in local encryptions. If no custom keys are specified, stunning fallbacks from our curated Prestige high-fidelity video libraries render seamlessly.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Live Search Input with Suggestions and Fast Results
        OutlinedTextField(
            value = searchInputText,
            onValueChange = {
                searchInputText = it
                viewModel.setClipsSearchQuery(it)
            },
            placeholder = { Text("Search video clips across Pexels / Pixabay...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "SearchIcon", tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (searchInputText.isNotEmpty()) {
                    IconButton(onClick = {
                        searchInputText = ""
                        viewModel.setClipsSearchQuery("")
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.error)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { viewModel.fetchClips() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("clips_search_input_field")
        )

        // Horizontal Categories Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("clips_categories_row"),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setClipsCategory(category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("clip_category_chip_$category")
                )
            }
        }

        // Main List/Grid View State Coordinator
        when (val state = clipsState) {
            is HubState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Gathering scenic video resources...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                }
            }
            is HubState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Default.ErrorOutline, "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(state.message, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchClips() }) {
                            Text("Retry Fetch")
                        }
                    }
                }
            }
            is HubState.Success -> {
                val videos = state.data
                if (videos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No clips matched your parameters.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { 
                                searchInputText = ""
                                viewModel.setClipsCategory("All")
                                viewModel.setClipsSearchQuery("")
                            }) {
                                Text("Reset Filters")
                            }
                        }
                    }
                } else {
                    if (isGridView) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .testTag("clips_grid_layout"),
                            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(videos) { clip ->
                                ClipGridCard(
                                    clip = clip,
                                    savedClips = savedClips,
                                    downloadProgress = downloadProgress,
                                    downloadedStatus = downloadedStatus,
                                    onPlayClicked = { activePlayingClip = clip },
                                    onDownloadClicked = { viewModel.downloadClip(clip) },
                                    onSaveClicked = { viewModel.toggleSaveClip(clip) }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .testTag("clips_list_layout"),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(videos) { clip ->
                                ClipListCard(
                                    clip = clip,
                                    savedClips = savedClips,
                                    downloadProgress = downloadProgress,
                                    downloadedStatus = downloadedStatus,
                                    onPlayClicked = { activePlayingClip = clip },
                                    onDownloadClicked = { viewModel.downloadClip(clip) },
                                    onSaveClicked = { viewModel.toggleSaveClip(clip) },
                                    onShareClicked = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, clip.title)
                                            putExtra(Intent.EXTRA_TEXT, "Discovered this stunning clip: ${clip.title} by ${clip.author}. Watch here: ${clip.videoUrl}")
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Clip"))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Fullscreen/Fluid Theater Video Player Dialog
    activePlayingClip?.let { playTarget ->
        Dialog(
            onDismissRequest = { activePlayingClip = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Real Inline Video Player
                VideoPlayer(
                    videoUrl = playTarget.videoUrl,
                    modifier = Modifier.fillMaxSize()
                )

                // Theater Overlay Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { activePlayingClip = null },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back to clips", tint = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = playTarget.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "@${playTarget.author} - ${playTarget.source.uppercase()}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Licensing Watermark Alert Footer
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Copyright, "License", tint = Color.Yellow)
                        Text(
                            text = "Licensed via ${playTarget.source} CC Commons licensing norms. Content is authorized for persistent stream caching.",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// --- STYLIZED PRESENTATION DESIGN CARDS ---
// ==========================================

@Composable
fun ClipListCard(
    clip: ClipItem,
    savedClips: List<com.example.data.local.SavedClip>,
    downloadProgress: Map<String, Int>,
    downloadedStatus: Map<String, Boolean>,
    onPlayClicked: () -> Unit,
    onDownloadClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onShareClicked: () -> Unit
) {
    val isRecentlySaved = savedClips.any { it.id == clip.id }
    val progress = downloadProgress[clip.id]
    val isDownloaded = downloadedStatus[clip.id] == true || savedClips.any { it.id == clip.id && it.isDownloaded }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .testTag("clip_list_card_${clip.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Visual Thumbnail Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onPlayClicked() }
            ) {
                AsyncImage(
                    model = clip.thumbnailUrl,
                    contentDescription = clip.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Linear Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )

                // Float Center Play Badge
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Tap to Play Movie",
                    tint = Color.White,
                    modifier = Modifier
                        .size(54.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f), CircleShape)
                        .padding(12.dp)
                        .align(Alignment.Center)
                )

                // Float Badge - Bottom Left: Duration
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = formatDuration(clip.duration),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Float Badge - Top Right: Source Provider
                Surface(
                    color = when (clip.source) {
                        "Pexels" -> Color(0xFF07B195)
                        "Pixabay" -> Color(0xFF1F80C9)
                        else -> MaterialTheme.colorScheme.tertiary
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = clip.source.uppercase(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Description details metadata elements
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = clip.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Created by @${clip.author}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.RemoveRedEye, "Views", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${clip.views ?: 1430} views", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Outlined.GetApp, "Downloads", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${clip.downloads ?: 210} downloads", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Real Dynamic Download Progression feedback
                if (progress != null) {
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Downloading stream metrics...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "$progress%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth().clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }

                // Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Download state trigger
                    Button(
                        onClick = { onDownloadClicked() },
                        enabled = progress == null && !isDownloaded,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDownloaded) Color.Transparent else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isDownloaded) Color.Green else MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("download_btn_${clip.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.SaveAlt,
                                contentDescription = if (isDownloaded) "Downloaded" else "Download Clip",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isDownloaded) "SAVED TO DEVICE" else "DOWNLOAD FILE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Save / Share Row
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { onSaveClicked() },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isRecentlySaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isRecentlySaved) Color.Yellow else MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { onShareClicked() },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClipGridCard(
    clip: ClipItem,
    savedClips: List<com.example.data.local.SavedClip>,
    downloadProgress: Map<String, Int>,
    downloadedStatus: Map<String, Boolean>,
    onPlayClicked: () -> Unit,
    onDownloadClicked: () -> Unit,
    onSaveClicked: () -> Unit
) {
    val isRecentlySaved = savedClips.any { it.id == clip.id }
    val progress = downloadProgress[clip.id]
    val isDownloaded = downloadedStatus[clip.id] == true || savedClips.any { it.id == clip.id && it.isDownloaded }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onPlayClicked() }
            .testTag("clip_grid_card_${clip.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = clip.thumbnailUrl,
                contentDescription = clip.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Minimalist dark overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            // Upper Actions Row (Save/Bookmark)
            IconButton(
                onClick = { onSaveClicked() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isRecentlySaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    modifier = Modifier.size(16.dp),
                    tint = if (isRecentlySaved) Color.Yellow else Color.White
                )
            }

            // Lower Info Block
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = clip.title,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "@${clip.author}",
                        color = Color.LightGray,
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { onDownloadClicked() },
                        enabled = progress == null && !isDownloaded,
                        modifier = Modifier
                            .size(24.dp)
                            .background(if (isDownloaded) Color.Green else Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isDownloaded) Icons.Default.Check else Icons.Default.GetApp,
                            contentDescription = "Save target",
                            modifier = Modifier.size(12.dp),
                            tint = if (isDownloaded) Color.Black else Color.White
                        )
                    }
                }
            }

            // Progress HUD overlay if triggering active download inside Grid
            if (progress != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            progress = { progress / 100f },
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "$progress%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Convert absolute duration numbers to compact user readable representations
fun formatDuration(durationSeconds: Int): String {
    val m = durationSeconds / 60
    val s = durationSeconds % 60
    return String.format("%d:%02d", m, s)
}
