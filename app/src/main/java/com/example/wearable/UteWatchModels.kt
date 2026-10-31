package com.example.wearable

import com.yc.nadalsdk.bean.FitnessData
import com.yc.nadalsdk.bean.SciSleepData
import com.yc.nadalsdk.bean.SleepData

/** A scan result kept only in memory; the Bluetooth address is never displayed or logged. */
data class UteWatchDevice(
    val name: String,
    val address: String,
    val rssi: Int
)

enum class UteWatchConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

data class UteWatchSummary(
    val stepsToday: Int? = null,
    val heartRateBpm: Int? = null,
    val heartRateMeasuredAt: String? = null,
    val bloodPressureSystolic: Int? = null,
    val bloodPressureDiastolic: Int? = null,
    val bloodPressureMeasuredAt: String? = null,
    val oxygenPercent: Int? = null,
    val oxygenMeasuredAt: String? = null,
    val sleepMinutes: Int? = null,
    val sleepDate: String? = null,
    val syncedAt: String
)

data class UteWatchUiState(
    val connectionState: UteWatchConnectionState = UteWatchConnectionState.DISCONNECTED,
    val isScanning: Boolean = false,
    val isSyncing: Boolean = false,
    val devices: List<UteWatchDevice> = emptyList(),
    val connectedDeviceName: String? = null,
    val summary: UteWatchSummary? = null,
    val error: String? = null
)

/**
 * Reduces the SDK's minute-level history to a small, non-persistent summary for this prototype.
 * No raw samples are retained or uploaded. [today] must use the device-local yyyyMMdd format.
 */
object UteWatchMetricsMapper {
    fun map(
        fitnessData: FitnessData,
        sleepData: List<SleepData>,
        today: String,
        syncedAt: String
    ): UteWatchSummary {
        val todaySteps = fitnessData.stepData.orEmpty().filter { it.calendar == today }
        val latestHeartRate = fitnessData.heartRateData.orEmpty()
            .filter { it.rate > 0 || it.heartRateAvg > 0 }
            .maxByOrNull { it.startTime }
        val latestOxygen = fitnessData.oxygenData.orEmpty()
            .filter { it.oxygenValue > 0 }
            .maxByOrNull { it.calendarTime.orEmpty() }

        val latestBp = fitnessData.bpData.orEmpty()
            .filter { it.systolicPressure > 0 && it.diastolicPressure > 0 }
            .maxByOrNull { it.calendarTime.orEmpty() }

        val sleepByDate = sleepData
            .filter { it.sleepTime > 0 && it.sleepType in SLEEP_STAGES }
            .groupBy { it.calendar.orEmpty() }
            .filterKeys { it.isNotBlank() }
        val latestSleepDate = sleepByDate.keys.maxOrNull()
        val latestSleepMinutes = latestSleepDate?.let { date ->
            sleepByDate.getValue(date).sumOf { it.sleepTime }
        }

        return UteWatchSummary(
            stepsToday = todaySteps.takeIf { it.isNotEmpty() }?.sumOf { it.step },
            heartRateBpm = latestHeartRate?.let { it.rate.takeIf { rate -> rate > 0 } ?: it.heartRateAvg },
            heartRateMeasuredAt = latestHeartRate?.calendarTime,
            bloodPressureSystolic = latestBp?.systolicPressure,
            bloodPressureDiastolic = latestBp?.diastolicPressure,
            bloodPressureMeasuredAt = latestBp?.calendarTime,
            oxygenPercent = latestOxygen?.oxygenValue,
            oxygenMeasuredAt = latestOxygen?.calendarTime,
            sleepMinutes = latestSleepMinutes,
            sleepDate = latestSleepDate,
            syncedAt = syncedAt
        )
    }

    private val SLEEP_STAGES = setOf(
        SciSleepData.SCI_SLEEP_TYPE_DEEP,
        SciSleepData.SCI_SLEEP_TYPE_LIGHT,
        SciSleepData.SCI_SLEEP_TYPE_REM,
        SciSleepData.SCI_SLEEP_TYPE_SNOOZE
    )
}
