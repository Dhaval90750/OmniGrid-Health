package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medcore.mobile.NetworkClient
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabResultsScreen(
    apiUrl: String,
    token: String,
    patientId: String,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<JSONArray?>(null) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            // Mocking the call since there isn't a direct endpoint for patient specific lab results in LabController yet,
            // or if there is, we simulate a successful parsed array of results with flags.
            val mockJson = """
            [
                {
                    "testName": "Complete Blood Count (CBC)",
                    "value": "11.2",
                    "unit": "g/dL",
                    "refRange": "12.0 - 15.5",
                    "isAbnormal": true,
                    "isCritical": false,
                    "flag": "LOW"
                },
                {
                    "testName": "Potassium (Serum)",
                    "value": "6.1",
                    "unit": "mmol/L",
                    "refRange": "3.5 - 5.0",
                    "isAbnormal": true,
                    "isCritical": true,
                    "flag": "HIGH - CRITICAL"
                },
                {
                    "testName": "Fasting Blood Sugar",
                    "value": "95",
                    "unit": "mg/dL",
                    "refRange": "70 - 100",
                    "isAbnormal": false,
                    "isCritical": false,
                    "flag": "NORMAL"
                }
            ]
            """.trimIndent()
            results = JSONArray(mockJson)
        } catch (e: Exception) {
            errorMsg = "Error loading labs: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laboratory Results", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
            } else if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else if (results != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(results!!.length()) { index ->
                        val result = results!!.getJSONObject(index)
                        val isAbnormal = result.optBoolean("isAbnormal", false)
                        val isCritical = result.optBoolean("isCritical", false)
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCritical) MaterialTheme.colorScheme.errorContainer 
                                                 else if (isAbnormal) MaterialTheme.colorScheme.errorContainer.copy(alpha=0.5f)
                                                 else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(result.getString("testName"), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                    if (isCritical) {
                                        Icon(Icons.Default.Warning, contentDescription = "Critical", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Result: ${result.getString("value")} ${result.getString("unit")}", 
                                         fontWeight = FontWeight.Bold, 
                                         fontSize = 20.sp,
                                         color = if (isCritical || isAbnormal) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                    Text("Ref: ${result.getString("refRange")}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (isAbnormal) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Flag: ${result.getString("flag")}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
