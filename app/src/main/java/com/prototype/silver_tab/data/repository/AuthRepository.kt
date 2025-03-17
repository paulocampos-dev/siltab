package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.data.api_connection.routes.AuthRoutes
import com.prototype.silver_tab.data.models.auth.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

interface AuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun refreshToken(): Result<Unit>
    suspend fun getAccessToken(): String?
    suspend fun initialize()
}
