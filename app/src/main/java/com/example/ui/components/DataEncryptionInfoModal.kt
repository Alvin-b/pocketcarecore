package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.MedicalDataEncryption
import com.example.ui.theme.*

/**
 * Data Encryption & Security Inspector Dialog.
 * Explains and validates the end-to-end AES-256-GCM data encryption,
 * KMPDC digital signatures, and Kenya Data Protection Act 2019 compliance.
 */
@Composable
fun DataEncryptionInfoModal(
    onDismiss: () -> Unit
) {
    var testData by remember { mutableStateOf("Patient: Joy Kamau | Diagnosis: BA00 Hypertension | Rx: Amlodipine 5mg") }
    var encryptedResult by remember { mutableStateOf(MedicalDataEncryption.encrypt(testData)) }
    var decryptedResult by remember { mutableStateOf(MedicalDataEncryption.decrypt(encryptedResult)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("data_encryption_modal"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TeleSuccess.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = TeleSuccess, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text("Data Encryption & Privacy", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("AES-256-GCM Cryptographic Security", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active Protection Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TeleSuccessSoft)
                        .border(1.dp, TeleSuccess.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TeleSuccess))
                        Column {
                            Text("Active End-to-End Encryption", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TeleSuccess)
                            Text("All patient records, prescriptions, and biometric wearable streams are encrypted at rest and in transit.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // Security Specifications Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, TeleBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("CRYPTOGRAPHIC STANDARDS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    SecuritySpecRow(label = "Cipher Suite", value = "AES-256-GCM (128-bit Auth Tag)")
                    SecuritySpecRow(label = "Key Exchange", value = "ECDH P-256 & Hardware Keystore")
                    SecuritySpecRow(label = "Digital Signatures", value = "HMAC-SHA256 Regulatory Seal")
                    SecuritySpecRow(label = "EHR Compliance", value = "Kenya Data Protection Act 2019 (§29)")
                    SecuritySpecRow(label = "Telemedicine Rules", value = "KMPDC Telehealth Standard 2024")
                }

                // Live Cryptographic Verification Tool
                Text("LIVE ENCRYPTION VERIFICATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                OutlinedTextField(
                    value = testData,
                    onValueChange = {
                        testData = it
                        val enc = MedicalDataEncryption.encrypt(it)
                        encryptedResult = enc
                        decryptedResult = MedicalDataEncryption.decrypt(enc)
                    },
                    label = { Text("Clinical Payload Preview", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )

                // Ciphertext Output
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .padding(10.dp)
                ) {
                    Text("CIPHERTEXT (AES-256-GCM Base64):", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = encryptedResult.cipherText.take(48) + "...",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "IV: ${encryptedResult.iv} · Tag: ${encryptedResult.authTag.take(16)}...",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF64748B)
                    )
                }

                // Decrypted Output
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TeleMuted)
                        .padding(10.dp)
                ) {
                    Text("AUTHENTICATED DECRYPTION:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TeleSuccess)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = decryptedResult,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            CareButton(
                text = "Close Security Panel",
                onClick = onDismiss,
                variant = BtnVariant.PRIMARY
            )
        }
    )
}

@Composable
private fun SecuritySpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
