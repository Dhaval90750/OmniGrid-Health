package com.medcore.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MedCoreDashboard()
                }
            }
        }
    }
}

@Composable
fun MedCoreDashboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MedCore HIS",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB) // Primary blue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Mobile Physician Portal",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { /* TODO: Auth Flow */ },
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
        ) {
            Text("Login via Biometrics / PIN", fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { /* TODO: QR Scanner */ },
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
        ) {
            Text("Scan Patient Barcode (CameraX)", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { /* TODO: AI Scribe */ },
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
        ) {
            Text("Launch AI Voice Scribe", fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "(Phase 4 Android MVP Scaffold)",
            fontSize = 12.sp,
            color = Color.LightGray
        )
    }
}
