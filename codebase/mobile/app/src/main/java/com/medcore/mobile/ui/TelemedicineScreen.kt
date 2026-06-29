package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelemedicineScreen(
    apiUrl: String,
    token: String,
    patientName: String,
    patientUhid: String,
    doctorName: String,
    onBack: () -> Unit
) {
    var inCall by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var activeRoomId by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (!inCall) {
                TopAppBar(
                    title = { Text("Telemedicine", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        },
        containerColor = if (inCall) Color.Black else MaterialTheme.colorScheme.background
    ) { padding ->
        if (!inCall) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(80.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text("Upcoming Consultation", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Patient: $patientName (UHID: $patientUhid)", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(48.dp))
                
                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    Button(
                        onClick = {
                            isLoading = true
                            errorMsg = ""
                            scope.launch {
                                try {
                                    val body = org.json.JSONObject().apply {
                                        put("patientName", patientName)
                                        put("doctorName", doctorName)
                                    }.toString()
                                    
                                    val res = com.medcore.mobile.NetworkClient.post("$apiUrl/telemedicine/rooms", body, token)
                                    val json = org.json.JSONObject(res)
                                    activeRoomId = json.getString("roomId")
                                    inCall = true
                                } catch (e: Exception) {
                                    errorMsg = "Failed to connect to telemedicine server: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Start Video Session", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Main Video (Patient)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Waiting for patient to join...", color = Color.LightGray)
                        Text("Room ID: ${activeRoomId?.take(8)}...", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                
                // PiP Video (Doctor)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 120.dp, end = 24.dp)
                        .size(width = 120.dp, height = 160.dp)
                        .background(Color.DarkGray, RoundedCornerShape(12.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your Camera", color = Color.LightGray, fontSize = 12.sp)
                }

                // Call Controls
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    activeRoomId?.let { id ->
                                        val body = org.json.JSONObject().apply { put("status", "COMPLETED") }.toString()
                                        com.medcore.mobile.NetworkClient.post("$apiUrl/telemedicine/rooms/$id/status", body, token) // Using post for PUT mock if NetworkClient doesn't have PUT
                                    }
                                } catch (e: Exception) {
                                    // Ignore errors on end
                                }
                                inCall = false
                                activeRoomId = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Text("END")
                    }
                }
            }
        }
    }
}
