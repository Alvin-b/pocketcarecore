package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CareData
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.model.CallType
import com.example.data.model.ClinicianTestRequest
import com.example.data.model.Doctor
import com.example.data.model.HealthRecord
import com.example.data.model.LabOrder
import com.example.data.model.LabRequestStatus
import com.example.data.model.Medication
import com.example.data.model.Message
import com.example.data.model.PatientAllergy
import com.example.data.model.PatientMedicalProfile
import com.example.data.model.Prescription
import com.example.data.model.Review
import com.example.data.model.Vital
import com.example.data.model.WalletEntry
import com.example.video.CallDiagnostics
import com.example.video.CallSession
import com.example.video.CallState
import com.example.video.VideoCallManager
import com.example.wearable.UteWatchDevice
import com.example.wearable.UteWatchRepository
import com.example.wearable.UteWatchUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class CareScreen {
    object Welcome : CareScreen()
    object Home : CareScreen()
    data class Doctors(val specialty: String? = null) : CareScreen()
    data class DoctorProfile(val id: String) : CareScreen()
    data class Book(val id: String, val prefill: String? = null) : CareScreen()
    data class Pay(val appointment: Appointment) : CareScreen()
    object Appointments : CareScreen()
    data class Call(val appointmentId: String) : CareScreen()
    data class Rate(val appointmentId: String) : CareScreen()
    object Meds : CareScreen()
    object Chats : CareScreen()
    data class Chat(val doctorId: String) : CareScreen()
    object Records : CareScreen()
    object Profile : CareScreen()
    object DocRegister : CareScreen()
    object DocHome : CareScreen()
    object DocPatients : CareScreen()
    data class DocPatient(val patient: String) : CareScreen()
    object DocChats : CareScreen()
    data class DocChat(val patient: String) : CareScreen()
    object DocSettings : CareScreen()
    data class DocPrescribe(val appointmentId: String, val fromChat: Boolean = false) : CareScreen()
    object Triage : CareScreen()
    object Vitals : CareScreen()
    object Labs : CareScreen()
    object Wallet : CareScreen()
    object DocEarnings : CareScreen()
    object DocSchedule : CareScreen()
}

class CareViewModel(application: Application) : AndroidViewModel(application) {

    private val uteWatchRepository = UteWatchRepository(application.applicationContext)
    val uteWatchUiState: StateFlow<UteWatchUiState> = uteWatchRepository.uiState
    private var uteWatchSyncJob: Job? = null

    // Role: "patient" | "doctor" | null
    private val _role = MutableStateFlow<String?>("patient")
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _patientName = MutableStateFlow("Joy Kamau")
    val patientName: StateFlow<String> = _patientName.asStateFlow()

    private val _doctors = MutableStateFlow(CareData.DOCTORS)
    val doctors: StateFlow<List<Doctor>> = _doctors.asStateFlow()

    private val _myDoctorId = MutableStateFlow<String?>("d1")
    val myDoctorId: StateFlow<String?> = _myDoctorId.asStateFlow()

    private val _appointments = MutableStateFlow(CareData.initialAppointments())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _prescriptions = MutableStateFlow(CareData.initialPrescriptions())
    val prescriptions: StateFlow<List<Prescription>> = _prescriptions.asStateFlow()

    private val _patientProfiles = MutableStateFlow<Map<String, PatientMedicalProfile>>(
        mapOf("Joy Kamau" to CareData.initialProfile())
    )
    val patientProfiles: StateFlow<Map<String, PatientMedicalProfile>> = _patientProfiles.asStateFlow()

    private val _messages = MutableStateFlow(
        CareData.initialMessages().map { message ->
            if (message.patient.isNullOrBlank()) message.copy(patient = _patientName.value) else message
        }
    )
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _records = MutableStateFlow(CareData.initialRecords())
    val records: StateFlow<List<HealthRecord>> = _records.asStateFlow()

    private val _vitals = MutableStateFlow<List<Vital>>(
        listOf(
            Vital("v-init-1", "Heart rate", "74 bpm", "Today, 10:30 AM", "Demo placeholder; not from a connected device"),
            Vital("v-init-2", "Blood pressure", "120/80 mmHg", "Today, 10:30 AM", "Demo placeholder; not measured"),
            Vital("v-init-3", "Temperature", "36.8 °C", "Today, 10:30 AM", "Demo placeholder; not measured"),
            Vital("v-init-4", "Glucose", "5.4 mmol/L", "Today, 08:15 AM", "Demo placeholder; not measured"),
            Vital("v-init-5", "Weight", "68.5 kg", "Today, 08:00 AM", "Demo placeholder; not measured")
        )
    )
    val vitals: StateFlow<List<Vital>> = _vitals.asStateFlow()

    private val _labOrders = MutableStateFlow<List<LabOrder>>(emptyList())
    val labOrders: StateFlow<List<LabOrder>> = _labOrders.asStateFlow()

    private val _clinicianTestRequests = MutableStateFlow<List<ClinicianTestRequest>>(emptyList())
    val clinicianTestRequests: StateFlow<List<ClinicianTestRequest>> = _clinicianTestRequests.asStateFlow()

    private val _walletBalance = MutableStateFlow(2500)
    val walletBalance: StateFlow<Int> = _walletBalance.asStateFlow()

    private val _walletEntries = MutableStateFlow(CareData.initialWalletEntries())
    val walletEntries: StateFlow<List<WalletEntry>> = _walletEntries.asStateFlow()

    private val _acceptingPatients = MutableStateFlow(true)
    val acceptingPatients: StateFlow<Boolean> = _acceptingPatients.asStateFlow()

    private val _availability = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val availability: StateFlow<Map<String, List<String>>> = _availability.asStateFlow()

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<CareScreen>>(listOf(CareScreen.Home))
    val screenStack: StateFlow<List<CareScreen>> = _screenStack.asStateFlow()

    val currentScreen: CareScreen
        get() = _screenStack.value.lastOrNull() ?: CareScreen.Home

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast.asStateFlow()

    // VideoSDK Telemedicine Integration & VideoCallManager
    val videoCallManager = VideoCallManager.getInstance()
    val videoCallState: StateFlow<CallState> = videoCallManager.callState
    val videoCallSession: StateFlow<CallSession?> = videoCallManager.currentSession
    val videoCallDiagnostics: StateFlow<CallDiagnostics> = videoCallManager.diagnostics
    val videoCallElapsedSeconds: StateFlow<Int> = videoCallManager.elapsedSeconds
    val videoCallMicOn: StateFlow<Boolean> = videoCallManager.isMicOn
    val videoCallCamOn: StateFlow<Boolean> = videoCallManager.isCamOn
    val videoCallCameraFacing: StateFlow<String> = videoCallManager.cameraFacing

    val videoSdkProjectId: StateFlow<String> = videoCallManager.projectId
    val videoSdkOrgName: StateFlow<String> = videoCallManager.organization
    val videoSdkToken: StateFlow<String> = videoCallManager.token

    private val _useEmbeddedVideoSdk = MutableStateFlow(true)
    val useEmbeddedVideoSdk: StateFlow<Boolean> = _useEmbeddedVideoSdk.asStateFlow()

    fun updateVideoSdkConfig(projectId: String, token: String) {
        videoCallManager.initialize(projectId = projectId, token = token)
        showToast("VideoSDK configuration updated")
    }

    fun toggleEmbeddedVideoSdk(enabled: Boolean) {
        _useEmbeddedVideoSdk.value = enabled
    }

    fun startVideoCall(
        appointmentId: String,
        participantName: String,
        isDoctor: Boolean,
        initialMic: Boolean = true,
        initialCam: Boolean = true
    ): CallSession {
        return videoCallManager.joinRoom(appointmentId, participantName, isDoctor, initialMic, initialCam)
    }

    fun toggleCallMic(): Boolean = videoCallManager.toggleMic()

    fun toggleCallCam(): Boolean = videoCallManager.toggleCamera()

    fun switchCallCamera(): String = videoCallManager.switchCamera()

    fun endVideoCall() {
        videoCallManager.leaveRoom()
    }

    fun getVideoSdkMeetingUrl(appointmentId: String, name: String): String {
        return CareData.buildVideoSdkMeetingUrl(
            appointmentId = appointmentId,
            name = name,
            token = videoCallManager.token.value.ifBlank { null }
        )
    }

    fun getVideoSdkEmbedUrl(appointmentId: String, name: String, micEnabled: Boolean, camEnabled: Boolean): String {
        return CareData.buildVideoSdkEmbedUrl(
            appointmentId = appointmentId,
            name = name,
            token = videoCallManager.token.value.ifBlank { null },
            micEnabled = micEnabled,
            webcamEnabled = camEnabled
        )
    }

    fun showToast(msg: String) {
        _toast.value = msg
    }

    fun clearToast() {
        _toast.value = null
    }

    init {
        startReminderTicker()
    }

    fun setRole(r: String?) {
        _role.value = r
        if (r == "doctor") {
            _screenStack.value = listOf(CareScreen.DocHome)
        } else if (r == "patient") {
            _screenStack.value = listOf(CareScreen.Home)
        } else {
            _screenStack.value = listOf(CareScreen.Welcome)
        }
    }

    fun navigateTo(screen: CareScreen) {
        val rootScreenNames = listOf(
            CareScreen.Home::class,
            CareScreen.Appointments::class,
            CareScreen.Meds::class,
            CareScreen.Chats::class,
            CareScreen.Profile::class,
            CareScreen.DocHome::class,
            CareScreen.DocPatients::class,
            CareScreen.DocChats::class,
            CareScreen.DocSchedule::class,
            CareScreen.DocSettings::class,
            CareScreen.Welcome::class
        )

        val isRoot = rootScreenNames.any { it.isInstance(screen) }
        if (isRoot) {
            _screenStack.value = listOf(screen)
        } else {
            _screenStack.value = _screenStack.value + screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            _screenStack.value = _screenStack.value.dropLast(1)
            return true
        }
        return false
    }

    fun resetDemoData() {
        disconnectUteWatch()
        uteWatchRepository.clearMetrics()
        _role.value = null
        _patientName.value = "Joy Kamau"
        _doctors.value = CareData.DOCTORS
        _myDoctorId.value = null
        _appointments.value = CareData.initialAppointments()
        _prescriptions.value = CareData.initialPrescriptions()
        _patientProfiles.value = mapOf("Joy Kamau" to CareData.initialProfile())
        _messages.value = CareData.initialMessages().map { message ->
            if (message.patient.isNullOrBlank()) message.copy(patient = _patientName.value) else message
        }
        _records.value = CareData.initialRecords()
        _vitals.value = emptyList()
        _labOrders.value = emptyList()
        _clinicianTestRequests.value = emptyList()
        _walletBalance.value = 2500
        _walletEntries.value = CareData.initialWalletEntries()
        _acceptingPatients.value = true
        _availability.value = emptyMap()
        _screenStack.value = listOf(CareScreen.Welcome)
        showToast("Demo data reset")
    }

    override fun onCleared() {
        uteWatchSyncJob?.cancel()
        uteWatchRepository.close()
        super.onCleared()
    }

    // Appointment Booking
    fun isAcceptingPatients(doctorId: String): Boolean =
        doctorId != _myDoctorId.value || _acceptingPatients.value

    fun availableSlots(doctorId: String, date: String): List<String> {
        if (!isAcceptingPatients(doctorId)) return emptyList()
        val key = "$doctorId:$date"
        val configuredSlots = _availability.value[key] ?: CareData.TIME_SLOTS.take(6)
        val bookedSlots = _appointments.value
            .filter { it.doctorId == doctorId && it.date == date && it.status == AppointmentStatus.UPCOMING }
            .map { it.time }
            .toSet()
        return configuredSlots.filterNot { it in bookedSlots }
    }

    fun canBookAppointment(appointment: Appointment): Boolean =
        appointment.time in availableSlots(appointment.doctorId, appointment.date)

    fun saveUnpaidDemoBooking(appointment: Appointment): Boolean {
        if (!canBookAppointment(appointment)) {
            showToast("That clinician is not accepting this slot. Please choose another available time.")
            return false
        }
        if (_appointments.value.any { it.id == appointment.id }) {
            showToast("This booking has already been saved")
            return false
        }
        _appointments.value = listOf(appointment.copy(paid = false)) + _appointments.value
        showToast("Demo booking saved — unpaid; no payment was taken")
        return true
    }

    fun addAppointment(appointment: Appointment) {
        _appointments.value = listOf(appointment) + _appointments.value
    }

    fun cancelAppointment(appointmentId: String) {
        val appt = _appointments.value.find { it.id == appointmentId } ?: return
        val doc = _doctors.value.find { it.id == appt.doctorId }
        _appointments.value = _appointments.value.map {
            if (it.id == appointmentId) it.copy(status = AppointmentStatus.CANCELLED) else it
        }
        if (appt.paid && doc != null) {
            val refund = doc.fee + 50
            _walletBalance.value += refund
            _walletEntries.value = listOf(
                WalletEntry("w-${System.currentTimeMillis()}", "Refund · ${doc.name}", refund, CareData.todayISO(), "credit")
            ) + _walletEntries.value
            showToast("Appointment cancelled; refund added to wallet")
        } else {
            showToast("Appointment cancelled")
        }
    }

    fun markAppointmentCompleted(appointmentId: String) {
        _appointments.value = _appointments.value.map {
            if (it.id == appointmentId) it.copy(status = AppointmentStatus.COMPLETED) else it
        }
    }

    fun rateAppointment(appointmentId: String, rating: Int, reviewText: String) {
        val appt = _appointments.value.find { it.id == appointmentId } ?: return
        val doc = _doctors.value.find { it.id == appt.doctorId } ?: return

        _appointments.value = _appointments.value.map {
            if (it.id == appointmentId) it.copy(rated = true) else it
        }

        val author = _patientName.value.split(" ").firstOrNull() ?: "Patient"
        val newReview = Review(author, rating, if (reviewText.isBlank()) "Great consultation." else reviewText)
        val updatedReviews = listOf(newReview) + doc.reviews
        val newAvgRating = (doc.rating * doc.reviews.size + rating) / (doc.reviews.size + 1)

        _doctors.value = _doctors.value.map {
            if (it.id == doc.id) it.copy(reviews = updatedReviews, rating = newAvgRating) else it
        }
        showToast("Thanks for your review!")
        navigateTo(CareScreen.Appointments)
    }

    // Payment
    fun processPayment(appointment: Appointment, method: String, phone: String): Boolean {
        showToast("${method.replaceFirstChar { it.uppercase() }} payment is unavailable in this demo. No payment was taken.")
        return false
    }

    // Wallet
    fun topupWallet(amount: Int) {
        if (amount < 100) {
            showToast("Enter at least KES 100")
            return
        }
        _walletBalance.value += amount
        _walletEntries.value = listOf(
            WalletEntry("w-${System.currentTimeMillis()}", "M-Pesa top-up (demo)", amount, CareData.todayISO(), "credit")
        ) + _walletEntries.value
        showToast("Wallet topped up (demo)")
    }

    // Lab Orders
    fun requestClinicianTest(patient: String, testName: String, instructions: String): Boolean {
        val doctorId = _myDoctorId.value
        if (_role.value != "doctor" || doctorId == null) {
            showToast("Only the assigned demo clinician can request a test")
            return false
        }
        if (_patientProfiles.value[patient]?.careTeamSharingConsent != true) {
            showToast("Patient sharing consent is required before requesting a test")
            return false
        }
        val relatedAppointment = _appointments.value
            .filter {
                it.patient == patient &&
                    it.doctorId == doctorId &&
                    it.status != AppointmentStatus.CANCELLED
            }
            .maxByOrNull { it.date + it.time }
        if (relatedAppointment == null) {
            showToast("A clinician-patient visit is required before requesting a test")
            return false
        }
        if (testName.isBlank()) {
            showToast("Enter the test name")
            return false
        }

        val request = ClinicianTestRequest(
            id = "lr-${System.currentTimeMillis()}",
            patient = patient,
            doctorId = doctorId,
            appointmentId = relatedAppointment.id,
            testName = testName.trim(),
            instructions = instructions.trim(),
            requestedDate = CareData.todayISO()
        )
        _clinicianTestRequests.value = listOf(request) + _clinicianTestRequests.value
        showToast("Test request added to the patient's demo record")
        return true
    }

    fun submitClinicianTestResult(requestId: String, resultText: String): Boolean {
        val request = _clinicianTestRequests.value.find { it.id == requestId } ?: return false
        if (_role.value != "doctor" || _myDoctorId.value != request.doctorId) {
            showToast("Only the requesting demo clinician can submit this result")
            return false
        }
        if (_patientProfiles.value[request.patient]?.careTeamSharingConsent != true) {
            showToast("Patient sharing consent is required to submit a result")
            return false
        }
        if (request.status != LabRequestStatus.PENDING) {
            showToast("A result has already been submitted for this request")
            return false
        }
        if (resultText.isBlank()) {
            showToast("Enter a result summary")
            return false
        }

        _clinicianTestRequests.value = _clinicianTestRequests.value.map {
            if (it.id == requestId) {
                it.copy(
                    status = LabRequestStatus.RESULT_SUBMITTED,
                    resultText = resultText.trim(),
                    resultDate = CareData.todayISO()
                )
            } else it
        }
        showToast("Demo result submitted and visible to the patient")
        return true
    }

    fun bookLabOrder(testName: String, price: Int, date: String, homeCollection: Boolean, address: String?) {
        val total = price + (if (homeCollection) 300 else 0)
        if (_walletBalance.value < total) {
            showToast("Not enough wallet balance. Top up to continue.")
            navigateTo(CareScreen.Wallet)
            return
        }
        _walletBalance.value -= total
        _walletEntries.value = listOf(
            WalletEntry("w-${System.currentTimeMillis()}", "Lab test · $testName", total, CareData.todayISO(), "debit")
        ) + _walletEntries.value

        val newOrder = LabOrder(
            id = "l-${System.currentTimeMillis()}",
            test = testName,
            date = date,
            homeCollection = homeCollection,
            address = address,
            status = "Scheduled",
            price = total
        )
        _labOrders.value = listOf(newOrder) + _labOrders.value
        showToast("Lab test booked")
    }

    // Vitals
    fun saveVitals(readings: List<Vital>) {
        if (readings.isEmpty()) return
        _vitals.value = readings + _vitals.value
        showToast("${readings.size} vital${if (readings.size == 1) "" else "s"} saved together")
    }

    fun removeVital(vitalId: String) {
        _vitals.value = _vitals.value.filter { it.id != vitalId }
    }

    fun isUteBluetoothEnabled(): Boolean = uteWatchRepository.isBluetoothEnabled()

    fun startUteWatchScan() = uteWatchRepository.startScan()

    fun connectToUteWatch(device: UteWatchDevice) = uteWatchRepository.connect(device)

    fun disconnectUteWatch() {
        uteWatchSyncJob?.cancel()
        uteWatchSyncJob = null
        uteWatchRepository.disconnect()
    }

    fun syncUteWatchMetrics() {
        if (uteWatchSyncJob?.isActive == true) return
        uteWatchSyncJob = viewModelScope.launch {
            uteWatchRepository.syncLastSevenDays()
        }
    }

    fun clearUteWatchError() = uteWatchRepository.clearError()

    fun setUteWatchError(message: String) = uteWatchRepository.setError(message)

    // Medications & Prescriptions
    fun updateMedication(prescriptionId: String, medicationId: String, transform: (Medication) -> Medication) {
        _prescriptions.value = _prescriptions.value.map { p ->
            if (p.id == prescriptionId) {
                p.copy(meds = p.meds.map { m -> if (m.id == medicationId) transform(m) else m })
            } else p
        }
    }

    fun toggleMedicationStatus(prescriptionId: String, medicationId: String, newStatus: String) {
        updateMedication(prescriptionId, medicationId) { m ->
            m.copy(
                status = newStatus,
                reminders = if (newStatus == "discontinued") false else m.reminders
            )
        }
        showToast(if (newStatus == "discontinued") "Medication marked discontinued" else "Medication marked active")
    }

    fun savePrescription(
        appointmentId: String,
        diagnosisTitle: String,
        diagnosisCode: String?,
        clinicalNotes: String?,
        meds: List<Medication>,
        safetyAlerts: List<String>,
        safetyAcknowledged: Boolean,
        fromChat: Boolean
    ) {
        val clinicianId = _myDoctorId.value
        if (_role.value != "doctor" || clinicianId == null) {
            showToast("Only the assigned demo clinician can prescribe")
            return
        }
        val appt = _appointments.value.find { it.id == appointmentId }
        if (appt == null || appt.doctorId != clinicianId || appt.status == AppointmentStatus.CANCELLED) {
            showToast("This visit is not available to the current clinician")
            return
        }
        if (_patientProfiles.value[appt.patient]?.careTeamSharingConsent != true) {
            showToast("Patient sharing consent is required before prescribing")
            return
        }
        if (diagnosisTitle.isBlank() || meds.isEmpty() || meds.any {
                it.name.isBlank() || it.dose.isBlank() || it.frequency <= 0 || it.days <= 0
            }
        ) {
            showToast("Complete the diagnosis and valid medication details before saving")
            return
        }
        val hasUnacknowledgedSevereAlert = safetyAlerts.any {
            it.substringBefore(':').trim().equals("severe", ignoreCase = true)
        } && !safetyAcknowledged
        if (hasUnacknowledgedSevereAlert) {
            showToast("Review and acknowledge severe safety alerts before prescribing")
            return
        }

        val newRx = Prescription(
            id = "p-${System.currentTimeMillis()}",
            doctorId = appt.doctorId,
            appointmentId = appt.id,
            date = CareData.todayISO(),
            diagnosis = diagnosisTitle,
            diagnosisCode = diagnosisCode,
            clinicalNotes = clinicalNotes,
            meds = meds,
            safetyAlerts = safetyAlerts,
            safetyAcknowledged = safetyAcknowledged
        )
        _prescriptions.value = listOf(newRx) + _prescriptions.value

        if (fromChat) {
            val medSummary = meds.joinToString("; ") { "${it.name} (${it.dose}, ${it.frequency}×/day for ${it.days} days)" }
            val rxMsg = Message(
                id = "m-${System.currentTimeMillis()}",
                doctorId = appt.doctorId,
                patient = appt.patient,
                from = "doctor",
                text = "\uD83D\uDC8A Prescription sent: $diagnosisTitle. $medSummary. Find it under Meds to set dose reminders.",
                at = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )
            _messages.value = _messages.value + rxMsg
        }

        showToast("Prescription sent to patient")
        if (fromChat) navigateBack() else navigateTo(CareScreen.DocHome)
    }

    // Messaging
    fun sendMessage(doctorId: String, patient: String?, from: String, text: String) {
        if (text.isBlank()) return
        val timeNow = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val msg = Message(
            id = "m-${System.currentTimeMillis()}",
            doctorId = doctorId,
            patient = patient,
            from = from,
            text = text,
            at = timeNow
        )
        _messages.value = _messages.value + msg

        if (from == "patient") {
            viewModelScope.launch {
                delay(1500)
                val reply = Message(
                    id = "m-${System.currentTimeMillis()}",
                    doctorId = doctorId,
                    patient = patient,
                    from = "doctor",
                    text = "Thanks for the update. I'll review this and we can discuss more during our consultation.",
                    at = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                )
                _messages.value = _messages.value + reply
            }
        }
    }

    // Health Records
    fun addHealthRecord(name: String, kind: String, size: String) {
        val newRec = HealthRecord(
            id = "r-${System.currentTimeMillis()}",
            name = name,
            kind = kind,
            size = size,
            date = CareData.todayISO()
        )
        _records.value = listOf(newRec) + _records.value
        showToast("Uploaded — shared with your doctors")
    }

    // Profile & Care-team sharing
    fun toggleCareTeamSharingConsent(patient: String) {
        val current = _patientProfiles.value[patient] ?: PatientMedicalProfile(patient, emptyList(), false)
        val updated = current.copy(careTeamSharingConsent = !current.careTeamSharingConsent)
        _patientProfiles.value = _patientProfiles.value + (patient to updated)
        showToast(if (updated.careTeamSharingConsent) "Care-team sharing enabled" else "Care-team sharing turned off")
    }

    fun addAllergy(patient: String, name: String, reaction: String, severity: String) {
        if (name.isBlank()) return
        val current = _patientProfiles.value[patient] ?: PatientMedicalProfile(patient, emptyList(), false)
        if (current.allergies.any { it.name.equals(name, ignoreCase = true) }) {
            showToast("That allergy is already on this chart")
            return
        }
        val record = PatientAllergy(
            id = "allergy-${System.currentTimeMillis()}",
            name = name,
            reaction = if (reaction.isBlank()) "Not specified" else reaction,
            severity = severity,
            date = CareData.todayISO(),
            recordedBy = _myDoctorId.value ?: "demo-clinician"
        )
        val updated = current.copy(allergies = listOf(record) + current.allergies)
        _patientProfiles.value = _patientProfiles.value + (patient to updated)
        showToast("Allergy added to shared chart")
    }

    fun removeAllergy(patient: String, allergyId: String) {
        val current = _patientProfiles.value[patient] ?: return
        val updated = current.copy(allergies = current.allergies.filter { it.id != allergyId })
        _patientProfiles.value = _patientProfiles.value + (patient to updated)
    }

    // Doctor Actions
    fun registerDoctor(name: String, specialty: String, license: String, years: Int, fee: Int, bio: String) {
        val id = "d${System.currentTimeMillis() % 1000}"
        val formattedName = if (name.startsWith("Dr")) name else "Dr. $name"
        val newDoc = Doctor(
            id = id,
            name = formattedName,
            specialty = specialty,
            photoRes = null,
            years = years,
            fee = fee,
            rating = 5.0f,
            bio = if (bio.isBlank()) "Certified physician." else bio,
            license = license,
            languages = listOf("English", "Swahili"),
            reviews = emptyList()
        )
        _doctors.value = _doctors.value + newDoc
        _myDoctorId.value = id
        _role.value = "doctor"

        val initialAppt = Appointment(
            id = "a-${System.currentTimeMillis()}",
            doctorId = id,
            patient = "Peter Otieno",
            date = CareData.todayISO(),
            time = "17:30",
            type = CallType.VOICE,
            reason = "Headaches for 3 days",
            status = AppointmentStatus.UPCOMING,
            paid = true
        )
        _appointments.value = listOf(initialAppt) + _appointments.value
        showToast("You're listed! Patients can now book you.")
        navigateTo(CareScreen.DocHome)
    }

    fun toggleAvailabilitySlot(day: String, time: String) {
        val id = _myDoctorId.value ?: "doctor"
        val key = "$id:$day"
        val currentList = _availability.value[key] ?: CareData.TIME_SLOTS.take(6)
        val updatedList = if (currentList.contains(time)) {
            currentList.filter { it != time }
        } else {
            (currentList + time).sorted()
        }
        _availability.value = _availability.value + (key to updatedList)
    }

    fun openAllSlots(day: String) {
        val id = _myDoctorId.value ?: "doctor"
        _availability.value = _availability.value + ("$id:$day" to CareData.TIME_SLOTS)
    }

    fun blockAllSlots(day: String) {
        val id = _myDoctorId.value ?: "doctor"
        _availability.value = _availability.value + ("$id:$day" to emptyList())
    }

    fun toggleAcceptingPatients() {
        _acceptingPatients.value = !_acceptingPatients.value
    }

    fun updateDoctorPractice(fee: Int, bio: String) {
        val id = _myDoctorId.value ?: return
        _doctors.value = _doctors.value.map {
            if (it.id == id) it.copy(fee = fee, bio = bio) else it
        }
    }

    // Reminder Ticker (Every 20s checks for medication doses & upcoming visits)
    private fun startReminderTicker() {
        viewModelScope.launch {
            val fired = mutableSetOf<String>()
            while (isActive) {
                delay(20000)
                if (_role.value != "patient") continue
                val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val today = CareData.todayISO()

                // Check medications
                _prescriptions.value.forEach { p ->
                    p.meds.forEach { m ->
                        if (m.status != "discontinued" && m.reminders && m.times.contains(nowTime)) {
                            val key = "${m.id}-$today-$nowTime"
                            if (!fired.contains(key)) {
                                fired.add(key)
                                showToast("\uD83D\uDC8A Time to take ${m.name} (${m.dose})")
                            }
                        }
                    }
                }

                // Check upcoming appointments
                _appointments.value.filter { it.patient == _patientName.value && it.status == AppointmentStatus.UPCOMING && it.date == today }.forEach { a ->
                    val parts = a.time.split(":")
                    if (parts.size == 2) {
                        val h = parts[0].toIntOrNull() ?: 0
                        val mi = parts[1].toIntOrNull() ?: 0
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, h)
                        cal.set(Calendar.MINUTE, mi - 10)
                        val tenMinBefore = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
                        if (tenMinBefore == nowTime && !fired.contains(a.id)) {
                            fired.add(a.id)
                            val doc = _doctors.value.find { it.id == a.doctorId }
                            showToast("\uD83D\uDCDE ${doc?.name ?: "Doctor"} will call you in 10 minutes")
                        }
                    }
                }
            }
        }
    }
}
