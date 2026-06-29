package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffRosterScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var rosters by remember { mutableStateOf<org.json.JSONArray?>(null) }
    var errorMsg by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val res = com.medcore.mobile.NetworkClient.get("$apiUrl/staff/rosters", token)
            rosters = org.json.JSONArray(res)
        } catch (e: Exception) {
            errorMsg = "Failed to load rosters: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duty Roster & Swaps", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Upcoming Shifts", style = MaterialTheme.typography.titleLarge)
            }
            if (rosters != null) {
                items(rosters!!.length()) { index ->
                    val roster = rosters!!.getJSONObject(index)
                    val title = roster.optString("shiftType", "Standard Shift")
                    val time = roster.optString("startTime", "08:00 AM") + " - " + roster.optString("endTime", "05:00 PM")
                    val department = roster.optString("department", "General")
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = "Shift", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "$title - $department", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = time, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { /* Request Swap */ }) {
                                Icon(Icons.Default.Send, contentDescription = "Swap", tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            } else if (isLoading) {
                item { CircularProgressIndicator() }
            } else {
                item { Text(errorMsg, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
