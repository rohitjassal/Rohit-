package com.example.data.model

data class User(
    val email: String,
    val name: String,
    val isGoogleUser: Boolean = false,
    val profilePictureUrl: String? = null,
    val headline: String = "Digital Minimalist & Tech Explorer"
)
