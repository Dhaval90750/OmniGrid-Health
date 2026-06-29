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
    
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                transcript = results[0]
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text("AI Voice Scribe Portal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (transcript.isEmpty()) "Tap to record clinical dictation..." else transcript,
                    fontSize = 16.sp,
                    color = if (transcript.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                    }
                    try {
                        speechRecognizerLauncher.launch(intent)
                    } catch (e: Exception) {
                        errorMsg = "Speech recognition not supported on this device"
                    }
                },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("🎙 Record", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Process SOAP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (soapResult != null) {
            Text("Structured Clinical Note", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            
            val soapObj = soapResult!!.optJSONObject("generated_soap_note")
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (soapObj != null) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text("Subjective", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(soapObj.optString("Subjective", "N/A"), fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text("Objective", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(soapObj.optString("Objective", "N/A"), fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text("Assessment", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(soapObj.optString("Assessment", "N/A"), fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text("Plan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(soapObj.optString("Plan", "N/A"), fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}
