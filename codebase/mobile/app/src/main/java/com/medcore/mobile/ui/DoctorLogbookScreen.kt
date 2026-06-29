package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorLogbookScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var procedure by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitResult by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intern/Resident Logbook", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("New Procedure Log", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = procedure,
                        onValueChange = { procedure = it },
                        label = { Text("Procedure Observed/Performed") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Clinical Notes / Complications") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            isSubmitting = true
                            scope.kotlinx.coroutines.launch {
                                try {
                                    val body = org.json.JSONObject().apply {
                                        put("procedureName", procedure)
                                        put("notes", notes)
                                    }
                                    com.medcore.mobile.NetworkClient.post("$apiUrl/staff/logbooks", body.toString(), token)
                                    submitResult = "Logbook entry submitted successfully."
                                    procedure = ""
                                    notes = ""
                                } catch (e: Exception) {
                                    submitResult = "Error: ${e.message}"
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = procedure.isNotBlank() && !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Icon(Icons.Default.Edit, contentDescription = "Sign", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit for Guide Sign-off")
                        }
                    }
                    if (submitResult.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(submitResult, color = if (submitResult.startsWith("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
