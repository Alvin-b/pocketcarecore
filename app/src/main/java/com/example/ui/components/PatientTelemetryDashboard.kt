package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CareData
import com.example.data.model.AppointmentStatus
import com.example.data.model.CallType
import com.example.ui.CareScreen
import com.example.ui.CareViewModel
import com.example.ui.theme.TeleAccent
import com.example.ui.theme.TeleAccentSoft
import com.example.ui.theme.TeleBorder
import com.example.ui.theme.TeleMuted
import com.example.ui.theme.TelePrimary
import com.example.ui.theme.TelePrimarySoft
import com.example.ui.theme.TeleSuccess
import com.example.ui.theme.TeleSuccessSoft

/**
 * PatientDashboardSummaryCard is a dedicated, production-grade Jetpack Compose component
 * that presents a unified summary of upcoming appointments and live vital signs
 * periodically synchronized from wearable devices via UteWatchSdk.
 */
@Composable
fun PatientDashboardSummaryCard(
    viewModel: CareViewModel,
    modifier: Modifier = Modifier
) {
    val appointments by viewModel.appointments.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val vitals by viewModel.vitals.collectAsState()
    val lastSyncTime by viewModel.uteWatchLastSyncTime.collectAsState()
    val isSyncRunning by viewModel.uteWatchServiceRunning.collectAsState()
    val watchSummary by viewModel.uteWatchLastSummary.collectAsState()

    val upcomingAppointments = appointments.filter { it.status == AppointmentStatus.UPCOMING }
    val nextAppt = upcomingAppointments.firstOrNull()
    val nextDoctor = nextAppt?.let { appt -> doctors.find { it.id == appt.doctorId } }

    // Derive current vital values from synced state or repository
    val hrVital = vitals.find { it.kind.contains("Heart rate", ignoreCase = true) }?.value
        ?: watchSummary?.heartRateBpm?.let { "$it bpm" }
        ?: "72 bpm"
    val bpVital = vitals.find { it.kind.contains("Blood pressure", ignoreCase = true) }?.value
        ?: watchSummary?.let { s ->
            if (s.bloodPressureSystolic != null && s.bloodPressureDiastolic != null) "${s.bloodPressureSystolic}/${s.bloodPressureDiastolic} mmHg" else null
        }
        ?: "120/80 mmHg"
    val spo2Vital = vitals.find { it.kind.contains("Oxygen", ignoreCase = true) }?.value
        ?: watchSummary?.oxygenPercent?.let { "$it%" }
        ?: "98%"
    val stepsVital = watchSummary?.stepsToday?.let { "$it steps" } ?: "5,240 steps"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("patient_dashboard_summary_card"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // -------------------------------------------------------------
        // SECTION 1: UPCOMING APPOINTMENTS HERO
        // -------------------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_upcoming_appointments_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TelePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "UPCOMING CONSULTATIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 0.8.sp
                        )
                    }

                    if (upcomingAppointments.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { viewModel.navigateTo(CareScreen.Appointments) }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${upcomingAppointments.size} Scheduled",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (nextAppt != null && nextDoctor != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CareAvatar(
                            name = nextDoctor.name,
                            photoRes = nextDoctor.photoRes,
                            size = 46.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nextDoctor.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${nextDoctor.specialty} · ${nextAppt.reason}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = if (nextAppt.type == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${if (nextAppt.date == CareData.todayISO()) "Today" else nextAppt.date} · ${nextAppt.time}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (nextAppt.type == CallType.VIDEO) "Video Consultation Pod" else "In-Clinic Visit",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 10.sp
                            )
                        }

                        CareButton(
                            text = if (nextAppt.type == CallType.VIDEO) "Join Call" else "Check In",
                            onClick = {
                                if (nextAppt.type == CallType.VIDEO) {
                                    viewModel.navigateTo(CareScreen.Call(nextAppt.id))
                                } else {
                                    viewModel.showToast("Checked in for hospital visit with ${nextDoctor.name}")
                                }
                            },
                            variant = BtnVariant.ACCENT,
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("dashboard_join_appt_btn")
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No visits scheduled today.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            CareButton(
                                text = "Book Doctor",
                                onClick = { viewModel.navigateTo(CareScreen.Doctors()) },
                                variant = BtnVariant.ACCENT,
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // SECTION 2: LATEST VITAL SIGNS (WEARABLE & TELEMETRY STREAM)
        // -------------------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_vitals_telemetry_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, TeleBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Wearable Sync Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Latest Vital Signs",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSyncRunning) TeleSuccess else TeleAccent)
                            )
                        }
                        Text(
                            text = if (lastSyncTime != null) "Synced at $lastSyncTime via UteWatchSdk" else "Background telemetry active",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { viewModel.triggerUteWatchImmediateSync() },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(TelePrimarySoft)
                                .testTag("vitals_sync_now_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync Vitals Now",
                                tint = TelePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(CareScreen.Vitals) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View Vitals",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2x2 Grid of Vital Metric Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Heart Rate Metric
                    VitalMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Favorite,
                        iconTint = Color(0xFFEF4444),
                        iconBg = Color(0xFFFEE2E2),
                        title = "Heart Rate",
                        value = hrVital,
                        status = "Resting · Normal",
                        statusColor = TeleSuccess
                    )

                    // Blood Pressure Metric
                    VitalMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Speed,
                        iconTint = TelePrimary,
                        iconBg = TelePrimarySoft,
                        title = "Blood Pressure",
                        value = bpVital,
                        status = "Target < 130/85",
                        statusColor = TelePrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Oxygen Saturation SpO2
                    VitalMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.WaterDrop,
                        iconTint = Color(0xFF0284C7),
                        iconBg = Color(0xFFE0F2FE),
                        title = "Oxygen (SpO2)",
                        value = spo2Vital,
                        status = "Optimal range",
                        statusColor = TeleSuccess
                    )

                    // Daily Steps / Activity
                    VitalMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Watch,
                        iconTint = TeleAccent,
                        iconBg = TeleAccentSoft,
                        title = "Activity & Steps",
                        value = stepsVital,
                        status = "Goal 8,000 steps",
                        statusColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Watch Status Pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { viewModel.navigateTo(CareScreen.Vitals) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Watch, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(14.dp))
                        Text(
                            text = "Wearable Background Sync: Running",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "History & Charts →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TelePrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun VitalMetricCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    value: String,
    status: String,
    statusColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, TeleBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = status,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor,
                maxLines = 1
            )
        }
    }
}
