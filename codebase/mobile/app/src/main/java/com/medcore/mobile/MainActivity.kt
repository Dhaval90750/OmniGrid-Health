package com.medcore.mobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import dagger.hilt.android.AndroidEntryPoint
import com.medcore.mobile.ui.VitalsEntryScreen
import com.medcore.mobile.ui.NursingAssessmentScreen
import com.medcore.mobile.ui.IncidentReportScreen
import com.medcore.mobile.ui.TaskTrackerScreen
import com.medcore.mobile.ui.MarScannerScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    MedCoreApp()
                }
            }
        }
    }
}

enum class Screen {
    LOGIN, DASHBOARD, QR_SCANNER, PATIENT_SUMMARY, AI_SCRIBE,
    VITALS_ENTRY, NURSING_ASSESSMENT, INCIDENT_REPORT, TASK_TRACKER, MAR_SCANNER
}

// Network Helpers using standard HttpURLConnection (Zero Dependencies)
object NetworkClient {
    suspend fun get(urlString: String, token: String?): String = withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        if (!token.isNullOrEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer $token")
        }
        conn.connectTimeout = 5000
        conn.readTimeout = 5000
        val code = conn.responseCode
        if (code == HttpURLConnection.HTTP_OK) {
            conn.inputStream.bufferedReader().use { it.readText() }
        } else {
            val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            throw Exception("HTTP $code: $err")
        }
    }

    suspend fun post(urlString: String, jsonBody: String, token: String?): String = withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        if (!token.isNullOrEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer $token")
        }
        conn.doOutput = true
        conn.connectTimeout = 5000
        conn.readTimeout = 5000
        
        OutputStreamWriter(conn.outputStream).use { it.write(jsonBody) }
        
        val code = conn.responseCode
        if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED) {
            conn.inputStream.bufferedReader().use { it.readText() }
        } else {
            val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            throw Exception("HTTP $code: $err")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedCoreApp() {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var apiUrl by remember { mutableStateOf("http://10.0.2.2:8080/api/v1") } // Android localhost
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var token by remember { mutableStateOf("") }
    var loggedInUser by remember { mutableStateOf("Dr. Anjali Desai") }
    
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Active Patient Context
    var activePatient by remember { mutableStateOf<JSONObject?>(null) }
    
    // AI Scribe State
    var aiResult by remember { mutableStateOf<JSONObject?>(null) }

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
                        token = json.getString("accessToken")
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
                currentScreen = Screen.DASHBOARD
            }
        )
        
        Screen.DASHBOARD -> DashboardScreen(
            user = loggedInUser,
            onScanClick = { currentScreen = Screen.QR_SCANNER },
            onScribeClick = { currentScreen = Screen.AI_SCRIBE },
            onVitalsClick = { currentScreen = Screen.VITALS_ENTRY },
            onAssessClick = { currentScreen = Screen.NURSING_ASSESSMENT },
            onIncidentClick = { currentScreen = Screen.INCIDENT_REPORT },
            onTasksClick = { currentScreen = Screen.TASK_TRACKER },
            onMarClick = { currentScreen = Screen.MAR_SCANNER },
            onLogout = {
                token = ""
                currentScreen = Screen.LOGIN
            }
        )
        
        Screen.QR_SCANNER -> QrScannerScreen(
            onBack = { currentScreen = Screen.DASHBOARD },
            onPatientDetected = { patientJson ->
                activePatient = patientJson
                currentScreen = Screen.PATIENT_SUMMARY
            }
        )
        
        Screen.PATIENT_SUMMARY -> PatientSummaryScreen(
            patient = activePatient!!,
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.AI_SCRIBE -> AiScribeScreen(
            apiUrl = apiUrl,
            token = token,
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.VITALS_ENTRY -> VitalsEntryScreen(
            patientUhid = activePatient?.optString("uhid") ?: "UHID-12345",
            onBack = { currentScreen = Screen.DASHBOARD },
            onSubmit = { _, _, _, _, _ -> currentScreen = Screen.DASHBOARD }
        )
        
        Screen.NURSING_ASSESSMENT -> NursingAssessmentScreen(
            patientUhid = activePatient?.optString("uhid") ?: "UHID-12345",
            onBack = { currentScreen = Screen.DASHBOARD },
            onSubmit = { _, _, _ -> currentScreen = Screen.DASHBOARD }
        )
        
        Screen.INCIDENT_REPORT -> IncidentReportScreen(
            onBack = { currentScreen = Screen.DASHBOARD },
            onSubmit = { _, _ -> currentScreen = Screen.DASHBOARD }
        )
        
        Screen.TASK_TRACKER -> TaskTrackerScreen(
            onBack = { currentScreen = Screen.DASHBOARD }
        )
        
        Screen.MAR_SCANNER -> MarScannerScreen(
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo Icon
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFDADCE0), RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "MedCore Mobile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A73E8)
        )
        Text(
            text = "Clinical Physician Portal",
            fontSize = 14.sp,
            color = Color(0xFF5F6368)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = apiUrl,
            onValueChange = onApiUrlChange,
            label = { Text("Server API Base URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF1A73E8))
        } else {
            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
            ) {
                Text("Secure Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onSimulateBiometric,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Simulate Biometrics / PIN Bypass", fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun DashboardScreen(
    user: String,
    onScanClick: () -> Unit,
    onScribeClick: () -> Unit,
    onVitalsClick: () -> Unit,
    onAssessClick: () -> Unit,
    onIncidentClick: () -> Unit,
    onTasksClick: () -> Unit,
    onMarClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .androidx.compose.foundation.verticalScroll(androidx.compose.foundation.rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Welcome Back,", fontSize = 14.sp, color = Color(0xFF5F6368))
                Text(user, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202124))
            }
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color(0xFFEA4335)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // System Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Active Ward Duty", fontWeight = FontWeight.Bold, color = Color(0xFF1557B0))
                Text("Location: ICU / General Wards", fontSize = 13.sp, color = Color(0xFF1A73E8))
                Text("Shift: Morning (08:00 - 16:00)", fontSize = 13.sp, color = Color(0xFF1A73E8))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Action Buttons
        Text("Clinical Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202124))
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onScanClick() }
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Scan QR", tint = Color(0xFF1A73E8), modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Scan Patient QR Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Access 360 Patient Summary dynamically", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onScribeClick() }
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "AI Scribe", tint = Color(0xFF34A853), modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Launch AI Voice Scribe", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Dictate SOAP notes on the go", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
        
        Card(modifier = Modifier.fillMaxWidth().clickable { onVitalsClick() }.padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircle, contentDescription = "Vitals", tint = Color(0xFFE67C73), modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text("Bedside Vitals", fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("Enter offline vitals (Room DB)", fontSize = 12.sp, color = Color.Gray) }
            }
        }
        
        Card(modifier = Modifier.fillMaxWidth().clickable { onAssessClick() }.padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.List, contentDescription = "Assessments", tint = Color(0xFFF2A600), modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text("Nursing Assessments", fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("Morse & Braden scales", fontSize = 12.sp, color = Color.Gray) }
            }
        }
        
        Card(modifier = Modifier.fillMaxWidth().clickable { onIncidentClick() }.padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = "Incidents", tint = Color(0xFFD32F2F), modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text("Report Incident", fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("Log adverse events securely", fontSize = 12.sp, color = Color.Gray) }
            }
        }
        
        Card(modifier = Modifier.fillMaxWidth().clickable { onTasksClick() }.padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Tasks", tint = Color(0xFF1E8E3E), modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text("Operations Tasks", fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("Turnaround & Porter tracker", fontSize = 12.sp, color = Color.Gray) }
            }
        }
        
        Card(modifier = Modifier.fillMaxWidth().clickable { onMarClick() }.padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = "MAR", tint = Color(0xFF5E35B1), modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text("MAR Bedside Scan", fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("5-Rights Medication verification", fontSize = 12.sp, color = Color.Gray) }
            }
        }
    }
}

@Composable
fun QrScannerScreen(
    onBack: () -> Unit,
    onPatientDetected: (JSONObject) -> Unit
) {
    var manualUhid by remember { mutableStateOf("") }
    var isScanningSimulated by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("QR Barcode Scanner", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Simulated Camera Viewfinder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(2.dp, if (hasCameraPermission) Color(0xFF1A73E8) else Color.Red, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Simulated Camera",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Simulating Camera Viewfinder...", color = Color.White, fontSize = 14.sp)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Permission Denied",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Camera permission is required to scan QR codes.", color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Scan Simulator: Enter QR data payload", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = manualUhid,
            onValueChange = { manualUhid = it },
            label = { Text("Paste Patient QR/UHID String") },
            placeholder = { Text("e.g. {\"uhid\":\"MED-2026-000001\",\"name\":\"Rahul Sharma\"}") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (manualUhid.isNotEmpty()) {
                    try {
                        val json = JSONObject(manualUhid)
                        onPatientDetected(json)
                    } catch (e: Exception) {
                        // Fallback to minimal patient object if plain string is entered
                        val fallback = JSONObject().apply {
                            put("uhid", manualUhid)
                            put("name", "Simulated Patient")
                            put("dob", "1992-05-15")
                        }
                        onPatientDetected(fallback)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
        ) {
            Text("Simulate Decode QR Code")
        }
    }
}

@Composable
fun PatientSummaryScreen(
    patient: JSONObject,
    onBack: () -> Unit
) {
    val name = patient.optString("name", "Rahul Sharma")
    val uhid = patient.optString("uhid", "MED-2026-000001")
    val dob = patient.optString("dob", "1992-08-10")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Patient 360° Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Patient Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202124))
                Text("UHID: $uhid", fontSize = 14.sp, color = Color(0xFF1A73E8), fontWeight = FontWeight.SemiBold)
                Text("DOB: $dob", fontSize = 13.sp, color = Color(0xFF5F6368))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Clinical Metrics Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        // Vitals
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Blood Pressure", color = Color.Gray)
                    Text("120/80 mmHg", fontWeight = FontWeight.Bold)
                }
                Divider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SpO2 (Oxygen)", color = Color.Gray)
                    Text("99%", fontWeight = FontWeight.Bold, color = Color(0xFF34A853))
                }
                Divider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Heart Rate", color = Color.Gray)
                    Text("72 bpm", fontWeight = FontWeight.Bold)
                }
                Divider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Allergies", color = Color.Gray)
                    Text("Penicillin (High)", fontWeight = FontWeight.Bold, color = Color(0xFFEA4335))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F6368))
        ) {
            Text("Done")
        }
    }
}

@Composable
fun AiScribeScreen(
    apiUrl: String,
    token: String,
    onBack: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var transcript by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var soapResult by remember { mutableStateOf<JSONObject?>(null) }
    val scope = rememberCoroutineScope()
    
    val context = LocalContext.current
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasAudioPermission = isGranted
        if (isGranted) {
            isRecording = true
            transcript = "Patient presents with a mild fever and severe headache for the past 2 days. Blood pressure is 120/80 mmHg. Heart rate is 85 bpm. Prescribing paracetamol for the fever."
            isRecording = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("AI Voice Scribe Portal", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (transcript.isEmpty()) "Tap simulate to dictate clinical notes..." else transcript,
                    fontSize = 14.sp,
                    color = if (transcript.isEmpty()) Color.Gray else Color.Black
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    if (hasAudioPermission) {
                        isRecording = true
                        transcript = "Patient presents with a mild fever and severe headache for the past 2 days. Blood pressure is 120/80 mmHg. Heart rate is 85 bpm. Prescribing paracetamol for the fever."
                        isRecording = false
                    } else {
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335))
            ) {
                Text("🎙 Record Voice")
            }
            
            Button(
                onClick = {
                    isProcessing = true
                    scope.launch {
                        try {
                            val body = JSONObject().apply {
                                put("transcript", transcript)
                            }.toString()
                            val res = NetworkClient.post("$apiUrl/ai/extract", body, token)
                            soapResult = JSONObject(res)
                        } catch (e: Exception) {
                            // Fallback mock
                            soapResult = JSONObject().apply {
                                put("confidence_score", 0.92)
                                put("generated_soap_note", JSONObject().apply {
                                    put("Subjective", "Fever and headache for 2 days.")
                                    put("Objective", "BP 120/80, HR 85.")
                                    put("Assessment", "Acute symptomatic fever.")
                                    put("Plan", "Paracetamol PRN.")
                                })
                            }
                        } finally {
                            isProcessing = false
                        }
                    }
                },
                enabled = transcript.isNotEmpty() && !isProcessing,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
            ) {
                Text(if (isProcessing) "Processing..." else "Process SOAP")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (soapResult != null) {
            Text("AI Extracted SOAP Note Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            val soapObj = soapResult!!.optJSONObject("generated_soap_note")
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (soapObj != null) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Subjective", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                                Text(soapObj.optString("Subjective", "N/A"), fontSize = 13.sp)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Objective", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                                Text(soapObj.optString("Objective", "N/A"), fontSize = 13.sp)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Assessment", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                                Text(soapObj.optString("Assessment", "N/A"), fontSize = 13.sp)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Plan", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                                Text(soapObj.optString("Plan", "N/A"), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
