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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medcore.mobile.viewmodels.CriticalCareViewModel
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IcuDashboardScreen(
    onBack: () -> Unit,
    onPatientClick: (String) -> Unit,
    viewModel: CriticalCareViewModel = viewModel()
) {
    val icuPatients by viewModel.icuPatients.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchIcuDashboard()
    }

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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(icuPatients) { patient ->
                    IcuPatientCard(patient) { onPatientClick(patient.optString("name")) }
                }
            }
        }
    }
}

// Removed IcuPatient class

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IcuPatientCard(patient: JSONObject, onClick: () -> Unit) {
    val apacheScore = patient.optInt("apacheScore", 0)
    val acuity = if (apacheScore > 20) "High" else if (apacheScore > 10) "Medium" else "Low"
    val acuityColor = when(acuity) {
        "High" -> Color(0xFFEF4444)
        "Medium" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(patient.optString("bed"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    if (acuity == "High") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
                Text(patient.optString("name"), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text("APACHE II Score: $apacheScore", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("Acuity", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = acuityColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        acuity,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = acuityColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
