package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var pos by remember { mutableStateOf<org.json.JSONArray?>(null) }
    var errorMsg by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val res = com.medcore.mobile.NetworkClient.get("$apiUrl/inventory/purchase-orders", token)
            pos = org.json.JSONArray(res)
        } catch (e: Exception) {
            errorMsg = "Failed to load POs: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory Approvals", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp)
        ) {
            Text("Pending Purchase Orders", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
            Spacer(modifier = Modifier.height(16.dp))
            
            if (pos != null) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(pos!!.length()) { index ->
                        val po = pos!!.getJSONObject(index)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "PO", tint = Color(0xFF795548))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = po.optString("poNumber", "Unknown"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Vendor: ${po.optJSONObject("vendor")?.optString("name") ?: "Unknown"}", color = Color.Gray)
                                Text("Amount: ₹${po.optString("totalAmount", "0.0")}", fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(onClick = { }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))) {
                                        Text("Approve")
                                    }
                                    Button(onClick = { }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                                        Text("Reject")
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF1565C0))
            } else {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
