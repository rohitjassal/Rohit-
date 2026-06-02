package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsResponse(
    val status: String?,
    val totalResults: Int?,
    val articles: List<Article>?
)

@JsonClass(generateAdapter = true)
data class Article(
    val source: ArticleSource?,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)

@JsonClass(generateAdapter = true)
data class ArticleSource(
    val id: String?,
    val name: String?
)
