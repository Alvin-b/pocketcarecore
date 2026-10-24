package com.example.data.model

data class Review(
    val author: String,
    val rating: Int,
    val text: String
)

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val photoRes: Int? = null,
    val years: Int,
    val fee: Int,
    val rating: Float,
    val bio: String,
    val license: String,
    val languages: List<String>,
    val reviews: List<Review>
)

enum class CallType {
    VIDEO,
    VOICE
}

enum class AppointmentStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}

data class Appointment(
    val id: String,
    val doctorId: String,
    val patient: String,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:MM
    val type: CallType,
    val reason: String,
    val status: AppointmentStatus,
    val paid: Boolean,
    val rated: Boolean = false
)

data class Medication(
    val id: String,
    val name: String,
    val status: String = "active", // "active" or "discontinued"
    val dose: String,
    val frequency: Int,
    val days: Int,
    val notes: String = "",
    val times: List<String> = emptyList(),
    val reminders: Boolean = true,
    val taken: List<String> = emptyList() // "YYYY-MM-DD HH:MM"
)

data class Prescription(
    val id: String,
    val doctorId: String,
    val appointmentId: String,
    val date: String,
    val diagnosis: String,
    val diagnosisCode: String? = null,
    val clinicalNotes: String? = null,
    val meds: List<Medication>,
    val safetyAlerts: List<String> = emptyList(),
    val safetyAcknowledged: Boolean = false
)

data class Message(
    val id: String,
    val doctorId: String,
    val patient: String? = null,
    val from: String, // "patient" or "doctor"
    val text: String,
    val at: String
)

data class HealthRecord(
    val id: String,
    val name: String,
    val kind: String,
    val size: String,
    val date: String
)

data class Vital(
    val id: String,
    val kind: String, // "Blood pressure", "Glucose", "Temperature", "Weight", "Heart rate"
    val value: String,
    val date: String,
    val note: String = ""
)

data class LabPackage(
    val name: String,
    val price: Int
)

data class LabOrder(
    val id: String,
    val test: String,
    val date: String,
    val homeCollection: Boolean,
    val address: String? = null,
    val status: String = "Scheduled",
    val price: Int
)

data class WalletEntry(
    val id: String,
    val label: String,
    val amount: Int,
    val date: String,
    val kind: String // "credit" or "debit"
)

data class PatientAllergy(
    val id: String,
    val name: String,
    val reaction: String,
    val severity: String, // "Mild", "Moderate", "Severe", "Unknown"
    val date: String,
    val recordedBy: String
)

data class PatientMedicalProfile(
    val patient: String,
    val allergies: List<PatientAllergy>,
    val careTeamSharingConsent: Boolean
)

data class Specialty(
    val name: String,
    val icon: String
)

data class DiagnosisOption(
    val code: String,
    val title: String
)

data class SafetyAlert(
    val id: String,
    val level: String, // "severe", "moderate", "info"
    val kind: String, // "allergy", "interaction", "duplicate"
    val title: String,
    val detail: String
)
