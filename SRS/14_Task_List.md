# MedCore HIS — Master Task List

**Version:** 1.0 | **Date:** 2026-06-21 | **Total Tasks:** 500+

---

> This is a living document. Mark tasks as `[x]` when completed, `[/]` when in progress.

---

# PHASE 1 — Foundation + Core Clinical (Months 1–6)

## Sprint 0: Project Setup (Weeks 1–2)

### Infrastructure & DevOps
- [ ] Create Git repository with mono-repo structure (backend + frontend + mobile + docs)
- [ ] Define branching strategy (main → develop → feature/*, release/*, hotfix/*)
- [ ] Setup CI/CD pipeline (build → lint → test → deploy)
- [ ] Create Docker-compose for local development (PostgreSQL, Redis, Elasticsearch, MinIO)
- [ ] Setup Dev, QA, Staging environments
- [ ] Configure monitoring stack (Prometheus + Grafana)
- [ ] Configure centralized logging (ELK Stack)
- [ ] Setup SonarQube for code quality gates

### Backend Foundation
- [ ] Initialize Spring Boot 3.3 project with multi-module structure
- [ ] Configure PostgreSQL connection + Flyway migrations
- [ ] Configure Redis for caching + sessions
- [ ] Configure Elasticsearch client
- [ ] Setup global exception handling + error response format (RFC 7807)
- [ ] Setup request/response logging middleware
- [ ] Setup correlation ID for distributed tracing
- [ ] Configure API versioning (/api/v1/*)
- [ ] Setup OpenAPI/Swagger documentation
- [ ] Setup base entity classes (audit fields: created_by, created_at, updated_by, updated_at)
- [ ] Setup soft-delete framework
- [ ] Setup pagination/sorting/filtering utilities

### Frontend Foundation
- [ ] Initialize Next.js 14 project with App Router
- [ ] Setup light mode design system (CSS variables)
  - [ ] Color tokens (primary, surface, text, status colors)
  - [ ] Typography scale (Inter font, size/weight hierarchy)
  - [ ] Spacing scale (4px base)
  - [ ] Shadow scale
  - [ ] Border radius scale
- [ ] Create base layout components
  - [ ] Sidebar navigation
  - [ ] Top header bar
  - [ ] Page wrapper with breadcrumbs
  - [ ] Responsive breakpoints
- [ ] Create reusable UI components
  - [ ] Button (primary, secondary, danger, ghost)
  - [ ] Input (text, number, phone, email)
  - [ ] Select / Dropdown
  - [ ] Searchable Select (typeahead)
  - [ ] Date picker
  - [ ] Time picker
  - [ ] Modal / Dialog
  - [ ] Toast notifications
  - [ ] Data table with sorting/filtering/pagination
  - [ ] Card component
  - [ ] Tabs component
  - [ ] Badge / Tag
  - [ ] Alert / Banner
  - [ ] Loading skeleton
  - [ ] Empty state
  - [ ] Form validation display
- [ ] Setup API client (Axios) with JWT interceptor
- [ ] Setup global state management (Zustand)
- [ ] Setup route guards (auth check, role check)

### Database Design (Core)
- [ ] Design patient schema (patients, patient_contacts, patient_allergies, patient_documents)
- [ ] Design auth schema (users, roles, permissions, role_permissions, user_roles, sessions, audit_logs)
- [ ] Design master schema (departments, wards, beds, bed_status_history)
- [ ] Design doctor schema (doctors, doctor_qualifications, doctor_privileges, doctor_schedules)
- [ ] Design clinical schema (visits, admissions, clinical_notes, diagnoses, prescriptions, prescription_lines)
- [ ] Design billing schema (tariffs, invoices, invoice_lines, payments, advances)
- [ ] Write Flyway migration scripts V1 through V10
- [ ] Seed master data (Indian states, countries, blood groups, gender, etc.)
- [ ] Import ICD-10 database (~70,000 codes) into PostgreSQL + Elasticsearch index
- [ ] Import drug database (~50,000 drugs) into PostgreSQL + Elasticsearch index

### UI/UX Design
- [ ] Design login screen (Figma)
- [ ] Design main dashboard layout
- [ ] Design patient registration form
- [ ] Design doctor workspace
- [ ] Design clinical note screens
- [ ] Design prescription screen
- [ ] Create icon set for modules
- [ ] Create color-coded status indicators
- [ ] Design light mode style guide document

---

## Sprints 1–2: Authentication & Core Infrastructure (Weeks 3–6)

### Backend — Auth Service
- [ ] POST /api/v1/auth/login — Username/password authentication
- [ ] POST /api/v1/auth/refresh — Refresh JWT token
- [ ] POST /api/v1/auth/logout — Invalidate session
- [ ] POST /api/v1/auth/forgot-password — Send password reset email/SMS
- [ ] POST /api/v1/auth/reset-password — Reset password with token
- [ ] POST /api/v1/auth/change-password — Authenticated password change
- [ ] POST /api/v1/auth/mfa/setup — Enable MFA
- [ ] POST /api/v1/auth/mfa/verify — Verify MFA token
- [ ] Implement JWT generation (access: 15min, refresh: 7 days)
- [ ] Implement Redis session storage
- [ ] Implement concurrent session limit (max 2)
- [ ] Implement session timeout based on role
- [ ] Implement failed login lockout (5 attempts → 30 min)
- [ ] Implement password policy enforcement

### Backend — RBAC Engine
- [ ] POST /api/v1/roles — Create role
- [ ] GET /api/v1/roles — List roles with permissions
- [ ] PUT /api/v1/roles/{id} — Update role permissions
- [ ] DELETE /api/v1/roles/{id} — Deactivate role
- [ ] GET /api/v1/permissions — List all permission categories
- [ ] Implement permission middleware (Spring Security method-level)
- [ ] Implement department-scoped access (doctor sees only their department patients)
- [ ] Seed default roles (Super Admin, Hospital Admin, Doctor, Nurse, Lab Tech, etc.)
- [ ] Seed default permission sets per role

### Backend — User Management
- [ ] POST /api/v1/users — Create user with role assignment
- [ ] GET /api/v1/users — List/search users with filters (role, department, status)
- [ ] GET /api/v1/users/{id} — Get user details
- [ ] PUT /api/v1/users/{id} — Update user profile
- [ ] PUT /api/v1/users/{id}/status — Activate/deactivate user
- [ ] PUT /api/v1/users/{id}/roles — Update user roles
- [ ] POST /api/v1/users/{id}/reset-password — Admin reset password

### Backend — Audit Service
- [ ] Implement AOP-based audit interceptor for all CUD operations
- [ ] Log: timestamp, user, action, module, entity, before/after values, IP, device
- [ ] GET /api/v1/audit-logs — Search audit logs with filters
- [ ] Implement immutable audit log storage (append-only)

### Frontend — Auth Screens
- [ ] Login page (username + password form, light theme)
- [ ] MFA verification step
- [ ] Forgot password page
- [ ] Reset password page
- [ ] Password change dialog

### Frontend — User Management
- [ ] User list page (data table with search, role filter, status filter)
- [ ] Create user modal/page (all profile fields + role assignment)
- [ ] Edit user modal/page
- [ ] User detail view (profile + assigned roles + activity log)
- [ ] Role management page (list roles, create role, permission matrix checkbox grid)

### Testing
- [ ] Unit tests: Auth service (login, token generation, refresh, lockout)
- [ ] Unit tests: RBAC (permission check, role assignment)
- [ ] Unit tests: User CRUD
- [ ] Integration tests: Auth flow end-to-end
- [ ] Integration tests: RBAC permission enforcement

---

## Sprints 3–4: Patient Registration & QR System (Weeks 7–10)

### Backend — Patient Service
- [ ] POST /api/v1/patients — Register new patient (full data model)
- [ ] GET /api/v1/patients/{uhid} — Get patient by UHID
- [ ] GET /api/v1/patients/search — Search (UHID, name, mobile, Aadhaar, DOB)
- [ ] PUT /api/v1/patients/{uhid} — Update demographics
- [ ] GET /api/v1/patients/{uhid}/qr — Generate/retrieve QR code
- [ ] POST /api/v1/patients/{uhid}/photo — Upload patient photo (webcam)
- [ ] GET /api/v1/patients/{uhid}/timeline — Patient event timeline
- [x] Implement UHID auto-generation (HOS-YYYY-NNNNNNN format)
- [ ] Implement QR code generation (ZXing, JSON payload with UHID, name, DOB, blood group, allergy flag)
- [x] Implement duplicate detection (fuzzy matching on name + DOB + mobile)
- [ ] GET /api/v1/patients/duplicates — Get potential duplicate list for review
- [ ] POST /api/v1/patients/merge — Merge duplicate records (admin only)
- [ ] Implement QR wristband print (Zebra ZPL thermal print format)
- [ ] Implement registration slip PDF generation

### Backend — Allergy Service
- [x] POST /api/v1/patients/{uhid}/allergies — Add allergy
- [x] GET /api/v1/patients/{uhid}/allergies — List allergies
- [ ] PUT /api/v1/patients/{uhid}/allergies/{id} — Update allergy status
- [ ] Implement allergy banner data (for display on every clinical screen)

### Frontend — Patient Screens
- [x] Patient search page (search bar, QR scan button, results table with quick actions)
- [x] New patient registration form (multi-step: personal → contact → emergency → insurance → photo)
- [ ] Registration form validation (required fields, format checks, Aadhaar Verhoeff)
- [x] Photo capture component (webcam integration)
- [x] Patient profile dashboard (overview of demographics, active alerts, vitals)
- [x] Patient QR code generation + printable PDF for physical ID cards
- [x] Patient profile page (demographics card, QR display, edit button, allergy section, visit history)
- [x] Allergy entry form (allergen search, severity, reaction type)
- [x] Allergy banner component (red banner on all clinical screens)
- [ ] Duplicate review modal (side-by-side comparison, merge option)
- [x] QR wristband print button + preview
- [x] Registration slip print/PDF

### Testing
- [ ] Unit tests: UHID generation, QR generation, duplicate detection
- [ ] Integration tests: Full registration flow
- [ ] Integration tests: QR scan → patient lookup
- [ ] E2E test: Register → Print QR → Scan QR → View profile

---

## Sprints 5–6: Department, Doctor & OPD (Weeks 11–14)

### Backend — Department Service
- [ ] POST /api/v1/departments — Create department
- [ ] GET /api/v1/departments — List departments (tree hierarchy)
- [ ] PUT /api/v1/departments/{id} — Update department
- [ ] GET /api/v1/departments/{id}/doctors — Doctors in department

### Backend — Doctor Service
- [ ] POST /api/v1/doctors — Create doctor profile (full model)
- [ ] GET /api/v1/doctors — List/search doctors (department, type, status)
- [ ] GET /api/v1/doctors/{id} — Get doctor full profile
- [ ] PUT /api/v1/doctors/{id} — Update profile
- [ ] PUT /api/v1/doctors/{id}/schedule — Set OPD schedule (days, timings, slot count)
- [ ] PUT /api/v1/doctors/{id}/privileges — Assign clinical privileges
- [ ] GET /api/v1/doctors/{id}/availability — Check availability for date
- [ ] PUT /api/v1/doctors/{id}/status — Update status (active/leave/suspended)

### Backend — OPD Visit Service
- [ ] POST /api/v1/visits — Create OPD visit (patient, department, doctor, complaint)
- [ ] GET /api/v1/visits — List visits with filters (date, doctor, department, status)
- [ ] GET /api/v1/visits/{id} — Visit details
- [ ] PUT /api/v1/visits/{id}/status — Update status (waiting → in-consultation → completed)
- [ ] Implement token/queue system (auto-increment per doctor per day)
- [ ] GET /api/v1/doctors/{id}/queue — Current queue for doctor
- [ ] Implement estimated wait time calculation
- [ ] Implement priority queue (emergency, VIP, senior citizen)

### Frontend — Department & Doctor Screens
- [ ] Department list page (tree view, create/edit modal)
- [ ] Doctor list page (data table with type, department, status filters)
- [ ] Doctor create/edit form (multi-tab: personal, qualifications, schedule, privileges, financial)
- [ ] Doctor profile view (full profile with all tabs)
- [ ] Doctor schedule configuration (day picker, time slots, max patients)

### Frontend — OPD Screens
- [ ] OPD visit creation form (patient search → select dept → select doctor → complaint → create)
- [ ] OPD queue display (current token, waiting list, estimated wait time)
- [ ] Doctor's patient queue panel (next patient, call button, skip, complete)
- [ ] Doctor dashboard (today's patients: OPD list + IPD list, pending reviews)
- [ ] OPD visit list (searchable, filterable by date/doctor/department)

### Testing
- [ ] Unit tests: Token generation, queue management, wait time calculation
- [ ] Integration tests: Create visit → queue → consult → complete
- [ ] Load test: 100 concurrent OPD visits

---

## Sprints 7–8: Doctor Clinical Workflow (Weeks 15–18)

### Backend — Clinical Notes Service
- [ ] POST /api/v1/clinical-notes — Create clinical note
- [ ] GET /api/v1/clinical-notes/{id} — Get note
- [ ] PUT /api/v1/clinical-notes/{id} — Update note (draft state only)
- [ ] POST /api/v1/clinical-notes/{id}/sign — Sign/finalize note (locks it)
- [ ] POST /api/v1/clinical-notes/{id}/addendum — Add addendum to signed note
- [ ] GET /api/v1/patients/{uhid}/clinical-notes — All notes for patient (timeline)

### Backend — Diagnosis Service
- [ ] GET /api/v1/icd/search — Search ICD-10 codes (Elasticsearch, fuzzy matching)
- [ ] POST /api/v1/patients/{uhid}/diagnoses — Add diagnosis
- [ ] GET /api/v1/patients/{uhid}/diagnoses — List diagnoses (active + historical)
- [ ] PUT /api/v1/patients/{uhid}/diagnoses/{id} — Update diagnosis (type, status)
- [ ] GET /api/v1/doctors/{id}/favorite-diagnoses — Favorite diagnosis sets
- [ ] POST /api/v1/doctors/{id}/favorite-diagnoses — Save favorite

### Backend — Patient Summary Service
- [ ] GET /api/v1/patients/{uhid}/summary — Aggregated patient summary
  - [ ] Demographics banner
  - [ ] Allergy alerts
  - [ ] Chronic conditions
  - [ ] Active medications
  - [ ] Recent vitals
  - [ ] Recent lab results
  - [ ] Recent visits
  - [ ] Pending orders

### Frontend — Clinical Screens
- [ ] Patient summary dashboard (360° view on QR scan / patient open)
  - [ ] Demographics banner with photo, age, gender, blood group
  - [ ] Allergy alert strip (red, prominent)
  - [ ] Quick-action buttons (new note, order lab, prescribe, refer)
  - [ ] Tabs: Current Visit | History | Labs | Imaging | Prescriptions | Notes
- [ ] Clinical note entry form
  - [ ] Tabbed: Chief Complaint | HPI | Past History | Examination | ROS | Assessment | Plan
  - [ ] Auto-save on field change
  - [ ] Rich text support for narrative sections
  - [ ] Body diagram component (for examination findings — clickable body parts)
- [ ] Diagnosis entry component
  - [ ] ICD-10 typeahead search (type → suggestions appear)
  - [ ] Add multiple diagnoses (primary + secondary)
  - [ ] Set type (Confirmed / Provisional / Differential / Rule-out)
  - [ ] Favorite diagnosis quick-add
- [ ] Clinical note timeline view
  - [ ] Chronological list with date, doctor, department, type
  - [ ] Expand to view full note
  - [ ] Filter by date range, department, doctor
- [ ] Sign/lock note dialog (confirm → digital signature timestamp)

### Testing
- [ ] Unit tests: Clinical note CRUD, ICD search ranking
- [ ] Integration tests: Full clinical documentation flow
- [ ] E2E: QR scan → patient summary → create note → diagnose → sign

---

## Sprints 9–10: Prescription & Drug Management (Weeks 19–22)

### Backend — Drug Service
- [ ] GET /api/v1/drugs/search — Search drugs (generic + brand, Elasticsearch)
- [ ] GET /api/v1/drugs/{id} — Drug details (interactions, contraindications)
- [ ] GET /api/v1/drugs/formulary — Hospital formulary list
- [ ] Implement drug-drug interaction checking (database of ~10,000 interaction pairs)
- [ ] Implement drug-allergy cross-reference (including cross-reactivity)
- [ ] Implement duplicate therapy detection (same drug class)
- [ ] Implement dose range validation (age/weight-based)
- [ ] Implement pregnancy category checking

### Backend — Prescription Service
- [ ] POST /api/v1/prescriptions — Create prescription with drug lines
- [ ] GET /api/v1/prescriptions/{id} — Get prescription
- [ ] PUT /api/v1/prescriptions/{id} — Update (draft only)
- [ ] POST /api/v1/prescriptions/interaction-check — Check interactions before saving
- [ ] GET /api/v1/patients/{uhid}/prescriptions — Prescription history
- [ ] GET /api/v1/patients/{uhid}/active-medications — Current active medications
- [ ] GET /api/v1/doctors/{id}/prescription-templates — Favorite templates
- [ ] POST /api/v1/doctors/{id}/prescription-templates — Save template
- [ ] GET /api/v1/prescriptions/{id}/pdf — Generate prescription PDF

### Frontend — Prescription Screens
- [ ] Prescription entry screen
  - [ ] Drug search typeahead (generic → brand suggestions)
  - [ ] Drug line form (strength, dose, form, route, frequency, timing, duration, instructions)
  - [ ] Auto-calculate quantity (frequency × duration)
  - [ ] Interaction warning inline (red/yellow badges on drug lines)
  - [ ] Template selector (load saved templates)
  - [ ] Save as template button
- [ ] Drug interaction alert modal (severity, description, override with justification)
- [ ] Allergy alert modal (hard stop for allergic drugs)
- [ ] Prescription preview/print (formatted PDF)
- [ ] Prescription history view (all Rx for patient, current vs past)
- [ ] Active medications summary component (for patient dashboard)

### Testing
- [ ] Unit tests: Drug interaction engine, allergy cross-reference, dose validation
- [ ] Integration tests: Create prescription → interaction check → save → PDF
- [ ] E2E: Full OPD flow with prescription

---

## Sprint 11: Basic Admission & Bed Management (Weeks 23–24)

### Backend
- [ ] POST /api/v1/wards — Create ward with bed configuration
- [ ] GET /api/v1/wards — List wards
- [ ] POST /api/v1/beds — Create bed
- [ ] GET /api/v1/beds/dashboard — Real-time bed status dashboard data
- [ ] PUT /api/v1/beds/{id}/status — Update bed status (available/occupied/cleaning/maintenance)
- [ ] POST /api/v1/admissions — Create admission (patient, dept, ward, bed, doctor, diagnosis)
- [ ] GET /api/v1/admissions — Active admissions list
- [ ] GET /api/v1/admissions/{id} — Admission details
- [ ] PUT /api/v1/admissions/{id}/transfer — Bed/ward transfer
- [ ] Implement admission number auto-generation
- [ ] Implement bed status state machine (available → occupied → discharged → cleaning → available)
- [ ] Implement WebSocket for real-time bed status updates

### Frontend
- [ ] Ward & bed configuration screen (create ward, add beds, set categories)
- [ ] Admission form (patient → department → ward type → available beds → doctor → diagnosis → advance)
- [ ] Bed dashboard (visual bed map with color coding, summary cards, filters)
- [ ] Admission list (active admissions, search, filters)
- [ ] Bed transfer dialog (select new bed, enter reason)

### Testing
- [ ] Unit tests: Bed state machine, admission creation
- [ ] Integration tests: Admit → allocate bed → transfer → release bed
- [ ] WebSocket test: Bed status real-time updates

---

## Sprint 12: Basic Billing & Phase 1 Wrap-Up (Weeks 25–26)

### Backend
- [ ] POST /api/v1/tariffs — Create/update service tariffs
- [ ] GET /api/v1/tariffs — List tariffs by category
- [ ] POST /api/v1/invoices — Create invoice
- [ ] POST /api/v1/invoices/{id}/lines — Add line items
- [ ] GET /api/v1/invoices/{id} — Get invoice with lines
- [ ] POST /api/v1/invoices/{id}/discount — Apply discount
- [ ] POST /api/v1/payments — Record payment (cash/card/UPI)
- [ ] GET /api/v1/payments/receipt/{id}/pdf — Generate receipt PDF
- [ ] POST /api/v1/advances — Collect advance deposit
- [ ] GET /api/v1/patients/{uhid}/billing — Billing summary

### Frontend
- [ ] Tariff management screen (service list, prices, categories)
- [ ] OPD billing screen (auto-populated charges, payment collection)
- [ ] Basic IPD billing screen (room charges, consultation, interim bill)
- [ ] Payment collection dialog (amount, mode selection, reference number)
- [ ] Receipt print/PDF
- [ ] Billing summary on patient profile

### Phase 1 Finalization
- [ ] Full integration testing (Register → OPD → Consult → Diagnose → Prescribe → Bill → Pay)
- [ ] Full integration testing (Register → Admit → Bed → Consult → Notes → Interim Bill)
- [ ] Performance testing (500 concurrent users)
- [ ] Security audit (basic OWASP checks)
- [ ] Bug fixes from testing
- [ ] User training materials (quick reference cards per role)
- [ ] Conduct user training sessions
- [ ] Pilot department go-live
- [ ] Monitor and stabilize (1 week)
- [ ] Full Phase 1 go-live

---

# PHASE 2 — Full Clinical Expansion (Months 7–12)

## Laboratory Information System
- [ ] Design lab schema (lab_sections, test_catalog, test_panels, test_parameters, reference_ranges, lab_orders, lab_samples, lab_results, lab_qc)
- [ ] Import test catalog with reference ranges
- [ ] Lab order API (create, cancel, view by patient/doctor/date)
- [ ] Order set management API
- [ ] Sample collection API (barcode generation, collection timestamp)
- [ ] Sample reception API (accept/reject with reason)
- [ ] Result entry API (manual + auto from analyzer)
- [ ] Abnormal flagging engine (compare with age/gender-specific reference ranges)
- [ ] Delta check engine (compare with previous results)
- [ ] Critical value detection + auto-alert
- [ ] Report authorization API (pathologist review + sign)
- [ ] TAT tracking engine (timestamps at each stage)
- [ ] Reflex testing rules engine (e.g., abnormal TSH → auto-order FT3/FT4)
- [ ] Lab worklist screen (by section, priority, status)
- [ ] Sample collection screen with barcode print
- [ ] Sample reception screen (accept/reject)
- [ ] Result entry grid (with ref ranges, flags, delta)
- [ ] Report preview and authorization screen
- [ ] Critical value alert notification + acknowledge
- [ ] Lab dashboard (volume, TAT, pending, critical)
- [ ] Lab order screen for doctors (test search, order sets, favorites)
- [ ] Lab report view in patient profile
- [ ] Analyzer integration interface (ASTM/HL7 framework)

## Radiology Module
- [ ] Design radiology schema (radiology_orders, radiology_reports, radiology_templates)
- [ ] Radiology order API
- [ ] Study scheduling API
- [ ] Report creation API (structured + free text)
- [ ] Critical finding alert API
- [ ] DICOM integration framework (dcm4che setup)
- [ ] PACS storage configuration (S3/MinIO)
- [ ] Radiology order screen (modality → study → indication)
- [ ] Radiology worklist screen
- [ ] Report creation screen with templates
- [ ] DICOM web viewer integration (Cornerstone.js)
- [ ] Radiology dashboard

## Pharmacy Module
- [ ] Design pharmacy schema (pharmacy_stock, stock_movements, dispensing, narcotic_register)
- [ ] Stock management API (receipt, issue, transfer, return, adjustment)
- [ ] Dispensing API (receive Rx → verify → dispense → bill)
- [ ] Batch/expiry tracking
- [ ] FEFO enforcement
- [ ] Reorder alert engine
- [ ] Controlled substance tracking API
- [ ] Pharmacy queue screen (incoming prescriptions)
- [ ] Dispensing screen (Rx view, pick, dispense, barcode)
- [ ] Stock dashboard (levels, expiry, reorder)
- [ ] Narcotic register screen
- [ ] Pharmacy reports (stock, consumption, expiry)

## Nursing Module
- [ ] Design nursing schema (vitals, mar, nursing_assessments, shift_handovers, incidents)
- [ ] Vitals API (record, trends, NEWS auto-calculation)
- [ ] MAR API (scheduled meds, administration recording)
- [ ] Nursing assessment APIs (falls, pressure ulcer, pain, wound)
- [ ] Shift handover API (SBAR format)
- [ ] Incident reporting API
- [ ] Ward dashboard screen
- [ ] Vital entry screen + trend charts
- [ ] MAR screen (schedule grid, status buttons)
- [ ] Assessment forms (structured scoring)
- [ ] Shift handover screen
- [ ] Incident report form

## ICU Management
- [ ] ICU charting API (hourly, ventilator, infusions, fluid balance)
- [ ] ICU scoring APIs (APACHE II, SOFA, GCS)
- [ ] ICU daily checklist API
- [ ] ICU dashboard (bed-level view with vitals, ventilator, alerts)
- [ ] ICU hourly charting screen
- [ ] ICU scoring display

## OT Management
- [ ] OT scheduling API (book surgery, assign OT/surgeon/anesthesiologist)
- [ ] WHO checklist API (sign-in, time-out, sign-out)
- [ ] Operation note API (findings, procedure, specimens, implants)
- [ ] Implant tracking API
- [ ] OT schedule screen (Gantt view)
- [ ] OT booking form
- [ ] WHO checklist screen
- [ ] Operation note screen
- [ ] Implant register screen
- [ ] OT utilization dashboard

## Discharge Module
- [ ] Discharge API (initiate, types: normal/DAMA/LAMA/death)
- [ ] Discharge summary auto-generation engine
- [ ] Discharge package generation (summary + Rx + labs + radiology + bill + follow-up)
- [ ] Follow-up scheduling API
- [ ] Discharge screen (summary review, sign-off)
- [ ] Discharge summary PDF generation
- [ ] Discharge package (bundled PDFs)
- [ ] Follow-up appointment scheduling

## Insurance & Full Billing
- [ ] Insurance master API (companies, TPA, policies)
- [ ] Pre-authorization API
- [ ] Claim management API
- [ ] Enhancement request API
- [ ] Full IPD billing API (room charges, multi-tariff, packages, discounts)
- [ ] Advance/deposit management
- [ ] Insurance verification screen
- [ ] Pre-auth submission screen
- [ ] Claim tracking dashboard
- [ ] Full IPD billing screen
- [ ] Financial reports (collection, outstanding, revenue, aging)
- [ ] Phase 2 integration testing (full IPD lifecycle)
- [ ] Phase 2 UAT + go-live

---

# PHASE 3 — Hospital Operations (Months 13–17)

## Inventory Management
- [ ] Design inventory schema (items, categories, stores, purchase_indents, purchase_orders, grn, stock, stock_movements, vendors, assets, amc_contracts)
- [ ] Item master API + classification engine (ABC, VED, FSN)
- [ ] Vendor master API + performance scoring
- [ ] Purchase indent API + multi-level approval
- [ ] Purchase order API + approval workflow
- [ ] GRN API + quality check + 3-way matching
- [ ] Stock issue API (to departments, patient-level)
- [ ] Inter-store transfer API
- [ ] Stock adjustment/write-off API
- [ ] Fixed asset API (register, depreciation, maintenance)
- [ ] AMC/CMC contract tracking API
- [ ] Implant tracking API (serial number, patient linkage, recall)
- [ ] CSSD tracking API (instrument sets, sterilization cycles)
- [ ] Linen management API (issue, soiled collection, laundry)
- [ ] Auto-indent on reorder level
- [ ] Item master screen + classification display
- [ ] Purchase workflow screens (indent → approval → PO → GRN)
- [ ] Stock dashboard (real-time levels, alerts)
- [ ] Stock issue screen (barcode scan, patient linkage)
- [ ] Asset register screen
- [ ] Vendor management screen
- [ ] Inventory reports (consumption, variance, expiry, ABC/VED)

## Operations Management
- [ ] Design operations schema (housekeeping_tasks, work_orders, transport_requests, diet_orders, waste_logs, security_incidents, energy_logs)
- [ ] Housekeeping API (zones, schedules, task assignment, bed turnaround)
- [ ] Work order API (create, assign, track, SLA, PM scheduling)
- [ ] Transport API (internal requests, ambulance dispatch)
- [ ] Dietary API (diet orders, meal planning, kitchen dashboard)
- [ ] BMW tracking API (waste logging, collection, disposal)
- [ ] Help desk API (service requests, auto-assign, SLA)
- [ ] Housekeeping dashboard + task tracker
- [ ] Work order screens (create, list, detail)
- [ ] PM calendar
- [ ] Transport request screen
- [ ] Kitchen dashboard + diet order screen
- [ ] BMW tracking screen
- [ ] Help desk portal
- [ ] Operations combined dashboard

## Doctor & Staff Management
- [ ] Design doctor management schema (doctor_hierarchy, credentials, intern_rotations, intern_logbook, resident_profiles, duty_rosters, roster_swaps, leaves, cross_dept_transfers, cross_consultations)
- [ ] Doctor hierarchy API (20 types, reporting relationships)
- [ ] Credentialing API (document upload, PSV tracking, privilege assignment)
- [ ] Intern management API (batch, rotation schedule, log book, evaluation)
- [ ] Resident management API (PG tracking, thesis, duty roster)
- [ ] Cross-department transfer API (request, multi-level approval, auto-access update)
- [ ] Rotation management API (schedule, auto-transition, access grant/revoke)
- [ ] On-call roster API (auto-generate, swap requests, coverage validation)
- [ ] Leave management API (apply, approve, balance, substitute)
- [ ] Cross-consultation API (referral, notification, note, TAT)
- [ ] Doctor hierarchy view screen
- [ ] Intern rotation calendar + log book screens
- [ ] Roster management screen (grid + drag-drop)
- [ ] Leave management screens (apply, approval queue, calendar)
- [ ] Cross-consultation screens (referral list, create, note)
- [ ] Doctor performance dashboard

## Infection Control, Blood Bank, Diet, MRD
- [ ] Infection control APIs (HAI surveillance, isolation, antibiogram)
- [ ] Blood bank APIs (donor, grouping, cross-match, issue, transfusion)
- [ ] Diet/nutrition APIs (assessment, plans, calorie tracking)
- [ ] MRD APIs (record completion, coding audit, certificates)
- [ ] Infection control dashboard
- [ ] Blood bank screens
- [ ] Diet management screens
- [ ] MRD screens
- [ ] Phase 3 integration testing + UAT + go-live

---

# PHASE 4 — AI & Mobile Platform (Months 18–21)

## Mobile App (Android)
- [ ] Project setup (Kotlin, Jetpack Compose, MVVM, Hilt, Room, Retrofit)
- [ ] Auth screens (login, PIN, biometric)
- [ ] QR scanner integration (CameraX + ML Kit)
- [ ] Patient summary screen (mobile-optimized)
- [ ] Clinical note creation (mobile)
- [ ] Vital entry screen (mobile)
- [ ] Lab result view (mobile)
- [ ] Prescription view/create (mobile)
- [ ] MAR screen (mobile) with barcode scan
- [ ] Nursing assessments (mobile) with photo capture
- [ ] Ward dashboard (mobile)
- [ ] Notification center (push notifications)
- [ ] Offline mode (Room database, sync engine)
- [ ] Offline patient cache for assigned patients
- [ ] Background sync (WorkManager)
- [ ] Lab sample collection (mobile)
- [ ] Transport request (mobile)
- [ ] Housekeeping task tracker (mobile)
- [ ] Work order creation (mobile) with photo
- [ ] Help desk request (mobile)

## AI Features
- [ ] Deploy Whisper Large V3 (self-hosted, GPU server)
- [ ] Optimize Whisper for medical vocabulary
- [ ] Train custom medical NER model (symptoms, drugs, dosages, diagnoses, vitals)
- [ ] Build clinical NLP pipeline (negation detection, relation extraction)
- [ ] Voice recording UI (web — microphone access, recording controls)
- [ ] Voice recording UI (mobile — large record button, real-time transcript)
- [ ] LLM fine-tuning for clinical note generation (Llama-3/Mistral)
- [ ] AI clinical note generation service (speech → entities → formatted note)
- [ ] AI note review screen (web + mobile — edit, accept, sign)
- [ ] AI SOAP note generator (aggregate data → S/O/A/P sections)
- [ ] SOAP display/edit screen (web + mobile)
- [ ] AI ICD code suggester (embedding-based search from free text)
- [ ] ICD suggestion inline UI (during diagnosis entry)
- [ ] Audio storage service (encrypted, linked to notes)
- [ ] AI confidence scoring and display
- [ ] AI performance monitoring (suggestions vs doctor decisions)
- [ ] Phase 4 UAT + mobile app release + AI go-live

---

# PHASE 5 — Advanced AI + Optimization (Months 22–24)

## Advanced AI
- [ ] Sepsis prediction model (train, validate, deploy)
- [ ] Readmission prediction model (train, validate, deploy)
- [ ] ICU transfer prediction model
- [ ] Risk alert integration (real-time scoring, auto-alerts)
- [ ] AI discharge summary generator
- [ ] AI patient summary generator ("one-line" instant summary)

## Analytics & Dashboards
- [ ] Custom report builder (drag-drop, filters, scheduling, export)
- [ ] Hospital overview dashboard
- [ ] Revenue dashboard
- [ ] Quality indicators dashboard (NABH KPIs)
- [ ] Doctor performance dashboard (advanced)
- [ ] Department comparison analytics

## Integration & Compliance
- [ ] ABDM integration (ABHA linking, health records)
- [ ] HL7 FHIR R4 API layer
- [ ] Lab analyzer integration (2–3 primary analyzers)
- [ ] SMS gateway integration
- [ ] WhatsApp Business integration
- [ ] Payment gateway integration
- [ ] NABH compliance verification + audit report
- [ ] Security penetration testing + fix findings

## Performance & Final Launch
- [ ] Query optimization (slow query analysis, index tuning)
- [ ] Frontend bundle optimization (lazy loading, code splitting)
- [ ] Redis caching strategy review
- [ ] Load testing (2000 concurrent users)
- [ ] Stress testing (beyond expected load)
- [ ] Mobile app performance optimization
- [ ] Accessibility audit (WCAG 2.1 AA)
- [ ] API documentation finalization (Swagger/OpenAPI)
- [ ] User guides per role
- [ ] Admin operations guide
- [ ] Data migration tool (if migrating from legacy)
- [ ] Final comprehensive UAT
- [ ] Production readiness checklist
- [ ] Full system go-live
- [ ] Hypercare support (4 weeks post-launch)

---

# POST-LAUNCH

## Ongoing Maintenance
- [ ] Monthly security patching
- [ ] Quarterly AI model retraining
- [ ] Annual re-credentialing workflow trigger
- [ ] Quarterly DR (Disaster Recovery) drill
- [ ] Monthly performance review
- [ ] User feedback collection and prioritization
- [ ] Continuous improvement backlog grooming

---

*Task List v1.0 — MedCore HIS — Total: ~500+ individual tasks*

## Mobile App Implementation
- [x] Scaffold Android app with Jetpack Compose
- [x] Implement UI for QR Scanner, AI Scribe, and Dashboards
- [x] Implement dynamic runtime permissions for `CAMERA` and `RECORD_AUDIO`
- [x] Implement local NetworkClient for API requests
- [x] Set up Hilt Dependency Injection
- [x] Configure local SQLite caching schema using Room Database
- [x] Implement sync background jobs using WorkManager
- [x] Bedside Vital Entry: Create entry dialog to save vitals
- [x] MAR Bedside Barcode Scan: Scan medication barcode
- [x] Nursing Assessments: Add Morse Falls Risk and Braden Pressure Ulcer
- [x] Incidents Reporting: Form to log adverse events
- [x] Operations Task Tracker: Bed turnaround, porter request
