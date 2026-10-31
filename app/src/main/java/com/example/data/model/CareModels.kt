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
    val route: String = "Oral", // "Oral", "Inhalation", "Topical", "Sublingual", "Intravenous"
    val instructions: String = "Take after meals",
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
    val safetyAcknowledged: Boolean = false,
    val kmpdcRegNumber: String = "KMPDC-A10293",
    val doctorName: String = "Dr. Amina Wanjiru",
    val specialty: String = "Family Physician",
    val facilityName: String = "CareConnect Medical Centre",
    val facilityLicense: String = "KMPDC-HF-0492",
    val rxSerialNumber: String? = null,
    val digitalSignatureHash: String? = null,
    val signedAt: String? = null,
    val isEncrypted: Boolean = true,
    val encryptionAlgorithm: String = "AES-256-GCM",
    val secondaryIcd11Codes: List<String> = emptyList()
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
    val date: String,
    val icd11Code: String? = null,
    val icd11Title: String? = null,
    val isEncrypted: Boolean = true,
    val encryptionStandard: String = "AES-256-GCM"
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

enum class LabRequestStatus {
    PENDING,
    RESULT_SUBMITTED
}

data class ClinicianTestRequest(
    val id: String,
    val patient: String,
    val doctorId: String,
    val appointmentId: String?,
    val testName: String,
    val instructions: String,
    val requestedDate: String,
    val status: LabRequestStatus = LabRequestStatus.PENDING,
    val resultText: String? = null,
    val resultDate: String? = null
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
    val title: String,
    val chapter: String = "",
    val category: String = "",
    val description: String = ""
)

data class SafetyAlert(
    val id: String,
    val level: String, // "severe", "moderate", "info"
    val kind: String, // "allergy", "interaction", "duplicate"
    val title: String,
    val detail: String
)

data class MedicalFacility(
    val id: String,
    val name: String,
    val license: String,
    val type: String, // "Hospital & Medical Center", "Specialized Clinic", "Diagnostic Center & Lab", "Pharmacy"
    val address: String,
    val phone: String,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val emergencyAvailable: Boolean = true,
    val accreditation: String = "MOH & KMPDC Certified Level 4 Facility",
    val rating: Double = 4.8,
    val reviewCount: Int = 340,
    val openHours: String = "24/7 Always Open"
)

data class FacilityStaffMember(
    val id: String,
    val name: String,
    val role: String,
    val license: String,
    val department: String,
    val isOnDuty: Boolean = true,
    val currentPatients: Int = 0
)

data class PharmacyDispenseOrder(
    val id: String,
    val prescriptionId: String,
    val patientName: String,
    val doctorName: String,
    val date: String,
    val medicationSummary: String,
    val status: String = "Ready for pickup", // "Pending", "Ready for pickup", "Dispensed"
    val dosageNotes: String
)

data class FacilityDepartment(
    val id: String,
    val name: String,
    val description: String,
    val headDoctorName: String,
    val activeStaffCount: Int,
    val waitTimeMinutes: Int,
    val iconKey: String = "stethoscope"
)

enum class FacilityVisitType {
    TELEMEDICINE_POD,
    IN_PERSON_CLINIC,
    EMERGENCY_TRIAGE
}

enum class TriagePriority {
    ROUTINE,
    URGENT,
    EMERGENCY
}

data class FacilityBooking(
    val id: String,
    val patientName: String,
    val departmentId: String,
    val departmentName: String,
    val assignedStaffName: String?,
    val date: String,
    val time: String,
    val visitType: FacilityVisitType,
    val priority: TriagePriority,
    val reason: String,
    val status: String = "Checked In", // "Scheduled", "Checked In", "In Consultation", "Completed"
    val consultationRoomId: String? = null
)

data class HospitalReferral(
    val id: String,
    val patientName: String,
    val fromDepartment: String,
    val referringClinician: String,
    val toDepartment: String,
    val targetClinician: String,
    val urgency: String, // "Routine", "Urgent", "STAT Emergency"
    val clinicalReason: String,
    val summaryNotes: String,
    val date: String,
    val status: String = "Pending Review" // "Pending Review", "Accepted", "Completed"
)

data class HospitalProcedureOrder(
    val id: String,
    val patientName: String,
    val orderingClinician: String,
    val department: String,
    val procedureName: String,
    val category: String, // "Diagnostic Laboratory", "Radiology / Imaging", "Procedure Suite", "Specialist Assessment"
    val urgency: String, // "Routine", "STAT Urgent"
    val clinicalIndication: String,
    val status: String = "Ordered", // "Ordered", "Sample Collected", "In Analysis", "Results Completed"
    val resultSummary: String? = null,
    val orderedDate: String,
    val completedDate: String? = null
)

data class HospitalFollowUp(
    val id: String,
    val patientName: String,
    val attendingDoctor: String,
    val dischargeDiagnosis: String,
    val followUpDate: String,
    val followUpType: String, // "Telehealth Video Consultation", "Vitals Telemetry Review", "Medication Adherence Check"
    val status: String = "Due Today", // "Scheduled", "Due Today", "Completed"
    val clinicalProtocol: String,
    val linkedAppointmentId: String? = null
)

data class HospitalWardBed(
    val id: String,
    val bedCode: String,
    val wardName: String,
    val isOccupied: Boolean,
    val patientName: String? = null,
    val admittedDate: String? = null,
    val attendingClinician: String? = null
)

data class FacilityService(
    val id: String,
    val facilityId: String,
    val name: String,
    val category: String, // "Consultation", "Diagnostic & Lab", "Imaging & Radiology", "Inpatient & Wards", "Specialist Assessment"
    val description: String,
    val price: Double, // in KSh
    val durationMinutes: Int = 30,
    val isTelehealthAvailable: Boolean = true
)

data class FacilityMedication(
    val id: String,
    val sellerId: String, // facilityId or chemistId
    val sellerName: String,
    val name: String,
    val genericName: String,
    val dosage: String,
    val price: Double,
    val inStock: Boolean = true,
    val category: String,
    val requiresPrescription: Boolean = false,
    val description: String = ""
)

data class ChemistPharmacy(
    val id: String,
    val name: String,
    val license: String,
    val address: String,
    val phone: String,
    val rating: Double = 4.8,
    val reviewCount: Int = 120,
    val deliveryTimeMinutes: Int = 30,
    val isOpen247: Boolean = true,
    val expressDelivery: Boolean = true,
    val deliveryFee: Double = 150.0
)

data class ChemistPrescriptionOrder(
    val id: String,
    val prescriptionId: String,
    val patientName: String,
    val patientPhone: String,
    val chemistId: String,
    val chemistName: String,
    val doctorName: String,
    val medicationsSummary: String,
    val totalAmount: Double,
    val paymentMethod: String = "M-Pesa",
    val paymentStatus: String = "Paid",
    val fulfillmentStatus: String = "Sent to Chemist", // "Sent to Chemist", "Preparing", "Out for Delivery", "Ready for Pickup", "Delivered"
    val deliveryType: String = "Home Delivery", // "Home Delivery", "Counter Pickup"
    val deliveryAddress: String = "Nairobi CBD",
    val orderDate: String
)

