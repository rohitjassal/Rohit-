package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    
    var isInitialized = false
        private set

    lateinit var auth: FirebaseAuth
        private set
    lateinit var database: FirebaseDatabase
        private set
    lateinit var storage: FirebaseStorage
        private set

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            val apps = FirebaseApp.getApps(context)
            val app = if (apps.isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey("AIzaSyDgn7X0_qbVjpelk9F4thHV41bUDA-65Ug")
                    .setApplicationId("1:121863133811:web:63035f167ec93064686806")
                    .setProjectId("dora-ba40d")
                    .setStorageBucket("dora-ba40d.firebasestorage.app")
                    .setDatabaseUrl("https://dora-ba40d-default-rtdb.firebaseio.com")
                    .build()
                FirebaseApp.initializeApp(context.applicationContext, options)
            } else {
                apps.first()
            }
            
            auth = FirebaseAuth.getInstance(app)
            database = FirebaseDatabase.getInstance(app)
            storage = FirebaseStorage.getInstance(app)
            isInitialized = true
            Log.d(TAG, "Firebase successfully programmatically initialized.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed initialized Firebase programmatically", e)
        }
    }

    /**
     * Store custom user node in the Realtime Database at users/{uid}
     */
    suspend fun saveUserNode(user: User) {
        if (!isInitialized) return
        try {
            withTimeoutOrNull(2000) {
                val userMap = mapOf(
                    "uid" to user.uid,
                    "name" to user.name,
                    "email" to user.email,
                    "photoURL" to (user.profilePictureUrl ?: ""),
                    "createdAt" to user.createdAt,
                    "lastLogin" to user.lastLogin,
                    "isGoogleUser" to user.isGoogleUser,
                    "headline" to user.headline
                )
                database.getReference("users").child(user.uid).setValue(userMap).await()
                Log.d(TAG, "Successfully saved/updated user record under users/${user.uid} in Firebase Realtime Database.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user node to RTDB", e)
        }
    }

    /**
     * Fetch user info from RTDB (falls back to local details if needed)
     */
    suspend fun getUserNode(uid: String): Map<String, Any>? {
        if (!isInitialized) return null
        return try {
            withTimeoutOrNull(2500) {
                val snapshot = database.getReference("users").child(uid).get().await()
                if (snapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    snapshot.value as? Map<String, Any>
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user node from Firebase DB", e)
            null
        }
    }

    /**
     * Storing User Settings in Firebase on users/{uid}/settings
     */
    suspend fun saveUserSettings(uid: String, settings: Map<String, Any>) {
        if (!isInitialized) return
        try {
            withTimeoutOrNull(2000) {
                database.getReference("users").child(uid).child("settings").setValue(settings).await()
                Log.d(TAG, "Saved settings for users/$uid/settings successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user settings to database", e)
        }
    }

    suspend fun getUserSettings(uid: String): Map<String, Any>? {
        if (!isInitialized) return null
        return try {
            withTimeoutOrNull(2500) {
                val snapshot = database.getReference("users").child(uid).child("settings").get().await()
                @Suppress("UNCHECKED_CAST")
                snapshot.value as? Map<String, Any>
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user settings from Remote RTDB", e)
            null
        }
    }

    /**
     * Storing Bookmark in Firebase under users/{uid}/bookmarks
     */
    suspend fun saveBookmark(uid: String, bookmarkId: String, bookmarkData: Map<String, Any?>) {
        if (!isInitialized) return
        try {
            withTimeoutOrNull(2000) {
                database.getReference("users").child(uid).child("bookmarks").child(bookmarkId).setValue(bookmarkData).await()
                Log.d(TAG, "Saved bookmark $bookmarkId under users/$uid/bookmarks")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save bookmark via firebase", e)
        }
    }

    suspend fun deleteBookmark(uid: String, bookmarkId: String) {
        if (!isInitialized) return
        try {
            withTimeoutOrNull(2000) {
                database.getReference("users").child(uid).child("bookmarks").child(bookmarkId).removeValue().await()
                Log.d(TAG, "Deleted bookmark $bookmarkId under users/$uid/bookmarks")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete bookmark via firebase", e)
        }
    }

    suspend fun getBookmarks(uid: String): Map<String, Any>? {
        if (!isInitialized) return null
        return try {
            withTimeoutOrNull(2500) {
                val snapshot = database.getReference("users").child(uid).child("bookmarks").get().await()
                @Suppress("UNCHECKED_CAST")
                snapshot.value as? Map<String, Any>
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed getting bookmarks from Firebase", e)
            null
        }
    }

    /**
     * Sync local search history to Firebase Realtime Database
     */
    suspend fun saveSearchHistory(uid: String, history: List<String>) {
        if (!isInitialized) return
        try {
            withTimeoutOrNull(2000) {
                database.getReference("users").child(uid).child("searchHistory").setValue(history).await()
                Log.d(TAG, "Successfully synced search history to Firebase RTDB")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed syncing search history to Firebase", e)
        }
    }

    suspend fun getSearchHistory(uid: String): List<String>? {
        if (!isInitialized) return null
        return try {
            withTimeoutOrNull(2500) {
                val snapshot = database.getReference("users").child(uid).child("searchHistory").get().await()
                if (snapshot.exists()) {
                    val list = snapshot.value as? List<*>
                    list?.filterIsInstance<String>()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching search history from Firebase", e)
            null
        }
    }

    /**
     * Firebase upload of Profile picture helper (using Firebase Storage)
     * Uploads bytes to profilePhotos/{uid}.jpg and returns the download url.
     */
    suspend fun uploadProfilePhoto(uid: String, imageBytes: ByteArray): String? {
        if (!isInitialized) return null
        return try {
            withTimeoutOrNull(5000) {
                val ref = storage.reference.child("profilePhotos/$uid.jpg")
                val uploadTask = ref.putBytes(imageBytes).await()
                val downloadUrl = uploadTask.metadata?.reference?.downloadUrl?.await()
                downloadUrl?.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed profile picture storage upload to Firebase", e)
            null
        }
    }
}
