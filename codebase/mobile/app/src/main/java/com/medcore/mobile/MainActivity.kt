package com.medcore.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.json.JSONObject

import dagger.hilt.android.AndroidEntryPoint
import com.medcore.mobile.ui.*
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
    ANALYTICS, TELEMEDICINE, INVENTORY, BILLING, ORDER_ENTRY,
    // Phase 3 additions
    STAFF_ROSTER, DOCTOR_LOGBOOK, DIET_ORDER, INDENT_APPROVAL
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
            onRosterClick = { currentScreen = Screen.STAFF_ROSTER },
            onLogbookClick = { currentScreen = Screen.DOCTOR_LOGBOOK },
            onDietClick = { requirePatient(Screen.DIET_ORDER) },
            onIndentApprovalClick = { currentScreen = Screen.INDENT_APPROVAL },
            onLogout = {
                token = ""
                permissions = emptyMap()
                activePatient = null
                pendingScreen = null
                currentScreen = Screen.LOGIN
            }
        )
        
        Screen.QR_SCANNER -> QrScannerScreen(
            apiUrl = apiUrl, token = token,
            onBack = { pendingScreen = null; currentScreen = Screen.DASHBOARD },
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
            onClearPatient = { activePatient = null; currentScreen = Screen.DASHBOARD }
        )
        
        Screen.AI_SCRIBE -> AiScribeScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        Screen.VITALS_ENTRY -> VitalsEntryScreen(patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN", patientId = activePatient?.optString("id") ?: "", apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD }, onSubmit = { _, _, _, _, _ -> currentScreen = Screen.DASHBOARD })
        Screen.ORDER_ENTRY -> OrderEntryScreen(patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN", patientId = activePatient?.optString("id") ?: "", apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        Screen.NURSING_ASSESSMENT -> NursingAssessmentScreen(patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN", patientId = activePatient?.optString("id") ?: "", apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD }, onSubmitSuccess = { currentScreen = Screen.DASHBOARD })
        Screen.INCIDENT_REPORT -> IncidentReportScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD }, onSubmitSuccess = { currentScreen = Screen.DASHBOARD })
        Screen.TASK_TRACKER -> TaskTrackerScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        Screen.MAR_SCANNER -> MarScannerScreen(apiUrl = apiUrl, token = token, patientId = activePatient?.optString("id") ?: "", onBack = { currentScreen = Screen.DASHBOARD })
        Screen.ANALYTICS -> AnalyticsScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        Screen.TELEMEDICINE -> TelemedicineScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        Screen.INVENTORY -> InventoryScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        Screen.BILLING -> BillingScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        
        Screen.STAFF_ROSTER -> StaffRosterScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        Screen.DOCTOR_LOGBOOK -> DoctorLogbookScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        Screen.DIET_ORDER -> DietOrderScreen(patientId = activePatient?.optString("id") ?: "", apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
        Screen.INDENT_APPROVAL -> IndentApprovalScreen(apiUrl = apiUrl, token = token, onBack = { currentScreen = Screen.DASHBOARD })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    apiUrl: String, username: String, password: String, errorMsg: String, isLoading: Boolean,
    onApiUrlChange: (String) -> Unit, onUsernameChange: (String) -> Unit, onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit, onSimulateBiometric: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Logo",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("MedcoreHIS", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground)
        Text("Premium Doctor Portal", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(
            value = username, onValueChange = onUsernameChange, label = { Text("Physician ID") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password, onValueChange = onPasswordChange, label = { Text("Secure Password") },
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val description = if (passwordVisible) "Hide" else "Show"
                androidx.compose.material3.TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(description, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
        )
        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(32.dp))
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            Button(
                onClick = onLogin, modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Authenticate", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onSimulateBiometric, modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = "FaceID", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("FaceID Login", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

data class ActionItem(val title: String, val icon: ImageVector, val color: Color, val onClick: () -> Unit)

@Composable
fun CarouselSection(title: String, items: List<ActionItem>) {
    if (items.isEmpty()) return
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(items) { action ->
                Card(
                    modifier = Modifier.size(140.dp, 120.dp).clickable { action.onClick() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = action.color.copy(alpha = 0.1f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(action.icon, contentDescription = action.title, tint = action.color, modifier = Modifier.size(24.dp))
                            }
                        }
                        Text(action.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    user: String, permissions: Map<String, String>,
    onScanClick: () -> Unit, onScribeClick: () -> Unit, onVitalsClick: () -> Unit,
    onAssessClick: () -> Unit, onOrderClick: () -> Unit, onIncidentClick: () -> Unit,
    onTasksClick: () -> Unit, onMarClick: () -> Unit, onAnalyticsClick: () -> Unit,
    onTelemedicineClick: () -> Unit, onInventoryClick: () -> Unit, onBillingClick: () -> Unit,
    onRosterClick: () -> Unit, onLogbookClick: () -> Unit, onDietClick: () -> Unit,
    onIndentApprovalClick: () -> Unit, onLogout: () -> Unit
) {
    fun hasAccess(module: String): Boolean = permissions[module] != null && permissions[module] != "NO_ACCESS"

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onScanClick,
                icon = { Icon(Icons.Default.Menu, "Scan") },
                text = { Text("Scan QR") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Good Shift,", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(user, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = onLogout) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Premium Hero Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))).padding(24.dp)) {
                    Column {
                        Text("Active Shift Details", style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Department: General Medicine", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha=0.9f))
                        Text("2 New Admits • 5 Pending Lab Results", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha=0.9f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Clinical Workflows
            val clinicalActions = mutableListOf<ActionItem>()
            if (hasAccess("Clinical Notes")) {
                clinicalActions.add(ActionItem("AI Scribe", Icons.Default.PlayArrow, Color(0xFF10B981), onScribeClick))
                clinicalActions.add(ActionItem("Telemed", Icons.Default.Call, Color(0xFFE11D48), onTelemedicineClick))
                clinicalActions.add(ActionItem("Diet Order", Icons.Default.ShoppingCart, Color(0xFFF59E0B), onDietClick)) // Phase 3
                clinicalActions.add(ActionItem("Logbook", Icons.Default.Edit, Color(0xFF6366F1), onLogbookClick)) // Phase 3
                clinicalActions.add(ActionItem("Roster", Icons.Default.DateRange, Color(0xFF8B5CF6), onRosterClick)) // Phase 3
            }
            CarouselSection("Clinical Workflows", clinicalActions)

            // Bedside Care
            val careActions = mutableListOf<ActionItem>()
            if (hasAccess("Clinical Notes")) {
                careActions.add(ActionItem("Vitals", Icons.Default.AddCircle, Color(0xFFEC4899), onVitalsClick))
                careActions.add(ActionItem("Orders", Icons.Default.List, Color(0xFF3B82F6), onOrderClick))
                careActions.add(ActionItem("Assessments", Icons.Default.ThumbUp, Color(0xFF14B8A6), onAssessClick))
            }
            if (hasAccess("Pharmacy")) {
                careActions.add(ActionItem("MAR Scanner", Icons.Default.Search, Color(0xFF8B5CF6), onMarClick))
            }
            CarouselSection("Bedside Care", careActions)

            // Management
            val mgmtActions = mutableListOf<ActionItem>()
            if (hasAccess("Dashboards")) {
                mgmtActions.add(ActionItem("Analytics", Icons.Default.Info, Color(0xFF4F46E5), onAnalyticsClick))
            }
            if (hasAccess("Inventory")) {
                mgmtActions.add(ActionItem("Indent Approvals", Icons.Default.CheckCircle, Color(0xFF10B981), onIndentApprovalClick)) // Phase 3
                mgmtActions.add(ActionItem("Inventory", Icons.Default.ShoppingCart, Color(0xFFD97706), onInventoryClick))
            }
            if (hasAccess("Operations")) {
                mgmtActions.add(ActionItem("Task Tracker", Icons.Default.CheckCircle, Color(0xFF0F766E), onTasksClick))
            }
            mgmtActions.add(ActionItem("Incidents", Icons.Default.Warning, Color(0xFFDC2626), onIncidentClick))
            CarouselSection("Management", mgmtActions)
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
