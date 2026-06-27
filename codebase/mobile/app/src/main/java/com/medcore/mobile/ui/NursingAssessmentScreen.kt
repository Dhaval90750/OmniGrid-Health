package com.medcore.mobile.ui

import androidx.compose.foundation.background
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
                title = { Text("Nursing Assessment", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                navigationIcon = {
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                        Text("Back", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Patient UHID: $patientUhid", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)

            Row {
                RadioButton(selected = assessmentType == "Morse Falls Scale", onClick = { assessmentType = "Morse Falls Scale" })
                Text("Morse Falls Risk", modifier = Modifier.padding(top = 12.dp, end = 16.dp), style = MaterialTheme.typography.bodyMedium)
                RadioButton(selected = assessmentType == "Braden Scale", onClick = { assessmentType = "Braden Scale" })
                Text("Braden Scale", modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodyMedium)
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
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(errorMsg, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp))
                }
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
                                    put("assessmentType", assessmentType)
                                    put("score", scoreInt)
                                    put("details", details)
                                }.toString()
                                
                                val res = NetworkClient.post("$apiUrl/nursing/assessment", body, token)
                                val json = JSONObject(res)
                                if (json.optString("id").isNotEmpty()) {
                                    onSubmitSuccess()
                                } else {
                                    errorMsg = "Failed to save assessment."
                                }
                            } catch (e: Exception) {
                                errorMsg = "Error: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Assessment", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
