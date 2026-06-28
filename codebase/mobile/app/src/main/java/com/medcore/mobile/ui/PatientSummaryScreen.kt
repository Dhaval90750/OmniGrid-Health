package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientSummaryScreen(
    patient: JSONObject,
    onBack: () -> Unit,
    onClearPatient: () -> Unit
) {
    val firstName = patient.optString("firstName", "Unknown")
    val lastName = patient.optString("lastName", "")
    val name = "$firstName $lastName"
    val uhid = patient.optString("uhid", "UNKNOWN-UHID")
    val dob = patient.optString("dateOfBirth", "Unknown")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient 360° Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FA),
                    titleContentColor = Color(0xFF263238),
                    navigationIconContentColor = Color(0xFF263238)
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            
            // Premium Patient Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Placeholder
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(36.dp))
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${firstName.firstOrNull() ?: 'U'}${lastName.firstOrNull() ?: ""}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Column {
                        Text(name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF263238))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("UHID: $uhid", fontSize = 15.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("DOB: $dob", fontSize = 14.sp, color = Color(0xFF78909C))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Clinical Metrics Summary", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF263238))
            Spacer(modifier = Modifier.height(16.dp))
            
            // Vitals Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricRow("Blood Pressure", "120/80 mmHg", Color(0xFF263238))
                    Divider(color = Color(0xFFECEFF1))
                    MetricRow("SpO2 (Oxygen)", "99%", Color(0xFF4CAF50))
                    Divider(color = Color(0xFFECEFF1))
                    MetricRow("Heart Rate", "72 bpm", Color(0xFF263238))
                    Divider(color = Color(0xFFECEFF1))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = "Alert", tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Allergies", color = Color(0xFF78909C), fontSize = 15.sp)
                        }
                        Text("Penicillin (High)", fontWeight = FontWeight.Bold, color = Color(0xFFF44336), fontSize = 15.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Active Issues Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Active Issues", tint = Color(0xFFFF8F00), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Active Diagnosis", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Type 2 Diabetes Mellitus, Essential Hypertension", fontSize = 14.sp, color = Color(0xFFEF6C00))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238))
            ) {
                Text("Return to Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onClearPatient,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
            ) {
                Text("Clear Active Patient", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MetricRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFF78909C), fontSize = 15.sp)
        Text(value, fontWeight = FontWeight.Bold, color = valueColor, fontSize = 16.sp)
    }
}
