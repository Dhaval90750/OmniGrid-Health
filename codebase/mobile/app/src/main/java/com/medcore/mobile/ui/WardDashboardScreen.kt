package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardDashboardScreen(
    wardName: String,
    onBack: () -> Unit,
    onPatientClick: (String) -> Unit
) {
    val patients = listOf(
        WardPatient("Bed 101", "John Doe", "Stable", Color(0xFF10B981)),
        WardPatient("Bed 102", "Jane Smith", "Critical", Color(0xFFEF4444)),
        WardPatient("Bed 103", "Alice Wong", "Observation", Color(0xFFF59E0B)),
        WardPatient("Bed 104", "Bob Brown", "Stable", Color(0xFF10B981))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$wardName Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            WardStatsRow()
            
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(patients) { patient ->
                    WardPatientCard(patient) { onPatientClick(patient.name) }
                }
            }
        }
    }
}

data class WardPatient(val bed: String, val name: String, val status: String, val statusColor: Color)

@Composable
fun WardStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Total", "24", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        StatCard("Critical", "3", Color(0xFFEF4444), Modifier.weight(1f))
        StatCard("Pending", "8", Color(0xFFF59E0B), Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 12.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardPatientCard(patient: WardPatient, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().background(Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(patient.bed, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(patient.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = patient.statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    patient.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = patient.statusColor,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
