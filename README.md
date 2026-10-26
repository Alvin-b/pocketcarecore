# CareConnect Android app

CareConnect is an Android application project built with Kotlin and Jetpack Compose. The repository currently represents a demo/prototype; see [Limitations](#limitations) before using it.

## Requirements

- Android Studio with the Android SDK, or a JDK 17 or newer and the Android command-line SDK tools.
- Android SDK Platform **API 36.1** (the app compiles against API 36.1) and the SDK Build Tools installed through Android Studio's SDK Manager or `sdkmanager`.
- An Android emulator or device for installation and instrumentation tests.

The Gradle wrapper is pinned to Gradle 9.3.1. Use the wrapper commands below rather than installing Gradle separately. When using Android Studio, select a compatible Gradle JDK (JDK 17 or newer; the bundled JDK is suitable).

## Local setup

1. Clone the repository and open its root folder in Android Studio, or run the wrapper from that folder.
2. Install the required Android SDK platform and Build Tools. Set `ANDROID_HOME`/`ANDROID_SDK_ROOT` if your SDK is not at the default location. Android Studio can create the ignored `local.properties` file automatically; do not commit it.
3. The app's debug build configuration expects a repository-root `debug.keystore`. If you do not already have one, create a local development key (the file is ignored by Git):

   ```sh
   keytool -genkeypair -v \
     -keystore debug.keystore \
     -storepass android \
     -alias androiddebugkey \
     -keypass android \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -dname "CN=Android Debug,O=Android,C=US"
   ```

4. Firebase/Google services configuration is not included. The build is configured to warn when `google-services.json` is absent, but Firebase-backed functionality needs a properly configured Firebase project and a local configuration file. Do not commit credentials or service configuration. The optional `.env` file is also local-only; consult `.env.example` for the optional Gemini key convention, and do not commit API keys.

## Build and test

Run these commands from the repository root:

```sh
# Compile and package a debug APK
./gradlew assembleDebug

# Run local unit/Robolectric tests
./gradlew testDebugUnitTest

# Run instrumentation tests on a connected device/emulator
./gradlew connectedDebugAndroidTest
```

The debug APK is written under `app/build/outputs/apk/debug/`. Install it on a connected device with `./gradlew installDebug` or install the APK through Android Studio.

On Windows, use `gradlew.bat` with the same task names (for example, `gradlew.bat assembleDebug`).

## Release signing

Do not use a debug key for distribution. The release build is configured to use an upload keystore whose alias is `upload`; supply the keystore path and passwords through environment variables, not source control:

```sh
export KEYSTORE_PATH="/secure/path/upload-key.jks"
export STORE_PASSWORD="..."
export KEY_PASSWORD="..."
./gradlew assembleRelease
```

The keystore and its passwords are not provided by this repository. Protect and back up the signing key securely. A successful release build alone does not make the app production-ready.

## Limitations

This is a demo/prototype, not a clinical or production telehealth service. Do not use it to provide care, make clinical decisions, store real patient information, or process real appointments or payments. No claim is made that the project meets healthcare privacy, security, regulatory, or operational requirements. Backend services and credentials must be independently configured and reviewed before any real-world use; never place real patient data or secrets in the repository.

Clinician-requested tests and submitted result summaries are demo-only in-memory records; they are not sent to a laboratory and are lost when the app's state resets. Video and voice calls remain disabled until an authenticated server issues short-lived, appointment-scoped VideoSDK tokens. Never put the VideoSDK API secret in the APK, Android `.env`, source code, or `BuildConfig`.

Android app backup is disabled until a reviewed policy for retaining and transferring health-related data is established.


The UteWatch SDK 1.3.5 integration is BLE-only and currently keeps aggregated steps, heart-rate, SpO₂, and sleep summaries in app memory; it does not upload them to the Lovable backend or persist raw samples. Readings are estimates, not for diagnosis, and disappear when the process restarts or demo data is reset. The supplied SDK archive did not include a license statement, so do not commit, publish, or redistribute the vendor AAR until redistribution permission is confirmed. No vendor app key or demo credential is included.
