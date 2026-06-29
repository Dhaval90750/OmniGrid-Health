package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DischargeInitiationScreen(
    patientName: String,
    onBack: () -> Unit,
    onInitiateSuccess: () -> Unit
) {
    var diagnosis by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Stable") }
    var instructions by remember { mutableStateOf("") }
    
    val conditions = listOf("Stable", "Improved", "Guarded", "Against Medical Advice")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Initiate Discharge") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Patient: $patientName", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = diagnosis, onValueChange = { diagnosis = it },
                label = { Text("Final Diagnosis") }, modifier = Modifier.fillMaxWidth()
            )
            
            Text("Condition at Discharge", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                conditions.take(2).forEach { cond ->
                    FilterChip(
                        selected = condition == cond,
                        onClick = { condition = cond },
                        label = { Text(cond) }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                conditions.drop(2).forEach { cond ->
                    FilterChip(
                        selected = condition == cond,
                        onClick = { condition = cond },
                        label = { Text(cond) }
                    )
                }
            }
            
            OutlinedTextField(
                value = instructions, onValueChange = { instructions = it },
                label = { Text("Follow-up Instructions") }, modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onInitiateSuccess,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Initiate Discharge Process")
            }
        }
    }
}
