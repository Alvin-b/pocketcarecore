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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FacilityMedication
import com.example.data.model.FacilityService
import com.example.ui.CareScreen
import com.example.ui.CareViewModel
import com.example.ui.components.BtnVariant
import com.example.ui.components.CareButton
import com.example.ui.components.CareCard
import com.example.ui.components.CareHeader
import com.example.ui.theme.TeleAccent
import com.example.ui.theme.TeleAccentSoft
import com.example.ui.theme.TeleBorder
import com.example.ui.theme.TeleMuted
import com.example.ui.theme.TelePrimary
import com.example.ui.theme.TelePrimarySoft
import com.example.ui.theme.TeleSuccess
import com.example.ui.theme.TeleSuccessSoft
import com.example.ui.theme.TeleTextSecondary

enum class FacilityProfileTab(val title: String) {
    SERVICES("Services & Pay"),
    PHARMACY("Buy Meds"),
    STRUCTURE("Hospital Structure")
}

@Composable
fun FacilityProfileScreen(
    viewModel: CareViewModel,
    facilityId: String
) {
    val context = LocalContext.current
    val facilities by viewModel.facilities.collectAsState()
    val allServices by viewModel.facilityServices.collectAsState()
    val allMedications by viewModel.facilityMedications.collectAsState()
    val departments by viewModel.facilityDepartments.collectAsState()
    val staff by viewModel.facilityStaff.collectAsState()
    val patientName by viewModel.patientName.collectAsState()

    val facility = facilities.find { it.id == facilityId } ?: facilities.firstOrNull() ?: return
    val facilityServices = allServices.filter { it.facilityId == facilityId }.ifEmpty { allServices }
    val facilityMeds = allMedications.filter { it.sellerId == facilityId }.ifEmpty { allMedications }

    var selectedTab by remember { mutableStateOf(FacilityProfileTab.SERVICES) }
    var serviceToPay by remember { mutableStateOf<FacilityService?>(null) }
    var medToBuy by remember { mutableStateOf<FacilityMedication?>(null) }
    var paymentReceiptBookingId by remember { mutableStateOf<String?>(null) }

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("facility_profile_screen")
    ) {
        CareHeader(title = "Facility Profile", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Facility Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TeleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(TelePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocalHospital,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = facility.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = TelePrimary, modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = facility.type,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TelePrimary
                                    )
                                    Text(
                                        text = facility.address,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Accreditation & Rating Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TeleSuccessSoft)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = facility.accreditation,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TeleSuccess
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                Text("${facility.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("(${facility.reviewCount})", fontSize = 11.sp, color = TeleTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Grid: Beds, Hours, Emergency
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TeleMuted)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("INPATIENT BEDS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TeleTextSecondary)
                                    Text("${facility.occupiedBeds}/${facility.totalBeds} Active", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TeleMuted)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("HOURS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TeleTextSecondary)
                                    Text(facility.openHours, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (facility.emergencyAvailable) TeleAccentSoft else TeleMuted)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("EMERGENCY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TeleAccent)
                                    Text(if (facility.emergencyAvailable) "24/7 Trauma" else "Clinic Hours", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TeleAccent)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Bar: Hotline & Book Structure
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CareButton(
                                text = "Call Hotline",
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${facility.phone}"))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        viewModel.showToast("Cannot dial phone")
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                variant = BtnVariant.OUTLINE,
                                modifier = Modifier.weight(1f).height(38.dp)
                            )
                            CareButton(
                                text = "Book Appointment",
                                onClick = { viewModel.navigateTo(CareScreen.FacilityBookCare()) },
                                variant = BtnVariant.PRIMARY,
                                modifier = Modifier.weight(1f).height(38.dp)
                            )
                        }
                    }
                }
            }

            // Tab Navigation Pill Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FacilityProfileTab.values().forEach { tab ->
                        val isSelected = tab == selectedTab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) TelePrimary else TeleMuted)
                                .clickable { selectedTab = tab }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Tab 1: SERVICES & DIRECT PAY
            if (selectedTab == FacilityProfileTab.SERVICES) {
                item {
                    Text(
                        text = "Clinical Services & Diagnostics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Select a service, pay securely, and your intake appointment is immediately confirmed on the facility side.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(facilityServices) { service ->
                    CareCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(TelePrimarySoft)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(service.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(service.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(service.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("KSh ${service.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TelePrimary)
                                    Text("${service.durationMinutes} mins", fontSize = 10.sp, color = TeleTextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (service.isTelehealthAvailable) {
                                        Icon(Icons.Default.Videocam, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(14.dp))
                                        Text("Virtual Telehealth Pod Ready", fontSize = 10.sp, color = TeleSuccess, fontWeight = FontWeight.Medium)
                                    } else {
                                        Icon(Icons.Default.Apartment, contentDescription = null, tint = TeleTextSecondary, modifier = Modifier.size(14.dp))
                                        Text("In-Facility Procedure Suite", fontSize = 10.sp, color = TeleTextSecondary)
                                    }
                                }

                                CareButton(
                                    text = "Choose & Pay",
                                    onClick = { serviceToPay = service },
                                    variant = BtnVariant.PRIMARY,
                                    modifier = Modifier.height(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Tab 2: FACILITY PHARMACY / MEDS
            if (selectedTab == FacilityProfileTab.PHARMACY) {
                item {
                    Text(
                        text = "Facility In-House Pharmacy & Dispensary",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Certified pharmaceutical dispensary. Buy hospital-prescribed meds for counter collection or instant dispatch.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(facilityMeds) { med ->
                    CareCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(TeleAccentSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = TeleAccent, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text(med.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${med.genericName} · ${med.dosage}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("KSh ${med.price.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                                        if (med.requiresPrescription) {
                                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(TeleMuted).padding(horizontal = 4.dp, vertical = 1.dp)) {
                                                Text("Rx Required", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TeleTextSecondary)
                                            }
                                        }
                                    }
                                }
                            }

                            CareButton(
                                text = "Buy Med",
                                onClick = { medToBuy = med },
                                variant = BtnVariant.PRIMARY,
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }
                }
            }

            // Tab 3: STRUCTURE & DEPARTMENTS
            if (selectedTab == FacilityProfileTab.STRUCTURE) {
                item {
                    Text(
                        text = "Hospital Departments & Active Team",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "When you book with our facility, your intake is evaluated and assigned to the clinical department on duty.",
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
                                Text(dept.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(TelePrimarySoft)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("${dept.waitTimeMinutes}m wait", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(dept.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Head of Division: ${dept.headDoctorName} · ${dept.activeStaffCount} Clinicians", fontSize = 10.sp, color = TeleTextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            CareButton(
                                text = "Book with this Department",
                                onClick = { viewModel.navigateTo(CareScreen.FacilityBookCare(dept.id)) },
                                variant = BtnVariant.OUTLINE,
                                modifier = Modifier.fillMaxWidth().height(32.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }

    // -----------------------------------------------------------------
    // SERVICE PAYMENT MODAL
    // -----------------------------------------------------------------
    serviceToPay?.let { service ->
        var pName by remember { mutableStateOf(patientName) }
        var patientNotes by remember { mutableStateOf("") }
        var paymentMethod by remember { mutableStateOf("M-Pesa Express") }

        AlertDialog(
            onDismissRequest = { serviceToPay = null },
            title = {
                Column {
                    Text("Pay for Service", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(service.name, fontSize = 12.sp, color = TelePrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(TelePrimarySoft.copy(alpha = 0.3f))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total to Pay:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("KSh ${service.price.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                        }
                    }

                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("Patient Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = patientNotes,
                        onValueChange = { patientNotes = it },
                        label = { Text("Clinical Notes / Symptoms (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Select Payment Method:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    listOf("M-Pesa Express", "Health Wallet", "Credit / Debit Card").forEach { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { paymentMethod = method }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = paymentMethod == method,
                                onClick = { paymentMethod = method }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(method, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                CareButton(
                    text = "Pay KSh ${service.price.toInt()}",
                    onClick = {
                        viewModel.payForFacilityService(
                            facilityId = facility.id,
                            serviceId = service.id,
                            patientNotes = patientNotes,
                            paymentMethod = paymentMethod
                        )
                        paymentReceiptBookingId = "CC-BK-${System.currentTimeMillis() % 10000}"
                        serviceToPay = null
                    },
                    variant = BtnVariant.PRIMARY
                )
            },
            dismissButton = {
                TextButton(onClick = { serviceToPay = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // -----------------------------------------------------------------
    // BUY MEDICATION MODAL
    // -----------------------------------------------------------------
    medToBuy?.let { med ->
        var quantity by remember { mutableIntStateOf(1) }
        var deliveryType by remember { mutableStateOf("Counter Pickup") }
        var address by remember { mutableStateOf("Nairobi CBD") }
        val total = med.price * quantity

        AlertDialog(
            onDismissRequest = { medToBuy = null },
            title = {
                Column {
                    Text("Buy Medication", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${med.name} · ${med.dosage}", fontSize = 12.sp, color = TelePrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Quantity:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(TeleMuted)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Text("$quantity", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            IconButton(
                                onClick = { quantity++ },
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(TeleMuted)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TelePrimarySoft.copy(alpha = 0.3f))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Price:", fontWeight = FontWeight.Medium)
                            Text("KSh ${total.toInt()}", fontWeight = FontWeight.Bold, color = TelePrimary)
                        }
                    }

                    Text("Delivery Method:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    listOf("Counter Pickup", "Express Delivery to Location").forEach { dType ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { deliveryType = dType }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(selected = deliveryType == dType, onClick = { deliveryType = dType })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(dType, fontSize = 13.sp)
                        }
                    }

                    if (deliveryType.contains("Delivery", true)) {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Delivery Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                CareButton(
                    text = "Pay & Order KSh ${total.toInt()}",
                    onClick = {
                        viewModel.buyFacilityMedication(
                            facilityId = facility.id,
                            medicationId = med.id,
                            quantity = quantity,
                            deliveryType = deliveryType,
                            address = address
                        )
                        medToBuy = null
                    },
                    variant = BtnVariant.PRIMARY
                )
            },
            dismissButton = {
                TextButton(onClick = { medToBuy = null }) { Text("Cancel") }
            }
        )
    }

    // -----------------------------------------------------------------
    // PAYMENT CONFIRMATION RECEIPT DIALOG
    // -----------------------------------------------------------------
    paymentReceiptBookingId?.let { refCode ->
        AlertDialog(
            onDismissRequest = { paymentReceiptBookingId = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(24.dp))
                    Text("Payment & Booking Confirmed!")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your payment has been logged and reflected directly on ${facility.name}'s central intake dashboard.", fontSize = 13.sp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(TeleMuted)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("BOOKING REFERENCE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleTextSecondary)
                            Text(refCode, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                            Text("Status: Paid & Checked In", fontSize = 11.sp, color = TeleSuccess, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("You can access this booking in your Appointments or check in at the facility reception upon arrival.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                CareButton(
                    text = "Done",
                    onClick = { paymentReceiptBookingId = null },
                    variant = BtnVariant.PRIMARY
                )
            }
        )
    }
}
