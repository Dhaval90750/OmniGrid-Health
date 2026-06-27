package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.medcore.mobile.NetworkClient
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var revenue by remember { mutableStateOf("0") }
    var occupancy by remember { mutableStateOf("0") }
    var alos by remember { mutableStateOf("0") }
    
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val res = NetworkClient.get("$apiUrl/analytics/dashboard", token)
                val json = JSONObject(res)
                val exec = json.optJSONObject("executive_overview")
                if (exec != null) {
                    revenue = exec.optString("total_revenue_today", "0")
                    occupancy = exec.optString("bed_occupancy_percent", "0")
                }
                val quality = json.optJSONObject("quality_kpis")
                if (quality != null) {
                    alos = quality.optString("average_length_of_stay", "0")
                }
            } catch (e: Exception) {
                errorMsg = "Failed to load dashboard: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Executive Analytics", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            } else if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = Color.Red, modifier = Modifier.padding(16.dp))
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Total Revenue Today", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹$revenue", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Occupancy", color = Color(0xFF78909C), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("$occupancy%", color = Color(0xFFE65100), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("ALOS", color = Color(0xFF78909C), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("$alos Days", color = Color(0xFF2E7D32), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
