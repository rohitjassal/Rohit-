package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.LogoConfig
import com.example.ui.components.DoraContentCard
import com.example.ui.components.ShimmerLoadingItem
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.SoftPurple
import com.example.ui.viewmodel.DoraViewModel
import com.example.ui.viewmodel.HubState
import java.util.Calendar

data class HubInfo(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val category: String
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DoraViewModel,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    // 20 State flows from VM
    val newsState by viewModel.newsState.collectAsStateWithLifecycle()
    val trendsState by viewModel.trendsState.collectAsStateWithLifecycle()
    val aiToolsState by viewModel.aiToolsState.collectAsStateWithLifecycle()
    val remoteJobsState by viewModel.remoteJobsState.collectAsStateWithLifecycle()
    val startupState by viewModel.startupState.collectAsStateWithLifecycle()
    val reelsState by viewModel.reelsState.collectAsStateWithLifecycle()
    val eventsState by viewModel.eventsState.collectAsStateWithLifecycle()
    val booksState by viewModel.booksState.collectAsStateWithLifecycle()
    val podcastsState by viewModel.podcastsState.collectAsStateWithLifecycle()
    val quotesState by viewModel.quotesState.collectAsStateWithLifecycle()
    val countriesState by viewModel.countriesState.collectAsStateWithLifecycle()
    val publicApisState by viewModel.publicApisState.collectAsStateWithLifecycle()
    val zooAnimalsState by viewModel.zooAnimalsState.collectAsStateWithLifecycle()
    val historyState by viewModel.historyState.collectAsStateWithLifecycle()
    val issPositionState by viewModel.issPositionState.collectAsStateWithLifecycle()
    val artWorksState by viewModel.artWorksState.collectAsStateWithLifecycle()
    val recipesState by viewModel.recipesState.collectAsStateWithLifecycle()
    val pollsState by viewModel.pollsState.collectAsStateWithLifecycle()
    val brainFactsState by viewModel.brainFactsState.collectAsStateWithLifecycle()
    val currenciesState by viewModel.currenciesState.collectAsStateWithLifecycle()

    var openedHub by remember { mutableStateOf<HubInfo?>(null) }
    var activeCategoryFilter by remember { mutableStateOf("All") }
    
    // Greeting based on time
    val greetingMessage = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Welcome back, good morning!"
            in 12..16 -> "Welcome back, good afternoon!"
            else -> "Welcome back, good evening!"
        }
    }

    val initials = remember(currentUser) {
        val user = currentUser
        if (user != null) {
            val parts = user.name.split(" ").filter { it.isNotEmpty() }
            if (parts.size >= 2) {
                "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}".uppercase()
            } else if (parts.isNotEmpty()) {
                parts[0].take(2).uppercase()
            } else {
                user.email.take(2).uppercase()
            }
        } else {
            "DL"
        }
    }

    // Comprehensive list of 20 Hub definitions with corresponding Material icon and vibrant themes
    val allHubsList = remember {
        listOf(
            HubInfo("news", "News Hub", "Universal tech news aggregator", Icons.Default.Newspaper, Color(0xFF3F51B5), "Tech"),
            HubInfo("trend", "Tech Trends", "Realtime HackerNews discussions", Icons.Default.TrendingUp, Color(0xFFE91E63), "Trends"),
            HubInfo("ai", "AI Tools", "Cutting edge developer AI engines", Icons.Default.AutoAwesome, Color(0xFF9C27B0), "AI"),
            HubInfo("job", "Remote Jobs", "Tech & startup global positions", Icons.Default.Work, Color(0xFF0D9488), "Career"),
            HubInfo("startup", "Startup Hub", "New hacker ventures & funding", Icons.Default.RocketLaunch, Color(0xFFE65100), "Business"),
            HubInfo("reels", "Reels Hub", "Tech & science byte reels", Icons.Default.PlayCircle, Color(0xFFEC407A), "Media"),
            HubInfo("events", "Events Radar", "PredictHQ premium event radar", Icons.Default.Event, Color(0xFF4CAF50), "Social"),
            HubInfo("books", "Books Hub", "Library of programming textbooks", Icons.Default.Book, Color(0xFF03A9F4), "Education"),
            HubInfo("podcasts", "Podcasts Hub", "iTunes technology podcast feeds", Icons.Default.Mic, Color(0xFFFF9800), "Media"),
            HubInfo("quotes", "Quotes Hub", "Inspirational wisdom quotes", Icons.Default.FormatQuote, Color(0xFF795548), "Writing"),
            HubInfo("country", "Geography Info", "Worldwide country explorer index", Icons.Default.Public, Color(0xFF673AB7), "Data"),
            HubInfo("apis", "Public APIs", "Indexed raw developer endpoints", Icons.Default.Code, Color(0xFF009688), "Data"),
            HubInfo("animal", "Animals Hub", "Ecology fauna facts generator", Icons.Default.Pets, Color(0xFF8BC34A), "Ecology"),
            HubInfo("history", "History Hub", "What occurred on this exact date", Icons.Default.History, Color(0xFFFF5722), "Data"),
            HubInfo("iss", "ISS Orbital", "Real time space station position", Icons.Default.Explore, Color(0xFF607D8B), "Space"),
            HubInfo("art", "Fine Arts Hub", "Metropolitan high res collection", Icons.Default.Palette, Color(0xFFE040FB), "Creative"),
            HubInfo("recipes", "Recipes Hub", "International meals repository", Icons.Default.Restaurant, Color(0xFF00C853), "Lifestyle"),
            HubInfo("polls", "Trivia Polls", "Futurama and geek query grids", Icons.Default.Poll, Color(0xFFFFC107), "Social"),
            HubInfo("brain", "Brain Facts", "Useless interesting facts flow", Icons.Default.Lightbulb, Color(0xFF00E5FF), "Ecology"),
            HubInfo("currency", "Currency FX", "Exogenous real time exchange rates", Icons.Default.AttachMoney, Color(0xFFFF3D00), "Finance")
        )
    }

    // Filter categories
    val filterGroups = listOf("All", "Tech", "Media", "Data", "Social", "Creative", "Lifestyle")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main Grid Page Content Structure
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Branding Sticky Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LogoConfig.DoraLogoIcon(
                        size = 38.dp,
                        cornerRadius = 10.dp
                    )

                    Column {
                        Text(
                            text = "Dora Library",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = greetingMessage,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.refreshAll() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .testTag("manual_refresh_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync channels online",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Quick Global search box
            OutlinedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onNavigateToSearch() }
                    .testTag("search_shortcut_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search shortcut icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Global search library or databases...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Category groups slider horizontal list
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterGroups.forEach { categoryValue ->
                    val isSelected = activeCategoryFilter == categoryValue
                    SuggestionChip(
                        onClick = { activeCategoryFilter = categoryValue },
                        label = {
                            Text(
                                text = categoryValue,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = if (isSelected) {
                            SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            SuggestionChipDefaults.suggestionChipColors()
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Text(
                text = "Interactive Library Hubs",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 22.dp, top = 2.dp, bottom = 8.dp)
            )

            // Dynamic grid layout displaying the 20 Hubs as medium graphic cards
            val filteredHubs = remember(activeCategoryFilter) {
                if (activeCategoryFilter == "All") allHubsList
                else allHubsList.filter { it.category.equals(activeCategoryFilter, ignoreCase = true) }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .testTag("hubs_vertical_grid_container"),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredHubs) { hub ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .clickable { openedHub = hub }
                            .testTag("hub_grid_card_${hub.id}"),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            // Floating graphic icon
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(hub.color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .align(Alignment.TopStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = hub.icon,
                                    contentDescription = hub.title,
                                    tint = hub.color,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            
                            // Badge indicating topic category
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = hub.category.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = hub.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = hub.description,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Animated Immersive Detail Overlay representing the opened hub content
        AnimatedVisibility(
            visible = openedHub != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            openedHub?.let { selected ->
                HubDetailOverlayContainer(
                    hub = selected,
                    viewModel = viewModel,
                    bookmarks = bookmarks,
                    onBackPress = { openedHub = null }
                )
            }
        }
    }
}

/**
 * Self-contained overlay representing any of the 20 customizable library sections
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubDetailOverlayContainer(
    hub: HubInfo,
    viewModel: DoraViewModel,
    bookmarks: List<com.example.data.local.Bookmark>,
    onBackPress: () -> Unit
) {
    var searchtext by remember { mutableStateOf("") }
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val newsState by viewModel.newsState.collectAsStateWithLifecycle()
    val trendsState by viewModel.trendsState.collectAsStateWithLifecycle()
    val aiToolsState by viewModel.aiToolsState.collectAsStateWithLifecycle()
    val remoteJobsState by viewModel.remoteJobsState.collectAsStateWithLifecycle()
    val startupState by viewModel.startupState.collectAsStateWithLifecycle()
    val reelsState by viewModel.reelsState.collectAsStateWithLifecycle()
    val eventsState by viewModel.eventsState.collectAsStateWithLifecycle()
    val booksState by viewModel.booksState.collectAsStateWithLifecycle()
    val podcastsState by viewModel.podcastsState.collectAsStateWithLifecycle()
    val quotesState by viewModel.quotesState.collectAsStateWithLifecycle()
    val countriesState by viewModel.countriesState.collectAsStateWithLifecycle()
    val publicApisState by viewModel.publicApisState.collectAsStateWithLifecycle()
    val zooAnimalsState by viewModel.zooAnimalsState.collectAsStateWithLifecycle()
    val historyState by viewModel.historyState.collectAsStateWithLifecycle()
    val issPositionState by viewModel.issPositionState.collectAsStateWithLifecycle()
    val artWorksState by viewModel.artWorksState.collectAsStateWithLifecycle()
    val recipesState by viewModel.recipesState.collectAsStateWithLifecycle()
    val pollsState by viewModel.pollsState.collectAsStateWithLifecycle()
    val brainFactsState by viewModel.brainFactsState.collectAsStateWithLifecycle()
    val currenciesState by viewModel.currenciesState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("hub_overlay_surface_${hub.id}"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Header Row block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onBackPress,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("overlay_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Return to Dashboard",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(hub.color.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = hub.icon,
                                    contentDescription = null,
                                    tint = hub.color,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = hub.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "Connected via Online Real-time JSON API",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Header share option to share current library category
                IconButton(
                    onClick = {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Dora Library ${hub.title}")
                                putExtra(Intent.EXTRA_TEXT, "Look at ${hub.title} on Dora Library! It integrates premium layouts with real-time dynamic backend APIs.")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share channel link"))
                        } catch (e: Exception) {}
                    },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share channel",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Local searchable text input bar inside the specific category context
            OutlinedTextField(
                value = searchtext,
                onValueChange = { searchtext = it },
                placeholder = { Text("Search inside ${hub.title}...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchtext.isNotEmpty()) {
                        IconButton(onClick = { searchtext = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .testTag("hub_local_search_input")
            )

            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dynamic view selector based on state of current hub index
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (hub.id) {
                    "news" -> RenderNewsList(newsState, searchtext, bookmarks, viewModel)
                    "trend" -> RenderHNItemList(trendsState, "trend", searchtext, bookmarks, viewModel)
                    "ai" -> RenderHNItemList(aiToolsState, "ai", searchtext, bookmarks, viewModel)
                    "job" -> RenderJobsList(remoteJobsState, searchtext, bookmarks, viewModel)
                    "startup" -> RenderHNItemList(startupState, "startup", searchtext, bookmarks, viewModel)
                    "reels" -> RenderReelsList(reelsState, searchtext, bookmarks, viewModel)
                    "events" -> RenderEventsList(eventsState, searchtext, bookmarks, viewModel)
                    "books" -> RenderBooksList(booksState, searchtext, bookmarks, viewModel)
                    "podcasts" -> RenderPodcastsList(podcastsState, searchtext, bookmarks, viewModel)
                    "quotes" -> RenderQuotesList(quotesState, searchtext, bookmarks, viewModel)
                    "country" -> RenderCountriesList(countriesState, searchtext, bookmarks, viewModel)
                    "apis" -> RenderApisList(publicApisState, searchtext, bookmarks, viewModel)
                    "animal" -> RenderAnimalsList(zooAnimalsState, searchtext, bookmarks, viewModel)
                    "history" -> RenderHistoryList(historyState, searchtext, bookmarks, viewModel)
                    "iss" -> RenderIssPosition(issPositionState, bookmarks, viewModel)
                    "art" -> RenderArtList(artWorksState, searchtext, bookmarks, viewModel)
                    "recipes" -> RenderRecipesList(recipesState, searchtext, bookmarks, viewModel)
                    "polls" -> RenderPollsList(pollsState, searchtext, bookmarks, viewModel)
                    "brain" -> RenderBrainTrivia(brainFactsState, bookmarks, viewModel)
                    "currency" -> RenderCurrenciesList(currenciesState, searchtext, bookmarks, viewModel)
                    else -> Text("Section index is being configured.", modifier = Modifier.padding(20.dp), color = Color.Gray)
                }
            }
        }
    }
}

// ==================== COMPONENT LIST RENDERING MODULES ====================

@Composable
fun RenderNewsList(state: HubState<List<Article>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.title.contains(query, ignoreCase = true) || (it.description?.contains(query, ignoreCase = true) ?: false)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { article ->
                        val articleUrl = article.url
                        val isBookmarked = bookmarks.any { it.id == articleUrl }
                        DoraContentCard(
                            title = article.title,
                            description = article.description,
                            sourceName = article.source?.name ?: "Technology News",
                            publishedDate = article.publishedAt?.substringBefore("T") ?: "Today",
                            timeLabel = article.author ?: "Anonymous",
                            category = "Technology",
                            imageUrl = article.urlToImage,
                            isSaved = isBookmarked,
                            onSaveToggle = { viewModel.toggleBookmarkArticle(article) },
                            actionSlot = { ViewBrowserButton(articleUrl) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderHNItemList(state: HubState<List<HNItem>>, type: String, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || (it.title?.contains(query, ignoreCase = true) ?: false)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val id = item.objectID
                        val isBookmarked = bookmarks.any { it.id == id }
                        val url = item.url ?: "https://news.ycombinator.com/item?id=$id"
                        DoraContentCard(
                            title = item.title ?: "Anonymous Venture Topic",
                            description = "Hacker News discussions. Author: ${item.author} | Points: ${item.points ?: 0} | Comments: ${item.num_comments ?: 0}",
                            sourceName = "YCombinator HN",
                            publishedDate = item.created_at?.substringBefore("T") ?: "Recent",
                            timeLabel = "HN / ${item.author ?: "Tech"}",
                            category = type.uppercase(),
                            imageUrl = null,
                            isSaved = isBookmarked,
                            onSaveToggle = { viewModel.toggleBookmarkHNItem(item, type) },
                            actionSlot = { ViewBrowserButton(url) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderJobsList(state: HubState<List<RemoteJob>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.title.contains(query, ignoreCase = true) || it.company.contains(query, ignoreCase = true)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { job ->
                        val isBookmarked = bookmarks.any { it.id == job.id }
                        DoraContentCard(
                            title = job.title,
                            description = "Remote job list. Salary: ${job.salary ?: "Competitive Rate"} | Tags: ${job.tags.take(4).joinToString(", ")}",
                            sourceName = job.company,
                            publishedDate = job.datePosted ?: "Recent",
                            timeLabel = job.location ?: "Remote USA/Global",
                            category = "Remote Job",
                            imageUrl = job.logoUrl,
                            isSaved = isBookmarked,
                            onSaveToggle = { viewModel.toggleBookmarkJob(job) },
                            actionSlot = { ViewBrowserButton(job.url) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderReelsList(state: HubState<List<ReelItem>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.title.contains(query, ignoreCase = true) || (it.description?.contains(query, ignoreCase = true) ?: false)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { reel ->
                        val isBookmarked = bookmarks.any { it.id == reel.id }
                        DoraContentCard(
                            title = reel.title,
                            description = reel.description ?: "Byte-sized technical breakdown loop on science.",
                            sourceName = "@${reel.author}",
                            publishedDate = "Recent",
                            timeLabel = reel.category,
                            category = "Tech Reel",
                            imageUrl = reel.thumbnailUrl,
                            isSaved = isBookmarked,
                            onSaveToggle = { viewModel.toggleBookmarkReel(reel) },
                            actionSlot = { ViewBrowserButton(reel.videoUrl) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderEventsList(state: HubState<List<PredictHqEvent>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.title.contains(query, ignoreCase = true) || (it.description?.contains(query, ignoreCase = true) ?: false)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val isBookmarked = bookmarks.any { it.id == item.id }
                        val urlVal = "https://api.predicthq.com/v1/events"
                        DoraContentCard(
                            title = item.title,
                            description = item.description ?: "Premium curated global conference, summit or tech event.",
                            sourceName = item.country ?: "Global Host",
                            publishedDate = item.start?.substringBefore("T") ?: "Future",
                            timeLabel = item.category ?: "Conference",
                            category = "Event Radar",
                            imageUrl = null,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(item.id, "event", item.title, item.description, item.country, item.category, urlVal, null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderBooksList(state: HubState<List<BookDoc>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.title.contains(query, ignoreCase = true) || (it.author_name?.any { n -> n.contains(query, ignoreCase = true) } ?: false)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val isBookmarked = bookmarks.any { it.id == item.key }
                        val coverUrl = if (item.cover_i != null) "https://covers.openlibrary.org/b/id/${item.cover_i}-M.jpg" else null
                        val authorText = item.author_name?.joinToString(", ") ?: "Anonymous Scientist"
                        val docUrl = "https://openlibrary.org" + (item.key ?: "/books")
                        DoraContentCard(
                            title = item.title,
                            description = "Author: $authorText | First Published: ${item.first_publish_year ?: "N/A"}",
                            sourceName = "OpenLibrary JSON",
                            publishedDate = item.first_publish_year?.toString() ?: "Recent",
                            timeLabel = "Book",
                            category = "Books Hub",
                            imageUrl = coverUrl,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(item.key ?: item.title, "book", item.title, "Author: $authorText", "OpenLibrary", item.first_publish_year?.toString(), docUrl, coverUrl)
                            },
                            actionSlot = { ViewBrowserButton(docUrl) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderPodcastsList(state: HubState<List<PodcastResult>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || (it.trackName?.contains(query, ignoreCase = true) ?: false) || (it.artistName?.contains(query, ignoreCase = true) ?: false)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val uniqueId = item.trackId?.toString() ?: item.trackName ?: ""
                        val isBookmarked = bookmarks.any { it.id == uniqueId }
                        DoraContentCard(
                            title = item.trackName ?: "Untitled Audio Podcast",
                            description = "Author/Artist Name: ${item.artistName ?: "Unknown"} | Genre: ${item.primaryGenreName ?: "Technology"}",
                            sourceName = "iTunes Podcast",
                            publishedDate = "Live Stream",
                            timeLabel = "Audio Feed",
                            category = "Podcasts Hub",
                            imageUrl = item.artworkUrl100,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(uniqueId, "podcast", item.trackName ?: "Podcast Item", item.artistName, item.trackId?.toString(), item.primaryGenreName, item.feedUrl ?: "", item.artworkUrl100)
                            },
                            actionSlot = { item.feedUrl?.let { ViewBrowserButton(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderQuotesList(state: HubState<List<QuoteItem>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.content.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val isBookmarked = bookmarks.any { it.id == item._id }
                        DoraContentCard(
                            title = "\"${item.content}\"",
                            description = "Author Statement: ${item.author} | Keywords: ${item.tags?.joinToString(", ")}",
                            sourceName = "Quotable API",
                            publishedDate = "Permanent",
                            timeLabel = "Wisdom quote",
                            category = "Quotes Hub",
                            imageUrl = null,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(item._id, "quote", item.content, "By ${item.author}", "Quotable API", null, "https://api.quotable.io", null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderCountriesList(state: HubState<List<CountryItem>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.name.common.contains(query, ignoreCase = true) || (it.capital?.any { c -> c.contains(query, ignoreCase = true) } ?: false)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val isBookmarked = bookmarks.any { it.id == item.cca2 }
                        DoraContentCard(
                            title = "${item.name.common} (${item.cca2})",
                            description = "Capital City: ${item.capital?.firstOrNull() ?: "N/A"} | Population: ${item.population} | Region status: ${item.region} (${item.subregion ?: ""})",
                            sourceName = "RestCountries Index",
                            publishedDate = "Census Data",
                            timeLabel = item.region,
                            category = "Geography",
                            imageUrl = item.flags.png,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(item.cca2, "country", item.name.common, "Capital: ${item.capital?.firstOrNull() ?: "N/A"}", "RestCountries", item.population.toString(), "https://restcountries.com", item.flags.png)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderApisList(state: HubState<List<PublicApiEntry>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || 
                (it.API?.contains(query, ignoreCase = true) == true) || 
                (it.Description?.contains(query, ignoreCase = true) == true) || 
                (it.Category?.contains(query, ignoreCase = true) == true)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val apiId = item.API ?: "Unknown API"
                        val isBookmarked = bookmarks.any { it.id == apiId }
                        val descStr = "Definition: ${item.Description ?: "None"} | Authentication: ${item.Auth ?: "None"} | HTTPS Support: ${if (item.HTTPS == true) "Required" else "No"}"
                        DoraContentCard(
                            title = apiId,
                            description = descStr,
                            sourceName = item.Category ?: "Public API",
                            publishedDate = "API Endpoint",
                            timeLabel = "Web Protocol",
                            category = "Developer APIs",
                            imageUrl = null,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(apiId, "api", apiId, item.Description, item.Category, item.Auth, item.Link ?: "https://api.publicapis.org", null)
                            },
                            actionSlot = { item.Link?.let { ViewBrowserButton(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderAnimalsList(state: HubState<List<ZooAnimal>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || 
                (it.name?.contains(query, ignoreCase = true) == true) || 
                (it.habitat?.contains(query, ignoreCase = true) == true)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val animalName = item.name ?: "Unknown Animal"
                        val isBookmarked = bookmarks.any { it.id == animalName }
                        val descStr = "Fauna trivia. Species classification: ${item.animal_type ?: "N/A"} | Nutrition diet: ${item.diet ?: "N/A"} | Natural habitat: ${item.habitat ?: "N/A"} | Biological lifespan: ${item.lifespan ?: "N/A"} years"
                        DoraContentCard(
                            title = animalName,
                            description = descStr,
                            sourceName = "Zoo Nature Org",
                            publishedDate = "Eco Trivia",
                            timeLabel = item.animal_type,
                            category = "Fauna Animals",
                            imageUrl = item.image_link,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(animalName, "animal", animalName, descStr, "Zoo Nature", null, "https://zoo-animal-api.herokuapp.com", item.image_link)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderHistoryList(state: HubState<List<HistoryEvent>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.text.contains(query, ignoreCase = true) || it.year.contains(query, ignoreCase = true)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val uniqueId = item.text.hashCode().toString()
                        val isBookmarked = bookmarks.any { it.id == uniqueId }
                        DoraContentCard(
                            title = "Historical event in Year: ${item.year}",
                            description = item.text,
                            sourceName = "MuffinLabs History Engine",
                            publishedDate = "Year ${item.year}",
                            timeLabel = "Chronicle Log",
                            category = "Historical Log",
                            imageUrl = null,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(uniqueId, "history", "Historical Year ${item.year}", item.text, "MuffinLabs History", null, "https://history.muffinlabs.com", null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderIssPosition(state: HubState<IssResponse>, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    val context = LocalContext.current
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val item = state.data
            val isBookmarked = bookmarks.any { it.id == "iss_position_${item.timestamp}" }
            val mapLat = item.iss_position?.latitude ?: "0.0"
            val mapLng = item.iss_position?.longitude ?: "0.0"
            val mapUrl = "https://www.openstreetmap.org/?mlat=$mapLat&mlon=$mapLng#map=5/$mapLat/$mapLng"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                DoraContentCard(
                    title = "ISS Station Orbital Position",
                    description = "NASA Space Station telemetry. Coordinates logged:\nLatitude: $mapLat\nLongitude: $mapLng\nLogged timestamp mark: ${item.timestamp ?: 0}",
                    sourceName = "NASA ISS Space Engine",
                    publishedDate = "Live telemetry",
                    timeLabel = "Real-time Tracker",
                    category = "ISS Position",
                    imageUrl = null,
                    isSaved = isBookmarked,
                    onSaveToggle = {
                        viewModel.toggleBookmarkCustom("iss_position_${item.timestamp}", "iss", "ISS Position ${item.timestamp}", "Coordinate Lat: $mapLat Lng: $mapLng", "NASA ISS tracker", null, mapUrl, null)
                    },
                    actionSlot = {
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            },
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text("📍 View ISS Location on Live Map", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RenderArtList(state: HubState<List<ArtObjectDetail>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.title.contains(query, ignoreCase = true) || (it.artistDisplayName?.contains(query, ignoreCase = true) ?: false)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val isBookmarked = bookmarks.any { it.id == item.objectID.toString() }
                        val metUrl = "https://www.metmuseum.org/art/collection/search/" + item.objectID
                        DoraContentCard(
                            title = item.title,
                            description = "Artist name: ${item.artistDisplayName ?: "Unknown Artisan"} | Year created: ${item.objectDate ?: "N/A"} | Department registry: ${item.department ?: "Fine Arts Museum"}",
                            sourceName = item.repository ?: "The Metropolitan Museum of Art",
                            publishedDate = item.objectDate ?: "Classic Epoch",
                            timeLabel = "Fine Canvas Art",
                            category = "Arts Hub",
                            imageUrl = item.primaryImage,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(item.objectID.toString(), "art", item.title, "Artist: ${item.artistDisplayName ?: "Unknown"}", item.repository ?: "Museum Registry", item.department, metUrl, item.primaryImage)
                            },
                            actionSlot = { ViewBrowserButton(metUrl) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderRecipesList(state: HubState<List<MealRecipe>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.strMeal.contains(query, ignoreCase = true) || (it.strCategory?.contains(query, ignoreCase = true) ?: false)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val isBookmarked = bookmarks.any { it.id == item.idMeal }
                        val steps = item.strInstructions ?: "Cooking instructions represent secret kitchen formulas."
                        DoraContentCard(
                            title = item.strMeal,
                            description = "Cuisine tags: ${item.strArea ?: "Universal"} | Classification: ${item.strCategory ?: "Meal Recipe"}\n\nPreparation Instructions:\n$steps",
                            sourceName = "TheMealDB",
                            publishedDate = "Culinary Guide",
                            timeLabel = "Food Recipe",
                            category = "Recipes Hub",
                            imageUrl = item.strMealThumb,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(item.idMeal, "recipe", item.strMeal, steps.take(150), "TheMealDB", item.strCategory, item.strYoutube ?: "https://www.themealdb.com", item.strMealThumb)
                            },
                            actionSlot = {
                                item.strYoutube?.let { yt ->
                                    if (yt.isNotEmpty()) {
                                        ViewBrowserButton(yt, text = "📺 Play Cooking Video on YouTube")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderPollsList(state: HubState<List<FuturamaQuestion>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter {
                query.isEmpty() || it.question.contains(query, ignoreCase = true)
            }
            if (filtered.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered) { item ->
                        val isBookmarked = bookmarks.any { it.id == item.id.toString() }
                        DoraContentCard(
                            title = "Surveys Query: ${item.question}",
                            description = "Trivia choices: ${item.possibleAnswers?.joinToString(" | ") ?: "True / False"}\n\nCorrect trivia response code: ${item.correctAnswer ?: "Classified"}",
                            sourceName = "Futurama Surveys API",
                            publishedDate = "Quiz Entry",
                            timeLabel = "Surveys Poll",
                            category = "Polls Hub",
                            imageUrl = null,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(item.id.toString(), "poll", "Trivia Poll #${item.id}", item.question, "Futurama Trivia", null, "https://api.sampleapis.com", null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderBrainTrivia(state: HubState<UselessFact>, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val item = state.data
            val isBookmarked = bookmarks.any { it.id == item.id }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                DoraContentCard(
                    title = "Did You Know?",
                    description = item.text,
                    sourceName = item.source ?: "Joseph Facts Organizer",
                    publishedDate = "General knowledge",
                    timeLabel = "Random Mind fact",
                    category = "Facts trivia",
                    imageUrl = null,
                    isSaved = isBookmarked,
                    onSaveToggle = {
                        viewModel.toggleBookmarkCustom(item.id, "brain_fact", "Useless Random Fact", item.text, "UselessFacts", item.source, item.source_url ?: "https://uselessfacts.jsph.pl", null)
                    },
                    actionSlot = {
                        item.source_url?.let { ViewBrowserButton(it, "🧠 View Verified Source Facts") }
                    }
                )
            }
        }
    }
}

@Composable
fun RenderCurrenciesList(state: HubState<ExchangeRateResponse>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val item = state.data
            val baseCode = item.base_code ?: "USD"
            val rawRates = item.rates ?: emptyMap()
            
            val filteredRates = remember(rawRates, query) {
                if (query.isEmpty()) rawRates.toList()
                else rawRates.toList().filter { it.first.contains(query, ignoreCase = true) }
            }

            if (filteredRates.isEmpty()) EmptyResultsView()
            else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "Base Anchor Currency: $baseCode\nUTC Updated Last Mark: ${item.time_last_update_utc ?: "N/A"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    items(filteredRates) { pair ->
                        val code = pair.first
                        val rate = pair.second
                        val uniqueId = "rates_${baseCode}_${code}"
                        val isBookmarked = bookmarks.any { it.id == uniqueId }
                        DoraContentCard(
                            title = "Base Unit: 1 $baseCode = $rate $code",
                            description = "Currency Symbol Exchange Code: $code | Instant Conversion valuation index.",
                            sourceName = "Exogenous ER Exchange",
                            publishedDate = "Daily FX mark",
                            timeLabel = "FX conversion",
                            category = "FX Currency",
                            imageUrl = null,
                            isSaved = isBookmarked,
                            onSaveToggle = {
                                viewModel.toggleBookmarkCustom(uniqueId, "currency", "Base: $baseCode Rate to $code", "1 USD = $rate $code", "FX Rates API", null, "https://open.er-api.com", null)
                            }
                        )
                    }
                }
            }
        }
    }
}

// ==================== HELPER PRESENTATIONAL SUBVIEWS ====================

@Composable
fun LoadingSkeletons() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(4) {
            ShimmerLoadingItem()
        }
    }
}

@Composable
fun EmptyResultsView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No records matched the current query query.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ErrorView(errorMsg: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Online synchrony failed.",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Trigger Backup offline Fallback", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ViewBrowserButton(url: String, text: String = "Read Article / Document") {
    val context = LocalContext.current
    Button(
        onClick = {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {}
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
