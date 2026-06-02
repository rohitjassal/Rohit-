package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey val id: String, // Article URL or HN objectID
    val type: String, // "news", "trend", "ai_tool"
    val title: String,
    val description: String?,
    val sourceName: String?, // "HackerNews" or source.name
    val author: String?,
    val url: String,
    val imageUrl: String?,
    val timestamp: Long = System.currentTimeMillis()
)
