package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medcore.mobile.viewmodels.IpdViewModel
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmitPatientScreen(
    patientName: String,
    onBack: () -> Unit,
    onAdmitSuccess: () -> Unit,
    viewModel: IpdViewModel = viewModel()
) {
    var selectedWard by remember { mutableStateOf("General Ward") }
    var selectedBed by remember { mutableStateOf<JSONObject?>(null) }
    
    val wards = listOf("General Ward", "Private Wing", "ICU", "ER Observation")
    val beds by viewModel.beds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(selectedWard) {
        viewModel.fetchBedMatrix(selectedWard)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admit Patient") },
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
                .padding(16.dp)
        ) {
            Text("Patient: $patientName", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Select Ward", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            ScrollableTabRow(
                selectedTabIndex = wards.indexOf(selectedWard),
                edgePadding = 0.dp,
                containerColor = Color.Transparent
            ) {
                wards.forEach { ward ->
                    Tab(
                        selected = selectedWard == ward,
                        onClick = { selectedWard = ward },
                        text = { Text(ward) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Select Available Bed", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(beds) { bed ->
                    val isAvailable = bed.optBoolean("isAvailable", false)
                    val label = bed.optString("label")
                    Card(
                        modifier = Modifier
                            .height(60.dp)
                            .clickable(enabled = isAvailable) { selectedBed = bed },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                selectedBed == bed -> MaterialTheme.colorScheme.primary
                                !isAvailable -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                label,
                                fontSize = 12.sp,
                                color = if (selectedBed == bed) Color.White else if (!isAvailable) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            
            Button(
                onClick = {
                    selectedBed?.let { bed ->
                        viewModel.admitPatient("pat_123", selectedWard, bed.optString("id")) {
                            onAdmitSuccess()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp),
                enabled = selectedBed != null && !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("Confirm Admission")
            }
        }
    }
}
