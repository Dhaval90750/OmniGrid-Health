package com.medcore.mobile.navigation

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object PinLogin : NavRoutes("pin_login")
    object BiometricLogin : NavRoutes("biometric_login")
    object Profile : NavRoutes("profile")
    
    object Dashboard : NavRoutes("dashboard")
    object QrScanner : NavRoutes("qr_scanner")
    object PatientSummary : NavRoutes("patient_summary")
    
    object AiScribe : NavRoutes("ai_scribe")
    object VoiceRecording : NavRoutes("voice_recording")
    object SoapNoteEditor : NavRoutes("soap_note_editor")
    
    object VitalsEntry : NavRoutes("vitals_entry")
    object VitalTrends : NavRoutes("vital_trends")
    
    object NursingAssessment : NavRoutes("nursing_assessment")
    object WardDashboard : NavRoutes("ward_dashboard")
    
    object OrderEntry : NavRoutes("order_entry")
    object PrescriptionWriter : NavRoutes("prescription_writer")
    
    object MarScanner : NavRoutes("mar_scanner")
    object IncidentReport : NavRoutes("incident_report")
    object TaskTracker : NavRoutes("task_tracker")
    object Analytics : NavRoutes("analytics")
    object Telemedicine : NavRoutes("telemedicine")
    object Inventory : NavRoutes("inventory")
    object Billing : NavRoutes("billing")
    
    // Wave 1 & 2 routes can be added here
    object MyOpdQueue : NavRoutes("my_opd_queue")
    object OpdConsultation : NavRoutes("opd_consultation")
    object AdmitPatient : NavRoutes("admit_patient")
    object MyIpdPatients : NavRoutes("my_ipd_patients")
    object ErTriage : NavRoutes("er_triage")
    object ErDashboard : NavRoutes("er_dashboard")
    object IcuDashboard : NavRoutes("icu_dashboard")
    object OtSchedule : NavRoutes("ot_schedule")
    object NotificationList : NavRoutes("notification_list")
    object DischargeInitiation : NavRoutes("discharge_initiation")
    object SampleCollection : NavRoutes("sample_collection")
    object DispensingQueue : NavRoutes("dispensing_queue")
    object DocumentUpload : NavRoutes("document_upload")
    object BloodAvailability : NavRoutes("blood_availability")
    object DiagnosisEntry : NavRoutes("diagnosis_entry")
    object RaiseServiceRequest : NavRoutes("raise_service_request")
}
