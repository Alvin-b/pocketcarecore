package com.example.data.local

import com.example.R
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.model.CallType
import com.example.data.model.DiagnosisOption
import com.example.data.model.Doctor
import com.example.data.model.HealthRecord
import com.example.data.model.LabPackage
import com.example.data.model.Medication
import com.example.data.model.Message
import com.example.data.model.PatientAllergy
import com.example.data.model.PatientMedicalProfile
import com.example.data.model.Prescription
import com.example.data.model.Review
import com.example.data.model.SafetyAlert
import com.example.data.model.Specialty
import com.example.data.model.WalletEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CareData {

    const val VIDEOSDK_PROJECT_ID = "Server-side only"
    const val VIDEOSDK_ORG_NAME = "CareConnect"
    const val VIDEOSDK_DASHBOARD_URL = "https://us.app.videosdk.live/dashboard/GMQXZEI1IBSP"

    fun formatMeetingId(appointmentId: String): String {
        val clean = appointmentId.filter { it.isLetterOrDigit() }.lowercase(Locale.ROOT)
        return "careconnect-$clean"
    }

    @Suppress("UNUSED_PARAMETER")
    fun buildVideoSdkMeetingUrl(
        appointmentId: String,
        name: String = "Participant",
        token: String? = null
    ): String {
        val roomSlug = formatMeetingId(appointmentId)
        val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
        val base = "https://us.app.videosdk.live/v2/meeting/$roomSlug?name=$encodedName"
        return base
    }

    @Suppress("UNUSED_PARAMETER")
    fun buildVideoSdkEmbedUrl(
        appointmentId: String,
        name: String = "Participant",
        token: String? = null,
        micEnabled: Boolean = true,
        webcamEnabled: Boolean = true
    ): String {
        val roomSlug = formatMeetingId(appointmentId)
        val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
        val base = "https://embed.videosdk.live/rtc-js-prebuilt/0.3.43/?meetingId=$roomSlug&name=$encodedName&micEnabled=$micEnabled&webcamEnabled=$webcamEnabled&theme=DARK"
        return base
    }

    fun todayISO(offsetDays: Int = 0): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, offsetDays)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(cal.time)
    }

    fun defaultTimes(frequency: Int): List<String> {
        return when (frequency) {
            1 -> listOf("08:00")
            2 -> listOf("08:00", "20:00")
            3 -> listOf("08:00", "14:00", "20:00")
            4 -> listOf("07:00", "12:00", "17:00", "22:00")
            else -> listOf("08:00")
        }
    }

    fun formatKES(n: Int): String {
        return "KES %,d".format(Locale.US, n)
    }

    fun initials(name: String): String {
        return name.replace("Dr. ", "").split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().toString() }
            .joinToString("")
    }

    val SPECIALTIES = listOf(
        Specialty("General", "Stethoscope"),
        Specialty("Pediatrics", "Baby"),
        Specialty("Cardiology", "HeartPulse"),
        Specialty("Dermatology", "Sparkles"),
        Specialty("Mental Health", "Brain"),
        Specialty("Gynecology", "Flower2")
    )

    val TIME_SLOTS = listOf(
        "09:00", "10:00", "11:30", "13:00", "14:30", "16:00", "17:30", "19:00"
    )

    val LAB_PACKAGES = listOf(
        LabPackage("Full blood count", 850),
        LabPackage("Diabetes screen", 1200),
        LabPackage("Lipid profile", 1500),
        LabPackage("Thyroid panel", 2200),
        LabPackage("Liver function", 1800),
        LabPackage("Kidney function", 1600),
        LabPackage("Vitamin D", 2500),
        LabPackage("Pregnancy test", 700)
    )

    val DOCTORS: List<Doctor> = listOf(
        Doctor(
            id = "d1",
            name = "Dr. Amina Wanjiru",
            specialty = "General",
            photoRes = R.drawable.doc1,
            years = 9,
            fee = 1500,
            rating = 4.9f,
            bio = "Family physician focused on everyday illnesses, chronic care and preventive health.",
            license = "KMPDC-A10293",
            languages = listOf("English", "Swahili"),
            reviews = listOf(
                Review("Brian K.", 5, "Very patient and explained everything clearly."),
                Review("Faith M.", 5, "Felt better within two days. Highly recommend.")
            )
        ),
        Doctor(
            id = "d2",
            name = "Dr. Rajesh Patel",
            specialty = "Cardiology",
            photoRes = R.drawable.doc2,
            years = 18,
            fee = 3500,
            rating = 4.8f,
            bio = "Consultant cardiologist. Blood pressure, chest pain, heart rhythm and follow-ups.",
            license = "KMPDC-C55120",
            languages = listOf("English", "Hindi", "Swahili"),
            reviews = listOf(
                Review("Grace O.", 5, "Thorough review of my ECG results.")
            )
        ),
        Doctor(
            id = "d3",
            name = "Dr. Samuel Tesfaye",
            specialty = "Mental Health",
            photoRes = R.drawable.doc3,
            years = 7,
            fee = 2500,
            rating = 4.9f,
            bio = "Psychiatrist supporting anxiety, depression, sleep problems and stress.",
            license = "KMPDC-P77341",
            languages = listOf("English", "Amharic"),
            reviews = listOf(
                Review("Anon", 5, "Safe space, never felt rushed.")
            )
        ),
        Doctor(
            id = "d4",
            name = "Dr. Claire Hoffmann",
            specialty = "Dermatology",
            photoRes = R.drawable.doc4,
            years = 12,
            fee = 2800,
            rating = 4.7f,
            bio = "Skin, hair and nail conditions. Acne, eczema, rashes and mole checks by video.",
            license = "KMPDC-D30118",
            languages = listOf("English", "German"),
            reviews = listOf(
                Review("Ivy N.", 4, "Great advice for my eczema.")
            )
        ),
        Doctor(
            id = "d5",
            name = "Dr. Mercy Achieng",
            specialty = "Pediatrics",
            photoRes = null,
            years = 11,
            fee = 2000,
            rating = 4.8f,
            bio = "Children's doctor — fevers, coughs, growth and vaccinations guidance.",
            license = "KMPDC-K44871",
            languages = listOf("English", "Swahili", "Luo"),
            reviews = emptyList()
        ),
        Doctor(
            id = "d6",
            name = "Dr. Lilian Mutua",
            specialty = "Gynecology",
            photoRes = null,
            years = 14,
            fee = 3000,
            rating = 4.9f,
            bio = "Women's health, pregnancy check-ins, family planning and cycle concerns.",
            license = "KMPDC-G21904",
            languages = listOf("English", "Swahili"),
            reviews = emptyList()
        )
    )

    val LOCAL_DIAGNOSES = listOf(
        DiagnosisOption("CA23", "Asthma"),
        DiagnosisOption("5A11", "Type 2 diabetes mellitus"),
        DiagnosisOption("5A10", "Type 1 diabetes mellitus"),
        DiagnosisOption("BA00", "Essential hypertension"),
        DiagnosisOption("8A80", "Migraine"),
        DiagnosisOption("8A81", "Tension-type headache"),
        DiagnosisOption("MD12", "Cough"),
        DiagnosisOption("EA80", "Atopic eczema"),
        DiagnosisOption("ED80", "Acne"),
        DiagnosisOption("6A70", "Single episode depressive disorder"),
        DiagnosisOption("6A71", "Recurrent depressive disorder"),
        DiagnosisOption("6B00", "Generalised anxiety disorder"),
        DiagnosisOption("7A00", "Chronic insomnia"),
        DiagnosisOption("DA42", "Gastritis"),
        DiagnosisOption("DA22", "Gastro-oesophageal reflux disease"),
        DiagnosisOption("1A07", "Typhoid fever"),
        DiagnosisOption("1B10", "Tuberculosis"),
        DiagnosisOption("RA01", "COVID-19"),
        DiagnosisOption("3A00", "Iron deficiency anaemia"),
        DiagnosisOption("5A00", "Hypothyroidism"),
        DiagnosisOption("", "Malaria"),
        DiagnosisOption("", "Urinary tract infection"),
        DiagnosisOption("", "Pneumonia"),
        DiagnosisOption("", "Acute upper respiratory tract infection"),
        DiagnosisOption("", "Acute gastroenteritis"),
        DiagnosisOption("", "Influenza"),
        DiagnosisOption("", "Low back pain"),
        DiagnosisOption("", "Fever"),
        DiagnosisOption("", "Contact dermatitis"),
        DiagnosisOption("", "Tonsillitis"),
        DiagnosisOption("", "Sinusitis"),
        DiagnosisOption("", "Otitis media"),
        DiagnosisOption("", "Conjunctivitis"),
        DiagnosisOption("", "Allergic rhinitis"),
        DiagnosisOption("", "Skin rash"),
        DiagnosisOption("", "Fungal skin infection"),
        DiagnosisOption("", "Peptic ulcer disease"),
        DiagnosisOption("", "Constipation"),
        DiagnosisOption("", "Diarrhoea")
    )

    fun searchLocalDiagnoses(term: String, limit: Int = 8): List<DiagnosisOption> {
        val q = term.lowercase().trim()
        if (q.length < 2) return emptyList()
        val words = q.split("\\s+".toRegex())
        return LOCAL_DIAGNOSES
            .filter { d ->
                d.code.lowercase().startsWith(q) || words.all { w -> d.title.lowercase().contains(w) }
            }
            .sortedByDescending { it.title.lowercase().startsWith(q) }
            .take(limit)
    }

    val FORMULARY_MEDICINES = listOf(
        "Paracetamol 500mg tablet", "Ibuprofen 400mg tablet", "Diclofenac 50mg tablet",
        "Amoxicillin 500mg capsule", "Azithromycin 500mg tablet", "Cefalexin 500mg capsule",
        "Cotrimoxazole 960mg tablet", "Ciprofloxacin 500mg tablet", "Doxycycline 100mg capsule",
        "Metronidazole 400mg tablet", "Artemether/Lumefantrine 20/120mg tablet",
        "Fluconazole 150mg capsule", "Clotrimazole 1% cream", "Hydrocortisone 1% cream",
        "Cetirizine 10mg tablet", "Loratadine 10mg tablet", "Chlorphenamine 4mg tablet",
        "Salbutamol 100mcg inhaler", "Prednisolone 5mg tablet", "Omeprazole 20mg capsule",
        "Oral rehydration salts sachet", "Zinc sulfate 20mg dispersible tablet",
        "Loperamide 2mg capsule", "Amlodipine 5mg tablet", "Enalapril 10mg tablet",
        "Losartan 50mg tablet", "Hydrochlorothiazide 25mg tablet", "Atorvastatin 20mg tablet",
        "Metformin 500mg tablet", "Glibenclamide 5mg tablet", "Folic acid 5mg tablet",
        "Ferrous sulfate 200mg tablet", "Vitamin D3 1000IU tablet", "Fluoxetine 20mg capsule",
        "Sertraline 50mg tablet", "Amitriptyline 25mg tablet", "Levothyroxine 50mcg tablet",
        "Sumatriptan 50mg tablet", "Cough syrup (guaifenesin)", "Eye drops (chloramphenicol 0.5%)"
    )

    private val DRUG_GROUPS = mapOf(
        "penicillin" to Pair("penicillins", listOf("penicillin", "amoxicillin", "ampicillin", "flucloxacillin", "cloxacillin", "piperacillin", "co-amoxiclav", "augmentin", "amoxyclav")),
        "cephalosporin" to Pair("cephalosporins", listOf("cef", "ceph", "cipro-ceph")),
        "sulfa" to Pair("sulfonamides (sulfa drugs)", listOf("sulfa", "sulpha", "cotrimoxazole", "co-trimoxazole", "septrin", "bactrim", "sulfamethoxazole")),
        "nsaid" to Pair("NSAIDs", listOf("ibuprofen", "diclofenac", "naproxen", "aspirin", "ketoprofen", "indomethacin", "piroxicam", "meloxicam", "celecoxib", "ketorolac", "brufen", "voltaren")),
        "opioid" to Pair("opioids", listOf("codeine", "tramadol", "morphine", "pethidine", "oxycodone")),
        "macrolide" to Pair("macrolides", listOf("erythromycin", "azithromycin", "clarithromycin")),
        "quinolone" to Pair("fluoroquinolones", listOf("ciprofloxacin", "levofloxacin", "ofloxacin", "norfloxacin")),
        "acei" to Pair("ACE inhibitors", listOf("enalapril", "lisinopril", "captopril", "ramipril", "perindopril")),
        "arb" to Pair("ARBs", listOf("losartan", "valsartan", "telmisartan", "candesartan", "irbesartan")),
        "ssri" to Pair("SSRIs", listOf("fluoxetine", "sertraline", "citalopram", "escitalopram", "paroxetine")),
        "nitrate" to Pair("nitrates", listOf("isosorbide", "glyceryl trinitrate", "nitroglycerin", "gtn")),
        "pde5" to Pair("PDE-5 inhibitors", listOf("sildenafil", "tadalafil", "vardenafil")),
        "anticoag" to Pair("warfarin", listOf("warfarin")),
        "statin" to Pair("statins", listOf("simvastatin", "atorvastatin", "lovastatin")),
        "potassium" to Pair("potassium-raising agents", listOf("potassium chloride", "spironolactone", "amiloride", "eplerenone")),
        "methotrexate" to Pair("methotrexate", listOf("methotrexate")),
        "paracetamol" to Pair("paracetamol", listOf("paracetamol", "acetaminophen", "panadol"))
    )

    private val PAIRS = listOf(
        Triple("anticoag", "nsaid", Pair("severe", "Warfarin with an NSAID raises bleeding risk substantially.")),
        Triple("anticoag", "macrolide", Pair("moderate", "Macrolides can increase warfarin effect; monitor INR.")),
        Triple("anticoag", "quinolone", Pair("moderate", "Fluoroquinolones can increase warfarin effect; monitor INR.")),
        Triple("anticoag", "sulfa", Pair("severe", "Co-trimoxazole strongly increases warfarin effect and bleeding risk.")),
        Triple("pde5", "nitrate", Pair("severe", "PDE-5 inhibitors with nitrates can cause dangerous hypotension. Avoid.")),
        Triple("methotrexate", "sulfa", Pair("severe", "Co-trimoxazole with methotrexate can cause severe bone-marrow toxicity.")),
        Triple("methotrexate", "nsaid", Pair("moderate", "NSAIDs can reduce methotrexate clearance and increase toxicity.")),
        Triple("acei", "potassium", Pair("severe", "ACE inhibitor with potassium-raising agents risks hyperkalaemia.")),
        Triple("arb", "potassium", Pair("severe", "ARB with potassium-raising agents risks hyperkalaemia.")),
        Triple("acei", "nsaid", Pair("moderate", "NSAIDs can blunt ACE inhibitors and harm kidney function.")),
        Triple("arb", "nsaid", Pair("moderate", "NSAIDs can blunt ARBs and harm kidney function.")),
        Triple("statin", "macrolide", Pair("moderate", "Clarithromycin or erythromycin with simvastatin or atorvastatin raises myopathy risk.")),
        Triple("ssri", "opioid", Pair("moderate", "Tramadol with SSRIs can cause serotonin syndrome and lowers seizure threshold.")),
        Triple("ssri", "nsaid", Pair("moderate", "SSRIs with NSAIDs increase gastrointestinal bleeding risk."))
    )

    private fun norm(s: String): String = s.lowercase().replace("\\s+".toRegex(), " ").trim()

    private fun groupsOf(name: String): List<String> {
        val n = norm(name)
        return DRUG_GROUPS.filter { (_, v) -> v.second.any { n.contains(it) } }.keys.toList()
    }

    fun checkPrescription(
        newMeds: List<Medication>,
        allergies: List<PatientAllergy>,
        currentMeds: List<Medication> = emptyList()
    ): List<SafetyAlert> {
        val alerts = mutableListOf<SafetyAlert>()
        val names = newMeds.map { it.name.trim() }.filter { it.isNotBlank() }

        names.forEachIndexed { i, name ->
            val gs = groupsOf(name)
            allergies.forEach { al ->
                val an = norm(al.name)
                val direct = norm(name).contains(an) || an.contains(norm(name))
                val allergyGroups = DRUG_GROUPS.filter { (_, g) -> g.second.any { an.contains(it) || it.contains(an) } }.keys
                val sameClass = allergyGroups.any { gs.contains(it) }
                val cross = (allergyGroups.contains("penicillin") && gs.contains("cephalosporin")) ||
                        (allergyGroups.contains("cephalosporin") && gs.contains("penicillin"))

                if (direct || sameClass) {
                    val lvl = if (al.severity == "Mild") "moderate" else "severe"
                    alerts.add(
                        SafetyAlert(
                            id = "al-$i-${al.id}",
                            level = lvl,
                            kind = "allergy",
                            title = "Allergy: $name",
                            detail = "Patient has a recorded ${al.severity.lowercase()} allergy to ${al.name}${if (al.reaction.isNotBlank()) " (${al.reaction})" else ""}."
                        )
                    )
                } else if (cross) {
                    alerts.add(
                        SafetyAlert(
                            id = "ax-$i-${al.id}",
                            level = "moderate",
                            kind = "allergy",
                            title = "Possible cross-reactivity: $name",
                            detail = "Recorded allergy to ${al.name}. Penicillins and cephalosporins can cross-react (low but real risk)."
                        )
                    )
                }
            }
        }

        val allItems = names.map { Pair(it, "new") } + currentMeds.map { Pair(it.name, "current") }
        for (i in allItems.indices) {
            for (j in (i + 1) until allItems.size) {
                val a = allItems[i]
                val b = allItems[j]
                if (a.second == "current" && b.second == "current") continue
                val ga = groupsOf(a.first)
                val gb = groupsOf(b.first)
                val where = if (a.second == "current" || b.second == "current") " (one is already in active medicines)" else ""

                if (norm(a.first) == norm(b.first)) {
                    alerts.add(
                        SafetyAlert(
                            id = "du-$i-$j",
                            level = "moderate",
                            kind = "duplicate",
                            title = "Duplicate: ${a.first}",
                            detail = "The same medicine appears twice$where."
                        )
                    )
                    continue
                }

                for (pair in PAIRS) {
                    val x = pair.first
                    val y = pair.second
                    val lvl = pair.third.first
                    val text = pair.third.second
                    if ((ga.contains(x) && gb.contains(y)) || (ga.contains(y) && gb.contains(x))) {
                        alerts.add(
                            SafetyAlert(
                                id = "in-$i-$j-$x-$y",
                                level = lvl,
                                kind = "interaction",
                                title = "${a.first} + ${b.first}",
                                detail = "$text$where"
                            )
                        )
                    }
                }

                val shared = ga.filter { gb.contains(it) && listOf("nsaid", "ssri", "opioid", "paracetamol", "acei", "arb", "statin").contains(it) }
                shared.forEach { g ->
                    alerts.add(
                        SafetyAlert(
                            id = "dt-$i-$j-$g",
                            level = "moderate",
                            kind = "duplicate",
                            title = "Duplicate therapy: ${a.first} + ${b.first}",
                            detail = "Two ${DRUG_GROUPS[g]?.first} together$where. Check this is intended."
                        )
                    )
                }
            }
        }

        return alerts.sortedBy { alert ->
            when (alert.level) {
                "severe" -> 0
                "moderate" -> 1
                else -> 2
            }
        }
    }

    fun initialAppointments(): List<Appointment> = listOf(
        Appointment(
            id = "a1",
            doctorId = "d1",
            patient = "Joy Kamau",
            date = todayISO(),
            time = "16:00",
            type = CallType.VIDEO,
            reason = "Persistent cough and mild fever",
            status = AppointmentStatus.UPCOMING,
            paid = true
        ),
        Appointment(
            id = "a0",
            doctorId = "d4",
            patient = "Joy Kamau",
            date = todayISO(-6),
            time = "11:30",
            type = CallType.VIDEO,
            reason = "Skin rash on arms",
            status = AppointmentStatus.COMPLETED,
            paid = true,
            rated = true
        )
    )

    fun initialPrescriptions(): List<Prescription> = listOf(
        Prescription(
            id = "p0",
            doctorId = "d4",
            appointmentId = "a0",
            date = todayISO(-6),
            diagnosis = "Contact dermatitis",
            meds = listOf(
                Medication(
                    id = "m1",
                    name = "Hydrocortisone 1% cream",
                    dose = "Thin layer",
                    frequency = 2,
                    days = 10,
                    notes = "Apply to affected skin",
                    times = defaultTimes(2),
                    reminders = true
                ),
                Medication(
                    id = "m2",
                    name = "Cetirizine 10mg",
                    dose = "1 tablet",
                    frequency = 1,
                    days = 7,
                    notes = "At night, may cause drowsiness",
                    times = listOf("21:00"),
                    reminders = true
                )
            )
        )
    )

    fun initialMessages(): List<Message> = listOf(
        Message(id = "x1", doctorId = "d4", from = "doctor", text = "Hi Joy, how is the rash today?", at = "09:12"),
        Message(id = "x2", doctorId = "d4", from = "patient", text = "Much better, the itching has reduced.", at = "09:20"),
        Message(id = "x3", doctorId = "d1", patient = "Joy Kamau", from = "patient", text = "Good morning doctor. The cough is now on day three and I still get mild fevers at night.", at = "08:40")
    )

    fun initialProfile(): PatientMedicalProfile = PatientMedicalProfile(
        patient = "Joy Kamau",
        careTeamSharingConsent = true,
        allergies = listOf(
            PatientAllergy(
                id = "allergy-joy-1",
                name = "Penicillin",
                reaction = "Hives",
                severity = "Severe",
                date = todayISO(-20),
                recordedBy = "d4"
            )
        )
    )

    fun initialRecords(): List<HealthRecord> = listOf(
        HealthRecord(id = "r1", name = "Full blood count.pdf", kind = "Lab result", size = "240 KB", date = todayISO(-20))
    )

    fun initialWalletEntries(): List<WalletEntry> = listOf(
        WalletEntry(id = "w0", label = "Welcome credit", amount = 2500, date = todayISO(), kind = "credit")
    )
}
