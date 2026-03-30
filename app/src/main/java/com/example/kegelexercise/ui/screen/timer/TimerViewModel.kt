package com.example.kegelexercise.ui.screen.timer

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.kegelexercise.TimerState
import com.example.kegelexercise.service.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor() : ViewModel() {

    val timerState: StateFlow<TimerState> = TimerService.timerState

    fun stopTimer(context: Context) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)
    }
}
