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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var type by remember { mutableStateOf("Patient Fall") }
    var description by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Incident", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.error),
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
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Incident Category (e.g. Fall, Med Error)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6
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
                        if (description.isEmpty()) {
                            errorMsg = "Please provide a description."
                            return@Button
                        }
                        
                        isLoading = true
                        errorMsg = ""
                        
                        scope.launch {
                            try {
                                val body = JSONObject().apply {
                                    put("title", "Mobile Incident Report")
                                    put("category", type)
                                    put("severity", "Medium")
                                    put("description", description)
                                    put("incidentTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                    put("location", "Ward")
                                    put("reportedBy", "Nurse (Mobile)")
                                }.toString()
                                
                                NetworkClient.post("$apiUrl/operations/incidents", body, token)
                                onSubmitSuccess()
                            } catch (e: Exception) {
                                errorMsg = "Failed to submit incident: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Submit Incident", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
