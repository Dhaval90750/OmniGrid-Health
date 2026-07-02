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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medcore.mobile.data.SessionManager

@Composable
fun ErDashboardScreen(
    sessionManager: SessionManager,
    onBack: () -> Unit
) {
    val erPatients = listOf(
        mapOf("name" to "John Doe", "triage" to "RED", "complaint" to "Chest Pain, diaphoresis", "time" to "10 min ago"),
        mapOf("name" to "Unknown Male", "triage" to "YELLOW", "complaint" to "Laceration on arm", "time" to "30 min ago"),
        mapOf("name" to "Sarah Connor", "triage" to "GREEN", "complaint" to "Mild fever", "time" to "1 hr ago")
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
            Text("ER Triage Dashboard", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(erPatients.size) { index ->
                val p = erPatients[index]
                val cardColor = when (p["triage"]) {
                    "RED" -> Color(0xFFFFEBEE) // Light Red
                    "YELLOW" -> Color(0xFFFFF8E1) // Light Yellow
                    "GREEN" -> Color(0xFFE8F5E9) // Light Green
                    else -> MaterialTheme.colorScheme.surface
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(p["name"] ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(p["time"] ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Triage Level: ${p["triage"]}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Complaint: ${p["complaint"]}", fontSize = 14.sp)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(onClick = { /* Handle Actions */ }, shape = RoundedCornerShape(8.dp)) {
                                Text("Assess Patient")
                            }
                        }
                    }
                }
            }
        }
    }
}
