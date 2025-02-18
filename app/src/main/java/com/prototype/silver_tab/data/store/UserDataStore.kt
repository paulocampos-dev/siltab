package com.prototype.silver_tab.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.prototype.silver_tab.data.models.auth.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

        private val USER_ID = longPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val EMAIL = stringPreferencesKey("email")
        private val ROLE = intPreferencesKey("role")
        private val ROLE_NAME = stringPreferencesKey("role_name")
        private val POSITION = longPreferencesKey("position")
        private val POSITION_NAME = stringPreferencesKey("position_name")
        private val USER_ENTITY_AUTHORITY = stringPreferencesKey("user_entity_authority")
        private val COMMERCIAL_POLICY_ACCESS = stringPreferencesKey("commercial_policy_access")
    }

    // Store user data
    suspend fun saveUserData(loginResponse: LoginResponse) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = loginResponse.id
            preferences[USERNAME] = loginResponse.username
            preferences[EMAIL] = loginResponse.email
            preferences[ROLE] = loginResponse.role
            preferences[ROLE_NAME] = loginResponse.roleName
            preferences[POSITION] = loginResponse.position
            preferences[POSITION_NAME] = loginResponse.positionName
            preferences[USER_ENTITY_AUTHORITY] = loginResponse.userEntityAuthority
            preferences[COMMERCIAL_POLICY_ACCESS] = loginResponse.userHasAccessToCommercialPolicy
        }
    }

    // Clear user data
    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Get individual data points
    val userId: Flow<Long?> = context.dataStore.data.map { it[USER_ID] }
    val username: Flow<String?> = context.dataStore.data.map { it[USERNAME] }
    val email: Flow<String?> = context.dataStore.data.map { it[EMAIL] }
    val role: Flow<Int?> = context.dataStore.data.map { it[ROLE] }
    val roleName: Flow<String?> = context.dataStore.data.map { it[ROLE_NAME] }
    val position: Flow<Long?> = context.dataStore.data.map { it[POSITION] }
    val positionName: Flow<String?> = context.dataStore.data.map { it[POSITION_NAME] }
    val userEntityAuthority: Flow<String?> = context.dataStore.data.map { it[USER_ENTITY_AUTHORITY] }
    val hasCommercialPolicyAccess: Flow<String?> = context.dataStore.data.map { it[COMMERCIAL_POLICY_ACCESS] }

    // Helper function to check permissions
    fun hasRole(requiredRole: Int): Flow<Boolean> = role.map { userRole ->
        userRole != null && userRole >= requiredRole
    }

    fun hasPosition(requiredPosition: Int): Flow<Boolean> = position.map { userPosition ->
        userPosition != null && userPosition >= requiredPosition
    }

}