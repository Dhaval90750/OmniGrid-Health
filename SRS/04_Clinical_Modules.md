# 4. Functional Requirements — Clinical Modules

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 4.1 Electronic Medical Record (EMR)

### 4.1.1 Purpose

The EMR is the **lifelong digital health record** of every patient. It aggregates all clinical data across all visits, admissions, and departments into a single, unified, chronological medical record.

### 4.1.2 EMR Data Model

```
┌─────────────────────────────────────────────────────────────┐
│                     PATIENT EMR                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Demographics │  │  Allergies  │  │  Chronic    │         │
│  │              │  │  & Adverse  │  │  Conditions │         │
│  │              │  │  Reactions  │  │             │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  Family     │  │  Social     │  │  Immunization│         │
│  │  History    │  │  History    │  │  Record      │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                              │
│  ┌─────────────────────────────────────────────────┐        │
│  │              VISIT HISTORY                       │        │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐   │        │
│  │  │Visit 1 │ │Visit 2 │ │Visit 3 │ │Visit N │   │        │
│  │  │(OPD)   │ │(IPD)   │ │(OPD)   │ │(ER)    │   │        │
│  │  └───┬────┘ └───┬────┘ └───┬────┘ └───┬────┘   │        │
│  │      │          │          │          │          │        │
│  │  Each visit contains:                            │        │
│  │  • Clinical Notes    • Lab Results               │        │
│  │  • Prescriptions     • Radiology Reports         │        │
│  │  • Procedures        • Nursing Records           │        │
│  │  • Discharge Summary • Billing                   │        │
│  └─────────────────────────────────────────────────┘        │
│                                                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  Medication │  │  Surgery    │  │  Document   │         │
│  │  History    │  │  History    │  │  Repository │         │
│  │  (All time) │  │             │  │  (Scans,    │         │
│  │             │  │             │  │   Reports)  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### 4.1.3 Data Sections

#### Demographics
- All registration data (see Section 3.2)
- Photo with date stamps
- Multiple address support (permanent, current, office)
- Multiple contact numbers
- Legal guardian details (for minors)

#### Allergies & Adverse Reactions

| Field | Type | Required |
|-------|------|----------|
| Allergen | Searchable (drug/food/environmental) | ✅ |
| Allergen Category | Drug / Food / Environmental / Latex / Contrast / Other | ✅ |
| Reaction Type | Allergy / Adverse Drug Reaction / Intolerance | ✅ |
| Severity | Mild / Moderate / Severe / Life-Threatening | ✅ |
| Reaction Description | Text | ✅ |
| Onset Date | Date | ❌ |
| Verified By | Doctor name | ✅ |
| Status | Active / Inactive / Resolved | ✅ |

**System Behavior:**
- Allergies displayed as banner alerts on every clinical screen
- Drug allergy auto-checked during prescription entry
- Cross-reactivity checking (e.g., Penicillin allergy → warn for Amoxicillin, Ampicillin)
- Food allergies communicated to dietary module

#### Chronic Disease Registry

| Field | Type |
|-------|------|
| Condition | ICD-10 coded |
| Date of Diagnosis | Date |
| Current Status | Active / In Remission / Resolved |
| Current Medications | Linked to prescription module |
| Last Review Date | Date |
| Managing Doctor | Doctor reference |
| Target Values | Configurable (e.g., HbA1c < 7% for diabetics) |

**Supported Chronic Conditions (examples):**
- Diabetes Mellitus (Type 1, Type 2, Gestational)
- Hypertension
- Chronic Kidney Disease (with staging)
- Coronary Artery Disease
- Chronic Obstructive Pulmonary Disease
- Asthma
- Epilepsy
- Thyroid disorders
- Chronic Liver Disease
- HIV/AIDS (restricted access)
- Cancer (with staging)
- Rheumatoid Arthritis
- Systemic Lupus Erythematosus

#### Family History

- Structured entry for first-degree relatives
- Conditions: Diabetes, Heart Disease, Cancer (type), Stroke, Hypertension, Mental Illness, Genetic Disorders
- Pedigree chart generation (optional)

#### Immunization Record

- Vaccine name, date, batch number, site, administrator
- Childhood immunization schedule tracking
- Adult immunizations (Flu, COVID, Hepatitis B, etc.)
- Travel immunizations
- Integration with government immunization registries (CoWIN for India)

#### Medication History (All-Time)

- Complete medication timeline across all visits
- Current vs. past medications clearly delineated
- Source: Hospital prescriptions + patient-reported external medications
- Medication reconciliation support at admission and discharge

#### Surgery History

| Field | Type |
|-------|------|
| Procedure | CPT/ICD-PCS coded |
| Date | Date |
| Hospital | Text (if external) |
| Surgeon | Text |
| Findings | Text |
| Complications | Text |
| Implants | Type, manufacturer, serial number |

### 4.1.4 EMR Timeline View

- Chronological timeline of all patient interactions
- Filterable by: Visit type, Department, Date range, Document type
- Visual indicators for: Admissions, Discharges, Surgeries, Critical events
- Quick-jump to specific events
- Print/export selected sections

### 4.1.5 Data Retention & Archival

| Category | Retention Period |
|----------|-----------------|
| Active patient records | Indefinite (hot storage) |
| Inactive records (no visit for 5+ years) | Archive to cold storage |
| Minor patient records | Until patient turns 25 or 8 years after last visit, whichever is later |
| MLC records | 30 years minimum |
| Radiology images | 10 years minimum |
| Lab results | 10 years minimum |
| Audit logs | 7 years minimum |

---

## 4.2 Laboratory Information System (LIS)

### 4.2.1 Lab Organization

```
CENTRAL LABORATORY
├── Hematology
│   ├── Complete Blood Count (CBC)
│   ├── Erythrocyte Sedimentation Rate (ESR)
│   ├── Peripheral Blood Smear
│   ├── Coagulation Studies (PT/INR, aPTT)
│   ├── D-Dimer
│   └── Reticulocyte Count
│
├── Biochemistry
│   ├── Liver Function Tests (LFT)
│   │   ├── Total Bilirubin, Direct Bilirubin
│   │   ├── SGOT (AST), SGPT (ALT)
│   │   ├── Alkaline Phosphatase (ALP)
│   │   ├── GGT
│   │   ├── Total Protein, Albumin, Globulin
│   │   └── A/G Ratio
│   ├── Kidney Function Tests (KFT/RFT)
│   │   ├── Blood Urea, Serum Creatinine
│   │   ├── Uric Acid
│   │   ├── Sodium, Potassium, Chloride
│   │   ├── Calcium, Phosphorus
│   │   └── eGFR
│   ├── Blood Sugar
│   │   ├── Fasting Blood Sugar (FBS)
│   │   ├── Post-Prandial Blood Sugar (PPBS)
│   │   ├── Random Blood Sugar (RBS)
│   │   ├── HbA1c
│   │   └── Glucose Tolerance Test (GTT)
│   ├── Lipid Profile
│   │   ├── Total Cholesterol
│   │   ├── HDL, LDL, VLDL
│   │   └── Triglycerides
│   ├── Thyroid Function Tests
│   │   ├── TSH
│   │   ├── Free T3, Free T4
│   │   └── Total T3, Total T4
│   ├── Cardiac Markers
│   │   ├── Troponin I / T
│   │   ├── CK-MB
│   │   ├── BNP / NT-proBNP
│   │   └── Myoglobin
│   ├── Pancreatic Enzymes
│   │   ├── Amylase
│   │   └── Lipase
│   ├── Iron Studies
│   │   ├── Serum Iron
│   │   ├── TIBC
│   │   └── Ferritin
│   ├── Inflammatory Markers
│   │   ├── CRP / hs-CRP
│   │   ├── Procalcitonin
│   │   └── ESR
│   ├── Arterial Blood Gas (ABG)
│   ├── Electrolytes
│   └── Special Chemistry
│
├── Microbiology
│   ├── Culture & Sensitivity
│   │   ├── Blood Culture
│   │   ├── Urine Culture
│   │   ├── Sputum Culture
│   │   ├── Wound Culture
│   │   ├── CSF Culture
│   │   └── Stool Culture
│   ├── Gram Stain
│   ├── AFB Smear & Culture
│   ├── Fungal Culture
│   └── Antibiotic Sensitivity Testing
│
├── Serology / Immunology
│   ├── HIV (ELISA, Rapid)
│   ├── Hepatitis B (HBsAg, Anti-HBs, Anti-HBc)
│   ├── Hepatitis C (Anti-HCV)
│   ├── Dengue (NS1, IgM, IgG)
│   ├── Malaria (Rapid, Smear)
│   ├── Typhoid (Widal, Typhi Dot)
│   ├── Rheumatoid Factor (RF)
│   ├── Anti-CCP
│   ├── ANA (Antinuclear Antibody)
│   ├── VDRL / RPR
│   ├── COVID-19 (RT-PCR, Rapid Antigen)
│   └── Tumor Markers
│       ├── PSA
│       ├── CEA
│       ├── CA-125
│       ├── CA 19-9
│       ├── AFP
│       └── Beta-hCG
│
├── Clinical Pathology
│   ├── Urinalysis (Routine & Microscopy)
│   ├── Stool Examination
│   ├── Semen Analysis
│   ├── Body Fluid Analysis (Pleural, Peritoneal, CSF)
│   └── Pregnancy Test (Urine)
│
├── Histopathology
│   ├── Biopsy
│   ├── FNAC (Fine Needle Aspiration Cytology)
│   ├── Frozen Section
│   ├── Immunohistochemistry (IHC)
│   └── Pap Smear
│
├── Molecular Biology
│   ├── PCR-based Tests
│   ├── Gene Panel Testing
│   └── Pharmacogenomics
│
├── Transfusion Medicine / Blood Bank
│   ├── Blood Grouping & Cross-matching
│   ├── Antibody Screening
│   ├── Direct & Indirect Coombs Test
│   ├── Blood Component Issue
│   └── Transfusion Reaction Reporting
│
└── Point-of-Care Testing (POCT)
    ├── Blood Glucose (Glucometer)
    ├── ABG
    ├── Cardiac Markers (Bedside)
    └── Urine Pregnancy Test
```

### 4.2.2 Lab Workflow

```
Doctor Orders Test
       │
  ┌────▼────┐
  │ Order    │ → Order received in Lab Queue
  │ Received │    (with priority: Stat/Urgent/Routine)
  └────┬────┘
       │
  ┌────▼────────────┐
  │ Sample Collection│ → Phlebotomist collects sample
  │ (Barcode Label)  │    Barcode links sample to order
  └────┬────────────┘
       │
  ┌────▼────────────┐
  │ Sample Reception │ → Lab receives and verifies sample
  │ & Accessioning   │    Check: correct tube, adequate volume, patient ID
  └────┬────────────┘
       │                    ┌────────────────┐
  ┌────▼──────┐            │ Sample Rejected │
  │ Sample OK?│───NO──────▶│ Reason Logged   │
  └────┬──────┘            │ Re-collection   │
       │YES                └────────────────┘
       │
  ┌────▼────────┐
  │ Processing  │ → Run on analyzer or manual processing
  └────┬────────┘
       │
  ┌────▼────────┐
  │ QC Check    │ → Quality control validation
  └────┬────────┘
       │
  ┌────▼──────────────┐
  │ Result Entry      │ → Auto-populated from analyzer or manual entry
  │ (Auto/Manual)     │
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Abnormal Flagging │ → System flags out-of-range values
  │ & Delta Check     │    Compares with patient's previous results
  └────┬──────────────┘
       │
  ┌────▼──────────────┐            ┌─────────────────────────┐
  │ Critical Value?   │───YES────▶│ CRITICAL ALERT          │
  └────┬──────────────┘            │ • Auto-notify doctor    │
       │NO                         │ • Auto-notify nurse     │
       │                           │ • Phone call required   │
  ┌────▼──────────────┐            │ • Acknowledge mandatory │
  │ Pathologist       │            └─────────────────────────┘
  │ Authorization     │
  │ (if required)     │
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Report Finalized  │ → Available to doctor on dashboard
  │ & Dispatched      │    Patient timeline updated
  └──────────────────┘
```

### 4.2.3 Lab Report Format

Each lab report contains:

| Component | Description |
|-----------|-------------|
| Patient Info | Name, UHID, Age, Gender |
| Sample Info | Sample ID, Collection date/time, Received date/time, Sample type |
| Test Info | Test name, LOINC code |
| Result | Value with unit |
| Reference Range | Age and gender-specific normal ranges |
| Flag | Normal / Low / High / Critical Low / Critical High |
| Previous Value | Last result for comparison (delta) |
| Method | Analytical method used |
| Instrument | Analyzer name and ID |
| QC Status | Passed / Reviewed |
| Reported By | Lab technician name |
| Authorized By | Pathologist name and signature |
| Report Date/Time | Timestamp |
| Comments | Interpretive notes (if any) |

### 4.2.4 Critical Value Notification Rules

| Test | Critical Low | Critical High |
|------|-------------|--------------|
| Hemoglobin | < 7.0 g/dL | > 20.0 g/dL |
| Platelet Count | < 50,000 /μL | > 1,000,000 /μL |
| WBC | < 2,000 /μL | > 30,000 /μL |
| Blood Sugar | < 50 mg/dL | > 400 mg/dL |
| Serum Potassium | < 2.5 mEq/L | > 6.5 mEq/L |
| Serum Sodium | < 120 mEq/L | > 160 mEq/L |
| Serum Creatinine | — | > 10.0 mg/dL |
| Troponin I | — | > 0.04 ng/mL |
| PT/INR | — | > 5.0 |

*Critical values are configurable per hospital policy.*

### 4.2.5 Analyzer Integration

| Integration Type | Protocol | Description |
|-----------------|----------|-------------|
| Unidirectional | ASTM/HL7 | Results from analyzer → LIS |
| Bidirectional | ASTM/HL7 | Orders from LIS → Analyzer + Results from Analyzer → LIS |
| Middleware | Custom | Via middleware like Data Innovations, Instrument Manager |

**Supported Analyzer Types:**
- Hematology: Sysmex, Beckman Coulter, Abbott
- Biochemistry: Roche Cobas, Beckman AU, Siemens Atellica
- Immunoassay: Roche Elecsys, Abbott Architect, Siemens ADVIA
- Coagulation: Stago, Siemens
- Blood Gas: Radiometer, IL
- Urinalysis: Roche, Sysmex

### 4.2.6 TAT (Turnaround Time) Monitoring

| Priority | Target TAT |
|----------|-----------|
| Stat / Emergency | 30 minutes – 1 hour |
| Urgent | 2–4 hours |
| Routine | 4–24 hours |
| Culture & Sensitivity | 48–72 hours |
| Histopathology | 3–7 days |
| Special Tests (send-out) | As per reference lab SLA |

System tracks TAT from:
- Order time to collection time
- Collection time to reception time
- Reception time to result time
- Result time to authorization time
- Authorization time to dispatch time

TAT breaches trigger alerts to lab supervisor.

---

## 4.3 Radiology Module

### 4.3.1 Supported Modalities

| Modality | Abbreviation | Common Studies |
|----------|-------------|----------------|
| X-Ray | XR | Chest PA, Abdomen, Skeletal |
| Computed Tomography | CT | CT Brain, CT Chest, CT Abdomen, CTPA, CT Angiography |
| Magnetic Resonance Imaging | MRI | MRI Brain, MRI Spine, MRI Knee, MR Angiography |
| Ultrasonography | USG | USG Abdomen, USG Pelvis, USG Obstetric, Doppler |
| Fluoroscopy | FL | Barium Swallow, Barium Enema, HSG |
| Mammography | MG | Screening, Diagnostic |
| PET-CT | PET | Whole body PET-CT |
| Interventional Radiology | IR | Angioplasty, Embolization, Biopsies |
| Nuclear Medicine | NM | Bone Scan, Thyroid Scan, DMSA, DTPA |
| Bone Densitometry | DEXA | Bone mineral density |
| Dental Imaging | DI | OPG, CBCT |

### 4.3.2 Radiology Workflow

```
Doctor Orders Study
       │
  ┌────▼────────┐
  │ Scheduling  │ → Assign slot based on modality, urgency
  └────┬────────┘
       │
  ┌────▼────────────┐
  │ Patient Arrives  │ → QR scan verification
  │ at Radiology     │
  └────┬────────────┘
       │
  ┌────▼──────────────┐
  │ Pre-Procedure     │ → Consent, contrast allergy check,
  │ Checklist          │    pregnancy check, renal function for contrast
  └────┬──────────────┘
       │
  ┌────▼──────┐
  │ Imaging   │ → Study performed, DICOM images generated
  │ Performed │
  └────┬──────┘
       │
  ┌────▼──────────────┐
  │ Images to PACS    │ → Auto-routed to PACS storage
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Radiologist       │ → View images on diagnostic workstation
  │ Worklist          │    (PACS viewer integrated)
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Report Creation   │ → Structured/Voice/Free-text reporting
  └────┬──────────────┘
       │
  ┌────▼──────────────┐          ┌──────────────────────┐
  │ Critical Finding? │──YES───▶│ CRITICAL ALERT       │
  └────┬──────────────┘          │ Immediate notification│
       │NO                       │ to ordering doctor    │
       │                         └──────────────────────┘
  ┌────▼──────────────┐
  │ Report Finalized  │ → Available to doctor
  │ & Dispatched      │    Patient timeline updated
  └──────────────────┘
```

### 4.3.3 PACS Integration

| Feature | Description |
|---------|-------------|
| DICOM Receive | Accept images from all modalities via DICOM protocol |
| DICOM Storage | Store all imaging data with redundancy |
| Web Viewer | Browser-based diagnostic quality DICOM viewer |
| Prior Studies | Automatic retrieval of patient's prior studies for comparison |
| 3D Reconstruction | MPR, MIP, VR capabilities for CT/MRI |
| Hanging Protocols | Configurable display layouts per study type |
| Annotation Tools | Measurement, windowing, zoom, pan, rotate |
| CD/DVD Burning | Export studies for patients on physical media |
| Image Sharing | Secure sharing via web links |

### 4.3.4 Structured Reporting Templates

| Template | Used For |
|----------|---------|
| BI-RADS | Breast imaging (Mammography, Breast USG, Breast MRI) |
| PI-RADS | Prostate MRI |
| LI-RADS | Liver imaging for HCC surveillance |
| TI-RADS | Thyroid USG |
| Lung-RADS | Lung cancer screening CT |
| O-RADS | Ovarian/Adnexal imaging |
| C-RADS | CT Colonography |

### 4.3.5 Voice Reporting

- Radiologist dictates findings via microphone
- AI speech-to-text engine converts to structured report
- Medical vocabulary and anatomical terms recognition
- Template auto-fill based on modality and study type
- Draft → Review → Finalize → Sign workflow
- Audio recording preserved as backup

---

## 4.4 Prescription Management

### 4.4.1 Prescription Entry

**Data Captured Per Medication Line:**

| Field | Type | Mandatory | Example |
|-------|------|-----------|---------|
| Drug Name | Searchable (Generic + Brand) | ✅ | Paracetamol / Crocin |
| Generic Name | Auto-populated | System | Paracetamol (Acetaminophen) |
| Strength | Dropdown/Text | ✅ | 650 mg |
| Dosage Form | Dropdown | ✅ | Tablet / Capsule / Syrup / Injection / Cream / Drops / Inhaler |
| Route | Dropdown | ✅ | Oral / IV / IM / SC / Topical / Inhalation / Rectal / Sublingual |
| Frequency | Dropdown | ✅ | OD / BD / TDS / QID / SOS / HS / Stat |
| Timing | Checkbox | ❌ | Morning / Afternoon / Evening / Night |
| Relation to Food | Dropdown | ❌ | Before food / After food / With food / Empty stomach |
| Duration | Number + Unit | ✅ | 5 Days / 2 Weeks / 1 Month |
| Quantity | Auto-calculated | System | Based on frequency × duration |
| Instructions | Text | ❌ | "Take with warm water", "Avoid alcohol" |
| Start Date | Date | ✅ | — |
| End Date | Auto-calculated | System | Start date + duration |
| Substitution | Dropdown | ❌ | Allow / Do Not Substitute |

### 4.4.2 Clinical Decision Support

**Automatic Checks:**

| Check | Description | Severity |
|-------|-------------|----------|
| **Drug-Allergy** | Cross-reference prescribed drug against patient's allergy list | 🔴 Hard Stop |
| **Drug-Drug Interaction** | Check interactions between all active medications | 🔴🟡 Severity-based |
| **Duplicate Therapy** | Same drug class already prescribed | 🟡 Warning |
| **Dose Range Check** | Dose outside normal range for age/weight | 🟡 Warning |
| **Renal Dose Adjustment** | Adjust dose for renal impairment (based on eGFR) | 🟡 Warning |
| **Hepatic Dose Adjustment** | Adjust for liver impairment | 🟡 Warning |
| **Pregnancy Contraindication** | FDA Category X drugs for pregnant patients | 🔴 Hard Stop |
| **Pediatric Dose** | Weight-based dosing for pediatric patients | 🟡 Warning |
| **Geriatric Alert** | Beers Criteria inappropriate medications for elderly | 🟡 Warning |
| **Formulary Check** | Drug not on hospital formulary | 🔵 Info |

**Interaction Severity:**
- 🔴 **Contraindicated:** Cannot proceed without override with documented justification
- 🟡 **Major/Moderate:** Warning displayed, proceed with acknowledgment
- 🔵 **Minor:** Informational, no block

### 4.4.3 Prescription Templates

Doctors can create and save prescription templates for common conditions:

| Template Name | Medications |
|--------------|-------------|
| Fever Protocol | Paracetamol 650mg TDS + Pantoprazole 40mg OD |
| CAP Empirical | Ceftriaxone 1g IV BD + Azithromycin 500mg OD |
| Diabetes Start | Metformin 500mg BD + Glimepiride 1mg OD |
| Hypertension Start | Amlodipine 5mg OD + Telmisartan 40mg OD |

### 4.4.4 Prescription Outputs

- **Digital Prescription:** Sent to pharmacy module electronically
- **Printed Prescription:** Formatted printout with hospital letterhead
- **Discharge Prescription:** Special format for take-home medications
- **E-Prescription:** ABDM-compatible digital prescription

---

## 4.5 Nursing Module

### 4.5.1 Vital Signs Monitoring

**Parameters Tracked:**

| Vital | Unit | Normal Range (Adult) | Frequency |
|-------|------|---------------------|-----------|
| Temperature | °F / °C | 97.0–99.5°F | Per doctor order (typically 4–6 hourly) |
| Pulse Rate | beats/min | 60–100 | Per doctor order |
| Blood Pressure (Systolic) | mmHg | 90–140 | Per doctor order |
| Blood Pressure (Diastolic) | mmHg | 60–90 | Per doctor order |
| Respiratory Rate | breaths/min | 12–20 | Per doctor order |
| SpO2 | % | 95–100% | Per doctor order |
| Pain Score | 0–10 (NRS) | 0 | Per assessment |
| Blood Sugar (POC) | mg/dL | 70–140 (fasting) | Per doctor order |
| Height | cm | — | On admission |
| Weight | kg | — | On admission, then weekly or as ordered |
| BMI | kg/m² | 18.5–24.9 | Auto-calculated |
| Intake (Oral + IV) | mL | — | Hourly for ICU, per shift for general |
| Output (Urine + Drain) | mL | — | Hourly for ICU, per shift for general |
| GCS (Glasgow Coma Scale) | 3–15 | 15 | For neuro/ICU patients |

**Visualization:**
- Line graphs showing trends over time
- Color-coded cells (green = normal, yellow = warning, red = critical)
- NEWS2 (National Early Warning Score) auto-calculated from vitals
- Auto-escalation when NEWS ≥ 5

### 4.5.2 Medication Administration Record (MAR)

| Field | Description |
|-------|-------------|
| Medication | Drug name, dose, route |
| Scheduled Time | Based on frequency (e.g., BD → 8AM, 8PM) |
| Status | Given / Held / Refused / Not Available / Omitted |
| Actual Time | When medication was actually administered |
| Administered By | Nurse name (auto-logged) |
| Witnessed By | Second nurse (for high-risk medications) |
| Site | Injection site (for IM/SC/IV medications) |
| Patient Response | Any observed reaction |
| Barcode Verification | Scan medication barcode + patient QR (5 Rights check) |

### 4.5.3 Nursing Assessments

| Assessment | Tools Used | Frequency |
|------------|-----------|-----------|
| Fall Risk | Morse Fall Scale / Hendrich II | On admission, per shift, after fall |
| Pressure Injury Risk | Braden Scale | On admission, daily for high-risk |
| Pain Assessment | Numeric Rating Scale (NRS) / Wong-Baker FACES / FLACC (pediatric) | Per shift, after pain medication |
| Nutritional Screening | Malnutrition Screening Tool (MST) | On admission |
| Restraint Assessment | — | Every 2 hours when restraints applied |
| Wound Assessment | With photos, measurements, staging | Per dressing change |
| IV Site Assessment | Visual Infusion Phlebitis (VIP) Score | Every shift |

### 4.5.4 Shift Handover

- Structured handover using SBAR format:
  - **S**ituation: Current status
  - **B**ackground: Relevant history
  - **A**ssessment: Nurse's assessment
  - **R**ecommendation: Pending tasks, things to watch
- Digital handover form with auto-populated data
- Handover acknowledged by receiving nurse (digital signature)
- Pending tasks highlighted and tracked

### 4.5.5 Incident Reporting

| Incident Type | Examples |
|--------------|---------|
| Patient Fall | With/without injury |
| Medication Error | Wrong drug, wrong dose, wrong route, wrong time, wrong patient |
| Adverse Drug Reaction | Documented with severity |
| Equipment Failure | Medical device malfunction |
| Blood/Body Fluid Exposure | Needle stick, splash |
| Patient Complaint | Documented for quality review |
| Near Miss | Event caught before reaching patient |
| Elopement | Patient left without knowledge |

---

## 4.6 ICU Management

### 4.6.1 ICU Types Supported

- Medical ICU (MICU)
- Surgical ICU (SICU)
- Cardiac ICU (CCU/CICU)
- Neonatal ICU (NICU)
- Pediatric ICU (PICU)
- Neuro ICU
- Burn ICU
- Transplant ICU

### 4.6.2 ICU-Specific Features

| Feature | Description |
|---------|-------------|
| **Continuous Monitoring Integration** | Real-time feed from bedside monitors (Philips, GE, Nihon Kohden) |
| **Ventilator Tracking** | Mode, settings (FiO2, PEEP, Tidal Volume, RR), ABG correlation |
| **Infusion Pump Tracking** | Drug, rate, concentration, dose per kg/min |
| **Hourly Charting** | All parameters charted hourly by nursing |
| **ICU Scoring** | APACHE II, APACHE IV, SOFA, qSOFA, GCS auto-calculated |
| **Sepsis Screening** | Automated qSOFA calculation, Sepsis-3 criteria screening |
| **Daily ICU Checklist** | Evidence-based checklist (Head elevation, DVT prophylaxis, Stress ulcer prophylaxis, Sedation vacation, SBT, Central line assessment, Catheter assessment) |
| **Fluid Balance** | Real-time cumulative intake vs output with net balance |
| **Blood Product Tracking** | Units transfused, reactions monitored |
| **Procedure Logging** | Central line insertion, intubation, chest drain, etc. with complications |
| **Family Communication** | Structured family update documentation |

### 4.6.3 ICU Dashboards

- Bed-level view showing current vitals, ventilator status, infusions
- Alert severity indicators
- Length of ICU stay
- Pending orders and overdue tasks
- Step-down readiness assessment

---

## 4.7 Operation Theatre (OT) Management

### 4.7.1 OT Booking & Scheduling

| Field | Type | Required |
|-------|------|----------|
| Patient | UHID | ✅ |
| Procedure | CPT/ICD-PCS coded | ✅ |
| Procedure Type | Elective / Emergency | ✅ |
| Surgeon | Searchable | ✅ |
| Assistant Surgeon | Searchable | ❌ |
| Anesthesiologist | Searchable | ✅ |
| Anesthesia Type | GA / SA / LA / Regional / Sedation | ✅ |
| OT Room | Dropdown (available) | ✅ |
| Estimated Duration | Hours:Minutes | ✅ |
| Date & Time | Calendar | ✅ |
| Special Requirements | Equipment, implants, blood products | ❌ |
| Laterality | Left / Right / Bilateral / N/A | ✅ (for applicable procedures) |

### 4.7.2 Pre-Operative Workflow

```
Surgery Scheduled
       │
  ┌────▼───────────┐
  │ Pre-Anesthesia │ → Anesthesia assessment, fitness clearance
  │ Evaluation      │    ASA grading, airway assessment
  └────┬───────────┘
       │
  ┌────▼───────────┐
  │ Pre-Op          │ → CBC, RFT, LFT, Coagulation, ECG, CXR
  │ Investigations  │    Blood grouping, cross-matching
  └────┬───────────┘
       │
  ┌────▼───────────┐
  │ Consent         │ → Informed consent signed by patient/guardian
  │ (Digital Sign)  │    Procedure, risks, alternatives documented
  └────┬───────────┘
       │
  ┌────▼───────────┐
  │ Pre-Op          │ → NPO status, site marking, site verification
  │ Checklist       │    WHO Surgical Safety Checklist
  └────┬───────────┘
       │
  ┌────▼───────────┐
  │ Patient         │ → Time-out: Patient ID, Procedure, Site confirmed
  │ Shifted to OT   │    by entire team
  └───────────────┘
```

### 4.7.3 Intra-Operative Documentation

| Section | Content |
|---------|---------|
| **WHO Checklist** | Sign-In, Time-Out, Sign-Out (all documented with timestamps) |
| **Anesthesia Record** | Drugs given, doses, times, vitals every 5 min, fluids, blood products |
| **Surgery Start/End** | Incision time, closure time |
| **Surgeon's Notes** | Findings, procedure details, complications |
| **Specimen** | Tissue specimens sent for pathology (labeled, tracked) |
| **Implants** | Type, manufacturer, serial number, lot number, size (for traceability) |
| **Blood Loss** | Estimated blood loss |
| **Drain/Catheter** | Drains placed, catheter status |
| **Counts** | Sponge, instrument, needle counts (before and after) |
| **Post-Op Orders** | Pain management, antibiotics, DVT prophylaxis, diet, activity |

### 4.7.4 OT Utilization Dashboard

- OT-wise daily schedule (Gantt chart view)
- Utilization rate per OT per day/week/month
- Surgeon-wise case volume
- Cancellation tracking with reasons
- Emergency vs. elective ratio
- Average surgery duration by procedure type

---

## 4.8 Infection Control Module

### 4.8.1 Surveillance

| Metric | Description |
|--------|-------------|
| **HAI Rate** | Hospital-Acquired Infection rate per 1000 patient-days |
| **CLABSI** | Central Line-Associated Bloodstream Infection |
| **CAUTI** | Catheter-Associated Urinary Tract Infection |
| **VAP** | Ventilator-Associated Pneumonia |
| **SSI** | Surgical Site Infection |
| **MRSA/VRE/ESBL** | Multi-drug resistant organism tracking |
| **C. diff** | Clostridioides difficile infection tracking |

### 4.8.2 Isolation Management

| Isolation Type | Color Code | Precautions |
|---------------|-----------|-------------|
| Contact | 🟡 Yellow | Gown, Gloves |
| Droplet | 🟢 Green | Surgical Mask |
| Airborne | 🔴 Red | N95 Respirator, Negative Pressure Room |
| Protective | 🔵 Blue | Reverse Isolation (for immunosuppressed) |

### 4.8.3 Antimicrobial Stewardship

- **Antibiogram:** Hospital-specific antibiotic sensitivity patterns, updated annually
- **Restricted Antibiotics:** Higher antibiotics (Meropenem, Colistin, Tigecycline, etc.) require ID physician approval
- **Antibiotic Utilization Report:** DDD (Defined Daily Dose) per 1000 patient-days
- **Duration Alerts:** Automatic alerts when antibiotic duration exceeds protocol
- **De-escalation Prompts:** System prompts to narrow-spectrum based on culture results
- **Antibiotic Auto-Stop:** Surgical prophylaxis auto-stopped after 24 hours (configurable)

### 4.8.4 Hand Hygiene Compliance

- Digital hand hygiene audit forms
- Compliance tracking by unit, role, shift
- Dashboard with trend analysis

### 4.8.5 Outbreak Management

- Cluster detection (unusual increase in similar infections)
- Line listing generation
- Contact tracing within hospital
- Alert to infection control team and hospital admin

---

## 4.9 Blood Bank Module

### 4.9.1 Features

| Feature | Description |
|---------|-------------|
| **Donor Management** | Donor registration, screening, eligibility assessment |
| **Blood Collection** | Bag labeling, component separation, storage |
| **Component Inventory** | Whole blood, PRBC, FFP, Platelets, Cryoprecipitate |
| **Cross-Matching** | ABO/Rh grouping, antibody screening, compatibility testing |
| **Issue Management** | Blood requisition from clinical units, issue with verification |
| **Transfusion Monitoring** | Transfusion reaction reporting and investigation |
| **Expiry Management** | FIFO (First In, First Out) with expiry alerts |
| **Wastage Tracking** | Track and analyze blood component wastage |
| **Regulatory Compliance** | Compliance with Drug Controller General of India (DCGI) and NACO guidelines |

---

## 4.10 Diet & Nutrition Module

### 4.10.1 Features

| Feature | Description |
|---------|-------------|
| **Diet Order** | Doctor/dietitian orders diet type for patient |
| **Diet Plans** | Pre-configured diet plans (Diabetic, Renal, Cardiac, Low-Sodium, Soft, Liquid, NPO) |
| **Allergy Integration** | Auto-excludes allergens from diet |
| **Calorie Tracking** | Track daily calorie/protein/fluid intake for critical patients |
| **Kitchen Integration** | Diet orders sent to kitchen/dietary services |
| **Meal Scheduling** | Breakfast, lunch, snack, dinner timing |
| **Enteral Nutrition** | Tube feeding plans (formula, rate, volume) |
| **Parenteral Nutrition** | TPN composition and monitoring |
| **Dietary Counseling** | Discharge diet instructions |

---

[→ Next: Functional Requirements — Administrative & Support Modules](./05_Administrative_Modules.md)
