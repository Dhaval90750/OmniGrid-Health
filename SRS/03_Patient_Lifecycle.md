# 3. Patient Lifecycle & Clinical Workflows

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 3.1 Patient Lifecycle Overview

The patient lifecycle in MedCore HIS follows a strictly defined journey where every step is tracked, timestamped, and linked to the patient's unique QR code.

```
REGISTRATION → TRIAGE/QUEUE → CONSULTATION → INVESTIGATION → TREATMENT → DISCHARGE → FOLLOW-UP
     │              │              │               │              │            │            │
     ▼              ▼              ▼               ▼              ▼            ▼            ▼
   UHID +        Token &       Doctor         Lab/Radiology   Prescription   Summary     Appointment
   QR Code       Priority      Assessment      Orders          Dispensing    Package      Scheduling
```

---

## 3.2 Step 1: Patient Registration

### 3.2.1 New Patient Registration

**Trigger:** Patient arrives at the hospital for the first time.

**Data Captured:**

| Field | Type | Mandatory | Validation |
|-------|------|-----------|------------|
| UHID | Auto-generated | System | Format: `HOS-YYYY-NNNNNNN` (e.g., `HOS-2026-0001234`) |
| QR Code | Auto-generated | System | Encodes UHID + hospital code + checksum |
| First Name | Text | ✅ | 2-50 chars, Unicode support for regional languages |
| Middle Name | Text | ❌ | — |
| Last Name / Surname | Text | ✅ | 2-50 chars |
| Date of Birth | Date | ✅ | Cannot be future date, age auto-calculated |
| Age (if DOB unknown) | Number + Unit | Conditional | Years/Months/Days |
| Gender | Dropdown | ✅ | Male / Female / Transgender / Other |
| Blood Group | Dropdown | ❌ | A+, A-, B+, B-, AB+, AB-, O+, O- |
| Marital Status | Dropdown | ❌ | Single / Married / Divorced / Widowed / Separated |
| Occupation | Text | ❌ | — |
| Mobile Number (Primary) | Phone | ✅ | Valid 10-digit Indian mobile / international format |
| Mobile Number (Secondary) | Phone | ❌ | — |
| Email | Email | ❌ | Valid email format |
| Address Line 1 | Text | ✅ | — |
| Address Line 2 | Text | ❌ | — |
| City | Text | ✅ | — |
| State | Dropdown | ✅ | Pre-populated Indian states / international |
| PIN Code | Number | ✅ | Valid 6-digit PIN |
| Country | Dropdown | ✅ | Default: India |
| Emergency Contact Name | Text | ✅ | — |
| Emergency Contact Relation | Dropdown | ✅ | Father / Mother / Spouse / Son / Daughter / Sibling / Other |
| Emergency Contact Phone | Phone | ✅ | — |
| Aadhaar Number | Number | ❌ | 12-digit with Verhoeff validation |
| ABHA ID | Text | ❌ | Format validation as per ABDM |
| Passport Number | Text | ❌ | For international patients |
| Photo | Image | ❌ | Webcam capture or upload, max 2MB |
| Nationality | Dropdown | ❌ | Default: Indian |
| Religion | Dropdown | ❌ | — |
| Language Preference | Dropdown | ❌ | For communication preference |
| Referred By | Searchable | ❌ | Doctor / Hospital / Self / Other |

### 3.2.2 Returning Patient Lookup

**Search By:**
- UHID (exact match)
- QR Code scan
- Mobile number
- Name + DOB
- Aadhaar number
- ABHA ID

**Behavior:**
- If patient found → Load existing record, offer demographic update
- If patient not found → Start new registration
- **Duplicate Detection:** System performs fuzzy matching on Name + DOB + Mobile to prevent duplicate registrations. Suspected duplicates shown for review before creating new record.

### 3.2.3 QR Code Generation

**QR Data Structure:**

```json
{
  "uhid": "HOS-2026-0001234",
  "hospital_code": "MH001",
  "name": "RAJESH KUMAR",
  "dob": "1985-03-15",
  "blood_group": "B+",
  "allergies_flag": true,
  "checksum": "a3f7b2"
}
```

**QR Outputs:**
- **Wristband QR:** Thermal-printed adhesive wristband for inpatients (waterproof, resistant to alcohol wipes)
- **Registration Slip QR:** A4/A5 printed slip for OPD patients
- **Digital QR:** Available on patient's phone via SMS link

---

## 3.3 Step 2: Visit Creation / Appointment

### 3.3.1 OPD Visit

**Visit Types:**
- Walk-in
- Scheduled Appointment
- Follow-up (linked to previous visit)
- Referral (from another doctor/department)
- Emergency → OPD conversion

**Data Captured:**

| Field | Type | Mandatory |
|-------|------|-----------|
| Visit Number | Auto-generated | System |
| Visit Date/Time | Timestamp | System |
| Department | Searchable Dropdown | ✅ |
| Consulting Doctor | Searchable Dropdown | ✅ |
| Visit Type | Dropdown | ✅ |
| Chief Complaint | Text + ICD auto-suggest | ✅ |
| Referred By | Doctor/Hospital/Self | ❌ |
| Priority | Normal / Urgent | ❌ |
| Token Number | Auto-generated | System |

### 3.3.2 Token / Queue Management

- Token auto-generated per doctor per day (Doctor A: T001, T002, T003...)
- **Queue Display:** Large screen outside each consultation room showing current token, estimated wait time
- **Priority Queue:** Emergency/VIP/Senior Citizen/Differently-abled patients can be prioritized
- **Doctor Notification:** Doctor's screen shows queue count and next patient
- **Patient Notification:** SMS/WhatsApp notification sent when patient is 2 tokens away

---

## 3.4 Step 3: Admission (IPD)

### 3.4.1 Admission Workflow

```
Doctor Orders Admission
         │
    ┌────▼────┐
    │ Admission │
    │ Request   │
    └────┬────┘
         │
    ┌────▼────┐     ┌──────────────┐
    │  Bed     │────▶│ Bed Dashboard │
    │ Booking  │     │ (Real-time)   │
    └────┬────┘     └──────────────┘
         │
    ┌────▼────┐
    │ Advance  │
    │ Deposit  │
    └────┬────┘
         │
    ┌────▼────────┐
    │ QR Wristband │
    │ Printed      │
    └────┬────────┘
         │
    ┌────▼────┐
    │ Patient  │
    │ Admitted │
    └─────────┘
```

### 3.4.2 Admission Data

| Field | Type | Mandatory |
|-------|------|-----------|
| Admission Number | Auto-generated | System |
| Admission Date/Time | Timestamp | System |
| Admitting Doctor | Searchable Dropdown | ✅ |
| Department | Searchable Dropdown | ✅ |
| Ward Category | Dropdown | ✅ |
| Ward | Dropdown (filtered) | ✅ |
| Bed | Dropdown (available only) | ✅ |
| Room Type | General / Semi-Private / Private / Suite / ICU / NICU / Isolation | ✅ |
| Provisional Diagnosis | ICD-10 searchable | ✅ |
| Admission Type | Elective / Emergency / Maternity / Day Care / Transfer-In | ✅ |
| Expected Duration | Days | ❌ |
| Insurance / TPA | Dropdown | ❌ |
| Pre-Authorization Number | Text | Conditional |
| Attendant Name | Text | ✅ |
| Attendant Phone | Phone | ✅ |
| Attendant Relationship | Dropdown | ✅ |
| Consent for Treatment | Checkbox + Signature | ✅ |
| MLC Flag | Checkbox | ❌ |
| Advance Amount | Currency | ❌ |

### 3.4.3 Bed Allocation Rules

1. System shows only available beds in selected ward category
2. Isolation beds shown separately with infection type tag
3. ICU/NICU beds require doctor authorization
4. Upgrade/downgrade logged with reason
5. Bed transfer creates new entry in patient journey timeline
6. Bed cleaning status tracked (occupied → discharged → cleaning in progress → ready)

---

## 3.5 Step 4: Doctor Assessment

### 3.5.1 Scan & Open Workflow

**Doctor scans patient QR** (mobile camera / bedside scanner).

System immediately opens the **Patient Summary Dashboard** showing:

```
┌────────────────────────────────────────────────────────────────────┐
│  PATIENT SUMMARY - RAJESH KUMAR (HOS-2026-0001234)               │
│  Age: 41Y | M | B+ | ⚠️ Allergic to Penicillin                   │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐             │
│  │ Current  │ │ Previous │ │ Active   │ │ Lab      │             │
│  │ Visit    │ │ Visits   │ │ Meds     │ │ Results  │             │
│  │          │ │ (12)     │ │ (5)      │ │ (3 new)  │             │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘             │
│                                                                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐             │
│  │ Allergies│ │ Chronic  │ │ Imaging  │ │ Surgery  │             │
│  │ ⚠️ (2)   │ │ Diseases │ │ History  │ │ History  │             │
│  │          │ │ (3)      │ │ (4)      │ │ (1)      │             │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘             │
│                                                                    │
│  VITALS (Last recorded 30 min ago by Nurse Priya)                 │
│  Temp: 101.2°F | Pulse: 88 | BP: 130/85 | SPO2: 96% | RR: 18   │
│                                                                    │
│  ⚠️ ALERTS:                                                       │
│  • Allergic to Penicillin, Sulfa drugs                            │
│  • Diabetic (Type 2) on Metformin                                 │
│  • Last HbA1c: 8.2% (3 months ago)                               │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

### 3.5.2 Clinical Documentation

**Doctor enters (via keyboard, template, or voice):**

| Section | Content | Input Method |
|---------|---------|-------------|
| Chief Complaint | Primary reason for visit in patient's words | Text / Voice / Template |
| History of Present Illness (HPI) | Detailed narrative of current illness | Text / Voice |
| Past Medical History | Chronic diseases, previous hospitalizations | Auto-populated from EMR + editable |
| Past Surgical History | Previous surgeries with dates | Auto-populated + editable |
| Family History | Relevant family medical history | Structured form + free text |
| Social History | Smoking, alcohol, occupation, lifestyle | Structured form |
| Review of Systems (ROS) | System-by-system review | Checklist + free text |
| Physical Examination | Findings from clinical examination | Text / Voice / Template with body diagrams |
| Assessment / Diagnosis | Primary and secondary diagnoses | ICD-10/11 searchable + free text |
| Plan | Treatment plan, orders, follow-up | Text / Voice |

### 3.5.3 Diagnosis Entry

- **ICD-10/ICD-11 Search:** Doctor types keywords, system auto-suggests matching ICD codes
- **Multiple Diagnoses:** Support for primary diagnosis + up to 10 secondary diagnoses
- **Diagnosis Type:** Provisional / Confirmed / Differential / Rule-Out
- **Coding Assistance:** AI suggests relevant ICD codes based on clinical notes
- **Favorite Diagnoses:** Doctors can save frequently used diagnosis combinations

**Example:**

| # | Diagnosis | ICD-10 Code | Type |
|---|-----------|-------------|------|
| 1 | Community Acquired Pneumonia | J18.9 | Primary - Confirmed |
| 2 | Type 2 Diabetes Mellitus | E11.9 | Secondary - Confirmed |
| 3 | Essential Hypertension | I10 | Secondary - Confirmed |

---

## 3.6 AI Voice-to-Clinical Notes

### 3.6.1 How It Works

```
Doctor Speaks into Mic (Mobile/Web)
           │
     ┌─────▼──────┐
     │  Whisper AI  │  ← Speech-to-Text Engine
     │  (STT)       │
     └─────┬──────┘
           │ Raw Transcript
     ┌─────▼──────────┐
     │  Clinical NLP   │  ← Medical entity extraction
     │  Engine         │
     └─────┬──────────┘
           │ Structured Entities
     ┌─────▼──────────┐
     │  LLM Medical    │  ← Professional note generation
     │  Summarizer     │
     └─────┬──────────┘
           │ Formatted Clinical Note
     ┌─────▼──────┐
     │  Doctor     │  ← Review & approve/edit
     │  Review     │
     └─────┬──────┘
           │
     ┌─────▼──────┐
     │  Signed &   │  ← Becomes part of permanent EMR
     │  Saved      │
     └────────────┘
```

### 3.6.2 Voice Input Example

**Doctor speaks:**

> "Patient Rajesh Kumar, 41-year-old male, presents with high-grade fever for five days associated with chills and rigors. He also complains of productive cough with yellowish sputum for three days and mild chest pain on the right side aggravated by deep breathing. No hemoptysis. History of Type 2 Diabetes on Metformin 500 twice daily and Hypertension on Amlodipine 5 once daily. Examination reveals temperature 101.2, pulse 88, BP 130 over 85, SPO2 96 on room air. Respiratory system examination reveals bilateral crepitations more on the right lower zone. Chest X-ray shows right lower lobe consolidation. Working diagnosis community acquired pneumonia with underlying uncontrolled diabetes. Plan to start IV Ceftriaxone 1 gram twice daily, IV Azithromycin 500 once daily, Paracetamol 650 SOS, continue Metformin, continue Amlodipine. Order CBC, CRP, Blood Sugar, HbA1c, Blood Culture, Sputum Culture. Repeat chest X-ray after 48 hours."

### 3.6.3 AI-Generated Clinical Note

**System converts to:**

---

**CLINICAL NOTE**

**Patient:** Rajesh Kumar | **UHID:** HOS-2026-0001234 | **Age/Sex:** 41Y/M  
**Date:** 2026-06-21 | **Doctor:** Dr. Mehta | **Department:** General Medicine

**CHIEF COMPLAINT:**  
High-grade fever for 5 days with productive cough for 3 days.

**HISTORY OF PRESENT ILLNESS:**  
Mr. Rajesh Kumar, a 41-year-old male, presented with a 5-day history of high-grade fever associated with chills and rigors. He also reports productive cough with yellowish sputum for the past 3 days, accompanied by mild right-sided chest pain that worsens on deep inspiration. No history of hemoptysis.

**PAST MEDICAL HISTORY:**  
- Type 2 Diabetes Mellitus — on Tab. Metformin 500mg BD  
- Essential Hypertension — on Tab. Amlodipine 5mg OD

**EXAMINATION:**  
- Temperature: 101.2°F  
- Pulse: 88/min, regular  
- Blood Pressure: 130/85 mmHg  
- SpO2: 96% on room air  
- Respiratory System: Bilateral crepitations, more pronounced in the right lower zone  
- Other Systems: Within normal limits

**INVESTIGATIONS:**  
- Chest X-ray PA view: Right lower lobe consolidation

**ASSESSMENT:**  
1. Community Acquired Pneumonia (ICD-10: J18.1) — Primary  
2. Type 2 Diabetes Mellitus, uncontrolled (ICD-10: E11.65) — Secondary  
3. Essential Hypertension (ICD-10: I10) — Secondary

**PLAN:**  
1. Inj. Ceftriaxone 1g IV BD  
2. Inj. Azithromycin 500mg IV OD  
3. Tab. Paracetamol 650mg SOS (for fever)  
4. Continue Tab. Metformin 500mg BD  
5. Continue Tab. Amlodipine 5mg OD  
6. **Investigations ordered:** CBC, CRP, Blood Sugar (Fasting + PP), HbA1c, Blood Culture (2 sets), Sputum Culture & Sensitivity  
7. Repeat Chest X-ray PA view after 48 hours  
8. Monitor vitals 4-hourly  
9. Adequate hydration

---

*Generated by AI Clinical Notes Engine. Reviewed and signed by Dr. Mehta at [timestamp].*

---

### 3.6.4 AI Processing Features

| Feature | Description |
|---------|-------------|
| **Medical Entity Extraction** | Automatically identifies symptoms, drugs, dosages, diagnoses, vitals from speech |
| **ICD Code Suggestion** | Auto-maps spoken diagnoses to ICD-10/11 codes |
| **Drug Name Normalization** | Converts brand names to generic + maps to hospital formulary |
| **Abbreviation Expansion** | Converts "BP" → "Blood Pressure", "SPO2" → "Peripheral Oxygen Saturation" |
| **Unit Standardization** | Normalizes units (e.g., "101.2" → "101.2°F", "96" with SPO2 context → "96%") |
| **Language Support** | English, Hindi, and configurable regional language support |
| **Audio Retention** | Original audio stored as legal evidence alongside generated text |
| **Confidence Score** | Each generated section has a confidence score. Low confidence sections highlighted for doctor review |

---

## 3.7 AI SOAP Note Generation

### 3.7.1 Automatic SOAP Structure

The system automatically structures clinical data into SOAP format:

#### S — Subjective
*What the patient reports*

Auto-populated from:
- Chief complaint
- History of present illness
- Patient's own description of symptoms
- Pain scores (patient-reported)
- Symptom questionnaires

**Example Output:**
> Patient reports high-grade fever for 5 days with chills and rigors. Productive cough with yellowish sputum for 3 days. Mild right-sided chest pain worsening on deep inspiration. Denies hemoptysis, dyspnea at rest, or weight loss.

#### O — Objective
*What the clinician observes and measures*

Auto-populated from:
- Vital signs (from nursing module)
- Physical examination findings (from doctor's entry)
- Lab results (from LIS)
- Imaging findings (from RIS)
- Nursing assessments

**Example Output:**
> **Vitals:** Temp 101.2°F, Pulse 88/min, BP 130/85 mmHg, SpO2 96%, RR 18/min  
> **General:** Febrile, mild tachycardia, no respiratory distress  
> **Respiratory:** Bilateral crepitations, more on right lower zone  
> **CXR:** Right lower lobe consolidation  
> **Labs (pending):** CBC, CRP, Blood Sugar, HbA1c, Blood Culture, Sputum C/S

#### A — Assessment
*Clinical diagnosis and clinical reasoning*

Auto-populated from:
- Diagnosis entries (ICD-coded)
- AI-suggested differential diagnoses
- Risk scores (NEWS, qSOFA, CURB-65 for pneumonia)

**Example Output:**
> 1. Community Acquired Pneumonia — CURB-65 Score: 1 (low risk)  
> 2. Type 2 Diabetes Mellitus, likely uncontrolled (await HbA1c)  
> 3. Essential Hypertension, controlled on current medication

#### P — Plan
*Treatment and follow-up plan*

Auto-populated from:
- Prescription entries
- Lab orders
- Radiology orders
- Doctor's free-text plan
- Follow-up scheduling

**Example Output:**
> **Medications:**  
> - Inj. Ceftriaxone 1g IV BD × 5 days  
> - Inj. Azithromycin 500mg IV OD × 3 days → switch to oral  
> - Tab. Paracetamol 650mg SOS  
> - Continue home medications  
>  
> **Investigations:** CBC, CRP, Blood Sugar F/PP, HbA1c, Blood Culture ×2, Sputum C/S  
> **Monitoring:** Vitals 4-hourly, I/O charting, repeat CXR after 48h  
> **Diet:** Diabetic diet, adequate fluids  
> **Follow-up:** Review after 48 hours with lab results

### 3.7.2 SOAP Note Lifecycle

1. **Auto-Generated:** Created from available data every time a significant update occurs
2. **Doctor Review:** Doctor reviews AI-generated SOAP, edits as needed
3. **Doctor Signs:** Digital signature with timestamp
4. **Locked:** Once signed, SOAP note is locked. Only addendum allowed.
5. **Versioning:** All versions preserved for audit trail

---

## 3.8 Step 5: Order Management

### 3.8.1 Lab Orders

- Doctor selects tests from catalog (searchable by name or code)
- **Order Sets:** Pre-configured bundles (e.g., "Fever Panel" = CBC + CRP + Blood Culture + Malaria + Dengue + Typhoid)
- **Favorite Orders:** Doctor-specific saved order sets
- **Clinical Decision Support:** System may suggest relevant tests based on diagnosis (e.g., Pneumonia → CBC, CRP, Blood Culture, Procalcitonin)
- **Sample Requirements:** System displays required sample type, tube color, volume, fasting requirements
- **Stat/Routine:** Priority selection affects lab TAT and workflow

### 3.8.2 Radiology Orders

- Modality selection (X-Ray, CT, MRI, USG, etc.)
- Study selection (e.g., CT Chest with Contrast)
- Clinical indication (mandatory — required for radiologist context)
- Contrast allergy check (auto-flagged from allergy list)
- Pregnancy check for radiation studies
- Prior authorization for high-cost studies
- Scheduling integration (available slots displayed)

### 3.8.3 Procedure Orders

- Minor procedures (wound suturing, abscess drainage)
- Major procedures (require OT scheduling)
- Consent form generation
- Pre-procedure checklist
- Implant/device tracking

### 3.8.4 Diet Orders

- Standard diet / NPO / Clear liquids / Soft diet
- Disease-specific diets (Diabetic, Renal, Cardiac, Low Sodium)
- Allergy-aware (auto-excludes allergens)
- Calorie targets
- Integration with dietary services module

### 3.8.5 Nursing Orders

- Monitoring frequency (vitals every 4h, hourly, continuous)
- Positioning (elevate head of bed, turn every 2h)
- Activity level (bed rest, ambulate with assistance)
- Special precautions (fall risk, aspiration precautions)
- Wound care instructions
- Catheter care

---

## 3.9 Step 6: Progress Notes (IPD)

### 3.9.1 Daily Progress Notes

For admitted patients, doctors create daily progress notes:

| Component | Content |
|-----------|---------|
| Date/Time | Timestamp of note |
| Subjective | Patient's current complaints, overnight events |
| Vitals Trend | Auto-pulled from nursing records (graphical trend available) |
| Examination | Relevant physical findings |
| Lab Review | New lab results with interpretation |
| Imaging Review | New imaging findings |
| Assessment | Updated diagnosis, response to treatment |
| Plan Changes | Medication changes, new orders, revised plan |

### 3.9.2 Consultant Notes

- Cross-consultation notes from specialist opinions
- Linked to the requesting doctor's referral
- Specialist recommendations clearly documented

### 3.9.3 Procedure Notes

- Pre-procedure assessment
- Procedure performed (with CPT/ICD-PCS code)
- Findings
- Complications (if any)
- Post-procedure instructions

---

## 3.10 Step 7: Discharge

### 3.10.1 Discharge Workflow

```
Doctor Approves Discharge
         │
    ┌────▼──────────┐
    │ Discharge      │
    │ Summary Auto-  │
    │ Generated      │
    └────┬──────────┘
         │
    ┌────▼──────────┐
    │ Doctor Reviews │
    │ & Signs        │
    └────┬──────────┘
         │
    ┌────▼──────────┐
    │ Pharmacy       │
    │ Discharge Meds │
    │ Dispensed       │
    └────┬──────────┘
         │
    ┌────▼──────────┐
    │ Final Bill     │
    │ Generated      │
    └────┬──────────┘
         │
    ┌────▼──────────┐
    │ Payment /      │
    │ Insurance      │
    │ Settlement     │
    └────┬──────────┘
         │
    ┌────▼──────────┐
    │ Discharge      │
    │ Package        │
    │ Generated      │
    └────┬──────────┘
         │
    ┌────▼──────────┐
    │ Bed Released   │
    │ Housekeeping   │
    │ Notified       │
    └────┬──────────┘
         │
    ┌────▼──────────┐
    │ Follow-up      │
    │ Scheduled      │
    └──────────────┘
```

### 3.10.2 Discharge Types

| Type | Description |
|------|-------------|
| **Normal Discharge** | Patient recovered, planned discharge |
| **DAMA** | Discharge Against Medical Advice — requires DAMA form signature |
| **LAMA** | Left Against Medical Advice — patient left without formal discharge |
| **Abscond** | Patient left without informing — triggers alert, police notification for MLC |
| **Transfer Out** | Transfer to another hospital — transfer summary generated |
| **Death** | Death in hospital — death summary, death certificate, MLC formalities if applicable |
| **Day Care Discharge** | Same-day procedure discharge |

### 3.10.3 Auto-Generated Discharge Summary

The system automatically compiles:

| Section | Source |
|---------|--------|
| Patient Demographics | Registration module |
| Admission Details | ADT module |
| Diagnosis (Final) | Doctor's diagnosis entries |
| History & Examination | Clinical notes |
| Investigation Summary | Lab results + Radiology reports (abnormal highlighted) |
| Treatment Given | Prescription module (all medications during stay) |
| Procedures Performed | Procedure notes |
| Condition at Discharge | Doctor's assessment |
| Discharge Medications | Doctor's discharge prescription |
| Follow-up Instructions | Doctor's plan |
| Diet Advice | Dietary module |
| Warning Signs | AI-generated based on diagnosis |
| Emergency Contact | Hospital emergency number |

**Output Formats:**
- PDF (printable, professionally formatted)
- Digital (accessible via patient QR link)
- ABDM-compatible (FHIR document format)

### 3.10.4 Discharge Package (One-Click)

Single button generates a consolidated package containing:

1. ✅ Discharge Summary (PDF)
2. ✅ Discharge Prescription (PDF)
3. ✅ Lab Results Compilation (PDF)
4. ✅ Radiology Reports (PDF + Images on CD/digital link)
5. ✅ Doctor's Clinical Notes Summary
6. ✅ Billing Summary / Final Bill
7. ✅ Insurance Claim Documents
8. ✅ Follow-up Appointment Confirmation
9. ✅ QR-linked Digital Medical Record (accessible via patient's phone)
10. ✅ Patient Education Material (disease-specific)
11. ✅ Feedback Form Link

---

## 3.11 Step 8: Follow-Up Management

### 3.11.1 Follow-Up Scheduling

- Doctor specifies follow-up date/timeframe at discharge
- System auto-schedules appointment
- SMS/WhatsApp reminder sent:
  - 3 days before appointment
  - 1 day before appointment
  - 2 hours before appointment

### 3.11.2 Post-Discharge Monitoring

- Automated calls/SMS for medication adherence check
- Symptom check questionnaires (digital forms)
- Readmission risk scoring (AI-based)
- Escalation to doctor if patient reports warning signs

### 3.11.3 Readmission Tracking

- If patient is readmitted within 30 days → flagged as potential readmission
- System links current admission to previous discharge
- Analytics tracks readmission rates by doctor, department, diagnosis

---

## 3.12 Emergency / Casualty Workflow

### 3.12.1 Emergency Registration

Simplified fast-track registration:
- Minimum data: Name (or "Unknown"), Age (estimated), Gender
- Full registration completed later
- Temporary ID generated, converted to UHID when registration completed

### 3.12.2 Triage (Manchester/Canadian Triage System)

| Level | Color | Category | Response Time | Examples |
|-------|-------|----------|---------------|----------|
| 1 | 🔴 Red | Immediate / Resuscitation | 0 min | Cardiac arrest, major trauma, airway obstruction |
| 2 | 🟠 Orange | Very Urgent | ≤ 10 min | Chest pain, severe asthma, active hemorrhage |
| 3 | 🟡 Yellow | Urgent | ≤ 60 min | Moderate pain, minor fractures, abdominal pain |
| 4 | 🟢 Green | Standard | ≤ 120 min | Minor injuries, mild symptoms |
| 5 | 🔵 Blue | Non-Urgent | ≤ 240 min | Follow-up issues, chronic complaints |

### 3.12.3 Emergency Workflow

```
Patient Arrives
      │
  ┌───▼───┐
  │ Triage │ → Assign Priority Level
  └───┬───┘
      │
  ┌───▼────────┐
  │ Quick Reg   │ → Minimal data, generate temp ID
  └───┬────────┘
      │
  ┌───▼────────┐
  │ Doctor      │ → Emergency physician assigned
  │ Assignment  │
  └───┬────────┘
      │
  ┌───▼────────┐
  │ Assessment  │ → ABCDE approach documented
  │ & Treatment │
  └───┬────────┘
      │
  ┌───▼──────────────────┐
  │ Disposition Decision  │
  ├───────────────────────┤
  │ • Admit to IPD       │
  │ • Admit to ICU       │
  │ • Discharge from ED  │
  │ • Transfer to OT     │
  │ • Transfer out       │
  │ • Death              │
  └──────────────────────┘
```

### 3.12.4 MLC (Medico-Legal Case) Handling

- MLC flag at triage or during assessment
- Mandatory police notification
- Wound certificate generation
- Chain of custody for evidence
- MLC register entry (digital)
- Special access controls (restricted visibility)

---

[→ Next: Functional Requirements — Clinical Modules](./04_Clinical_Modules.md)
