package com.example.kegelexercise.util

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VibrationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator = run {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator
    }

    /**
     * Two short pulses to signal phase switch (tighten ↔ relax).
     * @param level 1–10 user-facing intensity. Controls both pulse duration and amplitude.
     */
    fun vibratePhaseChange(level: Int = 7) {
        val pulseDuration = levelToDuration(level)  // 40–220 ms
        val amplitude = levelToAmplitude(level)
        val effect = VibrationEffect.createWaveform(
            longArrayOf(0, pulseDuration, 60, pulseDuration),
            intArrayOf(0, amplitude, 0, amplitude),
            -1
        )
        vibrator.vibrate(effect)
    }

    /**
     * Single long pulse to signal session completion.
     * @param level 1–10 user-facing intensity.
     */
    fun vibrateCompletion(level: Int = 7) {
        val duration = (level * 80 + 120).toLong()  // 200–920 ms
        val amplitude = levelToAmplitude(level)
        val effect = VibrationEffect.createOneShot(duration, amplitude)
        vibrator.vibrate(effect)
    }

    private fun levelToAmplitude(level: Int): Int =
        if (vibrator.hasAmplitudeControl()) (level * 25).coerceIn(1, 255)
        else VibrationEffect.DEFAULT_AMPLITUDE

    private fun levelToDuration(level: Int): Long = (level * 20 + 20).toLong()  // 40–220 ms
}
