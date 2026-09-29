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
    fun vibratePhaseChange(level: Int = VibrationLevel.DEFAULT) {
        val pulseDuration = VibrationLevel.pulseDurationMs(level)
        val amplitude = amplitudeFor(level)
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
    fun vibrateCompletion(level: Int = VibrationLevel.DEFAULT) {
        val effect = VibrationEffect.createOneShot(
            VibrationLevel.completionDurationMs(level),
            amplitudeFor(level)
        )
        vibrator.vibrate(effect)
    }

    private fun amplitudeFor(level: Int): Int =
        if (vibrator.hasAmplitudeControl()) VibrationLevel.amplitude(level)
        else VibrationEffect.DEFAULT_AMPLITUDE
}
