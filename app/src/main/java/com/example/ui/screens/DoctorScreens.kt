package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CareData
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.model.CallType
import com.example.data.model.Doctor
import com.example.data.model.Medication
import com.example.data.model.PatientAllergy
import com.example.data.model.PatientMedicalProfile
import com.example.data.model.Prescription
import com.example.ui.CareScreen
import com.example.ui.CareViewModel
import com.example.ui.components.BtnVariant
import com.example.ui.components.CareAvatar
import com.example.ui.components.CareButton
import com.example.ui.components.CareCard
import com.example.ui.components.CareField
import com.example.ui.components.CareHeader
import com.example.ui.theme.TeleAccent
import com.example.ui.theme.TeleAccentSoft
import com.example.ui.theme.TeleBorder
import com.example.ui.theme.TeleCall
import com.example.ui.theme.TeleCallBorder
import com.example.ui.theme.TeleCallForeground
import com.example.ui.theme.TeleCallSurface
import com.example.ui.theme.TeleCallTextMuted
import com.example.ui.theme.TeleCallTextSecondary
import com.example.ui.theme.TeleDestructive
import com.example.ui.theme.TeleDestructiveSoft
import com.example.ui.theme.TeleMuted
import com.example.ui.theme.TelePrimary
import com.example.ui.theme.TelePrimarySoft
import com.example.ui.theme.TeleSuccess
import com.example.ui.theme.TeleSuccessSoft
import com.example.ui.theme.TeleSuccessVibrant
import com.example.ui.theme.TeleWarningVibrant
import com.example.video.CallState
import kotlinx.coroutines.delay

@Composable
fun CallConsultationScreen(
    viewModel: CareViewModel,
    appointmentId: String
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val appointments by viewModel.appointments.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val prescriptions by viewModel.prescriptions.collectAsState()
    val vitals by viewModel.vitals.collectAsState()
    val role by viewModel.role.collectAsState()
    val videoSdkProjectId by viewModel.videoSdkProjectId.collectAsState()
    val videoSdkOrgName by viewModel.videoSdkOrgName.collectAsState()
    val useEmbeddedVideoSdk by viewModel.useEmbeddedVideoSdk.collectAsState()

    // VideoCallManager state flows
    val callState by viewModel.videoCallState.collectAsState()
    val secondsElapsed by viewModel.videoCallElapsedSeconds.collectAsState()
    val isMicOn by viewModel.videoCallMicOn.collectAsState()
    val isCamOn by viewModel.videoCallCamOn.collectAsState()
    val cameraFacing by viewModel.videoCallCameraFacing.collectAsState()
    val diagnostics by viewModel.videoCallDiagnostics.collectAsState()

    val appt = appointments.find { it.id == appointmentId } ?: return
    val doc = doctors.find { it.id == appt.doctorId }
    val otherName = if (role == "doctor") appt.patient else doc?.name ?: "Doctor"
    val myName = if (role == "doctor") doc?.name ?: "Doctor" else appt.patient

    val meetingId = CareData.formatMeetingId(appointmentId)
    val meetingUrl = viewModel.getVideoSdkMeetingUrl(appointmentId, myName)
    val embedUrl = viewModel.getVideoSdkEmbedUrl(appointmentId, myName, isMicOn, isCamOn)

    var showQuickChat by remember { mutableStateOf(false) }
    var showVideoSdkInfo by remember { mutableStateOf(false) }

    // Start VideoSDK call session via VideoCallManager
    LaunchedEffect(appointmentId) {
        viewModel.startVideoCall(
            appointmentId = appointmentId,
            participantName = myName,
            isDoctor = role == "doctor",
            initialMic = true,
            initialCam = appt.type == CallType.VIDEO
        )
    }

    val handleEndCall = {
        viewModel.endVideoCall()
        viewModel.markAppointmentCompleted(appointmentId)
        if (role == "doctor") {
            viewModel.navigateTo(CareScreen.DocPrescribe(appointmentId))
        } else {
            viewModel.navigateTo(CareScreen.Rate(appointmentId))
        }
    }

    BackHandler {
        handleEndCall()
    }

    val mm = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60)
    val isVideo = appt.type == CallType.VIDEO && callState == CallState.CONNECTED

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TeleCall)
            .testTag("call_screen")
    ) {
        // Main Consultation View: VideoSDK WebRTC WebView or Native Stream View
        if (callState == CallState.CONNECTED && useEmbeddedVideoSdk && isVideo) {
            // Interactive VideoSDK WebRTC Prebuilt Room
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.allowFileAccess = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        webChromeClient = object : WebChromeClient() {
                            override fun onPermissionRequest(request: PermissionRequest?) {
                                request?.grant(request.resources)
                            }
                        }
                        webViewClient = WebViewClient()
                        loadUrl(embedUrl)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 88.dp, bottom = 104.dp)
            )
        } else {
            // Native Consultation Video Background
            if (isVideo && doc?.photoRes != null && role != "doctor") {
                Image(
                    painter = painterResource(id = doc.photoRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().padding(bottom = 104.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xBB0B132B))
                )
            }
        }

        // Full Overlay UI with Header, Clinical Context, and Controls
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top VideoSDK Room Header (High-contrast surface dock)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TeleCallSurface)
                    .border(width = 1.dp, color = TeleCallBorder)
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // VideoSDK Org & Status Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (callState == CallState.CONNECTED) TeleSuccessVibrant else TeleWarningVibrant)
                        )
                        Text(
                            text = "VideoSDK · $videoSdkOrgName",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TeleCallForeground
                        )
                    }

                    // Room Actions: Browser WebRTC, Copy Link, Diagnostics
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Open in external browser for native WebRTC acceleration
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingUrl))
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    viewModel.showToast("Could not launch external browser")
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF283655))
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = "Open VideoSDK in Browser", tint = Color.White, modifier = Modifier.size(20.dp))
                        }

                        // Copy meeting link
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(meetingUrl))
                                viewModel.showToast("Meeting link copied to clipboard")
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF283655))
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Meeting Link", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        // VideoSDK Config / Info
                        IconButton(
                            onClick = { showVideoSdkInfo = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF283655))
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "VideoSDK Info", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Meeting ID and Live Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, TeleCallBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Room: $meetingId",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = TeleCallTextSecondary
                        )
                    }

                    Text(
                        text = if (callState == CallState.RINGING) "Connecting…" else "${if (appt.type == CallType.VIDEO) "Video" else "Voice"} · $mm",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TeleCallForeground
                    )
                }

                if (callState == CallState.RINGING) {
                    Spacer(modifier = Modifier.height(14.dp))
                    CareAvatar(name = otherName, photoRes = if (role != "doctor") doc?.photoRes else null, size = 84.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(otherName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TeleCallForeground)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Initializing WebRTC stream via VideoSDK…", fontSize = 13.sp, color = TeleCallTextSecondary)
                }
            }

            // Doctor Encounter Context Panel
            if (role == "doctor" && callState == CallState.CONNECTED) {
                val previousRx = prescriptions.find { it.appointmentId != appt.id }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.94f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TeleCallSurface)
                        .border(1.dp, TeleCallBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Clinical Context · $otherName", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TeleCallForeground)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TeleAccentSoft)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("ID: ${videoSdkProjectId.take(8)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleAccent)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Chief complaint: ${appt.reason}", fontSize = 12.sp, color = TeleCallTextSecondary)
                        if (vitals.isNotEmpty()) {
                            Text("Recorded vitals: ${vitals.take(3).joinToString(" · ") { "${it.kind} ${it.value}" }}", fontSize = 11.sp, color = TeleCallTextSecondary)
                        }
                        if (previousRx != null) {
                            Text("Previous Rx: ${previousRx.diagnosis}", fontSize = 11.sp, color = TeleCallTextSecondary)
                        }
                    }
                }
            }

            // Quick message drawer overlay if active
            if (showQuickChat) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.94f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, TeleBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("In-call clinical message", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TeleTextPrimary)
                            Text("Close", color = TelePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { showQuickChat = false })
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val msgs = listOf("Audio is clear", "Please repeat that", "One moment please", "Writing prescription")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            msgs.forEach { qm ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(TeleMuted)
                                        .border(1.dp, TeleBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .clickable {
                                            viewModel.sendMessage(appt.doctorId, appt.patient, if (role == "doctor") "doctor" else "patient", qm)
                                            showQuickChat = false
                                        }
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(qm, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TeleTextPrimary, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom In-Call Controls Dock (High-contrast surface dock)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TeleCallSurface)
                    .border(width = 1.dp, color = TeleCallBorder)
                    .padding(vertical = 18.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Toggle
                IconButton(
                    onClick = { viewModel.toggleCallMic() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isMicOn) Color(0xFF283655) else Color.White)
                ) {
                    Icon(if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff, contentDescription = "Toggle Mic", tint = if (isMicOn) Color.White else TeleCall, modifier = Modifier.size(24.dp))
                }

                // Camera Toggle
                if (appt.type == CallType.VIDEO) {
                    IconButton(
                        onClick = { viewModel.toggleCallCam() },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isCamOn) Color(0xFF283655) else Color.White)
                    ) {
                        Icon(if (isCamOn) Icons.Default.Videocam else Icons.Default.VideocamOff, contentDescription = "Toggle Camera", tint = if (isCamOn) Color.White else TeleCall, modifier = Modifier.size(24.dp))
                    }

                    // Switch Camera (Front / Back)
                    IconButton(
                        onClick = {
                            val nextFacing = viewModel.switchCallCamera()
                            viewModel.showToast("Using $nextFacing camera")
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF283655))
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                } else {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF283655))
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.White)
                    }
                }

                // Toggle VideoSDK Embedded View / Native Stream View
                if (isVideo) {
                    IconButton(
                        onClick = { viewModel.toggleEmbeddedVideoSdk(!useEmbeddedVideoSdk) },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (useEmbeddedVideoSdk) TelePrimary else Color(0xFF283655))
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Switch VideoSDK View", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }

                // Quick Messages
                IconButton(
                    onClick = { showQuickChat = !showQuickChat },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (showQuickChat) TelePrimary else Color(0xFF283655))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Quick Chat", tint = Color.White, modifier = Modifier.size(22.dp))
                }

                // End Call
                IconButton(
                    onClick = { handleEndCall() },
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(TeleDestructive)
                        .testTag("call_end_btn")
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
        }

        // Self-camera PIP preview (when in native view mode)
        if (isVideo && !useEmbeddedVideoSdk) {
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 130.dp)
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 110.dp, end = 18.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TeleCallSurface)
                    .border(2.dp, TelePrimary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isCamOn) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("You ($cameraFacing)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.White)
                }
            }
        }

        // VideoSDK Info & Diagnostics Dialog Modal (High contrast, clearly visible)
        if (showVideoSdkInfo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable { showVideoSdkInfo = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TeleBorder)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(TeleSuccessVibrant)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("VideoSDK Diagnostics", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TeleTextPrimary)
                            }
                            Text("Close", color = TelePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { showVideoSdkInfo = false })
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Organization & Project ID
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(TeleMuted)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("ORGANIZATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleTextMuted)
                                Text(videoSdkOrgName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TeleTextPrimary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("PROJECT CONFIGURATION ID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleTextMuted)
                                Text(videoSdkProjectId, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TelePrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Real-time telemetry specs
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TeleMuted)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text("VIDEO STREAM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleTextMuted)
                                    Text("${diagnostics.resolution} @ ${diagnostics.fps}fps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TeleTextPrimary)
                                    Text(diagnostics.videoCodec, fontSize = 10.sp, color = TeleTextSecondary)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TeleMuted)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text("AUDIO STREAM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleTextMuted)
                                    Text(diagnostics.audioCodec, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TeleTextPrimary)
                                    Text("Latency: ${diagnostics.latencyMs}ms", fontSize = 10.sp, color = TeleTextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("ENCRYPTION & SECURITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleTextMuted)
                        Text(diagnostics.encryption, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TeleSuccess)
                        Spacer(modifier = Modifier.height(16.dp))

                        CareButton(
                            text = "Launch full WebRTC in Browser",
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingUrl))
                                    context.startActivity(intent)
                                    showVideoSdkInfo = false
                                } catch (_: Exception) {
                                    viewModel.showToast("Cannot launch browser")
                                }
                            },
                            variant = BtnVariant.PRIMARY,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DocRegisterScreen(viewModel: CareViewModel) {
    var nameField by remember { mutableStateOf("") }
    var specialtyField by remember { mutableStateOf("General") }
    var licenseField by remember { mutableStateOf("") }
    var yearsField by remember { mutableStateOf("5") }
    var feeField by remember { mutableStateOf("2000") }
    var bioField by remember { mutableStateOf("") }
    var hasLicenseCert by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.setRole(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "List your practice", onBack = { viewModel.setRole(null) })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CareCard(backgroundColor = TeleMuted) {
                    Text("Clinician onboarding", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("License details are validated with KMPDC. Set up your profile to start receiving tele-consultations.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            item {
                CareField(label = "Full name") {
                    OutlinedTextField(value = nameField, onValueChange = { nameField = it }, placeholder = { Text("Dr. Jane Doe") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp))
                }
            }

            item {
                CareField(label = "Specialty") {
                    OutlinedTextField(value = specialtyField, onValueChange = { specialtyField = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp))
                }
            }

            item {
                CareField(label = "Medical license number") {
                    OutlinedTextField(value = licenseField, onValueChange = { licenseField = it }, placeholder = { Text("KMPDC-A...") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp))
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CareField(label = "Years of experience", modifier = Modifier.weight(1f)) {
                        OutlinedTextField(value = yearsField, onValueChange = { yearsField = it }, singleLine = true, shape = RoundedCornerShape(12.dp))
                    }
                    CareField(label = "Fee (KES)", modifier = Modifier.weight(1f)) {
                        OutlinedTextField(value = feeField, onValueChange = { feeField = it }, singleLine = true, shape = RoundedCornerShape(12.dp))
                    }
                }
            }

            item {
                CareField(label = "About you") {
                    OutlinedTextField(value = bioField, onValueChange = { bioField = it }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp))
                }
            }

            item {
                CareCard(onClick = { hasLicenseCert = !hasLicenseCert }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = TelePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (hasLicenseCert) "Certificate Attached: kmpdc_retention.pdf" else "Attach KMPDC License Certificate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            item {
                CareButton(
                    text = "Submit & go live",
                    onClick = {
                        val years = yearsField.toIntOrNull() ?: 5
                        val fee = feeField.toIntOrNull() ?: 2000
                        viewModel.registerDoctor(nameField, specialtyField, licenseField, years, fee, bioField)
                    },
                    enabled = nameField.isNotBlank() && licenseField.isNotBlank(),
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth().testTag("doc_submit_btn")
                )
            }

            item {
                Text(
                    text = "Demo: sign in as Dr. Amina Wanjiru",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TelePrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().clickable {
                        viewModel.setRole("doctor")
                    }.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DocHomeScreen(viewModel: CareViewModel) {
    val doctors by viewModel.doctors.collectAsState()
    val myDoctorId by viewModel.myDoctorId.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val prescriptions by viewModel.prescriptions.collectAsState()
    val messages by viewModel.messages.collectAsState()

    val me = doctors.find { it.id == myDoctorId } ?: doctors.first()
    val myVisits = appointments.filter { it.doctorId == me.id }
    val upcomingVisits = myVisits.filter { it.status == AppointmentStatus.UPCOMING }
    val completedVisits = myVisits.filter { it.status == AppointmentStatus.COMPLETED }
    val nextVisit = upcomingVisits.firstOrNull()

    val patientCount = myVisits.map { it.patient }.distinct().size
    val unread = messages.count { it.doctorId == me.id && it.from == "patient" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .testTag("doc_home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CareAvatar(name = me.name, photoRes = me.photoRes, size = 52.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Good day \uD83D\uDC4B", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(me.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(16.dp))
                    }
                }
                IconButton(onClick = { viewModel.navigateTo(CareScreen.DocSettings) }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // Next Consultation Card
        if (nextVisit != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = TelePrimary)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("NEXT CONSULTATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CareAvatar(name = nextVisit.patient, size = 44.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(nextVisit.patient, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                Text("${nextVisit.date} · ${nextVisit.time} · ${nextVisit.type.name.lowercase()}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CareButton(
                                text = "View chart",
                                onClick = { viewModel.navigateTo(CareScreen.DocPatient(nextVisit.patient)) },
                                variant = BtnVariant.SOFT,
                                modifier = Modifier.weight(1f).height(38.dp)
                            )
                            CareButton(
                                text = "Start call",
                                onClick = { viewModel.navigateTo(CareScreen.Call(nextVisit.id)) },
                                variant = BtnVariant.ACCENT,
                                modifier = Modifier.weight(1f).height(38.dp)
                            )
                        }
                    }
                }
            }
        }

        // Stats 4 columns
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Pair("Today", "${upcomingVisits.count { it.date == CareData.todayISO() }}"),
                    Pair("Upcoming", "${upcomingVisits.size}"),
                    Pair("Patients", "$patientCount"),
                    Pair("Rating", String.format("%.1f", me.rating))
                ).forEach { (label, value) ->
                    CareCard(modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TelePrimary)
                            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Quick Actions 2x2
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CareCard(modifier = Modifier.weight(1f), onClick = { viewModel.navigateTo(CareScreen.DocChats) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = TelePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Messages", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    CareCard(modifier = Modifier.weight(1f), onClick = { viewModel.navigateTo(CareScreen.DocPatients) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TelePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Patients", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CareCard(modifier = Modifier.weight(1f), onClick = { viewModel.navigateTo(CareScreen.DocSchedule) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = TelePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Availability", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    CareCard(modifier = Modifier.weight(1f), onClick = { viewModel.navigateTo(CareScreen.DocEarnings) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = TelePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Earnings", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Today's schedule
        item {
            Text("Today's schedule", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(upcomingVisits) { a ->
            CareCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(a.time, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TelePrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(a.patient, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(a.reason, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    CareButton(
                        text = "Call",
                        onClick = { viewModel.navigateTo(CareScreen.Call(a.id)) },
                        variant = BtnVariant.PRIMARY,
                        modifier = Modifier.height(34.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DocPatientsScreen(viewModel: CareViewModel) {
    val appointments by viewModel.appointments.collectAsState()
    val profiles by viewModel.patientProfiles.collectAsState()
    val myDoctorId by viewModel.myDoctorId.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val myPatients = appointments
        .filter { it.doctorId == myDoctorId }
        .map { it.patient }
        .distinct()
        .filter { it.contains(searchQuery, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text("Patient directory", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search patients", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(myPatients) { patient ->
                val visits = appointments.filter { it.patient == patient }
                val profile = profiles[patient]

                CareCard(
                    onClick = { viewModel.navigateTo(CareScreen.DocPatient(patient)) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CareAvatar(name = patient, size = 48.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(patient, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${visits.size} visits · longitudinal chart", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (profile?.allergies?.isNotEmpty() == true) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(TeleDestructiveSoft).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("Allergy", fontSize = 10.sp, color = TeleDestructive, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocPatientChartScreen(
    viewModel: CareViewModel,
    patient: String
) {
    val profiles by viewModel.patientProfiles.collectAsState()
    val prescriptions by viewModel.prescriptions.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val currentProfile = profiles[patient] ?: PatientMedicalProfile(patient, emptyList(), false)

    var newAllergen by remember { mutableStateOf("") }
    var newReaction by remember { mutableStateOf("") }
    var newSeverity by remember { mutableStateOf("Moderate") }

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Patient chart", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CareCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CareAvatar(name = patient, size = 48.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(patient, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Longitudinal chart", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Allergies
            item {
                Text("Allergies", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            items(currentProfile.allergies) { al ->
                CareCard(backgroundColor = TeleDestructiveSoft.copy(alpha = 0.5f), borderColor = TeleDestructive.copy(alpha = 0.3f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("${al.name} · ${al.severity}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Reaction: ${al.reaction} · Recorded ${al.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.removeAllergy(patient, al.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TeleDestructive, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            item {
                CareCard {
                    Text("Add allergy", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newAllergen, onValueChange = { newAllergen = it }, placeholder = { Text("Allergen (e.g. Penicillin)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newReaction, onValueChange = { newReaction = it }, placeholder = { Text("Reaction (e.g. Hives)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    CareButton(
                        text = "Save allergy",
                        onClick = {
                            viewModel.addAllergy(patient, newAllergen, newReaction, newSeverity)
                            newAllergen = ""
                            newReaction = ""
                        },
                        enabled = newAllergen.isNotBlank(),
                        variant = BtnVariant.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Diagnoses & Meds from all clinicians
            item {
                Text("Prescriptions & Diagnoses", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            items(prescriptions) { rx ->
                CareCard {
                    Text("Diagnosis: ${rx.diagnosis}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Date: ${rx.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    rx.meds.forEach { m ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("• ${m.name} (${m.dose})", fontSize = 12.sp)
                            Text(
                                text = if (m.status == "active") "Discontinue" else "Reactivate",
                                color = TelePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    val nextStatus = if (m.status == "active") "discontinued" else "active"
                                    viewModel.toggleMedicationStatus(rx.id, m.id, nextStatus)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocPrescribeScreen(
    viewModel: CareViewModel,
    appointmentId: String,
    fromChat: Boolean = false
) {
    val appointments by viewModel.appointments.collectAsState()
    val profiles by viewModel.patientProfiles.collectAsState()
    val appt = appointments.find { it.id == appointmentId } ?: return
    val profile = profiles[appt.patient]

    var diagnosisText by remember { mutableStateOf("") }
    var clinicalNotesText by remember { mutableStateOf("") }
    var selectedDiagnosisCode by remember { mutableStateOf<String?>(null) }
    var showDiagnosisSuggestions by remember { mutableStateOf(false) }

    var meds by remember {
        mutableStateOf(
            listOf(
                Medication(id = "m-${System.currentTimeMillis()}", name = "", dose = "1 tablet", frequency = 2, days = 5, notes = "")
            )
        )
    }
    var acknowledgedSevere by remember { mutableStateOf(false) }

    val safetyAlerts = remember(meds, profile) {
        CareData.checkPrescription(meds, profile?.allergies ?: emptyList())
    }
    val hasSevere = safetyAlerts.any { it.level == "severe" }

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Write prescription", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CareCard {
                    Text(appt.patient, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Reason: ${appt.reason}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // ICD-11 Diagnosis search
            item {
                CareField(label = "Diagnosis · ICD-11 MMS") {
                    OutlinedTextField(
                        value = diagnosisText,
                        onValueChange = {
                            diagnosisText = it
                            showDiagnosisSuggestions = true
                        },
                        placeholder = { Text("Search ICD-11 diagnosis (e.g. Asthma, Hypertension)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (showDiagnosisSuggestions && diagnosisText.length >= 2) {
                    val hits = CareData.searchLocalDiagnoses(diagnosisText)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, TeleBorder)
                    ) {
                        hits.forEach { hit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        diagnosisText = hit.title
                                        selectedDiagnosisCode = hit.code.ifBlank { null }
                                        showDiagnosisSuggestions = false
                                    }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(hit.title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                if (hit.code.isNotBlank()) {
                                    Text(hit.code, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                                }
                            }
                        }
                    }
                }
            }

            item {
                CareField(label = "Doctor notes") {
                    OutlinedTextField(
                        value = clinicalNotesText,
                        onValueChange = { clinicalNotesText = it },
                        placeholder = { Text("Clinical assessment, relevant history, and follow-up plan") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Medications
            itemsIndexed(meds) { i, m ->
                CareCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Medication ${i + 1}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TelePrimary)
                        if (meds.size > 1) {
                            Text("Remove", color = TeleDestructive, fontSize = 11.sp, modifier = Modifier.clickable {
                                meds = meds.filterIndexed { index, _ -> index != i }
                            })
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = m.name,
                        onValueChange = { newName ->
                            meds = meds.mapIndexed { index, med -> if (index == i) med.copy(name = newName) else med }
                        },
                        placeholder = { Text("Drug name (e.g. Paracetamol, Amoxicillin)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = m.dose, onValueChange = { d -> meds = meds.mapIndexed { index, med -> if (index == i) med.copy(dose = d) else med } }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(10.dp))
                        OutlinedTextField(value = "${m.frequency}×/day", onValueChange = {}, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(10.dp))
                        OutlinedTextField(value = "${m.days} days", onValueChange = {}, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(10.dp))
                    }
                }
            }

            item {
                CareButton(
                    text = "Add medication",
                    onClick = {
                        meds = meds + Medication(id = "m-${System.currentTimeMillis()}", name = "", dose = "1 tablet", frequency = 2, days = 5, notes = "")
                    },
                    variant = BtnVariant.SOFT,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Safety warnings
            if (safetyAlerts.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        safetyAlerts.forEach { a ->
                            val isSevere = a.level == "severe"
                            CareCard(backgroundColor = if (isSevere) TeleDestructiveSoft else TeleAccentSoft) {
                                Text(a.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isSevere) TeleDestructive else TeleAccent)
                                Text(a.detail, fontSize = 11.sp)
                            }
                        }
                        if (hasSevere) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = acknowledgedSevere, onCheckedChange = { acknowledgedSevere = it })
                                Text("I have reviewed the severe alerts and still want to prescribe.", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            CareButton(
                text = "Save diagnosis & prescription",
                onClick = {
                    val alertsSummary = safetyAlerts.map { "${it.level}: ${it.title}" }
                    viewModel.savePrescription(
                        appointmentId = appointmentId,
                        diagnosisTitle = diagnosisText,
                        diagnosisCode = selectedDiagnosisCode,
                        clinicalNotes = clinicalNotesText,
                        meds = meds,
                        safetyAlerts = alertsSummary,
                        safetyAcknowledged = acknowledgedSevere,
                        fromChat = fromChat
                    )
                },
                enabled = diagnosisText.isNotBlank() && meds.any { it.name.isNotBlank() } && (!hasSevere || acknowledgedSevere),
                variant = BtnVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth().testTag("save_rx_btn")
            )
        }
    }
}

@Composable
fun DocScheduleScreen(viewModel: CareViewModel) {
    val availability by viewModel.availability.collectAsState()
    val myDoctorId by viewModel.myDoctorId.collectAsState()
    val id = myDoctorId ?: "doctor"

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Availability", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(days) { day ->
                val key = "$id:$day"
                val enabledSlots = availability[key] ?: CareData.TIME_SLOTS.take(6)

                CareCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(day, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Open all", color = TelePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.openAllSlots(day) })
                            Text("Block day", color = TeleDestructive, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.blockAllSlots(day) })
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val chunks = CareData.TIME_SLOTS.chunked(4)
                    chunks.forEach { rowSlots ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            rowSlots.forEach { slot ->
                                val isOn = enabledSlots.contains(slot)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isOn) TelePrimary else TeleMuted)
                                        .clickable { viewModel.toggleAvailabilitySlot(day, slot) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slot,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOn) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DocEarningsScreen(viewModel: CareViewModel) {
    val appointments by viewModel.appointments.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val myDoctorId by viewModel.myDoctorId.collectAsState()

    val me = doctors.find { it.id == myDoctorId } ?: doctors.first()
    val completedVisits = appointments.filter { it.doctorId == me.id && it.status == AppointmentStatus.COMPLETED }
    val gross = completedVisits.size * me.fee
    val net = Math.round(gross * 0.85).toInt()

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareHeader(title = "Earnings", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = TelePrimary)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Available payout · after 15% platform fee", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(CareData.formatKES(net), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("This month · demo estimate", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CareCard(modifier = Modifier.weight(1f)) {
                        Text("Completed visits", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${completedVisits.size}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    CareCard(modifier = Modifier.weight(1f)) {
                        Text("Gross consultation fees", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CareData.formatKES(gross), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }

            item {
                CareButton(
                    text = "Request payout",
                    onClick = { viewModel.showToast("Payout requests are simulated in this demo.") },
                    variant = BtnVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text("Completed consultations", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            items(completedVisits) { a ->
                CareCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(a.patient, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${a.date} · ${a.time}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(CareData.formatKES(Math.round(me.fee * 0.85).toInt()), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TelePrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun DocSettingsScreen(viewModel: CareViewModel) {
    val doctors by viewModel.doctors.collectAsState()
    val myDoctorId by viewModel.myDoctorId.collectAsState()
    val acceptingPatients by viewModel.acceptingPatients.collectAsState()

    val me = doctors.find { it.id == myDoctorId } ?: doctors.first()
    var feeInput by remember { mutableStateOf(me.fee.toString()) }
    var bioInput by remember { mutableStateOf(me.bio) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .testTag("doc_settings_screen")
    ) {
        Text("Practice", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CareCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CareAvatar(name = me.name, photoRes = me.photoRes, size = 52.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(me.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("${me.specialty} · ${me.license}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                CareCard {
                    Text("Practice details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    CareField(label = "Consultation fee (KES)") {
                        OutlinedTextField(
                            value = feeInput,
                            onValueChange = {
                                feeInput = it
                                val f = it.toIntOrNull() ?: me.fee
                                viewModel.updateDoctorPractice(f, bioInput)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    CareField(label = "About you") {
                        OutlinedTextField(
                            value = bioInput,
                            onValueChange = {
                                bioInput = it
                                val f = feeInput.toIntOrNull() ?: me.fee
                                viewModel.updateDoctorPractice(f, bioInput)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                CareCard(onClick = { viewModel.toggleAcceptingPatients() }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Accepting new patients", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("When off, patients can't book new slots with you.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (acceptingPatients) TeleSuccessSoft else TeleMuted)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (acceptingPatients) "On" else "Off",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (acceptingPatients) TeleSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                CareCard(onClick = { viewModel.navigateTo(CareScreen.DocSchedule) }) {
                    Text("Weekly availability", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            item {
                CareCard(onClick = { viewModel.navigateTo(CareScreen.DocEarnings) }) {
                    Text("Earnings & payouts", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            item {
                val context = LocalContext.current
                val videoSdkProjectId by viewModel.videoSdkProjectId.collectAsState()
                val videoSdkOrgName by viewModel.videoSdkOrgName.collectAsState()
                var editingProject by remember { mutableStateOf(false) }
                var projectInput by remember { mutableStateOf(videoSdkProjectId) }

                CareCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("VideoSDK Infrastructure", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Real-time WebRTC teleconsultations", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TeleSuccessSoft)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Active · $20 credit", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TeleSuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Organization: ", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(videoSdkOrgName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TelePrimary)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Project Configuration ID:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = videoSdkProjectId,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CareButton(
                            text = "Test VideoSDK Room",
                            onClick = {
                                val testUrl = viewModel.getVideoSdkMeetingUrl("test-demo-room", me.name)
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(testUrl))
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    viewModel.showToast("Cannot launch browser")
                                }
                            },
                            variant = BtnVariant.OUTLINE,
                            modifier = Modifier.weight(1f)
                        )
                        CareButton(
                            text = "Dashboard",
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CareData.VIDEOSDK_DASHBOARD_URL))
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    viewModel.showToast("Cannot launch browser")
                                }
                            },
                            variant = BtnVariant.GHOST,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                CareCard(onClick = { viewModel.setRole(null) }) {
                    Text("Switch role", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            item {
                CareCard(onClick = { viewModel.resetDemoData() }) {
                    Text("Reset demo data", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TeleDestructive)
                }
            }
        }
    }
}
