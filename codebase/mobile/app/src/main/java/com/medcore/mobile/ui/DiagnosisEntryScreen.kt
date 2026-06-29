package com.medcore.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisEntryScreen(
    onBack: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedDiagnoses = remember { mutableStateListOf<String>() }
    
    val mockIcd = listOf(
        "I10 - Essential (primary) hypertension",
        "E11.9 - Type 2 diabetes mellitus without complications",
        "J45.909 - Unspecified asthma, uncomplicated",
        "K21.9 - Gastro-esophageal reflux disease without esophagitis",
        "M54.5 - Low back pain"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ICD-10 Diagnosis Entry") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search ICD-10 Code or Description") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            Text(
                "Selected: ${selectedDiagnoses.size}", 
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mockIcd) { icd ->
                    val isSelected = selectedDiagnoses.contains(icd)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            if (isSelected) selectedDiagnoses.remove(icd) else selectedDiagnoses.add(icd)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(icd, modifier = Modifier.weight(1f))
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
            
            Button(
                onClick = { onSave(selectedDiagnoses) },
                modifier = Modifier.fillMaxWidth().height(80.dp).padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedDiagnoses.isNotEmpty()
            ) {
                Text("Save Diagnoses")
            }
        }
    }
}
