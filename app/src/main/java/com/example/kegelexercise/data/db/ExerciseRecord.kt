package com.example.kegelexercise.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "exercise_records")
data class ExerciseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val startTime: LocalDateTime,
    val durationMinutes: Int,
    val tightenSeconds: Int,
    val relaxSeconds: Int,
    val completedCycles: Int,
    val wasCompleted: Boolean
)
