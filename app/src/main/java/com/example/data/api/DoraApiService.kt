package com.example.data.api

import com.example.data.model.AlgoliaResponse
import com.example.data.model.NewsResponse
import com.example.data.model.*
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DoraApiService {
    @GET("NewsAPI/top-headlines/category/technology/in.json")
    suspend fun getTechnologyNews(): NewsResponse

    @GET("https://hn.algolia.com/api/v1/search?query=trending")
    suspend fun getTrendingItems(): AlgoliaResponse

    @GET("https://hn.algolia.com/api/v1/search?query=ai")
    suspend fun getAiToolsItems(): AlgoliaResponse

    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
    @GET("https://remoteok.com/api")
    suspend fun getRemoteJobs(): List<Map<String, Any>>

    @GET("https://hn.algolia.com/api/v1/search?query=startup")
    suspend fun getStartups(): AlgoliaResponse

    @GET("https://raw.githubusercontent.com/poudyalanil/ca0bc05822a8c9558711a3e1e5aa5a1d/raw/ca058444a7276711a141aaabbfd490df1712a2aa/videos.json")
    suspend fun getReels(): List<Map<String, Any>>

    // 1. Events Hub
    @Headers("Content-Type: application/json")
    @GET("https://api.predicthq.com/v1/events")
    suspend fun getEvents(): PredictHqResponse

    // 2. Books Hub
    @GET("https://openlibrary.org/search.json?q=programming")
    suspend fun getBooks(): BookSearchResponse

    // 3. Podcasts Hub
    @GET("https://itunes.apple.com/search?term=technology&media=podcast")
    suspend fun getPodcasts(): PodcastSearchResponse

    // 4. Quotes Hub
    @GET("https://api.quotable.io/quotes")
    suspend fun getQuotes(): QuotesResponse

    // 5. Country Explorer Hub
    @GET("https://restcountries.com/v3.1/all")
    suspend fun getCountries(): List<CountryItem>

    // 6. Public APIs Hub
    @GET("https://api.publicapis.org/entries")
    suspend fun getPublicApis(): PublicApisResponse

    // 7. Animal & Nature Hub
    @GET("https://zoo-animal-api.herokuapp.com/animals/rand")
    suspend fun getZooAnimals(): List<ZooAnimal>

    // 8. History Hub
    @GET("https://history.muffinlabs.com/date")
    suspend fun getHistoryEvents(): HistoryResponse

    // 9. ISS Tracker Hub
    @GET("http://api.open-notify.org/iss-now.json")
    suspend fun getIssPosition(): IssResponse

    // 10. Art Hub
    @GET("https://collectionapi.metmuseum.org/public/collection/v1/search?q=art")
    suspend fun getArtWorks(): ArtSearchResponse

    @GET("https://collectionapi.metmuseum.org/public/collection/v1/objects/{id}")
    suspend fun getArtObjectDetail(@Path("id") id: Int): ArtObjectDetail

    // 11. Recipes Hub
    @GET("https://www.themealdb.com/api/json/v1/1/search.php?s=")
    suspend fun getRecipes(): RecipesResponse

    // 12. Polls Hub
    @GET("https://api.sampleapis.com/futurama/questions")
    suspend fun getPolls(): List<FuturamaQuestion>

    // 13. Brain Facts Hub
    @GET("https://uselessfacts.jsph.pl/api/v2/facts/random")
    suspend fun getBrainFacts(): UselessFact

    // 14. Currency Hub
    @GET("https://open.er-api.com/v6/latest/USD")
    suspend fun getCurrencies(): ExchangeRateResponse
}

