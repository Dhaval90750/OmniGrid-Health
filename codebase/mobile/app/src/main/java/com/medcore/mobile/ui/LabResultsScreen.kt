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
            val res = NetworkClient.get("$apiUrl/lab/patients/$patientId/results", token)
            val jsonArray = JSONArray(res)
            
            // Map backend LabResult structure to the UI structure expected below
            val mappedResults = JSONArray()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val sampleObj = obj.optJSONObject("sample")
                val testObj = sampleObj?.optJSONObject("test")
                
                val testName = testObj?.optString("testName", "Unknown Test") ?: "Unknown Test"
                val value = obj.optString("resultValue", "N/A")
                val unit = testObj?.optString("unit", "") ?: ""
                val refLow = testObj?.optString("referenceRangeLow", "") ?: ""
                val refHigh = testObj?.optString("referenceRangeHigh", "") ?: ""
                
                val isAbnormal = obj.optBoolean("isAbnormal", false)
                val isCritical = obj.optBoolean("isCritical", false)
                val flag = if (isCritical) "CRITICAL" else if (isAbnormal) "ABNORMAL" else "NORMAL"
                
                val mappedObj = JSONObject().apply {
                    put("testName", testName)
                    put("value", value)
                    put("unit", unit)
                    put("refRange", "$refLow - $refHigh")
                    put("isAbnormal", isAbnormal)
                    put("isCritical", isCritical)
                    put("flag", flag)
                }
                mappedResults.put(mappedObj)
            }
            results = mappedResults
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
