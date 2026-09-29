package com.example.kegelexercise.testing

import com.example.kegelexercise.data.db.ExerciseRecord
import com.example.kegelexercise.data.db.ExerciseRecordDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory stand-in for [ExerciseRecordDao]. Mirrors the ordering and the
 * inclusive date bounds of the real `@Query` annotations so tests written
 * against it stay honest.
 */
class FakeExerciseRecordDao : ExerciseRecordDao {

    private val records = MutableStateFlow<List<ExerciseRecord>>(emptyList())
    private var nextId = 1L

    /** Puts records in place without going through [insert]; ids are assigned in order. */
    fun seed(vararg seeded: ExerciseRecord) {
        records.value = seeded.map { it.copy(id = nextId++) }
    }

    override suspend fun insert(record: ExerciseRecord): Long {
        val id = nextId++
        records.value = records.value + record.copy(id = id)
        return id
    }

    override fun getAllRecords(): Flow<List<ExerciseRecord>> =
        records.map { list -> list.sortedByDescending { it.startTime } }

    override fun getRecordsInRange(from: String, to: String): Flow<List<ExerciseRecord>> =
        getAllRecords().map { list -> list.filter { it.date.toString() in from..to } }

    override fun getRecordsForDate(date: String): Flow<List<ExerciseRecord>> =
        getAllRecords().map { list -> list.filter { it.date.toString() == date } }

    override suspend fun delete(record: ExerciseRecord) {
        records.value = records.value.filterNot { it.id == record.id }
    }
}
