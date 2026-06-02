package com.example.data.model

import com.squareup.moshi.JsonClass

// 1. Events Hub
@JsonClass(generateAdapter = true)
data class PredictHqResponse(
    val results: List<PredictHqEvent>?
)

@JsonClass(generateAdapter = true)
data class PredictHqEvent(
    val id: String,
    val title: String,
    val description: String?,
    val start: String?,
    val category: String?,
    val country: String?
)

// 2. Books Hub
@JsonClass(generateAdapter = true)
data class BookSearchResponse(
    val docs: List<BookDoc>?
)

@JsonClass(generateAdapter = true)
data class BookDoc(
    val title: String,
    val author_name: List<String>?,
    val first_publish_year: Int?,
    val cover_i: Int?,
    val key: String
)

// 3. Podcasts Hub
@JsonClass(generateAdapter = true)
data class PodcastSearchResponse(
    val results: List<PodcastResult>?
)

@JsonClass(generateAdapter = true)
data class PodcastResult(
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val artworkUrl100: String?,
    val feedUrl: String?,
    val primaryGenreName: String?
)

// 4. Quotes Hub
@JsonClass(generateAdapter = true)
data class QuotesResponse(
    val results: List<QuoteItem>?
)

@JsonClass(generateAdapter = true)
data class QuoteItem(
    val _id: String,
    val content: String,
    val author: String,
    val tags: List<String>?
)

// 5. Country Explorer Hub
@JsonClass(generateAdapter = true)
data class CountryItem(
    val name: CountryName,
    val capital: List<String>?,
    val region: String?,
    val subregion: String?,
    val population: Long?,
    val flags: CountryFlags,
    val cca2: String
)

@JsonClass(generateAdapter = true)
data class CountryName(
    val common: String,
    val official: String?
)

@JsonClass(generateAdapter = true)
data class CountryFlags(
    val png: String?,
    val svg: String?
)

// 6. Public APIs Hub
@JsonClass(generateAdapter = true)
data class PublicApisResponse(
    val count: Int?,
    val entries: List<PublicApiEntry>?
)

@JsonClass(generateAdapter = true)
data class PublicApiEntry(
    val API: String,
    val Description: String?,
    val Auth: String?,
    val HTTPS: Boolean?,
    val Link: String?,
    val Category: String?
)

// 7. Animal & Nature Hub
@JsonClass(generateAdapter = true)
data class ZooAnimal(
    val name: String,
    val animal_type: String?,
    val active_time: String?,
    val length_min: String?,
    val length_max: String?,
    val weight_min: String?,
    val weight_max: String?,
    val lifespan: String?,
    val habitat: String?,
    val diet: String?,
    val geo_range: String?,
    val image_link: String?
)

// 8. History Hub
@JsonClass(generateAdapter = true)
data class HistoryResponse(
    val date: String?,
    val url: String?,
    val data: HistoryData?
)

@JsonClass(generateAdapter = true)
data class HistoryData(
    val Events: List<HistoryEvent>?
)

@JsonClass(generateAdapter = true)
data class HistoryEvent(
    val year: String,
    val text: String,
    val links: List<HistoryLink>?
)

@JsonClass(generateAdapter = true)
data class HistoryLink(
    val title: String?,
    val link: String?
)

// 9. ISS Tracker Hub
@JsonClass(generateAdapter = true)
data class IssResponse(
    val message: String?,
    val iss_position: IssPosition?,
    val timestamp: Long?
)

@JsonClass(generateAdapter = true)
data class IssPosition(
    val latitude: String,
    val longitude: String
)

// 10. Art Hub
@JsonClass(generateAdapter = true)
data class ArtSearchResponse(
    val total: Int?,
    val objectIDs: List<Int>?
)

@JsonClass(generateAdapter = true)
data class ArtObjectDetail(
    val objectID: Int,
    val title: String,
    val artistDisplayName: String?,
    val objectDate: String?,
    val primaryImage: String?,
    val primaryImageSmall: String?,
    val repository: String?,
    val department: String?
)

// 11. Recipes Hub
@JsonClass(generateAdapter = true)
data class RecipesResponse(
    val meals: List<MealRecipe>?
)

@JsonClass(generateAdapter = true)
data class MealRecipe(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    val strYoutube: String?
)

// 12. Polls Hub
@JsonClass(generateAdapter = true)
data class FuturamaQuestion(
    val id: Int,
    val question: String,
    val possibleAnswers: List<String>?,
    val correctAnswer: String?
)

// 13. Brain Facts Hub
@JsonClass(generateAdapter = true)
data class UselessFact(
    val id: String,
    val text: String,
    val source: String?,
    val source_url: String?
)

// 14. Currency Hub
@JsonClass(generateAdapter = true)
data class ExchangeRateResponse(
    val result: String,
    val base_code: String?,
    val rates: Map<String, Double>?,
    val time_last_update_utc: String?
)
