package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun MyIpdPatientsScreen(
    sessionManager: SessionManager,
    onBack: () -> Unit,
    onNavigateToPatient: (String) -> Unit
) {
    val patients = listOf(
        mapOf("id" to "101", "name" to "Robert Baratheon", "uhid" to "HOS-2026-9081", "bed" to "Ward A - Bed 04", "status" to "Stable"),
        mapOf("id" to "102", "name" to "Ned Stark", "uhid" to "HOS-2026-7762", "bed" to "ICU - Bed 01", "status" to "Critical")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("My IPD Patients", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(patients.size) { index ->
                val p = patients[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(p["name"] ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(p["uhid"] ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Location: ${p["bed"]}", fontSize = 14.sp)
                            Text(
                                text = p["status"] ?: "",
                                color = if (p["status"] == "Critical") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = { onNavigateToPatient(p["id"] ?: "") },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("View Chart")
                            }
                        }
                    }
                }
            }
        }
    }
}
