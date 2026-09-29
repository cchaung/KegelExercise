package com.example.kegelexercise.data.db

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `LocalDate survives a round trip`() {
        val date = LocalDate.of(2024, 2, 29)
        assertThat(converters.toLocalDate(converters.fromLocalDate(date))).isEqualTo(date)
    }

    @Test
    fun `LocalDateTime round trip keeps second precision`() {
        val time = LocalDateTime.of(2026, 9, 30, 23, 59, 58)
        assertThat(converters.toLocalDateTime(converters.fromLocalDateTime(time))).isEqualTo(time)
    }

    @Test
    fun `LocalDateTime round trip survives a midnight value`() {
        // toString() drops the ":00" seconds field here, which parse() must still accept.
        val midnight = LocalDateTime.of(2026, 1, 1, 0, 0, 0)
        assertThat(converters.fromLocalDateTime(midnight)).isEqualTo("2026-01-01T00:00")
        assertThat(converters.toLocalDateTime(converters.fromLocalDateTime(midnight)))
            .isEqualTo(midnight)
    }

    @Test
    fun `stored dates sort chronologically as plain strings`() {
        // ExerciseRecordDao compares the stored text with >= and <=, which only works
        // because the ISO-8601 form is lexicographically ordered.
        val earlier = converters.fromLocalDate(LocalDate.of(2026, 9, 9))
        val later = converters.fromLocalDate(LocalDate.of(2026, 9, 10))
        assertThat(earlier).isLessThan(later)
    }
}
