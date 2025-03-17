package com.prototype.silver_tab.data.models.auth

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userId: Long? = null,
    val username: String? = null,
    val email: String? = null,
    val role: Int? = null,
    val roleName: String? = null,
    val position: Long? = null,
    val positionName: String? = null,
    val userEntityAuthority: String? = null,
    val hasCommercialPolicyAccess: String? = null,
    val error: String? = null
)
