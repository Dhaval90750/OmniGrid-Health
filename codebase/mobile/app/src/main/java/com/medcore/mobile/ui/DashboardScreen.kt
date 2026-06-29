package com.medcore.mobile.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medcore.mobile.data.SessionManager
import com.medcore.mobile.viewmodels.DashboardViewModel

@Composable
fun DashboardScreen(
    sessionManager: SessionManager,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Patients", "Clinical", "Tasks", "Alerts", "More")
    
    val statsText by viewModel.statsText.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadMetrics()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Patients") },
                    label = { Text("Patients") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Clinical") },
                    label = { Text("Clinical") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
                    label = { Text("Alerts") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.MoreVert, contentDescription = "More") },
                    label = { Text("More") }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigate("qr_scanner") },
                icon = { Icon(Icons.Default.Menu, "Scan") },
                text = { Text("Scan QR") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Good Shift,", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(sessionManager.username ?: "Doctor", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = onLogout) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Premium Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text("Active Shift Details", style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Department: General Medicine", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                        Text(statsText, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            when (selectedTab) {
                0 -> PatientsSection(onNavigate)
                1 -> ClinicalSection(onNavigate, sessionManager.permissions)
                2 -> TasksSection(onNavigate)
                3 -> AlertsSection(onNavigate)
                4 -> MoreSection(onNavigate)
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PatientsSection(onNavigate: (String) -> Unit) {
    val items = listOf(
        ActionItem("Patient Search", Icons.Default.Search, Color(0xFF3B82F6)) { onNavigate("patient_search") },
        ActionItem("Active Patients", Icons.Default.Person, Color(0xFF10B981)) { onNavigate("active_patients") },
        ActionItem("My IPD Patients", Icons.Default.Home, Color(0xFF6366F1)) { onNavigate("my_ipd_patients") },
        ActionItem("Quick Reg (ER)", Icons.Default.Add, Color(0xFFF43F5E)) { onNavigate("quick_reg") }
    )
    CarouselSection("Patients", items)
}

@Composable
fun ClinicalSection(onNavigate: (String) -> Unit, permissions: Map<String, String>) {
    fun hasAccess(module: String): Boolean = permissions[module] != null && permissions[module] != "NO_ACCESS"

    val items = mutableListOf<ActionItem>()
    if (hasAccess("Clinical Notes")) {
        items.add(ActionItem("AI Scribe", Icons.Default.PlayArrow, Color(0xFF10B981)) { onNavigate("ai_scribe") })
        items.add(ActionItem("Telemed", Icons.Default.Call, Color(0xFFE11D48)) { onNavigate("telemedicine") })
        items.add(ActionItem("Lab Results", Icons.Default.Search, Color(0xFF0EA5E9)) { onNavigate("lab_results") })
        items.add(ActionItem("Radiology", Icons.Default.Search, Color(0xFFF59E0B)) { onNavigate("radiology") })
        items.add(ActionItem("Diet Order", Icons.Default.ShoppingCart, Color(0xFFF59E0B)) { onNavigate("diet_order") })
    }
    CarouselSection("Clinical Workflows", items)
    
    val careItems = mutableListOf<ActionItem>()
    if (hasAccess("Clinical Notes")) {
        careItems.add(ActionItem("Vitals", Icons.Default.AddCircle, Color(0xFFEC4899)) { onNavigate("vitals_entry") })
        careItems.add(ActionItem("Orders", Icons.Default.List, Color(0xFF3B82F6)) { onNavigate("order_entry") })
        careItems.add(ActionItem("Assessments", Icons.Default.ThumbUp, Color(0xFF14B8A6)) { onNavigate("nursing_assessment") })
    }
    CarouselSection("Bedside Care", careItems)
}

@Composable
fun TasksSection(onNavigate: (String) -> Unit) {
    val items = listOf(
        ActionItem("Nursing Tasks", Icons.Default.List, Color(0xFF8B5CF6)) { onNavigate("task_tracker") },
        ActionItem("Housekeeping", Icons.Default.CheckCircle, Color(0xFF0F766E)) { onNavigate("task_tracker") },
        ActionItem("Transport", Icons.Default.LocationOn, Color(0xFFF59E0B)) { onNavigate("transport_request") }
    )
    CarouselSection("Tasks & Duties", items)
}

@Composable
fun AlertsSection(onNavigate: (String) -> Unit) {
    val items = listOf(
        ActionItem("Critical Labs", Icons.Default.Warning, Color(0xFFDC2626)) { onNavigate("critical_alerts") },
        ActionItem("Risk Alerts", Icons.Default.Info, Color(0xFFF59E0B)) { onNavigate("notification_list") },
        ActionItem("Notifications", Icons.Default.Notifications, Color(0xFF3B82F6)) { onNavigate("notification_list") }
    )
    CarouselSection("Alerts", items)
}

@Composable
fun MoreSection(onNavigate: (String) -> Unit) {
    val items = listOf(
        ActionItem("Analytics", Icons.Default.Info, Color(0xFF4F46E5)) { onNavigate("analytics") },
        ActionItem("Inventory", Icons.Default.ShoppingCart, Color(0xFFD97706)) { onNavigate("inventory") },
        ActionItem("Billing", Icons.Default.DateRange, Color(0xFF607D8B)) { onNavigate("billing") },
        ActionItem("Profile", Icons.Default.AccountCircle, Color(0xFF10B981)) { onNavigate("profile") }
    )
    CarouselSection("More Options", items)
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
                    modifier = Modifier
                        .size(140.dp, 120.dp)
                        .clickable { action.onClick() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
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
