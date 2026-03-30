package com.example.kegelexercise.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import com.example.kegelexercise.TimerPhase
import com.example.kegelexercise.TimerState
import com.example.kegelexercise.data.db.ExerciseRecord
import com.example.kegelexercise.data.repository.ExerciseRepository
import com.example.kegelexercise.util.NotificationHelper
import com.example.kegelexercise.util.VibrationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject lateinit var repository: ExerciseRepository
    @Inject lateinit var vibrationHelper: VibrationHelper
    @Inject lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    companion object {
        private val _timerState = MutableStateFlow(TimerState())
        val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

        const val ACTION_START = "com.example.kegelexercise.START"
        const val ACTION_STOP = "com.example.kegelexercise.STOP"
        const val EXTRA_DURATION_MIN = "duration_minutes"
        const val EXTRA_TIGHTEN_SEC = "tighten_seconds"
        const val EXTRA_RELAX_SEC = "relax_seconds"
        const val EXTRA_VIBRATION_LEVEL = "vibration_level"
    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durationMin = intent.getIntExtra(EXTRA_DURATION_MIN, 5)
                val tightenSec = intent.getIntExtra(EXTRA_TIGHTEN_SEC, 5)
                val relaxSec = intent.getIntExtra(EXTRA_RELAX_SEC, 5)
                val vibrationLevel = intent.getIntExtra(EXTRA_VIBRATION_LEVEL, 7)
                startForeground(
                    NotificationHelper.NOTIF_TIMER_ID,
                    notificationHelper.buildTimerNotification("運動進行中..."),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
                startTimer(durationMin, tightenSec, relaxSec, vibrationLevel)
            }
            ACTION_STOP -> stopTimer(wasCompleted = false)
        }
        return START_STICKY
    }

    private var timerJob: Job? = null
    private var sessionStartTime: LocalDateTime = LocalDateTime.now()
    private var currentVibrationLevel: Int = 7

    private fun startTimer(durationMin: Int, tightenSec: Int, relaxSec: Int, vibrationLevel: Int = 7) {
        timerJob?.cancel()
        sessionStartTime = LocalDateTime.now()
        val totalSeconds = durationMin * 60
        currentVibrationLevel = vibrationLevel

        _timerState.value = TimerState(
            phase = TimerPhase.TIGHTEN,
            phaseRemainingSeconds = tightenSec,
            totalRemainingSeconds = totalSeconds,
            currentCycle = 0,
            totalDurationSeconds = totalSeconds,
            tightenSeconds = tightenSec,
            relaxSeconds = relaxSec
        )
        vibrationHelper.vibratePhaseChange(vibrationLevel)

        timerJob = serviceScope.launch {
            var totalRemaining = totalSeconds
            var phaseRemaining = tightenSec
            var isTighten = true
            var cycles = 0

            while (totalRemaining > 0) {
                delay(1000L)
                totalRemaining--
                phaseRemaining--

                if (phaseRemaining <= 0 && totalRemaining > 0) {
                    isTighten = !isTighten
                    if (isTighten) cycles++
                    phaseRemaining = if (isTighten) tightenSec else relaxSec
                    vibrationHelper.vibratePhaseChange(vibrationLevel)
                }

                _timerState.value = TimerState(
                    phase = if (isTighten) TimerPhase.TIGHTEN else TimerPhase.RELAX,
                    phaseRemainingSeconds = phaseRemaining,
                    totalRemainingSeconds = totalRemaining,
                    currentCycle = cycles,
                    totalDurationSeconds = totalSeconds,
                    tightenSeconds = tightenSec,
                    relaxSeconds = relaxSec
                )

                if (totalRemaining % 5 == 0) {
                    notificationHelper.updateTimerNotification(formatTime(totalRemaining))
                }
            }

            stopTimer(
                wasCompleted = true,
                completedCycles = cycles,
                durationMin = durationMin,
                tightenSec = tightenSec,
                relaxSec = relaxSec
            )
        }
    }

    private fun stopTimer(
        wasCompleted: Boolean,
        completedCycles: Int = _timerState.value.currentCycle,
        durationMin: Int = _timerState.value.totalDurationSeconds / 60,
        tightenSec: Int = _timerState.value.tightenSeconds,
        relaxSec: Int = _timerState.value.relaxSeconds
    ) {
        timerJob?.cancel()
        _timerState.value = TimerState(phase = TimerPhase.COMPLETED)

        serviceScope.launch {
            repository.saveRecord(
                ExerciseRecord(
                    date = sessionStartTime.toLocalDate(),
                    startTime = sessionStartTime,
                    durationMinutes = durationMin,
                    tightenSeconds = tightenSec,
                    relaxSeconds = relaxSec,
                    completedCycles = completedCycles,
                    wasCompleted = wasCompleted
                )
            )
            if (wasCompleted) {
                vibrationHelper.vibrateCompletion(currentVibrationLevel)
                notificationHelper.showCompletionNotification()
            }
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        _timerState.value = TimerState()
    }

    private fun formatTime(seconds: Int): String =
        "%02d:%02d".format(seconds / 60, seconds % 60)
}
