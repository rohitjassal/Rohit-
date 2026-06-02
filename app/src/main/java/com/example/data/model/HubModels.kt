package com.example.data.model

data class RemoteJob(
    val id: String,
    val title: String,
    val company: String,
    val logoUrl: String?,
    val salary: String?,
    val location: String?,
    val url: String,
    val datePosted: String?,
    val timestamp: Long,
    val tags: List<String>,
    val category: String
)

data class ReelItem(
    val id: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String?,
    val description: String?,
    val category: String,
    val author: String,
    val likesCount: Int,
    val savesCount: Int
)
