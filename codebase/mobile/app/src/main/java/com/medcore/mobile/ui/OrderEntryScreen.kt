package com.medcore.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
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
fun OrderEntryScreen(
    patientUhid: String,
    patientId: String,
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var orderType by remember { mutableStateOf("LAB") } // LAB or RADIOLOGY
    var orderDetails by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clinical Order Entry", fontWeight = FontWeight.Bold) },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Patient UHID:", fontSize = 14.sp, color = Color(0xFF546E7A))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(patientUhid, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                }
            }

            Text("Order Type", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = orderType == "LAB",
                    onClick = { orderType = "LAB" },
                    label = { Text("Laboratory") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1565C0),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = orderType == "RADIOLOGY",
                    onClick = { orderType = "RADIOLOGY" },
                    label = { Text("Radiology") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1565C0),
                        selectedLabelColor = Color.White
                    )
                )
            }

            OutlinedTextField(
                value = orderDetails,
                onValueChange = { orderDetails = it },
                label = { Text("Order Details (e.g. CBC, MRI Brain)") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White),
                maxLines = 5
            )

            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = Color.Red, fontSize = 14.sp)
            }
            if (successMsg.isNotEmpty()) {
                Text(successMsg, color = Color(0xFF388E3C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            } else {
                Button(
                    onClick = {
                        if (orderDetails.isBlank()) {
                            errorMsg = "Please enter order details."
                            return@Button
                        }
                        isLoading = true
                        errorMsg = ""
                        successMsg = ""
                        scope.launch {
                            try {
                                val body = JSONObject().apply {
                                    put("patientId", patientId)
                                    put("orderType", orderType)
                                    put("details", orderDetails)
                                    put("priority", "ROUTINE")
                                }.toString()
                                
                                // Mock endpoint hit, in reality this would hit the LIS/RIS order endpoint
                                // NetworkClient.post("$apiUrl/clinical/orders", body, token)
                                kotlinx.coroutines.delay(1000) // Simulate network call
                                
                                successMsg = "$orderType order placed successfully!"
                                orderDetails = ""
                            } catch (e: Exception) {
                                errorMsg = "Error: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Place Order", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
