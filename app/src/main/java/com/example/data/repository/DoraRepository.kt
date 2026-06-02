package com.example.data.repository

import com.example.data.api.DoraApiService
import com.example.data.local.Bookmark
import com.example.data.local.BookmarkDao
import com.example.data.model.AlgoliaResponse
import com.example.data.model.NewsResponse
import kotlinx.coroutines.flow.Flow

class DoraRepository(
    private val apiService: DoraApiService,
    private val bookmarkDao: BookmarkDao
) {
    suspend fun getTechnologyNews(): NewsResponse {
        return apiService.getTechnologyNews()
    }

    suspend fun getTrendingItems(): AlgoliaResponse {
        return apiService.getTrendingItems()
    }

    suspend fun getAiToolsItems(): AlgoliaResponse {
        return apiService.getAiToolsItems()
    }

    suspend fun getRemoteJobs(): List<Map<String, Any>> {
        return apiService.getRemoteJobs()
    }

    suspend fun getStartups(): AlgoliaResponse {
        return apiService.getStartups()
    }

    suspend fun getReels(): List<Map<String, Any>> {
        return apiService.getReels()
    }

    // 14 Extra Hubs Callers
    suspend fun getEvents() = apiService.getEvents()
    suspend fun getBooks() = apiService.getBooks()
    suspend fun getPodcasts() = apiService.getPodcasts()
    suspend fun getQuotes() = apiService.getQuotes()
    suspend fun getCountries() = apiService.getCountries()
    suspend fun getPublicApis() = apiService.getPublicApis()
    suspend fun getZooAnimals() = apiService.getZooAnimals()
    suspend fun getHistoryEvents() = apiService.getHistoryEvents()
    suspend fun getIssPosition() = apiService.getIssPosition()
    suspend fun getArtWorks() = apiService.getArtWorks()
    suspend fun getArtObjectDetail(id: Int) = apiService.getArtObjectDetail(id)
    suspend fun getRecipes() = apiService.getRecipes()
    suspend fun getPolls() = apiService.getPolls()
    suspend fun getBrainFacts() = apiService.getBrainFacts()
    suspend fun getCurrencies() = apiService.getCurrencies()

    val allBookmarks: Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()

    suspend fun insertBookmark(bookmark: Bookmark) {
        bookmarkDao.insertBookmark(bookmark)
    }

    suspend fun deleteBookmarkById(id: String) {
        bookmarkDao.deleteBookmarkById(id)
    }

    fun isBookmarked(id: String): Flow<Boolean> {
        return bookmarkDao.isBookmarked(id)
    }
}
