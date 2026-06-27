package com.medcore.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.json.JSONObject

import dagger.hilt.android.AndroidEntryPoint
import com.medcore.mobile.ui.VitalsEntryScreen
import com.medcore.mobile.ui.NursingAssessmentScreen
import com.medcore.mobile.ui.IncidentReportScreen
import com.medcore.mobile.ui.TaskTrackerScreen
import com.medcore.mobile.ui.MarScannerScreen
import com.medcore.mobile.ui.QrScannerScreen
import com.medcore.mobile.ui.PatientSummaryScreen
import com.medcore.mobile.ui.AiScribeScreen
import com.medcore.mobile.ui.AnalyticsScreen
import com.medcore.mobile.ui.TelemedicineScreen
import com.medcore.mobile.ui.InventoryScreen
import com.medcore.mobile.ui.BillingScreen

import com.medcore.mobile.ui.theme.MedCoreTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedCoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MedCoreAppContent()
                }
            }
        }
    }
}

enum class Screen {
    LOGIN, DASHBOARD, QR_SCANNER, PATIENT_SUMMARY, AI_SCRIBE,
    VITALS_ENTRY, NURSING_ASSESSMENT, INCIDENT_REPORT, TASK_TRACKER, MAR_SCANNER,
    ANALYTICS, TELEMEDICINE, INVENTORY, BILLING, ORDER_ENTRY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedCoreAppContent() {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var apiUrl by remember { mutableStateOf("https://medcore-his-backend-production.up.railway.app/api/v1") }
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var token by remember { mutableStateOf("") }
    var loggedInUser by remember { mutableStateOf("") }
    var permissions by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Active Patient Context
    var activePatient by remember { mutableStateOf<JSONObject?>(null) }
    var pendingScreen by remember { mutableStateOf<Screen?>(null) }

    // Helper to enforce QR Scan
    fun requirePatient(destination: Screen) {
        if (activePatient == null) {
            pendingScreen = destination
            currentScreen = Screen.QR_SCANNER
        } else {
            currentScreen = destination
        }
    }

    when (currentScreen) {
        Screen.LOGIN -> LoginScreen(
            apiUrl = apiUrl,
            username = username,
            password = password,
            errorMsg = errorMsg,
            isLoading = isLoading,
            onApiUrlChange = { apiUrl = it },
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onLogin = {
                isLoading = true
                errorMsg = ""
                scope.launch {
                    try {
                        val body = JSONObject().apply {
                            put("username", username)
                            put("password", password)
                        }.toString()
                        val res = NetworkClient.post("$apiUrl/auth/login", body, null)
                        val json = JSONObject(res)
                        token = json.getString("token")
                        loggedInUser = json.optString("username", "User")
                        
                        // Parse permissions
                        val permsObj = json.optJSONObject("permissions")
                        val newPerms = mutableMapOf<String, String>()
                        if (permsObj != null) {
                            val keys = permsObj.keys()
                            while (keys.hasNext()) {
                                val k = keys.next()
                                newPerms[k] = permsObj.getString(k)
                            }
                        }
                        permissions = newPerms
                        
                        currentScreen = Screen.DASHBOARD
                    } catch (e: Exception) {
                        errorMsg = "Login Failed: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            onSimulateBiometric = {
                // Secure Bypass in Developer Mode
                token = "simulated_token"
                loggedInUser = "Dr. Anjali Desai"
                permissions = mapOf(
                    "Patient Registration" to "FULL_ACCESS",
                    "Clinical Notes" to "FULL_ACCESS",
                    "Operations" to "FULL_ACCESS",
                    "Pharmacy" to "FULL_ACCESS",
                    "Dashboards" to "FULL_ACCESS",
                    "Inventory" to "FULL_ACCESS",
                    "Billing" to "FULL_ACCESS"
                )
                currentScreen = Screen.DASHBOARD
            }
        )
        
        Screen.DASHBOARD -> DashboardScreen(
            user = loggedInUser,
            permissions = permissions,
            onScanClick = { requirePatient(Screen.PATIENT_SUMMARY) },
            onScribeClick = { requirePatient(Screen.AI_SCRIBE) },
            onVitalsClick = { requirePatient(Screen.VITALS_ENTRY) },
            onAssessClick = { requirePatient(Screen.NURSING_ASSESSMENT) },
            onOrderClick = { requirePatient(Screen.ORDER_ENTRY) },
            onIncidentClick = { currentScreen = Screen.INCIDENT_REPORT },
            onTasksClick = { currentScreen = Screen.TASK_TRACKER },
            onMarClick = { requirePatient(Screen.MAR_SCANNER) },
            onAnalyticsClick = { currentScreen = Screen.ANALYTICS },
            onTelemedicineClick = { currentScreen = Screen.TELEMEDICINE },
            onInventoryClick = { currentScreen = Screen.INVENTORY },
            onBillingClick = { currentScreen = Screen.BILLING },
            onLogout = {
                token = ""
                permissions = emptyMap()
                activePatient = null
                pendingScreen = null
                currentScreen = Screen.LOGIN
            }
        )
        
        Screen.QR_SCANNER -> QrScannerScreen(
            apiUrl = apiUrl,
            token = token,
            onBack = { 
                pendingScreen = null
                currentScreen = Screen.DASHBOARD 
            },
            onPatientDetected = { patientJson ->
                activePatient = patientJson
                if (pendingScreen != null) {
                    currentScreen = pendingScreen!!
                    pendingScreen = null
                } else {
                    currentScreen = Screen.PATIENT_SUMMARY
                }
            }
        )
        
        Screen.PATIENT_SUMMARY -> PatientSummaryScreen(
            patient = activePatient!!,
            onBack = { currentScreen = Screen.DASHBOARD },
            onClearPatient = {
                activePatient = null
                currentScreen = Screen.DASHBOARD
            }
        )
        
        Screen.AI_SCRIBE -> AiScribeScreen(
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.VITALS_ENTRY -> VitalsEntryScreen(
            patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN",
            patientId = activePatient?.optString("id") ?: "",
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD },
            onSubmit = { _, _, _, _, _ -> currentScreen = Screen.DASHBOARD }
        )
        
        Screen.ORDER_ENTRY -> OrderEntryScreen(
            patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN",
            patientId = activePatient?.optString("id") ?: "",
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.NURSING_ASSESSMENT -> NursingAssessmentScreen(
            patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN",
            patientId = activePatient?.optString("id") ?: "",
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD },
            onSubmitSuccess = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.INCIDENT_REPORT -> IncidentReportScreen(
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD },
            onSubmitSuccess = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.TASK_TRACKER -> TaskTrackerScreen(
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.MAR_SCANNER -> MarScannerScreen(
            apiUrl = apiUrl,
            token = token,
            patientId = activePatient?.optString("id") ?: "",
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.ANALYTICS -> AnalyticsScreen(
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.TELEMEDICINE -> TelemedicineScreen(
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.INVENTORY -> InventoryScreen(
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.BILLING -> BillingScreen(
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    apiUrl: String,
    username: String,
    password: String,
    errorMsg: String,
    isLoading: Boolean,
    onApiUrlChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onSimulateBiometric: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color.White)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo Icon
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Color.White, RoundedCornerShape(24.dp))
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "MedCore Mobile",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1565C0)
        )
        Text(
            text = "Unified Hospital System",
            fontSize = 16.sp,
            color = Color(0xFF546E7A),
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = Color(0xFF1565C0),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = Color(0xFF1565C0),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )
        
        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMsg,
                    color = Color(0xFFD32F2F),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF1565C0))
        } else {
            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                Text("Secure Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onSimulateBiometric,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1565C0)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1565C0))
            ) {
                Text("Use Biometric Unlock", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun DashboardActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = iconColor.copy(alpha = 0.12f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, fontSize = 14.sp, color = Color(0xFF78909C))
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", tint = Color(0xFFB0BEC5))
        }
    }
}

@Composable
fun DashboardScreen(
    user: String,
    permissions: Map<String, String>,
    onScanClick: () -> Unit,
    onScribeClick: () -> Unit,
    onVitalsClick: () -> Unit,
    onAssessClick: () -> Unit,
    onOrderClick: () -> Unit,
    onIncidentClick: () -> Unit,
    onTasksClick: () -> Unit,
    onMarClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onTelemedicineClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onBillingClick: () -> Unit,
    onLogout: () -> Unit
) {
    // Helper function to check access
    fun hasAccess(module: String): Boolean {
        return permissions[module] != null && permissions[module] != "NO_ACCESS"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Welcome Back,", fontSize = 16.sp, color = Color(0xFF78909C))
                Text(user, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF263238))
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFEBEE),
                modifier = Modifier.clickable { onLogout() }
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Dynamic Hero Card based on Role
        if (hasAccess("Dashboards")) {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth().clickable { onAnalyticsClick() }
            ) {
                Box(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(listOf(Color(0xFF3F51B5), Color(0xFF283593))))
                        .padding(24.dp)
                ) {
                    Column {
                        Text("Executive Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Tap to view live revenue & KPIs", fontSize = 15.sp, color = Color.White.copy(alpha=0.9f))
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1E88E5), Color(0xFF1565C0))))
                        .padding(24.dp)
                ) {
                    Column {
                        Text("Active Shift Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("📍 Location: Main Hospital", fontSize = 15.sp, color = Color.White.copy(alpha=0.9f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("⏰ Time: Logged in securely", fontSize = 15.sp, color = Color.White.copy(alpha=0.9f))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(36.dp))
        
        Text("Quick Actions", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
        Spacer(modifier = Modifier.height(16.dp))
        
        // ABAC Logic from Backend Mapping
        if (hasAccess("Patient Registration") || hasAccess("Clinical Notes") || hasAccess("Operations")) {
            DashboardActionCard("Scan Patient QR", "Access 360° Profile dynamically", Icons.Default.Menu, Color(0xFF2196F3), onScanClick)
        }
        
        if (hasAccess("Clinical Notes")) {
            DashboardActionCard("Telemedicine Room", "Join live video consultations", Icons.Default.Call, Color(0xFFE91E63), onTelemedicineClick)
            DashboardActionCard("Voice AI Scribe", "Dictate SOAP notes on the go", Icons.Default.PlayArrow, Color(0xFF4CAF50), onScribeClick)
            DashboardActionCard("Bedside Vitals", "Enter offline vitals (Room DB)", Icons.Default.AddCircle, Color(0xFFFF9800), onVitalsClick)
            DashboardActionCard("Nursing Assessments", "Morse & Braden scales", Icons.Default.List, Color(0xFF9C27B0), onAssessClick)
        }

        if (hasAccess("Lab Orders/Results") || hasAccess("Radiology") || hasAccess("Clinical Notes")) {
            DashboardActionCard("Order Entry (CPOE)", "Order Labs & Radiology bedside", Icons.Default.AddCircle, Color(0xFF673AB7), onOrderClick)
        }
        
        if (hasAccess("Pharmacy") || hasAccess("Clinical Notes")) {
            DashboardActionCard("MAR Scanner", "5-Rights Medication verification", Icons.Default.Search, Color(0xFF3F51B5), onMarClick)
        }

        if (hasAccess("Operations")) {
            DashboardActionCard("Task Tracker", "Porter & Maintenance jobs", Icons.Default.CheckCircle, Color(0xFF009688), onTasksClick)
        }
        
        if (hasAccess("Inventory")) {
            DashboardActionCard("Inventory Management", "Approve POs & Check Stock", Icons.Default.ShoppingCart, Color(0xFF795548), onInventoryClick)
        }
        
        if (hasAccess("Billing")) {
            DashboardActionCard("Quick Billing", "View interim IPD & OPD bills", Icons.Default.DateRange, Color(0xFF607D8B), onBillingClick)
        }

        // Available to mostly everyone (with a valid session) for Safety
        DashboardActionCard("Report Incident", "Log adverse events securely", Icons.Default.Warning, Color(0xFFF44336), onIncidentClick)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
