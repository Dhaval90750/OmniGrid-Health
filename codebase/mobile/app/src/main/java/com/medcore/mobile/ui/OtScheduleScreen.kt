package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
fun OtScheduleScreen(
    onBack: () -> Unit,
    onCaseClick: (String) -> Unit
) {
    val cases = listOf(
        OtCase("09:00 AM", "OT 1", "Laparoscopic Cholecystectomy", "Dr. Sameer", "Rahul S."),
        OtCase("10:30 AM", "OT 2", "Total Knee Replacement", "Dr. Anjali", "Suman L."),
        OtCase("01:00 PM", "OT 1", "Appendectomy", "Dr. Sameer", "Amit K.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's OT Schedule") },
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
            items(cases) { otCase ->
                OtCaseCard(otCase) { onCaseClick(otCase.procedure) }
            }
        }
    }
}

data class OtCase(val time: String, val room: String, val procedure: String, val surgeon: String, val patient: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtCaseCard(otCase: OtCase, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(otCase.time, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(otCase.room, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(otCase.procedure, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Surgeon: ${otCase.surgeon}", fontSize = 14.sp)
                Text("Patient: ${otCase.patient}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
