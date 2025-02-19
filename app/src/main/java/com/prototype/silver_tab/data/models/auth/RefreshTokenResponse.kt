package com.prototype.silver_tab.data.models.auth

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)