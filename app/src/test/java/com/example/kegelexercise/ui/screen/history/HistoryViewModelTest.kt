package com.example.kegelexercise.ui.screen.history

import app.cash.turbine.test
import com.example.kegelexercise.data.repository.ExerciseRepository
import com.example.kegelexercise.testing.FakeExerciseRecordDao
import com.example.kegelexercise.testing.MainDispatcherRule
import com.example.kegelexercise.testing.exerciseRecord
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dao = FakeExerciseRecordDao()
    private val repository = ExerciseRepository(dao)

    // Relative to today, because the ViewModel starts out selecting LocalDate.now().
    private val yesterday = LocalDate.now().minusDays(1)
    private val twoDaysAgo = LocalDate.now().minusDays(2)

    /**
     * The exposed flows are `stateIn(..., WhileSubscribed)`, so they stay parked on
     * their initial value until something collects them.
     */
    private fun TestScope.keepHot(flow: StateFlow<*>) {
        backgroundScope.launch { flow.collect {} }
        runCurrent()
    }

    @Test
    fun `allRecords exposes every stored session, newest first`() =
        runTest(mainDispatcherRule.testDispatcher) {
            dao.seed(
                exerciseRecord(date = twoDaysAgo),
                exerciseRecord(date = yesterday)
            )
            val viewModel = HistoryViewModel(repository)

            keepHot(viewModel.allRecords)

            assertThat(viewModel.allRecords.value.map { it.date })
                .containsExactly(yesterday, twoDaysAgo).inOrder()
        }

    @Test
    fun `selection starts on today`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = HistoryViewModel(repository)

        assertThat(viewModel.selectedDate.value).isEqualTo(LocalDate.now())
    }

    @Test
    fun `selectedDateRecords keeps only the sessions of the selected day`() =
        runTest(mainDispatcherRule.testDispatcher) {
            dao.seed(
                exerciseRecord(date = yesterday, at = LocalTime.of(8, 0)),
                exerciseRecord(date = yesterday, at = LocalTime.of(20, 0)),
                exerciseRecord(date = twoDaysAgo)
            )
            val viewModel = HistoryViewModel(repository)
            keepHot(viewModel.selectedDateRecords)

            viewModel.selectDate(yesterday)
            runCurrent()

            assertThat(viewModel.selectedDateRecords.value.map { it.date })
                .containsExactly(yesterday, yesterday)
        }

    @Test
    fun `selectedDateRecords is empty for a day with no sessions`() =
        runTest(mainDispatcherRule.testDispatcher) {
            dao.seed(exerciseRecord(date = yesterday))
            val viewModel = HistoryViewModel(repository)
            keepHot(viewModel.selectedDateRecords)

            viewModel.selectDate(twoDaysAgo)
            runCurrent()

            assertThat(viewModel.selectedDateRecords.value).isEmpty()
        }

    @Test
    fun `switching the selected day re-emits the filtered list`() =
        runTest(mainDispatcherRule.testDispatcher) {
            dao.seed(
                exerciseRecord(date = yesterday, at = LocalTime.of(8, 0)),
                exerciseRecord(date = yesterday, at = LocalTime.of(20, 0)),
                exerciseRecord(date = twoDaysAgo)
            )
            val viewModel = HistoryViewModel(repository)

            viewModel.selectedDateRecords.test {
                // Today was selected on construction and has no sessions.
                assertThat(awaitItem()).isEmpty()

                viewModel.selectDate(yesterday)
                assertThat(awaitItem()).hasSize(2)

                viewModel.selectDate(twoDaysAgo)
                assertThat(awaitItem()).hasSize(1)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
