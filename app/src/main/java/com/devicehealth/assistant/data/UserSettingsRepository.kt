package com.devicehealth.assistant.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserSettingsRepository(private val context: Context) {
    companion object {
        val LARGE_APP_THRESHOLD = longPreferencesKey("large_app_threshold_bytes")
        val LOW_STORAGE_WARNING_PERCENT = intPreferencesKey("low_storage_warning_percent")
        
        const val DEFAULT_LARGE_APP_BYTES = 500L * 1024 * 1024 // 500 MB
        const val DEFAULT_LOW_STORAGE_PERCENT = 15
    }

    val largeAppThresholdFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[LARGE_APP_THRESHOLD] ?: DEFAULT_LARGE_APP_BYTES
    }

    val lowStorageWarningFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LOW_STORAGE_WARNING_PERCENT] ?: DEFAULT_LOW_STORAGE_PERCENT
    }

    suspend fun setLargeAppThreshold(bytes: Long) {
        context.dataStore.edit { preferences ->
            preferences[LARGE_APP_THRESHOLD] = bytes
        }
    }

    suspend fun setLowStorageWarningPercent(percent: Int) {
        context.dataStore.edit { preferences ->
            preferences[LOW_STORAGE_WARNING_PERCENT] = percent
        }
    }
}
