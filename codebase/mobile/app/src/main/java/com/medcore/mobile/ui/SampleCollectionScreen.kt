package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleCollectionScreen(
    apiUrl: String,
    token: String,
    patientId: String,
    patientName: String,
    onBack: () -> Unit,
    onCollectSuccess: () -> Unit
) {
    var barcode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isFetching by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    // Test Name -> isChecked
    val pendingTests = remember { mutableStateListOf<Pair<String, Boolean>>() }

    LaunchedEffect(patientId) {
        try {
            isFetching = true
            val res = com.medcore.mobile.NetworkClient.get("$apiUrl/lab/samples/pending?patientId=$patientId", token)
            val jsonArray = org.json.JSONArray(res)
            
            pendingTests.clear()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val testName = obj.optString("testName", "Unknown Test")
                pendingTests.add(Pair(testName, true)) // default checked
            }
        } catch (e: Exception) {
            errorMsg = "Failed to load tests: ${e.message}"
            // Fallback for UI testing if backend not fully up
            pendingTests.add(Pair("CBC (Fallback)", true))
        } finally {
            isFetching = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sample Collection") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Patient: $patientName", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            
            Text("Pending Tests for Collection", fontWeight = FontWeight.SemiBold)
            
            if (isFetching) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pendingTests.forEachIndexed { index, test ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = test.second,
                                onCheckedChange = { checked ->
                                    pendingTests[index] = test.copy(second = checked)
                                }
                            )
                            Text(test.first)
                        }
                    }
                    if (pendingTests.isEmpty() && !isFetching) {
                        Text("No pending tests found.")
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Scan Vacutainer Barcode", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            OutlinedTextField(
                value = barcode, onValueChange = { barcode = it },
                label = { Text("Or Enter Barcode Manually") }, modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            try {
                                val payload = org.json.JSONObject()
                                payload.put("barcode", barcode)
                                val selectedTests = org.json.JSONArray()
                                pendingTests.filter { it.second }.forEach { selectedTests.put(it.first) }
                                payload.put("tests", selectedTests)
                                payload.put("patientId", patientId)
                                
                                com.medcore.mobile.NetworkClient.post("$apiUrl/lab/samples/collect", payload.toString(), token)
                                onCollectSuccess()
                            } catch (e: Exception) {
                                errorMsg = "Submission failed: ${e.message}"
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = barcode.isNotEmpty() && pendingTests.any { it.second }
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Collection")
                }
            }
        }
    }
}
