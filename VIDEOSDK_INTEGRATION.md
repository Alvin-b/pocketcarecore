# VideoSDK integration notes

## Current state

The Android call manager fails closed: it does not join a provider room and reports that secure call setup is unavailable rather than claiming a connection. The former simulated/WebView path is not a verified call integration.

The VideoSDK dashboard has an existing active `Auto Generated` API key. Its secret remained masked; no key was revealed, copied, or created during this review. The backend must keep API credentials server-side; no API secret belongs in the Android project.

## CareConnect backend handoff

The CareConnect backend repository now provides `POST /api/videosdk/session`. The request body is `{ "appointmentId": "<Supabase appointment UUID>" }`; the caller must send `Authorization: Bearer <Supabase access token>`. The endpoint accepts only the appointment's authenticated patient or assigned clinician with the matching `patient`/`doctor` role, requires payment and an `upcoming` or `in_progress` status, and additionally requires the clinician's verification status to be `approved`. It creates or reuses the appointment's stable VideoSDK room and returns `meetingId`, a 10-minute room-and-participant-bound token, `participantId`, `participantName`, `callType`, and `tokenExpiresAt`.

In the Lovable project's server-side Secrets/environment settings, configure `VIDEOSDK_API_KEY` and `VIDEOSDK_API_SECRET` for the intended VideoSDK project. Keep them server-only; never paste them into chat, Android resources, BuildConfig, browser URLs, or a checked-in `.env` file. The authenticated web call page uses this endpoint and the official VideoSDK React SDK.

The Android app does not yet have Supabase sign-in or real appointment records. Its call manager now fails closed instead of simulating a joined room, and its placeholder diagnostics report unavailable. Native Android calls remain a separate follow-up: connect Supabase identity and real appointment lookup, call this endpoint with the user's bearer token, and drive call controls/state from the official native VideoSDK SDK callbacks. Until that work is complete, Android should not be described as supporting live calls.

## Supported implementation paths

VideoSDK documents a native Android RTC SDK and an Android WebView Prebuilt path. Choose one path deliberately rather than mixing simulated manager state with a separate embedded page.

For the native path, the official Android quick start documents `live.videosdk:rtc-android-sdk:2.2.0`, SDK initialization, configuring with a token, creating a meeting, registering `MeetingEventListener`, and joining the room. Drive connected, disconnected, participant, and error UI from SDK callbacks. Mute, camera, and leave controls must call SDK methods and release the room lifecycle.

For Prebuilt in WebView, the official quick start uses a room ID and token in its hosted embed URL. Android runtime camera and microphone permissions are required. Restrict permission grants to the trusted VideoSDK origin and only the requested media resources; the sample's broad grant is not sufficient as a production security policy.

## Authorization and privacy requirements

VideoSDK's Android token documentation recommends generating access tokens on a backend so API secrets stay safe. Issue short-lived tokens scoped to the authorized appointment room and participant, and validate the signed-in user's role and appointment access server-side. Never package the VideoSDK secret in the APK, Android `.env`, source, or client-side BuildConfig. Do not use a predictable appointment-derived room slug as the authorization boundary.

Until the Android app is wired to use the authenticated backend endpoint with real Supabase identity and appointment records, the Android call path should fail closed and clearly say that secure call setup is unavailable. It must not show fabricated connection quality, encryption, latency, or connected status.

## Official sources

- [VideoSDK Android quick start](https://docs.videosdk.live/android/guide/video-and-audio-calling-api-sdk/quick-start)
- [VideoSDK Android token authentication](https://docs.videosdk.live/android/0.1.x/guide/video-and-audio-calling-api-sdk/authentication-and-token)
- [VideoSDK Android Prebuilt WebView quick start](https://docs.videosdk.live/prebuilt/guide/prebuilt-video-and-audio-calling/quick-start-android)
- [VideoSDK Android meeting-state callback reference](https://docs.videosdk.live/android-sdk-reference/zn-android-sdk/live.videosdk.rtc.android.listeners/-meeting-event-listener/on-meeting-state-changed)
- [VideoSDK Android leave/end room guide](https://docs.videosdk.live/android/guide/video-and-audio-calling-api-sdk/setup-call/leave-end-meeting)
