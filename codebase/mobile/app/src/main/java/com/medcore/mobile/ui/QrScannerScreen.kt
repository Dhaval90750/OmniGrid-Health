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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import com.medcore.mobile.NetworkClient
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun QrScannerScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit,
    onPatientDetected: (JSONObject) -> Unit
) {
    var isFetching by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("QR Barcode Scanner", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Real Camera Viewfinder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(2.dp, if (hasCameraPermission) Color(0xFF1A73E8) else Color.Red, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission) {
                com.medcore.mobile.ui.components.QrCameraPreview(
                    onBarcodeScanned = { scannedValue ->
                        if (!isFetching) {
                            isFetching = true
                            errorMsg = ""
                            scope.launch {
                                try {
                                    val parsedUhid = try {
                                        JSONObject(scannedValue).optString("uhid", scannedValue)
                                    } catch (e: Exception) {
                                        scannedValue
                                    }
                                    val encodedQ = java.net.URLEncoder.encode(parsedUhid, "UTF-8")
                                    val res = NetworkClient.get("$apiUrl/patients/search?q=$encodedQ", token)
                                    val jsonArray = JSONArray(res)
                                    if (jsonArray.length() > 0) {
                                        val patient = jsonArray.getJSONObject(0)
                                        onPatientDetected(patient)
                                    } else {
                                        errorMsg = "Patient not found for: $scannedValue"
                                        isFetching = false
                                    }
                                } catch (e: Exception) {
                                    errorMsg = "Error fetching patient: ${e.localizedMessage}"
                                    isFetching = false
                                }
                            }
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
                    Text("Camera permission is required to scan QR codes.", color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMsg, color = Color.Red, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isFetching) {
            CircularProgressIndicator()
        }
    }
}
