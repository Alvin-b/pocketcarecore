package com.example.video

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
    val role: String,
    val isLocal: Boolean = false,
    val isMicOn: Boolean = false,
    val isCamOn: Boolean = false
)

data class CallDiagnostics(
    val resolution: String = "Unavailable",
    val fps: Int = 0,
    val audioCodec: String = "Unavailable",
    val videoCodec: String = "Unavailable",
    val latencyMs: Int = 0,
    val packetLossPercent: Float = 0f,
    val encryption: String = "Unavailable",
    val connectionQuality: String = "Unavailable",
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
 * The Android prototype has no Supabase session or server appointment lookup, so it must not
 * fabricate an SDK connection. A native VideoSDK client can be added once it can consume the
 * authenticated backend session endpoint and drive this state from real SDK callbacks.
 */
class VideoCallManager private constructor() {
    private val _projectId = MutableStateFlow("Server-side only")
    val projectId: StateFlow<String> = _projectId.asStateFlow()

    private val _organization = MutableStateFlow("CareConnect")
    val organization: StateFlow<String> = _organization.asStateFlow()

    // Never store VideoSDK secrets or client-supplied long-lived tokens in the Android app.
    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _callState = MutableStateFlow(CallState.IDLE)
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val _currentSession = MutableStateFlow<CallSession?>(null)
    val currentSession: StateFlow<CallSession?> = _currentSession.asStateFlow()

    private val _isMicOn = MutableStateFlow(false)
    val isMicOn: StateFlow<Boolean> = _isMicOn.asStateFlow()

    private val _isCamOn = MutableStateFlow(false)
    val isCamOn: StateFlow<Boolean> = _isCamOn.asStateFlow()

    private val _cameraFacing = MutableStateFlow("front")
    val cameraFacing: StateFlow<String> = _cameraFacing.asStateFlow()

    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val participants: StateFlow<List<Participant>> = _participants.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _diagnostics = MutableStateFlow(CallDiagnostics())
    val diagnostics: StateFlow<CallDiagnostics> = _diagnostics.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun initialize() {
        _projectId.value = "Server-side only"
        _organization.value = "CareConnect"
        _token.value = ""
        _isInitialized.value = false
        _statusMessage.value = unavailableMessage
    }

    @Suppress("UNUSED_PARAMETER")
    fun joinRoom(
        appointmentId: String,
        participantName: String,
        isDoctor: Boolean,
        initialMic: Boolean = true,
        initialCam: Boolean = true
    ) {
        reset()
        _callState.value = CallState.ERROR
        _statusMessage.value = unavailableMessage
    }

    fun toggleMic(): Boolean = _isMicOn.value

    fun toggleCamera(): Boolean = _isCamOn.value

    fun switchCamera(): String = _cameraFacing.value

    fun leaveRoom() {
        _callState.value = CallState.ENDED
        _statusMessage.value = "No live VideoSDK room was joined."
        _elapsedSeconds.value = 0
        _participants.value = emptyList()
        _currentSession.value = null
    }

    fun reset() {
        _callState.value = CallState.IDLE
        _currentSession.value = null
        _elapsedSeconds.value = 0
        _participants.value = emptyList()
        _isMicOn.value = false
        _isCamOn.value = false
        _diagnostics.value = CallDiagnostics()
        _statusMessage.value = null
    }

    companion object {
        private const val unavailableMessage =
            "Secure Android calls are disabled until Supabase sign-in and real appointment authorization are connected."

        @Volatile
        private var instance: VideoCallManager? = null

        fun getInstance(): VideoCallManager {
            return instance ?: synchronized(this) {
                instance ?: VideoCallManager().also { instance = it }
            }
        }
    }
}
