# VideoSDK integration notes

## Current state

The Android call manager currently fails closed: it does not join a provider room and reports that secure call setup is unavailable rather than claiming a connection. The former simulated/WebView path is not a verified call integration.

The VideoSDK dashboard has an existing active `Auto Generated` API key. Its secret remained masked; no key was revealed, copied, or created during this review. This repository contains no authenticated token backend or server-side appointment authorization, so live calls must remain disabled until those pieces are configured.

## Supported implementation paths

VideoSDK documents a native Android RTC SDK and an Android WebView Prebuilt path. Choose one path deliberately rather than mixing simulated manager state with a separate embedded page.

For the native path, the official Android quick start documents `live.videosdk:rtc-android-sdk:2.2.0`, SDK initialization, configuring with a token, creating a meeting, registering `MeetingEventListener`, and joining the room. Drive connected, disconnected, participant, and error UI from SDK callbacks. Mute, camera, and leave controls must call SDK methods and release the room lifecycle.

For Prebuilt in WebView, the official quick start uses a room ID and token in its hosted embed URL. Android runtime camera and microphone permissions are required. Restrict permission grants to the trusted VideoSDK origin and only the requested media resources; the sample's broad grant is not sufficient as a production security policy.

## Authorization and privacy requirements

VideoSDK's Android token documentation recommends generating access tokens on a backend so API secrets stay safe. Issue short-lived tokens scoped to the authorized appointment room and participant, and validate the signed-in user's role and appointment access server-side. Never package the VideoSDK secret in the APK, Android `.env`, source, or client-side BuildConfig. Do not use a predictable appointment-derived room slug as the authorization boundary.

Until there is an authenticated token endpoint and provider credentials in the server's protected environment, the app should fail closed and clearly say that secure call setup is unavailable. It must not show fabricated connection quality, encryption, latency, or connected status.

## Official sources

- [VideoSDK Android quick start](https://docs.videosdk.live/android/guide/video-and-audio-calling-api-sdk/quick-start)
- [VideoSDK Android token authentication](https://docs.videosdk.live/android/0.1.x/guide/video-and-audio-calling-api-sdk/authentication-and-token)
- [VideoSDK Android Prebuilt WebView quick start](https://docs.videosdk.live/prebuilt/guide/prebuilt-video-and-audio-calling/quick-start-android)
- [VideoSDK Android meeting-state callback reference](https://docs.videosdk.live/android-sdk-reference/zn-android-sdk/live.videosdk.rtc.android.listeners/-meeting-event-listener/on-meeting-state-changed)
- [VideoSDK Android leave/end room guide](https://docs.videosdk.live/android/guide/video-and-audio-calling-api-sdk/setup-call/leave-end-meeting)
