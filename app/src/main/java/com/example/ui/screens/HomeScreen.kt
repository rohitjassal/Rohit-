package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.launch
import java.util.Calendar

data class HubInfo(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val category: String
)

data class CountryModel(
    val code: String,
    val name: String,
    val states: List<String>,
    val cities: Map<String, List<String>>
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DoraViewModel,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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

    // Dialog state controllers for location & date-range overrides
    var showLocationDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }

    val currentCity by viewModel.currentCity.collectAsStateWithLifecycle()
    val currentCountryName by viewModel.currentCountryName.collectAsStateWithLifecycle()
    val currentDateFilter by viewModel.dateFilter.collectAsStateWithLifecycle()
    
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(310.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(vertical = 36.dp, horizontal = 24.dp)
                ) {
                    LogoConfig.DoraWordmarkLogo(
                        iconSize = 68.dp,
                        textSize = 34.sp,
                        subtitleSize = 13.sp,
                        isCentered = false,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = MaterialTheme.colorScheme.primary) },
                    label = { Text("Main Library Hubs", fontWeight = FontWeight.Bold) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp).testTag("drawer_home_nav")
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.MyLocation, contentDescription = "Location Settings", tint = MaterialTheme.colorScheme.primary) },
                    label = { Text("Global Geo System", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            showLocationDialog = true
                        }
                    },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp).testTag("drawer_location_nav")
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Date Filters", tint = MaterialTheme.colorScheme.primary) },
                    label = { Text("Chronological Filters", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            showDateDialog = true
                        }
                    },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp).testTag("drawer_date_nav")
                )

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "ACTIVE USER PREFERENCES",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "📍 Local Zone: $currentCity, $currentCountryName",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "📅 Filter Selection: ${currentDateFilter.replace("_", " ")}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) {
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
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("drawer_menu_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Drawer menu",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

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

            // Dynamic Active Geo & Clock preference indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { showLocationDialog = true },
                    label = { Text("📍 $currentCity, $currentCountryName", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        labelColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("home_location_indicator_chip")
                )

                AssistChip(
                    onClick = { showDateDialog = true },
                    label = { Text("📅 Filter: ${currentDateFilter.replace("_", " ")}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f),
                        labelColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("home_date_indicator_chip")
                )
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

        if (showLocationDialog) {
            LocationSelectorDialog(
                onDismiss = { showLocationDialog = false },
                viewModel = viewModel
            )
        }

        if (showDateDialog) {
            DateFilterDialog(
                onDismiss = { showDateDialog = false },
                viewModel = viewModel
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

            // Real-Time Sync Indicator, Active Location & Date Range Indicator Banner
            val lastSyncedTimes by viewModel.lastUpdatedTimes.collectAsStateWithLifecycle()
            val lastSyncTime = lastSyncedTimes[hub.id]
            val lastSyncLabel = if (lastSyncTime != null) {
                val diffSeconds = (System.currentTimeMillis() - lastSyncTime) / 1000
                when {
                    diffSeconds < 5 -> "Just updated"
                    diffSeconds < 60 -> "$diffSeconds seconds ago"
                    else -> "${diffSeconds / 60}m ago"
                }
            } else {
                "Synced"
            }

            val currentCountryName by viewModel.currentCountryName.collectAsStateWithLifecycle()
            val currentCityName by viewModel.currentCity.collectAsStateWithLifecycle()
            val activeFilterName by viewModel.dateFilter.collectAsStateWithLifecycle()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(6.dp).background(Color.Green, CircleShape))
                    Text(
                        text = "Real-Time Sync: $lastSyncLabel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Text(
                    text = "📍 $currentCityName ($currentCountryName) • 📅 $activeFilterName",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
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

fun isDateWithinFilter(
    dateStr: String?,
    timestamp: Long? = null,
    activeFilter: String,
    customStart: Long?,
    customEnd: Long?
): Boolean {
    if (activeFilter == "ALL") return true
    
    // 1. Resolve target millis
    val targetMillis: Long = when {
        timestamp != null && timestamp > 0L -> timestamp
        !dateStr.isNullOrEmpty() -> {
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "yyyy-MM"
            )
            var resolvedTime: Long? = null
            for (fmt in formats) {
                try {
                    val sdf = java.text.SimpleDateFormat(fmt, java.util.Locale.US)
                    sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                    resolvedTime = sdf.parse(dateStr)?.time
                    if (resolvedTime != null) break
                } catch (e: Exception) {
                    // try next format
                }
            }
            resolvedTime ?: return true // If unparseable, don't filter out to be safe
        }
        else -> return true // If no date info, keep it
    }

    // 2. Resolve bounds
    val now = System.currentTimeMillis()
    val calInstance = java.util.Calendar.getInstance()
    calInstance.timeInMillis = now
    
    val todayStart = calInstance.apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todayEnd = todayStart + 24 * 60 * 60 * 1000L - 1

    return when (activeFilter) {
        "TODAY" -> targetMillis in todayStart..todayEnd
        "YESTERDAY" -> {
            val yesterdayStart = todayStart - 24 * 60 * 60 * 1000L
            val yesterdayEnd = todayStart - 1
            targetMillis in yesterdayStart..yesterdayEnd
        }
        "LAST_7_DAYS" -> {
            val start = now - 7 * 24 * 60 * 60 * 1000L
            targetMillis in start..now
        }
        "LAST_30_DAYS" -> {
            val start = now - 30L * 24 * 60 * 60 * 1000L
            targetMillis in start..now
        }
        "THIS_MONTH" -> {
            calInstance.timeInMillis = now
            val currentMonth = calInstance.get(java.util.Calendar.MONTH)
            val currentYear = calInstance.get(java.util.Calendar.YEAR)
            
            val targetCal = java.util.Calendar.getInstance()
            targetCal.timeInMillis = targetMillis
            targetCal.get(java.util.Calendar.MONTH) == currentMonth && targetCal.get(java.util.Calendar.YEAR) == currentYear
        }
        "THIS_YEAR" -> {
            calInstance.timeInMillis = now
            val currentYear = calInstance.get(java.util.Calendar.YEAR)
            
            val targetCal = java.util.Calendar.getInstance()
            targetCal.timeInMillis = targetMillis
            targetCal.get(java.util.Calendar.YEAR) == currentYear
        }
        "CUSTOM" -> {
            val start = customStart ?: 0L
            val end = customEnd ?: Long.MAX_VALUE
            targetMillis in start..end
        }
        else -> true
    }
}

@Composable
fun RenderNewsList(state: HubState<List<Article>>, query: String, bookmarks: List<com.example.data.local.Bookmark>, viewModel: DoraViewModel) {
    val dateFilter by viewModel.dateFilter.collectAsStateWithLifecycle()
    val customStart by viewModel.customDateStart.collectAsStateWithLifecycle()
    val customEnd by viewModel.customDateEnd.collectAsStateWithLifecycle()

    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter { article ->
                val matchesQuery = query.isEmpty() || article.title.contains(query, ignoreCase = true) || (article.description?.contains(query, ignoreCase = true) ?: false)
                val matchesDate = isDateWithinFilter(article.publishedAt, null, dateFilter, customStart, customEnd)
                matchesQuery && matchesDate
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
    val dateFilter by viewModel.dateFilter.collectAsStateWithLifecycle()
    val customStart by viewModel.customDateStart.collectAsStateWithLifecycle()
    val customEnd by viewModel.customDateEnd.collectAsStateWithLifecycle()

    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter { item ->
                val matchesQuery = query.isEmpty() || (item.title?.contains(query, ignoreCase = true) ?: false)
                val matchesDate = isDateWithinFilter(item.created_at, null, dateFilter, customStart, customEnd)
                matchesQuery && matchesDate
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
    val dateFilter by viewModel.dateFilter.collectAsStateWithLifecycle()
    val customStart by viewModel.customDateStart.collectAsStateWithLifecycle()
    val customEnd by viewModel.customDateEnd.collectAsStateWithLifecycle()

    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter { job ->
                val matchesQuery = query.isEmpty() || job.title.contains(query, ignoreCase = true) || job.company.contains(query, ignoreCase = true)
                val matchesDate = isDateWithinFilter(null, job.timestamp, dateFilter, customStart, customEnd)
                matchesQuery && matchesDate
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
    val dateFilter by viewModel.dateFilter.collectAsStateWithLifecycle()
    val customStart by viewModel.customDateStart.collectAsStateWithLifecycle()
    val customEnd by viewModel.customDateEnd.collectAsStateWithLifecycle()

    when (state) {
        is HubState.Loading -> LoadingSkeletons()
        is HubState.Error -> ErrorView(state.message) { viewModel.refreshAll() }
        is HubState.Success -> {
            val filtered = state.data.filter { item ->
                val matchesQuery = query.isEmpty() || item.title.contains(query, ignoreCase = true) || (item.description?.contains(query, ignoreCase = true) ?: false)
                val matchesDate = isDateWithinFilter(item.start, null, dateFilter, customStart, customEnd)
                matchesQuery && matchesDate
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

// ==================== GLOBAL LOCATION ENGINE & SELECTOR ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectorDialog(
    onDismiss: () -> Unit,
    viewModel: DoraViewModel
) {
    val currentCountryName by viewModel.currentCountryName.collectAsStateWithLifecycle()
    val currentStateVal by viewModel.currentState.collectAsStateWithLifecycle()
    val currentCityName by viewModel.currentCity.collectAsStateWithLifecycle()
    val useCurrent by viewModel.useCurrentLocation.collectAsStateWithLifecycle()

    var detectLocState by remember { mutableStateOf(useCurrent) }
    var selectedCountry by remember { mutableStateOf(currentCountryName) }
    var selectedState by remember { mutableStateOf(currentStateVal) }
    var selectedCity by remember { mutableStateOf(currentCityName) }

    // static hierarchical database of countries, states, and cities
    val countriesData: List<CountryModel> = remember {
        listOf(
            CountryModel("in", "India", listOf("Karnataka", "Delhi", "Maharashtra", "Tamil Nadu", "Telangana"), mapOf(
                "Karnataka" to listOf("Bengaluru", "Mysore", "Hubli"),
                "Delhi" to listOf("New Delhi", "Dwarka", "Rohini"),
                "Maharashtra" to listOf("Mumbai", "Pune", "Nagpur"),
                "Tamil Nadu" to listOf("Chennai", "Coimbatore", "Madurai"),
                "Telangana" to listOf("Hyderabad", "Warangal", "Nizamabad")
            )),
            CountryModel("us", "United States", listOf("California", "New York", "Texas", "Washington", "Florida"), mapOf(
                "California" to listOf("San Francisco", "Los Angeles", "San Diego"),
                "New York" to listOf("New York City", "Buffalo", "Rochester"),
                "Texas" to listOf("Houston", "Austin", "Dallas"),
                "Washington" to listOf("Seattle", "Spokane", "Tacoma"),
                "Florida" to listOf("Miami", "Orlando", "Tampa")
            )),
            CountryModel("gb", "United Kingdom", listOf("England", "Scotland", "Wales", "Northern Ireland"), mapOf(
                "England" to listOf("London", "Manchester", "Birmingham"),
                "Scotland" to listOf("Edinburgh", "Glasgow", "Aberdeen"),
                "Wales" to listOf("Cardiff", "Swansea", "Newport"),
                "Northern Ireland" to listOf("Belfast", "Derry", "Armagh")
            )),
            CountryModel("ca", "Canada", listOf("Ontario", "Quebec", "British Columbia", "Alberta"), mapOf(
                "Ontario" to listOf("Toronto", "Ottawa", "Mississauga"),
                "Quebec" to listOf("Montreal", "Quebec City", "Laval"),
                "British Columbia" to listOf("Vancouver", "Victoria", "Burnaby"),
                "Alberta" to listOf("Calgary", "Edmonton", "Red Deer")
            )),
            CountryModel("au", "Australia", listOf("New South Wales", "Victoria", "Queensland", "Western Australia"), mapOf(
                "New South Wales" to listOf("Sydney", "Newcastle", "Wollongong"),
                "Victoria" to listOf("Melbourne", "Geelong", "Ballarat"),
                "Queensland" to listOf("Brisbane", "Gold Coast", "Cairns"),
                "Western Australia" to listOf("Perth", "Fremantle", "Bunbury")
            )),
            CountryModel("de", "Germany", listOf("Bavaria", "Berlin", "Hamburg", "Saxony"), mapOf(
                "Bavaria" to listOf("Munich", "Nuremberg", "Augsburg"),
                "Berlin" to listOf("Berlin Center", "East Berlin", "Potsdam"),
                "Hamburg" to listOf("Hamburg Altona", "Harburg", "Bergedorf"),
                "Saxony" to listOf("Dresden", "Leipzig", "Chemnitz")
            )),
            CountryModel("fr", "France", listOf("Île-de-France", "Provence-Alpes", "Rhône-Alpes"), mapOf(
                "Île-de-France" to listOf("Paris", "Boulogne", "Saint-Denis"),
                "Provence-Alpes" to listOf("Marseille", "Nice", "Toulon"),
                "Rhône-Alpes" to listOf("Lyon", "Grenoble", "Saint-Étienne")
            )),
            CountryModel("jp", "Japan", listOf("Tokyo", "Osaka", "Kyoto", "Hokkaido"), mapOf(
                "Tokyo" to listOf("Shinjuku", "Shibuya", "Ginza"),
                "Osaka" to listOf("Umeda", "Namba", "Tennōji"),
                "Kyoto" to listOf("Shimogyo", "Nakagyo", "Kamigyo"),
                "Hokkaido" to listOf("Sapporo", "Asahikawa", "Hakodate")
            )),
            CountryModel("sg", "Singapore", listOf("Central Region", "East Region", "North Region"), mapOf(
                "Central Region" to listOf("Downtown Core", "Bukit Merah", "Queenstown"),
                "East Region" to listOf("Tampines", "Bedok", "Pasir Ris"),
                "North Region" to listOf("Woodlands", "Yishun", "Sembawang")
            )),
            CountryModel("ae", "UAE", listOf("Dubai", "Abu Dhabi", "Sharjah"), mapOf(
                "Dubai" to listOf("Deira", "Dubai Marina", "Downtown Dubai"),
                "Abu Dhabi" to listOf("Al Khalidiyah", "Yas Island", "Khalifa City"),
                "Sharjah" to listOf("Al Majaz", "Al Nahda", "Muwaileh")
            ))
        )
    }

    val currentCountryObject: CountryModel = countriesData.find { it.name == selectedCountry } ?: countriesData[1]

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (detectLocState) {
                        viewModel.autoDetectLocation()
                    } else {
                        val itemCode = currentCountryObject.code
                        viewModel.updateLocation(itemCode, selectedCountry, selectedState, selectedCity, false)
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("apply_location_btn")
            ) {
                Text("Apply Location")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MyLocation, contentDescription = "Location Settings", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Global Geo System", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Provide coordinates or pick specific global regions to query location-aware events, trends, jobs, and news automatically.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { detectLocState = true }
                        ) {
                            RadioButton(selected = detectLocState, onClick = { detectLocState = true })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Auto Detect Location", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Matches local system timezone & preferences", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        
                        HorizontalDivider()
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { detectLocState = false }
                        ) {
                            RadioButton(selected = !detectLocState, onClick = { detectLocState = false })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Select Manually", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Specify country, state/province, and city", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                if (!detectLocState) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Country Selection", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            countriesData.forEach { country ->
                                FilterChip(
                                    selected = selectedCountry == country.name,
                                    onClick = {
                                        selectedCountry = country.name
                                        selectedState = country.states.first()
                                        selectedCity = country.cities[selectedState]?.first() ?: ""
                                    },
                                    label = { Text(country.name) }
                                )
                            }
                        }

                        Text("State/Province Selection", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentCountryObject.states.forEach { stateVal ->
                                FilterChip(
                                    selected = selectedState == stateVal,
                                    onClick = {
                                        selectedState = stateVal
                                        selectedCity = currentCountryObject.cities[stateVal]?.first() ?: ""
                                    },
                                    label = { Text(stateVal) }
                                )
                            }
                        }

                        Text("City Selection", style = MaterialTheme.typography.labelMedium)
                        val citiesList = currentCountryObject.cities[selectedState] ?: emptyList()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            citiesList.forEach { cityVal ->
                                FilterChip(
                                    selected = selectedCity == cityVal,
                                    onClick = { selectedCity = cityVal },
                                    label = { Text(cityVal) }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

// ==================== CHRONOLOGY DATE RANGE SELECTOR ====================

@Composable
fun DateFilterDialog(
    onDismiss: () -> Unit,
    viewModel: DoraViewModel
) {
    val currentDateFilter by viewModel.dateFilter.collectAsStateWithLifecycle()
    val customStart by viewModel.customDateStart.collectAsStateWithLifecycle()
    val customEnd by viewModel.customDateEnd.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf(currentDateFilter) }
    var startDateText by remember { mutableStateOf(customStart?.let { formatDate(it) } ?: "") }
    var endDateText by remember { mutableStateOf(customEnd?.let { formatDate(it) } ?: "") }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (selectedFilter == "CUSTOM") {
                        val startLong = parseDateText(startDateText)
                        val endLong = parseDateText(endDateText)
                        if (startLong == null || endLong == null) {
                            validationError = "Please enter valid dates (yyyy-MM-dd)"
                            return@Button
                        }
                        viewModel.updateDateFilter("CUSTOM", startLong, endLong)
                    } else {
                        viewModel.updateDateFilter(selectedFilter, null, null)
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("apply_date_filter_btn")
            ) {
                Text("Apply Selection")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = "Calendar", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Time Selector System", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Filter content chronologically. Newer information matches priority metrics.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                val options = listOf(
                    "ALL" to "Anytime",
                    "TODAY" to "Today Only",
                    "YESTERDAY" to "Yesterday",
                    "LAST_7_DAYS" to "Last 7 Days",
                    "LAST_30_DAYS" to "Last 30 Days",
                    "THIS_MONTH" to "This Month",
                    "THIS_YEAR" to "This Year",
                    "CUSTOM" to "Custom Date Range"
                )

                options.forEach { (key, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedFilter == key) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { selectedFilter = key }
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                    ) {
                        RadioButton(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = label, fontWeight = if (selectedFilter == key) FontWeight.Bold else FontWeight.Normal, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (selectedFilter == "CUSTOM") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Format: yyyy-MM-dd", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = startDateText,
                            onValueChange = { startDateText = it; validationError = null },
                            label = { Text("Start Date") },
                            placeholder = { Text("e.g. 2026-06-01") },
                            modifier = Modifier.fillMaxWidth().testTag("custom_date_start_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endDateText,
                            onValueChange = { endDateText = it; validationError = null },
                            label = { Text("End Date") },
                            placeholder = { Text("e.g. 2026-06-05") },
                            modifier = Modifier.fillMaxWidth().testTag("custom_date_end_input"),
                            singleLine = true
                        )
                        if (validationError != null) {
                            Text(text = validationError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    )
}

fun formatDate(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    return sdf.format(java.util.Date(millis))
}

fun parseDateText(text: String): Long? {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    return try {
        sdf.parse(text)?.time
    } catch (e: Exception) {
        null
    }
}

