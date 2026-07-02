package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medcore.mobile.data.SessionManager

@Composable
fun OpdConsultationScreen(
    sessionManager: SessionManager,
    onNavigateToAiScribe: () -> Unit,
    onNavigateToSoapEditor: () -> Unit,
    onNavigateToPrescription: () -> Unit,
    onBack: () -> Unit
) {
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
            Text("OPD Consultation", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Patient: Jane Doe (F/32)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("UHID: HOS-2026-000412", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Chief Complaint: Severe headache, nausea x 2 days", fontSize = 14.sp)
            }
        }
        
        Text("Consultation Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
        
        ConsultationActionCard(
            title = "✨ AI Voice Scribe",
            description = "Dictate notes and let AI structure them into SOAP format",
            onClick = onNavigateToAiScribe,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
        
        ConsultationActionCard(
            title = "📝 Manual SOAP Note",
            description = "Type clinical notes manually in standard format",
            onClick = onNavigateToSoapEditor
        )
        
        ConsultationActionCard(
            title = "💊 Prescription Writer",
            description = "Order medications with CDS safety checks",
            onClick = onNavigateToPrescription
        )
    }
}

@Composable
fun ConsultationActionCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = contentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 14.sp, color = contentColor.copy(alpha = 0.8f))
        }
    }
}
