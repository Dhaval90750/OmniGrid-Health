# 2. User Roles & Access Control

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 2.1 Access Control Model

MedCore HIS implements a **hybrid RBAC + ABAC** (Role-Based + Attribute-Based Access Control) model:

- **RBAC Layer:** Each user is assigned one or more roles. Roles define base permissions.
- **ABAC Layer:** Fine-grained rules based on attributes like department, ward, shift timing, IP address, and device type.
- **Context-Aware Access:** A doctor can only see patients assigned to them unless they have cross-department privileges.
- **Break-the-Glass:** In emergencies, authorized users can override access restrictions with mandatory audit logging and justification.

### 2.1.1 Permission Categories

| Category | Examples |
|----------|----------|
| **Patient Data** | View demographics, view EMR, edit clinical notes, view billing |
| **Clinical Actions** | Create prescription, order lab test, create diagnosis, approve discharge |
| **Administrative** | Create user, manage beds, manage wards, configure departments |
| **Financial** | Create invoice, process refund, submit insurance claim, view revenue reports |
| **System** | Configure workflows, manage integrations, view audit logs, manage templates |
| **Inventory** | View stock, create purchase order, approve indent, manage vendors |
| **Operations** | Manage housekeeping, manage maintenance, manage transport, manage dietary |

### 2.1.2 Data Sensitivity Levels

| Level | Description | Examples |
|-------|-------------|----------|
| **Public** | Non-sensitive operational data | Bed availability count, department list |
| **Internal** | Internal hospital data | Staff schedules, OT calendar |
| **Confidential** | Patient identifiable information | Name, contact, demographics |
| **Restricted** | Sensitive clinical data | HIV status, psychiatric records, MLC cases |
| **Top Secret** | System and financial data | Audit logs, revenue data, system configuration |

---

## 2.2 User Roles

### 2.2.1 Super Admin

**Description:** Top-level system administrator with unrestricted access. Typically the IT head or system vendor representative during deployment.

**Full Access To:**

| Area | Permissions |
|------|------------|
| Hospital Configuration | Create hospitals, manage branches, configure multi-tenancy |
| Department Management | Create/edit/delete departments, map department hierarchies |
| User Management | Create/edit/deactivate all users, assign roles, reset passwords |
| Role & Permission Config | Create custom roles, define permission sets, manage ABAC policies |
| Workflow Configuration | Design approval workflows, configure escalation chains |
| Template Management | Create/edit report templates, discharge templates, prescription templates |
| Master Data | Manage ICD codes, drug database, lab test catalog, procedure codes |
| System Integration | Configure HL7/FHIR endpoints, PACS integration, ABDM linking |
| Audit & Compliance | View all audit logs, generate compliance reports, data retention policies |
| Analytics | Full access to all dashboards, custom report builder |
| Inventory Config | Configure warehouses, manage vendor master, set reorder policies |
| Operations Config | Configure housekeeping zones, maintenance schedules, transport routes |

**Restrictions:** None.

**Session Policy:** Multi-factor authentication mandatory. Session timeout: 15 minutes of inactivity.

---

### 2.2.2 Hospital Admin

**Description:** Administrative head of a specific hospital or branch. Manages day-to-day hospital operations without touching system-level configurations.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Admissions | View/create/edit admissions, assign beds | — |
| Doctor Assignment | Assign consulting doctors, manage on-call rosters | — |
| Ward Management | Manage ward configurations, view occupancy | Delete wards with active patients |
| Bed Management | Allocate/transfer beds, manage bed categories | — |
| Staff Management | Create/edit staff profiles, manage shifts | Modify Super Admin accounts |
| Hospital Dashboards | View all operational dashboards | — |
| Inventory Oversight | Approve purchase orders, view stock dashboards | Modify vendor contracts |
| Operations Management | Oversee housekeeping, maintenance, dietary, transport | Modify system configurations |
| System Configuration | — | Modify system-level settings, integrations |
| Audit Logs | View hospital-level audit logs | Modify or delete audit entries |

**Session Policy:** MFA recommended. Session timeout: 30 minutes.

---

### 2.2.3 Doctor

**Description:** Licensed medical practitioner. Primary clinical user of the system. Interacts with patient records, creates clinical documentation, and drives the treatment lifecycle.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| QR Scan | Scan patient QR to pull up records | — |
| Patient History | View complete EMR for assigned patients | View unassigned patients (without break-the-glass) |
| Diagnosis | Create/edit primary and secondary diagnoses using ICD-10/11 | — |
| Prescriptions | Create/modify prescriptions with drug interaction checking | Dispense medication (Pharmacy role) |
| Progress Notes | Add daily progress notes, SOAP notes | — |
| Lab Orders | Order laboratory tests, view results, mark as reviewed | Process lab samples |
| Radiology Orders | Order imaging studies, view reports and images | Upload radiology images |
| Voice Dictation | Dictate clinical notes via AI speech-to-text | — |
| AI Clinical Notes | Generate and review AI-structured SOAP notes | Auto-publish without review |
| Referrals | Create inter-department and external referrals | — |
| Discharge | Approve discharge, sign discharge summary | Process billing |
| Surgery | Schedule surgeries, create operation notes, record implants | — |
| Consent Forms | Generate and record patient consent | — |
| Death Certificate | Generate and sign death certificates | — |
| Medico-Legal | Flag MLC cases, create MLC documentation | — |

**Special Features:**
- **Cross-Consultation:** Can be invited to view another doctor's patient for a second opinion.
- **Favorite Prescriptions:** Can save frequently used prescription templates.
- **Favorite Diagnoses:** Can save commonly used diagnosis sets.
- **Voice Notes:** All voice dictations are stored as audio + text for medico-legal purposes.

**Session Policy:** Biometric/PIN authentication on mobile. Session timeout: 60 minutes (configurable per hospital policy).

---

### 2.2.4 Nurse

**Description:** Registered nurse responsible for bedside patient care, vital monitoring, medication administration, and shift-based documentation.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| QR Scan | Scan patient QR for bedside identification | — |
| Vitals | Record temperature, pulse, BP, SPO2, respiratory rate, pain score, blood sugar, weight, height, BMI, intake/output charting | — |
| Medication Administration | Record medication given, refused, or held with timestamps | Modify prescription |
| Shift Notes | Create nursing shift handover notes | — |
| Nursing Assessment | Perform nursing assessments (falls risk, pressure ulcer risk, pain assessment) | — |
| Care Plans | Create/update nursing care plans | Modify doctor's treatment plan |
| Ward Monitoring | View ward dashboard, bed occupancy, patient list | — |
| Incident Reporting | Report adverse events, near-misses, falls | — |
| Emergency Escalation | Trigger code blue/red alerts, escalate to doctor | — |
| Wound Care | Document wound assessments with photos | — |
| IV Management | Record IV site changes, infusion rates | — |
| Restraint Documentation | Document patient restraint with justification and periodic review | Apply restraint without doctor's order |
| Diagnosis | — | Create or modify diagnosis |
| Discharge | — | Approve discharge |

**Special Features:**
- **Barcode Medication Verification:** Scan medication barcode + patient wristband QR to verify correct patient, correct drug, correct dose, correct time, correct route (5 Rights).
- **Automated Early Warning Score (EWS):** System calculates EWS from vitals and auto-escalates.

**Session Policy:** PIN + ward device authentication. Session timeout: 30 minutes.

---

### 2.2.5 Lab Technician

**Description:** Laboratory staff responsible for sample collection, processing, quality control, and result entry.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Sample Reception | Receive and barcode-label samples, verify patient identity via QR | — |
| Sample Processing | Log processing steps, record instrument readings | — |
| Result Entry | Enter test results, upload analyzer data (via LIS interface) | — |
| Quality Control | Run QC samples, log QC results, flag out-of-range QC | — |
| Abnormal Flagging | Mark critical/abnormal values with automatic alert triggering | — |
| Report Upload | Upload finalized lab reports (pending pathologist approval for certain tests) | — |
| Result Dispatch | Send results to ordering doctor electronically | — |
| Sample Rejection | Reject samples with documented reason (hemolyzed, insufficient, wrong container) | — |
| TAT Monitoring | View turnaround time dashboards | — |
| Reagent Management | Log reagent usage, record lot numbers, track expiry | Approve reagent purchase orders |
| Patient Records | — | View full EMR, modify diagnosis, prescriptions |

**Special Features:**
- **Analyzer Integration:** Direct data feed from lab analyzers (Beckman, Roche, Siemens, etc.) via HL7/ASTM protocols.
- **Delta Check:** System automatically compares current result with previous results and flags significant deviations.
- **Reflex Testing:** System auto-orders follow-up tests based on rules (e.g., if TSH abnormal → auto-order Free T3, Free T4).

---

### 2.2.6 Pathologist

**Description:** Senior laboratory physician who reviews, validates, and authorizes laboratory reports. Provides interpretive comments on complex tests.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Report Authorization | Review and authorize/reject lab reports | — |
| Interpretive Comments | Add clinical interpretation to reports | — |
| Histopathology | Create biopsy/histopathology reports with staging | — |
| Cytology | Create cytology reports (Pap smear, FNAC) | — |
| Quality Oversight | Review QC trends, approve corrective actions | — |
| Critical Value Alerts | Acknowledge critical value notifications | — |

---

### 2.2.7 Radiologist

**Description:** Specialist physician responsible for interpreting medical imaging studies and creating radiology reports.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Image Viewing | View DICOM images via integrated PACS viewer | — |
| Report Creation | Create radiology findings and impressions | — |
| Voice Reporting | Dictate findings using voice recognition (AI-assisted) | — |
| Template Reports | Use structured reporting templates (BI-RADS, PI-RADS, LI-RADS) | — |
| Critical Findings | Flag critical/unexpected findings with immediate notification to ordering doctor | — |
| Addendum | Add addendum to previously finalized reports | Delete finalized reports |
| Teaching Files | Mark interesting cases for teaching (de-identified) | — |
| Prior Studies | Access patient's prior imaging studies for comparison | — |

**Special Features:**
- **AI-Assisted Reading:** AI pre-reads images and highlights potential findings for radiologist review.
- **Structured Reporting:** Templates for standardized reporting (e.g., BI-RADS for breast imaging).
- **Peer Review:** Random case selection for peer review quality assurance.

---

### 2.2.8 Pharmacist

**Description:** Licensed pharmacist responsible for medication dispensing, drug information, stock management, and prescription verification.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Prescription Verification | Review and verify prescriptions before dispensing | Modify doctor's prescription |
| Medication Dispensing | Dispense medications, record batch numbers and expiry | — |
| Drug Interaction Check | Review system-flagged drug interactions, override with justification | — |
| Stock Management | Manage pharmacy inventory, record stock movements | Approve purchase orders above threshold |
| Controlled Substances | Dispense controlled substances with additional verification and logging | — |
| Drug Information | Provide drug information to clinical staff | — |
| Return Processing | Process medication returns from wards | — |
| Expiry Management | Monitor expiring medications, initiate returns/destruction | — |
| Formulary Management | Suggest additions/removals to hospital formulary | Approve formulary changes |
| Patient Counseling | Record medication counseling provided to patients | — |

**Special Features:**
- **Unit Dose System:** Support for unit-dose packaging and dispensing.
- **Automated Dispensing Cabinet (ADC) Integration:** Interface with Pyxis/Omnicell type systems.
- **Narcotic Register:** Digital narcotic register with double-sign-off.

---

### 2.2.9 Receptionist / Front Desk

**Description:** First point of contact for patients. Handles registration, appointment scheduling, and basic administrative tasks.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Patient Registration | Register new patients, generate UHID, print QR wristband | — |
| Demographics Update | Update patient demographics, contact info, emergency contacts | — |
| Appointment Scheduling | Schedule/reschedule/cancel OPD appointments | — |
| Admission Creation | Initiate admission process, assign ward/bed (or request) | Approve admission for restricted wards |
| Visitor Management | Issue visitor passes, track visitor log | — |
| Queue Management | Manage OPD queues, token generation | — |
| Insurance Verification | Verify insurance eligibility, capture policy details | Process claims |
| Document Collection | Scan and upload patient documents (ID proof, referral letters) | — |
| Clinical Data | — | View clinical notes, lab results, prescriptions |
| Billing | — | Create invoices (unless dual role assigned) |

---

### 2.2.10 Billing Executive

**Description:** Handles financial transactions including billing, payment collection, insurance claims, and refunds.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Invoice Generation | Create itemized bills for consultations, procedures, lab, pharmacy, room charges | — |
| Payment Collection | Record payments (cash, card, UPI, NEFT, cheque) | — |
| Insurance Claims | Submit pre-authorization requests, process insurance claims, handle rejections/resubmissions | Approve claim write-offs above threshold |
| TPA Processing | Interface with TPA portals, upload documents, track claim status | — |
| Interim Billing | Generate interim bills for IPD patients | — |
| Advance Collection | Collect and manage patient advance deposits | — |
| Refund Processing | Initiate refund requests (approval required above threshold) | Approve self-initiated refunds |
| Discharge Billing | Generate final discharge bill, process outstanding payments | — |
| Price List Management | View tariff/price list | Modify tariff |
| Package Billing | Apply package rates for surgeries/procedures | Create new packages |
| Credit Management | Manage credit limits for corporate/insurance patients | — |
| Financial Reports | View billing and collection reports | View P&L, balance sheet |

---

### 2.2.11 Inventory Manager

**Description:** Manages hospital-wide inventory including medical supplies, consumables, surgical items, linen, and general stores.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Stock Management | View/manage stock across all warehouses and sub-stores | — |
| Purchase Indents | Create/approve purchase indents within authority limit | Approve beyond authority limit |
| Purchase Orders | Create/approve purchase orders, manage vendor selection | — |
| Goods Receipt | Receive goods, perform quality inspection, update stock | — |
| Issue Management | Issue items to departments, manage inter-store transfers | — |
| Vendor Management | Create/edit vendor profiles, manage contracts, rate negotiations | — |
| Expiry Tracking | Monitor expiring items, initiate returns/disposal | — |
| Reorder Management | Configure reorder levels, manage auto-reorder rules | — |
| Asset Management | Track fixed assets, manage AMC/CMC contracts | Approve asset disposal |
| Consumption Reports | Generate consumption analytics, variance reports | — |
| Audit | Conduct physical stock audits, reconcile variances | — |

---

### 2.2.12 Operations Manager

**Description:** Manages non-clinical hospital operations including housekeeping, maintenance, transport, dietary services, laundry, and biomedical waste management.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Housekeeping | Assign cleaning tasks, track room turnaround, manage schedules | — |
| Maintenance | Create/assign work orders, track equipment maintenance, manage preventive maintenance schedules | Approve capital expenditure |
| Transport | Manage ambulance fleet, patient transport scheduling, driver assignment | — |
| Dietary Services | Manage diet charts (in coordination with dietitians), meal scheduling, kitchen operations | Modify clinical diet orders |
| Laundry | Track linen inventory, laundry schedules, soiled linen management | — |
| Biomedical Waste | Track waste generation, segregation compliance, disposal records | — |
| Security | Manage CCTV access, security staff scheduling, incident logging | — |
| Energy Management | Monitor utility consumption, manage energy efficiency programs | — |
| Space Management | Track room/space utilization, manage renovation projects | — |
| Vendor Coordination | Coordinate with outsourced service vendors, manage SLAs | — |
| Staff Scheduling | Create/manage operations staff rosters and shifts | — |
| Reports | Generate operations dashboards and KPI reports | — |

---

### 2.2.13 Dietitian / Nutritionist

**Description:** Clinical nutrition specialist who creates and manages patient diet plans.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Nutrition Assessment | Perform nutritional screening and assessment | — |
| Diet Plans | Create/modify diet plans for inpatients | — |
| Special Diets | Configure special diet menus (diabetic, renal, cardiac, etc.) | — |
| Enteral/Parenteral | Manage enteral and parenteral nutrition orders | — |
| Food Allergy | Document and alert on food allergies/intolerances | — |
| Kitchen Coordination | Communicate diet orders to kitchen/dietary services | — |
| Calorie Tracking | Monitor calorie/protein intake for critical patients | — |

---

### 2.2.14 Management / CXO

**Description:** Hospital leadership (CEO, COO, CFO, CMO, CNO) with read-only access to dashboards and analytics.

**Permissions:**

| Area | Can Do | Cannot Do |
|------|--------|-----------|
| Executive Dashboards | View hospital-wide KPIs, financial summaries, occupancy trends | — |
| Financial Reports | View revenue, collections, outstanding, P&L, budget vs actual | Modify financial data |
| Clinical Quality | View infection rates, mortality rates, readmission rates, LOS trends | — |
| Operational KPIs | View OT utilization, bed turnover, TAT metrics | — |
| Staff Analytics | View staff productivity, attendance, overtime | Modify staff records |
| Patient Satisfaction | View patient feedback scores and trends | — |
| Comparative Analytics | Benchmark across departments, doctors, time periods | — |
| Custom Reports | Build custom reports using report builder | — |
| Data Export | Export reports in PDF, Excel, CSV formats | Export raw patient data |
| Clinical Data | — | View individual patient clinical records |
| Transactional | — | Create/modify any transactional data |

---

## 2.3 Role Permission Matrix (Summary)

| Module | Super Admin | Hospital Admin | Doctor | Nurse | Lab Tech | Radiologist | Pharmacist | Receptionist | Billing | Inventory Mgr | Operations Mgr | Management |
|--------|:-----------:|:--------------:|:------:|:-----:|:--------:|:-----------:|:----------:|:------------:|:-------:|:--------------:|:---------------:|:----------:|
| Patient Registration | ✅ | ✅ | 👁️ | 👁️ | ❌ | ❌ | ❌ | ✅ | 👁️ | ❌ | ❌ | ❌ |
| Admission/ADT | ✅ | ✅ | 👁️ | 👁️ | ❌ | ❌ | ❌ | ✅ | 👁️ | ❌ | ❌ | 📊 |
| Clinical Notes | ✅ | ❌ | ✅ | 👁️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Prescriptions | ✅ | ❌ | ✅ | 👁️ | ❌ | ❌ | 👁️ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Lab Orders/Results | ✅ | 👁️ | ✅ | 👁️ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | 📊 |
| Radiology | ✅ | 👁️ | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | 📊 |
| Pharmacy | ✅ | 👁️ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | 📊 |
| Billing | ✅ | 👁️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | 📊 |
| Inventory | ✅ | 👁️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | 👁️ | 📊 |
| Operations | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | 📊 |
| Bed Management | ✅ | ✅ | 👁️ | 👁️ | ❌ | ❌ | ❌ | 👁️ | ❌ | ❌ | 👁️ | 📊 |
| Dashboards | ✅ | ✅ | 👁️ | 👁️ | 👁️ | 👁️ | 👁️ | ❌ | 👁️ | 👁️ | 👁️ | ✅ |
| Audit Logs | ✅ | 👁️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | 👁️ |
| System Config | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |

**Legend:** ✅ Full Access | 👁️ Read Only | ❌ No Access | 📊 Dashboard/Analytics Only

---

## 2.4 Authentication & Security Policies

### 2.4.1 Authentication Methods

| Method | Used By | Context |
|--------|---------|---------|
| Username + Password | All users | Web login |
| PIN | Nurses, Technicians | Quick ward device login |
| Biometric (Fingerprint) | Doctors, Nurses | Mobile app, controlled substance dispensing |
| Smart Card / Proximity Card | All clinical staff | Workstation login in clinical areas |
| Multi-Factor Authentication (MFA) | Admins, Management | High-security operations |
| Single Sign-On (SSO) | All users | Corporate LDAP/Active Directory integration |

### 2.4.2 Session Management

| Policy | Value |
|--------|-------|
| Maximum concurrent sessions per user | 2 |
| Session timeout (Admin) | 15 minutes |
| Session timeout (Clinical) | 60 minutes |
| Session timeout (Front Desk) | 30 minutes |
| Password expiry | 90 days |
| Password complexity | Min 8 chars, uppercase, lowercase, number, special character |
| Failed login lockout | 5 attempts → 30-minute lock |
| Account deactivation | Auto-deactivate after 90 days of inactivity |

### 2.4.3 Break-the-Glass Protocol

For emergency situations where a clinician needs access to a patient's records outside their normal scope:

1. User requests emergency access
2. System prompts for justification (dropdown + free text)
3. Access granted for 60 minutes (configurable)
4. Full audit log entry created
5. Automatic notification sent to:
   - Patient's attending physician
   - Department head
   - Compliance officer
6. Weekly review report generated for compliance team

---

[→ Next: Patient Lifecycle](./03_Patient_Lifecycle.md)
