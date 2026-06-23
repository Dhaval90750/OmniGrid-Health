package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.medcore.mobile.NetworkClient
import org.json.JSONArray

data class OperationsTask(val id: String, val title: String, val zone: String, var isComplete: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTrackerScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    val tasks = remember { mutableStateListOf<OperationsTask>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val res = NetworkClient.get("$apiUrl/operations/housekeeping", token)
            val jsonArray = JSONArray(res)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                tasks.add(
                    OperationsTask(
                        id = obj.optString("id", ""),
                        title = obj.optString("description", "Unknown Task"),
                        zone = obj.optString("zone", "Unknown Zone"),
                        isComplete = obj.optString("status", "Pending") == "Completed"
                    )
                )
            }
        } catch (e: Exception) {
            errorMsg = "Failed to load tasks: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Operations Tasks", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A73E8)),
                navigationIcon = {
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                        Text("Back", color = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else if (tasks.isEmpty()) {
                Text("No tasks assigned to you right now.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks) { task ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (task.isComplete) Color(0xFFE8F5E9) else Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(task.title, style = MaterialTheme.typography.titleMedium)
                                    Text("Zone: ${task.zone}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                }
                                Checkbox(
                                    checked = task.isComplete,
                                    onCheckedChange = { checked ->
                                        // In a real app we'd PATCH the backend here
                                        val index = tasks.indexOf(task)
                                        if (index != -1) {
                                            tasks[index] = tasks[index].copy(isComplete = checked)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
