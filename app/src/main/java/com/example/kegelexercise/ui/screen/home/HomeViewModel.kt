package com.example.kegelexercise.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kegelexercise.data.preferences.UserPreferences
import com.example.kegelexercise.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    fun savePreferences(durationMinutes: Int, tightenSeconds: Int, relaxSeconds: Int, vibrationLevel: Int) {
        viewModelScope.launch {
            preferencesRepository.savePreferences(durationMinutes, tightenSeconds, relaxSeconds, vibrationLevel)
        }
    }
}
