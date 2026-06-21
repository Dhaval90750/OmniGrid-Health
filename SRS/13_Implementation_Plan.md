# MedCore HIS — Implementation Plan

**Version:** 1.0 | **Date:** 2026-06-21 | **Total Duration:** 24 months (5 Phases)

---

## 1. Implementation Strategy

### 1.1 Approach: Phased Delivery with Incremental Go-Lives

We follow a **phase-wise delivery** approach where each phase produces a deployable, usable product increment. The hospital can start using the system after Phase 1 itself, with additional modules activated in subsequent phases.

```
Phase 1 (MVP)          Phase 2              Phase 3              Phase 4           Phase 5
Foundation +           Full Clinical        Hospital             AI & Mobile       Advanced AI +
Core Clinical          Expansion            Operations           Platform          Optimization
───────────────────────────────────────────────────────────────────────────────────────────────
Month 1───────6        Month 7─────12       Month 13────17       Month 18───21     Month 22──24
                │                    │                    │                  │              │
           GO-LIVE 1           GO-LIVE 2           GO-LIVE 3         GO-LIVE 4      GO-LIVE 5
           (Core)              (Clinical)          (Operations)      (AI+Mobile)    (Full)
```

### 1.2 Development Methodology

| Aspect | Choice | Details |
|--------|--------|---------|
| **Methodology** | Agile Scrum | 2-week sprints |
| **Sprint Duration** | 14 days | — |
| **Sprint Ceremonies** | Daily standup (15 min), Sprint Planning (4 hr), Sprint Review (2 hr), Retrospective (1 hr) | — |
| **Release Cadence** | Every 4 sprints (monthly release to staging, bi-monthly to production) | — |
| **Code Review** | Mandatory PR review by 2 reviewers | — |
| **Testing** | TDD for backend, component testing for frontend, E2E for critical paths | — |
| **CI/CD** | Automated build → test → deploy on every merge to main | — |
| **Environment** | Dev → QA → Staging → Production | — |

### 1.3 Team Structure

| Role | Count | Phase 1 | Phase 2 | Phase 3 | Phase 4 | Phase 5 |
|------|:-----:|:-------:|:-------:|:-------:|:-------:|:-------:|
| Project Manager | 1 | ✅ | ✅ | ✅ | ✅ | ✅ |
| Product Owner (Healthcare Domain) | 1 | ✅ | ✅ | ✅ | ✅ | ✅ |
| Technical Architect | 1 | ✅ | ✅ | ✅ | ✅ | ✅ |
| Backend Lead (Java/Spring) | 1 | ✅ | ✅ | ✅ | ✅ | ✅ |
| Backend Developers | 4–5 | ✅ | ✅ | ✅ | ✅ | ✅ |
| Frontend Lead (React) | 1 | ✅ | ✅ | ✅ | ✅ | ✅ |
| Frontend Developers | 2–3 | ✅ | ✅ | ✅ | ✅ | ✅ |
| Mobile Developer (Android/Kotlin) | 1–2 | ❌ | ❌ | ❌ | ✅ | ✅ |
| AI/ML Engineer | 1 | ❌ | ❌ | ❌ | ✅ | ✅ |
| Database Engineer | 1 | ✅ | ✅ | ✅ | ✅ | ❌ |
| QA Lead | 1 | ✅ | ✅ | ✅ | ✅ | ✅ |
| QA Engineers | 1–2 | ✅ | ✅ | ✅ | ✅ | ✅ |
| DevOps Engineer | 1 | ✅ | ✅ | ✅ | ✅ | ✅ |
| UI/UX Designer | 1 | ✅ | ✅ | ✅ | ✅ | ❌ |
| **Active Team Size** | — | **14** | **15** | **14** | **18** | **14** |

---

## 2. Phase 1 — Foundation + Core Clinical (Months 1–6)

### 2.1 Objective

Build the platform foundation and deliver the **minimum viable hospital system** that can handle patient registration, OPD consultation, basic IPD admission, doctor workflow, prescriptions, and basic billing.

### 2.2 Sprint Plan

#### Sprint 0 — Project Setup (Weeks 1–2)

| Task | Owner | Deliverable |
|------|-------|-------------|
| Project kickoff, team onboarding | PM | Kickoff deck, team access |
| Development environment setup | DevOps | Docker-compose for local dev |
| CI/CD pipeline setup | DevOps | Jenkins/GitLab CI pipeline |
| Repository structure + branching strategy | Architect | Git repo with mono-repo structure |
| PostgreSQL schema design (core tables) | DB Engineer | Migration scripts V1 |
| Spring Boot project scaffolding | Backend Lead | Base project with security, logging, error handling |
| React/Next.js project scaffolding | Frontend Lead | Base project with routing, auth, design system |
| Light mode design system + UI kit | UI/UX Designer | Figma designs + CSS tokens |
| Coding standards + review guidelines | Architect | Wiki documentation |
| QA environment + test framework setup | QA Lead | Test infrastructure ready |

#### Sprints 1–2 — Authentication & Core Infrastructure (Weeks 3–6)

| Task | Owner | Details |
|------|-------|---------|
| **Auth Service** | Backend | Login, JWT (access+refresh), logout, password reset, MFA framework |
| **RBAC Engine** | Backend | Role model, permission model, role-permission mapping, middleware enforcement |
| **User Management API** | Backend | CRUD users, assign roles, activate/deactivate, search |
| **Login Page** | Frontend | Username/password form, MFA step, error handling, light theme |
| **Admin → User Management Screen** | Frontend | User list, create/edit user modal, role assignment, filters |
| **Role Management Screen** | Frontend | Create role, permission matrix UI, assign to users |
| **Session Management** | Backend | Redis sessions, concurrent session limit, timeout policies |
| **Audit Log Service** | Backend | Generic audit interceptor, log all CUD operations to audit table |
| **API Gateway Setup** | DevOps | Kong/Nginx gateway with rate limiting, CORS, SSL |
| **Unit Tests** | QA | Auth API tests, RBAC permission tests |

#### Sprints 3–4 — Patient Registration & QR System (Weeks 7–10)

| Task | Owner | Details |
|------|-------|---------|
| **Patient Master API** | Backend | Register, search (UHID/name/mobile/Aadhaar), update demographics, duplicate detection |
| **UHID Generation** | Backend | Auto-increment with hospital prefix, checksum validation |
| **QR Code Generation** | Backend | ZXing-based QR with encoded patient data (UHID, name, DOB, blood group, allergy flag) |
| **QR Wristband Print** | Backend | Thermal printer integration (Zebra ZPL format), A5 registration slip |
| **Patient Search Screen** | Frontend | Search bar with UHID/name/mobile, results table, quick actions |
| **New Patient Registration Form** | Frontend | Multi-step form with validation, photo webcam capture, allergy entry |
| **Patient Profile View** | Frontend | Demographics card, quick-edit, QR display, visit history (empty placeholder) |
| **Duplicate Detection** | Backend | Fuzzy matching on name+DOB+mobile, confidence scoring, review UI |
| **Master Data: States, Countries** | Backend | Seed data for dropdowns |
| **Integration Tests** | QA | Registration flow E2E, QR generation, duplicate detection |

#### Sprints 5–6 — Department, Doctor & OPD Setup (Weeks 11–14)

| Task | Owner | Details |
|------|-------|---------|
| **Department Master API** | Backend | CRUD departments, hierarchy, active/inactive |
| **Doctor Master API** | Backend | Doctor profile (full model from SRS 12.3), type, department, qualifications, schedule |
| **Doctor Privilege Engine** | Backend | Privilege assignment per doctor type, department-based access |
| **OPD Visit API** | Backend | Create visit, assign doctor, token generation, queue management |
| **Token/Queue System** | Backend | Auto-token per doctor per day, priority queue logic, estimated wait time |
| **Department Management Screen** | Frontend | List, create/edit department, hierarchy view |
| **Doctor Management Screen** | Frontend | Doctor list, create/edit with all profile fields, schedule config |
| **OPD Visit Creation Screen** | Frontend | Patient search → select department → select doctor → create visit |
| **OPD Queue Display** | Frontend | Current token, waiting list, estimated time, doctor's queue panel |
| **Doctor Dashboard (Basic)** | Frontend | Today's patient list, pending reviews, quick actions |

#### Sprints 7–8 — Doctor Clinical Workflow (Weeks 15–18)

| Task | Owner | Details |
|------|-------|---------|
| **Clinical Notes API** | Backend | Create/read/update clinical notes (HPI, examination, ROS, assessment, plan) |
| **Diagnosis API** | Backend | ICD-10 search engine (Elasticsearch index), multi-diagnosis support, favorites |
| **ICD-10 Data Import** | DB Engineer | Import full ICD-10-CM database (~70,000 codes) into search index |
| **Patient Summary API** | Backend | Aggregate patient data: demographics, allergies, chronic conditions, visit history |
| **Allergy Management API** | Backend | CRUD allergies with severity, cross-reactivity database |
| **Patient Summary Dashboard** | Frontend | 360° view: demographics banner, allergies alert, vitals, active meds, history tabs |
| **Clinical Note Entry Screen** | Frontend | Tabbed form: HPI, Exam, ROS, Assessment, Plan with auto-save |
| **Diagnosis Entry Component** | Frontend | ICD-10 searchable typeahead, add multiple, set primary/secondary, type (confirmed/provisional) |
| **Allergy Entry Screen** | Frontend | Add allergy with severity, reaction type, verified-by |
| **Clinical Note Timeline** | Frontend | Chronological list of all notes for a patient |
| **Doctor Favorite Diagnoses** | Backend + Frontend | Save/load favorite diagnosis combinations |

#### Sprints 9–10 — Prescription & Drug Management (Weeks 19–22)

| Task | Owner | Details |
|------|-------|---------|
| **Drug Master API** | Backend | Drug database (generic+brand), search, dosage forms, strengths |
| **Drug Database Import** | DB Engineer | Import Indian drug database (CDSCO or equivalent), ~50,000 entries |
| **Prescription API** | Backend | Create prescription with drug lines (drug, strength, dose, frequency, duration, instructions) |
| **Drug Interaction Engine** | Backend | Drug-drug, drug-allergy, duplicate therapy checking with severity levels |
| **Prescription Template API** | Backend | Save/load doctor's favorite prescription templates |
| **Prescription Entry Screen** | Frontend | Drug search typeahead, add lines, interaction warnings inline, template selector |
| **Prescription Print/PDF** | Backend + Frontend | Formatted prescription PDF with hospital header, doctor signature placeholder |
| **Drug Interaction Alert Modal** | Frontend | Warning dialog showing interaction details, override with justification |
| **Prescription History View** | Frontend | All prescriptions for a patient, current vs past, timeline |
| **Integration Tests** | QA | Full OPD flow: register → visit → consult → diagnose → prescribe |

#### Sprint 11 — Basic Admission & Bed Management (Weeks 23–24)

| Task | Owner | Details |
|------|-------|---------|
| **Ward/Bed Master API** | Backend | CRUD wards, bed categories (General/Private/ICU), bed status tracking |
| **Admission API** | Backend | Create admission, bed allocation, admission number generation |
| **Bed Dashboard API** | Backend | Real-time bed status (available/occupied/cleaning), ward-wise summary |
| **Ward & Bed Configuration Screen** | Frontend | Manage wards, add beds, set categories and rates |
| **Admission Form** | Frontend | Patient → department → ward → bed → doctor → diagnosis → advance |
| **Bed Dashboard** | Frontend | Visual bed map (color-coded), summary cards, filters |
| **Admission List Screen** | Frontend | Active admissions, search, filters, quick actions |

#### Sprint 12 — Basic Billing & Phase 1 Wrap-Up (Weeks 25–26)

| Task | Owner | Details |
|------|-------|---------|
| **Tariff Master API** | Backend | Service codes, prices, multiple tariffs (cash/insurance) |
| **Invoice API** | Backend | Create invoice, add line items, calculate total, apply discount |
| **Payment API** | Backend | Record payment (cash/card/UPI), receipt generation, advance management |
| **OPD Billing Screen** | Frontend | Auto-populated from consultation + tests, payment collection |
| **Basic IPD Billing Screen** | Frontend | Room charges, consultation charges, interim bill |
| **Receipt Print** | Backend + Frontend | Payment receipt PDF |
| **Phase 1 Integration Testing** | QA | Full flow: Register → OPD Visit → Consult → Prescribe → Bill → Pay |
| **Phase 1 UAT** | QA + Domain | User acceptance testing with hospital staff |
| **Performance Testing** | QA | Load test core APIs (500 concurrent users) |
| **Production Deployment** | DevOps | Deploy Phase 1 to production |
| **User Training (Phase 1)** | PM + Domain | Train reception, doctors, billing staff |

### 2.3 Phase 1 Deliverables

| Deliverable | Screens | Status |
|------------|:-------:|--------|
| Authentication & User Management | 8 | ✅ Go-Live |
| Patient Registration + QR | 6 | ✅ Go-Live |
| Department & Doctor Management | 8 | ✅ Go-Live |
| OPD Visit + Queue | 6 | ✅ Go-Live |
| Doctor Clinical Workflow (Notes, Diagnosis) | 10 | ✅ Go-Live |
| Prescription Management | 6 | ✅ Go-Live |
| Basic Admission + Bed Management | 6 | ✅ Go-Live |
| Basic Billing + Payments | 6 | ✅ Go-Live |
| **Total** | **~56** | — |

---

## 3. Phase 2 — Full Clinical Expansion (Months 7–12)

### 3.1 Objective

Expand the clinical system with full LIS, Radiology, Pharmacy, Nursing, ICU, OT, Discharge, and Insurance modules.

### 3.2 Sprint Plan

#### Sprints 13–15 — Laboratory Information System (Weeks 1–6)

| Task | Owner | Details |
|------|-------|---------|
| **Lab Master Data** | Backend + DB | Test catalog (500+ tests), panels, reference ranges (age/gender-specific), lab sections |
| **Lab Order API** | Backend | Doctor orders tests, order sets, stat/routine priority, auto-billing |
| **Sample Management API** | Backend | Sample collection, barcode labeling, reception, acceptance/rejection |
| **Result Entry API** | Backend | Manual entry, auto-flag abnormal, delta check (compare with previous) |
| **Report Authorization API** | Backend | Pathologist review, digital signature, auto-dispatch |
| **Critical Value Engine** | Backend | Configurable critical ranges, auto-alert to doctor/nurse on critical result |
| **TAT Tracking** | Backend | Track turnaround time per stage (order → collection → result → dispatch) |
| **Lab Worklist Screen** | Frontend | Pending orders by section, priority sorting, batch processing |
| **Sample Collection Screen** | Frontend | Barcode print, patient verification, tube type guide |
| **Result Entry Screen** | Frontend | Data grid with reference ranges, flag column, delta display |
| **Lab Report View/Print** | Frontend | Formatted report with patient info, results table, abnormal highlights |
| **Lab Dashboard** | Frontend | Volume, TAT, pending, critical values summary |
| **Critical Value Alert UI** | Frontend | Push notification + acknowledge flow |
| **Lab Order Screen (Doctor)** | Frontend | Test search, order sets, favorite orders, sample requirements display |
| **Analyzer Integration Framework** | Backend | ASTM/HL7 interface for future analyzer connectivity |

#### Sprints 16–17 — Radiology Module (Weeks 7–10)

| Task | Owner | Details |
|------|-------|---------|
| **Radiology Order API** | Backend | Order imaging studies, modality selection, clinical indication, scheduling |
| **Radiology Report API** | Backend | Create findings/impression, structured reporting templates, authorization |
| **DICOM/PACS Integration Framework** | Backend | dcm4che setup, DICOM receive/store, web viewer integration setup |
| **Critical Finding Alert** | Backend | Flag critical findings, notify ordering doctor |
| **Radiology Order Screen (Doctor)** | Frontend | Modality → study selection, indication, pregnancy/contrast allergy check |
| **Radiology Worklist Screen** | Frontend | Pending studies, scheduled, completed, priority |
| **Report Creation Screen** | Frontend | Template-based reporting, free text, structured (BI-RADS etc.) |
| **DICOM Viewer Integration** | Frontend | Cornerstone.js/OHIF viewer embedded for image viewing |
| **Radiology Dashboard** | Frontend | Volume by modality, TAT, pending reports |

#### Sprints 18–19 — Pharmacy Module (Weeks 11–14)

| Task | Owner | Details |
|------|-------|---------|
| **Pharmacy Stock API** | Backend | Stock management, batch tracking, FEFO, reorder alerts |
| **Dispensing API** | Backend | Receive prescription → verify → dispense → bill, batch/expiry logging |
| **Return Management API** | Backend | Patient returns, ward returns, credit notes |
| **Controlled Substance Tracking** | Backend | Narcotic register, double verification, shift handover count |
| **Expiry Management** | Backend | Expiry alerts (30/60/90 day), near-expiry report |
| **Pharmacy Queue Screen** | Frontend | Incoming prescriptions, dispensing workflow |
| **Dispensing Screen** | Frontend | Prescription view, pick/dispense, barcode scan, batch selection |
| **Pharmacy Stock Dashboard** | Frontend | Stock levels, expiry alerts, reorder items, ABC analysis |
| **Narcotic Register Screen** | Frontend | Digital narcotic register with audit trail |
| **Pharmacy Reports** | Frontend | Stock report, consumption, expiry, dispensing volume |

#### Sprints 20–21 — Nursing Module (Weeks 15–18)

| Task | Owner | Details |
|------|-------|---------|
| **Vitals API** | Backend | Record vitals (temp, pulse, BP, SpO2, RR, pain, GCS, I/O), trends, NEWS auto-calculation |
| **Medication Administration API** | Backend | MAR (Medication Administration Record), 5-rights verification, status tracking |
| **Nursing Assessment API** | Backend | Falls risk (Morse), pressure ulcer (Braden), pain assessment, wound assessment |
| **Shift Handover API** | Backend | SBAR format handover, pending tasks, acknowledgment |
| **Incident Reporting API** | Backend | Report adverse events, categorization, severity, follow-up |
| **Ward Dashboard Screen** | Frontend | Patient list with vital summaries, alerts, pending tasks |
| **Vital Entry Screen** | Frontend | Quick entry form with normal range indicators, trend graph |
| **Vital Trends Chart** | Frontend | Interactive line charts (temperature, BP, pulse, SpO2 over time), NEWS score |
| **MAR Screen** | Frontend | Medication schedule grid, given/held/refused status, barcode verify |
| **Nursing Assessment Forms** | Frontend | Structured assessment forms with scoring |
| **Shift Handover Screen** | Frontend | Auto-populated SBAR form, pending task list, sign-off |
| **Incident Report Form** | Frontend | Categorized incident entry with severity |

#### Sprints 22–23 — ICU, OT & Discharge (Weeks 19–22)

| Task | Owner | Details |
|------|-------|---------|
| **ICU Charting API** | Backend | Hourly charting, ventilator settings, infusion tracking, fluid balance, ICU scoring (APACHE, SOFA) |
| **ICU Dashboard** | Frontend | Bed-level view: vitals, ventilator, infusions, alerts, scoring |
| **ICU Hourly Chart Screen** | Frontend | Dense data grid for hourly documentation |
| **OT Scheduling API** | Backend | Surgery booking, OT room allocation, surgeon/anesthesiologist assignment |
| **OT Documentation API** | Backend | WHO checklist, anesthesia record, surgeon notes, implant logging, counts |
| **OT Schedule Screen** | Frontend | Gantt chart view of OT rooms, daily/weekly schedule |
| **OT Booking Form** | Frontend | Procedure, surgeon, anesthesia type, date/time, special requirements |
| **Pre-Op Checklist Screen** | Frontend | Digital WHO checklist with timestamps |
| **Operation Note Screen** | Frontend | Structured OT note: findings, procedure, specimens, implants |
| **Discharge API** | Backend | Initiate discharge, auto-generate summary, discharge types (normal/DAMA/LAMA/death) |
| **Discharge Summary Generator** | Backend | Compile data from all modules into formatted discharge summary PDF |
| **Discharge Screen** | Frontend | Summary review, medication at discharge, follow-up, patient education |
| **Discharge PDF** | Backend | Professional discharge summary PDF with all sections |

#### Sprints 24–25 — Insurance/TPA & Full Billing (Weeks 23–26)

| Task | Owner | Details |
|------|-------|---------|
| **Insurance Master API** | Backend | Insurance companies, TPA, policy management, room category limits |
| **Pre-Authorization API** | Backend | Submit pre-auth, track status, enhancement requests |
| **Claim Management API** | Backend | Submit claims, track settlement, handle queries/rejections |
| **Full IPD Billing API** | Backend | Room charges (per day), multi-tariff, package billing, interim bills, final bill |
| **Advance/Deposit Management** | Backend | Collect advances, auto-adjust against final bill |
| **Insurance Verification Screen** | Frontend | Eligibility check, policy details capture |
| **Pre-Auth Screen** | Frontend | Submit pre-auth with clinical details, estimated cost |
| **Claim Tracking Dashboard** | Frontend | Pending claims, approved, rejected, query, settlement tracking |
| **Full IPD Billing Screen** | Frontend | Itemized bill, insurance split, package application, discounts |
| **Financial Reports** | Frontend | Collection report, outstanding report, revenue by department/doctor |
| **Phase 2 Integration Testing** | QA | Full IPD flow: Admit → Lab → Radiology → Pharmacy → Nursing → ICU → OT → Discharge → Bill |
| **Phase 2 UAT + Go-Live** | All | UAT with clinical staff, production deployment, training |

### 3.3 Phase 2 Deliverables

| Deliverable | Screens |
|------------|:-------:|
| Laboratory Information System (Full) | 20 |
| Radiology Module | 13 |
| Pharmacy Module | 16 |
| Nursing Module | 18 |
| ICU Management | 12 |
| OT Management | 13 |
| Discharge Module | 9 |
| Insurance/TPA + Full Billing | 23 |
| **Total** | **~124** |

---

## 4. Phase 3 — Hospital Operations (Months 13–17)

### 4.1 Objective

Add all non-clinical support modules: Inventory, Operations, Doctor Staff Management, Infection Control, Blood Bank, and remaining support modules.

### 4.2 Sprint Plan

#### Sprints 26–28 — Inventory Management (Weeks 1–6)

| Task | Owner | Details |
|------|-------|---------|
| **Item Master API** | Backend | Item catalog with classifications (ABC, VED, FSN), UOM, reorder levels |
| **Vendor Master API** | Backend | Vendor profiles, contracts, performance scoring |
| **Purchase Indent API** | Backend | Create/approve indents, auto-indent on reorder level, multi-level approval |
| **Purchase Order API** | Backend | Generate PO from approved indents, vendor selection, terms, approval workflow |
| **GRN API** | Backend | Goods receipt, quality check, batch/expiry capture, 3-way matching |
| **Stock Issue API** | Backend | Issue to departments, patient-level consumption, barcode tracking |
| **Inter-Store Transfer API** | Backend | Transfer between stores, return management, stock adjustments |
| **Fixed Asset API** | Backend | Asset register, depreciation, AMC/CMC tracking, calibration schedule |
| **Implant Tracking API** | Backend | Serial number tracking, patient linkage, recall management |
| **Item Master Screen** | Frontend | Searchable catalog, create/edit item, classification display |
| **Purchase Workflow Screens** | Frontend | Indent form, approval queue, PO form, GRN form |
| **Stock Dashboard** | Frontend | Real-time stock levels, expiry alerts, reorder alerts |
| **Stock Issue Screen** | Frontend | Issue to department, barcode scan, patient charge linkage |
| **Asset Register Screen** | Frontend | Asset list, maintenance history, AMC tracking, calibration due |
| **Vendor Management Screen** | Frontend | Vendor list, performance scorecard, contract tracking |
| **Inventory Reports** | Frontend | Consumption, stock status, expiry, purchase analysis, ABC/VED matrix |

#### Sprints 29–31 — Operations Management (Weeks 7–12)

| Task | Owner | Details |
|------|-------|---------|
| **Housekeeping API** | Backend | Zone management, task scheduling, checklist completion, bed turnaround |
| **Work Order API** | Backend | Create/assign/track maintenance work orders, SLA management, PM scheduling |
| **Transport API** | Backend | Internal transport requests, porter assignment, ambulance dispatch |
| **Dietary API** | Backend | Diet orders from clinical, meal planning, kitchen dashboard, meal count |
| **BMW Tracking API** | Backend | Waste generation logging, collection, handover to CBWTF, manifest |
| **Help Desk API** | Backend | Service requests, auto-categorization, auto-assignment, SLA tracking |
| **Housekeeping Dashboard** | Frontend | Zone compliance, bed turnaround times, task tracker |
| **Work Order Screens** | Frontend | Create WO, WO list with filters, WO detail with timeline |
| **PM Calendar** | Frontend | Preventive maintenance calendar view, equipment-wise schedule |
| **Transport Request Screen** | Frontend | Request form, status tracking, porter assignment |
| **Kitchen Dashboard** | Frontend | Meal counts by diet type, ward-wise distribution, preparation status |
| **BMW Tracking Screen** | Frontend | Daily waste logging, collection records, compliance reports |
| **Help Desk Portal** | Frontend | Create request, track status, feedback |
| **Operations Dashboard** | Frontend | Combined KPI view: housekeeping, maintenance, transport, dietary, waste |

#### Sprints 32–33 — Doctor & Staff Management (Weeks 13–16)

| Task | Owner | Details |
|------|-------|---------|
| **Doctor Hierarchy API** | Backend | 20 doctor types, hierarchy relationships, designation management |
| **Credentialing API** | Backend | Document submission, PSV tracking, privilege assignment, re-credentialing |
| **Intern Management API** | Backend | Intern batch creation, rotation schedule generation, log book, evaluation |
| **Resident Management API** | Backend | Resident profile extension, PG tracking, thesis tracking |
| **Cross-Department Transfer API** | Backend | Transfer request, multi-level approval, access management |
| **Rotation Management API** | Backend | Rotation scheduling, auto-transition, access update on rotation change |
| **On-Call Roster API** | Backend | Roster generation (auto + manual), swap requests, coverage validation |
| **Leave Management API** | Backend | Leave application, approval workflow, auto-substitute, balance tracking |
| **Cross-Consultation API** | Backend | Referral creation, notification, consultation note, TAT tracking |
| **Doctor Management Screens** | Frontend | Doctor list with hierarchy view, profile with full detail tabs |
| **Intern Rotation Screen** | Frontend | Rotation calendar, current posting, log book entry, evaluation forms |
| **Roster Management Screen** | Frontend | Monthly roster grid, drag-drop assignment, swap requests |
| **Leave Management Screen** | Frontend | Apply leave, approval queue, leave balance, calendar view |
| **Cross-Consultation Screen** | Frontend | Referral list, create referral, consultation note entry |
| **Doctor Performance Dashboard** | Frontend | Volume, revenue, outcomes, documentation compliance |

#### Sprint 34 — Infection Control, Blood Bank, Diet, MRD (Weeks 17–18)

| Task | Owner | Details |
|------|-------|---------|
| **Infection Control API** | Backend | HAI surveillance, isolation management, antibiogram, hand hygiene audit |
| **Blood Bank API** | Backend | Donor management, grouping/cross-matching, component inventory, transfusion tracking |
| **Diet & Nutrition API** | Backend | Nutrition assessment, diet plan creation, special diet management |
| **MRD API** | Backend | Record completion tracking, coding audit, statistics, certificate generation |
| **Infection Control Dashboard** | Frontend | HAI rates, isolation map, antimicrobial usage |
| **Blood Bank Screens** | Frontend | Donor register, cross-match, issue blood, inventory |
| **Diet Management Screens** | Frontend | Diet order, meal planning, calorie tracking |
| **MRD Screens** | Frontend | Record deficiency list, coding audit, certificates |
| **Phase 3 UAT + Go-Live** | All | UAT, production deployment, training for new modules |

---

## 5. Phase 4 — AI & Mobile Platform (Months 18–21)

### 5.1 Objective

Deliver the Android mobile app for clinical staff and implement core AI features (voice-to-text, SOAP generation, clinical coding assistance).

### 5.2 Sprint Plan

#### Sprints 35–37 — Mobile App Foundation (Weeks 1–6)

| Task | Owner | Details |
|------|-------|---------|
| **Android Project Setup** | Mobile | Kotlin + Jetpack Compose, MVVM + Clean Architecture, Hilt DI |
| **Authentication (Mobile)** | Mobile | Login, PIN, biometric (fingerprint), session management |
| **QR Scanner** | Mobile | CameraX + ML Kit QR scanner, instant patient lookup |
| **Patient Summary (Mobile)** | Mobile | Patient dashboard optimized for mobile |
| **Clinical Notes (Mobile)** | Mobile | Create/edit clinical notes on mobile |
| **Vital Entry (Mobile)** | Mobile | Quick vital entry form optimized for bedside use |
| **Lab Result View (Mobile)** | Mobile | View lab results with abnormal highlighting |
| **Prescription View (Mobile)** | Mobile | View/create prescriptions |
| **Notification Push** | Mobile + Backend | Firebase Cloud Messaging, notification center |
| **Offline Mode Framework** | Mobile | Room database for local storage, sync engine (WorkManager) |
| **Offline Patient Cache** | Mobile | Cache assigned patients' data for offline access |

#### Sprints 38–39 — Mobile Clinical Features (Weeks 7–10)

| Task | Owner | Details |
|------|-------|---------|
| **MAR (Mobile)** | Mobile | Medication administration with barcode scan verification |
| **Nursing Assessments (Mobile)** | Mobile | Falls risk, wound assessment with photo capture |
| **Ward Dashboard (Mobile)** | Mobile | Patient list, alerts, pending tasks |
| **Lab Sample Collection (Mobile)** | Mobile | Barcode scan for sample verification |
| **Transport Request (Mobile)** | Mobile | Request transport, track status |
| **Housekeeping Task (Mobile)** | Mobile | View assigned tasks, zone QR scan, checklist completion |
| **Work Order (Mobile)** | Mobile | Create maintenance requests with photo |
| **Help Desk (Mobile)** | Mobile | Submit service requests |

#### Sprints 40–42 — AI Features (Weeks 11–18)

| Task | Owner | Details |
|------|-------|---------|
| **Whisper STT Deployment** | AI/ML | Self-host Whisper Large V3, optimize for medical speech |
| **Medical NER Model** | AI/ML | Train custom NER for symptoms, drugs, dosages, diagnoses, vitals, body parts |
| **Clinical NLP Pipeline** | AI/ML | Negation detection, relation extraction, abbreviation expansion |
| **Voice Recording UI (Mobile)** | Mobile | Large record button, real-time transcription display |
| **Voice Recording UI (Web)** | Frontend | Microphone access, recording controls, transcription preview |
| **LLM Medical Summarizer** | AI/ML | Fine-tune Llama-3/Mistral for clinical note generation from structured entities |
| **AI Clinical Note Review Screen** | Frontend + Mobile | Display AI-generated note with confidence scores, edit capability, sign-off |
| **AI SOAP Generator** | AI/ML + Backend | Aggregate patient data → auto-generate SOAP sections |
| **SOAP Display/Edit Screen** | Frontend + Mobile | View SOAP with edit capability per section |
| **AI ICD Code Suggester** | AI/ML | Embedding-based ICD search from free text diagnosis, ranked suggestions |
| **ICD Suggestion UI** | Frontend | Inline ICD suggestions during diagnosis entry |
| **Audio Storage** | Backend | Encrypted audio storage linked to clinical notes for medico-legal compliance |
| **AI Performance Monitoring** | Backend | Log AI suggestions vs doctor selections, accuracy tracking |
| **Phase 4 UAT + Go-Live** | All | Mobile app release to Play Store (internal), AI feature UAT |

---

## 6. Phase 5 — Advanced AI + Optimization (Months 22–24)

### 6.1 Objective

Implement advanced AI (risk prediction, radiology AI), management dashboards, ABDM integration, performance optimization, and final polish.

### 6.2 Sprint Plan

#### Sprints 43–44 — Advanced AI & Analytics (Weeks 1–4)

| Task | Owner | Details |
|------|-------|---------|
| **Sepsis Prediction Model** | AI/ML | Train on historical data (vitals + labs + clinical), qSOFA/SOFA correlation |
| **Readmission Prediction** | AI/ML | Train model on discharge data, comorbidity index, LOS, medication count |
| **AI Risk Alert Integration** | Backend + Frontend | Real-time risk score display on patient dashboard, auto-alert on high risk |
| **AI Discharge Summary Generator** | AI/ML + Backend | Auto-compile all data into professional discharge summary |
| **Custom Report Builder** | Backend + Frontend | Drag-drop report designer, filter/group/aggregate, schedule, export |
| **Management Dashboards** | Frontend | Hospital overview, revenue, quality indicators, department comparison |
| **Doctor Performance Dashboard (Advanced)** | Frontend | Volume trends, outcome metrics, benchmarking |

#### Sprints 45–46 — Integration & Compliance (Weeks 5–8)

| Task | Owner | Details |
|------|-------|---------|
| **ABDM Integration** | Backend | ABHA ID linking, Health Records push/pull via FHIR |
| **HL7 FHIR API Layer** | Backend | Expose patient/encounter/observation/medication resources as FHIR R4 |
| **Lab Analyzer Integration** | Backend | ASTM/HL7 interface for 2-3 primary analyzers |
| **SMS Gateway Integration** | Backend | Appointment reminders, lab results, discharge follow-up |
| **WhatsApp Business Integration** | Backend | Appointment confirmation, follow-up reminders |
| **Payment Gateway Integration** | Backend | Online payment for advances/deposits |
| **NABH Compliance Audit** | QA + Domain | Verify all workflows meet NABH standards, generate compliance report |
| **Security Audit** | QA | OWASP vulnerability scan, penetration testing, fix findings |

#### Sprints 47–48 — Performance, Polish & Launch (Weeks 9–12)

| Task | Owner | Details |
|------|-------|---------|
| **Performance Optimization** | Backend + Frontend | Query optimization, caching strategy, lazy loading, bundle optimization |
| **Load Testing** | QA | Simulate 2000 concurrent users, identify bottlenecks, fix |
| **Stress Testing** | QA | Push beyond expected load, ensure graceful degradation |
| **Accessibility Audit** | Frontend | WCAG 2.1 AA compliance, screen reader testing |
| **Mobile Performance** | Mobile | App startup time < 3s, smooth scrolling, memory optimization |
| **Documentation** | All | API documentation (Swagger), user guides per role, admin guide, ops runbook |
| **Data Migration Tool** | Backend | If migrating from existing system: import patients, history |
| **Final UAT** | All | Comprehensive UAT across all modules, all roles |
| **Go-Live Preparation** | DevOps | Production readiness checklist, monitoring setup, alerting setup |
| **Full System Go-Live** | All | Final production deployment, on-site support (2 weeks) |
| **Hypercare Support** | All | 4-week post-go-live intensive support period |

---

## 7. Risk Management

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| **Scope creep** | High | High | Strict change control process, prioritized backlog, phase gates |
| **Healthcare domain complexity** | High | Medium | Dedicated domain expert on team, regular clinical SME reviews |
| **AI model accuracy** | Medium | Medium | Shadow mode deployment, human-in-the-loop always, gradual rollout |
| **Integration challenges (analyzers, PACS)** | Medium | High | Early PoC for integrations, vendor coordination started in Phase 1 |
| **Performance at scale** | High | Medium | Performance testing every phase, database optimization sprints |
| **Data migration issues** | High | Medium | Migration tool built early, parallel run period |
| **Staff resistance to change** | Medium | High | Change management program, super-user champions in each department |
| **Regulatory compliance gaps** | High | Low | NABH consultant engaged from Phase 1, compliance audit in Phase 5 |
| **Key person dependency** | Medium | Medium | Knowledge sharing, pair programming, documentation |
| **Infrastructure delays** | Medium | Low | Cloud fallback option, containerized deployment |

---

## 8. Go-Live Strategy

### 8.1 Rollout Approach

| Option | Description | Recommendation |
|--------|-------------|---------------|
| **Big Bang** | All modules go live simultaneously | ❌ Not recommended (high risk) |
| **Phased Rollout** | Phase-wise go-live (our approach) | ✅ Recommended |
| **Pilot Department** | Single department first, then expand | ✅ Recommended for Phase 1 |
| **Parallel Run** | Old + new system run together for 2-4 weeks | ✅ Recommended for Phase 1 |

### 8.2 Training Plan

| Phase | Audience | Duration | Method |
|-------|----------|----------|--------|
| Phase 1 | Reception, Doctors (OPD), Billing | 3 days classroom + 2 days hands-on | On-site |
| Phase 2 | Lab, Radiology, Pharmacy, Nurses, ICU, OT | 5 days classroom + 3 days hands-on | On-site |
| Phase 3 | Inventory, Operations, HR | 3 days classroom + 2 days hands-on | On-site |
| Phase 4 | All clinical staff (mobile app) | 2 days + video tutorials | On-site + online |
| Phase 5 | Management, AI feature users | 1 day + quick reference guides | On-site |

### 8.3 Post-Go-Live Support

| Period | Support Level | Response Time |
|--------|-------------|---------------|
| Week 1–2 | On-site team (24×7) | < 15 min |
| Week 3–4 | On-site team (business hours) + remote (after hours) | < 30 min |
| Month 2–3 | Remote support (business hours) | < 1 hour |
| Month 4+ | Standard SLA support | < 4 hours |

---

*Implementation Plan v1.0 — MedCore HIS*
