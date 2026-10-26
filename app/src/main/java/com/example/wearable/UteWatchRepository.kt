package com.example.wearable

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import androidx.core.content.ContextCompat
import com.yc.nadalsdk.bean.FitnessData
import com.yc.nadalsdk.bean.SleepData
import com.yc.nadalsdk.ble.open.UteBleClient
import com.yc.nadalsdk.ble.open.UteBleConnection
import com.yc.nadalsdk.listener.BleConnectStateListener
import com.yc.nadalsdk.log.LogUtils
import com.yc.nadalsdk.scan.UteScanCallback
import com.yc.nadalsdk.scan.UteScanDevice
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.ArrayDeque
import java.util.Date
import java.util.Locale

/** Local BLE-only integration. It deliberately does not use Ute's cloud/app-key features. */
class UteWatchRepository(context: Context) {
    private val appContext = context.applicationContext
    private val scanStartTimes = ArrayDeque<Long>()
    private var selectedAddress: String? = null

    private val _uiState = MutableStateFlow(UteWatchUiState())
    val uiState: StateFlow<UteWatchUiState> = _uiState.asStateFlow()

    private val connectionListener = object : BleConnectStateListener {
        override fun onConnecteStateChange(status: Int) {
            when (status) {
                BleConnectStateListener.STATE_CONNECTED -> {
                    _uiState.value = _uiState.value.copy(
                        connectionState = UteWatchConnectionState.CONNECTED,
                        isScanning = false,
                        connectedDeviceName = _uiState.value.connectedDeviceName ?: selectedAddress?.let { address ->
                            _uiState.value.devices.firstOrNull { it.address == address }?.name
                        },
                        error = null
                    )
                }
                BleConnectStateListener.STATE_CONNECTING -> {
                    _uiState.value = _uiState.value.copy(
                        connectionState = UteWatchConnectionState.CONNECTING,
                        isScanning = false
                    )
                }
                BleConnectStateListener.STATE_DISCONNECTED -> {
                    val wasConnecting = _uiState.value.connectionState == UteWatchConnectionState.CONNECTING
                    _uiState.value = _uiState.value.copy(
                        connectionState = UteWatchConnectionState.DISCONNECTED,
                        connectedDeviceName = null,
                        error = if (wasConnecting) "The watch disconnected before the connection completed." else _uiState.value.error
                    )
                }
            }
        }
    }

    private val clientDelegate = lazy {
        // The vendor demo enables persistent SDK logs; keep health/device details out of logs here.
        LogUtils.setLogEnable(false)
        LogUtils.setPrintEnable(false)
        UteBleClient.initialize(appContext).apply {
            // No authenticated backend/user ID exists in this prototype; avoid binding a demo identity.
            setSupportUserIdPair(false)
        }
    }
    private val client: UteBleClient by clientDelegate
    private val connectionDelegate = lazy {
        client.getUteBleConnection().apply { setConnectStateListener(connectionListener) }
    }
    private val connection: UteBleConnection by connectionDelegate

    fun isBluetoothEnabled(): Boolean = try {
        hasConnectPermission() && client.isBluetoothEnable()
    } catch (_: SecurityException) {
        false
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (!hasScanPermissions()) {
            setError(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                "Allow Nearby devices access to scan for your watch."
            } else {
                "Allow location access while using the app to scan for Bluetooth devices."
            })
            return
        }
        if (!hasConnectPermission()) {
            setError("Allow Nearby devices access before connecting to a watch.")
            return
        }
        if (!isBluetoothEnabled()) {
            setError("Turn on Bluetooth to find a compatible Ute watch.")
            return
        }
        if (_uiState.value.connectionState != UteWatchConnectionState.DISCONNECTED) {
            setError("Disconnect the current watch before scanning for another device.")
            return
        }
        if (!canStartAnotherScan()) {
            setError("Please wait a little before scanning again; the watch SDK limits repeated scans.")
            return
        }

        _uiState.value = _uiState.value.copy(isScanning = true, devices = emptyList(), error = null)
        try {
            val started = client.scanDevice(object : UteScanCallback {
                override fun onScanning(device: UteScanDevice) {
                    toNearbyWatch(device)?.let(::addOrUpdateDevice)
                }

                override fun onScanComplete(scanDeviceList: MutableList<UteScanDevice>) {
                    scanDeviceList.mapNotNull(::toNearbyWatch).forEach(::addOrUpdateDevice)
                    _uiState.value = _uiState.value.copy(isScanning = false)
                }

                override fun onScanFailed(errorCode: Int) {
                    _uiState.value = _uiState.value.copy(
                        isScanning = false,
                        error = "The watch scan failed (SDK code $errorCode). Check Bluetooth and try again."
                    )
                }
            }, SCAN_DURATION_MS)
            if (!started) {
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    error = "The watch SDK could not start scanning. Check Bluetooth and try again."
                )
            }
        } catch (_: SecurityException) {
            _uiState.value = _uiState.value.copy(isScanning = false, error = "Bluetooth permission is required to scan.")
        } catch (_: Exception) {
            _uiState.value = _uiState.value.copy(isScanning = false, error = "Could not start the watch scan. Please try again.")
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(device: UteWatchDevice) {
        if (!hasConnectPermission()) {
            setError("Allow Nearby devices access before connecting to a watch.")
            return
        }
        if (!isBluetoothEnabled()) {
            setError("Turn on Bluetooth before connecting to a watch.")
            return
        }
        try {
            client.cancelScan()
            selectedAddress = device.address
            _uiState.value = _uiState.value.copy(
                connectionState = UteWatchConnectionState.CONNECTING,
                isScanning = false,
                connectedDeviceName = device.name,
                error = null
            )
            connection.setConnectStateListener(connectionListener)
            client.connect(device.address)
        } catch (_: SecurityException) {
            _uiState.value = _uiState.value.copy(
                connectionState = UteWatchConnectionState.DISCONNECTED,
                connectedDeviceName = null,
                error = "Bluetooth permission is required to connect."
            )
        } catch (_: Exception) {
            _uiState.value = _uiState.value.copy(
                connectionState = UteWatchConnectionState.DISCONNECTED,
                connectedDeviceName = null,
                error = "Could not connect to that watch. Keep it nearby and try again."
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (clientDelegate.isInitialized()) {
            try {
                client.cancelScan()
                client.disconnect()
            } catch (_: SecurityException) {
                // Still clear local UI state if Bluetooth access has been revoked.
            } catch (_: Exception) {
                // Vendor disconnect failures must not retain a false connected state in the UI.
            }
        }
        selectedAddress = null
        _uiState.value = _uiState.value.copy(
            connectionState = UteWatchConnectionState.DISCONNECTED,
            isScanning = false,
            isSyncing = false,
            connectedDeviceName = null,
            error = null
        )
    }

    suspend fun syncLastSevenDays() {
        if (!hasConnectPermission() || !client.isConnected) {
            setError("Connect your watch before syncing readings.")
            return
        }
        if (_uiState.value.isSyncing) return
        _uiState.value = _uiState.value.copy(isSyncing = true, error = null)
        try {
            val summary = withContext(Dispatchers.IO) {
                val endTime = (System.currentTimeMillis() / 1_000L).toInt()
                val startTime = endTime - SEVEN_DAYS_SECONDS
                val fitnessResponse = connection.syncFitnessHistoryData(startTime, endTime)
                val sleepResponse = connection.syncSleepHistoryData(startTime, endTime)
                val fitnessData = fitnessResponse.data as? FitnessData
                    ?: throw SyncFailure(fitnessResponse.errorCode)
                val sleepData = (sleepResponse.data as? List<*>)
                    ?.mapNotNull { it as? SleepData }
                    ?: if (sleepResponse.isSuccess) emptyList() else throw SyncFailure(sleepResponse.errorCode)
                if (!fitnessResponse.isSuccess || !sleepResponse.isSuccess) {
                    throw SyncFailure(if (!fitnessResponse.isSuccess) fitnessResponse.errorCode else sleepResponse.errorCode)
                }
                val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
                val syncedAt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date())
                UteWatchMetricsMapper.map(fitnessData, sleepData, today, syncedAt)
            }
            _uiState.value = _uiState.value.copy(summary = summary, isSyncing = false)
        } catch (cancelled: CancellationException) {
            _uiState.value = _uiState.value.copy(isSyncing = false)
            throw cancelled
        } catch (failure: SyncFailure) {
            _uiState.value = _uiState.value.copy(
                isSyncing = false,
                error = "The watch could not return all requested readings (SDK code ${failure.code})."
            )
        } catch (_: SecurityException) {
            _uiState.value = _uiState.value.copy(isSyncing = false, error = "Bluetooth permission is required to sync readings.")
        } catch (_: Exception) {
            _uiState.value = _uiState.value.copy(isSyncing = false, error = "Could not sync readings from the watch. Please reconnect and try again.")
        }
    }

    fun clearMetrics() {
        _uiState.value = _uiState.value.copy(summary = null, error = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    @SuppressLint("MissingPermission")
    fun close() {
        if (clientDelegate.isInitialized()) disconnect()
    }

    private fun hasScanPermissions(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        hasPermission(Manifest.permission.BLUETOOTH_SCAN)
    } else {
        hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun hasConnectPermission(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
        hasPermission(Manifest.permission.BLUETOOTH_CONNECT)

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED

    private fun canStartAnotherScan(): Boolean {
        val now = SystemClock.elapsedRealtime()
        synchronized(scanStartTimes) {
            while (scanStartTimes.isNotEmpty() && now - scanStartTimes.first() >= SCAN_WINDOW_MS) {
                scanStartTimes.removeFirst()
            }
            if (scanStartTimes.size >= MAX_SCANS_PER_WINDOW) return false
            scanStartTimes.addLast(now)
            return true
        }
    }

    @SuppressLint("MissingPermission")
    private fun toNearbyWatch(scanDevice: UteScanDevice): UteWatchDevice? = try {
        val device = scanDevice.device ?: return null
        val address = device.address ?: return null
        if (address.isBlank()) return null
        UteWatchDevice(
            name = device.name?.takeIf { it.isNotBlank() } ?: "Ute-compatible wearable",
            address = address,
            rssi = scanDevice.rssi
        )
    } catch (_: SecurityException) {
        null
    }

    private fun addOrUpdateDevice(device: UteWatchDevice) {
        val devices = _uiState.value.devices.filterNot { it.address == device.address } + device
        _uiState.value = _uiState.value.copy(devices = devices.sortedByDescending { it.rssi })
    }

    private class SyncFailure(val code: Int) : Exception()

    private companion object {
        const val SCAN_DURATION_MS = 10_000L
        const val SCAN_WINDOW_MS = 30_000L
        const val MAX_SCANS_PER_WINDOW = 5
        const val SEVEN_DAYS_SECONDS = 7 * 24 * 60 * 60
    }
}
