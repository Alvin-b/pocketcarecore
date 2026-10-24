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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CareData
import com.example.data.model.LabPackage
import com.example.data.model.Vital
import com.example.ui.CareScreen
import com.example.ui.CareViewModel
import com.example.ui.components.BtnVariant
import com.example.ui.components.CareButton
import com.example.ui.components.CareCard
import com.example.ui.components.CareField
import com.example.ui.components.CareHeader
import com.example.ui.theme.TeleAccent
import com.example.ui.theme.TeleBorder
import com.example.ui.theme.TeleDestructive
import com.example.ui.theme.TeleDestructiveSoft
import com.example.ui.theme.TeleMuted
import com.example.ui.theme.TelePrimary
import com.example.ui.theme.TelePrimarySoft
import com.example.ui.theme.TeleSuccess
import com.example.ui.theme.TeleSuccessSoft
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun TriageScreen(viewModel: CareViewModel) {
    var symptoms by remember { mutableStateOf("") }
    var resultShown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler {
        viewModel.navigateBack()
    }

    val lowerText = symptoms.lowercase()
    val isEmergency = lowerText.contains("chest pain") ||
            lowerText.contains("difficulty breathing") ||
            lowerText.contains("can't breathe") ||
            lowerText.contains("shortness of breath") ||
            lowerText.contains("unconscious") ||
            lowerText.contains("severe bleeding") ||
            lowerText.contains("stroke") ||
            lowerText.contains("seizure")

    val matchedSpecialty = when {
        lowerText.contains("heart") || lowerText.contains("palpitation") || lowerText.contains("chest") -> "Cardiology"
        lowerText.contains("rash") || lowerText.contains("itch") || lowerText.contains("skin") || lowerText.contains("acne") -> "Dermatology"
        lowerText.contains("anxiety") || lowerText.contains("stress") || lowerText.contains("sleep") || lowerText.contains("mood") -> "Mental Health"
        lowerText.contains("child") || lowerText.contains("baby") || lowerText.contains("kid") || lowerText.contains("infant") -> "Pediatrics"
        else -> "General"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Symptom checker", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CareCard(backgroundColor = TelePrimarySoft, borderColor = TelePrimary.copy(alpha = 0.2f)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("A helpful first step", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("This tool offers general guidance only. It cannot diagnose or replace a clinician.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                CareField(label = "Tell us what you're experiencing") {
                    OutlinedTextField(
                        value = symptoms,
                        onValueChange = {
                            symptoms = it
                            resultShown = false
                        },
                        placeholder = { Text("Describe your symptoms, when they started, and how severe they feel…", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // Quick pills
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Headache", "Fever", "Skin rash", "Chest pain").forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(TeleMuted)
                                .clickable {
                                    symptoms = if (symptoms.isBlank()) tag else "$symptoms, ${tag.lowercase()}"
                                    resultShown = false
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("+ $tag", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                CareButton(
                    text = "Review symptoms",
                    onClick = { resultShown = true },
                    enabled = symptoms.isNotBlank(),
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth().testTag("review_symptoms_btn")
                )
            }

            if (resultShown) {
                item {
                    CareCard(
                        backgroundColor = if (isEmergency) TeleDestructiveSoft else TelePrimarySoft.copy(alpha = 0.5f),
                        borderColor = if (isEmergency) TeleDestructive else TelePrimary.copy(alpha = 0.3f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isEmergency) Icons.Default.Warning else Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = if (isEmergency) TeleDestructive else TelePrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEmergency) "Seek urgent medical help now" else "Suggested next step",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isEmergency) TeleDestructive else TelePrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isEmergency) {
                                "These symptoms may be serious. Call your local emergency number (in Kenya, 999 or 112) or go to the nearest emergency department. Do not wait for an online appointment."
                            } else {
                                "Consider discussing this with a ${matchedSpecialty.lowercase()} clinician. A doctor will assess you and determine the right care."
                            },
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isEmergency) {
                            CareButton(
                                text = "Call 999",
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:999"))
                                    context.startActivity(dialIntent)
                                },
                                variant = BtnVariant.PRIMARY,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            CareButton(
                                text = "Find a $matchedSpecialty doctor",
                                onClick = { viewModel.navigateTo(CareScreen.Doctors(matchedSpecialty)) },
                                variant = BtnVariant.PRIMARY,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VitalsScreen(viewModel: CareViewModel) {
    val vitals by viewModel.vitals.collectAsState()

    var bpField by remember { mutableStateOf("") }
    var glucoseField by remember { mutableStateOf("") }
    var tempField by remember { mutableStateOf("") }
    var weightField by remember { mutableStateOf("") }
    var hrField by remember { mutableStateOf("") }
    var noteField by remember { mutableStateOf("") }

    var isBleConnected by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateBack()
    }

    val kinds = listOf("Blood pressure", "Glucose", "Temperature", "Weight", "Heart rate")
    val latestReadings = kinds.map { k -> Pair(k, vitals.firstOrNull { it.kind == k }) }

    val isAbnormal = { v: Vital ->
        val n = v.value.replace(Regex("[^0-9.]"), "").toFloatOrNull() ?: 0f
        when (v.kind) {
            "Blood pressure" -> n >= 140 || v.value.contains("/9")
            "Glucose" -> n >= 200 || (n < 70 && n > 0)
            "Temperature" -> n >= 38
            "Heart rate" -> n > 100 || (n < 50 && n > 0)
            else -> false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Vitals tracker", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Latest readings 2x2 grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val chunks = latestReadings.chunked(2)
                    chunks.forEach { rowPairs ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowPairs.forEach { (kind, vital) ->
                                CareCard(modifier = Modifier.weight(1f)) {
                                    Text(kind, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(vital?.value ?: "—", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (vital != null) {
                                        Text(vital.date, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            if (rowPairs.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Record today's vitals card
            item {
                CareCard {
                    Text("Record today’s vitals", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Enter one or more measurements, then save them together.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))
                    CareField(label = "Blood pressure (e.g. 120/80 mmHg)") {
                        OutlinedTextField(value = bpField, onValueChange = { bpField = it }, placeholder = { Text("120/80") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CareField(label = "Glucose (mmol/L)", modifier = Modifier.weight(1f)) {
                            OutlinedTextField(value = glucoseField, onValueChange = { glucoseField = it }, placeholder = { Text("5.4") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                        }
                        CareField(label = "Temperature (°C)", modifier = Modifier.weight(1f)) {
                            OutlinedTextField(value = tempField, onValueChange = { tempField = it }, placeholder = { Text("36.8") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CareField(label = "Weight (kg)", modifier = Modifier.weight(1f)) {
                            OutlinedTextField(value = weightField, onValueChange = { weightField = it }, placeholder = { Text("70.0") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                        }
                        CareField(label = "Heart rate (bpm)", modifier = Modifier.weight(1f)) {
                            OutlinedTextField(value = hrField, onValueChange = { hrField = it }, placeholder = { Text("72") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    CareField(label = "Note (optional)") {
                        OutlinedTextField(value = noteField, onValueChange = { noteField = it }, placeholder = { Text("Context or how you feel") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    val hasAny = bpField.isNotBlank() || glucoseField.isNotBlank() || tempField.isNotBlank() || weightField.isNotBlank() || hrField.isNotBlank()
                    CareButton(
                        text = "Save all entered vitals",
                        onClick = {
                            val newVitals = mutableListOf<Vital>()
                            val nowStr = "Today, " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            if (bpField.isNotBlank()) newVitals.add(Vital(UUID.randomUUID().toString(), "Blood pressure", "$bpField mmHg", nowStr, noteField))
                            if (glucoseField.isNotBlank()) newVitals.add(Vital(UUID.randomUUID().toString(), "Glucose", "$glucoseField mmol/L", nowStr, noteField))
                            if (tempField.isNotBlank()) newVitals.add(Vital(UUID.randomUUID().toString(), "Temperature", "$tempField °C", nowStr, noteField))
                            if (weightField.isNotBlank()) newVitals.add(Vital(UUID.randomUUID().toString(), "Weight", "$weightField kg", nowStr, noteField))
                            if (hrField.isNotBlank()) newVitals.add(Vital(UUID.randomUUID().toString(), "Heart rate", "$hrField bpm", nowStr, noteField))

                            viewModel.saveVitals(newVitals)
                            bpField = ""
                            glucoseField = ""
                            tempField = ""
                            weightField = ""
                            hrField = ""
                            noteField = ""
                        },
                        enabled = hasAny,
                        variant = BtnVariant.PRIMARY,
                        modifier = Modifier.fillMaxWidth().testTag("save_vitals_btn")
                    )
                }
            }

            // Pair a health device card
            item {
                CareCard(borderColor = TelePrimary.copy(alpha = 0.2f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Watch, contentDescription = null, tint = TelePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pair a health device", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connect compatible Bluetooth LE heart-rate monitors, thermometers, or blood-pressure monitors using standard GATT services. Live readings fill the form above.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CareButton(
                        text = if (isBleConnected) "Connected · Bluetooth Medical Sensor" else "Pair Bluetooth device",
                        onClick = {
                            isBleConnected = !isBleConnected
                            if (isBleConnected) {
                                hrField = "74"
                                bpField = "120/80"
                                tempField = "36.8"
                                viewModel.showToast("Connected to Bluetooth health device; live values filled in.")
                            }
                        },
                        variant = BtnVariant.SOFT,
                        modifier = Modifier.fillMaxWidth().testTag("pair_ble_btn")
                    )
                }
            }

            // Measurement history
            item {
                Text("Measurement history", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            items(vitals) { v ->
                val abnormal = isAbnormal(v)
                CareCard {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Watch, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${v.kind} · ${v.value}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (abnormal) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = TeleAccent, modifier = Modifier.size(14.dp))
                                }
                            }
                            Text("${v.date}${if (v.note.isNotBlank()) " · ${v.note}" else ""}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (abnormal) {
                                Text("Outside a common adult reference range. Contact a clinician for interpretation.", fontSize = 11.sp, color = TeleAccent)
                            }
                        }
                        Text(
                            text = "Remove",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TeleDestructive,
                            modifier = Modifier.clickable { viewModel.removeVital(v.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LabsScreen(viewModel: CareViewModel) {
    val labOrders by viewModel.labOrders.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()

    var selectedPkg by remember { mutableStateOf(CareData.LAB_PACKAGES[0]) }
    var selectedDate by remember { mutableStateOf(CareData.todayISO(1)) }
    var homeCollection by remember { mutableStateOf(false) }
    var addressField by remember { mutableStateOf("") }

    BackHandler {
        viewModel.navigateBack()
    }

    val total = selectedPkg.price + (if (homeCollection) 300 else 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Lab tests", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CareCard(backgroundColor = TelePrimarySoft, borderColor = TelePrimary.copy(alpha = 0.2f)) {
                    Text("Book a test", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Demo booking. Results and collection status are simulated.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Package options
            items(CareData.LAB_PACKAGES) { pkg ->
                val isSelected = selectedPkg.name == pkg.name
                CareCard(
                    borderColor = if (isSelected) TelePrimary else TeleBorder,
                    onClick = { selectedPkg = pkg }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(pkg.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text(CareData.formatKES(pkg.price), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TelePrimary)
                    }
                }
            }

            // Date & Home collection
            item {
                CareField(label = "Appointment date") {
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { selectedDate = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            item {
                CareCard(onClick = { homeCollection = !homeCollection }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = homeCollection, onCheckedChange = { homeCollection = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Home sample collection (+KES 300)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (homeCollection) {
                item {
                    CareField(label = "Collection address") {
                        OutlinedTextField(
                            value = addressField,
                            onValueChange = { addressField = it },
                            placeholder = { Text("Street, estate, town") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                CareButton(
                    text = "Book · ${CareData.formatKES(total)}",
                    onClick = {
                        viewModel.bookLabOrder(selectedPkg.name, selectedPkg.price, selectedDate, homeCollection, addressField)
                    },
                    enabled = !homeCollection || addressField.isNotBlank(),
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth().testTag("book_lab_btn")
                )
            }

            // Your orders
            item {
                Text("Your orders", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            if (labOrders.isEmpty()) {
                item {
                    Text("No lab orders yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(labOrders) { o ->
                    CareCard {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(o.test, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(TelePrimarySoft).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(o.status, fontSize = 10.sp, color = TelePrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${o.date} · ${if (o.homeCollection) "Home collection · ${o.address}" else "Clinic collection"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${CareData.formatKES(o.price)} · paid from wallet", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun WalletScreen(viewModel: CareViewModel) {
    val walletBalance by viewModel.walletBalance.collectAsState()
    val walletEntries by viewModel.walletEntries.collectAsState()
    var amountField by remember { mutableStateOf("1000") }

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Health wallet", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Balance card with gradient primary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = TelePrimary)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Wallet, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Available balance", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(CareData.formatKES(walletBalance), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Demo balance · no real payments processed", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            // Top up with M-Pesa
            item {
                CareCard {
                    Text("Top up with M-Pesa", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(500, 1000, 2000).forEach { n ->
                            val isSelected = amountField == n.toString()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TeleMuted)
                                    .border(1.dp, if (isSelected) TelePrimary else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { amountField = n.toString() }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(CareData.formatKES(n), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    CareField(label = "Amount (KES)") {
                        OutlinedTextField(
                            value = amountField,
                            onValueChange = { amountField = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    CareButton(
                        text = "Top up wallet",
                        onClick = {
                            val amt = amountField.toIntOrNull() ?: 0
                            viewModel.topupWallet(amt)
                        },
                        variant = BtnVariant.PRIMARY,
                        modifier = Modifier.fillMaxWidth().testTag("wallet_topup_btn")
                    )
                }
            }

            // Transactions list
            item {
                Text("Transactions", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            items(walletEntries) { e ->
                val isCredit = e.kind == "credit"
                CareCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isCredit) TeleSuccessSoft else TeleDestructiveSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (isCredit) TeleSuccess else TeleDestructive,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(e.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(e.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = "${if (isCredit) "+" else "−"}${CareData.formatKES(e.amount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isCredit) TeleSuccess else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
