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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErTriageScreen(
    patientName: String,
    onBack: () -> Unit,
    onTriageSuccess: () -> Unit
) {
    var category by remember { mutableStateOf("Green") }
    var chiefComplaint by remember { mutableStateOf("") }
    
    val categories = listOf(
        TriageCategory("Red", "Immediate", Color(0xFFDC2626)),
        TriageCategory("Yellow", "Urgent", Color(0xFFF59E0B)),
        TriageCategory("Green", "Standard", Color(0xFF10B981))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ER Triage Assessment") },
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
            
            Text("Triage Category", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = category == cat.name,
                        onClick = { category = cat.name },
                        label = { Text(cat.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = cat.color,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            
            OutlinedTextField(
                value = chiefComplaint, onValueChange = { chiefComplaint = it },
                label = { Text("Chief Complaint") }, modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onTriageSuccess,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = categories.find { it.name == category }?.color ?: MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Complete Triage", color = Color.White)
            }
        }
    }
}

data class TriageCategory(val name: String, val description: String, val color: Color)
