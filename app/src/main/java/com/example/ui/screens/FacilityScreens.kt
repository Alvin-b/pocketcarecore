package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CareData
import com.example.data.model.*
import com.example.ui.CareScreen
import com.example.ui.CareViewModel
import com.example.ui.components.BtnVariant
import com.example.ui.components.CareAvatar
import com.example.ui.components.CareButton
import com.example.ui.components.CareCard
import com.example.ui.components.CareHeader
import com.example.ui.theme.*

enum class FacilityTab(val label: String, val icon: ImageVector) {
    OVERVIEW("Overview", Icons.Default.Apartment),
    DEPARTMENTS("Departments", Icons.Default.MedicalServices),
    REFERRALS("Referrals", Icons.Default.SwapHoriz),
    PROCEDURES("Labs & Tests", Icons.Default.Science),
    FOLLOW_UPS("Telehealth", Icons.Default.Videocam),
    WARDS("Wards & Beds", Icons.Default.LocalHospital),
    STAFF("Staff Roster", Icons.Default.Person),
    PHARMACY("Pharmacy", Icons.Default.LocalPharmacy)
}

@Composable
fun FacilityHomeScreen(viewModel: CareViewModel) {
    val facility by viewModel.facility.collectAsState()
    val staff by viewModel.facilityStaff.collectAsState()
    val departments by viewModel.facilityDepartments.collectAsState()
    val bookings by viewModel.facilityBookings.collectAsState()
    val referrals by viewModel.hospitalReferrals.collectAsState()
    val procedures by viewModel.hospitalProcedures.collectAsState()
    val followUps by viewModel.hospitalFollowUps.collectAsState()
    val pharmacyOrders by viewModel.pharmacyOrders.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(FacilityTab.OVERVIEW) }

    val onDutyCount = staff.count { it.isOnDuty }
    val pendingRxCount = pharmacyOrders.count { it.status != "Dispensed" }
    val pendingReferralsCount = referrals.count { it.status == "Pending Review" }
    val dueFollowUpsCount = followUps.count { it.status == "Due Today" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("facility_home_screen")
    ) {
        // Executive Facility Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TelePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(
                            text = facility.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TeleSuccessVibrant))
                            Text(
                                text = "24/7 Level 4 Center · Telehealth Active",
                                fontSize = 11.sp,
                                color = TeleSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Emergency Call Button
                CareButton(
                    text = "Hotline",
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${facility.phone}"))
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            viewModel.showToast("Cannot dial phone")
                        }
                    },
                    variant = BtnVariant.ACCENT,
                    modifier = Modifier.height(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Segmented Navigation Pill Strip
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FacilityTab.values()) { tab ->
                    val isSelected = tab == selectedTab
                    val badgeCount = when (tab) {
                        FacilityTab.REFERRALS -> pendingReferralsCount
                        FacilityTab.FOLLOW_UPS -> dueFollowUpsCount
                        FacilityTab.PHARMACY -> pendingRxCount
                        else -> 0
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) TelePrimary else TeleMuted)
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = tab.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            if (badgeCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White else TeleAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badgeCount.toString(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) TelePrimary else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Tab Module Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                FacilityTab.OVERVIEW -> {
                    FacilityOverviewContent(
                        viewModel = viewModel,
                        facility = facility,
                        staff = staff,
                        bookings = bookings,
                        followUps = followUps,
                        referrals = referrals,
                        onSwitchTab = { selectedTab = it }
                    )
                }
                FacilityTab.DEPARTMENTS -> {
                    FacilityDepartmentsScreen(viewModel = viewModel)
                }
                FacilityTab.REFERRALS -> {
                    FacilityReferralsScreen(viewModel = viewModel)
                }
                FacilityTab.PROCEDURES -> {
                    FacilityProceduresScreen(viewModel = viewModel)
                }
                FacilityTab.FOLLOW_UPS -> {
                    FacilityFollowUpsScreen(viewModel = viewModel)
                }
                FacilityTab.WARDS -> {
                    FacilityWardsScreen(viewModel = viewModel)
                }
                FacilityTab.STAFF -> {
                    FacilityStaffScreen(viewModel = viewModel)
                }
                FacilityTab.PHARMACY -> {
                    FacilityPharmacyScreen(viewModel = viewModel)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// OVERVIEW TAB: HOSPITAL TELEMEDICINE COMMAND CENTER
// -------------------------------------------------------------
@Composable
fun FacilityOverviewContent(
    viewModel: CareViewModel,
    facility: MedicalFacility,
    staff: List<FacilityStaffMember>,
    bookings: List<FacilityBooking>,
    followUps: List<HospitalFollowUp>,
    referrals: List<HospitalReferral>,
    onSwitchTab: (FacilityTab) -> Unit
) {
    val vitals by viewModel.vitals.collectAsState()
    val watchSummary by viewModel.uteWatchLastSummary.collectAsState()
    val lastSyncTime by viewModel.uteWatchLastSyncTime.collectAsState()
    val procedures by viewModel.hospitalProcedures.collectAsState()
    val departments by viewModel.facilityDepartments.collectAsState()

    var queueFilter by remember { mutableStateOf("ALL") } // "ALL", "TELE", "CLINIC", "EMERGENCY"
    var selectedBookingForAction by remember { mutableStateOf<FacilityBooking?>(null) }
    var actionType by remember { mutableStateOf<String?>(null) } // "REFER", "PROCEDURE"

    val onDutyCount = staff.count { it.isOnDuty }
    val dueFollowUpsCount = followUps.count { it.status == "Due Today" }
    val pendingReferralsCount = referrals.count { it.status == "Pending Review" }
    val activeTelePodsCount = bookings.count { it.visitType == FacilityVisitType.TELEMEDICINE_POD }
    val bedOccupancyPct = (facility.occupiedBeds.toFloat() / facility.totalBeds.toFloat()).coerceIn(0f, 1f)

    // Current RPM Telemetry for remote patient (Joy Kamau / Wearable)
    val patientHr = vitals.find { it.kind.contains("Heart rate", ignoreCase = true) }?.value
        ?: watchSummary?.heartRateBpm?.let { "$it bpm" } ?: "72 bpm"
    val patientBp = vitals.find { it.kind.contains("Blood pressure", ignoreCase = true) }?.value
        ?: watchSummary?.let { s ->
            if (s.bloodPressureSystolic != null && s.bloodPressureDiastolic != null) "${s.bloodPressureSystolic}/${s.bloodPressureDiastolic} mmHg" else null
        } ?: "120/80 mmHg"
    val patientSpo2 = vitals.find { it.kind.contains("Oxygen", ignoreCase = true) }?.value
        ?: watchSummary?.oxygenPercent?.let { "$it%" } ?: "98%"

    val filteredBookings = when (queueFilter) {
        "TELE" -> bookings.filter { it.visitType == FacilityVisitType.TELEMEDICINE_POD }
        "CLINIC" -> bookings.filter { it.visitType == FacilityVisitType.IN_PERSON_CLINIC }
        "EMERGENCY" -> bookings.filter { it.priority == TriagePriority.EMERGENCY || it.visitType == FacilityVisitType.EMERGENCY_TRIAGE }
        else -> bookings
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .testTag("facility_overview_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // 1. Live Command Center Operational Strip (4 KPI Cards)
        item {
            Text("Hospital Telemedicine Command Center", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Real-time clinical flow, bed capacity & remote patient telemetry", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Virtual Consultation Pods
                Card(
                    modifier = Modifier.weight(1f).clickable { queueFilter = "TELE" },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (queueFilter == "TELE") TelePrimary else TeleBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(18.dp))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TeleSuccess))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$activeTelePodsCount Active", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                        Text("Telehealth Pods", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Inpatient Bed Occupancy
                Card(
                    modifier = Modifier.weight(1f).clickable { onSwitchTab(FacilityTab.WARDS) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TeleBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(18.dp))
                            Text("${(bedOccupancyPct * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${facility.occupiedBeds}/${facility.totalBeds}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Beds Occupied", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Remote Patient Monitoring (RPM)
                Card(
                    modifier = Modifier.weight(1f).clickable { onSwitchTab(FacilityTab.FOLLOW_UPS) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TeleBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Watch, contentDescription = null, tint = TeleAccent, modifier = Modifier.size(18.dp))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TeleSuccess))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$dueFollowUpsCount Monitored", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TeleAccent)
                        Text("Wearable Telemetry", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Clinicians On Duty
                Card(
                    modifier = Modifier.weight(1f).clickable { onSwitchTab(FacilityTab.STAFF) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TeleBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(18.dp))
                            Text("Active", fontSize = 9.sp, color = TeleSuccess, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$onDutyCount/${staff.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Doctors On Duty", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // 2. Hospital Fast Clinical Action Bar
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CareButton(
                    text = "+ Intake Patient",
                    onClick = { viewModel.navigateTo(CareScreen.FacilityBookCare()) },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.weight(1f).height(36.dp)
                )
                CareButton(
                    text = "Referrals (${pendingReferralsCount})",
                    onClick = { onSwitchTab(FacilityTab.REFERRALS) },
                    variant = BtnVariant.OUTLINE,
                    modifier = Modifier.weight(1f).height(36.dp)
                )
                CareButton(
                    text = "Diagnostic Labs",
                    onClick = { onSwitchTab(FacilityTab.PROCEDURES) },
                    variant = BtnVariant.OUTLINE,
                    modifier = Modifier.weight(1f).height(36.dp)
                )
                CareButton(
                    text = "Pharmacy",
                    onClick = { onSwitchTab(FacilityTab.PHARMACY) },
                    variant = BtnVariant.OUTLINE,
                    modifier = Modifier.weight(1f).height(36.dp)
                )
            }
        }

        // 3. Remote Patient Monitoring (RPM) Live Telemetry Stream
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TelePrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(TelePrimarySoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Watch, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Remote Patient Telemetry (RPM)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TeleSuccess))
                                }
                                Text("Live vitals surveillance · Patient: Joy Kamau", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        CareButton(
                            text = "Tele-Review",
                            onClick = { onSwitchTab(FacilityTab.FOLLOW_UPS) },
                            variant = BtnVariant.ACCENT,
                            leadingIcon = { Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp)) },
                            modifier = Modifier.height(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Synced Vitals Grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Heart Rate
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, TeleBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Heart Rate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(patientHr, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Sinus Rhythm", fontSize = 9.sp, color = TeleSuccess, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Blood Pressure
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, TeleBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Blood Pressure", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(patientBp, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Controlled", fontSize = 9.sp, color = TelePrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Oxygen SpO2
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, TeleBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("SpO2", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(patientSpo2, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Optimal", fontSize = 9.sp, color = TeleSuccess, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Wearable background sync: UteWatch active (${lastSyncTime ?: "Live"})",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 4. Patient Intake & Telehealth Pods Filter Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Patient Intake & Consultation Pods", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${filteredBookings.size} patients", fontSize = 11.sp, color = TelePrimary, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips Strip
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "ALL" to "All Intake",
                    "TELE" to "Telehealth Pods",
                    "CLINIC" to "In-Clinic",
                    "EMERGENCY" to "Emergency STAT"
                ).forEach { (key, label) ->
                    val isSelected = queueFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) TelePrimary else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (isSelected) TelePrimary else TeleBorder, RoundedCornerShape(8.dp))
                            .clickable { queueFilter = key }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 5. Patient Intake Cards List
        if (filteredBookings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No patients in this queue filter.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(filteredBookings) { booking ->
                CareCard(
                    borderColor = if (booking.priority == TriagePriority.EMERGENCY) TeleDestructive.copy(alpha = 0.5f) else TeleBorder
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                CareAvatar(name = booking.patientName, size = 42.dp)
                                Column {
                                    Text(booking.patientName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(booking.departmentName, fontSize = 11.sp, color = TelePrimary, fontWeight = FontWeight.SemiBold)
                                        Text("·", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(booking.time, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            val priorityColor = when (booking.priority) {
                                TriagePriority.EMERGENCY -> TeleDestructive
                                TriagePriority.URGENT -> TeleAccent
                                TriagePriority.ROUTINE -> TeleSuccess
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(priorityColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = booking.priority.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = priorityColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Indication: ${booking.reason}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions Row: Telehealth Launch or Clinic Consultation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Attending: ${booking.assignedStaffName ?: "Triage Pool"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (booking.visitType == FacilityVisitType.TELEMEDICINE_POD) {
                                    CareButton(
                                        text = "Launch Pod",
                                        onClick = { viewModel.launchFacilityBookingCall(booking) },
                                        variant = BtnVariant.PRIMARY,
                                        leadingIcon = { Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp)) },
                                        modifier = Modifier.height(32.dp).testTag("launch_pod_${booking.id}")
                                    )
                                }

                                CareButton(
                                    text = "Lab Test",
                                    onClick = {
                                        selectedBookingForAction = booking
                                        actionType = "PROCEDURE"
                                    },
                                    variant = BtnVariant.OUTLINE,
                                    modifier = Modifier.height(32.dp)
                                )

                                CareButton(
                                    text = "Refer",
                                    onClick = {
                                        selectedBookingForAction = booking
                                        actionType = "REFER"
                                    },
                                    variant = BtnVariant.OUTLINE,
                                    modifier = Modifier.height(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Active Diagnostic Procedures & Results Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Diagnostic Laboratory & Imaging Orders", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("View All", fontSize = 11.sp, color = TelePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onSwitchTab(FacilityTab.PROCEDURES) })
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                procedures.take(3).forEach { proc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TeleBorder)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(proc.procedureName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${proc.patientName} · ${proc.category} · Ordered: ${proc.orderedDate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (!proc.resultSummary.isNullOrBlank()) {
                                    Text("Findings: ${proc.resultSummary}", fontSize = 11.sp, color = TeleSuccess, fontWeight = FontWeight.Medium)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (proc.status == "Results Completed") TeleSuccessSoft else TelePrimarySoft)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = proc.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (proc.status == "Results Completed") TeleSuccess else TelePrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    // Modal Action: Direct Referral from Overview
    if (actionType == "REFER" && selectedBookingForAction != null) {
        val booking = selectedBookingForAction!!
        var targetDept by remember { mutableStateOf(departments.firstOrNull()?.name ?: "Cardiology") }
        var clinicalReason by remember { mutableStateOf("Patient requires specialist cross-consultation") }

        AlertDialog(
            onDismissRequest = { actionType = null },
            title = { Text("Transfer / Refer ${booking.patientName}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Referring from ${booking.departmentName} to:", fontSize = 12.sp)
                    departments.forEach { dept ->
                        val isSelected = targetDept == dept.name
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) TelePrimarySoft else MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (isSelected) TelePrimary else TeleBorder, RoundedCornerShape(8.dp))
                                .clickable { targetDept = dept.name }
                                .padding(8.dp)
                        ) {
                            Text(dept.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                    OutlinedTextField(
                        value = clinicalReason,
                        onValueChange = { clinicalReason = it },
                        label = { Text("Clinical Indication", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                CareButton(
                    text = "Submit Referral",
                    onClick = {
                        viewModel.createHospitalReferral(
                            patientName = booking.patientName,
                            fromDept = booking.departmentName,
                            referringDoc = booking.assignedStaffName ?: "Attending Staff",
                            toDept = targetDept,
                            targetDoc = "On-Duty Specialist",
                            urgency = "Urgent",
                            clinicalReason = clinicalReason,
                            summaryNotes = "Cross-consultation initiated from Telehealth intake queue"
                        )
                        actionType = null
                        selectedBookingForAction = null
                    },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.height(34.dp)
                )
            },
            dismissButton = {
                CareButton(text = "Cancel", onClick = { actionType = null }, variant = BtnVariant.OUTLINE, modifier = Modifier.height(34.dp))
            }
        )
    }

    // Modal Action: Order Lab Diagnostic from Overview
    if (actionType == "PROCEDURE" && selectedBookingForAction != null) {
        val booking = selectedBookingForAction!!
        var procName by remember { mutableStateOf("Full Metabolic & Lipid Panel") }

        AlertDialog(
            onDismissRequest = { actionType = null },
            title = { Text("Order Diagnostic for ${booking.patientName}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Comprehensive Metabolic Panel" to "Diagnostic Laboratory",
                        "12-Lead Electrocardiogram (ECG)" to "Procedure Suite",
                        "High-Resolution Contrast CT" to "Radiology / Imaging",
                        "Cardiac Biomarkers (Troponin I)" to "Diagnostic Laboratory"
                    ).forEach { (name, category) ->
                        val isSelected = procName == name
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) TelePrimarySoft else MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (isSelected) TelePrimary else TeleBorder, RoundedCornerShape(8.dp))
                                .clickable { procName = name }
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                Text(category, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                CareButton(
                    text = "Order Test",
                    onClick = {
                        viewModel.orderHospitalProcedure(
                            patientName = booking.patientName,
                            orderingDoc = booking.assignedStaffName ?: "Attending Staff",
                            department = booking.departmentName,
                            procedureName = procName,
                            category = "Diagnostic Laboratory",
                            urgency = "STAT Urgent",
                            indication = "Clinical evaluation during triage intake"
                        )
                        actionType = null
                        selectedBookingForAction = null
                    },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.height(34.dp)
                )
            },
            dismissButton = {
                CareButton(text = "Cancel", onClick = { actionType = null }, variant = BtnVariant.OUTLINE, modifier = Modifier.height(34.dp))
            }
        )
    }
}

// -------------------------------------------------------------
// 1. HOSPITAL DEPARTMENTS STRUCTURE
// -------------------------------------------------------------
@Composable
fun FacilityDepartmentsScreen(viewModel: CareViewModel) {
    val departments by viewModel.facilityDepartments.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Hospital Department Structure", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                "Patients access care through the hospital's clinical structure with on-duty teams and virtual pods.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(departments) { dept ->
            CareCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(38.dp).clip(CircleShape).background(TelePrimarySoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (dept.iconKey) {
                                        "heart" -> Icons.Default.Favorite
                                        "baby" -> Icons.Default.BabyChangingStation
                                        "brain" -> Icons.Default.Psychology
                                        "emergency" -> Icons.Default.Warning
                                        else -> Icons.Default.MedicalServices
                                    },
                                    contentDescription = null,
                                    tint = TelePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(dept.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Lead Clinician: ${dept.headDoctorName}", fontSize = 11.sp, color = TelePrimary)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (dept.waitTimeMinutes == 0) TeleDestructiveSoft else TeleMuted)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                if (dept.waitTimeMinutes == 0) "Immediate Triage" else "~${dept.waitTimeMinutes}m wait",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dept.waitTimeMinutes == 0) TeleDestructive else TeleTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(dept.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${dept.activeStaffCount} Clinicians on duty", fontSize = 11.sp, color = TeleTextSecondary)

                        CareButton(
                            text = "Route Patient Intake",
                            onClick = { viewModel.navigateTo(CareScreen.FacilityBookCare(dept.id)) },
                            variant = BtnVariant.PRIMARY,
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

// -------------------------------------------------------------
// 2. DOCTOR-TO-DOCTOR REFERRALS
// -------------------------------------------------------------
@Composable
fun FacilityReferralsScreen(viewModel: CareViewModel) {
    val referrals by viewModel.hospitalReferrals.collectAsState()
    var showNewReferralDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Doctor-to-Doctor Internal Referrals", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Inter-departmental patient referrals within the hospital network", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                CareButton(
                    text = "+ Refer",
                    onClick = { showNewReferralDialog = true },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        items(referrals) { ref ->
            CareCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ref.patientName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (ref.urgency) {
                                        "STAT Emergency" -> TeleDestructiveSoft
                                        "Urgent" -> TeleAccentSoft
                                        else -> TelePrimarySoft
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                ref.urgency,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (ref.urgency) {
                                    "STAT Emergency" -> TeleDestructive
                                    "Urgent" -> TeleAccent
                                    else -> TelePrimary
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("From: ${ref.referringClinician} (${ref.fromDepartment})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("To: ${ref.targetClinician} · ${ref.toDepartment}", fontSize = 12.sp, color = TelePrimary, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Indication: ${ref.clinicalReason}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("Clinical Notes: ${ref.summaryNotes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status: ${ref.status}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (ref.status == "Pending Review") TeleAccent else TeleSuccess)

                        if (ref.status == "Pending Review") {
                            CareButton(
                                text = "Accept Referral",
                                onClick = { viewModel.updateReferralStatus(ref.id, "Accepted") },
                                variant = BtnVariant.PRIMARY,
                                modifier = Modifier.height(30.dp)
                            )
                        } else if (ref.status == "Accepted") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CareButton(
                                    text = "Video Consult",
                                    onClick = { viewModel.launchReferralConsultationCall(ref) },
                                    variant = BtnVariant.PRIMARY,
                                    modifier = Modifier.height(30.dp)
                                )
                                CareButton(
                                    text = "Mark Done",
                                    onClick = { viewModel.updateReferralStatus(ref.id, "Completed") },
                                    variant = BtnVariant.OUTLINE,
                                    modifier = Modifier.height(30.dp)
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(14.dp))
                                Text("Completed", fontSize = 11.sp, color = TeleSuccess, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    if (showNewReferralDialog) {
        var pName by remember { mutableStateOf("Joy Kamau") }
        var fromDept by remember { mutableStateOf("Outpatient & Telehealth (OPD)") }
        var referringDoc by remember { mutableStateOf("Dr. Amina Wanjiru") }
        var toDept by remember { mutableStateOf("Cardiology & Diagnostics") }
        var targetDoc by remember { mutableStateOf("Dr. Rajesh Patel") }
        var urgency by remember { mutableStateOf("Urgent") }
        var reason by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewReferralDialog = false },
            title = { Text("Inter-Department Doctor Referral") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("Patient Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = targetDoc,
                        onValueChange = { targetDoc = it },
                        label = { Text("Target Specialist") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = toDept,
                        onValueChange = { toDept = it },
                        label = { Text("Target Department") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Clinical Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Clinical Summary & Vitals") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                CareButton(
                    text = "Submit Referral",
                    onClick = {
                        if (pName.isNotBlank() && reason.isNotBlank()) {
                            viewModel.createHospitalReferral(
                                patientName = pName.trim(),
                                fromDept = fromDept,
                                referringDoc = referringDoc,
                                toDept = toDept,
                                targetDoc = targetDoc,
                                urgency = urgency,
                                clinicalReason = reason.trim(),
                                summaryNotes = notes.ifBlank { "Specialist assessment requested via hospital system." }
                            )
                            showNewReferralDialog = false
                        }
                    },
                    variant = BtnVariant.PRIMARY
                )
            },
            dismissButton = {
                TextButton(onClick = { showNewReferralDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------
// 3. DIAGNOSTICS & PROCEDURES
// -------------------------------------------------------------
@Composable
fun FacilityProceduresScreen(viewModel: CareViewModel) {
    val procedures by viewModel.hospitalProcedures.collectAsState()
    var showOrderDialog by remember { mutableStateOf(false) }
    var resultDialogOrder by remember { mutableStateOf<HospitalProcedureOrder?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Diagnostic Procedures & Lab Orders", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Real-time orders for clinical pathology, radiology & procedure suites", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                CareButton(
                    text = "+ Order",
                    onClick = { showOrderDialog = true },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        items(procedures) { proc ->
            CareCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(proc.procedureName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (proc.status == "Results Completed") TeleSuccessSoft else TelePrimarySoft)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                proc.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (proc.status == "Results Completed") TeleSuccess else TelePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Patient: ${proc.patientName} · Ordered by ${proc.orderingClinician}", fontSize = 12.sp, color = TeleTextSecondary)
                    Text("Unit: ${proc.department} (${proc.category})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Indication: ${proc.clinicalIndication}", fontSize = 12.sp, fontWeight = FontWeight.Medium)

                    if (!proc.resultSummary.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(TeleSuccessSoft.copy(alpha = 0.5f))
                                .border(1.dp, TeleSuccess.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("DIAGNOSTIC REPORT:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleSuccess)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(proc.resultSummary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    if (proc.status != "Results Completed") {
                        CareButton(
                            text = "Record Findings & Upload Results",
                            onClick = { resultDialogOrder = proc },
                            variant = BtnVariant.PRIMARY,
                            modifier = Modifier.fillMaxWidth().height(32.dp)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(14.dp))
                            Text("Synced with digital health records", fontSize = 11.sp, color = TeleSuccess, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    if (showOrderDialog) {
        var pName by remember { mutableStateOf("Joy Kamau") }
        var docName by remember { mutableStateOf("Dr. Amina Wanjiru") }
        var deptName by remember { mutableStateOf("Cardiology & Diagnostics") }
        var testName by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Diagnostic Laboratory") }
        var urgency by remember { mutableStateOf("STAT Urgent") }
        var indication by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showOrderDialog = false },
            title = { Text("Order Diagnostic Test / Procedure") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("Patient Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = testName,
                        onValueChange = { testName = it },
                        label = { Text("Procedure / Test Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = deptName,
                        onValueChange = { deptName = it },
                        label = { Text("Department / Lab") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = indication,
                        onValueChange = { indication = it },
                        label = { Text("Clinical Indication") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                CareButton(
                    text = "Confirm Order",
                    onClick = {
                        if (pName.isNotBlank() && testName.isNotBlank()) {
                            viewModel.orderHospitalProcedure(
                                patientName = pName.trim(),
                                orderingDoc = docName,
                                department = deptName,
                                procedureName = testName.trim(),
                                category = category,
                                urgency = urgency,
                                indication = indication.ifBlank { "Diagnostic evaluation" }
                            )
                            showOrderDialog = false
                        }
                    },
                    variant = BtnVariant.PRIMARY
                )
            },
            dismissButton = {
                TextButton(onClick = { showOrderDialog = false }) { Text("Cancel") }
            }
        )
    }

    resultDialogOrder?.let { targetOrder ->
        var resultText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { resultDialogOrder = null },
            title = { Text("Upload Report: ${targetOrder.procedureName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Patient: ${targetOrder.patientName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = resultText,
                        onValueChange = { resultText = it },
                        label = { Text("Analyzer Findings & Report") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                CareButton(
                    text = "Publish to EMR",
                    onClick = {
                        if (resultText.isNotBlank()) {
                            viewModel.completeHospitalProcedure(targetOrder.id, resultText.trim())
                            resultDialogOrder = null
                        }
                    },
                    variant = BtnVariant.PRIMARY
                )
            },
            dismissButton = {
                TextButton(onClick = { resultDialogOrder = null }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------
// 4. TELEHEALTH REMOTE FOLLOW-UP HUB
// -------------------------------------------------------------
@Composable
fun FacilityFollowUpsScreen(viewModel: CareViewModel) {
    val followUps by viewModel.hospitalFollowUps.collectAsState()
    var showScheduleDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Telehealth Remote Follow-Up Hub", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Post-discharge remote check-ins, surgical recovery & chronic vitals telemetry", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                CareButton(
                    text = "+ Follow-Up",
                    onClick = { showScheduleDialog = true },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        items(followUps) { fu ->
            CareCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(fu.patientName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (fu.status) {
                                        "Due Today" -> TeleAccentSoft
                                        "Completed" -> TeleSuccessSoft
                                        else -> TelePrimarySoft
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                fu.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (fu.status) {
                                    "Due Today" -> TeleAccent
                                    "Completed" -> TeleSuccess
                                    else -> TelePrimary
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Condition: ${fu.dischargeDiagnosis}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TelePrimary)
                    Text("Physician: ${fu.attendingDoctor} · Due: ${fu.followUpDate}", fontSize = 11.sp, color = TeleTextSecondary)
                    Text("Protocol: ${fu.clinicalProtocol}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (fu.status != "Completed") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CareButton(
                                    text = "Start Video Call",
                                    onClick = { viewModel.launchFollowUpTelehealthCall(fu) },
                                    variant = BtnVariant.PRIMARY,
                                    modifier = Modifier.height(32.dp)
                                )
                                CareButton(
                                    text = "Complete",
                                    onClick = { viewModel.completeHospitalFollowUp(fu.id) },
                                    variant = BtnVariant.OUTLINE,
                                    modifier = Modifier.height(32.dp)
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(16.dp))
                                Text("Protocol Completed", fontSize = 12.sp, color = TeleSuccess, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    if (showScheduleDialog) {
        var pName by remember { mutableStateOf("Joy Kamau") }
        var doctorName by remember { mutableStateOf("Dr. Amina Wanjiru") }
        var diagnosis by remember { mutableStateOf("") }
        var dueDate by remember { mutableStateOf(CareData.todayISO()) }
        var protocol by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            title = { Text("Schedule Telehealth Follow-up") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("Patient Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = doctorName,
                        onValueChange = { doctorName = it },
                        label = { Text("Attending Physician") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = diagnosis,
                        onValueChange = { diagnosis = it },
                        label = { Text("Diagnosis / Procedure") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = protocol,
                        onValueChange = { protocol = it },
                        label = { Text("Follow-up Protocol") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                CareButton(
                    text = "Confirm Schedule",
                    onClick = {
                        if (pName.isNotBlank() && diagnosis.isNotBlank()) {
                            viewModel.scheduleHospitalFollowUp(
                                patientName = pName.trim(),
                                doctor = doctorName,
                                diagnosis = diagnosis.trim(),
                                dueDate = dueDate,
                                followUpType = "Telehealth Video Consultation",
                                protocol = protocol.ifBlank { "Remote HD video inspection & vitals assessment." }
                            )
                            showScheduleDialog = false
                        }
                    },
                    variant = BtnVariant.PRIMARY
                )
            },
            dismissButton = {
                TextButton(onClick = { showScheduleDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------
// 5. WARDS & BED CAPACITY
// -------------------------------------------------------------
@Composable
fun FacilityWardsScreen(viewModel: CareViewModel) {
    val beds by viewModel.hospitalWardBeds.collectAsState()
    val facility by viewModel.facility.collectAsState()
    var admitBedTarget by remember { mutableStateOf<HospitalWardBed?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TelePrimarySoft.copy(alpha = 0.35f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, TelePrimarySoft)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Licensed Bed Occupancy", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${facility.occupiedBeds} / ${facility.totalBeds} Occupied", fontWeight = FontWeight.Bold, color = TelePrimary, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Ward telemetry across ICU, Medical, Surgical, Pediatric & Telehealth Observation Pods.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        items(beds) { bed ->
            CareCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (bed.isOccupied) TeleAccentSoft else TeleSuccessSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = if (bed.isOccupied) TeleAccent else TeleSuccess, modifier = Modifier.size(20.dp))
                        }

                        Column {
                            Text("${bed.bedCode} · ${bed.wardName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (bed.isOccupied) {
                                Text("Patient: ${bed.patientName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                                Text("Attending: ${bed.attendingClinician ?: "Doctor"} · Adm: ${bed.admittedDate}", fontSize = 11.sp, color = TeleTextSecondary)
                            } else {
                                Text("Available for admission", fontSize = 11.sp, color = TeleSuccess, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    if (bed.isOccupied) {
                        CareButton(
                            text = "Discharge",
                            onClick = { viewModel.dischargePatientFromBed(bed.id) },
                            variant = BtnVariant.OUTLINE,
                            modifier = Modifier.height(30.dp)
                        )
                    } else {
                        CareButton(
                            text = "Admit",
                            onClick = { admitBedTarget = bed },
                            variant = BtnVariant.PRIMARY,
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    admitBedTarget?.let { targetBed ->
        var pName by remember { mutableStateOf("") }
        var clinician by remember { mutableStateOf("Dr. Amina Wanjiru") }

        AlertDialog(
            onDismissRequest = { admitBedTarget = null },
            title = { Text("Admit to Bed ${targetBed.bedCode}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ward: ${targetBed.wardName}", fontSize = 12.sp, color = TelePrimary, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("Patient Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = clinician,
                        onValueChange = { clinician = it },
                        label = { Text("Attending Physician") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                CareButton(
                    text = "Confirm Admission",
                    onClick = {
                        if (pName.isNotBlank()) {
                            viewModel.admitPatientToBed(targetBed.id, pName.trim(), clinician)
                            admitBedTarget = null
                        }
                    },
                    variant = BtnVariant.PRIMARY
                )
            },
            dismissButton = {
                TextButton(onClick = { admitBedTarget = null }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------
// 6. CLINICIAN STAFF ROSTER
// -------------------------------------------------------------
@Composable
fun FacilityStaffScreen(viewModel: CareViewModel) {
    val staff by viewModel.facilityStaff.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hospital Staff & Duty Roster", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Verified clinicians, active duty shifts & caseloads", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                CareButton(
                    text = "+ Add",
                    onClick = { showAddDialog = true },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        items(staff) { member ->
            CareCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        CareAvatar(name = member.name, size = 44.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(member.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Icon(Icons.Default.Verified, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(14.dp))
                            }
                            Text(member.role, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${member.department} · ${member.license}", fontSize = 11.sp, color = TeleTextSecondary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (member.isOnDuty) TeleSuccessSoft else TeleMuted)
                            .clickable { viewModel.toggleStaffDuty(member.id) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (member.isOnDuty) "On Duty" else "Off Duty",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (member.isOnDuty) TeleSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newRole by remember { mutableStateOf("Consultant Physician") }
        var newLicense by remember { mutableStateOf("KMPDC-") }
        var newDept by remember { mutableStateOf("General Medicine") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Register Clinician") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRole,
                        onValueChange = { newRole = it },
                        label = { Text("Role") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newLicense,
                        onValueChange = { newLicense = it },
                        label = { Text("License Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDept,
                        onValueChange = { newDept = it },
                        label = { Text("Department") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                CareButton(
                    text = "Add Clinician",
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.showToast("$newName added to roster")
                            showAddDialog = false
                        }
                    },
                    variant = BtnVariant.PRIMARY
                )
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------
// 7. PHARMACY QUEUE
// -------------------------------------------------------------
@Composable
fun FacilityPharmacyScreen(viewModel: CareViewModel) {
    val orders by viewModel.pharmacyOrders.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Hospital Central Pharmacy Queue", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Dispense digital e-prescriptions with automatic allergy verification", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(orders) { order ->
            CareCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(order.patientName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (order.status == "Dispensed") TeleSuccessSoft else TeleAccentSoft)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                order.status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (order.status == "Dispensed") TeleSuccess else TeleAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Prescribed by: ${order.doctorName}", fontSize = 12.sp, color = TelePrimary, fontWeight = FontWeight.SemiBold)
                    Text("Medications: ${order.medicationSummary}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("Instructions: ${order.dosageNotes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (order.status != "Dispensed") {
                        CareButton(
                            text = "Mark as Dispensed",
                            onClick = { viewModel.dispensePharmacyOrder(order.id) },
                            variant = BtnVariant.PRIMARY,
                            modifier = Modifier.fillMaxWidth().height(34.dp)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(16.dp))
                            Text("Dispensed & logged into digital health record", fontSize = 12.sp, color = TeleSuccess, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

// -------------------------------------------------------------
// 8. BOOK HOSPITAL CARE (PATIENT & INTAKE FACILITY FLOW)
// -------------------------------------------------------------
@Composable
fun FacilityBookCareScreen(viewModel: CareViewModel, preselectedDeptId: String? = null) {
    val departments by viewModel.facilityDepartments.collectAsState()
    val patientName by viewModel.patientName.collectAsState()

    var selectedDeptId by remember { mutableStateOf(preselectedDeptId ?: departments.firstOrNull()?.id ?: "dept-opd") }
    var selectedVisitType by remember { mutableStateOf(FacilityVisitType.TELEMEDICINE_POD) }
    var priority by remember { mutableStateOf(TriagePriority.ROUTINE) }
    var reason by remember { mutableStateOf("") }
    var customPatientName by remember { mutableStateOf(patientName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Hospital Care Booking", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Book with Hospital Structure", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Access specialized clinical divisions attended by our active hospital clinicians.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Step 1: Select Department
            item {
                Text("1. Select Clinical Department", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    departments.forEach { dept ->
                        val isSelected = dept.id == selectedDeptId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedDeptId = dept.id }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) TelePrimary else TeleBorder,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) TelePrimarySoft.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(dept.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(dept.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Step 2: Mode (Telehealth Pod vs In-Person)
            item {
                Text("2. Consultation Delivery Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedVisitType = FacilityVisitType.TELEMEDICINE_POD }
                            .border(
                                width = if (selectedVisitType == FacilityVisitType.TELEMEDICINE_POD) 2.dp else 1.dp,
                                color = if (selectedVisitType == FacilityVisitType.TELEMEDICINE_POD) TelePrimary else TeleBorder,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedVisitType == FacilityVisitType.TELEMEDICINE_POD) TelePrimarySoft.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Telehealth Pod", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Virtual HD Video", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedVisitType = FacilityVisitType.IN_PERSON_CLINIC }
                            .border(
                                width = if (selectedVisitType == FacilityVisitType.IN_PERSON_CLINIC) 2.dp else 1.dp,
                                color = if (selectedVisitType == FacilityVisitType.IN_PERSON_CLINIC) TelePrimary else TeleBorder,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedVisitType == FacilityVisitType.IN_PERSON_CLINIC) TelePrimarySoft.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("In-Person Visit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Hospital Center", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Step 3: Chief Complaint
            item {
                Text("3. Patient Details & Chief Complaint", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = customPatientName,
                        onValueChange = { customPatientName = it },
                        label = { Text("Patient Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Symptoms / Reason for Consultation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Confirm Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                CareButton(
                    text = if (selectedVisitType == FacilityVisitType.TELEMEDICINE_POD) "Confirm & Setup Telehealth Pod" else "Confirm Hospital Appointment",
                    onClick = {
                        if (reason.isNotBlank()) {
                            val booking = viewModel.bookFacilityCare(
                                patientName = customPatientName,
                                departmentId = selectedDeptId,
                                visitType = selectedVisitType,
                                priority = priority,
                                reason = reason.trim()
                            )
                            if (selectedVisitType == FacilityVisitType.TELEMEDICINE_POD && booking.consultationRoomId != null) {
                                viewModel.navigateTo(CareScreen.Call(booking.consultationRoomId))
                            } else {
                                viewModel.navigateBack()
                            }
                        } else {
                            viewModel.showToast("Please enter reason for visit")
                        }
                    },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// 9. DIAGNOSTIC LAB PACKAGES (LEGACY SCREEN KEEP ALIVE)
// -------------------------------------------------------------
@Composable
fun FacilityLabsScreen(viewModel: CareViewModel) {
    FacilityProceduresScreen(viewModel = viewModel)
}

// -------------------------------------------------------------
// 10. FACILITY SETTINGS & ADMIN
// -------------------------------------------------------------
@Composable
fun FacilitySettingsScreen(viewModel: CareViewModel) {
    val facility by viewModel.facility.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Facility Administration", onBack = { viewModel.navigateTo(CareScreen.FacilityHome) })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CareCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Apartment, contentDescription = null, tint = TelePrimary)
                            Text(facility.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Accreditation: ${facility.accreditation}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("License Number: ${facility.license}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = TelePrimary)
                        Text("Address: ${facility.address}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Hotline: ${facility.phone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                Text("Role & Account", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            item {
                CareCard(onClick = { viewModel.setRole(null) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Switch Portal / Role", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Switch to Patient or Healthcare Personnel", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                CareCard(onClick = { viewModel.resetDemoData() }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = TeleDestructive, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Reset Demo Database", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TeleDestructive)
                            Text("Restore initial consultations, beds, referrals, and procedures", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
