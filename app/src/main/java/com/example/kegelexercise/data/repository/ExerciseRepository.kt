package com.example.kegelexercise.data.repository

import com.example.kegelexercise.data.db.ExerciseRecord
import com.example.kegelexercise.data.db.ExerciseRecordDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val dao: ExerciseRecordDao
) {
    suspend fun saveRecord(record: ExerciseRecord) {
        dao.insert(record)
    }

    fun getAllRecords(): Flow<List<ExerciseRecord>> = dao.getAllRecords()

    fun getRecordsForDate(date: LocalDate): Flow<List<ExerciseRecord>> =
        dao.getRecordsForDate(date.toString())

    fun getRecordsInRange(from: LocalDate, to: LocalDate): Flow<List<ExerciseRecord>> =
        dao.getRecordsInRange(from.toString(), to.toString())
}
