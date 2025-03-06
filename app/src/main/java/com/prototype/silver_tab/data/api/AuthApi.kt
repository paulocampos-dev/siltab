package com.prototype.silver_tab.data.api

import com.prototype.silver_tab.data.models.auth.LoginRequest
import com.prototype.silver_tab.data.models.auth.LoginResponse
import com.prototype.silver_tab.data.models.auth.RefreshTokenRequest
import com.prototype.silver_tab.data.models.auth.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/refreshtoken")
    suspend fun refreshToken(
        @Body refreshTokenRequest: RefreshTokenRequest
    ): Response<RefreshTokenResponse>
}