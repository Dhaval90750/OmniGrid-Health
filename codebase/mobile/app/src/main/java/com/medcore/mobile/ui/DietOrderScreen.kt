package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietOrderScreen(
    patientId: String,
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var dietType by remember { mutableStateOf("Diabetic") }
    var instructions by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prescribe Diet Order", fontWeight = FontWeight.Bold) },
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
                    Text("Dietary Requirements", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = dietType,
                        onValueChange = { dietType = it },
                        label = { Text("Diet Type (e.g., NPO, Clear Liquid, Diabetic)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Special Instructions (e.g., Low Sodium)") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 4
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    var isSubmitting by remember { mutableStateOf(false) }
                    var submitResult by remember { mutableStateOf("") }
                    val scope = rememberCoroutineScope()
                    
                    Button(
                        onClick = {
                            isSubmitting = true
                            scope.kotlinx.coroutines.launch {
                                try {
                                    val body = org.json.JSONObject().apply {
                                        put("priority", "Routine")
                                        put("description", "Diet Order: $dietType. Instructions: $instructions. Patient: $patientId")
                                        put("status", "Pending")
                                    }
                                    com.medcore.mobile.NetworkClient.post("$apiUrl/operations/work-orders", body.toString(), token)
                                    submitResult = "Diet order sent to kitchen."
                                    dietType = ""
                                    instructions = ""
                                } catch (e: Exception) {
                                    submitResult = "Error: ${e.message}"
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary)
                        } else {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Order", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send to Kitchen")
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
