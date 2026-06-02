package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlgoliaResponse(
    val hits: List<HNItem>?
)

@JsonClass(generateAdapter = true)
data class HNItem(
    val objectID: String,
    val title: String?,
    val url: String?,
    val author: String?,
    val points: Int?,
    val num_comments: Int?,
    val created_at: String?
)
