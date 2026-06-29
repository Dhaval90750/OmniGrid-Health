package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun BloodAvailabilityScreen(
    onBack: () -> Unit
) {
    val inventory = listOf(
        BloodUnit("A+", "12 Units", Color(0xFF10B981)),
        BloodUnit("A-", "2 Units", Color(0xFFEF4444)),
        BloodUnit("B+", "8 Units", Color(0xFF10B981)),
        BloodUnit("B-", "4 Units", Color(0xFFF59E0B)),
        BloodUnit("O+", "15 Units", Color(0xFF10B981)),
        BloodUnit("O-", "1 Unit", Color(0xFFEF4444)),
        BloodUnit("AB+", "5 Units", Color(0xFF10B981)),
        BloodUnit("AB-", "0 Units", Color(0xFFEF4444))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blood Bank Inventory") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(inventory) { unit ->
                BloodUnitCard(unit)
            }
        }
    }
}

data class BloodUnit(val group: String, val quantity: String, val statusColor: Color)

@Composable
fun BloodUnitCard(unit: BloodUnit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(28.dp),
                color = unit.statusColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(unit.group, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = unit.statusColor)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text("Stock Level", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(unit.quantity, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Button(onClick = { /* Request Cross-match */ }) {
                Text("Request")
            }
        }
    }
}
