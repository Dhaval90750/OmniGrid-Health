package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.medcore.mobile.NetworkClient
import org.json.JSONArray

data class OperationsTask(val id: String, val title: String, val zone: String, var isComplete: Boolean, val type: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTrackerScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Housekeeping", "Biomedical", "Transport")
    
    val tasks = remember { mutableStateListOf<OperationsTask>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedTabIndex) {
        isLoading = true
        errorMsg = ""
        tasks.clear()
        try {
            // Mock network latency
            kotlinx.coroutines.delay(500)
            
            // Generate mock tasks based on category for demonstration (since operations backend is mocked in Phase 3)
            val category = tabs[selectedTabIndex]
            val mockTasks = when (category) {
                "Biomedical" -> listOf(
                    OperationsTask("BIO-1", "Repair MRI Cooling System", "Radiology", false, category),
                    OperationsTask("BIO-2", "Calibrate Ventillator #4", "ICU", true, category)
                )
                "Transport" -> listOf(
                    OperationsTask("TR-1", "Move Patient MED-1045 to OT", "Ward B -> OT 2", false, category),
                    OperationsTask("TR-2", "Transport Blood Bags", "Blood Bank -> ER", false, category)
                )
                else -> listOf(
                    OperationsTask("HK-1", "Deep Clean OT 1", "Surgical Wing", false, category),
                    OperationsTask("HK-2", "Spill Cleanup", "ER Waiting Area", true, category)
                )
            }
            tasks.addAll(mockTasks)
            
        } catch (e: Exception) {
            errorMsg = "Failed to load tasks: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Operations Hub", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FA),
                    titleContentColor = Color(0xFF263238),
                    navigationIconContentColor = Color(0xFF263238)
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFFF8F9FA),
                contentColor = Color(0xFF1565C0),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFF1565C0)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) Color(0xFF1565C0) else Color(0xFF78909C)
                            ) 
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFF1565C0), modifier = Modifier.align(Alignment.Center))
                } else if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                } else if (tasks.isEmpty()) {
                    Text("No tasks assigned to you right now.", color = Color(0xFF546E7A), modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(tasks) { task ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (task.isComplete) Color(0xFFE8F5E9) else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (task.isComplete) Color(0xFF2E7D32) else Color(0xFF263238)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Zone: ${task.zone}",
                                            fontSize = 14.sp,
                                            color = if (task.isComplete) Color(0xFF4CAF50) else Color(0xFF546E7A)
                                        )
                                    }
                                    Checkbox(
                                        checked = task.isComplete,
                                        onCheckedChange = { checked ->
                                            val index = tasks.indexOf(task)
                                            if (index != -1) {
                                                tasks[index] = tasks[index].copy(isComplete = checked)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF388E3C),
                                            checkmarkColor = Color.White,
                                            uncheckedColor = Color(0xFF90A4AE)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
