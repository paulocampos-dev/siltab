package com.prototype.silver_tab.data.models.auth

data class LoginResponse(
    val positionName: String,
    val role: Int,
    val roleName: String,
    val position: Int,
    val id: Int,
    val userHasAccessToCommercialPolicy: String,
    val accessToken: String,
    val tokenType: String,
    val email: String,
    val refreshToken: String,
    val username: String,
    val userEntityAuthority: String,
)