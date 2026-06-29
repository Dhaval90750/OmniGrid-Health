package com.medcore.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medcore.mobile.viewmodels.AuthViewModel

@Composable
fun PinLoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var pin by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = "Pin", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Quick Access PIN", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Enter your 6-digit secure PIN", color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Simple PIN entry mock
        Text(pin.replace(".".toRegex(), "*"), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 8.sp)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        GridLayout(3, 4) { index ->
            val num = when {
                index < 9 -> (index + 1).toString()
                index == 9 -> "Clear"
                index == 10 -> "0"
                else -> "Go"
            }
            Button(
                onClick = {
                    when (num) {
                        "Clear" -> pin = ""
                        "Go" -> { if (pin.length >= 4) viewModel.pinLogin(pin) }
                        else -> if (pin.length < 6) pin += num
                    }
                },
                modifier = Modifier.padding(8.dp).size(80.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(num, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }
        
        if (errorMsg != null) {
            Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun GridLayout(columns: Int, items: Int, content: @Composable (Int) -> Unit) {
    Column {
        for (i in 0 until (items + columns - 1) / columns) {
            Row {
                for (j in 0 until columns) {
                    val index = i * columns + j
                    if (index < items) {
                        content(index)
                    } else {
                        Spacer(modifier = Modifier.size(80.dp).padding(8.dp))
                    }
                }
            }
        }
    }
}
