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
    fun videoSdkUrl_formatsMeetingCorrectly() {
        val apptId = "apt-101"
        val meetingId = CareData.formatMeetingId(apptId)
        assertEquals("careconnect-apt101", meetingId)

        val url = CareData.buildVideoSdkMeetingUrl(apptId, "Joy Kamau")
        assertTrue(url.startsWith("https://us.app.videosdk.live/v2/meeting/careconnect-apt101"))
        assertTrue(url.contains("name=Joy+Kamau"))
        assertTrue(url.contains("videosdk.live"))

        val embedUrl = CareData.buildVideoSdkEmbedUrl(apptId, "Dr. Amina", micEnabled = true, webcamEnabled = true)
        assertTrue(embedUrl.startsWith("https://embed.videosdk.live/rtc-js-prebuilt/0.3.43/?meetingId=careconnect-apt101"))
        assertTrue(embedUrl.contains("micEnabled=true"))
        assertTrue(embedUrl.contains("webcamEnabled=true"))

        assertEquals("fe33aadd-4607-477a-88b7-f2683e9f6f3e", CareData.VIDEOSDK_PROJECT_ID)
        assertEquals("Malik's Org", CareData.VIDEOSDK_ORG_NAME)
        assertEquals("https://us.app.videosdk.live/dashboard/ADWLQ29Y0YHD", CareData.VIDEOSDK_DASHBOARD_URL)
    }

    @Test
    fun videoCallManager_initializationAndDefaults() {
        val manager = VideoCallManager.getInstance()
        manager.initialize()

        assertEquals("fe33aadd-4607-477a-88b7-f2683e9f6f3e", manager.projectId.value)
        assertEquals("Malik's Org", manager.organization.value)
        assertTrue(manager.isInitialized.value)
    }

    @Test
    fun videoCallManager_joinRoomAndTrackControls() {
        val manager = VideoCallManager.getInstance()
        manager.reset()

        val session = manager.joinRoom(
            appointmentId = "apt-777",
            participantName = "Dr. Amina",
            isDoctor = true,
            initialMic = true,
            initialCam = true
        )

        assertNotNull(session)
        assertEquals("careconnect-apt777", session.meetingId)
        assertEquals("apt-777", session.appointmentId)
        assertEquals("Dr. Amina", session.participantName)
        assertTrue(session.isDoctor)
        assertEquals(CallState.RINGING, manager.callState.value)
        assertTrue(manager.isMicOn.value)
        assertTrue(manager.isCamOn.value)

        // Test mic toggle
        val micAfterToggle = manager.toggleMic()
        assertFalse(micAfterToggle)
        assertFalse(manager.isMicOn.value)
        val micRestored = manager.toggleMic()
        assertTrue(micRestored)
        assertTrue(manager.isMicOn.value)

        // Test camera toggle
        val camAfterToggle = manager.toggleCamera()
        assertFalse(camAfterToggle)
        assertFalse(manager.isCamOn.value)
        val camRestored = manager.toggleCamera()
        assertTrue(camRestored)
        assertTrue(manager.isCamOn.value)

        // Test camera switch
        val initialFacing = manager.cameraFacing.value
        val switchedFacing = manager.switchCamera()
        assertTrue(switchedFacing != initialFacing)

        // Test leave room and reset
        manager.leaveRoom()
        assertEquals(CallState.ENDED, manager.callState.value)

        manager.reset()
        assertEquals(CallState.IDLE, manager.callState.value)
        assertNull(manager.currentSession.value)
        assertEquals(0, manager.elapsedSeconds.value)
    }
}
