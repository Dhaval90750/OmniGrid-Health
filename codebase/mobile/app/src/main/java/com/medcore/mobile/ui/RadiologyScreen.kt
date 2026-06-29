package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
fun RadiologyScreen(
    apiUrl: String,
    token: String,
    patientId: String,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var reports by remember { mutableStateOf<JSONArray?>(null) }
    var errorMsg by remember { mutableStateOf("") }
    var selectedReport by remember { mutableStateOf<JSONObject?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val res = NetworkClient.get("$apiUrl/radiology/patients/$patientId/reports", token)
            val jsonArray = JSONArray(res)
            
            val mappedReports = JSONArray()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val orderObj = obj.optJSONObject("order")
                val templateObj = obj.optJSONObject("template")
                
                val modality = templateObj?.optString("modality", "Unknown") ?: "Unknown"
                val region = templateObj?.optString("bodyRegion", "Unknown") ?: "Unknown"
                val status = obj.optString("status", "Final")
                val isCritical = obj.optBoolean("isCritical", false)
                val date = obj.optString("createdAt", "").take(10)
                
                val reportText = "FINDINGS:\n${obj.optString("findings", "N/A")}\n\nIMPRESSION:\n${obj.optString("impression", "N/A")}"
                
                val mappedObj = JSONObject().apply {
                    put("modality", modality)
                    put("region", region)
                    put("status", status)
                    put("isCritical", isCritical)
                    put("date", date)
                    put("reportText", reportText)
                }
                mappedReports.put(mappedObj)
            }
            reports = mappedReports
        } catch (e: Exception) {
            errorMsg = "Error loading radiology: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    if (selectedReport != null) {
        // Detailed Report View (Mocking DICOM / Text Viewer)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedReport!!.getString("modality") + " Report", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { selectedReport = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                // Mock DICOM Viewer Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.DarkGray, RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = "DICOM", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("[ DICOM Viewer Placeholder ]", color = Color.LightGray)
                        Text(selectedReport!!.getString("region"), color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Radiologist Report", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    LazyColumn(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        item {
                            Text(selectedReport!!.getString("reportText"), fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    } else {
        // List View
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Radiology (RIS)", fontWeight = FontWeight.Bold) },
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
                } else if (reports != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reports!!.length()) { index ->
                            val report = reports!!.getJSONObject(index)
                            val isCritical = report.optBoolean("isCritical", false)
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { selectedReport = report },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCritical) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${report.getString("modality")} - ${report.getString("region")}", 
                                             fontWeight = FontWeight.Bold, 
                                             fontSize = 16.sp,
                                             color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(report.getString("date"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Status: ${report.getString("status")}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                        if (isCritical) {
                                            Text("CRITICAL FINDING", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
