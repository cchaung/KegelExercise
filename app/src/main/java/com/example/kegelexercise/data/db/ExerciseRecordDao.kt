package com.example.kegelexercise.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseRecordDao {

    @Insert
    suspend fun insert(record: ExerciseRecord): Long

    @Query("SELECT * FROM exercise_records ORDER BY startTime DESC")
    fun getAllRecords(): Flow<List<ExerciseRecord>>

    @Query(
        """
        SELECT * FROM exercise_records
        WHERE date >= :from AND date <= :to
        ORDER BY startTime DESC
        """
    )
    fun getRecordsInRange(from: String, to: String): Flow<List<ExerciseRecord>>

    @Query("SELECT * FROM exercise_records WHERE date = :date ORDER BY startTime DESC")
    fun getRecordsForDate(date: String): Flow<List<ExerciseRecord>>

    @Delete
    suspend fun delete(record: ExerciseRecord)
}
