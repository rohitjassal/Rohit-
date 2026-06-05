package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_clips")
data class SavedClip(
    @PrimaryKey val id: String, // Dynamic key combining source + item ID
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val duration: Int,
    val source: String,
    val author: String,
    val views: Int?,
    val downloads: Int?,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface SavedClipDao {
    @Query("SELECT * FROM saved_clips ORDER BY timestamp DESC")
    fun getAllSavedClips(): Flow<List<SavedClip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedClip(clip: SavedClip)

    @Query("DELETE FROM saved_clips WHERE id = :id")
    suspend fun deleteSavedClipById(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_clips WHERE id = :id)")
    fun isSaved(id: String): Flow<Boolean>

    @Query("UPDATE saved_clips SET isDownloaded = :downloaded, localFilePath = :filePath WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, downloaded: Boolean, filePath: String?)
}
