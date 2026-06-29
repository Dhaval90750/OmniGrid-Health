package com.medcore.mobile.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medcore.mobile.viewmodels.PrescriptionViewModel
import org.json.JSONObject

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
    onSave: () -> Unit,
    viewModel: PrescriptionViewModel = viewModel()
) {
    val items = remember { mutableStateListOf<JSONObject>() }
    var showAddDialog by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()

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
                            onClick = { 
                                viewModel.savePrescription("pat_123", items) {
                                    onSave()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            else Text("Finalize & Sign Prescription")
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        // Need to pass viewModel for searching
        AddDrugDialog(
            viewModel = viewModel,
            onDismiss = { showAddDialog = false },
            onAdd = { items.add(it); showAddDialog = false }
        )
    }
}

@Composable
fun PrescriptionCard(item: JSONObject, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.optString("name"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${item.optString("dosage", "500mg")} • ${item.optString("frequency", "1-0-1")} • ${item.optString("duration", "5 Days")}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDrugDialog(
    viewModel: PrescriptionViewModel,
    onDismiss: () -> Unit,
    onAdd: (JSONObject) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()

    LaunchedEffect(query) {
        viewModel.searchDrugs(query)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = query, onValueChange = { query = it },
                    label = { Text("Search Drug") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(searchResults) { result ->
                        Text(
                            result.optString("name"),
                            modifier = Modifier.fillMaxWidth().clickable {
                                onAdd(result)
                            }.padding(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}
