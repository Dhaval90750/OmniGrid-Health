package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medcore.mobile.data.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoapNoteEditorScreen(
    sessionManager: SessionManager,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var subjective by remember { mutableStateOf("") }
    var objective by remember { mutableStateOf("") }
    var assessment by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }
    
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("SOAP Note Editor", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = subjective,
            onValueChange = { subjective = it },
            label = { Text("Subjective") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = objective,
            onValueChange = { objective = it },
            label = { Text("Objective") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = assessment,
            onValueChange = { assessment = it },
            label = { Text("Assessment / Diagnoses") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = plan,
            onValueChange = { plan = it },
            label = { Text("Plan / Orders") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                isSaving = true
                // Simulate network save
                onSaveSuccess()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Save Clinical Note")
            }
        }
    }
}
