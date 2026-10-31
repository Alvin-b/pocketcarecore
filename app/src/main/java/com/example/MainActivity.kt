package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CareScreen
import com.example.ui.CareViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TelePrimary
import com.example.ui.theme.TelePrimarySoft

class MainActivity : ComponentActivity() {
    private val viewModel: CareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CareConnectApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CareConnectApp(viewModel: CareViewModel) {
    val role by viewModel.role.collectAsState()
    val screenStack by viewModel.screenStack.collectAsState()
    val toastMessage by viewModel.toast.collectAsState()
    val currentScreen = screenStack.lastOrNull() ?: CareScreen.Home
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    val isRootPatientTab = role == "patient" && (
            currentScreen is CareScreen.Home ||
            currentScreen is CareScreen.Appointments ||
            currentScreen is CareScreen.Meds ||
            currentScreen is CareScreen.Chats ||
            currentScreen is CareScreen.Profile
    )

    val isRootDoctorTab = role == "doctor" && (
            currentScreen is CareScreen.DocHome ||
            currentScreen is CareScreen.DocPatients ||
            currentScreen is CareScreen.DocChats ||
            currentScreen is CareScreen.DocSchedule ||
            currentScreen is CareScreen.DocSettings
    )

    val isRootFacilityTab = role == "facility" && (
            currentScreen is CareScreen.FacilityHome ||
            currentScreen is CareScreen.FacilityStaff ||
            currentScreen is CareScreen.FacilityPharmacy ||
            currentScreen is CareScreen.FacilityLabs ||
            currentScreen is CareScreen.FacilitySettings
    )

    val showTabs = isRootPatientTab || isRootDoctorTab || isRootFacilityTab

    BackHandler(enabled = screenStack.size > 1) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = if (showTabs) 80.dp else 16.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            )
        },
        bottomBar = {
            if (showTabs) {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    if (role == "patient") {
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.Home,
                            onClick = { viewModel.navigateTo(CareScreen.Home) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_home")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.Appointments,
                            onClick = { viewModel.navigateTo(CareScreen.Appointments) },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Visits") },
                            label = { Text("Visits", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_visits")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.Meds,
                            onClick = { viewModel.navigateTo(CareScreen.Meds) },
                            icon = { Icon(Icons.Default.Medication, contentDescription = "Meds") },
                            label = { Text("Meds", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_meds")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.Chats,
                            onClick = { viewModel.navigateTo(CareScreen.Chats) },
                            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
                            label = { Text("Chat", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_chat")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.Profile,
                            onClick = { viewModel.navigateTo(CareScreen.Profile) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Me") },
                            label = { Text("Me", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_me")
                        )
                    } else if (role == "doctor") {
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.DocHome,
                            onClick = { viewModel.navigateTo(CareScreen.DocHome) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_doc_home")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.DocPatients,
                            onClick = { viewModel.navigateTo(CareScreen.DocPatients) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Patients") },
                            label = { Text("Patients", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_doc_patients")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.DocChats,
                            onClick = { viewModel.navigateTo(CareScreen.DocChats) },
                            icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") },
                            label = { Text("Chats", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_doc_chats")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.DocSchedule,
                            onClick = { viewModel.navigateTo(CareScreen.DocSchedule) },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Schedule") },
                            label = { Text("Schedule", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_doc_schedule")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.DocSettings,
                            onClick = { viewModel.navigateTo(CareScreen.DocSettings) },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Practice") },
                            label = { Text("Practice", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_doc_practice")
                        )
                    } else if (role == "facility") {
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.FacilityHome,
                            onClick = { viewModel.navigateTo(CareScreen.FacilityHome) },
                            icon = { Icon(Icons.Default.Apartment, contentDescription = "Facility") },
                            label = { Text("Facility", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_fac_home")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.FacilityStaff,
                            onClick = { viewModel.navigateTo(CareScreen.FacilityStaff) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Staff") },
                            label = { Text("Staff", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_fac_staff")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.FacilityPharmacy,
                            onClick = { viewModel.navigateTo(CareScreen.FacilityPharmacy) },
                            icon = { Icon(Icons.Default.LocalPharmacy, contentDescription = "Pharmacy") },
                            label = { Text("Pharmacy", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_fac_pharmacy")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.FacilityLabs,
                            onClick = { viewModel.navigateTo(CareScreen.FacilityLabs) },
                            icon = { Icon(Icons.Default.Science, contentDescription = "Labs") },
                            label = { Text("Labs", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_fac_labs")
                        )
                        NavigationBarItem(
                            selected = currentScreen is CareScreen.FacilitySettings,
                            onClick = { viewModel.navigateTo(CareScreen.FacilitySettings) },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Admin") },
                            label = { Text("Admin", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = TelePrimarySoft,
                                selectedIconColor = TelePrimary,
                                selectedTextColor = TelePrimary
                            ),
                            modifier = Modifier.testTag("tab_fac_settings")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(
                    if (currentScreen is CareScreen.Login || currentScreen is CareScreen.Welcome || currentScreen is CareScreen.Call) Modifier
                    else Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
        ) {
            when (val s = currentScreen) {
                is CareScreen.Login -> LoginScreen(viewModel = viewModel)
                is CareScreen.Welcome -> WelcomeScreen(viewModel = viewModel)
                is CareScreen.Home -> PatientHomeScreen(viewModel = viewModel)
                is CareScreen.Doctors -> DoctorsListScreen(viewModel = viewModel, initialSpecialty = s.specialty)
                is CareScreen.DoctorProfile -> DoctorProfileScreen(viewModel = viewModel, doctorId = s.id)
                is CareScreen.Book -> BookAppointmentScreen(viewModel = viewModel, doctorId = s.id, prefillReason = s.prefill)
                is CareScreen.Pay -> PaymentScreen(viewModel = viewModel, appointment = s.appointment)
                is CareScreen.Appointments -> AppointmentsScreen(viewModel = viewModel)
                is CareScreen.Call -> CallConsultationScreen(viewModel = viewModel, appointmentId = s.appointmentId)
                is CareScreen.Rate -> RateScreen(viewModel = viewModel, appointmentId = s.appointmentId)
                is CareScreen.Meds -> MedsScreen(viewModel = viewModel)
                is CareScreen.Chats -> ChatsListScreen(viewModel = viewModel)
                is CareScreen.Chat -> ChatScreen(viewModel = viewModel, doctorId = s.doctorId)
                is CareScreen.Records -> RecordsScreen(viewModel = viewModel)
                is CareScreen.Profile -> ProfileScreen(viewModel = viewModel)
                is CareScreen.DocRegister -> DocRegisterScreen(viewModel = viewModel)
                is CareScreen.DocHome -> DocHomeScreen(viewModel = viewModel)
                is CareScreen.DocPatients -> DocPatientsScreen(viewModel = viewModel)
                is CareScreen.DocPatient -> DocPatientChartScreen(viewModel = viewModel, patient = s.patient)
                is CareScreen.DocChats -> ChatsListScreen(viewModel = viewModel, isDoctor = true)
                is CareScreen.DocChat -> {
                    val doctorId = viewModel.myDoctorId.collectAsState().value
                    if (doctorId != null) {
                        ChatScreen(viewModel = viewModel, doctorId = doctorId, patientThread = s.patient)
                    }
                }
                is CareScreen.DocSettings -> DocSettingsScreen(viewModel = viewModel)
                is CareScreen.DocPrescribe -> DocPrescribeScreen(viewModel = viewModel, appointmentId = s.appointmentId, fromChat = s.fromChat)
                is CareScreen.Triage -> TriageScreen(viewModel = viewModel)
                is CareScreen.Vitals -> VitalsScreen(viewModel = viewModel)
                is CareScreen.Labs -> LabsScreen(viewModel = viewModel)
                is CareScreen.Wallet -> WalletScreen(viewModel = viewModel)
                is CareScreen.DocEarnings -> DocEarningsScreen(viewModel = viewModel)
                is CareScreen.DocSchedule -> DocScheduleScreen(viewModel = viewModel)
                is CareScreen.FacilityHome -> FacilityHomeScreen(viewModel = viewModel)
                is CareScreen.FacilityDepartments -> FacilityDepartmentsScreen(viewModel = viewModel)
                is CareScreen.FacilityReferrals -> FacilityReferralsScreen(viewModel = viewModel)
                is CareScreen.FacilityProcedures -> FacilityProceduresScreen(viewModel = viewModel)
                is CareScreen.FacilityFollowUps -> FacilityFollowUpsScreen(viewModel = viewModel)
                is CareScreen.FacilityWards -> FacilityWardsScreen(viewModel = viewModel)
                is CareScreen.FacilityBookCare -> FacilityBookCareScreen(viewModel = viewModel, preselectedDeptId = s.preselectedDeptId)
                is CareScreen.FacilityProfile -> FacilityProfileScreen(viewModel = viewModel, facilityId = s.facilityId)
                is CareScreen.FacilityStaff -> FacilityStaffScreen(viewModel = viewModel)
                is CareScreen.FacilityPharmacy -> FacilityPharmacyScreen(viewModel = viewModel)
                is CareScreen.FacilityLabs -> FacilityLabsScreen(viewModel = viewModel)
                is CareScreen.FacilitySettings -> FacilitySettingsScreen(viewModel = viewModel)
            }
        }
    }
}
