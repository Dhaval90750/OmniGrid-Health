package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NursingAssessmentScreen(
    patientUhid: String,
    onBack: () -> Unit,
    onSubmit: (type: String, score: Int, details: String) -> Unit
) {
    var assessmentType by remember { mutableStateOf("MorseFalls") }
    var score by remember { mutableStateOf(0) }
    var details by remember { mutableStateOf("") }

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
                RadioButton(selected = assessmentType == "MorseFalls", onClick = { assessmentType = "MorseFalls" })
                Text("Morse Falls Risk", modifier = Modifier.padding(top = 12.dp, end = 16.dp))
                RadioButton(selected = assessmentType == "Braden", onClick = { assessmentType = "Braden" })
                Text("Braden Scale", modifier = Modifier.padding(top = 12.dp))
            }

            OutlinedTextField(
                value = score.toString(),
                onValueChange = { score = it.toIntOrNull() ?: 0 },
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

            Button(
                onClick = { onSubmit(assessmentType, score, details) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Assessment")
            }
        }
    }
}
