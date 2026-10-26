package com.example.video

import com.example.data.local.CareData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CallState {
    IDLE,
    INITIALIZING,
    RINGING,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    ENDED,
    ERROR
}

data class Participant(
    val id: String,
    val name: String,
    val role: String, // "patient" | "doctor"
    val isLocal: Boolean = false,
    val isMicOn: Boolean = true,
    val isCamOn: Boolean = true
)

data class CallDiagnostics(
    val resolution: String = "Unavailable",
    val fps: Int = 0,
    val audioCodec: String = "Unavailable",
    val videoCodec: String = "Unavailable",
    val latencyMs: Int = 0,
    val packetLossPercent: Float = 0f,
    val encryption: String = "Not verified",
    val connectionQuality: String = "Not connected",
    val bitrateKbps: Int = 0
)

data class CallSession(
    val meetingId: String,
    val appointmentId: String,
    val participantName: String,
    val isDoctor: Boolean,
    val startedAtMillis: Long,
    val roomUrl: String,
    val embedUrl: String
)

/**
 * Fail-closed call state holder. A server-issued token endpoint and a real
 * VideoSDK room client must be configured before a session can be joined.
 */
class VideoCallManager private constructor() {

    // VideoSDK Credentials & Project Configuration
    private val _projectId = MutableStateFlow(CareData.VIDEOSDK_PROJECT_ID)
    val projectId: StateFlow<String> = _projectId.asStateFlow()

    private val _organization = MutableStateFlow(CareData.VIDEOSDK_ORG_NAME)
    val organization: StateFlow<String> = _organization.asStateFlow()

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    // Call Room & Lifecycle State
    private val _callState = MutableStateFlow(CallState.IDLE)
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val _currentSession = MutableStateFlow<CallSession?>(null)
    val currentSession: StateFlow<CallSession?> = _currentSession.asStateFlow()

    private val _isMicOn = MutableStateFlow(true)
    val isMicOn: StateFlow<Boolean> = _isMicOn.asStateFlow()

    private val _isCamOn = MutableStateFlow(true)
    val isCamOn: StateFlow<Boolean> = _isCamOn.asStateFlow()

    private val _cameraFacing = MutableStateFlow("front") // "front" or "back"
    val cameraFacing: StateFlow<String> = _cameraFacing.asStateFlow()

    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val participants: StateFlow<List<Participant>> = _participants.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _diagnostics = MutableStateFlow(CallDiagnostics())
    val diagnostics: StateFlow<CallDiagnostics> = _diagnostics.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    /**
     * Initializes or reconfigures VideoSDK credentials.
     */
    fun initialize(
        projectId: String = CareData.VIDEOSDK_PROJECT_ID,
        token: String? = null,
        organization: String = CareData.VIDEOSDK_ORG_NAME
    ) {
        _projectId.value = projectId.trim()
        _organization.value = organization.trim()
        if (token != null) {
            _token.value = token.trim()
        }
        _isInitialized.value = false
        _statusMessage.value = "Configuration saved; a secure native VideoSDK session is not configured."
    }

    /**
     * Joins a VideoSDK meeting room for the given appointment.
     */
    fun joinRoom(
        appointmentId: String,
        participantName: String,
        isDoctor: Boolean,
        initialMic: Boolean = true,
        initialCam: Boolean = true
    ): CallSession {
        val meetingId = CareData.formatMeetingId(appointmentId)
        val attemptedSession = CallSession(
            meetingId = meetingId,
            appointmentId = appointmentId,
            participantName = participantName,
            isDoctor = isDoctor,
            startedAtMillis = System.currentTimeMillis(),
            roomUrl = "",
            embedUrl = ""
        )

        _currentSession.value = null
        _isMicOn.value = initialMic
        _isCamOn.value = initialCam
        _elapsedSeconds.value = 0
        _participants.value = emptyList()
        _callState.value = CallState.ERROR
        _statusMessage.value = if (_token.value.isBlank()) {
            "Secure call unavailable: no server-issued VideoSDK token is configured."
        } else {
            "Secure call unavailable: the native VideoSDK room client is not integrated."
        }
        return attemptedSession
    }

    /**
     * Mute or unmute local microphone track.
     */
    fun toggleMic(): Boolean {
        val newState = !_isMicOn.value
        _isMicOn.value = newState
        updateLocalParticipant { it.copy(isMicOn = newState) }
        return newState
    }

    /**
     * Turn camera video track on or off.
     */
    fun toggleCamera(): Boolean {
        val newState = !_isCamOn.value
        _isCamOn.value = newState
        updateLocalParticipant { it.copy(isCamOn = newState) }
        return newState
    }

    /**
     * Switch camera between front and back sensor.
     */
    fun switchCamera(): String {
        val next = if (_cameraFacing.value == "front") "back" else "front"
        _cameraFacing.value = next
        return next
    }

    /**
     * Ends the active VideoSDK meeting room session.
     */
    fun leaveRoom() {
        _callState.value = CallState.IDLE
        _currentSession.value = null
        _participants.value = emptyList()
        _elapsedSeconds.value = 0
        _statusMessage.value = "No active VideoSDK session to leave."
    }

    /**
     * Resets manager back to IDLE state.
     */
    fun reset() {
        _callState.value = CallState.IDLE
        _currentSession.value = null
        _elapsedSeconds.value = 0
        _participants.value = emptyList()
    }

    private fun updateLocalParticipant(block: (Participant) -> Participant) {
        _participants.value = _participants.value.map {
            if (it.isLocal) block(it) else it
        }
    }

    companion object {
        @Volatile
        private var instance: VideoCallManager? = null

        fun getInstance(): VideoCallManager {
            return instance ?: synchronized(this) {
                instance ?: VideoCallManager().also { instance = it }
            }
        }
    }
}
