package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndentApprovalScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var indents by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<org.json.JSONArray?>(null) }
    var errorMsg by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        isLoading = true
        try {
            val res = com.medcore.mobile.NetworkClient.get("$apiUrl/inventory/indents", token)
            indents = org.json.JSONArray(res)
        } catch (e: Exception) {
            errorMsg = "Failed to load indents: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending Indent Approvals", fontWeight = FontWeight.Bold) },
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
                Text("High Priority Approvals", style = MaterialTheme.typography.titleLarge)
            }
            if (indents != null) {
                items(indents!!.length()) { index ->
                    val indent = indents!!.getJSONObject(index)
                    val indentId = indent.optString("id", "")
                    val isApproved = indent.optString("status", "") == "Approved"
                    if (!isApproved) {
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
                                Icon(Icons.Default.Warning, contentDescription = "Critical", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Indent: ${indent.optString("indentNumber", "Unknown")}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Requested By: ${indent.optString("requestedBy", "N/A")}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = {
                                    scope.kotlinx.coroutines.launch {
                                        try {
                                            com.medcore.mobile.NetworkClient.post("$apiUrl/inventory/indents/$indentId/approve?level=L1", "", token)
                                            // Refresh logic would ideally go here
                                        } catch (e: Exception) {
                                            // Handle error
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Approve", tint = Color(0xFF10B981))
                                }
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
