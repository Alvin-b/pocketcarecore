package com.example

import com.example.data.local.CareData
import com.example.data.model.Medication
import com.example.data.model.PatientAllergy
import com.example.video.CallState
import com.example.video.VideoCallManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun kmpdcVerification_findsDoctorByLicenseNumber() {
        val doctors = CareData.DOCTORS
        val doctor = doctors.find { it.license == "KMPDC-A10293" }

        assertNotNull(doctor)
        assertEquals("Dr. Amina Wanjiru", doctor?.name)
        assertEquals("General", doctor?.specialty)
        assertEquals(1500, doctor?.fee)
        assertTrue(doctor?.languages?.contains("Swahili") == true)
    }

    @Test
    fun checkPrescription_detectsPenicillinAllergyWithAmoxicillin() {
        val allergies = listOf(
            PatientAllergy(
                id = "al-1",
                name = "Penicillin",
                reaction = "Hives, facial swelling",
                severity = "Severe",
                date = "2026-03-01",
                recordedBy = "d1"
            )
        )

        val newMed = Medication(
            id = "m1",
            name = "Amoxicillin",
            dose = "500 mg",
            frequency = 3,
            days = 7,
            times = listOf("08:00", "14:00", "20:00")
        )

        val alerts = CareData.checkPrescription(
            newMeds = listOf(newMed),
            allergies = allergies,
            currentMeds = emptyList()
        )

        assertTrue(alerts.isNotEmpty())
        val allergyAlert = alerts.find { it.kind == "allergy" }
        assertNotNull(allergyAlert)
        assertTrue(allergyAlert?.detail?.contains("Penicillin") == true)
        assertEquals("severe", allergyAlert?.level)
    }

    @Test
    fun checkPrescription_detectsDrugDrugInteraction() {
        val existingMed = Medication(
            id = "m1",
            name = "Warfarin",
            dose = "5 mg",
            frequency = 1,
            days = 30,
            times = listOf("18:00")
        )

        val newMed = Medication(
            id = "m2",
            name = "Ibuprofen",
            dose = "400 mg",
            frequency = 3,
            days = 5,
            times = listOf("08:00", "14:00", "20:00")
        )

        val alerts = CareData.checkPrescription(
            newMeds = listOf(newMed),
            allergies = emptyList(),
            currentMeds = listOf(existingMed)
        )

        assertTrue(alerts.isNotEmpty())
        val interactionAlert = alerts.find { it.kind == "interaction" }
        assertNotNull(interactionAlert)
        assertEquals("severe", interactionAlert?.level)
    }

    @Test
    fun videoSdkConfiguration_staysServerSideAndDoesNotPutTokensInUrls() {
        val appointmentId = "apt-101"
        assertEquals("careconnect-apt101", CareData.formatMeetingId(appointmentId))

        val meetingUrl = CareData.buildVideoSdkMeetingUrl(appointmentId, "Joy Kamau", "test-token")
        assertTrue(meetingUrl.startsWith("https://us.app.videosdk.live/v2/meeting/careconnect-apt101"))
        assertTrue(meetingUrl.contains("name=Joy+Kamau"))
        assertFalse(meetingUrl.contains("test-token"))
        assertFalse(meetingUrl.contains("token="))

        val embedUrl = CareData.buildVideoSdkEmbedUrl(
            appointmentId,
            "Dr. Amina",
            token = "test-token",
            micEnabled = true,
            webcamEnabled = true
        )
        assertTrue(embedUrl.contains("micEnabled=true"))
        assertTrue(embedUrl.contains("webcamEnabled=true"))
        assertFalse(embedUrl.contains("test-token"))
        assertFalse(embedUrl.contains("token="))

        assertEquals("https://us.app.videosdk.live/dashboard/GMQXZEI1IBSP", CareData.VIDEOSDK_DASHBOARD_URL)
    }

    @Test
    fun videoCallManager_initializationDoesNotClaimToBeConfigured() {
        val manager = VideoCallManager.getInstance()
        manager.reset()
        manager.initialize()

        assertEquals("Server-side only", manager.projectId.value)
        assertEquals("CareConnect", manager.organization.value)
        assertEquals("", manager.token.value)
        assertFalse(manager.isInitialized.value)
    }

    @Test
    fun videoCallManager_joinRoomFailsClosedWithoutARealAuthenticatedSession() {
        val manager = VideoCallManager.getInstance()
        manager.reset()

        manager.joinRoom(
            appointmentId = "apt-777",
            participantName = "Dr. Amina",
            isDoctor = true,
            initialMic = true,
            initialCam = true
        )

        assertEquals(CallState.ERROR, manager.callState.value)
        assertNull(manager.currentSession.value)
        assertTrue(manager.participants.value.isEmpty())
        assertFalse(manager.isMicOn.value)
        assertFalse(manager.isCamOn.value)
        assertEquals("Unavailable", manager.diagnostics.value.resolution)
        assertEquals(0, manager.elapsedSeconds.value)
        assertFalse(manager.toggleMic())
        assertFalse(manager.toggleCamera())

        manager.leaveRoom()
        assertEquals(CallState.ENDED, manager.callState.value)

        manager.reset()
        assertEquals(CallState.IDLE, manager.callState.value)
        assertNull(manager.currentSession.value)
        assertEquals(0, manager.elapsedSeconds.value)
    }
}
