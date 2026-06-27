package com.medcore.mobile.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import com.medcore.mobile.NetworkClient
import org.json.JSONObject

@Composable
fun AiScribeScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var transcript by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var soapResult by remember { mutableStateOf<JSONObject?>(null) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    val context = LocalContext.current
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasAudioPermission = isGranted
        if (isGranted) {
            isRecording = true
            transcript = "Patient presents with a mild fever and severe headache for the past 2 days. Blood pressure is 120/80 mmHg. Heart rate is 85 bpm. Prescribing paracetamol for the fever."
            isRecording = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("AI Voice Scribe Portal", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (transcript.isEmpty()) "Tap simulate to dictate clinical notes..." else transcript,
                    fontSize = 14.sp,
                    color = if (transcript.isEmpty()) Color.Gray else Color.Black
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    if (hasAudioPermission) {
                        isRecording = true
                        transcript = "Patient presents with a mild fever and severe headache for the past 2 days. Blood pressure is 120/80 mmHg. Heart rate is 85 bpm. Prescribing paracetamol for the fever."
                        isRecording = false
                    } else {
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335))
            ) {
                Text("🎙 Record Voice")
            }
            
            Button(
                onClick = {
                    isProcessing = true
                    scope.launch {
                        try {
                            errorMsg = ""
                            soapResult = null
                            val body = JSONObject().apply {
                                put("transcript", transcript)
                            }.toString()
                            val res = NetworkClient.post("$apiUrl/ai/extract", body, token)
                            soapResult = JSONObject(res)
                        } catch (e: Exception) {
                            errorMsg = "Failed to connect to AI Server: ${e.localizedMessage}"
                        } finally {
                            isProcessing = false
                        }
                    }
                },
                enabled = transcript.isNotEmpty() && !isProcessing,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
            ) {
                Text(if (isProcessing) "Processing..." else "Process SOAP")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = Color.Red, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (soapResult != null) {
            Text("AI Extracted SOAP Note Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            val soapObj = soapResult!!.optJSONObject("generated_soap_note")
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (soapObj != null) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Subjective", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                                Text(soapObj.optString("Subjective", "N/A"), fontSize = 13.sp)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Objective", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                                Text(soapObj.optString("Objective", "N/A"), fontSize = 13.sp)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Assessment", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                                Text(soapObj.optString("Assessment", "N/A"), fontSize = 13.sp)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Plan", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                                Text(soapObj.optString("Plan", "N/A"), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
