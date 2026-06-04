package com.example.data.model

data class User(
    val uid: String,
    val email: String,
    val name: String,
    val isGoogleUser: Boolean = false,
    val profilePictureUrl: String? = null,
    val headline: String = "Digital Minimalist & Tech Explorer",
    val createdAt: Long = 0L,
    val lastLogin: Long = 0L
)
