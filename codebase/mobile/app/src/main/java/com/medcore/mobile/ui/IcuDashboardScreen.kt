package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
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
fun IcuDashboardScreen(
    onBack: () -> Unit,
    onPatientClick: (String) -> Unit
) {
    val icuPatients = listOf(
        IcuPatient("Bed 1", "John Doe", "Ventilated", "High", Color(0xFFEF4444)),
        IcuPatient("Bed 2", "Jane Smith", "Post-op", "Medium", Color(0xFFF59E0B)),
        IcuPatient("Bed 3", "Alice Wong", "Stable", "Low", Color(0xFF10B981))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ICU Command Center") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(icuPatients) { patient ->
                IcuPatientCard(patient) { onPatientClick(patient.name) }
            }
        }
    }
}

data class IcuPatient(val bed: String, val name: String, val support: String, val acuity: String, val acuityColor: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IcuPatientCard(patient: IcuPatient, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(patient.bed, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    if (patient.acuity == "High") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
                Text(patient.name, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text("Support: ${patient.support}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("Acuity", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = patient.acuityColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        patient.acuity,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = patient.acuityColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
