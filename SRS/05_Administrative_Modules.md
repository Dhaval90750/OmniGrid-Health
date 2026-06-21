# 5. Functional Requirements — Administrative & Support Modules

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 5.1 Billing & Revenue Cycle Management

### 5.1.1 Billing Types

| Type | Description | Trigger |
|------|-------------|---------|
| **OPD Billing** | Consultation charges, procedure charges | At registration or after consultation |
| **IPD Interim Billing** | Periodic bills during stay | Daily or on-demand |
| **IPD Final/Discharge Billing** | Final consolidated bill at discharge | At discharge |
| **Pharmacy Billing** | Medication charges | At dispensing |
| **Lab Billing** | Investigation charges | At order or result dispatch |
| **Radiology Billing** | Imaging charges | At order or study completion |
| **OT Billing** | Surgery charges, implant charges | Post-surgery |
| **Package Billing** | Pre-defined bundled charges (e.g., maternity package, knee replacement package) | At admission or surgery booking |
| **Emergency Billing** | Emergency room charges | At ER visit |
| **Day Care Billing** | Same-day procedure billing | At procedure |

### 5.1.2 Charge Categories

| Category | Examples |
|----------|---------|
| **Registration Fee** | OPD registration, ER registration |
| **Consultation Fee** | Doctor consultation (varies by doctor seniority/specialty) |
| **Room Charges** | General ward, semi-private, private, suite, ICU (per day) |
| **Nursing Charges** | Nursing care per day (based on ward type) |
| **Lab Charges** | Per test/panel, varies by test complexity |
| **Radiology Charges** | Per study, varies by modality |
| **Procedure Charges** | Minor/major procedures, surgeon fee |
| **OT Charges** | OT usage charge (per hour/per procedure) |
| **Anesthesia Charges** | Based on ASA grade and duration |
| **Medication Charges** | Drug cost + dispensing fee |
| **Consumable Charges** | Surgical consumables, dressings, IV sets |
| **Implant Charges** | Implant cost (tracked by serial number) |
| **Blood/Component Charges** | Blood product charges |
| **Diet Charges** | Meal charges (if separate) |
| **Ambulance Charges** | Transport charges |
| **Miscellaneous** | Documentation charges, medical certificate, etc. |

### 5.1.3 Tariff Management

- **Multi-Tariff Support:** Different price lists for:
  - Cash patients
  - Insurance/TPA patients (per TPA agreement)
  - Corporate patients (per MOU)
  - Government scheme patients (PMJAY, state schemes)
  - Staff/employee patients
  - International patients
- **Tariff Versioning:** Maintain history of tariff changes with effective dates
- **Auto-Tariff Selection:** System auto-selects tariff based on patient's payment category
- **Package Rates:** Bundled pricing for common procedures
  - Inclusive packages (all charges bundled)
  - Exclusive packages (some items excluded, billed separately)
  - Day-wise breakup within package

### 5.1.4 Invoice Structure

```
┌─────────────────────────────────────────────────────────────┐
│              HOSPITAL NAME & LOGO                           │
│              Address, Phone, GSTIN, Registration No.        │
├─────────────────────────────────────────────────────────────┤
│  Bill No: INV-2026-001234      Date: 21-Jun-2026           │
│  UHID: HOS-2026-0001234       Adm No: ADM-2026-005678     │
│  Patient: Rajesh Kumar         Age/Sex: 41Y/M              │
│  Doctor: Dr. Mehta             Dept: General Medicine       │
│  Room: Private-201             Stay: 5 Days                 │
│  Payment Mode: Insurance       TPA: Star Health             │
├─────────────────────────────────────────────────────────────┤
│  #  │ Service              │ Qty │ Rate    │ Amount        │
│─────┼──────────────────────┼─────┼─────────┼───────────────│
│  1  │ Room Charges (Pvt)   │  5  │ 3,500   │ 17,500        │
│  2  │ Nursing Charges      │  5  │ 1,000   │  5,000        │
│  3  │ Dr. Consultation     │  5  │   800   │  4,000        │
│  4  │ Medications          │  -  │    -    │  8,250        │
│  5  │ Lab Investigations   │  -  │    -    │  6,500        │
│  6  │ Radiology (CXR × 2) │  2  │   500   │  1,000        │
│  7  │ IV Fluids & Consume. │  -  │    -    │  3,200        │
├─────┼──────────────────────┼─────┼─────────┼───────────────│
│     │ Gross Total          │     │         │ 45,450        │
│     │ Discount (10%)       │     │         │ (4,545)       │
│     │ Net Total            │     │         │ 40,905        │
│     │ GST (if applicable)  │     │         │     -         │
│     │ Advance Paid         │     │         │(10,000)       │
│     │ Insurance Approved   │     │         │(25,000)       │
│     │ BALANCE DUE          │     │         │  5,905        │
├─────────────────────────────────────────────────────────────┤
│  Payment: ☐ Cash  ☐ Card  ☐ UPI  ☐ NEFT  ☐ Cheque        │
└─────────────────────────────────────────────────────────────┘
```

### 5.1.5 Payment Processing

| Method | Details |
|--------|---------|
| **Cash** | Receipt generated with amount, denomination capture optional |
| **Card** | POS integration, card type (debit/credit), last 4 digits, approval code |
| **UPI** | QR code display, UPI transaction ID recorded |
| **NEFT/RTGS** | Bank reference number, auto-reconciliation |
| **Cheque** | Cheque number, bank, date, clearance tracking |
| **Online Payment** | Payment gateway integration for advance/deposit |
| **Wallet** | Hospital wallet/advance deposit balance |
| **Multi-Mode** | Split payment across multiple modes |

### 5.1.6 Financial Reports

| Report | Description |
|--------|-------------|
| **Daily Collection Report** | Total collections by payment mode |
| **Outstanding Report** | Pending payments by patient, TPA, corporate |
| **Revenue Report** | Revenue by department, doctor, service type |
| **Discount Report** | All discounts given with approval details |
| **Refund Report** | All refunds processed |
| **Advance Report** | Advance deposits collected and utilized |
| **GST Report** | Tax collected and payable |
| **Cash Handover** | Shift-wise cash handover summary |
| **MIS Report** | Management information summary |
| **Aging Report** | Outstanding receivables by aging bucket (30/60/90/120+ days) |

---

## 5.2 Insurance & TPA Management

### 5.2.1 Insurance Workflow

```
Patient Has Insurance
       │
  ┌────▼───────────┐
  │ Eligibility     │ → Verify policy active, coverage limits
  │ Verification    │    (Online/phone/portal)
  └────┬───────────┘
       │
  ┌────▼───────────┐
  │ Pre-Auth        │ → Submit pre-authorization with
  │ Request         │    clinical details, estimated cost
  └────┬───────────┘
       │
  ┌────▼───────────┐
  │ TPA Review      │ → TPA approves / queries / denies
  └────┬───────────┘
       │
  ┌────▼──────────────────────────┐
  │ Query?                        │
  │ ├── YES → Provide additional  │
  │ │         info, re-submit     │
  │ └── NO → Proceed             │
  └────┬──────────────────────────┘
       │
  ┌────▼───────────┐
  │ Approved Amount │ → Record approved amount
  └────┬───────────┘
       │
  ┌────▼───────────────┐
  │ Enhancement         │ → If treatment cost exceeds approved
  │ (if needed)         │    amount, submit enhancement request
  └────┬───────────────┘
       │
  ┌────▼───────────┐
  │ Final Bill      │ → Generate final bill with insurance split
  └────┬───────────┘
       │
  ┌────▼───────────┐
  │ Claim           │ → Submit final claim with all documents
  │ Submission      │
  └────┬───────────┘
       │
  ┌────▼───────────┐
  │ Settlement      │ → TPA settles claim → Payment received
  │ Tracking        │    Track receivables
  └───────────────┘
```

### 5.2.2 Insurance Data Model

| Field | Description |
|-------|-------------|
| Insurance Company | Name of insurer |
| TPA | Third Party Administrator |
| Policy Number | Insurance policy number |
| Group/Corporate Name | Employer name (for group policies) |
| Sum Insured | Maximum coverage amount |
| Policy Start/End | Validity period |
| Patient Relationship | Self / Spouse / Child / Parent |
| Card Photo | Upload of insurance card (front + back) |
| Network Type | Cashless / Reimbursement |
| Co-Pay Percentage | Patient's co-pay obligation |
| Room Category Limit | Room charges eligible under policy |
| Sub-Limits | Disease-wise or procedure-wise caps |
| Exclusions | Pre-existing disease waiting period, specific exclusions |

### 5.2.3 Government Health Schemes

| Scheme | Description |
|--------|-------------|
| **PMJAY (Ayushman Bharat)** | ₹5 lakh/family/year for secondary & tertiary care. Requires ABHA ID linking, HBP package code mapping, real-time pre-auth via ABDM portal |
| **CGHS** | Central Government Health Scheme — government employee insurance |
| **ESI** | Employee State Insurance — for organized sector employees |
| **State Schemes** | Rajiv Gandhi Jeevandayee, Aarogyasri, Mahatma Jyotirao Phule, etc. |

### 5.2.4 Claim Documents Checklist

| Document | Required |
|----------|----------|
| Pre-authorization approval letter | ✅ |
| Discharge summary | ✅ |
| Final bill (itemized) | ✅ |
| Lab reports | ✅ |
| Radiology reports | ✅ (if done) |
| Prescription copies | ✅ |
| OT notes (if surgery) | ✅ (if done) |
| Implant invoice & sticker | ✅ (if implant used) |
| Doctor's certificate | ✅ |
| Patient KYC (ID proof + photo) | ✅ |
| Insurance card copy | ✅ |
| Claim form (signed) | ✅ |
| MLC documents | ✅ (if applicable) |

---

## 5.3 Bed Management

### 5.3.1 Bed Categories

| Category | Description | Typical Features |
|----------|-------------|-----------------|
| **General Ward** | Multi-bed open ward (4–20 beds) | Basic amenities, shared bathroom |
| **Semi-Private** | 2–3 beds per room | Shared room, curtain partition |
| **Private Room** | Single occupancy | Private bathroom, TV, attendant bed |
| **Deluxe / Suite** | Premium single room | Living area, premium amenities |
| **ICU** | Intensive care bed | Monitors, ventilator point, central gas |
| **NICU** | Neonatal intensive care | Warmer/incubator, neonatal monitors |
| **PICU** | Pediatric intensive care | Pediatric monitors |
| **HDU** | High Dependency Unit | Step-down from ICU |
| **Isolation** | Negative/positive pressure | Special ventilation |
| **Labor Room** | Obstetric delivery | Delivery table, fetal monitor |
| **Day Care** | Same-day procedure bed | 4–8 hour use |
| **Emergency** | ER beds/stretchers | Resuscitation equipment |
| **Burn Ward** | Burn patient care | Special dressing facilities |
| **Psychiatric Ward** | Mental health | Safety features |

### 5.3.2 Bed States

```
┌──────────┐      ┌───────────┐      ┌────────────┐
│ Available │─────▶│ Occupied  │─────▶│ Discharged │
│  (Green)  │      │  (Red)    │      │  (Yellow)  │
└──────────┘      └───────────┘      └─────┬──────┘
     ▲                                      │
     │                                      │
     │            ┌────────────┐            │
     └────────────│ Cleaned    │◀───────────┘
                  │ & Ready    │   Housekeeping
                  │  (Green)   │   cleans room
                  └────────────┘
```

| State | Color | Description |
|-------|-------|-------------|
| Available | 🟢 Green | Clean, ready for new patient |
| Occupied | 🔴 Red | Patient currently admitted |
| Reserved | 🟠 Orange | Booked for incoming admission |
| Under Maintenance | 🔵 Blue | Equipment repair, deep cleaning |
| Discharged - Cleaning | 🟡 Yellow | Patient left, housekeeping notified |
| Blocked | ⚫ Black | Temporarily blocked (e.g., construction, quarantine) |

### 5.3.3 Real-Time Bed Dashboard

**Visual Floor Plan View:**
- Floor-wise, ward-wise graphical layout
- Color-coded bed icons showing status
- Click on bed to see patient details (for authorized users)
- Quick filters: Available, ICU, Isolation, Ventilator

**Summary View:**

```
┌──────────────────────────────────────────────────────────────┐
│  BED MANAGEMENT DASHBOARD              Date: 21-Jun-2026    │
├──────────────────────────────────────────────────────────────┤
│  Total Beds: 500 │ Occupied: 412 │ Available: 68 │ Maint: 20│
│  Occupancy Rate: 82.4%                                       │
├──────────────────────────────────────────────────────────────┤
│  Ward         │ Total │ Occupied │ Available │ Rate          │
│───────────────┼───────┼──────────┼───────────┼───────────────│
│  General Med  │   60  │    52    │     6     │  86.7%        │
│  General Surg │   50  │    43    │     5     │  86.0%        │
│  Orthopedics  │   30  │    25    │     4     │  83.3%        │
│  Cardiology   │   20  │    18    │     1     │  90.0%        │
│  Pediatrics   │   25  │    18    │     5     │  72.0%        │
│  Obstetrics   │   30  │    24    │     4     │  80.0%        │
│  ICU (MICU)   │   20  │    18    │     2     │  90.0%        │
│  ICU (SICU)   │   15  │    14    │     1     │  93.3%        │
│  ICU (CCU)    │   10  │     9    │     1     │  90.0%        │
│  NICU         │   15  │    12    │     3     │  80.0%        │
│  Private      │   80  │    65    │    12     │  81.3%        │
│  Suite        │   20  │    15    │     4     │  75.0%        │
│  Isolation    │   10  │     8    │     2     │  80.0%        │
│  Day Care     │   25  │    18    │     7     │  72.0%        │
│  Emergency    │   30  │    25    │     5     │  83.3%        │
│  Others       │   60  │    48    │     8     │  80.0%        │
├──────────────────────────────────────────────────────────────┤
│  Ventilator Beds: 25 (Used: 20, Available: 5)               │
│  Upcoming Discharges Today: 35                               │
│  Pending Admissions: 22                                      │
└──────────────────────────────────────────────────────────────┘
```

### 5.3.4 Bed Transfer

| Field | Description |
|-------|-------------|
| Transfer Reason | Clinical upgrade, clinical downgrade, patient request, bed issue, isolation requirement |
| Transfer From | Current ward/bed |
| Transfer To | New ward/bed (only available beds shown) |
| Transfer Date/Time | Timestamp |
| Authorized By | Doctor name |
| Billing Impact | Auto-adjusts room charges from transfer time |

---

## 5.4 Pharmacy Module

### 5.4.1 Pharmacy Organization

```
PHARMACY
├── Inpatient Pharmacy
│   └── Floor-wise sub-pharmacies (optional)
├── Outpatient Pharmacy
├── Emergency Pharmacy (24×7)
├── OT Pharmacy / Surgical Stores
├── Narcotic & Controlled Substance Store
└── Central Drug Store (Warehouse)
```

### 5.4.2 Drug Master Data

| Field | Description |
|-------|-------------|
| Generic Name | INN (International Non-proprietary Name) |
| Brand Name(s) | Multiple brands per generic |
| Drug Class | Therapeutic classification (ATC code) |
| Dosage Form | Tablet, Capsule, Syrup, Injection, Cream, etc. |
| Strength | 250mg, 500mg, 1g, etc. |
| Unit of Issue | Strip, Bottle, Vial, Ampoule, Tube |
| Pack Size | Tablets per strip, mL per bottle, etc. |
| Manufacturer | Pharmaceutical company |
| HSN Code | Harmonized System Nomenclature (for GST) |
| GST Rate | 5%, 12%, 18%, etc. |
| MRP | Maximum Retail Price |
| Hospital Price | Hospital's purchase price |
| Selling Price | Price charged to patient |
| Schedule | H, H1, X, OTC |
| Storage | Room temperature, Refrigerated (2-8°C), Frozen (-20°C) |
| Narcotic Flag | Yes/No (for additional tracking) |
| High-Alert Flag | Yes/No (ISMP high-alert medications) |
| Look-Alike/Sound-Alike | LASA flag with similar drug names |
| Formulary Status | On formulary / Non-formulary / Restricted |
| Reorder Level | Minimum stock threshold |
| Maximum Level | Maximum stock limit |
| Lead Time | Typical procurement lead time (days) |

### 5.4.3 Pharmacy Workflow (IPD)

```
Doctor Prescribes (Electronic)
          │
     ┌────▼──────────┐
     │ Prescription   │ → Prescription appears in Pharmacy Queue
     │ Received       │
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Pharmacist     │ → Verify drug, dose, frequency, interactions
     │ Review         │
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Prepare &      │ → Pick medication, label, unit dose packaging
     │ Label          │
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Issue to Ward  │ → Record batch, expiry, quantity issued
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Nurse Receives │ → Verify at ward, store in medication cart
     │ & Verifies     │
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Administer     │ → 5 Rights check (Right Patient, Drug,
     │ to Patient     │    Dose, Route, Time)
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Auto-Billed    │ → Charges added to patient's bill
     └──────────────┘
```

### 5.4.4 Stock Management

| Feature | Description |
|---------|-------------|
| **Real-Time Stock** | Current stock by drug, batch, expiry across all pharmacy locations |
| **Batch Tracking** | Every dispensation linked to batch number |
| **Expiry Management** | FEFO (First Expiry, First Out) enforcement, 3/6 month expiry alerts |
| **Auto-Reorder** | System generates indent when stock hits reorder level |
| **Inter-Pharmacy Transfer** | Transfer stock between pharmacy locations |
| **Return Management** | Patient returns, ward returns, vendor returns |
| **Wastage/Damage** | Log damaged/expired stock with disposal documentation |
| **ABC Analysis** | Categorize drugs by consumption value (A=high, B=medium, C=low) |
| **VED Analysis** | Vital, Essential, Desirable classification |
| **Dead Stock** | Identify and report non-moving items |

### 5.4.5 Controlled Substance Management

- **Digital Narcotic Register:** All receipts, issues, and balances logged
- **Double Verification:** Two authorized persons verify narcotic dispensing
- **Shift Handover:** Narcotic count at every shift change
- **Reconciliation:** Physical count vs system count daily
- **Audit Trail:** Immutable log of all narcotic transactions
- **Regulatory Reporting:** Generate reports required by NDPS Act

---

## 5.5 Emergency / Casualty Module

### 5.5.1 ER Dashboard

Real-time display showing:

| Element | Description |
|---------|-------------|
| **Incoming Patients** | New arrivals list with triage level |
| **Active Patients** | Patients currently in ER with status |
| **Waiting Patients** | Triaged but waiting for doctor |
| **Under Treatment** | Being examined or treated |
| **Pending Admission** | Disposition decided, waiting for bed |
| **Pending Discharge** | Ready for ER discharge |
| **Bed Status** | Available ER beds/stretchers/resuscitation bays |
| **Average Wait Time** | Current average wait by triage level |
| **Turn-Around Time** | Average ER stay duration |

### 5.5.2 Ambulance Integration

| Feature | Description |
|---------|-------------|
| **Fleet Tracking** | GPS location of all ambulances |
| **Dispatch** | Assign nearest available ambulance |
| **Pre-Alert** | Crew sends patient info before arrival |
| **ETA Display** | Estimated time of arrival on ER dashboard |
| **Run Sheet** | Digital ambulance run sheet (pick-up, vitals en-route, interventions) |

---

## 5.6 Document Management System (DMS)

### 5.6.1 Document Types

| Category | Documents |
|----------|-----------|
| **Patient Documents** | ID proof, insurance card, referral letters, old reports, consent forms |
| **Clinical Documents** | Lab reports, radiology images, discharge summaries, certificates |
| **Administrative Documents** | Policies, SOPs, circulars, meeting minutes |
| **HR Documents** | Staff credentials, licenses, certificates |
| **Legal Documents** | MLC reports, death certificates, birth certificates |
| **Financial Documents** | Invoices, claim documents, audit reports |

### 5.6.2 Features

- **Scanning:** Integrate with document scanners for paper digitization
- **OCR:** Optical Character Recognition for searchable scanned documents
- **Categorization:** Auto-categorize by document type
- **Versioning:** Track document versions
- **Access Control:** Document-level permissions
- **Retention:** Automated retention policies
- **Search:** Full-text search across all documents
- **Digital Signatures:** Sign documents digitally with timestamp
- **Watermarking:** Automatic watermarks on printed documents ("COPY", "CONFIDENTIAL")

---

## 5.7 Notification & Alerts Engine

### 5.7.1 Alert Categories

| Category | Priority | Examples |
|----------|----------|---------|
| **Clinical Critical** | 🔴 Immediate | Critical lab value, ICU deterioration, allergy alert, code blue |
| **Clinical Urgent** | 🟠 High | Abnormal lab value, overdue medication, pending results |
| **Clinical Routine** | 🟡 Medium | Lab results ready, follow-up due, diet order change |
| **Administrative** | 🔵 Normal | Bed ready, discharge pending, admission approval needed |
| **System** | ⚪ Low | Maintenance window, password expiry, license renewal |

### 5.7.2 Delivery Channels

| Channel | Used For | Latency |
|---------|----------|---------|
| **In-App Push** | All alerts (primary channel) | Real-time |
| **SMS** | Critical alerts, appointment reminders, OTP | < 10 seconds |
| **WhatsApp** | Appointment reminders, follow-up, patient communication | < 30 seconds |
| **Email** | Reports, summaries, non-urgent notifications | < 5 minutes |
| **Audio Alert** | Code blue, fire alarm, critical vitals | Immediate |
| **Phone Call** | Critical lab value communication (lab → doctor) | Immediate |
| **Dashboard Pop-Up** | Active alerts on user's dashboard | Real-time |
| **Pager** | Legacy support for some hospitals | < 30 seconds |

### 5.7.3 Escalation Matrix

```
Alert Triggered
      │
  ┌───▼────┐
  │ Level 1 │ → Assigned doctor/nurse
  │ (0 min) │
  └───┬────┘
      │ Not acknowledged in X minutes
  ┌───▼────┐
  │ Level 2 │ → Department head / Shift in-charge
  │ (15 min)│
  └───┬────┘
      │ Not acknowledged in X minutes
  ┌───▼────┐
  │ Level 3 │ → Hospital admin / Medical superintendent
  │ (30 min)│
  └───┬────┘
      │ Not acknowledged in X minutes
  ┌───▼────┐
  │ Level 4 │ → CEO notification + Incident log
  │ (60 min)│
  └────────┘
```

---

## 5.8 Audit Trail & Compliance

### 5.8.1 Audit Log Fields

Every action in the system is logged with:

| Field | Description |
|-------|-------------|
| **Timestamp** | ISO 8601 format with timezone |
| **User ID** | Who performed the action |
| **User Role** | Role at time of action |
| **Action Type** | CREATE, READ, UPDATE, DELETE, PRINT, EXPORT, LOGIN, LOGOUT |
| **Module** | Which module (Registration, Lab, Pharmacy, etc.) |
| **Entity** | What was affected (Patient ID, Report ID, etc.) |
| **Before Value** | Previous state (for updates) |
| **After Value** | New state (for updates) |
| **IP Address** | Client IP address |
| **Device** | Device type and identifier |
| **Location** | Ward/department where action was performed |
| **Session ID** | For traceability across actions |
| **Justification** | Required for break-the-glass and overrides |

### 5.8.2 Audit Reports

| Report | Frequency | Used By |
|--------|-----------|---------|
| **User Activity Log** | On-demand | IT Admin, Compliance |
| **Patient Record Access Log** | On-demand | Compliance, Legal |
| **Break-the-Glass Report** | Weekly | Compliance Committee |
| **Failed Login Report** | Daily | IT Security |
| **Data Export Report** | Monthly | Data Privacy Officer |
| **Prescription Override Report** | Weekly | Clinical Quality |
| **Critical Value Acknowledgment** | Daily | Lab Manager |
| **Consent Audit** | Monthly | Legal |

### 5.8.3 Compliance Requirements

| Standard | Requirements Covered |
|----------|---------------------|
| **NABH** | All clinical workflows, documentation, patient safety, infection control |
| **HIPAA** | Data encryption, access controls, audit logs, breach notification |
| **DISHA (India)** | Digital health data privacy, patient consent management |
| **ABDM** | ABHA integration, health record sharing, M1/M2/M3 milestone compliance |
| **ISO 27001** | Information security management system |
| **IEC 62304** | Medical device software lifecycle (if applicable) |

---

## 5.9 Medical Records Department (MRD)

### 5.9.1 MRD Functions

| Function | Description |
|----------|-------------|
| **Record Indexing** | Assign ICD codes to all diagnoses and procedures |
| **Record Completion** | Ensure all discharge summaries are complete and signed |
| **Deficiency Tracking** | Track incomplete records and chase completion |
| **Medical Certificates** | Issue medical certificates, fitness certificates, death certificates |
| **Medico-Legal Records** | Manage MLC case documentation |
| **Birth/Death Registration** | Register births and deaths with government authorities |
| **Statistics** | Morbidity and mortality statistics |
| **Record Retrieval** | Fulfill record requests from doctors, legal, insurance |
| **Coding Quality** | Audit coding accuracy (ICD, CPT) |

### 5.9.2 Coding Standards

| Standard | Used For |
|----------|---------|
| **ICD-10-CM** | Diagnosis coding |
| **ICD-10-PCS** | Procedure coding (inpatient) |
| **ICD-11** | Next-gen diagnosis coding (being adopted) |
| **CPT** | Procedure coding (outpatient) |
| **SNOMED CT** | Clinical terminology (interoperability) |
| **LOINC** | Lab test identification |
| **ATC** | Drug classification |
| **NABH Indicators** | Quality indicators as per NABH standards |

---

[→ Next: Inventory Management](./06_Inventory_Management.md)
