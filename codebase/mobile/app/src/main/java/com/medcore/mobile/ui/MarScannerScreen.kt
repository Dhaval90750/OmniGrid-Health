package com.medcore.mobile.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import com.medcore.mobile.NetworkClient
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarScannerScreen(
    apiUrl: String,
    token: String,
    patientId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    var manualDrugCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MAR 5-Rights Scanner", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A73E8)),
                navigationIcon = {
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                        Text("Back", color = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Scan Patient Wristband & Medication Barcode", textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .border(2.dp, if (hasCameraPermission) Color(0xFF4CAF50) else Color.Red, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    com.medcore.mobile.ui.components.QrCameraPreview(
                        onBarcodeScanned = { scannedValue ->
                            // Auto-populate and attempt submit if not currently loading
                            if (!isLoading) {
                                manualDrugCode = scannedValue
                                // We can auto-submit or just let the user hit Administer
                            }
                        }
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Permission Denied",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Camera permission is required.", color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                            Text("Grant Permission")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Simulator/Manual Override: Enter Drug Barcode / Details", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = manualDrugCode,
                onValueChange = { manualDrugCode = it },
                label = { Text("e.g. Paracetamol 500mg PO") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }
            if (successMsg.isNotEmpty()) {
                Text(successMsg, color = Color(0xFF4CAF50))
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (patientId.isEmpty()) {
                            errorMsg = "Invalid patient ID. Please scan patient QR first."
                            return@Button
                        }
                        if (manualDrugCode.isEmpty()) {
                            errorMsg = "Please scan or enter medication details."
                            return@Button
                        }

                        isLoading = true
                        errorMsg = ""
                        successMsg = ""
                        scope.launch {
                            try {
                                val body = JSONObject().apply {
                                    put("patientId", patientId)
                                    put("drugId", java.util.UUID.randomUUID().toString()) // Mock drug ID since it's free text
                                    put("dose", "1 Tablet")
                                    put("route", "PO")
                                    put("notes", manualDrugCode)
                                }.toString()

                                NetworkClient.post("$apiUrl/nursing/mar", body, token)
                                successMsg = "Medication Administration Recorded!"
                                manualDrugCode = ""
                            } catch (e: Exception) {
                                errorMsg = "Error: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Administer Medication")
                }
            }
        }
    }
}
