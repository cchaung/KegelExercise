package com.example.kegelexercise.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserPreferences(
    val durationMinutes: Int = 5,
    val tightenSeconds: Int = 5,
    val relaxSeconds: Int = 5,
    val vibrationLevel: Int = 7  // 1–10 scale
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_DURATION = intPreferencesKey("duration_minutes")
        val KEY_TIGHTEN = intPreferencesKey("tighten_seconds")
        val KEY_RELAX = intPreferencesKey("relax_seconds")
        val KEY_VIBRATION_LEVEL = intPreferencesKey("vibration_level")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            durationMinutes = prefs[KEY_DURATION] ?: 5,
            tightenSeconds = prefs[KEY_TIGHTEN] ?: 5,
            relaxSeconds = prefs[KEY_RELAX] ?: 5,
            vibrationLevel = prefs[KEY_VIBRATION_LEVEL] ?: 7
        )
    }

    suspend fun savePreferences(
        durationMinutes: Int,
        tightenSeconds: Int,
        relaxSeconds: Int,
        vibrationLevel: Int
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DURATION] = durationMinutes
            prefs[KEY_TIGHTEN] = tightenSeconds
            prefs[KEY_RELAX] = relaxSeconds
            prefs[KEY_VIBRATION_LEVEL] = vibrationLevel
        }
    }
}
