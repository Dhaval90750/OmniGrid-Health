# MedCore HIS — Complete SRS vs Codebase Audit (File-by-File)

This document presents a highly detailed, file-by-file audit comparing the requirements specified in the **System Requirements Specification (SRS)** against the actual implementation in the codebase.

---

## Overall Implementation Summary

| Category | SRS Target | Codebase Current State | Completion % |
| :--- | :--- | :--- | :---: |
| **Database Tables** | 200–260 Tables | ~53 Tables | **20%** |
| **API Endpoints** | 500+ APIs | ~40 stubs/basic CRUD | **8%** |
| **Frontend Screens** | 250+ Screens | ~24 screens (mostly mockups) | **10%** |
| **Functional Modules** | 30+ Modules | 2 (Authentication & Patient Registration) | **7%** |

---

## Detailed File-by-File Deviations

### 1. Authentication & Access Control (SRS §2)
**Key SRS Requirements:** 
- Hybrid RBAC + ABAC model with 14 strict roles (Doctor, Nurse, Admin, etc.)
- Break-the-glass emergency protocol
- MFA, PIN, Biometric session support
- Dynamic Access Control matrix

**Files Audited & Deviations:**
- **[MODIFY] `backend/src/main/java/com/medcore/his/controller/AuthController.java`**:
  - The `/signup` endpoint accepts almost any arbitrary role name, automatically prefixing it with `ROLE_`, instead of enforcing the strict 14 roles defined in the SRS.
  - If no role is provided, it defaults to `ROLE_DOCTOR`.
  - There is no logic for MFA, Biometric login, PIN login, or Break-the-glass protocols.
  - Hardcoded session creation logic does not respect the timeout limits (e.g., 15 mins for Admin, 60 mins for Clinical).
- **[MODIFY] `backend/src/main/java/com/medcore/his/security/SecurityConfig.java`**:
  - Sets up JWT correctly but does not contain or integrate with a dynamic ABAC policy engine or the required Access Control Matrix.
- **[MODIFY] `Controllers (43 files)`**:
  - Only `PatientController`, `RiskAlertController`, `RoleController`, and `UserController` actually use the `@PreAuthorize` annotation to secure endpoints.
  - The remaining 39 controllers (e.g., `BillingController.java`, `PharmacyController.java`, `VisitController.java`) completely lack `@PreAuthorize` method-level security, essentially bypassing the RBAC model for those modules.

### 2. Patient Registration & Lifecycle (SRS §3.2)
**Key SRS Requirements:**
- Highly detailed demographic capture including Photo uploads and Aadhaar/ABHA fields.
- Duplicate detection via fuzzy matching.
- Multi-format QR generation (Wristband, Slip, Digital).

**Files Audited & Deviations:**
- **[MODIFY] `frontend/src/app/patients/new/page.tsx`**:
  - Frontend has the `react-webcam` UI for photo capture and a Duplicate Detection Modal. However, it sends `photoBase64` which the backend ignores.
- **[MODIFY] `backend/src/main/java/com/medcore/his/controller/PatientController.java` & `PatientRegistrationRequest.java`**:
  - The API explicitly ignores the `photoBase64` field sent by the frontend (it is completely missing in the DTO).
  - The QR code endpoint `/qr-pdf` strictly returns a PDF. It does not separate logic for thermal wristband printing or raw digital links.
- **[MODIFY] `backend/src/main/java/com/medcore/his/service/PatientService.java`**:
  - **Duplicate Detection**: Implements a strict match on mobile number or exact match on First + Last Name + DOB, missing the required "fuzzy matching" (SRS 3.2.2). The frontend handles the HTTP 409 response nicely, but the backend matching is too rigid.
  - **UHID Format**: Generates `MED-YYYY-XXXXXX` instead of the mandated `HOS-YYYY-NNNNNNN` format.
  - **QR Code Payload**: Encodes only `uhid`, `name`, and `dob`. It misses mandatory fields like `hospital_code` and `blood_group` specified in the QR JSON structure (SRS 3.2.3).

### 3. OPD & Doctor Assessment (SRS §3.3 - §3.7)
**Key SRS Requirements:**
- Priority queues, live token displays.
- Detailed clinical assessment (HPI, ROS).
- Structured ICD-10 diagnosis entry.
- AI Voice-to-Clinical Notes & Auto-SOAP Note generation.

**Files Audited & Deviations:**
- **[MODIFY] `backend/src/main/java/com/medcore/his/controller/VisitController.java`**:
  - **Visit Creation**: The `createVisit` endpoint is very basic. It takes a raw Map payload and extracts `chiefComplaint`, ignoring required fields like `priority`, `department`, and `referredBy`.
  - **Token Logic**: Implements a simplistic `maxToken + 1` logic per doctor/date. There is no concept of priority queues (e.g., for Emergency/VIP patients) or live WebSocket notifications.
  - **Clinical Notes**: The `/{id}/notes` endpoint takes free-text for HPI, Physical Exam, and Treatment Plan, but completely misses the structured ICD-10 diagnosis fields.
  - **AI & SOAP**: There is no integration with Whisper AI or LLMs for Voice-to-Text or SOAP generation here. The data is just saved directly to the database.

---
*Audit is ongoing. More modules will be appended here.*
