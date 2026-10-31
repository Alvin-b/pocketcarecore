package com.example.video

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object VideoSdkTokenHelper {
    const val DEFAULT_PROJECT_ID = "fe33aadd-4607-477a-8b7-f2683e9f6f3e"
    const val DEFAULT_ORG_NAME = "Malik's Org"
    const val DASHBOARD_URL = "https://us.app.videosdk.live/dashboard/ADWLQ29Y0YHD"

    /**
     * Verifies if a given string has the structure of a signed 3-part JWT (header.payload.signature).
     */
    fun isSignedToken(token: String?): Boolean {
        if (token.isNullOrBlank()) return false
        val parts = token.trim().split(".")
        return parts.size == 3 && parts.all { it.isNotBlank() }
    }

    /**
     * Generates a signed JSON Web Token (JWT) using HS256 algorithm as required by VideoSDK.live
     *
     * @param apiKey The VideoSDK API / Project Key (e.g., fe33aadd-4607-477a-8b7-f2683e9f6f3e)
     * @param secretKey The secret key from VideoSDK dashboard
     * @param expiresInSeconds Expiration duration (defaults to 24 hours = 86400s)
     */
    fun generateToken(
        apiKey: String,
        secretKey: String,
        expiresInSeconds: Long = 86400L
    ): String {
        val cleanApiKey = apiKey.trim().ifBlank { DEFAULT_PROJECT_ID }
        val cleanSecret = secretKey.trim()

        if (cleanSecret.isBlank()) {
            return ""
        }

        val header = """{"alg":"HS256","typ":"JWT"}"""

        val now = System.currentTimeMillis() / 1000L
        val exp = now + expiresInSeconds

        val payload = """{"apikey":"$cleanApiKey","permissions":["allow_join","allow_mod"],"version":2,"roles":["rtc"],"iat":$now,"exp":$exp}"""

        val base64Header = base64UrlEncode(header.toByteArray(Charsets.UTF_8))
        val base64Payload = base64UrlEncode(payload.toByteArray(Charsets.UTF_8))
        val unsignedToken = "$base64Header.$base64Payload"

        return try {
            val mac = Mac.getInstance("HmacSHA256")
            val secretKeySpec = SecretKeySpec(cleanSecret.toByteArray(Charsets.UTF_8), "HmacSHA256")
            mac.init(secretKeySpec)
            val signature = mac.doFinal(unsignedToken.toByteArray(Charsets.UTF_8))
            val base64Signature = base64UrlEncode(signature)
            "$unsignedToken.$base64Signature"
        } catch (_: Throwable) {
            ""
        }
    }

    private fun base64UrlEncode(bytes: ByteArray): String {
        return try {
            java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        } catch (_: Throwable) {
            try {
                android.util.Base64.encodeToString(
                    bytes,
                    android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP
                )
            } catch (_: Throwable) {
                ""
            }
        }
    }
}
