package com.example.kegelexercise.util

/**
 * Maps the user-facing 1–10 intensity level onto the vibration parameters.
 *
 * Kept free of Android types so the mapping can be unit tested on the JVM;
 * [VibrationHelper] owns everything that needs a real [android.os.Vibrator].
 */
object VibrationLevel {

    const val MIN = 1
    const val MAX = 10
    const val DEFAULT = 7

    /** Pulse length for a phase-change buzz: 40–220 ms. */
    fun pulseDurationMs(level: Int): Long = (clamp(level) * 20 + 20).toLong()

    /** Pulse length for the single completion buzz: 200–920 ms. */
    fun completionDurationMs(level: Int): Long = (clamp(level) * 80 + 120).toLong()

    /** Amplitude for devices with amplitude control: 25–250 of the 1–255 range. */
    fun amplitude(level: Int): Int = (clamp(level) * 25).coerceIn(1, 255)

    private fun clamp(level: Int): Int = level.coerceIn(MIN, MAX)
}
