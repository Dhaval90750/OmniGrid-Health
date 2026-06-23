package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.medcore.mobile.NetworkClient
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsEntryScreen(
    patientUhid: String,
    patientId: String,
    apiUrl: String,
    token: String,
    onBack: () -> Unit,
    onSubmit: (temp: String, pulse: String, bp: String, spo2: String, pain: String) -> Unit
) {
    var temp by remember { mutableStateOf("") }
    var pulse by remember { mutableStateOf("") }
    var bp by remember { mutableStateOf("") }
    var spo2 by remember { mutableStateOf("") }
    var pain by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bedside Vitals", color = Color.White) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Patient UHID: $patientUhid", fontSize = 16.sp, color = Color.Gray)
            
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            OutlinedTextField(
                value = temp,
                onValueChange = { temp = it },
                label = { Text("Temperature (°F)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = pulse,
                onValueChange = { pulse = it },
                label = { Text("Pulse (BPM)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = bp,
                onValueChange = { bp = it },
                label = { Text("Blood Pressure (mmHg)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = spo2,
                onValueChange = { spo2 = it },
                label = { Text("SpO2 (%)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = pain,
                onValueChange = { pain = it },
                label = { Text("Pain Level (1-10)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        if (patientId.isEmpty()) {
                            errorMsg = "Invalid patient ID. Cannot save vitals."
                            return@Button
                        }
                        isLoading = true
                        errorMsg = ""
                        scope.launch {
                            try {
                                val body = JSONObject().apply {
                                    put("patientId", patientId)
                                    put("temperature", temp.toDoubleOrNull() ?: 98.6)
                                    put("bloodPressure", if (bp.isEmpty()) "120/80" else bp)
                                    put("heartRate", pulse.toIntOrNull() ?: 72)
                                    put("respiratoryRate", 16) // Default/mock for now as there's no UI field
                                    put("oxygenSaturation", spo2.toDoubleOrNull() ?: 99.0)
                                }.toString()
                                
                                NetworkClient.post("$apiUrl/nursing/vitals", body, token)
                                onSubmit(temp, pulse, bp, spo2, pain)
                            } catch (e: Exception) {
                                errorMsg = "Failed to sync vitals: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save & Sync Vitals")
                }
            }
        }
    }
}
