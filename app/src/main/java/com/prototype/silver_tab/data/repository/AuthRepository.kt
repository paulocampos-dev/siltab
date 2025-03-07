package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.data.api_connection.routes.AuthRoutes
import com.prototype.silver_tab.data.models.auth.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val api: AuthRoutes
) {
    fun login(username: String, password: String): Flow<AuthResult<LoginResponse>> = flow {
        try {
            emit(AuthResult.Loading())

            val loginRequest = LoginRequest(username, password)
            val response = api.login(loginRequest)

            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    emit(AuthResult.Success(loginResponse))
                } ?: emit(AuthResult.Error("Empty response body"))
            } else {
                emit(AuthResult.Error("Login failed: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(AuthResult.Error("Network error: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(AuthResult.Error("Connection error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(AuthResult.Error("Unknown error: ${e.localizedMessage}"))
        }
    }

    fun refreshToken(refreshToken: String): Flow<AuthResult<RefreshTokenResponse>> = flow {
        try {
            emit(AuthResult.Loading())

            val refreshRequest = RefreshTokenRequest(refreshToken)
            val response = api.refreshToken(refreshRequest)

            if (response.isSuccessful) {
                response.body()?.let { refreshResponse ->
                    emit(AuthResult.Success(refreshResponse))
                } ?: emit(AuthResult.Error("Empty response body"))
            } else {
                emit(AuthResult.Error("Token refresh failed: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(AuthResult.Error("Network error: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(AuthResult.Error("Connection error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(AuthResult.Error("Unknown error: ${e.localizedMessage}"))
        }
    }
}