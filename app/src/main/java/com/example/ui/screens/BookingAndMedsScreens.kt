package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CareData
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.model.CallType
import com.example.data.model.Doctor
import com.example.data.model.HealthRecord
import com.example.data.model.Medication
import com.example.data.model.PatientMedicalProfile
import com.example.data.model.Prescription
import com.example.ui.CareScreen
import com.example.ui.CareViewModel
import com.example.ui.components.BtnVariant
import com.example.ui.components.CareAvatar
import com.example.ui.components.CareButton
import com.example.ui.components.CareCard
import com.example.ui.components.CareField
import com.example.ui.components.CareHeader
import com.example.ui.components.CareStars
import com.example.ui.theme.TeleAccent
import com.example.ui.theme.TeleAccentSoft
import com.example.ui.theme.TeleBorder
import com.example.ui.theme.TeleDestructive
import com.example.ui.theme.TeleMuted
import com.example.ui.theme.TelePrimary
import com.example.ui.theme.TelePrimarySoft
import com.example.ui.theme.TeleSuccess
import com.example.ui.theme.TeleSuccessSoft
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun BookAppointmentScreen(
    viewModel: CareViewModel,
    doctorId: String,
    prefillReason: String? = null
) {
    val doctors by viewModel.doctors.collectAsState()
    val patientName by viewModel.patientName.collectAsState()
    val availability by viewModel.availability.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val acceptingPatients by viewModel.acceptingPatients.collectAsState()
    val doctor = doctors.find { it.id == doctorId } ?: return

    val days = remember { (0..6).map { CareData.todayISO(it) } }
    var selectedDate by remember { mutableStateOf(days[0]) }
    var selectedTime by remember { mutableStateOf("") }
    var consultationType by remember { mutableStateOf(CallType.VIDEO) }
    var reasonText by remember { mutableStateOf(prefillReason ?: "") }
    val openSlots = viewModel.availableSlots(doctorId, selectedDate)

    LaunchedEffect(availability, appointments, acceptingPatients, doctorId, selectedDate, selectedTime) {
        if (selectedTime.isNotBlank() && selectedTime !in openSlots) selectedTime = ""
    }

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
                CareHeader(title = "Book appointment", onBack = { viewModel.navigateBack() })
            }

            // Doctor Summary Card
            item {
                CareCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CareAvatar(name = doctor.name, photoRes = doctor.photoRes, size = 48.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(doctor.specialty, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Date horizontal picker
            item {
                Text("Date", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(days) { isoDate ->
                        val selected = selectedDate == isoDate
                        val cal = Calendar.getInstance()
                        val parts = isoDate.split("-").map { it.toInt() }
                        cal.set(parts[0], parts[1] - 1, parts[2])
                        val weekday = SimpleDateFormat("EEE", Locale.US).format(cal.time)
                        val dayNum = parts[2]

                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selected) TelePrimary else MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (selected) TelePrimary else TeleBorder, RoundedCornerShape(16.dp))
                                .clickable {
                                    selectedDate = isoDate
                                    selectedTime = ""
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = weekday.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$dayNum",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Time Slots 4 columns
            item {
                Text("Time", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (!viewModel.isAcceptingPatients(doctorId)) {
                    Text("This clinician is not accepting appointments right now.", fontSize = 12.sp, color = TeleMuted)
                } else if (openSlots.isEmpty()) {
                    Text("No available times on this date.", fontSize = 12.sp, color = TeleMuted)
                }
                val chunks = CareData.TIME_SLOTS.chunked(4)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    chunks.forEach { rowSlots ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowSlots.forEach { slot ->
                                val isSelected = selectedTime == slot
                                val slotAvailable = slot in openSlots
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) TelePrimary else if (slotAvailable) MaterialTheme.colorScheme.surface else TeleMuted.copy(alpha = 0.35f))
                                        .border(1.dp, if (isSelected) TelePrimary else TeleBorder, RoundedCornerShape(12.dp))
                                        .clickable(enabled = slotAvailable) { selectedTime = slot }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slot,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else if (slotAvailable) MaterialTheme.colorScheme.onSurface else TeleMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Consultation Type
            item {
                Text("Consultation type", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CareCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("type_video"),
                        backgroundColor = if (consultationType == CallType.VIDEO) TelePrimarySoft else MaterialTheme.colorScheme.surface,
                        borderColor = if (consultationType == CallType.VIDEO) TelePrimary else TeleBorder,
                        onClick = { consultationType = CallType.VIDEO }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Video call", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (consultationType == CallType.VIDEO) TelePrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    CareCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("type_voice"),
                        backgroundColor = if (consultationType == CallType.VOICE) TelePrimarySoft else MaterialTheme.colorScheme.surface,
                        borderColor = if (consultationType == CallType.VOICE) TelePrimary else TeleBorder,
                        onClick = { consultationType = CallType.VOICE }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Voice call", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (consultationType == CallType.VOICE) TelePrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Reason Text
            item {
                CareField(label = "What's bothering you?") {
                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        placeholder = { Text("Describe your symptoms", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        // Sticky Bottom Continue to Payment Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, TeleBorder)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            CareButton(
                text = "Review booking · ${CareData.formatKES(doctor.fee)} (unpaid)",
                onClick = {
                    val appt = Appointment(
                        id = "a-${System.currentTimeMillis()}",
                        doctorId = doctor.id,
                        patient = patientName,
                        date = selectedDate,
                        time = selectedTime,
                        type = consultationType,
                        reason = reasonText,
                        status = AppointmentStatus.UPCOMING,
                        paid = false
                    )
                    if (viewModel.canBookAppointment(appt)) {
                        viewModel.navigateTo(CareScreen.Pay(appt))
                    } else {
                        viewModel.showToast("That slot is no longer available. Choose another time.")
                    }
                },
                enabled = selectedTime in openSlots && reasonText.isNotBlank(),
                variant = BtnVariant.PRIMARY,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("book_continue_btn")
            )
        }
    }
}

@Composable
fun PaymentScreen(
    viewModel: CareViewModel,
    appointment: Appointment
) {
    val doctors by viewModel.doctors.collectAsState()
    val doctor = doctors.find { it.id == appointment.doctorId } ?: return

    var isBusy by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }

    val total = doctor.fee + 50

    BackHandler {
        if (!isBusy) viewModel.navigateBack()
    }

    if (isDone) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(TeleSuccessSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(44.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Demo booking saved — unpaid", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "No payment was taken. ${doctor.name} is scheduled for ${appointment.type.name.lowercase()} on ${appointment.date} at ${appointment.time}. This is an unpaid demo booking.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(28.dp))
            CareButton(
                text = "View appointments",
                onClick = { viewModel.navigateTo(CareScreen.Appointments) },
                variant = BtnVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Payment unavailable", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Fee breakdown card
            item {
                CareCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Consultation estimate", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CareData.formatKES(doctor.fee), fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Service fee", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CareData.formatKES(50), fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(TeleBorder))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimated total · not charged", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(CareData.formatKES(total), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TelePrimary)
                    }
                }
            }

            // Payment methods
            item {
                // M-Pesa
                CareCard(
                    borderColor = TeleBorder
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(TelePrimarySoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Smartphone, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("M-Pesa · unavailable", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("No STK push integration is connected", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .border(2.dp, TeleBorder, CircleShape)
                                .background(Color.Transparent)
                        )
                    }
                }
            }

            item {
                // Card
                CareCard(
                    borderColor = TeleBorder
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(TelePrimarySoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Card · unavailable", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("No card processor is connected", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .border(2.dp, TeleBorder, CircleShape)
                                .background(Color.Transparent)
                        )
                    }
                }
            }

            item {
                // Health Wallet
                CareCard(
                    borderColor = TeleBorder
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(TelePrimarySoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Wallet, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Health wallet · demo only", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Demo balance cannot be used as a real payment", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .border(2.dp, TeleBorder, CircleShape)
                                .background(Color.Transparent)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Payment is not available in this demo. Saving a booking below will not charge you and will leave it marked unpaid.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            CareButton(
                text = if (isBusy) "Saving…" else "Save unpaid demo booking",
                onClick = {
                    isBusy = true
                    val saved = viewModel.saveUnpaidDemoBooking(appointment)
                    isBusy = false
                    isDone = saved
                },
                enabled = !isBusy,
                variant = BtnVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth().testTag("pay_btn")
            )
        }
    }
}

@Composable
fun AppointmentsScreen(viewModel: CareViewModel) {
    val appointments by viewModel.appointments.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val prescriptions by viewModel.prescriptions.collectAsState()

    var activeTab by remember { mutableStateOf("upcoming") }

    val filteredList = appointments.filter { a ->
        if (activeTab == "upcoming") a.status == AppointmentStatus.UPCOMING else a.status == AppointmentStatus.COMPLETED
    }.sortedBy { it.date + it.time }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .testTag("appointments_screen")
    ) {
        Text("Appointments", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(TeleMuted)
                .padding(4.dp)
        ) {
            listOf("upcoming", "completed").forEach { tab ->
                val isSelected = activeTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { activeTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.replaceFirstChar { it.uppercase() },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList) { a ->
                val doc = doctors.find { it.id == a.doctorId }
                val rx = prescriptions.find { it.appointmentId == a.id }

                CareCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CareAvatar(name = doc?.name ?: "Doctor", photoRes = doc?.photoRes, size = 48.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc?.name ?: "Doctor", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(doc?.specialty ?: "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(TelePrimarySoft)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = a.type.name.lowercase(),
                                color = TelePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${if (a.date == CareData.todayISO()) "Today" else a.date} · ${a.time}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(a.reason, fontSize = 12.sp)
                    if (!a.paid) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("UNPAID · demo booking", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TeleDestructive)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (activeTab == "upcoming") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CareButton(
                                text = "Message",
                                onClick = { viewModel.navigateTo(CareScreen.Chat(a.doctorId)) },
                                variant = BtnVariant.SOFT,
                                modifier = Modifier.weight(1f).height(38.dp)
                            )
                            CareButton(
                                text = "Join call",
                                onClick = { viewModel.navigateTo(CareScreen.Call(a.id)) },
                                variant = BtnVariant.PRIMARY,
                                modifier = Modifier.weight(1f).height(38.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CareButton(
                                text = "Cancel · refund",
                                onClick = { viewModel.cancelAppointment(a.id) },
                                variant = BtnVariant.OUTLINE,
                                modifier = Modifier.weight(1f).height(36.dp)
                            )
                            CareButton(
                                text = "Reschedule",
                                onClick = {
                                    viewModel.cancelAppointment(a.id)
                                    viewModel.navigateTo(CareScreen.Book(a.doctorId, a.reason))
                                },
                                variant = BtnVariant.SOFT,
                                modifier = Modifier.weight(1f).height(36.dp)
                            )
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CareButton(
                                text = if (rx != null) "Prescription" else "Message",
                                onClick = {
                                    if (rx != null) viewModel.navigateTo(CareScreen.Meds)
                                    else viewModel.navigateTo(CareScreen.Chat(a.doctorId))
                                },
                                variant = BtnVariant.SOFT,
                                modifier = Modifier.weight(1f).height(38.dp)
                            )
                            if (a.rated) {
                                CareButton(
                                    text = "Book again",
                                    onClick = { viewModel.navigateTo(CareScreen.Book(a.doctorId)) },
                                    variant = BtnVariant.OUTLINE,
                                    modifier = Modifier.weight(1f).height(38.dp)
                                )
                            } else {
                                CareButton(
                                    text = "Rate visit",
                                    onClick = { viewModel.navigateTo(CareScreen.Rate(a.id)) },
                                    variant = BtnVariant.ACCENT,
                                    modifier = Modifier.weight(1f).height(38.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RateScreen(
    viewModel: CareViewModel,
    appointmentId: String
) {
    val appointments by viewModel.appointments.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val appt = appointments.find { it.id == appointmentId } ?: return
    val doc = doctors.find { it.id == appt.doctorId } ?: return

    var rating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Rate your visit", onBack = { viewModel.navigateBack() })

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CareAvatar(name = doc.name, photoRes = doc.photoRes, size = 96.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("How was ${doc.name}?", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))
            CareStars(value = rating.toFloat(), size = 32.dp, onChange = { rating = it })

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                placeholder = { Text("Share your experience (optional)", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                shape = RoundedCornerShape(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            CareButton(
                text = "Submit review",
                onClick = { viewModel.rateAppointment(appointmentId, rating, reviewText) },
                variant = BtnVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth().testTag("rate_submit_btn")
            )
        }
    }
}

@Composable
fun MedsScreen(viewModel: CareViewModel) {
    val prescriptions by viewModel.prescriptions.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val today = CareData.todayISO()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .testTag("meds_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Medications", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            CareButton(
                text = "Allow alerts",
                onClick = { viewModel.showToast("Phone notifications & in-app alerts active") },
                variant = BtnVariant.SOFT,
                leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(14.dp)) },
                modifier = Modifier.height(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (prescriptions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Prescriptions from your doctor will appear here.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(prescriptions) { p ->
                    val doc = doctors.find { it.id == p.doctorId }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${doc?.name ?: "Doctor"} · ${p.date} · ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(p.diagnosis, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            if (!p.diagnosisCode.isNullOrBlank()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(TelePrimarySoft).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text("ICD-11 ${p.diagnosisCode}", fontSize = 10.sp, color = TelePrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (!p.clinicalNotes.isNullOrBlank()) {
                            CareCard(backgroundColor = TeleMuted.copy(alpha = 0.5f)) {
                                Text("DOCTOR NOTES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(p.clinicalNotes, fontSize = 12.sp)
                            }
                        }

                        p.meds.forEach { m ->
                            CareCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(TeleAccentSoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Medication, contentDescription = null, tint = TeleAccent, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(m.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${m.dose} · ${m.frequency}× daily · ${m.days} days", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.updateMedication(p.id, m.id) { med -> med.copy(reminders = !med.reminders) }
                                        },
                                        modifier = Modifier.size(36.dp).clip(CircleShape).background(if (m.reminders) TelePrimary else TeleMuted)
                                    ) {
                                        Icon(
                                            imageVector = if (m.reminders) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                                            contentDescription = null,
                                            tint = if (m.reminders) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Reminder times pills
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    m.times.forEach { t ->
                                        val key = "$today $t"
                                        val isDone = m.taken.contains(key)

                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isDone) TeleSuccessSoft else TeleMuted)
                                                .border(1.dp, if (isDone) TeleSuccess else TeleBorder, RoundedCornerShape(10.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(t, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            IconButton(
                                                onClick = {
                                                    viewModel.updateMedication(p.id, m.id) { med ->
                                                        val updatedTaken = if (isDone) med.taken - key else med.taken + key
                                                        med.copy(taken = updatedTaken)
                                                    }
                                                },
                                                modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp)).background(if (isDone) TeleSuccess else Color.White)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = if (isDone) Color.White else Color.Gray, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatsListScreen(viewModel: CareViewModel, isDoctor: Boolean = false) {
    val doctors by viewModel.doctors.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val role by viewModel.role.collectAsState()
    val myDoctorId by viewModel.myDoctorId.collectAsState()
    val patientName by viewModel.patientName.collectAsState()

    val clinicianInbox = isDoctor || role == "doctor"
    val threadKeys = if (clinicianInbox) {
        messages.filter { it.doctorId == myDoctorId }
            .mapNotNull { message -> message.patient?.takeIf { it.isNotBlank() }?.let { patient -> message.doctorId to patient } }
            .distinct()
    } else {
        messages.filter { it.patient == patientName }.map { it.doctorId to patientName }.distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .testTag("chats_screen")
    ) {
        Text(if (clinicianInbox) "Patient messages" else "Messages", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(14.dp))

        if (threadKeys.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(if (clinicianInbox) "Patient messages will appear here." else "Open a doctor's profile to start a conversation.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(threadKeys) { (docId, patient) ->
                    val doc = doctors.find { it.id == docId } ?: return@items
                    val lastMsg = messages.lastOrNull { it.doctorId == docId && it.patient == patient }
                    val title = if (clinicianInbox) patient else doc.name

                    CareCard(
                        onClick = {
                            if (clinicianInbox) viewModel.navigateTo(CareScreen.DocChat(patient))
                            else viewModel.navigateTo(CareScreen.Chat(docId))
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CareAvatar(name = title, photoRes = if (clinicianInbox) null else doc.photoRes, size = 48.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(lastMsg?.text ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }
                            Text(lastMsg?.at ?: "", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    viewModel: CareViewModel,
    doctorId: String,
    patientThread: String? = null
) {
    val doctors by viewModel.doctors.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val patientName by viewModel.patientName.collectAsState()
    val role by viewModel.role.collectAsState()
    val doc = doctors.find { it.id == doctorId } ?: return

    val clinicianView = role == "doctor" || patientThread != null
    val threadPatient = patientThread ?: patientName
    val thread = messages.filter { it.doctorId == doctorId && it.patient == threadPatient }
    val title = if (clinicianView) threadPatient else doc.name
    var inputMessage by remember { mutableStateOf("") }

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(
            title = title,
            onBack = { viewModel.navigateBack() },
            right = { CareAvatar(name = title, photoRes = if (clinicianView) null else doc.photoRes, size = 36.dp) }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(thread) { m ->
                val isMe = if (clinicianView) m.from == "doctor" else m.from == "patient"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                )
                            )
                            .background(if (isMe) TelePrimary else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (isMe) Color.Transparent else TeleBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(m.text, fontSize = 13.sp, color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                m.at,
                                fontSize = 9.sp,
                                color = if (isMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, TeleBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Type a message", fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    viewModel.sendMessage(doctorId, threadPatient, if (clinicianView) "doctor" else "patient", inputMessage)
                    inputMessage = ""
                },
                modifier = Modifier.size(44.dp).clip(CircleShape).background(TelePrimary).testTag("chat_send_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun RecordsScreen(viewModel: CareViewModel) {
    val records by viewModel.records.collectAsState()
    var selectedKind by remember { mutableStateOf("Lab result") }

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Health records", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Upload button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(TelePrimarySoft)
                        .border(1.5.dp, TelePrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable {
                            viewModel.addHealthRecord("Diagnostic blood panel.pdf", selectedKind, "320 KB")
                        }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Upload a file", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TelePrimary)
                        Text("PDF, photos of lab results, scans", fontSize = 11.sp, color = TelePrimary.copy(alpha = 0.8f))
                    }
                }
            }

            // Categories
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val kinds = listOf("Lab result", "Imaging", "Prescription", "Insurance", "Other")
                    items(kinds) { k ->
                        val isSelected = selectedKind == k
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) TelePrimary else MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (isSelected) TelePrimary else TeleBorder, RoundedCornerShape(16.dp))
                                .clickable { selectedKind = k }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(k, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            items(records) { r ->
                CareCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(TelePrimarySoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(r.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${r.kind} · ${r.size} · ${r.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModel: CareViewModel) {
    val patientName by viewModel.patientName.collectAsState()
    val profiles by viewModel.patientProfiles.collectAsState()
    val currentProfile = profiles[patientName] ?: PatientMedicalProfile(patientName, emptyList(), false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .testTag("profile_screen")
    ) {
        Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CareCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CareAvatar(name = patientName, size = 56.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(patientName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Patient · Nairobi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                CareCard(onClick = { viewModel.navigateTo(CareScreen.Records) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Health records", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Care-team sharing consent card
            item {
                CareCard(
                    borderColor = TelePrimary.copy(alpha = 0.2f),
                    onClick = { viewModel.toggleCareTeamSharingConsent(patientName) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Share my records with my care team", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (currentProfile.careTeamSharingConsent) TeleSuccessSoft else TeleMuted)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (currentProfile.careTeamSharingConsent) "On" else "Off",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentProfile.careTeamSharingConsent) TeleSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "When on, clinicians you consult through this app can view diagnoses, allergies, and medication history recorded by other clinicians on the app. You can turn this off at any time.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }

            item {
                val videoSdkOrgName by viewModel.videoSdkOrgName.collectAsState()
                CareCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("VideoSDK HD Telehealth", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Encrypted via $videoSdkOrgName · WebRTC DTLS-SRTP", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TeleSuccessSoft)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Ready", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleSuccess)
                        }
                    }
                }
            }

            item {
                CareCard(onClick = { viewModel.setRole(null) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Switch role", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            item {
                CareCard(onClick = { viewModel.resetDemoData() }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = TeleDestructive, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Reset demo data", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TeleDestructive)
                    }
                }
            }
        }
    }
}
