package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.Bookmark
import com.example.ui.components.DoraArticleCard
import com.example.ui.viewmodel.DoraViewModel
import com.example.ui.viewmodel.HubState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: DoraViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val newsState by viewModel.newsState.collectAsStateWithLifecycle()
    val trendsState by viewModel.trendsState.collectAsStateWithLifecycle()
    val aiToolsState by viewModel.aiToolsState.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

    var activeCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "News Hub", "Tech Trends", "AI Tools")

    // Filter logic
    val filteredArticles = remember(searchQuery, newsState, activeCategoryFilter) {
        val list = when (val state = newsState) {
            is HubState.Success -> state.data
            else -> emptyList()
        }
        val query = searchQuery.trim()
        val match = list.filter {
            query.isEmpty() ||
            it.title.contains(query, ignoreCase = true) ||
            (it.description?.contains(query, ignoreCase = true) ?: false)
        }
        if (activeCategoryFilter == "All" || activeCategoryFilter == "News Hub") match else emptyList()
    }

    val filteredTrends = remember(searchQuery, trendsState, activeCategoryFilter) {
        val list = when (val state = trendsState) {
            is HubState.Success -> state.data
            else -> emptyList()
        }
        val query = searchQuery.trim()
        val match = list.filter {
            query.isEmpty() ||
            (it.title?.contains(query, ignoreCase = true) ?: false)
        }
        if (activeCategoryFilter == "All" || activeCategoryFilter == "Tech Trends") match else emptyList()
    }

    val filteredAiTools = remember(searchQuery, aiToolsState, activeCategoryFilter) {
        val list = when (val state = aiToolsState) {
            is HubState.Success -> state.data
            else -> emptyList()
        }
        val query = searchQuery.trim()
        val match = list.filter {
            query.isEmpty() ||
            (it.title?.contains(query, ignoreCase = true) ?: false)
        }
        if (activeCategoryFilter == "All" || activeCategoryFilter == "AI Tools") match else emptyList()
    }

    val totalCount = filteredArticles.size + filteredTrends.size + filteredAiTools.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Sticky Header / Search Bar container
        Text(
            text = "Global Search",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search by title, keywords...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "SearchIcon"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "ClearIcon"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("global_search_input_field")
        )

        // Pill filter selectors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.forEach { filter ->
                FilterChip(
                    selected = activeCategoryFilter == filter,
                    onClick = { activeCategoryFilter = filter },
                    label = { Text(text = filter, fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        if (totalCount > 0) {
            Text(
                text = "Showing $totalCount results",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("search_results_lazy_list"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            if (totalCount == 0) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No matches found.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try searching for keywords like 'Google', 'Apple', 'Sora', 'GPT', 'Hacker', etc.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // News results
                items(filteredArticles) { article ->
                    val url = article.url
                    val isBookmarked = bookmarks.any { it.id == url }

                    DoraArticleCard(
                        title = article.title,
                        description = article.description,
                        sourceName = article.source?.name ?: "News Hub",
                        author = article.author,
                        url = url,
                        imageUrl = article.urlToImage,
                        isBookmarked = isBookmarked,
                        onBookmarkToggle = { viewModel.toggleBookmarkArticle(article) },
                        badgeLabel = "News",
                        badgeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        badgeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                // Trends
                items(filteredTrends) { item ->
                    val id = item.objectID
                    val isBookmarked = bookmarks.any { it.id == id }

                    DoraArticleCard(
                        title = item.title ?: "No Title",
                        description = "Points: ${item.points ?: 0} | Comments: ${item.num_comments ?: 0}",
                        sourceName = "Hacker News",
                        author = item.author,
                        url = item.url ?: "https://news.ycombinator.com/item?id=$id",
                        imageUrl = null,
                        isBookmarked = isBookmarked,
                        onBookmarkToggle = { viewModel.toggleBookmarkHNItem(item, "trend") },
                        badgeLabel = "Trend",
                        badgeContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        badgeContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                // AI Tools Hub
                items(filteredAiTools) { item ->
                    val id = item.objectID
                    val isBookmarked = bookmarks.any { it.id == id }

                    DoraArticleCard(
                        title = item.title ?: "No Title",
                        description = "Points: ${item.points ?: 0} | Comments: ${item.num_comments ?: 0}",
                        sourceName = "Hacker News",
                        author = item.author,
                        url = item.url ?: "https://news.ycombinator.com/item?id=$id",
                        imageUrl = null,
                        isBookmarked = isBookmarked,
                        onBookmarkToggle = { viewModel.toggleBookmarkHNItem(item, "ai_tool") },
                        badgeLabel = "AI Tool",
                        badgeContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        badgeContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}
