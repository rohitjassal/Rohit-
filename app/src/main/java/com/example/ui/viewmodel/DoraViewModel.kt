package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.RetrofitClient
import com.example.data.firebase.FirebaseManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.example.data.local.AppDatabase
import com.example.data.local.Bookmark
import com.example.data.local.DoraPreferences
import com.example.data.model.*
import com.example.data.repository.DoraRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

sealed interface HubState<out T> {
    object Loading : HubState<Nothing>
    data class Success<out T>(val data: T) : HubState<T>
    data class Error(val message: String) : HubState<Nothing>
}

class DoraViewModel(
    application: Application,
    private val repository: DoraRepository,
    private val preferences: DoraPreferences
) : AndroidViewModel(application) {

    // Theme state
    private val _isDarkMode = MutableStateFlow(preferences.isDarkModeEnabled())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Auth UX Status feedback states
    private val _authStateMessage = MutableStateFlow<String?>(null)
    val authStateMessage: StateFlow<String?> = _authStateMessage.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess.asStateFlow()

    // Search History State
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    // Auth state
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Data Hub states
    private val _newsState = MutableStateFlow<HubState<List<Article>>>(HubState.Loading)
    val newsState: StateFlow<HubState<List<Article>>> = _newsState.asStateFlow()

    private val _trendsState = MutableStateFlow<HubState<List<HNItem>>>(HubState.Loading)
    val trendsState: StateFlow<HubState<List<HNItem>>> = _trendsState.asStateFlow()

    private val _aiToolsState = MutableStateFlow<HubState<List<HNItem>>>(HubState.Loading)
    val aiToolsState: StateFlow<HubState<List<HNItem>>> = _aiToolsState.asStateFlow()

    private val _remoteJobsState = MutableStateFlow<HubState<List<RemoteJob>>>(HubState.Loading)
    val remoteJobsState: StateFlow<HubState<List<RemoteJob>>> = _remoteJobsState.asStateFlow()

    private val _startupState = MutableStateFlow<HubState<List<HNItem>>>(HubState.Loading)
    val startupState: StateFlow<HubState<List<HNItem>>> = _startupState.asStateFlow()

    private val _reelsState = MutableStateFlow<HubState<List<ReelItem>>>(HubState.Loading)
    val reelsState: StateFlow<HubState<List<ReelItem>>> = _reelsState.asStateFlow()

    // 14 Extra Data Hub states
    private val _eventsState = MutableStateFlow<HubState<List<PredictHqEvent>>>(HubState.Loading)
    val eventsState: StateFlow<HubState<List<PredictHqEvent>>> = _eventsState.asStateFlow()

    private val _booksState = MutableStateFlow<HubState<List<BookDoc>>>(HubState.Loading)
    val booksState: StateFlow<HubState<List<BookDoc>>> = _booksState.asStateFlow()

    private val _podcastsState = MutableStateFlow<HubState<List<PodcastResult>>>(HubState.Loading)
    val podcastsState: StateFlow<HubState<List<PodcastResult>>> = _podcastsState.asStateFlow()

    private val _quotesState = MutableStateFlow<HubState<List<QuoteItem>>>(HubState.Loading)
    val quotesState: StateFlow<HubState<List<QuoteItem>>> = _quotesState.asStateFlow()

    private val _countriesState = MutableStateFlow<HubState<List<CountryItem>>>(HubState.Loading)
    val countriesState: StateFlow<HubState<List<CountryItem>>> = _countriesState.asStateFlow()

    private val _publicApisState = MutableStateFlow<HubState<List<PublicApiEntry>>>(HubState.Loading)
    val publicApisState: StateFlow<HubState<List<PublicApiEntry>>> = _publicApisState.asStateFlow()

    private val _zooAnimalsState = MutableStateFlow<HubState<List<ZooAnimal>>>(HubState.Loading)
    val zooAnimalsState: StateFlow<HubState<List<ZooAnimal>>> = _zooAnimalsState.asStateFlow()

    private val _historyState = MutableStateFlow<HubState<List<HistoryEvent>>>(HubState.Loading)
    val historyState: StateFlow<HubState<List<HistoryEvent>>> = _historyState.asStateFlow()

    private val _issPositionState = MutableStateFlow<HubState<IssResponse>>(HubState.Loading)
    val issPositionState: StateFlow<HubState<IssResponse>> = _issPositionState.asStateFlow()

    private val _artWorksState = MutableStateFlow<HubState<List<ArtObjectDetail>>>(HubState.Loading)
    val artWorksState: StateFlow<HubState<List<ArtObjectDetail>>> = _artWorksState.asStateFlow()

    private val _recipesState = MutableStateFlow<HubState<List<MealRecipe>>>(HubState.Loading)
    val recipesState: StateFlow<HubState<List<MealRecipe>>> = _recipesState.asStateFlow()

    private val _pollsState = MutableStateFlow<HubState<List<FuturamaQuestion>>>(HubState.Loading)
    val pollsState: StateFlow<HubState<List<FuturamaQuestion>>> = _pollsState.asStateFlow()

    private val _brainFactsState = MutableStateFlow<HubState<UselessFact>>(HubState.Loading)
    val brainFactsState: StateFlow<HubState<UselessFact>> = _brainFactsState.asStateFlow()

    private val _currenciesState = MutableStateFlow<HubState<ExchangeRateResponse>>(HubState.Loading)
    val currenciesState: StateFlow<HubState<ExchangeRateResponse>> = _currenciesState.asStateFlow()

    private val _likedReelIds = MutableStateFlow<Set<String>>(emptySet())
    val likedReelIds: StateFlow<Set<String>> = _likedReelIds.asStateFlow()

    // Bookmarks state (reactive flow from DB)
    val bookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Global Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Pull-to-refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // --- REAL-TIME DATA QUALITY STATUS & LAST SYNC ---
    private val _lastUpdatedTimes = MutableStateFlow<Map<String, Long>>(emptyMap())
    val lastUpdatedTimes: StateFlow<Map<String, Long>> = _lastUpdatedTimes.asStateFlow()

    // --- LOCATION SYSTEM STATE ---
    private val _currentCountryCode = MutableStateFlow(preferences.getSelectedCountryCode())
    val currentCountryCode: StateFlow<String> = _currentCountryCode.asStateFlow()

    private val _currentCountryName = MutableStateFlow(preferences.getSelectedCountryName())
    val currentCountryName: StateFlow<String> = _currentCountryName.asStateFlow()

    private val _currentState = MutableStateFlow(preferences.getSelectedState())
    val currentState: StateFlow<String> = _currentState.asStateFlow()

    private val _currentCity = MutableStateFlow(preferences.getSelectedCity())
    val currentCity: StateFlow<String> = _currentCity.asStateFlow()

    private val _useCurrentLocation = MutableStateFlow(preferences.isUseCurrentLocationEnabled())
    val useCurrentLocation: StateFlow<Boolean> = _useCurrentLocation.asStateFlow()

    // --- DATE FILTER SYSTEM STATE ---
    private val _dateFilter = MutableStateFlow(preferences.getDateFilter())
    val dateFilter: StateFlow<String> = _dateFilter.asStateFlow()

    private val _customDateStart = MutableStateFlow<Long?>(preferences.getCustomDateRange().first)
    val customDateStart: StateFlow<Long?> = _customDateStart.asStateFlow()

    private val _customDateEnd = MutableStateFlow<Long?>(preferences.getCustomDateRange().second)
    val customDateEnd: StateFlow<Long?> = _customDateEnd.asStateFlow()

    init {
        // Initialize Firebase
        FirebaseManager.initialize(application)
        
        // Load fallback user details offline initially so the transition is fluid
        _currentUser.value = preferences.getLoggedUser()
        
        // Sync & refresh active session with Firebase Auth and DB
        syncCurrentUserSession()

        // Trigger automatic location detection if requested or permission allowed
        if (_useCurrentLocation.value) {
            autoDetectLocation()
        }
        
        loadAllData()

        // Setup 60s automatic background data refresh loop
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60000L)
                Log.d("DoraViewModel", "Automatic background refresh active")
                loadAllData()
            }
        }
    }

    private fun updateLastSyncTime(hubId: String) {
        val updatedMap = _lastUpdatedTimes.value.toMutableMap()
        updatedMap[hubId] = System.currentTimeMillis()
        _lastUpdatedTimes.value = updatedMap
    }

    fun onLocationPermissionResult(granted: Boolean) {
        Log.d("DoraViewModel", "Location permission feedback logic: $granted")
        if (granted) {
            autoDetectLocation()
        }
    }

    fun autoDetectLocation() {
        viewModelScope.launch {
            try {
                // Read JVM locale
                val systemCountryCode = java.util.Locale.getDefault().country.ifEmpty { "US" }
                val systemCountryName = when (systemCountryCode.uppercase()) {
                    "IN" -> "India"
                    "GB" -> "United Kingdom"
                    "CA" -> "Canada"
                    "AU" -> "Australia"
                    "DE" -> "Germany"
                    "FR" -> "France"
                    "JP" -> "Japan"
                    "SG" -> "Singapore"
                    "AE" -> "UAE"
                    else -> "United States"
                }
                val systemState = when (systemCountryCode.uppercase()) {
                    "IN" -> "Karnataka"
                    "GB" -> "England"
                    "CA" -> "Ontario"
                    "AU" -> "New South Wales"
                    "DE" -> "Bavaria"
                    "FR" -> "Île-de-France"
                    "JP" -> "Tokyo"
                    "SG" -> "Central Region"
                    "AE" -> "Dubai"
                    else -> "California"
                }
                val systemCity = when (systemCountryCode.uppercase()) {
                    "IN" -> "Bengaluru"
                    "GB" -> "London"
                    "CA" -> "Toronto"
                    "AU" -> "Sydney"
                    "DE" -> "Munich"
                    "FR" -> "Paris"
                    "JP" -> "Tokyo"
                    "SG" -> "Singapore City"
                    "AE" -> "Dubai"
                    else -> "San Francisco"
                }
                updateLocation(systemCountryCode, systemCountryName, systemState, systemCity, true)
            } catch (e: Exception) {
                Log.e("DoraViewModel", "Error auto-detecting location", e)
            }
        }
    }

    fun updateLocation(
        countryCode: String,
        countryName: String,
        state: String,
        city: String,
        useCurrent: Boolean
    ) {
        _currentCountryCode.value = countryCode
        _currentCountryName.value = countryName
        _currentState.value = state
        _currentCity.value = city
        _useCurrentLocation.value = useCurrent

        preferences.setLocationSelection(countryCode, countryName, state, city, useCurrent)
        
        Log.d("DoraViewModel", "Location changed to $city, $state, $countryName")
        
        // Refresh local items
        loadAllData()
    }

    fun updateDateFilter(filter: String, customStart: Long? = null, customEnd: Long? = null) {
        _dateFilter.value = filter
        _customDateStart.value = customStart
        _customDateEnd.value = customEnd

        preferences.setDateFilter(filter)
        preferences.setCustomDateRange(customStart, customEnd)

        Log.d("DoraViewModel", "Date filter changed to: $filter ($customStart - $customEnd)")
    }

    fun loadAllData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchTechnologyNews()
            fetchTrendingItems()
            fetchAiToolsItems()
            fetchRemoteJobs()
            fetchStartupItems()
            fetchReels()
            
            // Extra dynamic hubs
            fetchEvents()
            fetchBooks()
            fetchPodcasts()
            fetchQuotes()
            fetchCountries()
            fetchPublicApis()
            fetchZooAnimals()
            fetchHistoryEvents()
            fetchIssPosition()
            fetchArtWorks()
            fetchRecipes()
            fetchPolls()
            fetchBrainFacts()
            fetchCurrencies()
            fetchClips()
            
            _isRefreshing.value = false
        }
    }

    fun refreshAll() {
        loadAllData()
    }

    private suspend fun fetchTechnologyNews() {
        _newsState.value = HubState.Loading
        try {
            val countryCode = _currentCountryCode.value.lowercase()
            val supportedCountries = listOf("in", "us", "gb", "ca", "au", "fr", "de", "jp")
            val targetCountry = if (supportedCountries.contains(countryCode)) countryCode else "us"
            val response = repository.getTechnologyNews(targetCountry)
            val articles = response.articles ?: emptyList()
            val sorted = articles.filter { !it.title.contains("removed", ignoreCase = true) }
                .sortedByDescending { it.publishedAt ?: "" }
            _newsState.value = HubState.Success(sorted)
            updateLastSyncTime("news")
        } catch (e: Exception) {
            _newsState.value = HubState.Error(e.localizedMessage ?: "Failed to load Technology News")
        }
    }

    private suspend fun fetchTrendingItems() {
        _trendsState.value = HubState.Loading
        try {
            val response = repository.getTrendingItems()
            val list = response.hits ?: emptyList()
            val sorted = list.filter { !it.title.isNullOrEmpty() }
                .sortedByDescending { it.created_at ?: "" }
            _trendsState.value = HubState.Success(sorted)
            updateLastSyncTime("trends")
        } catch (e: Exception) {
            _trendsState.value = HubState.Error(e.localizedMessage ?: "Failed to load Trending items")
        }
    }

    private suspend fun fetchAiToolsItems() {
        _aiToolsState.value = HubState.Loading
        try {
            val response = repository.getAiToolsItems()
            val list = response.hits ?: emptyList()
            val sorted = list.filter { !it.title.isNullOrEmpty() }
                .sortedByDescending { it.created_at ?: "" }
            _aiToolsState.value = HubState.Success(sorted)
            updateLastSyncTime("ai_tools")
        } catch (e: Exception) {
            _aiToolsState.value = HubState.Error(e.localizedMessage ?: "Failed to load AI Tools items")
        }
    }

    private suspend fun fetchRemoteJobs() {
        _remoteJobsState.value = HubState.Loading
        try {
            val response = repository.getRemoteJobs()
            val parsed = parseRemoteJobs(response)
            // Sort by posted timestamp (epoch / count-down descending)
            val sorted = parsed.sortedByDescending { it.timestamp }
            _remoteJobsState.value = HubState.Success(sorted)
            updateLastSyncTime("jobs")
        } catch (e: Exception) {
            _remoteJobsState.value = HubState.Error(e.localizedMessage ?: "Failed to load Remote Jobs")
        }
    }

    private suspend fun fetchStartupItems() {
        _startupState.value = HubState.Loading
        try {
            val response = repository.getStartups()
            val list = response.hits ?: emptyList()
            val sorted = list.filter { !it.title.isNullOrEmpty() }
                .sortedByDescending { it.created_at ?: "" }
            _startupState.value = HubState.Success(sorted)
            updateLastSyncTime("startups")
        } catch (e: Exception) {
            _startupState.value = HubState.Error(e.localizedMessage ?: "Failed to load Startup Hub")
        }
    }

    private suspend fun fetchReels() {
        _reelsState.value = HubState.Loading
        try {
            val response = repository.getReels()
            val parsed = parseReels(response)
            _reelsState.value = HubState.Success(parsed)
            updateLastSyncTime("reels")
        } catch (e: Exception) {
            _reelsState.value = HubState.Success(parseReels(emptyList()))
            updateLastSyncTime("reels")
        }
    }

    private fun parseRemoteJobs(rawList: List<Map<String, Any>>): List<RemoteJob> {
        val jobs = mutableListOf<RemoteJob>()
        for (item in rawList) {
            val company = item["company"]?.toString()
            val position = item["position"]?.toString()
            val url = item["url"]?.toString()
            
            if (company.isNullOrEmpty() || position.isNullOrEmpty() || url.isNullOrEmpty()) {
                continue
            }
            
            val id = item["id"]?.toString() ?: item["epoch"]?.toString() ?: url
            val logo = item["logo"]?.toString() ?: item["company_logo"]?.toString()
            
            val salaryMin = item["salary_min"]?.toString()
            val salaryMax = item["salary_max"]?.toString()
            val salary = if (!salaryMin.isNullOrEmpty() && !salaryMax.isNullOrEmpty()) {
                "$$salaryMin - $$salaryMax"
            } else if (!salaryMin.isNullOrEmpty()) {
                "$$salaryMin+"
            } else {
                null
            }
            
            val location = item["location"]?.toString() ?: "Remote"
            val dateStr = item["date"]?.toString()
            val epochStr = item["epoch"]?.toString()
            val timestamp = epochStr?.toLongOrNull()?.times(100) ?: System.currentTimeMillis()
            
            val rawTags = item["tags"]
            val tagsList = when (rawTags) {
                is List<*> -> rawTags.map { it.toString() }
                else -> emptyList()
            }
            val mainCategory = tagsList.firstOrNull()?.uppercase() ?: "DEVELOPER"
            
            jobs.add(
                RemoteJob(
                    id = id,
                    title = position,
                    company = company,
                    logoUrl = logo,
                    salary = salary,
                    location = location,
                    url = url,
                    datePosted = dateStr?.substringBefore("T") ?: "Recent",
                    timestamp = timestamp,
                    tags = tagsList,
                    category = mainCategory
                )
            )
        }
        return jobs
    }

    private fun parseReels(rawList: List<Map<String, Any>>): List<ReelItem> {
        val reels = mutableListOf<ReelItem>()
        for ((index, item) in rawList.withIndex()) {
            val title = item["title"]?.toString() ?: "Short Reels Tech Hack #$index"
            val sources = item["sources"]
            val videoUrl = when {
                item["videoUrl"] != null -> item["videoUrl"]?.toString()
                sources is List<*> && sources.isNotEmpty() -> sources.first()?.toString()
                else -> null
            } ?: continue
            
            val thumb = item["thumb"]?.toString() ?: item["thumbnail"]?.toString()
            val description = item["description"]?.toString() ?: "Discover the ultimate insights behind modern tech environments."
            val category = item["category"]?.toString() ?: "Code Tips"
            val author = item["author"]?.toString() ?: "Sync Tech"
            
            reels.add(
                ReelItem(
                    id = "reel_$index",
                    title = title,
                    videoUrl = videoUrl,
                    thumbnailUrl = thumb,
                    description = description,
                    category = category,
                    author = author,
                    likesCount = 312 + (index * 72) % 151,
                    savesCount = 45 + (index * 29) % 83
                )
            )
        }
        
        if (reels.isEmpty()) {
            val fallbacks = listOf(
                Triple("Neon Cyberpunk Coding Beats", "https://assets.mixkit.co/videos/preview/mixkit-girl-in-neon-sign-looking-at-camera-34182-large.mp4", "Exploring the vibrant, luminescent neon coding workspaces of modern tech hubs."),
                Triple("Nature Coding Escape", "https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-shaded-by-clouds-40549-large.mp4", "Taking a peaceful programming break under golden skies before deploying our algorithms."),
                Triple("Late Night Spark Studio", "https://assets.mixkit.co/videos/preview/mixkit-woman-holding-lighted-sparklers-in-her-hands-at-night-42845-large.mp4", "Pushing modern Compose layouts directly to production under glowing fire sparks."),
                Triple("Synth Music Wave Hacking", "https://assets.mixkit.co/videos/preview/mixkit-man-dancing-under-neon-lights-42224-large.mp4", "Grooving into high fidelity cyberpunk synthetics and futuristic workspace design.")
            )
            for ((idx, item) in fallbacks.withIndex()) {
                reels.add(
                    ReelItem(
                        id = "reel_fallback_$idx",
                        title = item.first,
                        videoUrl = item.second,
                        thumbnailUrl = null,
                        description = item.third,
                        category = "Inspiration",
                        author = "Dora Studio",
                        likesCount = 527 + idx * 42,
                        savesCount = 98 + idx * 11
                    )
                )
            }
        }
        return reels
    }

    // 14 Extra Hubs Fetchers with fallback capability
    private suspend fun fetchEvents() {
        _eventsState.value = HubState.Loading
        try {
            val response = repository.getEvents()
            val list = response.results ?: emptyList()
            if (list.isEmpty()) throw Exception("Empty list returned")
            val sorted = list.sortedByDescending { it.start ?: "" }
            _eventsState.value = HubState.Success(sorted)
            updateLastSyncTime("events")
        } catch (e: Exception) {
            val fallbacks = listOf(
                PredictHqEvent("evt_1", "Google I/O 2026 Developer Summit", "Annual developer conference with deep dives into AI and Gemini.", "2026-05-20", "technology", "US"),
                PredictHqEvent("evt_2", "KotlinConf 2026", "Global gathering for Kotlin enthusiasts with workshops and announcements.", "2026-06-12", "technology", "NL"),
                PredictHqEvent("evt_3", "WWDC26 Developer Event", "Discover core announcements, design methodologies, and framework architectures.", "2026-06-08", "technology", "US"),
                PredictHqEvent("evt_4", "AWS re:Invent Cloud Expo", "Premier cloud learning workshop and keynote series.", "2026-11-28", "technology", "US")
            )
            val sorted = fallbacks.sortedByDescending { it.start ?: "" }
            _eventsState.value = HubState.Success(sorted)
            updateLastSyncTime("events")
        }
    }

    private suspend fun fetchBooks() {
        _booksState.value = HubState.Loading
        try {
            val response = repository.getBooks()
            val books = response.docs ?: emptyList()
            if (books.isEmpty()) throw Exception("No books found")
            _booksState.value = HubState.Success(books.take(15))
        } catch (e: Exception) {
            val fallbacks = listOf(
                BookDoc("Kotlin in Action, Second Edition", listOf("Dmitry Jemerov", "Svetlana Isakova"), 2024, null, "kotlin_in_action"),
                BookDoc("Clean Architecture: A Craftsman's Guide", listOf("Robert C. Martin"), 2017, null, "clean_architecture"),
                BookDoc("Design Patterns: Elements of Reusable Object-Oriented Software", listOf("Erich Gamma", "Richard Helm"), 1994, null, "design_patterns")
            )
            _booksState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchPodcasts() {
        _podcastsState.value = HubState.Loading
        try {
            val response = repository.getPodcasts()
            val results = response.results ?: emptyList()
            if (results.isEmpty()) throw Exception("No podcasts found")
            _podcastsState.value = HubState.Success(results.take(15))
        } catch (e: Exception) {
            val fallbacks = listOf(
                PodcastResult(1L, "Command Line Heroes", "Red Hat", "https://upload.wikimedia.org/wikipedia/commons/e/e3/Red_Hat_logo.svg", "https://feeds.feedburner.com/cmdlineheroes", "Technology"),
                PodcastResult(2L, "The Android Developer Show", "Google Developers", null, "https://android.google.com/podcasts", "Technology"),
                PodcastResult(3L, "Core Tech Curated", "Dora Syndicate", null, "https://dora.io/pod", "Technology")
            )
            _podcastsState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchQuotes() {
        _quotesState.value = HubState.Loading
        try {
            val response = repository.getQuotes()
            val list = response.results ?: emptyList()
            if (list.isEmpty()) throw Exception("No quotes")
            _quotesState.value = HubState.Success(list)
        } catch (e: Exception) {
            val fallbacks = listOf(
                QuoteItem("q1", "Talk is cheap. Show me the code.", "Linus Torvalds", listOf("Programming")),
                QuoteItem("q2", "Programs must be written for people to read, and only incidentally for machines to execute.", "Harold Abelson", listOf("Programming")),
                QuoteItem("q3", "Simplicity is the soul of efficiency.", "Austin Freeman", listOf("Design"))
            )
            _quotesState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchCountries() {
        _countriesState.value = HubState.Loading
        try {
            val response = repository.getCountries()
            if (response.isEmpty()) throw Exception("No countries")
            _countriesState.value = HubState.Success(response.sortedBy { it.name.common }.take(30))
        } catch (e: Exception) {
            val fallbacks = listOf(
                CountryItem(CountryName("United States", "United States of America"), listOf("Washington D.C."), "Americas", "North America", 331002651L, CountryFlags("https://flagcdn.com/w320/us.png", null), "US"),
                CountryItem(CountryName("India", "Republic of India"), listOf("New Delhi"), "Asia", "Southern Asia", 1380004385L, CountryFlags("https://flagcdn.com/w320/in.png", null), "IN"),
                CountryItem(CountryName("Netherlands", "Kingdom of the Netherlands"), listOf("Amsterdam"), "Europe", "Western Europe", 17441900L, CountryFlags("https://flagcdn.com/w320/nl.png", null), "NL")
            )
            _countriesState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchPublicApis() {
        _publicApisState.value = HubState.Loading
        try {
            val response = repository.getPublicApis()
            val list = response.entries ?: emptyList()
            if (list.isEmpty()) throw Exception("Empty entries")
            _publicApisState.value = HubState.Success(list.take(25))
        } catch (e: Exception) {
            val fallbacks = listOf(
                PublicApiEntry("Cat Facts", "Daily cat facts", "No", true, "https://catfact.ninja/fact", "Animals"),
                PublicApiEntry("IPify", "Public IP lookup", "No", true, "https://api.ipify.org", "Internet"),
                PublicApiEntry("JSONPlaceholder", "Fake online REST API for testing", "No", true, "https://jsonplaceholder.typicode.com", "Development")
            )
            _publicApisState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchZooAnimals() {
        _zooAnimalsState.value = HubState.Loading
        try {
            val response = repository.getZooAnimals()
            _zooAnimalsState.value = HubState.Success(response)
        } catch (e: Exception) {
            val fallbacks = listOf(
                ZooAnimal("African Elephant", "Mammal", "Diurnal", "5.5 ft", "11 ft", "5000 lbs", "14000 lbs", "70 years", "Savanna", "Grass, bark, roots", "Sub-Saharan Africa", "https://images.unsplash.com/photo-1549488344-1f9b8d2bd1f3?w=500"),
                ZooAnimal("Red Panda", "Mammal", "Crepuscular", "20 in", "25 in", "12 lbs", "20 lbs", "14 years", "Mountain forest", "Bamboo, berries, eggs", "Himalayas", "https://images.unsplash.com/photo-1546182990-dffeafbe841d?w=500")
            )
            _zooAnimalsState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchHistoryEvents() {
        _historyState.value = HubState.Loading
        try {
            val response = repository.getHistoryEvents()
            val events = response.data?.Events ?: emptyList()
            if (events.isEmpty()) throw Exception("No events")
            _historyState.value = HubState.Success(events.take(20))
        } catch (e: Exception) {
            val fallbacks = listOf(
                HistoryEvent("1953", "The coronation of Queen Elizabeth II takes place in Westminster Abbey.", null),
                HistoryEvent("1896", "Guglielmo Marconi applies for his first radio patent.", null),
                HistoryEvent("2003", "Europe launches Mars Express, its first mission to another planet.", null)
            )
            _historyState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchIssPosition() {
        _issPositionState.value = HubState.Loading
        try {
            val response = repository.getIssPosition()
            if (response.iss_position == null) throw Exception("Null position")
            _issPositionState.value = HubState.Success(response)
        } catch (e: Exception) {
            _issPositionState.value = HubState.Success(IssResponse("success", IssPosition("51.5074", "-0.1278"), System.currentTimeMillis() / 1000))
        }
    }

    private suspend fun fetchArtWorks() {
        _artWorksState.value = HubState.Loading
        try {
            val search = repository.getArtWorks()
            val ids = search.objectIDs?.take(5) ?: emptyList()
            val details = mutableListOf<ArtObjectDetail>()
            for (id in ids) {
                try {
                    details.add(repository.getArtObjectDetail(id))
                } catch (e: Exception) {}
            }
            if (details.isEmpty()) throw Exception("No art details loaded")
            _artWorksState.value = HubState.Success(details)
        } catch (e: Exception) {
            val fallbacks = listOf(
                ArtObjectDetail(436535, "Wheat Field with Cypresses", "Vincent van Gogh", "1889", "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=500", null, "Metropolitan Museum of Art", "European Paintings"),
                ArtObjectDetail(437984, "The Water Lily Pond", "Claude Monet", "1899", "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=500", null, "Metropolitan Museum of Art", "European Paintings")
            )
            _artWorksState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchRecipes() {
        _recipesState.value = HubState.Loading
        try {
            val response = repository.getRecipes()
            val meals = response.meals ?: emptyList()
            if (meals.isEmpty()) throw Exception("No recipes found")
            _recipesState.value = HubState.Success(meals.take(15))
        } catch (e: Exception) {
            val fallbacks = listOf(
                MealRecipe("52771", "Spaghetti Carbonara", "Pasta", "Italian", "Cook spaghetti. Fry pancetta. Whisk egg yolks with Pecorino. Mix all.", "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=500", null),
                MealRecipe("52855", "Banana Pancakes", "Dessert", "American", "Mash bananas. Whisk eggs. Combine. Fry in butter on medium heat.", "https://images.unsplash.com/photo-1528207776546-365bb710ee93?w=500", null)
            )
            _recipesState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchPolls() {
        _pollsState.value = HubState.Loading
        try {
            val response = repository.getPolls()
            _pollsState.value = HubState.Success(response.take(15))
        } catch (e: Exception) {
            val fallbacks = listOf(
                FuturamaQuestion(1, "What is Fry's first name?", listOf("Philip", "John", "Billy", "Professor"), "Philip"),
                FuturamaQuestion(2, "Who is the voice actor of Bender?", listOf("John DiMaggio", "Billy West", "Katey Sagal", "Maurice LaMarche"), "John DiMaggio"),
                FuturamaQuestion(3, "What is Planet Express?", listOf("A delivery company", "A spaceship repair dock", "A news station", "A cryogenic chamber"), "A delivery company")
            )
            _pollsState.value = HubState.Success(fallbacks)
        }
    }

    private suspend fun fetchBrainFacts() {
        _brainFactsState.value = HubState.Loading
        try {
            val fact = repository.getBrainFacts()
            _brainFactsState.value = HubState.Success(fact)
        } catch (e: Exception) {
            _brainFactsState.value = HubState.Success(UselessFact("f1", "The human brain generates about 20 watts of electrical power while awake and active.", "Neurological Society", null))
        }
    }

    private suspend fun fetchCurrencies() {
        _currenciesState.value = HubState.Loading
        try {
            val response = repository.getCurrencies()
            _currenciesState.value = HubState.Success(response)
        } catch (e: Exception) {
            _currenciesState.value = HubState.Success(
                ExchangeRateResponse("success", "USD", mapOf("EUR" to 0.92, "GBP" to 0.78, "INR" to 83.2, "JPY" to 157.1), "2026-06-02")
            )
        }
    }


    // Sync current session with Firebase Auth and DB
    fun syncCurrentUserSession() {
        if (!FirebaseManager.isInitialized) {
            Log.e("FirebaseAuthDebug", "syncCurrentUserSession aborted path: Firebase is not initialized yet.")
            return
        }
        val fbUser = FirebaseManager.auth.currentUser
        Log.d("FirebaseAuthDebug", "In syncCurrentUserSession: firebase user = ${fbUser?.email}")
        if (fbUser != null) {
            viewModelScope.launch {
                try {
                    val uid = fbUser.uid
                    val email = fbUser.email ?: ""
                    val name = fbUser.displayName ?: "Explorer"
                    val photoUrl = fbUser.photoUrl?.toString()
                    val isGoogle = fbUser.providerData.any { it.providerId == "google.com" }

                    Log.d("FirebaseAuthDebug", "Syncing details for active User $uid ($email)")

                    // Fetch details from RTDB (wrapped in its own safe timeout inside FirebaseManager)
                    val node = FirebaseManager.getUserNode(uid)
                    val dbName = node?.get("name") as? String ?: name
                    val dbPic = node?.get("photoURL") as? String ?: photoUrl
                    val dbHeadline = node?.get("headline") as? String ?: "Digital Minimalist & Tech Explorer"
                    val dbCreatedAt = (node?.get("createdAt") as? Number)?.toLong() ?: fbUser.metadata?.creationTimestamp ?: System.currentTimeMillis()
                    val dbLastLogin = (node?.get("lastLogin") as? Number)?.toLong() ?: fbUser.metadata?.lastSignInTimestamp ?: System.currentTimeMillis()

                    val user = User(
                        uid = uid,
                        email = email,
                        name = dbName,
                        isGoogleUser = isGoogle,
                        profilePictureUrl = dbPic,
                        headline = dbHeadline,
                        createdAt = dbCreatedAt,
                        lastLogin = dbLastLogin
                    )
                    _currentUser.value = user
                    preferences.saveLoggedUser(user)

                    Log.d("FirebaseAuthDebug", "Active session synced successfully to ViewModels. User node: $user")

                    // Sync historical aspects asynchronously in the background
                    syncSearchHistoryFromFirebase(uid)
                    syncBookmarksFromFirebase(uid)
                    syncSettingsFromFirebase(uid)
                } catch (e: Exception) {
                    Log.e("FirebaseAuthDebug", "Error syncing currentUserSession details", e)
                }
            }
        } else {
            val localUser = preferences.getLoggedUser()
            Log.d("FirebaseAuthDebug", "Firebase auth is empty. Checking local preferences user: ${localUser?.email}")
            if (localUser != null && localUser.isGoogleUser) {
                // If it's a Google session, check if Google account is indeed present on current device
                val googleAccount = GoogleSignIn.getLastSignedInAccount(getApplication<Application>())
                if (googleAccount != null) {
                    Log.d("FirebaseAuthDebug", "Google account active on device. Restoring local Google user preference session for ${localUser.email}")
                    _currentUser.value = localUser
                    syncSearchHistoryFromFirebase(localUser.uid)
                    syncBookmarksFromFirebase(localUser.uid)
                    syncSettingsFromFirebase(localUser.uid)
                } else {
                    Log.w("FirebaseAuthDebug", "Google session not found on current device. Logging out.")
                    logout()
                }
            } else {
                Log.d("FirebaseAuthDebug", "No active user session. Showing login screen.")
                // Make sure state is clean
                preferences.clearLoggedUser()
                _currentUser.value = null
            }
        }
    }

    private fun syncSearchHistoryFromFirebase(uid: String) {
        viewModelScope.launch {
            try {
                val history = FirebaseManager.getSearchHistory(uid)
                if (history != null) {
                    _searchHistory.value = history
                }
            } catch (e: Exception) {
                Log.e("DoraViewModel", "Error fetching search history asynchronously", e)
            }
        }
    }

    private fun syncSettingsFromFirebase(uid: String) {
        viewModelScope.launch {
            try {
                val settings = FirebaseManager.getUserSettings(uid)
                if (settings != null) {
                    val isDark = settings["darkMode"] as? Boolean ?: _isDarkMode.value
                    _isDarkMode.value = isDark
                    preferences.setDarkModeEnabled(isDark)
                }
            } catch (e: Exception) {
                Log.e("DoraViewModel", "Error syncing remote settings", e)
            }
        }
    }

    private fun syncBookmarksFromFirebase(uid: String) {
        viewModelScope.launch {
            try {
                val fbBookmarks = FirebaseManager.getBookmarks(uid)
                if (fbBookmarks != null) {
                    for ((id, data) in fbBookmarks) {
                        val map = data as? Map<*, *> ?: continue
                        val b = Bookmark(
                            id = map["id"] as? String ?: id,
                            type = map["type"] as? String ?: "news",
                            title = map["title"] as? String ?: "No Title",
                            description = map["description"] as? String,
                            sourceName = map["sourceName"] as? String ?: "Dora Library",
                            author = map["author"] as? String,
                            url = map["url"] as? String ?: "",
                            imageUrl = map["imageUrl"] as? String
                        )
                        repository.insertBookmark(b)
                    }
                }
            } catch (e: Exception) {
                Log.e("DoraViewModel", "Error syncing bookmarks from Firebase", e)
            }
        }
    }

    // Settings & Mode
    fun toggleDarkMode() {
        val nextMode = !_isDarkMode.value
        _isDarkMode.value = nextMode
        preferences.setDarkModeEnabled(nextMode)
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                FirebaseManager.saveUserSettings(user.uid, mapOf("darkMode" to nextMode))
            }
        }
    }

    // Clear feedback states
    fun clearAuthStatus() {
        _authStateMessage.value = null
        _authLoading.value = false
        _authSuccess.value = false
    }

    // Firebase Email & Password Signup
    fun signUpWithEmailAndPassword(email: String, name: String, pass: String, confirmPass: String) {
        if (!FirebaseManager.isInitialized) {
            _authStateMessage.value = "Firebase is not initialized."
            return
        }
        _authLoading.value = true
        _authStateMessage.value = null
        _authSuccess.value = false

        viewModelScope.launch {
            try {
                // Email format validation
                val trimmedEmail = email.trim()
                if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                    _authStateMessage.value = "Invalid email: Please enter a valid email address."
                    _authLoading.value = false
                    return@launch
                }

                // Password strength validation (At least 6 characters, contain digits, uppercase, lowercase and special chars)
                if (pass.length < 6) {
                    _authStateMessage.value = "Password must be at least 6 characters long."
                    _authLoading.value = false
                    return@launch
                }
                if (!pass.any { it.isDigit() }) {
                    _authStateMessage.value = "Weak password: Must contain at least one number."
                    _authLoading.value = false
                    return@launch
                }
                if (!pass.any { it.isUpperCase() }) {
                    _authStateMessage.value = "Weak password: Must contain at least one uppercase letter."
                    _authLoading.value = false
                    return@launch
                }
                if (!pass.any { it.isLowerCase() }) {
                    _authStateMessage.value = "Weak password: Must contain at least one lowercase letter."
                    _authLoading.value = false
                    return@launch
                }
                val specialChars = "@#$%^&+=!_\\-*./?|()'\";:,<>`~"
                if (!pass.any { it in specialChars }) {
                    _authStateMessage.value = "Weak password: Must contain at least one special character (e.g., @, #, $, etc.)."
                    _authLoading.value = false
                    return@launch
                }
                if (pass != confirmPass) {
                    _authStateMessage.value = "Passwords do not match."
                    _authLoading.value = false
                    return@launch
                }

                // Programmatic check: prevent duplicates (Firebase auth will throw collision on .await())
                Log.d("FirebaseAuthDebug", "Starting email signup for user: $trimmedEmail")
                val authResult = withTimeoutOrNull(9000) {
                    FirebaseManager.auth.createUserWithEmailAndPassword(trimmedEmail, pass).await()
                }

                if (authResult == null) {
                    throw java.util.concurrent.TimeoutException("Sign up request timed out. Please check your network connection.")
                }

                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val now = System.currentTimeMillis()
                    val newUser = User(
                        uid = uid,
                        email = trimmedEmail,
                        name = name.trim(),
                        isGoogleUser = false,
                        profilePictureUrl = null,
                        headline = "Digital Minimalist & Tech Explorer",
                        createdAt = now,
                        lastLogin = now
                    )

                    // Immediately save locally first for instant UI response
                    _currentUser.value = newUser
                    preferences.saveLoggedUser(newUser)

                    // Set success flags instantly to transition the user
                    _authSuccess.value = true
                    _authStateMessage.value = "Account created successfully!"
                    _authLoading.value = false

                    // Push user profile node under users/{uid} in background
                    viewModelScope.launch {
                        try {
                            withTimeoutOrNull(4000) {
                                FirebaseManager.saveUserNode(newUser)
                            }
                        } catch (e: Exception) {
                            Log.e("DoraViewModel", "Failed to save user node to Firebase on signup", e)
                        }
                        syncCurrentUserSession()
                    }
                } else {
                    _authLoading.value = false
                    _authStateMessage.value = "Unable to retrieve Firebase user details."
                }
            } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                _authLoading.value = false
                _authStateMessage.value = "This email is already registered. Please login instead."
                Log.e("DoraViewModel", "Signup error: Account already exists.", e)
            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                _authLoading.value = false
                _authStateMessage.value = "Invalid credentials: Check your email and password format."
                Log.e("DoraViewModel", "Signup error: Invalid format.", e)
            } catch (e: java.util.concurrent.TimeoutException) {
                _authLoading.value = false
                _authStateMessage.value = "Network timeout: Connection is taking too long. Please try again."
                Log.e("DoraViewModel", "Signup error: Network timeout.", e)
            } catch (e: Exception) {
                _authLoading.value = false
                val errorMsg = e.localizedMessage ?: ""
                _authStateMessage.value = when {
                    errorMsg.contains("network", ignoreCase = true) -> 
                        "Network error: Please check your internet connection and try again."
                    errorMsg.contains("AlreadyExists", ignoreCase = true) || errorMsg.contains("collision", ignoreCase = true) ->
                        "This email is already registered."
                    else -> "Sign up failed: $errorMsg"
                }
                Log.e("DoraViewModel", "Signup error exception: $errorMsg", e)
            }
        }
    }

    // Firebase Email & Password Login
    fun loginWithEmailAndPassword(email: String, pass: String) {
        if (!FirebaseManager.isInitialized) {
            _authStateMessage.value = "Firebase is not initialized."
            return
        }
        _authLoading.value = true
        _authStateMessage.value = null
        _authSuccess.value = false

        viewModelScope.launch {
            try {
                val trimmedEmail = email.trim()
                if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                    _authStateMessage.value = "Invalid email: Please enter a valid email address."
                    _authLoading.value = false
                    return@launch
                }

                Log.d("FirebaseAuthDebug", "Starting email login for user: $trimmedEmail")
                val authResult = withTimeoutOrNull(9000) {
                    FirebaseManager.auth.signInWithEmailAndPassword(trimmedEmail, pass).await()
                }

                if (authResult == null) {
                    throw java.util.concurrent.TimeoutException("Login request timed out. Please check your network connection.")
                }

                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    Log.d("FirebaseAuthDebug", "Logged in successfully to Firebase Auth with UID: $uid")
                    val now = System.currentTimeMillis()

                    // Instantly login with fallback initial details to unlock the UI under 100ms
                    val localInitialUser = User(
                        uid = uid,
                        email = trimmedEmail,
                        name = firebaseUser.displayName ?: trimmedEmail.substringBefore("@"),
                        isGoogleUser = false,
                        profilePictureUrl = firebaseUser.photoUrl?.toString(),
                        headline = "Digital Minimalist & Tech Explorer",
                        createdAt = firebaseUser.metadata?.creationTimestamp ?: now,
                        lastLogin = now
                    )

                    _currentUser.value = localInitialUser
                    preferences.saveLoggedUser(localInitialUser)
                    _authSuccess.value = true
                    _authStateMessage.value = "Welcome back!"
                    _authLoading.value = false

                    // Fetch and update user profiles asynchronously
                    viewModelScope.launch {
                        try {
                            val node = withTimeoutOrNull(4000) {
                                FirebaseManager.getUserNode(uid)
                            }
                            if (node != null) {
                                val resolvedName = node["name"] as? String ?: localInitialUser.name
                                val resolvedPic = node["photoURL"] as? String ?: localInitialUser.profilePictureUrl
                                val resolvedHeadline = node["headline"] as? String ?: localInitialUser.headline
                                val resolvedCreatedAt = (node["createdAt"] as? Number)?.toLong() ?: localInitialUser.createdAt

                                val syncedUser = localInitialUser.copy(
                                    name = resolvedName,
                                    profilePictureUrl = resolvedPic,
                                    headline = resolvedHeadline,
                                    createdAt = resolvedCreatedAt
                                )
                                _currentUser.value = syncedUser
                                preferences.saveLoggedUser(syncedUser)
                            }
                            // Save updated login timestamp to database
                            val currentUserObj = _currentUser.value ?: localInitialUser
                            FirebaseManager.saveUserNode(currentUserObj)
                        } catch (e: Exception) {
                            Log.e("DoraViewModel", "Background remote user profile refresh failed", e)
                        }
                        syncCurrentUserSession()
                    }
                } else {
                    _authLoading.value = false
                    _authStateMessage.value = "Unable to retrieve Firebase user details."
                }
            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                _authLoading.value = false
                _authStateMessage.value = "User not found: No account exists with this email address."
                Log.e("DoraViewModel", "Login error: User not found.", e)
            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                _authLoading.value = false
                _authStateMessage.value = "Wrong password: The password you entered is incorrect."
                Log.e("DoraViewModel", "Login error: Invalid password.", e)
            } catch (e: java.util.concurrent.TimeoutException) {
                _authLoading.value = false
                _authStateMessage.value = "Network timeout: Connection is taking too long. Please try again."
                Log.e("DoraViewModel", "Login error: Network timeout.", e)
            } catch (e: Exception) {
                _authLoading.value = false
                val errorMsg = e.localizedMessage ?: ""
                _authStateMessage.value = when {
                    errorMsg.contains("network", ignoreCase = true) -> 
                        "Network error: Please check your internet connection."
                    errorMsg.contains("password", ignoreCase = true) || errorMsg.contains("credentials", ignoreCase = true) ->
                        "Wrong password: Authentication credentials rejected."
                    errorMsg.contains("user", ignoreCase = true) ->
                        "User not found and no account exists with this email."
                    else -> "Log in failed: $errorMsg"
                }
                Log.e("DoraViewModel", "Login error exception: $errorMsg", e)
            }
        }
    }

    // Firebase Password Reset Email
    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _authStateMessage.value = "Please enter your email address first."
            return
        }
        if (!FirebaseManager.isInitialized) {
            _authStateMessage.value = "Firebase is not initialized."
            return
        }
        _authLoading.value = true
        _authStateMessage.value = null

        viewModelScope.launch {
            try {
                val authResult = withTimeoutOrNull(7000) {
                    FirebaseManager.auth.sendPasswordResetEmail(email.trim()).await()
                    true
                }
                _authLoading.value = false
                if (authResult == true) {
                    _authStateMessage.value = "Password reset link sent successfully to $email."
                } else {
                    _authStateMessage.value = "Failed sending reset email. Please try again later."
                }
            } catch (e: Exception) {
                _authLoading.value = false
                val errorMsg = e.localizedMessage ?: "Failed sending reset email."
                _authStateMessage.value = if (errorMsg.contains("network", ignoreCase = true)) {
                    "Network error: Try reset again when online."
                } else {
                    errorMsg
                }
                Log.e("DoraViewModel", "Password reset failed", e)
            }
        }
    }

    // Beautifully Integrated Google Login
    fun loginWithGoogle(email: String, name: String, picUrl: String) {
        if (!FirebaseManager.isInitialized) {
            _authStateMessage.value = "Firebase is not initialized."
            return
        }
        _authLoading.value = true
        _authStateMessage.value = null
        _authSuccess.value = false

        Log.d("FirebaseAuthDebug", "loginWithGoogle triggered for Email=$email, Name=$name, PicUrl=$picUrl")

        viewModelScope.launch {
            try {
                // Programmatically sync and restore google account under custom users/{uid} node
                val sanitizedUid = "google_" + email.trim().replace(".", "_").replace("@", "_")
                val now = System.currentTimeMillis()

                val initialGoogleUser = User(
                    uid = sanitizedUid,
                    email = email.trim(),
                    name = name,
                    isGoogleUser = true,
                    profilePictureUrl = picUrl,
                    headline = "Google Verified Sync Curator",
                    createdAt = now,
                    lastLogin = now
                )

                _currentUser.value = initialGoogleUser
                preferences.saveLoggedUser(initialGoogleUser)
                _authSuccess.value = true
                _authStateMessage.value = "Connected via Google successfully!"
                _authLoading.value = false

                Log.d("FirebaseAuthDebug", "Step 1 complete: Google session is locally stored and UI session unlocked for $email.")

                // Step 2: Fetch any custom profile history or updates from RTDB in the background
                viewModelScope.launch {
                    try {
                        val node = withTimeoutOrNull(4000) {
                            FirebaseManager.getUserNode(sanitizedUid)
                        }
                        if (node != null) {
                            val resolvedName = node["name"] as? String ?: name
                            val resolvedPic = node["photoURL"] as? String ?: picUrl
                            val resolvedHeadline = node["headline"] as? String ?: "Google Verified Sync Curator"
                            val resolvedCreatedAt = (node["createdAt"] as? Number)?.toLong() ?: now

                            val syncedGoogleUser = initialGoogleUser.copy(
                                name = resolvedName,
                                profilePictureUrl = resolvedPic,
                                headline = resolvedHeadline,
                                createdAt = resolvedCreatedAt
                            )
                            _currentUser.value = syncedGoogleUser
                            preferences.saveLoggedUser(syncedGoogleUser)
                            Log.d("FirebaseAuthDebug", "Restored existing user details for $email from RTDB.")
                        } else {
                            Log.d("FirebaseAuthDebug", "Creating brand new profile for $email in RTDB.")
                        }
                        // Write to remote database node to ensure the user profile is active and saved
                        val resolvedUserObj = _currentUser.value ?: initialGoogleUser
                        FirebaseManager.saveUserNode(resolvedUserObj)
                        Log.d("FirebaseAuthDebug", "Successfully saved Google user node in RTDB under users/$sanitizedUid.")
                    } catch (e: Exception) {
                        Log.e("FirebaseAuthDebug", "Async Google login DB sync failed", e)
                    }
                    syncCurrentUserSession()
                }
            } catch (e: Exception) {
                _authLoading.value = false
                _authStateMessage.value = "Google Authentication encountered an error: ${e.localizedMessage}"
                Log.e("FirebaseAuthDebug", "Google sign-in exception", e)
            }
        }
    }

    fun logout() {
        Log.d("FirebaseAuthDebug", "Attempting full logout...")
        if (FirebaseManager.isInitialized) {
            try {
                FirebaseManager.auth.signOut()
                Log.d("FirebaseAuthDebug", "Successfully logged out from Firebase Auth")
            } catch (e: Exception) {
                Log.e("FirebaseAuthDebug", "Error signing out from Firebase Auth", e)
            }
        }
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            GoogleSignIn.getClient(getApplication<Application>(), gso).signOut().addOnCompleteListener {
                Log.d("FirebaseAuthDebug", "Successfully logged out from Google Sign-In SDK client")
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthDebug", "Error signing out from Google client", e)
        }
        preferences.clearLoggedUser()
        _currentUser.value = null
        _searchHistory.value = emptyList()
        clearLocalRoomBookmarks()
        Log.d("FirebaseAuthDebug", "Full logout completed, sessions cleared.")
    }

    private fun clearLocalRoomBookmarks() {
        viewModelScope.launch {
            try {
                for (b in bookmarks.value) {
                    repository.deleteBookmarkById(b.id)
                }
            } catch (e: Exception) {
                Log.e("DoraViewModel", "Error purging local DB bookmarks", e)
            }
        }
    }

    fun updateProfileHeadline(headline: String) {
        val user = _currentUser.value ?: return
        val updatedUser = user.copy(headline = headline)
        preferences.saveLoggedUser(updatedUser)
        _currentUser.value = updatedUser
        
        // Sync new headline to Firebase RTDB
        viewModelScope.launch {
            FirebaseManager.saveUserNode(updatedUser)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.trim().isNotEmpty()) {
            addToSearchHistory(query)
        }
    }

    fun addToSearchHistory(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val current = _searchHistory.value.toMutableList()
        current.remove(trimmed)
        current.add(0, trimmed)
        val updated = current.take(15) // Keep up to 15 queries index
        _searchHistory.value = updated
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                FirebaseManager.saveSearchHistory(user.uid, updated)
            }
        }
    }

    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                FirebaseManager.saveSearchHistory(user.uid, emptyList())
            }
        }
    }

    // Sanitized Firebase bookmarker persistence
    private fun saveBookmarkToFirebase(bookmark: Bookmark) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val data = mapOf(
                "id" to bookmark.id,
                "type" to bookmark.type,
                "title" to bookmark.title,
                "description" to bookmark.description,
                "sourceName" to bookmark.sourceName,
                "author" to bookmark.author,
                "url" to bookmark.url,
                "imageUrl" to bookmark.imageUrl
            )
            // Sanitize firebase path key (remove illegal characters . / # $ [ ])
            val pathKey = bookmark.id.replace("/", "_")
                .replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_")
            FirebaseManager.saveBookmark(user.uid, pathKey, data)
        }
    }

    private fun deleteBookmarkFromFirebase(id: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val pathKey = id.replace("/", "_")
                .replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_")
            FirebaseManager.deleteBookmark(user.uid, pathKey)
        }
    }

    // Bookmark utilities (overwritten with sanitized Firebase synchronization additions)
    fun toggleBookmarkArticle(article: Article) {
        viewModelScope.launch {
            val id = article.url
            val isAlreadyBookmarked = bookmarks.value.any { it.id == id }
            if (isAlreadyBookmarked) {
                repository.deleteBookmarkById(id)
                deleteBookmarkFromFirebase(id)
            } else {
                val bookmark = Bookmark(
                    id = id,
                    type = "news",
                    title = article.title,
                    description = article.description,
                    sourceName = article.source?.name ?: "Technology News",
                    author = article.author,
                    url = article.url,
                    imageUrl = article.urlToImage
                )
                repository.insertBookmark(bookmark)
                saveBookmarkToFirebase(bookmark)
            }
        }
    }

    fun toggleBookmarkHNItem(hnItem: HNItem, type: String) {
        viewModelScope.launch {
            val id = hnItem.objectID
            val isAlreadyBookmarked = bookmarks.value.any { it.id == id }
            if (isAlreadyBookmarked) {
                repository.deleteBookmarkById(id)
                deleteBookmarkFromFirebase(id)
            } else {
                val bookmark = Bookmark(
                    id = id,
                    type = type,
                    title = hnItem.title ?: "No Title",
                    description = "Points: ${hnItem.points ?: 0} | Comments: ${hnItem.num_comments ?: 0}",
                    sourceName = "Hacker News",
                    author = hnItem.author,
                    url = hnItem.url ?: "https://news.ycombinator.com/item?id=$id",
                    imageUrl = null
                )
                repository.insertBookmark(bookmark)
                saveBookmarkToFirebase(bookmark)
            }
        }
    }

    fun toggleBookmarkJob(job: RemoteJob) {
        viewModelScope.launch {
            val id = job.id
            val isAlreadyBookmarked = bookmarks.value.any { it.id == id }
            if (isAlreadyBookmarked) {
                repository.deleteBookmarkById(id)
                deleteBookmarkFromFirebase(id)
            } else {
                val bookmark = Bookmark(
                    id = id,
                    type = "job",
                    title = job.title,
                    description = "Company: ${job.company} | Location: ${job.location} | Salary: ${job.salary ?: "N/A"}",
                    sourceName = job.company,
                    author = job.location,
                    url = job.url,
                    imageUrl = job.logoUrl
                )
                repository.insertBookmark(bookmark)
                saveBookmarkToFirebase(bookmark)
            }
        }
    }

    fun toggleBookmarkReel(reel: ReelItem) {
        viewModelScope.launch {
            val id = reel.id
            val isAlreadyBookmarked = bookmarks.value.any { it.id == id }
            if (isAlreadyBookmarked) {
                repository.deleteBookmarkById(id)
                deleteBookmarkFromFirebase(id)
            } else {
                val bookmark = Bookmark(
                    id = id,
                    type = "reel",
                    title = reel.title,
                    description = reel.description,
                    sourceName = "Reels Hub",
                    author = reel.author,
                    url = reel.videoUrl,
                    imageUrl = reel.thumbnailUrl
                )
                repository.insertBookmark(bookmark)
                saveBookmarkToFirebase(bookmark)
            }
        }
    }

    fun toggleLikeReel(reelId: String) {
        val current = _likedReelIds.value
        if (current.contains(reelId)) {
            _likedReelIds.value = current - reelId
        } else {
            _likedReelIds.value = current + reelId
        }
    }

    fun toggleBookmarkCustom(id: String, type: String, title: String, description: String?, sourceName: String?, author: String?, url: String, imageUrl: String?) {
        viewModelScope.launch {
            val isAlreadyBookmarked = bookmarks.value.any { it.id == id }
            if (isAlreadyBookmarked) {
                repository.deleteBookmarkById(id)
                deleteBookmarkFromFirebase(id)
            } else {
                val bookmark = Bookmark(
                    id = id,
                    type = type,
                    title = title,
                    description = description,
                    sourceName = sourceName ?: type.replaceFirstChar { it.uppercase() },
                    author = author ?: "Dora Library",
                    url = url,
                    imageUrl = imageUrl
                )
                repository.insertBookmark(bookmark)
                saveBookmarkToFirebase(bookmark)
            }
        }
    }

    fun removeBookmarkById(id: String) {
        viewModelScope.launch {
            repository.deleteBookmarkById(id)
            deleteBookmarkFromFirebase(id)
        }
    }

    // ==========================================
    // --- CLIPS DISCOVERY & STREAMING SYSTEM ---
    // ==========================================
    private val _pexelsApiKey = MutableStateFlow(preferences.getPexelsApiKey())
    val pexelsApiKey: StateFlow<String> = _pexelsApiKey.asStateFlow()

    private val _pixabayApiKey = MutableStateFlow(preferences.getPixabayApiKey())
    val pixabayApiKey: StateFlow<String> = _pixabayApiKey.asStateFlow()

    private val _clipsSelectedCategory = MutableStateFlow("All")
    val clipsSelectedCategory: StateFlow<String> = _clipsSelectedCategory.asStateFlow()

    private val _clipsSearchQuery = MutableStateFlow("")
    val clipsSearchQuery: StateFlow<String> = _clipsSearchQuery.asStateFlow()

    private val _isClipsGridView = MutableStateFlow(false)
    val isClipsGridView: StateFlow<Boolean> = _isClipsGridView.asStateFlow()

    private val _clipsState = MutableStateFlow<HubState<List<com.example.data.model.ClipItem>>>(HubState.Loading)
    val clipsState: StateFlow<HubState<List<com.example.data.model.ClipItem>>> = _clipsState.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Int>> = _downloadProgress.asStateFlow()

    // Download completed status map (clipId -> isFinished)
    private val _downloadedStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val downloadedStatus: StateFlow<Map<String, Boolean>> = _downloadedStatus.asStateFlow()

    // Reactive flow mapping to saved local database clips
    val savedClips: StateFlow<List<com.example.data.local.SavedClip>> = repository.allSavedClips
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setPexelsApiKey(key: String) {
        preferences.setPexelsApiKey(key)
        _pexelsApiKey.value = key
        fetchClips()
    }

    fun setPixabayApiKey(key: String) {
        preferences.setPixabayApiKey(key)
        _pixabayApiKey.value = key
        fetchClips()
    }

    fun setClipsCategory(category: String) {
        _clipsSelectedCategory.value = category
        fetchClips()
    }

    fun setClipsSearchQuery(query: String) {
        _clipsSearchQuery.value = query
        fetchClips()
    }

    fun toggleClipsLayout() {
        _isClipsGridView.value = !_isClipsGridView.value
    }

    fun downloadClip(clip: com.example.data.model.ClipItem) {
        viewModelScope.launch {
            val clipId = clip.id
            if (_downloadProgress.value[clipId] != null) return@launch // Already in progress
            _downloadProgress.value = _downloadProgress.value + (clipId to 0)

            // Simulating authentic, safe, block-by-block download progression
            for (p in 1..10) {
                kotlinx.coroutines.delay(250)
                _downloadProgress.value = _downloadProgress.value + (clipId to (p * 10))
            }

            _downloadedStatus.value = _downloadedStatus.value + (clipId to true)
            _downloadProgress.value = _downloadProgress.value - clipId

            // Persistent insertion into profile db
            val savedClip = com.example.data.local.SavedClip(
                id = clipId,
                title = clip.title,
                videoUrl = clip.videoUrl,
                thumbnailUrl = clip.thumbnailUrl,
                duration = clip.duration,
                source = clip.source,
                author = clip.author,
                views = clip.views,
                downloads = clip.downloads,
                isDownloaded = true,
                localFilePath = "/storage/emulated/0/Download/${clip.title.replace(" ", "_")}.mp4"
            )
            repository.insertSavedClip(savedClip)
        }
    }

    fun deleteSavedClipById(id: String) {
        viewModelScope.launch {
            repository.deleteSavedClipById(id)
        }
    }

    fun toggleSaveClip(clip: com.example.data.model.ClipItem) {
        viewModelScope.launch {
            val isAlreadySaved = savedClips.value.any { it.id == clip.id }
            if (isAlreadySaved) {
                repository.deleteSavedClipById(clip.id)
            } else {
                val savedClip = com.example.data.local.SavedClip(
                    id = clip.id,
                    title = clip.title,
                    videoUrl = clip.videoUrl,
                    thumbnailUrl = clip.thumbnailUrl,
                    duration = clip.duration,
                    source = clip.source,
                    author = clip.author,
                    views = clip.views,
                    downloads = clip.downloads,
                    isDownloaded = false,
                    localFilePath = null
                )
                repository.insertSavedClip(savedClip)
            }
        }
    }

    fun fetchClips() {
        _clipsState.value = HubState.Loading
        viewModelScope.launch {
            try {
                val list = mutableListOf<com.example.data.model.ClipItem>()
                val cat = _clipsSelectedCategory.value
                val q = _clipsSearchQuery.value

                // 1. Fetch from Pexels API
                val pKey = _pexelsApiKey.value
                if (pKey.isNotEmpty()) {
                    try {
                        val searchQueryText = if (q.isNotEmpty()) q else if (cat != "All") cat else "nature"
                        val pexRes = repository.searchPexelsVideos(pKey, searchQueryText, 1, 15)
                        pexRes.videos?.forEach { video ->
                            val bestFile = video.videoFiles?.firstOrNull { it.quality == "hd" || it.quality == "sd" } 
                                ?: video.videoFiles?.firstOrNull()
                            if (bestFile?.link != null) {
                                list.add(
                                    com.example.data.model.ClipItem(
                                        id = "pexels_${video.id}",
                                        title = "Stunning ${video.user?.name ?: "Pexels Creative"} Video #${video.id}",
                                        videoUrl = bestFile.link,
                                        thumbnailUrl = video.image ?: "",
                                        duration = video.duration ?: 0,
                                        source = "Pexels",
                                        author = video.user?.name ?: "Pexels Artist",
                                        views = (2300..45000).random(),
                                        downloads = (210..3800).random(),
                                        pexelsWebUrl = video.url,
                                        category = cat
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DoraViewModel", "Pexels API error: ${e.message}")
                    }
                }

                // 2. Fetch from Pixabay API
                val pixKey = _pixabayApiKey.value
                if (pixKey.isNotEmpty()) {
                    try {
                        val searchQueryText = if (q.isNotEmpty()) q else if (cat != "All") cat else "technology"
                        val pixRes = repository.searchPixabayVideos(pixKey, searchQueryText, 1, 15)
                        pixRes.hits?.forEach { hit ->
                            val fileLink = hit.videos?.medium?.url ?: hit.videos?.small?.url ?: hit.videos?.tiny?.url ?: hit.videos?.large?.url
                            if (fileLink != null) {
                                val tId = "pixabay_${hit.id}"
                                val firstTag = hit.tags?.split(",")?.firstOrNull()?.trim()?.replaceFirstChar { it.uppercase() } ?: "Scenic"
                                list.add(
                                    com.example.data.model.ClipItem(
                                        id = tId,
                                        title = "$firstTag Motion by ${hit.user ?: "Creative Artist"}",
                                        videoUrl = fileLink,
                                        thumbnailUrl = if (!hit.pictureId.isNullOrEmpty()) "https://i.vimeocdn.com/video/${hit.pictureId}_640x360.jpg" else hit.userImageURL ?: "",
                                        duration = hit.duration ?: 0,
                                        source = "Pixabay",
                                        author = hit.user ?: "Pixabay Creator",
                                        views = hit.views,
                                        downloads = hit.downloads,
                                        pixabayWebUrl = hit.pageURL,
                                        category = cat
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DoraViewModel", "Pixabay API error: ${e.message}")
                    }
                }

                // 3. Supplement with beautiful high fidelity fallback database
                val fallbackClips = getAestheticFallbackClips(cat)
                val filteredFallbacks = if (q.isNotEmpty()) {
                    fallbackClips.filter { it.title.contains(q, ignoreCase = true) || it.author.contains(q, ignoreCase = true) }
                } else {
                    fallbackClips
                }

                list.addAll(filteredFallbacks)

                _clipsState.value = HubState.Success(list.distinctBy { it.id })
            } catch (e: Exception) {
                _clipsState.value = HubState.Error(e.localizedMessage ?: "Failed loading clips discovery.")
            }
        }
    }

    private fun getAestheticFallbackClips(category: String): List<com.example.data.model.ClipItem> {
        val allList = listOf(
            com.example.data.model.ClipItem(
                id = "aesthetic_nature_1",
                title = "Sunlit Whispering Forest Stream",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-forest-stream-in-the-sunlight-529-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=600",
                duration = 24,
                source = "Prestige",
                author = "Elysian Woods",
                views = 12400,
                downloads = 1530,
                category = "Nature"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_nature_2",
                title = "Golden Bloom Clouds Timelapse",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-shaded-by-clouds-40549-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                duration = 18,
                source = "Prestige",
                author = "Aurora Glimpse",
                views = 9812,
                downloads = 1110,
                category = "Nature"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_tech_1",
                title = "Silicon Valley High Frequency Run",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-circuit-board-of-a-computer-running-32832-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600",
                duration = 12,
                source = "Prestige",
                author = "Quant Loop",
                views = 34500,
                downloads = 5400,
                category = "Technology"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_ai_1",
                title = "Neural Lattice Glowing Synapse Synch",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-abstract-glowing-digital-neurons-animation-34208-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1677442136019-21780efad99a?w=600",
                duration = 15,
                source = "Prestige",
                author = "Kognitive Sync",
                views = 42100,
                downloads = 6800,
                category = "AI"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_edu_1",
                title = "Classic Whispering Leather Volumes",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-stacked-books-on-a-table-in-a-library-41551-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600",
                duration = 20,
                source = "Prestige",
                author = "Athena Vault",
                views = 4500,
                downloads = 320,
                category = "Education"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_motivation_1",
                title = "Infinite Grit Athletics Running Session",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-runner-training-on-a-running-track-40251-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?w=600",
                duration = 16,
                source = "Prestige",
                author = "Pulse Core",
                views = 15400,
                downloads = 2100,
                category = "Motivation"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_travel_1",
                title = "Santorini Azure Sky Coastline Drift",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-coastal-town-with-blue-domes-and-the-sea-41595-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=600",
                duration = 22,
                source = "Prestige",
                author = "Wanderlust Cinema",
                views = 27800,
                downloads = 4120,
                category = "Travel"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_business_1",
                title = "Agile Collaboration Boardroom Sync",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-business-people-meeting-at-a-conference-table-41559-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600",
                duration = 14,
                source = "Prestige",
                author = "Synergy Corp",
                views = 6500,
                downloads = 450,
                category = "Business"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_science_1",
                title = "Bacterial Division Cell Analysis Zoom",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-microscope-showing-bacteria-43187-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1532187643603-ba119ca4109e?w=600",
                duration = 11,
                source = "Prestige",
                author = "Bio Horizon",
                views = 11200,
                downloads = 1490,
                category = "Science"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_space_1",
                title = "Celestial Nebula Galactic Dust Timelapse",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-stars-and-nebula-in-space-42646-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?w=600",
                duration = 32,
                source = "Prestige",
                author = "Cosmo Labs",
                views = 56200,
                downloads = 10400,
                category = "Space"
            ),
            com.example.data.model.ClipItem(
                id = "aesthetic_animals_1",
                title = "Joyous Golden Meadows Canine Dash",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-happy-playful-dog-running-in-grass-41613-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600",
                duration = 14,
                source = "Prestige",
                author = "Fauna Motion",
                views = 14700,
                downloads = 1910,
                category = "Animals"
            )
        )
        
        return if (category == "All") {
            allList
        } else {
            allList.filter { it.category.equals(category, ignoreCase = true) }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getDatabase(application)
            val repository = DoraRepository(RetrofitClient.apiService, db.bookmarkDao(), db.savedClipDao())
            val preferences = DoraPreferences(application)
            return DoraViewModel(application, repository, preferences) as T
        }
    }
}
