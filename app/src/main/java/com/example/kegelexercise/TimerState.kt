package com.example.kegelexercise

enum class TimerPhase {
    IDLE, TIGHTEN, RELAX, COMPLETED
}

data class TimerState(
    val phase: TimerPhase = TimerPhase.IDLE,
    val phaseRemainingSeconds: Int = 0,
    val totalRemainingSeconds: Int = 0,
    val currentCycle: Int = 0,
    val totalDurationSeconds: Int = 0,
    val tightenSeconds: Int = 0,
    val relaxSeconds: Int = 0
)
