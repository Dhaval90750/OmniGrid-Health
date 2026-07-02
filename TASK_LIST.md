# MedCore HIS — Detailed Development Task List
> Derived from: `srs_vs_codebase_audit.md` + full SRS document review  
> Last Updated: 2026-07-02  
> Status Legend: `[ ]` = Not Started | `[/]` = In Progress | `[x]` = Done

---

## 🔴 PRIORITY 1 — CRITICAL FOUNDATION (Security, Auth, Core Workflows)

### MODULE 1: Authentication & Access Control (SRS §2)

#### 1.1 Role Enforcement
- `[ ]` **[BE]** Refactor `AuthController.java` `/signup` to enforce exactly 14 predefined roles from an allowlist (`ROLE_SUPER_ADMIN`, `ROLE_HOSPITAL_ADMIN`, `ROLE_DOCTOR`, `ROLE_NURSE`, `ROLE_LAB_TECH`, `ROLE_PATHOLOGIST`, `ROLE_RADIOLOGIST`, `ROLE_PHARMACIST`, `ROLE_RECEPTIONIST`, `ROLE_BILLING_EXEC`, `ROLE_INVENTORY_MGR`, `ROLE_OPERATIONS_MGR`, `ROLE_DIETITIAN`, `ROLE_MANAGEMENT`)
- `[ ]` **[BE]** Reject any role not in the allowlist with a clear `400 Bad Request` error
- `[ ]` **[BE]** Remove auto-creation of roles — roles must be pre-seeded in a `V1__seed_roles.sql` migration
- `[ ]` **[BE]** Create Flyway migration `V1__seed_roles.sql` to pre-seed all 14 roles with correct names and descriptions
- `[ ]` **[BE]** Enable Flyway (`flyway.enabled: true`) in `application.properties`

#### 1.2 RBAC — Secure All 39 Unprotected Controllers
- `[ ]` **[BE]** Add `@PreAuthorize` to `AdmissionController.java`
- `[ ]` **[BE]** Add `@PreAuthorize` to `BillingController.java` — billing roles only
- `[ ]` **[BE]** Add `@PreAuthorize` to `VisitController.java` — doctor/admin/receptionist
- `[ ]` **[BE]** Add `@PreAuthorize` to `LabController.java` — lab tech/doctor/admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `RadiologyController.java` — radiologist/doctor/admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `PharmacyController.java` — pharmacist/admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `PrescriptionController.java` — doctor/admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `NursingController.java` — nurse/doctor/admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `IcuController.java`
- `[ ]` **[BE]** Add `@PreAuthorize` to `OtController.java`
- `[ ]` **[BE]** Add `@PreAuthorize` to `InventoryController.java` — inventory manager/admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `OperationsController.java` — operations manager/admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `StaffController.java` — admin/hospital admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `AnalyticsController.java` — management/admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `DiagnosisController.java` — doctor/admin
- `[ ]` **[BE]** Add `@PreAuthorize` to `AllergyController.java`
- `[ ]` **[BE]** Add `@PreAuthorize` to `EmergencyController.java`
- `[ ]` **[BE]** Add `@PreAuthorize` to `DischargeController.java`
- `[ ]` **[BE]** Add `@PreAuthorize` to `ConsentController.java`
- `[ ]` **[BE]** Add `@PreAuthorize` to remaining 19+ controllers

#### 1.3 Session Management
- `[ ]` **[BE]** Add configurable JWT expiry per role type (Admin: 15 min, Clinical: 60 min, Front Desk: 30 min)
- `[ ]` **[BE]** Implement failed login lockout after 5 attempts (lock for 30 minutes) — store counts in Redis or DB
- `[ ]` **[BE]** Implement max concurrent session limit (2 per user) — invalidate oldest session on 3rd login

#### 1.4 Break-the-Glass Protocol (SRS §2.4.3)
- `[ ]` **[BE]** Create `EmergencyAccessRequest` entity and repository
- `[ ]` **[BE]** Create `POST /api/v1/auth/emergency-access` endpoint — captures justification, grants 60-minute access
- `[ ]` **[BE]** Log every break-the-glass access to audit trail
- `[ ]` **[BE]** Send notification to: patient's attending doctor + department head on emergency access
- `[ ]` **[FE]** Build Break-the-Glass modal dialog in frontend

#### 1.5 Global Exception Handler
- `[ ]` **[BE]** Create `GlobalExceptionHandler.java` with `@ControllerAdvice`:
  - `MethodArgumentNotValidException` → `400` with field-level errors
  - `AccessDeniedException` → `403` with clear message
  - `ResourceNotFoundException` → `404`
  - `DuplicatePatientException` → `409`
  - All unhandled `Exception` → `500` sanitized

---

### MODULE 2: Patient Registration & Lifecycle (SRS §3)

#### 2.1 Fix Registration DTO & Service
- `[ ]` **[BE]** Add `photoBase64` to `PatientRegistrationRequest.java` DTO
- `[ ]` **[BE]** Add `photo` column to `patients` table via Flyway migration; save in `PatientService.java`
- `[ ]` **[BE]** Fix UHID format: `MED-YYYY-XXXXXX` → `HOS-YYYY-NNNNNNN` (SRS §3.2.1)
- `[ ]` **[BE]** Add missing fields to `Patient.java`: `middleName`, `occupation`, `secondaryMobile`, `abhaId`, `passportNumber`, `religion`, `referredBy`
- `[ ]` **[FE]** Add missing fields to registration form `patients/new/page.tsx`: ABHA ID, occupation, secondary mobile, religion, referred by

#### 2.2 Fix QR Code Generation (SRS §3.2.3)
- `[ ]` **[BE]** Update QR payload to include: `uhid`, `hospital_code`, `name`, `dob`, `blood_group`, `allergies_flag`, `checksum`
- `[ ]` **[BE]** Create `GET /api/v1/patients/{id}/qr/digital` — raw JSON QR data for SMS/digital link
- `[ ]` **[BE]** Create `GET /api/v1/patients/{id}/qr/wristband-pdf` — thermal wristband print format

#### 2.3 Fuzzy Duplicate Detection (SRS §3.2.2)
- `[ ]` **[BE]** Replace exact match in `PatientService.java` with `pg_trgm` similarity + phonetic matching
- `[ ]` **[BE]** Return list of similar patients with similarity scores in 409 response

#### 2.4 Returning Patient Lookup
- `[ ]` **[BE]** Extend `GET /api/v1/patients/search` to support: UHID, QR scan, mobile, Aadhaar, ABHA ID, name+DOB
- `[ ]` **[FE]** Build Patient Search page `patients/search/page.tsx`

#### 2.5 Patient Profile Page
- `[ ]` **[FE]** Build patient profile `patients/[id]/page.tsx` with tabs: Demographics | Allergies | Chronic Diseases | Visits | Medications | Documents
- `[ ]` **[FE]** Display live QR code on profile with print button

---

### MODULE 3: OPD / Visit Management (SRS §3.3)

#### 3.1 Refactor Visit Creation DTO
- `[ ]` **[BE]** Create `CreateVisitRequest.java` DTO replacing raw `Map<String, String>`: `patientId`, `doctorId`, `departmentId`, `visitType`, `chiefComplaint`, `priority`, `referredBy`
- `[ ]` **[FE]** Update OPD visit creation form to send all required fields

#### 3.2 Token & Priority Queue (SRS §3.3.2)
- `[ ]` **[BE]** Implement priority queue: Emergency/VIP/Senior Citizen patients prioritized (not just `maxToken + 1`)
- `[ ]` **[BE]** Create `GET /api/v1/visits/queue/{doctorId}` for real-time queue status
- `[ ]` **[BE]** Add WebSocket / SSE for live queue updates
- `[ ]` **[FE]** Build live Queue Display board `opd/queue/page.tsx` showing current + next 5 tokens per doctor
- `[ ]` **[FE]** Build Doctor-facing queue panel with "Call Next Patient" button

#### 3.3 Department Linking
- `[ ]` **[BE]** Link `Visit` entity to `Department`; add `departmentId` FK column
- `[ ]` **[FE]** Add Department searchable dropdown to visit creation form

---

## 🟠 PRIORITY 2 — CORE CLINICAL MODULES

### MODULE 4: Clinical Notes & Doctor Assessment (SRS §3.5–3.7)

#### 4.1 Structured Clinical Notes
- `[ ]` **[BE]** Extend `ClinicalNote.java` to add: `reviewOfSystems`, `socialHistory`, `familyHistory`, SOAP sections
- `[ ]` **[BE]** Add `diagnosisEntries` collection (ICD-coded, type: Provisional/Confirmed/Rule-out)
- `[ ]` **[BE]** Implement note signing/locking: signed notes become immutable (addendum only)
- `[ ]` **[BE]** Implement note versioning (all versions preserved)
- `[ ]` **[FE]** Build full Clinical Notes editor with all sections + digital "Sign & Lock" button

#### 4.2 ICD-10 Diagnosis Search (SRS §3.5.3)
- `[ ]` **[DB]** Import complete ICD-10 code database via Flyway seed migration
- `[ ]` **[BE]** Create `GET /api/v1/icd/search?q=` with full-text search (`tsvector`)
- `[ ]` **[BE]** Support multiple diagnoses per visit (primary + up to 10 secondary with type)
- `[ ]` **[FE]** Build ICD-10 auto-suggest input component
- `[ ]` **[FE]** Add "Favorite Diagnoses" quick-add per doctor

#### 4.3 SOAP Note Auto-Generation (SRS §3.7)
- `[ ]` **[BE]** Create `SoapNoteService.java` aggregating: vitals, lab results, imaging, diagnoses, prescriptions
- `[ ]` **[BE]** Create `POST /api/v1/visits/{id}/soap/generate` endpoint
- `[ ]` **[FE]** Add "Generate SOAP" button that fetches draft and populates the form

---

### MODULE 5: Laboratory Information System / LIS (SRS §4.2)

#### 5.1 Lab Order Management
- `[ ]` **[BE]** Create proper `LabOrder` entity: `patient`, `visit`, `orderedBy`, `priority` (Stat/Urgent/Routine)
- `[ ]` **[BE]** Implement Order Sets (Fever Panel, Liver Panel, etc.)
- `[ ]` **[DB]** Seed lab test catalog: 100+ tests with LOINC codes, sample type, tube color, TAT target, reference ranges (age/gender-specific)

#### 5.2 Barcode & Sample Management
- `[ ]` **[BE]** Generate Code-128 barcodes in `generate-barcodes` endpoint (link sample to order)
- `[ ]` **[BE]** Validate barcode match at sample reception
- `[ ]` **[BE]** Create `POST /api/v1/lab/samples/{id}/reject` with rejection reason
- `[ ]` **[FE]** Add barcode scanner input to Sample Reception tab

#### 5.3 Result Entry & Abnormal Flagging (SRS §4.2.3)
- `[ ]` **[BE]** Auto-flag result values: `Normal`, `Low`, `High`, `Critical Low`, `Critical High` based on age/gender-specific ranges
- `[ ]` **[BE]** Implement Delta Check: compare with previous result, flag significant deviation
- `[ ]` **[BE]** Implement Reflex Testing: auto-order follow-up tests based on rules
- `[ ]` **[FE]** Highlight critical/abnormal values in red/orange + show delta next to each parameter

#### 5.4 Critical Value Notification
- `[ ]` **[BE]** Trigger immediate notification to doctor + nurse on critical result
- `[ ]` **[BE]** Require doctor acknowledgment; track `acknowledgedAt`, `acknowledgedBy`

#### 5.5 TAT Monitoring
- `[ ]` **[BE]** Track all timestamps: `orderedAt`, `collectedAt`, `receivedAt`, `resultEnteredAt`, `authorizedAt`
- `[ ]` **[FE]** Build TAT monitoring dashboard for lab supervisor

---

### MODULE 6: Radiology Information System / RIS (SRS §4.3)

- `[ ]` **[BE]** Add to `RadiologyOrder`: modality, study name, clinical indication, contrast allergy check, pregnancy check, scheduling slot
- `[ ]` **[BE]** Create `POST /api/v1/radiology/studies/{id}/dicom` for DICOM file uploads → store in MinIO/S3
- `[ ]` **[DB]** Seed structured report templates: BI-RADS, PI-RADS, LI-RADS, TI-RADS, Lung-RADS
- `[ ]` **[FE]** Integrate open-source DICOM web viewer (OHIF / Cornerstone.js) into radiology page
- `[ ]` **[FE]** Build template-driven radiology report editor
- `[ ]` **[BE]** Implement critical finding notification to ordering doctor

---

### MODULE 7: Prescription & Clinical Decision Support (SRS §4.4)

- `[ ]` **[DB]** Import drug master: 5000+ drugs with generic, brand, ATC code, forms, strengths, HSN code, GST, schedule, LASA/high-alert flags
- `[ ]` **[BE]** Create `GET /api/v1/drugs/search?q=` for drug name auto-suggest
- `[ ]` **[BE]** Create structured `PrescriptionLine.java` with all required fields (SRS §4.4.1)
- `[ ]` **[BE]** Implement `ClinicalDecisionSupportService.java`: Drug-Allergy, Drug-Drug, Duplicate, Dose Range, Pregnancy, Renal checks
- `[ ]` **[FE]** Show CDS alerts during prescription entry (Hard Stop = blocking, Warning = acknowledgable)
- `[ ]` **[BE]** Create `PrescriptionTemplate` entity + CRUD endpoints
- `[ ]` **[FE]** Add "Save as Template" and "Load Template" on prescription editor

---

### MODULE 8: Billing & Revenue Cycle Management (SRS §5.1)

- `[ ]` **[BE]** Implement event-driven auto charge capture: lab order → bill line; dispensed drug → bill line; room transfer → adjust room charges
- `[ ]` **[BE]** Create `Tariff.java` entity with multi-tariff support (Cash/Insurance/Corporate/Govt/Staff); auto-select based on patient category
- `[ ]` **[BE]** Build full invoice with: gross total, discount, GST, advance paid, insurance, balance due
- `[ ]` **[BE]** Generate print-ready invoice PDF (extend `PdfGenerationService.java`)
- `[ ]` **[BE]** Implement multi-mode payment: Cash, Card, UPI, NEFT, Cheque, Wallet; support split payments
- `[ ]` **[BE]** Create advance deposit management `POST /api/v1/billing/advance`
- `[ ]` **[BE]** Build report endpoints: Daily Collection, Outstanding, Revenue, Discount, Refund, GST
- `[ ]` **[FE]** Build invoice view page `billing/invoices/[id]/page.tsx` with print
- `[ ]` **[FE]** Build financial reports page `billing/reports/page.tsx`

---

## 🟡 PRIORITY 3 — ADMINISTRATIVE & SUPPORT MODULES

### MODULE 9: ADT — Admission, Discharge & Transfer (SRS §3.4, §3.10, §5.3)

- `[ ]` **[BE]** Validate real-time bed availability before admission; implement ICU bed authorization rule
- `[ ]` **[BE]** Capture all required admission fields: `admissionType`, `roomType`, `insurance`, `preAuthNumber`, `consentForTreatment`, `mlcFlag`, `provisionalDiagnosis`
- `[ ]` **[FE]** Build complete admission form `admissions/new/page.tsx` with real-time bed picker
- `[ ]` **[BE]** Implement bed state machine: `Available → Occupied → Discharged-Cleaning → Ready → Available`
- `[ ]` **[BE]** On discharge: auto-set bed to `Discharged-Cleaning`; notify housekeeping
- `[ ]` **[BE]** Create `GET /api/v1/beds/dashboard` with real-time counts per ward/category
- `[ ]` **[FE]** Build real-time Bed Dashboard `admissions/beds/page.tsx` with color-coded floor-plan
- `[ ]` **[BE]** Auto-generate discharge summary from: demographics, diagnoses, treatment, labs, imaging, discharge Rx, follow-up
- `[ ]` **[BE]** Generate Discharge Package (PDF bundle): summary, prescription, labs, billing
- `[ ]` **[BE]** Implement DAMA/LAMA workflows with mandatory form generation
- `[ ]` **[FE]** Build discharge page `discharge/[admissionId]/page.tsx`
- `[ ]` **[BE]** Create `BedTransfer` entity: log from/to bed, reason, authorized by; auto-adjust billing

---

### MODULE 10: Nursing Module (SRS §4.5)

- `[ ]` **[BE]** Extend vitals entity: 15+ parameters, `recordedBy` (nurse ID), auto-calculate NEWS2, trigger escalation if NEWS ≥ 5
- `[ ]` **[FE]** Build vitals charting panel with trend graphs `nursing/vitals/page.tsx`
- `[ ]` **[BE]** Create `MedicationAdministrationRecord.java`: drug, scheduled time, actual time, status, barcode-verified field
- `[ ]` **[BE]** Implement 5-Rights barcode check at medication administration
- `[ ]` **[FE]** Build MAR view for nurses
- `[ ]` **[BE]** Create nursing assessment entities: Morse Fall Scale, Braden Scale, MST, Pain, Wound Assessment
- `[ ]` **[BE]** Create `ShiftHandover.java` in SBAR format; require acknowledgment by receiving nurse
- `[ ]` **[BE]** Create `IncidentReport.java` with type, severity, description
- `[ ]` **[FE]** Build incident reporting form `nursing/incidents/page.tsx`

---

### MODULE 11: Pharmacy Module (SRS §5.4)

- `[ ]` **[BE]** Extend drug/stock with: batch, expiry, buying price, selling price, GST, schedule, narcotic flag, LASA flag
- `[ ]` **[BE]** Implement FEFO (First Expiry First Out) on dispensing
- `[ ]` **[BE]** Implement 3/6-month expiry alert scheduled job
- `[ ]` **[BE]** Implement auto-reorder indent when stock hits reorder level
- `[ ]` **[BE]** Create `NarcoticRegisterEntry.java`: drug, quantity, batch, dispensed to/by, witness, running balance
- `[ ]` **[BE]** Require double-verification for narcotics (two authorized sign-offs)
- `[ ]` **[FE]** Build Narcotic Register page `pharmacy/narcotic-register/page.tsx`
- `[ ]` **[BE]** Create prescription queue for pharmacists; implement auto-billing on dispense
- `[ ]` **[FE]** Build pharmacy dispensing workflow page

---

### MODULE 12: Insurance & TPA Management (SRS §5.2)

- `[ ]` **[BE]** Create `InsuranceClaim.java` with full claim lifecycle data model
- `[ ]` **[BE]** Implement pre-authorization workflow `POST /api/v1/insurance/pre-auth`
- `[ ]` **[BE]** Track claim status lifecycle: Submitted → Under Review → Approved/Queried/Rejected → Settlement
- `[ ]` **[BE]** Implement PMJAY ABHA ID linking + HBP package mapping
- `[ ]` **[BE]** Auto-generate claim documents checklist
- `[ ]` **[FE]** Build Insurance & TPA page `billing/insurance/page.tsx`

---

### MODULE 13: Inventory Management (SRS §6)

- `[ ]` **[BE]** Create comprehensive `InventoryItem.java` with: auto-generated item code, category, UOM, HSN, GST, reorder level, lead time, ABC/VED classification
- `[ ]` **[BE]** Create Item Master CRUD endpoints
- `[ ]` **[BE]** Create Purchase Indent workflow: Draft → Pending Approval → Approved → Converted to PO
- `[ ]` **[BE]** Create `PurchaseOrder.java` and `GoodsReceivedNote.java` entities + endpoints
- `[ ]` **[BE]** Implement inter-store transfer, return management, wastage logging
- `[ ]` **[BE]** Implement physical stock audit / reconciliation
- `[ ]` **[FE]** Build Inventory procurement workflow pages (Indent, PO, GRN) and stock dashboard

---

## 🔵 PRIORITY 4 — AI FEATURES

### MODULE 14: AI Voice-to-Clinical Notes (SRS §3.6 & §8.2.1)

- `[ ]` **[AI-SVC]** Set up `ai-service` with FastAPI
- `[ ]` **[AI-SVC]** Integrate Whisper (or self-hosted) for audio → transcript (`POST /ai/voice/transcribe`)
- `[ ]` **[AI-SVC]** Clinical NLP pipeline: NER for symptoms, diagnoses, medications, vitals from transcript
- `[ ]` **[AI-SVC]** LLM medical document generator from extracted entities with confidence scores
- `[ ]` **[BE]** Create `POST /api/v1/ai/voice-to-notes` proxy endpoint
- `[ ]` **[BE]** Store original audio (encrypted) linked to clinical note
- `[ ]` **[FE]** Build voice recorder UI in clinical notes editor: record → processing → draft → approve/edit → sign

### MODULE 15: AI ICD Coding (SRS §8.2.3)

- `[ ]` **[AI-SVC]** Implement semantic ICD-10 search: embedding-based similarity search
- `[ ]` **[BE]** Create `POST /api/v1/ai/icd-suggest` endpoint returning ranked ICD codes + confidence
- `[ ]` **[FE]** Integrate ICD suggestion into diagnosis entry with confidence scores

### MODULE 16: AI Drug Interaction Engine (SRS §8.2.4)

- `[ ]` **[DB]** Import drug interaction database (DrugBank or equivalent)
- `[ ]` **[BE]** Implement `DrugInteractionService.java`: Drug-Drug, Drug-Allergy, Drug-Disease, Drug-Pregnancy, Dose Range, Duplicate
- `[ ]` **[FE]** Show CDS alerts in real time during prescription entry

### MODULE 17: AI Risk Prediction (SRS §8.3.2)

- `[ ]` **[AI-SVC]** Sepsis risk model: inputs = vitals + lab values; output = probability + risk tier
- `[ ]` **[AI-SVC]** 30-day Readmission risk model
- `[ ]` **[BE]** Run sepsis risk on every new vital/lab; push alert when threshold crossed
- `[ ]` **[FE]** Display risk scores on patient summary page

---

## ⚪ PRIORITY 5 — OPERATIONS, ANALYTICS & PLATFORM

### MODULE 18: Operations Management (SRS §7)

- `[ ]` **[BE]** Housekeeping: bed turnaround notification; task assignment; checklist tracking
- `[ ]` **[BE]** Maintenance: work order lifecycle (Reported → Assigned → In-Progress → Resolved); preventive maintenance schedule
- `[ ]` **[BE]** Patient Transport: porter request + ambulance dispatch with GPS
- `[ ]` **[BE]** Dietary: diet order → kitchen notification; allergy-aware meal planning
- `[ ]` **[BE]** Biomedical Waste: waste logs, segregation compliance
- `[ ]` **[BE]** Help Desk: service request ticketing with SLA + escalation
- `[ ]` **[FE]** Build operations dashboard and module pages

### MODULE 19: Doctor & Staff Management (SRS §12)

- `[ ]` **[BE]** Create `DoctorProfile.java`: hierarchy type, specialization, registration number, qualifications, privileges, OPD schedule
- `[ ]` **[BE]** Implement `InternRotation` entity for CRRI schedule
- `[ ]` **[BE]** Implement on-call roster and leave management with approval workflow
- `[ ]` **[FE]** Build doctor profile `staff/doctors/[id]/page.tsx` and scheduling/roster page

### MODULE 20: Notification Engine (SRS §5.7)

- `[ ]` **[BE]** Create `NotificationService.java`: In-App, SMS, WhatsApp, Email channels
- `[ ]` **[BE]** Implement escalation matrix: L1 (0 min) → L2 (15 min) → L3 (30 min) → L4 (60 min)
- `[ ]` **[BE]** Integrate SMS gateway (Twilio / AWS SNS)
- `[ ]` **[FE]** Build notification center in frontend header with unread badge

### MODULE 21: Audit Trail (SRS §5.8)

- `[ ]` **[BE]** Create `AuditLog.java` with all required fields: timestamp, user, role, action, module, entity, before/after, IP, device, session ID
- `[ ]` **[BE]** Implement `AuditAspect.java` via Spring AOP to auto-log all CREATE/UPDATE/DELETE
- `[ ]` **[BE]** Generate reports: User Activity, Patient Record Access, Break-the-Glass, Failed Login
- `[ ]` **[FE]** Build audit log viewer `admin/audit-logs/page.tsx` (admin only)

### MODULE 22: Analytics & Dashboards

- `[ ]` **[BE]** Build analytics endpoints: hospital KPIs, clinical quality, financial summary, operational KPIs
- `[ ]` **[FE]** Build Executive dashboard `analytics/page.tsx` with charts + KPI cards
- `[ ]` **[FE]** Build Doctor dashboard showing workload + pending actions
- `[ ]` **[FE]** Build LIS dashboard with TAT metrics

### MODULE 23: ABDM / FHIR Integration (SRS §10)

- `[ ]` **[BE]** Complete FHIR Patient resource in `FhirPatientController.java`
- `[ ]` **[BE]** ABHA ID linking and verification via ABDM APIs
- `[ ]` **[BE]** Health record sharing via FHIR document API
- `[ ]` **[BE]** PMJAY pre-authorization via NHA portal APIs

---

## 🗄️ DATABASE — MISSING TABLES (~147+ tables needed via Flyway Migrations)

- `[ ]` `departments` — department master
- `[ ]` `doctor_profiles` — full doctor profile
- `[ ]` `intern_rotations` — CRRI schedule
- `[ ]` `on_call_rosters` — duty schedules
- `[ ]` `allergy_records` — patient allergies (Allergen, Category, Severity, Status)
- `[ ]` `chronic_diseases` — chronic disease registry
- `[ ]` `family_history` — structured family medical history
- `[ ]` `immunization_records` — vaccination records
- `[ ]` `medication_history` — all-time medication timeline
- `[ ]` `surgery_history` — patient surgical history
- `[ ]` `tariff_master` + `tariff_items` — pricing rules by category
- `[ ]` `bill_charges` — itemized bill lines
- `[ ]` `payments` — payment transactions with mode
- `[ ]` `advance_deposits` — advance payment tracking
- `[ ]` `insurance_claims` — claim lifecycle
- `[ ]` `tpa_master` + `insurance_details` — insurer + patient policy
- `[ ]` `icd10_codes` — full ICD-10 database (~70,000 codes)
- `[ ]` `drug_master` — comprehensive drug database
- `[ ]` `drug_interactions` — interaction pairs and severity
- `[ ]` `prescription_lines` — individual medication lines
- `[ ]` `narcotic_register` — controlled substance log
- `[ ]` `medication_administration_records` — nurse MAR
- `[ ]` `nursing_assessments` — fall risk, Braden, pain, wound
- `[ ]` `shift_handovers` — SBAR handover records
- `[ ]` `incidents` — incident/adverse event reports
- `[ ]` `lab_reference_ranges` — age/gender-specific ranges per test parameter
- `[ ]` `lab_critical_values` — configurable critical thresholds
- `[ ]` `lab_tat_tracking` — TAT per workflow stage
- `[ ]` `radiology_slots` — scheduling slots per modality
- `[ ]` `dicom_studies` — DICOM study metadata
- `[ ]` `radiology_templates` — structured report templates
- `[ ]` `icu_scoring` — APACHE II, SOFA, qSOFA scores
- `[ ]` `icu_checklists` — daily ICU bundle
- `[ ]` `ot_bookings` — surgery schedule
- `[ ]` `ot_checklists` — WHO safety checklist
- `[ ]` `anesthesia_records` — intra-op chart
- `[ ]` `implant_tracking` — implant traceability
- `[ ]` `infection_control_events` — HAI, CLABSI, CAUTI, VAP
- `[ ]` `blood_bank_inventory` + `transfusion_records`
- `[ ]` `diet_orders` + `diet_charts`
- `[ ]` `inventory_items` — item master
- `[ ]` `purchase_indents` + `purchase_orders` + `grn_records`
- `[ ]` `stock_ledger` — every stock movement
- `[ ]` `vendors` — vendor master
- `[ ]` `housekeeping_tasks` + `work_orders` + `transport_requests`
- `[ ]` `ambulances` — fleet master
- `[ ]` `notification_log` — all notifications sent
- `[ ]` `audit_logs` — immutable audit trail
- `[ ]` `break_the_glass_log` — emergency access audit
- `[ ]` `document_repository` — document metadata
- `[ ]` `ai_session_logs` — AI suggestions + doctor decisions

---

## 🔧 INFRASTRUCTURE & PLATFORM

- `[ ]` Enable Flyway and create versioned SQL migration files for all schema changes
- `[ ]` Add Redis for: session management, caching (queue state, bed status), notifications
- `[ ]` Set up WebSocket / SSE for: live queue display, bed dashboard, critical alerts
- `[ ]` Configure MinIO or AWS S3 for: DICOM images, documents, audio files
- `[ ]` Add global API pagination: all list endpoints return `Page<T>` with `page`, `size`, `totalElements`
- `[ ]` Add `@Cacheable` for: ICD-10 lookups, drug master, tariff data
- `[ ]` Configure Docker Compose: Spring Boot, PostgreSQL, Redis, MinIO, AI service (FastAPI), frontend
- `[ ]` Implement AES-256 encryption for sensitive fields: Aadhaar, ABHA, financial data
- `[ ]` Enable HTTPS/TLS in all environments
- `[ ]` Automated DB backup: hourly snapshots, daily full, 30-day retention

---

*Total estimated tasks: ~280 items across Backend, Frontend, Database, AI-Service, and Infrastructure.*  
*Completion % per priority: P1 (Security/Core) = 8%, P2 (Clinical) = 12%, P3 (Admin) = 15%, P4 (AI) = 2%, P5 (Ops/Platform) = 5%*
