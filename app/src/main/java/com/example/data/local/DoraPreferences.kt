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
        val name = prefs.getString("user_name", "Explorer") ?: "Explorer"
        val isGoogle = prefs.getBoolean("user_is_google", false)
        val pic = prefs.getString("user_pic", null)
        val headline = prefs.getString("user_headline", "Tech Explorer & Digital Native") ?: "Tech Explorer & Digital Native"
        return User(email, name, isGoogle, pic, headline)
    }

    fun saveLoggedUser(user: User) {
        prefs.edit().apply {
            putString("user_email", user.email)
            putString("user_name", user.name)
            putBoolean("user_is_google", user.isGoogleUser)
            putString("user_pic", user.profilePictureUrl)
            putString("user_headline", user.headline)
            apply()
        }
    }

    fun clearLoggedUser() {
        prefs.edit().apply {
            remove("user_email")
            remove("user_name")
            remove("user_is_google")
            remove("user_pic")
            remove("user_headline")
            apply()
        }
    }
}
