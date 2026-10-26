package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.CareViewModel
import com.example.ui.components.BtnVariant
import com.example.ui.components.CareButton
import com.example.ui.components.CareCard
import com.example.wearable.UteWatchConnectionState
import com.example.wearable.UteWatchDevice
import com.example.wearable.UteWatchSummary
import com.example.wearable.UteWatchUiState

@Composable
fun UteWatchCard(viewModel: CareViewModel) {
    val context = LocalContext.current
    val state by viewModel.uteWatchUiState.collectAsState()

    val bluetoothEnableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.clearUteWatchError()
            viewModel.startUteWatchScan()
        } else {
            viewModel.setUteWatchError("Bluetooth must be enabled before searching for a watch.")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val permissionsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            results[Manifest.permission.BLUETOOTH_SCAN] == true &&
                results[Manifest.permission.BLUETOOTH_CONNECT] == true
        } else {
            results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
        if (permissionsGranted) {
            if (viewModel.isUteBluetoothEnabled()) {
                viewModel.clearUteWatchError()
                viewModel.startUteWatchScan()
            } else {
                bluetoothEnableLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
        } else {
            viewModel.setUteWatchError("Bluetooth access is needed to find and connect to your watch.")
        }
    }

    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val onFindWatch = {
        viewModel.clearUteWatchError()
        val missingPermissions = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        } else if (viewModel.isUteBluetoothEnabled()) {
            viewModel.startUteWatchScan()
        } else {
            bluetoothEnableLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    CareCard(borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Watch, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Connect a Ute-compatible watch", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Sync steps, heart rate, sleep, and SpO₂ from a supported watch. Only summary values are held in app memory in this prototype; nothing is uploaded to a backend.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Wearable readings are estimates and are not for diagnosis or treatment decisions.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))
        when (state.connectionState) {
            UteWatchConnectionState.DISCONNECTED -> Text(
                "Not connected",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            UteWatchConnectionState.CONNECTING -> Text(
                "Connecting to ${state.connectedDeviceName ?: "watch"}…",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
            UteWatchConnectionState.CONNECTED -> Text(
                "Connected to ${state.connectedDeviceName ?: "watch"}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (state.connectionState == UteWatchConnectionState.DISCONNECTED) {
            Spacer(modifier = Modifier.height(8.dp))
            CareButton(
                text = if (state.isScanning) "Searching nearby…" else "Find watch",
                onClick = onFindWatch,
                enabled = !state.isScanning,
                variant = BtnVariant.SOFT,
                modifier = Modifier.fillMaxWidth().testTag("ute_watch_scan_btn")
            )
            if (state.isScanning) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Keep the watch nearby and allow its pairing request if prompted.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            state.devices.forEach { device ->
                WatchDeviceRow(device = device, onConnect = { viewModel.connectToUteWatch(device) })
            }
        }

        if (state.connectionState == UteWatchConnectionState.CONNECTED) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                CareButton(
                    text = if (state.isSyncing) "Syncing…" else "Sync last 7 days",
                    onClick = { viewModel.syncUteWatchMetrics() },
                    enabled = !state.isSyncing,
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.weight(1f).testTag("ute_watch_sync_btn")
                )
                CareButton(
                    text = "Disconnect",
                    onClick = { viewModel.disconnectUteWatch() },
                    enabled = !state.isSyncing,
                    variant = BtnVariant.SOFT,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        state.error?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
        }

        state.summary?.let { summary ->
            Spacer(modifier = Modifier.height(12.dp))
            Text("Watch summary", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                SummaryMetric("Steps today", summary.stepsToday?.toString() ?: "—", Modifier.weight(1f))
                SummaryMetric("Heart rate", summary.heartRateBpm?.let { "$it bpm" } ?: "—", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                SummaryMetric("SpO₂", summary.oxygenPercent?.let { "$it%" } ?: "—", Modifier.weight(1f))
                SummaryMetric("Latest sleep", formatSleep(summary.sleepMinutes), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Last synced ${summary.syncedAt}. Readings are temporary and will be cleared when the app process is restarted or demo data is reset.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WatchDeviceRow(device: UteWatchDevice, onConnect: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Bluetooth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(device.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("Signal ${device.rssi} dBm", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        CareButton(
            text = "Connect",
            onClick = onConnect,
            variant = BtnVariant.SOFT,
            modifier = Modifier.width(105.dp).testTag("ute_watch_connect_btn")
        )
    }
}

@Composable
private fun SummaryMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

private fun formatSleep(minutes: Int?): String {
    if (minutes == null) return "—"
    val hours = minutes / 60
    val remainder = minutes % 60
    return if (hours > 0) "$hours hr $remainder min" else "$remainder min"
}
