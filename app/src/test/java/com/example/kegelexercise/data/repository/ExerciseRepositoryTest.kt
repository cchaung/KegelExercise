package com.example.kegelexercise.data.repository

import com.example.kegelexercise.testing.FakeExerciseRecordDao
import com.example.kegelexercise.testing.exerciseRecord
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate

class ExerciseRepositoryTest {

    private val dao = FakeExerciseRecordDao()
    private val repository = ExerciseRepository(dao)

    private val day1 = LocalDate.of(2026, 9, 28)
    private val day2 = LocalDate.of(2026, 9, 29)
    private val day3 = LocalDate.of(2026, 9, 30)

    @Test
    fun `saved records come back from getAllRecords`() = runTest {
        repository.saveRecord(exerciseRecord(date = day1))

        assertThat(repository.getAllRecords().first()).hasSize(1)
    }

    @Test
    fun `getAllRecords returns the newest session first`() = runTest {
        dao.seed(
            exerciseRecord(date = day1),
            exerciseRecord(date = day3),
            exerciseRecord(date = day2)
        )

        val dates = repository.getAllRecords().first().map { it.date }

        assertThat(dates).containsExactly(day3, day2, day1).inOrder()
    }

    @Test
    fun `getRecordsForDate returns only that day`() = runTest {
        dao.seed(exerciseRecord(date = day1), exerciseRecord(date = day2))

        val records = repository.getRecordsForDate(day2).first()

        assertThat(records.map { it.date }).containsExactly(day2)
    }

    @Test
    fun `getRecordsInRange includes both endpoints`() = runTest {
        dao.seed(
            exerciseRecord(date = day1),
            exerciseRecord(date = day2),
            exerciseRecord(date = day3)
        )

        val records = repository.getRecordsInRange(from = day1, to = day3).first()

        assertThat(records.map { it.date }).containsExactly(day3, day2, day1).inOrder()
    }

    @Test
    fun `getRecordsInRange excludes days outside the window`() = runTest {
        dao.seed(exerciseRecord(date = day1), exerciseRecord(date = day3))

        val records = repository.getRecordsInRange(from = day2, to = day3).first()

        assertThat(records.map { it.date }).containsExactly(day3)
    }
}
