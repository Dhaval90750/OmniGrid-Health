package com.medcore.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispensingQueueScreen(
    onBack: () -> Unit,
    onPrescriptionClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val queue = listOf(
        PharmacyPrescription("RX-9921", "John Doe", "3 Drugs", "Priority"),
        PharmacyPrescription("RX-9922", "Jane Smith", "1 Drug", "Routine"),
        PharmacyPrescription("RX-9923", "Alice Wong", "5 Drugs", "Priority")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pharmacy Dispensing Queue") },
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
                placeholder = { Text("Search by RX# or Patient Name") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(queue) { item ->
                    PharmacyQueueItem(item) { onPrescriptionClick(item.rxNumber) }
                }
            }
        }
    }
}

data class PharmacyPrescription(val rxNumber: String, val patientName: String, val drugCount: String, val urgency: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyQueueItem(item: PharmacyPrescription, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.rxNumber, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(item.patientName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(item.drugCount, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            if (item.urgency == "Priority") {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        item.urgency, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
