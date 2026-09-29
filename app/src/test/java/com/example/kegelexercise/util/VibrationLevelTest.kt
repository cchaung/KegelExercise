package com.example.kegelexercise.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VibrationLevelTest {

    @Test
    fun `phase-change pulse spans 40ms to 220ms across the level range`() {
        assertThat(VibrationLevel.pulseDurationMs(VibrationLevel.MIN)).isEqualTo(40L)
        assertThat(VibrationLevel.pulseDurationMs(VibrationLevel.MAX)).isEqualTo(220L)
    }

    @Test
    fun `completion pulse spans 200ms to 920ms across the level range`() {
        assertThat(VibrationLevel.completionDurationMs(VibrationLevel.MIN)).isEqualTo(200L)
        assertThat(VibrationLevel.completionDurationMs(VibrationLevel.MAX)).isEqualTo(920L)
    }

    @Test
    fun `a higher level always vibrates longer and harder`() {
        val levels = VibrationLevel.MIN..VibrationLevel.MAX
        for (level in levels.first until levels.last) {
            val next = level + 1
            assertThat(VibrationLevel.pulseDurationMs(next))
                .isGreaterThan(VibrationLevel.pulseDurationMs(level))
            assertThat(VibrationLevel.completionDurationMs(next))
                .isGreaterThan(VibrationLevel.completionDurationMs(level))
            assertThat(VibrationLevel.amplitude(next))
                .isGreaterThan(VibrationLevel.amplitude(level))
        }
    }

    @Test
    fun `amplitude stays inside the platform 1 to 255 range`() {
        for (level in VibrationLevel.MIN..VibrationLevel.MAX) {
            assertThat(VibrationLevel.amplitude(level)).isIn(1..255)
        }
    }

    @Test
    fun `levels outside 1 to 10 are clamped instead of producing absurd pulses`() {
        assertThat(VibrationLevel.pulseDurationMs(0))
            .isEqualTo(VibrationLevel.pulseDurationMs(VibrationLevel.MIN))
        assertThat(VibrationLevel.pulseDurationMs(-5))
            .isEqualTo(VibrationLevel.pulseDurationMs(VibrationLevel.MIN))
        assertThat(VibrationLevel.completionDurationMs(99))
            .isEqualTo(VibrationLevel.completionDurationMs(VibrationLevel.MAX))
        assertThat(VibrationLevel.amplitude(99))
            .isEqualTo(VibrationLevel.amplitude(VibrationLevel.MAX))
    }

    @Test
    fun `the default level sits inside the supported range`() {
        assertThat(VibrationLevel.DEFAULT).isIn(VibrationLevel.MIN..VibrationLevel.MAX)
    }
}
