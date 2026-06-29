package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var invoices by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<org.json.JSONArray?>(null) }
    var errorMsg by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        isLoading = true
        try {
            val res = com.medcore.mobile.NetworkClient.get("$apiUrl/billing/invoices/pending", token)
            invoices = org.json.JSONArray(res)
        } catch (e: Exception) {
            errorMsg = "Failed to load invoices: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Billing", fontWeight = FontWeight.Bold) },
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
            Text("Pending IPD Bills", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
            Spacer(modifier = Modifier.height(16.dp))
            
            if (invoices != null) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(invoices!!.length()) { index ->
                        val invoice = invoices!!.getJSONObject(index)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Bill", tint = Color(0xFF607D8B))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(invoice.optString("invoiceNumber", "Unknown"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Patient: ${invoice.optJSONObject("patient")?.optString("fullName") ?: "Unknown"}", color = Color.Gray)
                                Text("Total Due: ₹${invoice.optString("totalAmount", "0.0")}", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))) {
                                    Text("Send Payment Link via SMS")
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
