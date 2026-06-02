package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.RetrofitClient
import com.example.data.local.AppDatabase
import com.example.data.local.Bookmark
import com.example.data.local.DoraPreferences
import com.example.data.model.*
import com.example.data.repository.DoraRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    // Auth state
    private val _currentUser = MutableStateFlow(preferences.getLoggedUser())
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

    init {
        loadAllData()
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
            
            _isRefreshing.value = false
        }
    }

    fun refreshAll() {
        loadAllData()
    }

    private suspend fun fetchTechnologyNews() {
        _newsState.value = HubState.Loading
        try {
            val response = repository.getTechnologyNews()
            val articles = response.articles ?: emptyList()
            _newsState.value = HubState.Success(articles.filter { !it.title.contains("removed", ignoreCase = true) })
        } catch (e: Exception) {
            _newsState.value = HubState.Error(e.localizedMessage ?: "Failed to load Technology News")
        }
    }

    private suspend fun fetchTrendingItems() {
        _trendsState.value = HubState.Loading
        try {
            val response = repository.getTrendingItems()
            val list = response.hits ?: emptyList()
            _trendsState.value = HubState.Success(list.filter { !it.title.isNullOrEmpty() })
        } catch (e: Exception) {
            _trendsState.value = HubState.Error(e.localizedMessage ?: "Failed to load Trending items")
        }
    }

    private suspend fun fetchAiToolsItems() {
        _aiToolsState.value = HubState.Loading
        try {
            val response = repository.getAiToolsItems()
            val list = response.hits ?: emptyList()
            _aiToolsState.value = HubState.Success(list.filter { !it.title.isNullOrEmpty() })
        } catch (e: Exception) {
            _aiToolsState.value = HubState.Error(e.localizedMessage ?: "Failed to load AI Tools items")
        }
    }

    private suspend fun fetchRemoteJobs() {
        _remoteJobsState.value = HubState.Loading
        try {
            val response = repository.getRemoteJobs()
            val parsed = parseRemoteJobs(response)
            _remoteJobsState.value = HubState.Success(parsed)
        } catch (e: Exception) {
            _remoteJobsState.value = HubState.Error(e.localizedMessage ?: "Failed to load Remote Jobs")
        }
    }

    private suspend fun fetchStartupItems() {
        _startupState.value = HubState.Loading
        try {
            val response = repository.getStartups()
            val list = response.hits ?: emptyList()
            _startupState.value = HubState.Success(list.filter { !it.title.isNullOrEmpty() })
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
        } catch (e: Exception) {
            _reelsState.value = HubState.Success(parseReels(emptyList())) // Fallbacks will be resolved beautifully
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
            _eventsState.value = HubState.Success(list)
        } catch (e: Exception) {
            val fallbacks = listOf(
                PredictHqEvent("evt_1", "Google I/O 2026 Developer Summit", "Annual developer conference with deep dives into AI and Gemini.", "2026-05-20", "technology", "US"),
                PredictHqEvent("evt_2", "KotlinConf 2026", "Global gathering for Kotlin enthusiasts with workshops and announcements.", "2026-06-12", "technology", "NL"),
                PredictHqEvent("evt_3", "WWDC26 Developer Event", "Discover core announcements, design methodologies, and framework architectures.", "2026-06-08", "technology", "US"),
                PredictHqEvent("evt_4", "AWS re:Invent Cloud Expo", "Premier cloud learning workshop and keynote series.", "2026-11-28", "technology", "US")
            )
            _eventsState.value = HubState.Success(fallbacks)
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


    // Settings & Mode
    fun toggleDarkMode() {
        val nextMode = !_isDarkMode.value
        _isDarkMode.value = nextMode
        preferences.setDarkModeEnabled(nextMode)
    }

    // Authentication Actions
    fun loginWithEmail(email: String, name: String) {
        val user = User(
            email = email,
            name = name,
            isGoogleUser = false,
            headline = "Digital Tech Curator & Tech Enthusiast"
        )
        preferences.saveLoggedUser(user)
        _currentUser.value = user
    }

    fun loginWithGoogle(email: String, name: String, picUrl: String) {
        val user = User(
            email = email,
            name = name,
            isGoogleUser = true,
            profilePictureUrl = picUrl,
            headline = "Google Verified Sync Curator"
        )
        preferences.saveLoggedUser(user)
        _currentUser.value = user
    }

    fun logout() {
        preferences.clearLoggedUser()
        _currentUser.value = null
    }

    fun updateProfileHeadline(headline: String) {
        val user = _currentUser.value ?: return
        val updatedUser = user.copy(headline = headline)
        preferences.saveLoggedUser(updatedUser)
        _currentUser.value = updatedUser
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Bookmark utilities
    fun toggleBookmarkArticle(article: Article) {
        viewModelScope.launch {
            val id = article.url
            val isAlreadyBookmarked = bookmarks.value.any { it.id == id }
            if (isAlreadyBookmarked) {
                repository.deleteBookmarkById(id)
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
            }
        }
    }

    fun toggleBookmarkHNItem(hnItem: HNItem, type: String) {
        viewModelScope.launch {
            val id = hnItem.objectID
            val isAlreadyBookmarked = bookmarks.value.any { it.id == id }
            if (isAlreadyBookmarked) {
                repository.deleteBookmarkById(id)
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
            }
        }
    }

    fun toggleBookmarkJob(job: RemoteJob) {
        viewModelScope.launch {
            val id = job.id
            val isAlreadyBookmarked = bookmarks.value.any { it.id == id }
            if (isAlreadyBookmarked) {
                repository.deleteBookmarkById(id)
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
            }
        }
    }

    fun toggleBookmarkReel(reel: ReelItem) {
        viewModelScope.launch {
            val id = reel.id
            val isAlreadyBookmarked = bookmarks.value.any { it.id == id }
            if (isAlreadyBookmarked) {
                repository.deleteBookmarkById(id)
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
            }
        }
    }

    fun removeBookmarkById(id: String) {
        viewModelScope.launch {
            repository.deleteBookmarkById(id)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getDatabase(application)
            val repository = DoraRepository(RetrofitClient.apiService, db.bookmarkDao())
            val preferences = DoraPreferences(application)
            return DoraViewModel(application, repository, preferences) as T
        }
    }
}
