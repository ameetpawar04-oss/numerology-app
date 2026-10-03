package com.amietppawar.numerology.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore for app preferences and settings
 * Stores: theme mode, notification settings, user preferences
 */
private const val PREFERENCES_NAME = "app_preferences"

val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)

/**
 * Preference keys
 */
object PreferenceKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode") // "system", "dark", "light"
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val NOTIFICATION_TIME = stringPreferencesKey("notification_time") // HH:MM format
    val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
    val FIRST_LAUNCH = booleanPreferencesKey("first_launch") // For onboarding
}

/**
 * Settings repository for DataStore operations
 */
class SettingsRepository(private val context: Context) {

    /**
     * Get theme mode as Flow
     */
    fun getThemeMode(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.THEME_MODE] ?: "dark"
    }

    /**
     * Set theme mode
     */
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_MODE] = mode
        }
    }

    /**
     * Get notifications enabled state
     */
    fun getNotificationsEnabled(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] ?: false
    }

    /**
     * Set notifications enabled
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    /**
     * Get notification time
     */
    fun getNotificationTime(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.NOTIFICATION_TIME] ?: "08:00"
    }

    /**
     * Set notification time
     */
    suspend fun setNotificationTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOTIFICATION_TIME] = time
        }
    }

    /**
     * Get reduce motion preference
     */
    fun getReduceMotion(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.REDUCE_MOTION] ?: false
    }

    /**
     * Set reduce motion preference
     */
    suspend fun setReduceMotion(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.REDUCE_MOTION] = enabled
        }
    }

    /**
     * Get first launch flag
     */
    fun getFirstLaunch(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FIRST_LAUNCH] ?: true
    }

    /**
     * Set first launch flag to false (onboarding completed)
     */
    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.FIRST_LAUNCH] = false
        }
    }

    /**
     * Reset all settings to defaults
     */
    suspend fun resetAllSettings() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
