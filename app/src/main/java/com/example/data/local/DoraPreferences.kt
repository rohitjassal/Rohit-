package com.example.data.local

import android.content.Context
import com.example.data.model.User

class DoraPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("dora_library_prefs", Context.MODE_PRIVATE)

    fun isDarkModeEnabled(): Boolean {
        // Default to dark mode for a premium magazine vibe!
        return prefs.getBoolean("dark_mode_enabled", true)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode_enabled", enabled).apply()
    }

    fun getLoggedUser(): User? {
        val email = prefs.getString("user_email", null) ?: return null
        val uid = prefs.getString("user_uid", "local_user") ?: "local_user"
        val name = prefs.getString("user_name", "Explorer") ?: "Explorer"
        val isGoogle = prefs.getBoolean("user_is_google", false)
        val pic = prefs.getString("user_pic", null)
        val headline = prefs.getString("user_headline", "Tech Explorer & Digital Native") ?: "Tech Explorer & Digital Native"
        val createdAt = prefs.getLong("user_created_at", 0L)
        val lastLogin = prefs.getLong("user_last_login", 0L)
        return User(uid, email, name, isGoogle, pic, headline, createdAt, lastLogin)
    }

    fun saveLoggedUser(user: User) {
        prefs.edit().apply {
            putString("user_uid", user.uid)
            putString("user_email", user.email)
            putString("user_name", user.name)
            putBoolean("user_is_google", user.isGoogleUser)
            putString("user_pic", user.profilePictureUrl)
            putString("user_headline", user.headline)
            putLong("user_created_at", user.createdAt)
            putLong("user_last_login", user.lastLogin)
            apply()
        }
    }

    fun clearLoggedUser() {
        prefs.edit().apply {
            remove("user_uid")
            remove("user_email")
            remove("user_name")
            remove("user_is_google")
            remove("user_pic")
            remove("user_headline")
            remove("user_created_at")
            remove("user_last_login")
            apply()
        }
    }

    // --- LOCATION PERSISTENCE ---
    fun getSelectedCountryCode(): String {
        return prefs.getString("selected_country_code", "US") ?: "US"
    }

    fun getSelectedCountryName(): String {
        return prefs.getString("selected_country_name", "United States") ?: "United States"
    }

    fun getSelectedState(): String {
        return prefs.getString("selected_state", "California") ?: "California"
    }

    fun getSelectedCity(): String {
        return prefs.getString("selected_city", "San Francisco") ?: "San Francisco"
    }

    fun isUseCurrentLocationEnabled(): Boolean {
        return prefs.getBoolean("use_current_location", true)
    }

    fun setLocationSelection(
        countryCode: String,
        countryName: String,
        state: String,
        city: String,
        useCurrent: Boolean
    ) {
        prefs.edit().apply {
            putString("selected_country_code", countryCode)
            putString("selected_country_name", countryName)
            putString("selected_state", state)
            putString("selected_city", city)
            putBoolean("use_current_location", useCurrent)
            apply()
        }
    }

    // --- DATE FILTER PERSISTENCE ---
    fun getDateFilter(): String {
        return prefs.getString("selected_date_filter", "ALL") ?: "ALL"
    }

    fun setDateFilter(filter: String) {
        prefs.edit().putString("selected_date_filter", filter).apply()
    }

    fun getCustomDateRange(): Pair<Long?, Long?> {
        val startVal = prefs.getLong("custom_date_start", -1L)
        val endVal = prefs.getLong("custom_date_end", -1L)
        val start = if (startVal != -1L) startVal else null
        val end = if (endVal != -1L) endVal else null
        return Pair(start, end)
    }

    fun setCustomDateRange(start: Long?, end: Long?) {
        prefs.edit().apply {
            putLong("custom_date_start", start ?: -1L)
            putLong("custom_date_end", end ?: -1L)
            apply()
        }
    }

    // --- VIDEO API KEYS ---
    fun getPexelsApiKey(): String {
        return prefs.getString("pexels_api_key", "") ?: ""
    }

    fun setPexelsApiKey(key: String) {
        prefs.edit().putString("pexels_api_key", key).apply()
    }

    fun getPixabayApiKey(): String {
        return prefs.getString("pixabay_api_key", "") ?: ""
    }

    fun setPixabayApiKey(key: String) {
        prefs.edit().putString("pixabay_api_key", key).apply()
    }
}
