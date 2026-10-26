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
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CareData
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.model.CallType
import com.example.data.model.Doctor
import com.example.data.model.Medication
import com.example.data.model.Prescription
import com.example.data.model.Specialty
import com.example.ui.CareScreen
import com.example.ui.CareViewModel
import com.example.ui.components.BtnVariant
import com.example.ui.components.CareAvatar
import com.example.ui.components.CareButton
import com.example.ui.components.CareCard
import com.example.ui.components.CareField
import com.example.ui.components.CareHeader
import com.example.ui.components.CareStars
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WelcomeScreen(viewModel: CareViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TelePrimary, Color(0xFF155E75), Color(0xFF0F172A))
                )
            )
            .padding(24.dp)
            .testTag("welcome_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(top = 48.dp)) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Care that\ncomes to you.",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 44.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "See certified doctors by video or voice, get prescriptions on your phone and never miss a dose.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 22.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CareButton(
                    text = "I need a doctor",
                    onClick = {
                        viewModel.setRole("patient")
                        viewModel.showToast("Opening patient dashboard")
                    },
                    variant = BtnVariant.ACCENT,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("welcome_patient_btn")
                )

                CareButton(
                    text = "Doctor sign in / registration",
                    onClick = {
                        viewModel.setRole(null)
                        viewModel.navigateTo(CareScreen.DocRegister)
                        viewModel.showToast("Opening clinician setup")
                    },
                    variant = BtnVariant.OUTLINE,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("welcome_doctor_btn")
                )
            }
        }
    }
}

@Composable
fun PatientHomeScreen(viewModel: CareViewModel) {
    val patientName by viewModel.patientName.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val prescriptions by viewModel.prescriptions.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var showSosDialog by remember { mutableStateOf(false) }

    val nextAppt = appointments.firstOrNull { it.status == AppointmentStatus.UPCOMING }
    val nextDoctor = nextAppt?.let { a -> doctors.find { it.id == a.doctorId } }
    val matchingDoctors = searchQuery.trim().takeIf { it.isNotEmpty() }?.let { query ->
        val terms = query.split(Regex("\\s+")).filter { it.isNotBlank() }
        doctors.filter { doctor ->
            val searchable = "${doctor.name} ${doctor.specialty} ${doctor.bio}".lowercase(Locale.ROOT)
            terms.all { term -> searchable.contains(term.lowercase(Locale.ROOT)) }
        }
    }.orEmpty()

    val allDoses = prescriptions.flatMap { p ->
        p.meds.flatMap { m -> m.times.map { t -> Pair(m, t) } }
    }.sortedBy { it.second }
    val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    val upcomingDose = allDoses.firstOrNull { it.second >= nowTime } ?: allDoses.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
            .testTag("patient_home_screen"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good day \uD83D\uDC4B",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = patientName.split(" ").firstOrNull() ?: patientName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { viewModel.navigateTo(CareScreen.Meds) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, TeleBorder.copy(alpha = 0.5f), CircleShape)
                        .testTag("home_bell_btn")
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Reminders",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TeleAccent)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search doctor, symptom, specialty", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (searchQuery.isNotBlank()) {
            item {
                Text(
                    text = if (matchingDoctors.isEmpty()) "No matching doctors found" else "Matching doctors",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(matchingDoctors, key = { "home-search-${it.id}" }) { doctor ->
                DoctorRowCard(
                    doctor = doctor,
                    onClick = { viewModel.navigateTo(CareScreen.DoctorProfile(doctor.id)) }
                )
            }
        }

        // 4 Action Tiles
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CareCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_triage"),
                        backgroundColor = TelePrimarySoft,
                        borderColor = TelePrimary.copy(alpha = 0.2f),
                        onClick = { viewModel.navigateTo(CareScreen.Triage) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Symptom checker", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                        }
                    }

                    CareCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_vitals"),
                        onClick = { viewModel.navigateTo(CareScreen.Vitals) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Watch, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Track vitals", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CareCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_labs"),
                        onClick = { viewModel.navigateTo(CareScreen.Labs) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tests & results", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    CareCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_wallet"),
                        onClick = { viewModel.navigateTo(CareScreen.Wallet) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Wallet, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Health wallet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Emergency SOS Button
        item {
            CareButton(
                text = "Emergency SOS",
                onClick = { showSosDialog = true },
                variant = BtnVariant.PRIMARY,
                leadingIcon = {
                    Icon(Icons.Default.Sos, contentDescription = null, tint = Color.White)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("emergency_sos_btn")
            )
        }

        // Next Appointment Card
        if (nextAppt != null && nextDoctor != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_next_appt_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = TelePrimary)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "NEXT APPOINTMENT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CareAvatar(name = nextDoctor.name, photoRes = nextDoctor.photoRes, size = 48.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(nextDoctor.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                Text(nextDoctor.specialty, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
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
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${if (nextAppt.date == CareData.todayISO()) "Today" else nextAppt.date} · ${nextAppt.time}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            CareButton(
                                text = "Join",
                                onClick = { viewModel.navigateTo(CareScreen.Call(nextAppt.id)) },
                                variant = BtnVariant.ACCENT,
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("home_join_call_btn")
                            )
                        }
                    }
                }
            }
        }

        // Specialties 3x2 Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Specialties", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = "See all",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TelePrimary,
                    modifier = Modifier.clickable { viewModel.navigateTo(CareScreen.Doctors()) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunks = CareData.SPECIALTIES.chunked(3)
                chunks.forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowItems.forEach { sp ->
                            val iconVector = when (sp.icon) {
                                "Baby" -> Icons.Default.BabyChangingStation
                                "HeartPulse" -> Icons.Default.Favorite
                                "Brain" -> Icons.Default.Psychology
                                "Flower2" -> Icons.Default.Woman
                                else -> Icons.Default.MedicalServices
                            }
                            CareCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("specialty_${sp.name}"),
                                onClick = { viewModel.navigateTo(CareScreen.Doctors(sp.name)) }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(TelePrimarySoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(iconVector, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(sp.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Next Dose Card
        if (upcomingDose != null) {
            item {
                CareCard(
                    backgroundColor = TeleAccentSoft,
                    borderColor = TeleAccent.copy(alpha = 0.3f),
                    onClick = { viewModel.navigateTo(CareScreen.Meds) },
                    modifier = Modifier.testTag("home_next_dose_card")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(TeleAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Next dose · ${upcomingDose.second}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(upcomingDose.first.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text(upcomingDose.first.dose, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TeleAccent)
                    }
                }
            }
        }

        // Top Doctors Section
        item {
            Text("Top doctors", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        items(doctors.take(3)) { doc ->
            DoctorRowCard(
                doctor = doc,
                onClick = { viewModel.navigateTo(CareScreen.DoctorProfile(doc.id)) }
            )
        }

        // Bottom 2 Action Cards
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CareCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_records_btn"),
                    onClick = { viewModel.navigateTo(CareScreen.Records) }
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Health records", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Labs & files", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                CareCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_messages_btn"),
                    onClick = { viewModel.navigateTo(CareScreen.Chats) }
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Messages", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Ask your doctor", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }

    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = TeleDestructive)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Emergency Help", fontWeight = FontWeight.Bold, color = TeleDestructive)
                }
            },
            text = {
                Text("If this is a medical emergency, call 999 or 112 now. Telehealth is not for acute emergency care.", fontSize = 13.sp)
            },
            confirmButton = {
                CareButton(
                    text = "Call 999",
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:999"))
                        context.startActivity(dialIntent)
                        showSosDialog = false
                    },
                    variant = BtnVariant.PRIMARY
                )
            },
            dismissButton = {
                TextButton(onClick = { showSosDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun DoctorRowCard(
    doctor: Doctor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CareCard(
        modifier = modifier.testTag("doctor_card_${doctor.id}"),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CareAvatar(name = doctor.name, photoRes = doctor.photoRes, size = 56.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Verified, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(14.dp))
                }
                Text("${doctor.specialty} · ${doctor.years} yrs", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CareStars(value = doctor.rating, size = 12.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(String.format("%.1f", doctor.rating), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(CareData.formatKES(doctor.fee), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TelePrimary)
        }
    }
}

@Composable
fun DoctorsListScreen(
    viewModel: CareViewModel,
    initialSpecialty: String? = null
) {
    val doctors by viewModel.doctors.collectAsState()
    var filter by remember { mutableStateOf(if (CareData.SPECIALTIES.any { it.name == initialSpecialty }) initialSpecialty!! else "All") }
    var query by remember { mutableStateOf(if (CareData.SPECIALTIES.none { it.name == initialSpecialty }) initialSpecialty ?: "" else "") }

    BackHandler {
        viewModel.navigateBack()
    }

    val filteredList = doctors.filter { d ->
        (filter == "All" || d.specialty == filter) &&
                (d.name + d.specialty + d.bio).contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Find a doctor", onBack = { viewModel.navigateBack() })

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("e.g. rash, chest pain, child fever", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Specialty filter chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp)
        ) {
            val filters = listOf("All") + CareData.SPECIALTIES.map { it.name }
            items(filters) { item ->
                val selected = filter == item
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) TelePrimary else MaterialTheme.colorScheme.surface)
                        .border(1.dp, if (selected) TelePrimary else TeleBorder, RoundedCornerShape(20.dp))
                        .clickable { filter = item }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = item,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { doc ->
                DoctorRowCard(
                    doctor = doc,
                    onClick = { viewModel.navigateTo(CareScreen.DoctorProfile(doc.id)) }
                )
            }
        }
    }
}

@Composable
fun DoctorProfileScreen(
    viewModel: CareViewModel,
    doctorId: String
) {
    val doctors by viewModel.doctors.collectAsState()
    val doctor = doctors.find { it.id == doctorId } ?: return

    BackHandler {
        viewModel.navigateBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CareHeader(title = "Doctor", onBack = { viewModel.navigateBack() })
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CareAvatar(name = doctor.name, photoRes = doctor.photoRes, size = 110.dp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(doctor.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Verified, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(20.dp))
                    }
                    Text(doctor.specialty, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TeleSuccessSoft)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Licensed · ${doctor.license}", color = TeleSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Stats row 3 cols
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CareCard(modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("${doctor.years} yrs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                            Text("Experience", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    CareCard(modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(String.format("%.1f", doctor.rating), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                            Text("Rating", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    CareCard(modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("${doctor.reviews.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                            Text("Reviews", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // About Section
            item {
                Text("About", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(doctor.bio, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Speaks: ${doctor.languages.joinToString(", ")}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Reviews Section
            item {
                Text("Reviews", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(6.dp))
                if (doctor.reviews.isEmpty()) {
                    Text("No reviews yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        doctor.reviews.forEach { r ->
                            CareCard {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(r.author, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    CareStars(value = r.rating.toFloat(), size = 12.dp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(r.text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Sticky Bottom Booking Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, TeleBorder)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Consultation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(CareData.formatKES(doctor.fee), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TelePrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                CareButton(
                    text = "Message",
                    onClick = { viewModel.navigateTo(CareScreen.Chat(doctor.id)) },
                    variant = BtnVariant.SOFT,
                    modifier = Modifier.testTag("doc_message_btn")
                )
                Spacer(modifier = Modifier.width(8.dp))
                CareButton(
                    text = "Book appointment",
                    onClick = { viewModel.navigateTo(CareScreen.Book(doctor.id)) },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("doc_book_btn")
                )
            }
        }
    }
}
