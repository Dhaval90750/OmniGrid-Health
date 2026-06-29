package com.medcore.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.medcore.mobile.data.SessionManager
import com.medcore.mobile.navigation.NavRoutes
import com.medcore.mobile.ui.*
import com.medcore.mobile.ui.theme.MedCoreTheme
import com.medcore.mobile.viewmodels.AuthViewModel
import com.medcore.mobile.viewmodels.PatientViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedCoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MedCoreAppContent(sessionManager)
                }
            }
        }
    }
}

@Composable
fun MedCoreAppContent(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val patientViewModel: PatientViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    val activePatient by patientViewModel.activePatient.collectAsState()

    NavHost(navController = navController, startDestination = NavRoutes.Login.route) {
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                sessionManager = sessionManager,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.PinLogin.route) {
            PinLoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(NavRoutes.PinLogin.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(NavRoutes.Profile.route) {
            MyProfileScreen(
                sessionManager = sessionManager,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.MyOpdQueue.route) {
            MyOpdQueueScreen(
                onBack = { navController.popBackStack() },
                onPatientClick = { patient ->
                    patientViewModel.setPatient(patient)
                    navController.navigate(NavRoutes.PatientSummary.route)
                }
            )
        }

        composable(NavRoutes.AdmitPatient.route) {
            AdmitPatientScreen(
                patientName = activePatient?.optString("fullName") ?: "Unknown",
                onBack = { navController.popBackStack() },
                onAdmitSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.PrescriptionWriter.route) {
            PrescriptionWriterScreen(
                patientName = activePatient?.optString("fullName") ?: "Unknown",
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.WardDashboard.route) {
            WardDashboardScreen(
                wardName = "General Ward",
                onBack = { navController.popBackStack() },
                onPatientClick = { _ -> /* Navigate to patient summary */ }
            )
        }

        composable(NavRoutes.OtSchedule.route) {
            OtScheduleScreen(
                onBack = { navController.popBackStack() },
                onCaseClick = { _ -> }
            )
        }

        composable(NavRoutes.DischargeInitiation.route) {
            DischargeInitiationScreen(
                patientName = activePatient?.optString("fullName") ?: "Unknown",
                onBack = { navController.popBackStack() },
                onInitiateSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.NotificationList.route) {
            NotificationListScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SampleCollection.route) {
            SampleCollectionScreen(
                patientName = activePatient?.optString("fullName") ?: "Unknown",
                onBack = { navController.popBackStack() },
                onCollectSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.DispensingQueue.route) {
            DispensingQueueScreen(
                onBack = { navController.popBackStack() },
                onPrescriptionClick = { _ -> }
            )
        }

        composable(NavRoutes.ErTriage.route) {
            ErTriageScreen(
                patientName = activePatient?.optString("fullName") ?: "Unknown",
                onBack = { navController.popBackStack() },
                onTriageSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.IcuDashboard.route) {
            IcuDashboardScreen(
                onBack = { navController.popBackStack() },
                onPatientClick = { _ -> }
            )
        }

        composable(NavRoutes.DocumentUpload.route) {
            DocumentUploadScreen(
                patientName = activePatient?.optString("fullName") ?: "Unknown",
                onBack = { navController.popBackStack() },
                onUploadSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.BloodAvailability.route) {
            BloodAvailabilityScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.DiagnosisEntry.route) {
            DiagnosisEntryScreen(
                onBack = { navController.popBackStack() },
                onSave = { _ -> navController.popBackStack() }
            )
        }

        composable(NavRoutes.RaiseServiceRequest.route) {
            RaiseServiceRequestScreen(
                onBack = { navController.popBackStack() },
                onSubmitSuccess = { navController.popBackStack() }
            )
        }
        
        composable("quick_reg") {
            QuickRegistrationScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { patient ->
                    patientViewModel.setPatient(patient)
                    navController.navigate(NavRoutes.PatientSummary.route)
                }
            )
        }

        composable(NavRoutes.QrScanner.route) {
            QrScannerScreen(
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1", // Should come from a config/datastore
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() },
                onPatientDetected = { patient ->
                    patientViewModel.setPatient(patient)
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.PatientSummary.route) {
            activePatient?.let { patient ->
                PatientSummaryScreen(
                    patient = patient,
                    onBack = { navController.popBackStack() },
                    onClearPatient = {
                        patientViewModel.clearPatient()
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(NavRoutes.VitalsEntry.route) {
            VitalsEntryScreen(
                patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN",
                patientId = activePatient?.optString("id") ?: "",
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() },
                onSubmit = { _, _, _, _, _ -> navController.popBackStack() }
            )
        }

        // Add more routes as we implement screens
        composable(NavRoutes.AiScribe.route) {
            AiScribeScreen(
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.NursingAssessment.route) {
            NursingAssessmentScreen(
                patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN",
                patientId = activePatient?.optString("id") ?: "",
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() },
                onSubmitSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.OrderEntry.route) {
            OrderEntryScreen(
                patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN",
                patientId = activePatient?.optString("id") ?: "",
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MarScanner.route) {
            MarScannerScreen(
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                patientId = activePatient?.optString("id") ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.IncidentReport.route) {
            IncidentReportScreen(
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() },
                onSubmitSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TaskTracker.route) {
            TaskTrackerScreen(
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Analytics.route) {
            AnalyticsScreen(
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Telemedicine.route) {
            TelemedicineScreen(
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                patientName = activePatient?.optString("fullName") ?: "Unknown",
                patientUhid = activePatient?.optString("uhid") ?: "UNKNOWN",
                doctorName = sessionManager.username ?: "Doctor",
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Inventory.route) {
            InventoryScreen(
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Billing.route) {
            BillingScreen(
                apiUrl = "https://medcore-his-backend-production.up.railway.app/api/v1",
                token = sessionManager.token ?: "",
                onBack = { navController.popBackStack() }
            )
        }
    }
}
