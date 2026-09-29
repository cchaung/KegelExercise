package com.example.kegelexercise.testing

import com.example.kegelexercise.data.db.ExerciseRecord
import java.time.LocalDate
import java.time.LocalTime

/**
 * Builds an [ExerciseRecord] with sane defaults so each test only spells out
 * the fields it actually cares about.
 */
fun exerciseRecord(
    date: LocalDate,
    at: LocalTime = LocalTime.of(9, 0),
    durationMinutes: Int = 5,
    tightenSeconds: Int = 5,
    relaxSeconds: Int = 5,
    completedCycles: Int = 30,
    wasCompleted: Boolean = true
): ExerciseRecord = ExerciseRecord(
    date = date,
    startTime = date.atTime(at),
    durationMinutes = durationMinutes,
    tightenSeconds = tightenSeconds,
    relaxSeconds = relaxSeconds,
    completedCycles = completedCycles,
    wasCompleted = wasCompleted
)
