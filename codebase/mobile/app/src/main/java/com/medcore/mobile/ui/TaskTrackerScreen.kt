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

data class OperationsTask(val id: String, val title: String, val zone: String, var isComplete: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTrackerScreen(
    onBack: () -> Unit
) {
    val tasks = remember {
        mutableStateListOf(
            OperationsTask("T1", "Bed Turnaround - ICU Bed 4", "ICU", false),
            OperationsTask("T2", "Porter Request - X-Ray transfer for UHID-991", "Ward B", false),
            OperationsTask("T3", "Biohazard Cleanup", "ER Trauma Bay", false)
        )
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
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
