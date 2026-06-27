package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
                title = { Text("Bedside Vitals", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FA),
                    titleContentColor = Color(0xFF263238),
                    navigationIconContentColor = Color(0xFF263238)
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Patient UHID:", fontSize = 14.sp, color = Color(0xFF546E7A))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(patientUhid, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                }
            }
            
            if (errorMsg.isNotEmpty()) {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        errorMsg, 
                        color = Color(0xFFD32F2F), 
                        style = MaterialTheme.typography.bodyMedium, 
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text("Clinical Measurements", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF263238))

            OutlinedTextField(
                value = temp,
                onValueChange = { temp = it },
                label = { Text("Temperature (°F)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)
            )
            OutlinedTextField(
                value = pulse,
                onValueChange = { pulse = it },
                label = { Text("Pulse (BPM)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)
            )
            OutlinedTextField(
                value = bp,
                onValueChange = { bp = it },
                label = { Text("Blood Pressure (mmHg)") },
                placeholder = { Text("e.g. 120/80") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)
            )
            OutlinedTextField(
                value = spo2,
                onValueChange = { spo2 = it },
                label = { Text("SpO2 (%)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)
            )
            OutlinedTextField(
                value = pain,
                onValueChange = { pain = it },
                label = { Text("Pain Level (1-10)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            } else {
                Button(
                    onClick = {
                        if (temp.isBlank() || pulse.isBlank() || bp.isBlank()) {
                            errorMsg = "Temperature, Pulse, and BP are required fields."
                            return@Button
                        }
                        isLoading = true
                        errorMsg = ""
                        scope.launch {
                            try {
                                val body = JSONObject().apply {
                                    put("temperatureFahrenheit", temp.toDoubleOrNull() ?: 98.6)
                                    put("pulseBpm", pulse.toIntOrNull() ?: 80)
                                    put("bloodPressure", bp)
                                    put("spo2Percent", spo2.toIntOrNull() ?: 98)
                                    put("painLevel", pain.toIntOrNull() ?: 0)
                                }.toString()
                                
                                val res = NetworkClient.post("$apiUrl/patients/$patientId/vitals", body, token)
                                val json = JSONObject(res)
                                val savedId = json.optString("id")
                                if (savedId.isNotEmpty()) {
                                    onSubmit(temp, pulse, bp, spo2, pain)
                                } else {
                                    errorMsg = "Failed to synchronize vitals with EMR."
                                }
                            } catch (e: Exception) {
                                errorMsg = "Error: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text("Save to Electronic Record", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
