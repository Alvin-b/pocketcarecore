package com.example.wearable

import com.yc.nadalsdk.bean.FitnessData
import com.yc.nadalsdk.bean.FitnessHeartRateData
import com.yc.nadalsdk.bean.FitnessOxygenData
import com.yc.nadalsdk.bean.FitnessStepData
import com.yc.nadalsdk.bean.SciSleepData
import com.yc.nadalsdk.bean.SleepData
import org.junit.Assert.assertEquals
import org.junit.Test

class UteWatchMetricsMapperTest {
    @Test
    fun mapsSelectedMetricsAndExcludesNonSleepStages() {
        val fitness = FitnessData().apply {
            stepData = listOf(
                FitnessStepData().apply { calendar = "20261026"; step = 1_200 },
                FitnessStepData().apply { calendar = "20261026"; step = 800 },
                FitnessStepData().apply { calendar = "20261025"; step = 5_000 }
            )
            heartRateData = listOf(
                FitnessHeartRateData().apply { startTime = 10; rate = 71; calendarTime = "2026-10-26 08:00" },
                FitnessHeartRateData().apply { startTime = 20; rate = 77; calendarTime = "2026-10-26 09:00" }
            )
            oxygenData = listOf(
                FitnessOxygenData().apply { calendarTime = "2026-10-26 08:30"; oxygenValue = 97 },
                FitnessOxygenData().apply { calendarTime = "2026-10-26 09:15"; oxygenValue = 98 }
            )
        }
        val sleep = listOf(
            SleepData().apply { calendar = "20261025"; sleepType = SciSleepData.SCI_SLEEP_TYPE_DEEP; sleepTime = 90 },
            SleepData().apply { calendar = "20261025"; sleepType = SciSleepData.SCI_SLEEP_TYPE_LIGHT; sleepTime = 180 },
            SleepData().apply { calendar = "20261025"; sleepType = SciSleepData.SCI_SLEEP_TYPE_REM; sleepTime = 35 },
            SleepData().apply { calendar = "20261025"; sleepType = SciSleepData.SCI_SLEEP_TYPE_AWAKE; sleepTime = 20 },
            SleepData().apply { calendar = "20261024"; sleepType = SciSleepData.SCI_SLEEP_TYPE_DEEP; sleepTime = 360 }
        )

        val summary = UteWatchMetricsMapper.map(
            fitnessData = fitness,
            sleepData = sleep,
            today = "20261026",
            syncedAt = "Oct 26, 10:00 AM"
        )

        assertEquals(2_000, summary.stepsToday)
        assertEquals(77, summary.heartRateBpm)
        assertEquals("2026-10-26 09:00", summary.heartRateMeasuredAt)
        assertEquals(98, summary.oxygenPercent)
        assertEquals("2026-10-26 09:15", summary.oxygenMeasuredAt)
        assertEquals(305, summary.sleepMinutes)
        assertEquals("20261025", summary.sleepDate)
        assertEquals("Oct 26, 10:00 AM", summary.syncedAt)
    }

    @Test
    fun leavesUnavailableMetricsNullInsteadOfInventingReadings() {
        val summary = UteWatchMetricsMapper.map(
            fitnessData = FitnessData(),
            sleepData = emptyList(),
            today = "20261026",
            syncedAt = "Oct 26, 10:00 AM"
        )

        assertEquals(null, summary.stepsToday)
        assertEquals(null, summary.heartRateBpm)
        assertEquals(null, summary.oxygenPercent)
        assertEquals(null, summary.sleepMinutes)
    }
}
