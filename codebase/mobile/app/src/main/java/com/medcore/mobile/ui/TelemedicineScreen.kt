package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelemedicineScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var inCall by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (!inCall) {
                TopAppBar(
                    title = { Text("Telemedicine", fontWeight = FontWeight.Bold) },
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
            }
        },
        containerColor = if (inCall) Color.Black else Color(0xFFF8F9FA)
    ) { padding ->
        if (!inCall) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFFE91E63), modifier = Modifier.size(80.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text("Upcoming Consultation", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Patient: David Chen (UHID: 8842)", fontSize = 16.sp, color = Color(0xFF78909C))
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = { inCall = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("Join Video Call", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Main Video (Patient)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Receiving Patient Video Stream...", color = Color.Gray)
                }
                
                // PiP Video (Doctor)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 120.dp, end = 24.dp)
                        .size(width = 120.dp, height = 160.dp)
                        .background(Color.DarkGray, RoundedCornerShape(12.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your Camera", color = Color.LightGray, fontSize = 12.sp)
                }

                // Call Controls
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { inCall = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Text("END")
                    }
                }
            }
        }
    }
}
