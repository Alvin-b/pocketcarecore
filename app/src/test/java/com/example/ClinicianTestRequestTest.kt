package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.LabRequestStatus
import com.example.data.model.Medication
import com.example.ui.CareViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class ClinicianTestRequestTest {
    private val mainDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun clinicianCanRequestTestAndSubmitResultForPatient() {
        val viewModel = CareViewModel(ApplicationProvider.getApplicationContext<Application>())
        viewModel.setRole("doctor")

        assertTrue(viewModel.requestClinicianTest("Joy Kamau", "Complete blood count", "Complete at a local lab"))
        val request = viewModel.clinicianTestRequests.value.single()
        assertEquals(LabRequestStatus.PENDING, request.status)
        assertEquals("Joy Kamau", request.patient)

        assertTrue(viewModel.submitClinicianTestResult(request.id, "Demo result summary"))
        val updated = viewModel.clinicianTestRequests.value.single()
        assertEquals(LabRequestStatus.RESULT_SUBMITTED, updated.status)
        assertEquals("Demo result summary", updated.resultText)
        assertTrue(updated.resultDate?.isNotBlank() == true)
    }

    @Test
    fun clinicianCannotRequestTestWithoutPatientSharingConsent() {
        val viewModel = CareViewModel(ApplicationProvider.getApplicationContext<Application>())
        viewModel.setRole("doctor")
        viewModel.toggleCareTeamSharingConsent("Joy Kamau")

        assertFalse(viewModel.requestClinicianTest("Joy Kamau", "Complete blood count", ""))
        assertTrue(viewModel.clinicianTestRequests.value.isEmpty())
    }

    @Test
    fun prescriptionRequiresAssignedClinicianConsentAndSevereAlertAcknowledgement() {
        val viewModel = CareViewModel(ApplicationProvider.getApplicationContext<Application>())
        val initialCount = viewModel.prescriptions.value.size
        val medication = Medication(
            id = "test-medication",
            name = "Example medicine",
            dose = "1 tablet",
            frequency = 2,
            days = 5
        )
        val severeAlert = listOf("severe: possible interaction")

        viewModel.savePrescription("a1", "Demo diagnosis", null, null, listOf(medication), severeAlert, true, false)
        assertEquals(initialCount, viewModel.prescriptions.value.size)

        viewModel.setRole("doctor")
        viewModel.savePrescription("a0", "Demo diagnosis", null, null, listOf(medication), severeAlert, true, false)
        assertEquals(initialCount, viewModel.prescriptions.value.size)

        viewModel.toggleCareTeamSharingConsent("Joy Kamau")
        viewModel.savePrescription("a1", "Demo diagnosis", null, null, listOf(medication), emptyList(), true, false)
        assertEquals(initialCount, viewModel.prescriptions.value.size)

        viewModel.toggleCareTeamSharingConsent("Joy Kamau")
        viewModel.savePrescription("a1", "Demo diagnosis", null, null, listOf(medication), severeAlert, false, false)
        assertEquals(initialCount, viewModel.prescriptions.value.size)

        viewModel.savePrescription("a1", "Demo diagnosis", null, null, listOf(medication), severeAlert, true, false)
        assertEquals(initialCount + 1, viewModel.prescriptions.value.size)
        assertTrue(viewModel.prescriptions.value.first().safetyAcknowledged)
    }
}
