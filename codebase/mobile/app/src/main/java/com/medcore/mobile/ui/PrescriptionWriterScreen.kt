package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PrescriptionItem(
    val drugName: String,
    val dosage: String,
    val frequency: String,
    val duration: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionWriterScreen(
    patientName: String,
    onBack: () -> Unit,
    onSave: (List<PrescriptionItem>) -> Unit
) {
    val items = remember { mutableStateListOf<PrescriptionItem>() }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prescription Writer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Drug")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Patient: $patientName", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No drugs added. Tap + to add.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items) { item ->
                        PrescriptionCard(item) { items.remove(item) }
                    }
                    item {
                        Button(
                            onClick = { onSave(items) },
                            modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Finalize & Sign Prescription")
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddDrugDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { items.add(it); showAddDialog = false }
        )
    }
}

@Composable
fun PrescriptionCard(item: PrescriptionItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.drugName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${item.dosage} • ${item.frequency} • ${item.duration}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDrugDialog(onDismiss: () -> Unit, onAdd: (PrescriptionItem) -> Unit) {
    var drugName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("1-0-1") }
    var duration by remember { mutableStateOf("5 Days") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = drugName, onValueChange = { drugName = it }, label = { Text("Drug Name") })
                OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage (e.g. 500mg)") })
                OutlinedTextField(value = frequency, onValueChange = { frequency = it }, label = { Text("Frequency") })
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration") })
            }
        },
        confirmButton = {
            Button(onClick = { if (drugName.isNotEmpty()) onAdd(PrescriptionItem(drugName, dosage, frequency, duration)) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
