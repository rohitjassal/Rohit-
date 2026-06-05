package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PexelsVideoResponse(
    val page: Int?,
    @Json(name = "per_page") val perPage: Int?,
    @Json(name = "total_results") val totalResults: Int?,
    val url: String?,
    val videos: List<PexelsVideo>?
)

@JsonClass(generateAdapter = true)
data class PexelsVideo(
    val id: Int?,
    val width: Int?,
    val height: Int?,
    val url: String?,
    val image: String?,
    val duration: Int?,
    val user: PexelsUser?,
    @Json(name = "video_files") val videoFiles: List<PexelsVideoFile>?,
    @Json(name = "video_pictures") val videoPictures: List<PexelsVideoPicture>?
)

@JsonClass(generateAdapter = true)
data class PexelsUser(
    val id: Int?,
    val name: String?,
    val url: String?
)

@JsonClass(generateAdapter = true)
data class PexelsVideoFile(
    val id: Int?,
    val quality: String?,
    @Json(name = "file_type") val fileType: String?,
    val width: Int?,
    val height: Int?,
    val link: String?
)

@JsonClass(generateAdapter = true)
data class PexelsVideoPicture(
    val id: Int?,
    val picture: String?,
    val nr: Int?
)

@JsonClass(generateAdapter = true)
data class PixabayVideoResponse(
    val total: Int?,
    val totalHits: Int?,
    val hits: List<PixabayVideoHit>?
)

@JsonClass(generateAdapter = true)
data class PixabayVideoHit(
    val id: Int?,
    val pageURL: String?,
    val type: String?,
    val tags: String?,
    val duration: Int?,
    @Json(name = "picture_id") val pictureId: String?,
    val videos: PixabayVideoFiles?,
    val views: Int?,
    val downloads: Int?,
    val likes: Int?,
    val comments: Int?,
    @Json(name = "user_id") val userId: Int?,
    val user: String?,
    val userImageURL: String?
)

@JsonClass(generateAdapter = true)
data class PixabayVideoFiles(
    val large: PixabayVideoDetails?,
    val medium: PixabayVideoDetails?,
    val small: PixabayVideoDetails?,
    val tiny: PixabayVideoDetails?
)

@JsonClass(generateAdapter = true)
data class PixabayVideoDetails(
    val url: String?,
    val width: Int?,
    val height: Int?,
    val size: Int?
)

/**
 * Clean UI representation of any discovered video clip from connected video resources.
 */
data class ClipItem(
    val id: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val duration: Int, // in seconds
    val source: String, // "Pexels", "Pixabay", or "Prestige"
    val author: String,
    val views: Int?,
    val downloads: Int?,
    val pexelsWebUrl: String? = null,
    val pixabayWebUrl: String? = null,
    val category: String = "All"
)
