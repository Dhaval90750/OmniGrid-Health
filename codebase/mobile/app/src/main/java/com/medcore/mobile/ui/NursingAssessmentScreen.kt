package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.medcore.mobile.NetworkClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NursingAssessmentScreen(
    patientUhid: String,
    patientId: String,
    apiUrl: String,
    token: String,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var assessmentType by remember { mutableStateOf("Morse Falls Scale") }
    var score by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nursing Assessment", color = Color.White) },
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
            Text("Patient UHID: $patientUhid", color = Color.Gray)

            Row {
                RadioButton(selected = assessmentType == "Morse Falls Scale", onClick = { assessmentType = "Morse Falls Scale" })
                Text("Morse Falls Risk", modifier = Modifier.padding(top = 12.dp, end = 16.dp))
                RadioButton(selected = assessmentType == "Braden Scale", onClick = { assessmentType = "Braden Scale" })
                Text("Braden Scale", modifier = Modifier.padding(top = 12.dp))
            }

            OutlinedTextField(
                value = score,
                onValueChange = { score = it },
                label = { Text("Calculated Score") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Assessment Details/Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        val scoreInt = score.toIntOrNull()
                        if (patientId.isEmpty()) {
                            errorMsg = "No active patient selected."
                            return@Button
                        }
                        if (scoreInt == null) {
                            errorMsg = "Please enter a valid numeric score."
                            return@Button
                        }
                        
                        isLoading = true
                        errorMsg = ""
                        
                        scope.launch {
                            try {
                                val body = JSONObject().apply {
                                    put("patientId", patientId)
                                    put("type", assessmentType)
                                    put("score", scoreInt)
                                    put("riskLevel", if (scoreInt > 45) "High Risk" else "Low Risk")
                                    put("notes", details)
                                    put("recordedBy", "Nurse (Mobile)")
                                }.toString()
                                
                                NetworkClient.post("$apiUrl/nursing/assessments", body, token)
                                onSubmitSuccess()
                            } catch (e: Exception) {
                                errorMsg = "Failed to submit assessment: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Assessment")
                }
            }
        }
    }
}
