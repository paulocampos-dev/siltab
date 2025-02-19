package com.prototype.silver_tab.data.models.auth

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val email: String,
    val role: Int,
    val roleName: String,
    val position: Long,
    val positionName: String,
    val id: Long,
    val userEntityAuthority: String,
    val userHasAccessToCommercialPolicy: String,
    val tokenType: String
)