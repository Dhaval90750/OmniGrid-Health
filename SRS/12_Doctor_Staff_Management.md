# 12. Functional Requirements — Doctor & Medical Staff Management

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 12.1 Overview

This module manages the complete lifecycle of all medical professionals within the hospital — from onboarding to retirement. It covers the hierarchy of medical staff, credentialing, privilege management, rotation scheduling (especially for interns and residents), cross-department transfers, performance tracking, and on-call/duty roster management.

This module integrates tightly with:

- **User & Role Management** — Login credentials and system access
- **Patient Module** — Doctor-patient assignment
- **OPD / IPD** — Consultation scheduling
- **OT Module** — Surgeon assignment
- **Billing** — Doctor fee configuration
- **Analytics** — Doctor performance dashboards
- **Notification** — Duty alerts, rotation reminders

---

## 12.2 Medical Staff Hierarchy

### 12.2.1 Complete Doctor Hierarchy

```
┌─────────────────────────────────────────────────────────────────┐
│                    HOSPITAL MEDICAL HIERARCHY                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    ADMINISTRATION                        │    │
│  │  ┌────────────┐                                          │    │
│  │  │    DEAN    │ ← Overall academic & administrative head │    │
│  │  └─────┬──────┘                                          │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ MEDICAL SUPERINTENDENT │ ← Day-to-day hospital ops    │    │
│  │  │ / MEDICAL DIRECTOR     │                              │    │
│  │  └─────┬──────────────────┘                              │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ DEPUTY MEDICAL         │ ← Assists MS in operations   │    │
│  │  │ SUPERINTENDENT         │                              │    │
│  │  └────────────────────────┘                              │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              DEPARTMENT LEVEL                            │    │
│  │  ┌──────────────────────────┐                            │    │
│  │  │ HEAD OF DEPARTMENT (HOD) │ ← Leads each department    │    │
│  │  └─────┬────────────────────┘                            │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ PROFESSOR              │ ← Senior-most academic rank  │    │
│  │  └─────┬──────────────────┘                              │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ ASSOCIATE PROFESSOR    │ ← Mid-level academic         │    │
│  │  └─────┬──────────────────┘                              │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ ASSISTANT PROFESSOR    │ ← Entry-level academic        │    │
│  │  └────────────────────────┘                              │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              CONSULTANT / SPECIALIST LEVEL               │    │
│  │  ┌──────────────────────────┐                            │    │
│  │  │ SENIOR CONSULTANT       │ ← 10+ years post-specialty  │    │
│  │  └─────┬────────────────────┘                            │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ CONSULTANT             │ ← Board-certified specialist │    │
│  │  └─────┬──────────────────┘                              │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ JUNIOR CONSULTANT      │ ← Recently completed         │    │
│  │  │                        │   super-specialty/fellowship  │    │
│  │  └────────────────────────┘                              │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │            VISITING / CONTRACTUAL                        │    │
│  │  ┌──────────────────────────┐                            │    │
│  │  │ VISITING CONSULTANT      │ ← Visits on specific days  │    │
│  │  └──────────────────────────┘                            │    │
│  │  ┌──────────────────────────┐                            │    │
│  │  │ CONTRACTUAL DOCTOR       │ ← Fixed-term employment    │    │
│  │  └──────────────────────────┘                            │    │
│  │  ┌──────────────────────────┐                            │    │
│  │  │ LOCUM / TEMPORARY        │ ← Short-term substitute    │    │
│  │  └──────────────────────────┘                            │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │               RESIDENT DOCTORS                           │    │
│  │  ┌──────────────────────────┐                            │    │
│  │  │ SENIOR RESIDENT (SR)     │ ← Post-MD/MS (3 years)    │    │
│  │  └─────┬────────────────────┘                            │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ JUNIOR RESIDENT (JR-3) │ ← PG 3rd Year               │    │
│  │  └─────┬──────────────────┘                              │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ JUNIOR RESIDENT (JR-2) │ ← PG 2nd Year               │    │
│  │  └─────┬──────────────────┘                              │    │
│  │        │                                                  │    │
│  │  ┌─────▼──────────────────┐                              │    │
│  │  │ JUNIOR RESIDENT (JR-1) │ ← PG 1st Year               │    │
│  │  └────────────────────────┘                              │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    INTERNS                                │    │
│  │  ┌──────────────────────────┐                            │    │
│  │  │ INTERN (CRRI)            │ ← Compulsory Rotatory      │    │
│  │  │                          │   Residential Internship    │    │
│  │  │                          │   (12 months across depts)  │    │
│  │  └──────────────────────────┘                            │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                 OTHER MEDICAL STAFF                       │    │
│  │  ┌───────────────┐ ┌──────────────┐ ┌────────────────┐  │    │
│  │  │ Medical        │ │ Registrar    │ │ Casualty       │  │    │
│  │  │ Officer (MO)   │ │              │ │ Medical Officer│  │    │
│  │  └───────────────┘ └──────────────┘ └────────────────┘  │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 12.2.2 Doctor Type Definitions

| Type | Code | Description | Employment | Privileges |
|------|------|-------------|-----------|------------|
| **Dean** | DEAN | Overall head of hospital (academic + administrative). Typically a senior professor. | Permanent | Full administrative + clinical |
| **Medical Superintendent (MS)** | MS | Responsible for day-to-day hospital operations, patient care quality, and regulatory compliance. | Permanent | Administrative + limited clinical |
| **Deputy MS** | DMS | Assists the MS. Handles specific operational domains. | Permanent | Administrative |
| **Head of Department (HOD)** | HOD | Leads a specific clinical department. Responsible for patient care, teaching, and research within the department. | Permanent | Full departmental clinical + admin |
| **Professor** | PROF | Senior-most academic rank. Extensive clinical experience (15+ years post-PG). Supervises residents and interns. | Permanent | Full clinical + teaching |
| **Associate Professor** | ASSOC_PROF | Mid-level academic rank. 8-15 years post-PG. Teaching and clinical duties. | Permanent | Full clinical + teaching |
| **Assistant Professor** | ASST_PROF | Entry-level academic rank. 3-8 years post-PG. Active teaching role. | Permanent | Clinical + teaching |
| **Senior Consultant** | SR_CONS | Highly experienced specialist (10+ years post-specialty). May lead a sub-specialty unit. | Permanent / Contract | Full clinical in specialty |
| **Consultant** | CONS | Board-certified specialist with independent practice privileges. | Permanent / Contract | Full clinical in specialty |
| **Junior Consultant** | JR_CONS | Recently completed super-specialty or fellowship. Working under senior guidance initially. | Contract (probation) | Clinical under supervision initially |
| **Visiting Consultant** | VISIT_CONS | Specialist who visits on specific days/hours. Maintains practice elsewhere. | Visiting / Part-time | Clinical on visiting days only |
| **Contractual Doctor** | CONTRACT | Doctor on fixed-term contract (6 months to 3 years). | Contract | As per contract terms |
| **Locum Doctor** | LOCUM | Temporary substitute for a regular doctor (leave cover, vacancy fill). | Temporary | Limited clinical, time-bound |
| **Senior Resident (SR)** | SR | Post-MD/MS doctor. 3-year appointment. Semi-independent clinical work. First point of escalation from JRs. | Tenure (3 years) | Clinical under consultant supervision |
| **Junior Resident Year 3 (JR-3)** | JR3 | PG trainee in final year. More autonomy in clinical decisions. Can handle complex cases under guidance. | Training | Clinical under SR/Consultant supervision |
| **Junior Resident Year 2 (JR-2)** | JR2 | PG trainee in second year. Moderate autonomy. Handles routine cases. | Training | Clinical under SR/Consultant supervision |
| **Junior Resident Year 1 (JR-1)** | JR1 | PG trainee in first year. Learning clinical skills. Close supervision required. | Training | Clinical under close supervision |
| **Intern (CRRI)** | INTERN | Compulsory Rotatory Residential Intern. MBBS graduate doing 12-month internship. Rotates across multiple departments. | Internship (12 months) | Supervised clinical only |
| **Medical Officer (MO)** | MO | MBBS doctor without PG. Handles general duties, casualty, PHC. | Permanent / Contract | General clinical |
| **Registrar** | REG | Administrative + clinical role. Manages OPD, admissions, documentation for a department. | Permanent | Clinical + administrative |
| **Casualty Medical Officer (CMO)** | CMO | Doctor posted in Emergency/Casualty department. | Rotation / Permanent | Emergency clinical |

---

## 12.3 Doctor Master Profile

### 12.3.1 Profile Data Model

| Field | Type | Mandatory | Description |
|-------|------|-----------|-------------|
| **Doctor ID** | Auto-generated | System | Unique ID: `DOC-YYYY-NNNNN` |
| **Employee ID** | Text | ✅ | Hospital employee number |
| **Full Name** | Text | ✅ | Including salutation (Dr. / Prof.) |
| **Photo** | Image | ✅ | Professional photo |
| **Date of Birth** | Date | ✅ | — |
| **Gender** | Dropdown | ✅ | Male / Female / Other |
| **Nationality** | Dropdown | ✅ | — |
| **Contact Number** | Phone | ✅ | Primary mobile |
| **Email** | Email | ✅ | Official email |
| **Personal Email** | Email | ❌ | — |
| **Address** | Text | ✅ | Current residential address |
| **Doctor Type** | Dropdown | ✅ | See Section 12.2.2 codes |
| **Designation** | Dropdown | ✅ | Official designation title |
| **Primary Department** | Dropdown | ✅ | Home/base department |
| **Secondary Departments** | Multi-select | ❌ | Cross-department privileges |
| **Sub-Specialty** | Text | ❌ | e.g., "Interventional Cardiology", "Neuro-Oncology" |
| **Employment Type** | Dropdown | ✅ | Permanent / Contract / Visiting / Locum / Training |
| **Joining Date** | Date | ✅ | Date of joining this hospital |
| **Contract End Date** | Date | Conditional | For contract/visiting/locum |
| **Reporting To** | Doctor reference | ✅ | Supervising doctor / HOD |

**Qualifications & Credentials:**

| Field | Type | Mandatory | Description |
|-------|------|-----------|-------------|
| **MBBS** | Text + Year + University | ✅ | Basic medical degree |
| **PG Degree** | Text + Year + University | Conditional | MD / MS / DNB / Diploma |
| **Super-Specialty** | Text + Year + University | ❌ | DM / MCh / DNB-SS |
| **Fellowship** | Text + Institution | ❌ | — |
| **Additional Qualifications** | Text | ❌ | FRCS, MRCP, etc. |
| **Medical Council Registration** | Registration No. + State Council + Validity | ✅ | State Medical Council / NMC |
| **Registration Certificate** | Document upload | ✅ | Scanned copy |
| **NMC ID** | Text | ❌ | National Medical Commission ID |
| **Specialty Board Certification** | Text + Date | ❌ | — |
| **DEA Number** | Text | ❌ | For controlled substance prescribing (if applicable) |
| **CPR/BLS/ACLS Certification** | Date + Validity | ❌ | — |

**Financial:**

| Field | Type | Mandatory |
|-------|------|-----------|
| **OPD Consultation Fee** | Currency | ✅ |
| **Follow-up Fee** | Currency | ✅ |
| **Emergency Consultation Fee** | Currency | ❌ |
| **Procedure Fees** | Per procedure | ❌ |
| **Revenue Share Model** | Fixed / Percentage / Hybrid | Conditional |
| **Revenue Share %** | Percentage | Conditional |
| **Bank Details** | IFSC + Account | ✅ |
| **PAN Number** | Text | ✅ |

**Availability & Schedule:**

| Field | Type | Description |
|-------|------|-------------|
| **OPD Days** | Multi-select (Mon-Sun) | Days available for OPD |
| **OPD Timings** | Time range per day | Morning: 9AM-1PM, Evening: 4PM-7PM |
| **OPD Slots** | Number | Max patients per slot |
| **Average Consultation Time** | Minutes | For queue estimation |
| **OT Days** | Multi-select | Days available for surgery |
| **On-Call Schedule** | Calendar | On-call duty days |
| **Leave Calendar** | Calendar | Planned leaves, conferences |
| **Visiting Schedule** | Custom | For visiting consultants only |

### 12.3.2 Doctor Status Lifecycle

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│ Onboarding│────▶│  Active  │────▶│On Leave  │────▶│  Active  │
└──────────┘     └────┬─────┘     └──────────┘     └──────────┘
                      │
                 ┌────┴─────────────────┐
                 │                      │
           ┌─────▼─────┐         ┌─────▼──────┐
           │ Suspended  │         │ Transferred│
           └─────┬─────┘         │ (to other  │
                 │               │  dept/hosp)│
           ┌─────▼─────┐         └────────────┘
           │ Reinstated │
           │  or        │
           │ Terminated │
           └────────────┘
```

| Status | Description |
|--------|-------------|
| **Onboarding** | Credentials being verified, system access being set up |
| **Active** | Fully active, can see patients, create records |
| **On Leave** | Temporarily unavailable (vacation, conference, medical leave) |
| **Suspended** | Privileges suspended pending investigation |
| **Transferred** | Moved to another department or hospital branch |
| **Resigned** | Resigned from position |
| **Retired** | Reached retirement age |
| **Terminated** | Employment terminated |
| **Deceased** | Doctor is deceased |

---

## 12.4 Clinical Privileges & Credentialing

### 12.4.1 Privilege Categories

| Category | Description | Examples |
|----------|-------------|---------|
| **Admitting Privileges** | Can admit patients | Which wards/departments |
| **Consulting Privileges** | Can provide consultations | OPD, IPD, cross-department |
| **Surgical Privileges** | Can perform surgeries | Specific procedure list |
| **Prescribing Privileges** | Can prescribe medications | Full / Restricted (e.g., interns can't prescribe narcotics) |
| **Ordering Privileges** | Can order investigations | Lab, Radiology, specific high-cost tests |
| **Procedure Privileges** | Can perform bedside procedures | Central line, intubation, chest drain, lumbar puncture |
| **Discharge Privileges** | Can approve discharge | Final discharge authority |
| **Death Certification** | Can certify death | — |
| **MLC Privileges** | Can handle medico-legal cases | — |
| **Teaching Privileges** | Can supervise trainees | Interns, residents |
| **Research Privileges** | Can conduct clinical research | IRB-approved studies |

### 12.4.2 Privilege Matrix by Doctor Type

| Privilege | Dean | HOD | Prof | Assoc Prof | Asst Prof | Sr Cons | Cons | Jr Cons | Visit Cons | SR | JR-3 | JR-2 | JR-1 | Intern | MO |
|-----------|:----:|:---:|:----:|:----------:|:---------:|:-------:|:----:|:-------:|:----------:|:--:|:----:|:----:|:----:|:------:|:--:|
| Admit Patient | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅⁰ | ❌ | ❌ | ❌ | ❌ | ✅⁰ |
| OPD Consultation | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅⁰ | ✅⁰ | ❌ | ❌ | ✅ |
| IPD Rounds | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅⁰ | ✅ |
| Major Surgery | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️¹ | ✅ | ⚠️¹ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Minor Surgery | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️¹ | ❌ | ❌ | ❌ | ❌ |
| Prescribe (Full) | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| Prescribe (Narcotics) | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Order Lab/Radiology | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️² | ✅ |
| Approve Discharge | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Sign Death Certificate | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Supervise Trainees | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️³ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Cross-Dept Consult | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Voice Dictation | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |

**Legend:**
- ✅ = Full privilege
- ✅⁰ = Allowed with restrictions (e.g., limited to specific cases/wards)
- ⚠️¹ = Allowed only as first assistant, not primary surgeon
- ⚠️² = Can order basic tests only; high-cost tests need senior approval
- ⚠️³ = Can supervise interns only
- ❌ = Not allowed

### 12.4.3 Credentialing Workflow

```
Doctor Applies / Hospital Invites
          │
     ┌────▼──────────┐
     │ Document       │ → Degree certificates, registration,
     │ Submission     │    experience certificates, references
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Primary Source │ → Verify degrees with university
     │ Verification   │    Verify registration with medical council
     │ (PSV)          │    Verify experience with previous hospitals
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Credential     │ → Committee reviews documents + PSV results
     │ Committee      │    Evaluates competency and fit
     │ Review         │
     └────┬──────────┘
          │
     ┌────▼──────────────────┐
     │ Privilege Assignment   │ → Assign clinical privileges based on
     │                        │    qualifications, experience, and role
     └────┬──────────────────┘
          │
     ┌────▼──────────────────┐
     │ Provisional Privileges │ → Initial probation period (3-6 months)
     │ (Probation)            │    Supervised by HOD/senior consultant
     └────┬──────────────────┘
          │
     ┌────▼──────────────────┐
     │ Full Privileges        │ → After successful probation
     │ (Confirmed)            │    Annual re-credentialing required
     └──────────────────────┘
```

### 12.4.4 Re-Credentialing (Annual)

| Checked | Details |
|---------|---------|
| Medical Council registration validity | Must be current |
| Malpractice / complaints review | Any complaints filed |
| Patient volume | Adequate volume for competency maintenance |
| Complication rate | Within acceptable benchmarks |
| CME (Continuing Medical Education) credits | Minimum credits required per year |
| Peer review feedback | From colleagues and HOD |
| Updated certifications | BLS/ACLS, specialty certifications |

---

## 12.5 Intern Management

### 12.5.1 Intern Profile (Extended)

In addition to standard doctor profile fields:

| Field | Type | Mandatory | Description |
|-------|------|-----------|-------------|
| Medical College | Dropdown/Text | ✅ | College from which MBBS completed |
| University | Dropdown/Text | ✅ | Affiliated university |
| MBBS Completion Date | Date | ✅ | — |
| Internship Start Date | Date | ✅ | — |
| Internship End Date | Auto-calculated | System | Start + 12 months |
| Current Rotation Department | Dropdown | System | Current department posting |
| Rotation Schedule | Reference | System | Link to rotation calendar |
| Supervising Senior Resident | Doctor reference | ✅ | Assigned SR for current rotation |
| Supervising Consultant | Doctor reference | ✅ | Assigned consultant for current rotation |
| Log Book | Reference | System | Digital log book of procedures |
| Stipend | Currency | ✅ | Monthly stipend amount |
| Hostel Allotment | Text | ❌ | Room number if applicable |
| Attendance | Reference | System | Link to attendance record |

### 12.5.2 Intern Rotation Schedule (CRRI — 12 Months)

Standard rotation as per NMC (National Medical Commission) guidelines:

| # | Department | Duration | Key Learning Objectives |
|---|-----------|----------|------------------------|
| 1 | **General Medicine** | 2 months | History taking, physical examination, common disease management, ECG interpretation, ABG analysis |
| 2 | **General Surgery** | 2 months | Wound care, minor procedures (suturing, abscess drainage), pre-op evaluation, surgical assisting |
| 3 | **Obstetrics & Gynecology** | 1 month | Antenatal care, normal delivery, C-section assistance, family planning counseling |
| 4 | **Pediatrics** | 1 month | Newborn care, vaccination, common childhood illnesses, growth monitoring |
| 5 | **Orthopedics** | 15 days | Fracture management, plastering, splinting, joint examination |
| 6 | **Ophthalmology** | 15 days | Eye examination, visual acuity testing, common eye diseases |
| 7 | **ENT** | 15 days | Ear/nose/throat examination, foreign body removal, audiometry |
| 8 | **Emergency Medicine** | 1 month | Triage, ABCDE assessment, BLS/ACLS, trauma management, stabilization |
| 9 | **Community Medicine / Rural Posting** | 1 month | PHC operations, public health, epidemiology, immunization |
| 10 | **Anesthesia** | 15 days | Airway management, intubation, IV access, monitoring, pain management |
| 11 | **Psychiatry** | 15 days | Mental health assessment, common psychiatric conditions, counseling |
| 12 | **Elective** | 1 month | Any department of intern's choice (based on career interest) |
| | **TOTAL** | **12 months** | — |

*Note: Schedule may vary by hospital. The system allows fully configurable rotation schedules per hospital policy.*

### 12.5.3 Rotation Management Workflow

```
Academic Calendar Created (Annual)
          │
     ┌────▼──────────────┐
     │ Intern Batch        │ → New batch of interns onboarded
     │ Onboarding          │    (e.g., Batch 2026-A: 50 interns)
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Rotation Schedule  │ → Auto-generate rotation schedule
     │ Auto-Generated     │    based on template + batch size
     │                    │    Each dept gets equal intern count
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ HOD Review &       │ → Department HODs review and approve
     │ Approval            │    allocation for their department
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Intern Notified    │ → Intern receives rotation schedule
     │                    │    with department, dates, supervisor
     └────┬──────────────┘
          │
     ┌────▼──────────────────────────────────────────────────────┐
     │                    ROTATION IN PROGRESS                    │
     │                                                            │
     │  Day 1: Intern reports to department                      │
     │         → HOD/SR assigns duties, orientation               │
     │         → System access updated for department             │
     │                                                            │
     │  During rotation:                                          │
     │         → Procedures logged in digital log book            │
     │         → Attendance tracked                                │
     │         → Cases handled documented                          │
     │         → Supervisor provides periodic feedback             │
     │                                                            │
     │  Last week: Rotation completion assessment                  │
     │         → Supervisor evaluates intern (rating + comments)   │
     │         → Intern provides dept feedback (anonymous)         │
     │                                                            │
     └────┬──────────────────────────────────────────────────────┘
          │
     ┌────▼──────────────┐
     │ Rotation Complete  │ → Marked complete in system
     │                    │    System access revoked from old dept
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Next Rotation       │ → Auto-transition to next department
     │ Begins              │    System access granted for new dept
     └──────────────────┘
```

### 12.5.4 Intern Digital Log Book

Tracks procedures performed during internship:

| Field | Type | Description |
|-------|------|-------------|
| Date | Date | When procedure was done |
| Department | Dropdown | Current rotation department |
| Procedure | Searchable | From standard procedure list |
| Patient UHID | Reference | Link to patient record |
| Role | Dropdown | Observed / Assisted / Performed Under Supervision / Performed Independently |
| Supervisor | Doctor reference | Who supervised |
| Outcome | Text | Brief outcome description |
| Complications | Text | If any |
| Learning Points | Text | What was learned |
| Supervisor Sign-Off | Digital signature | Verified by supervisor |

**Minimum Procedure Requirements (Example):**

| Procedure | Min Required | Observed | Assisted | Performed |
|-----------|:----------:|:--------:|:--------:|:---------:|
| IV Cannulation | 20 | — | — | 20 |
| Blood Drawing | 20 | — | — | 20 |
| Urinary Catheterization | 5 | — | — | 5 |
| Ryles Tube Insertion | 5 | — | — | 5 |
| Suturing | 10 | — | 5 | 5 |
| Normal Delivery | 5 | 2 | 2 | 1 |
| Intubation | 3 | 1 | 1 | 1 |
| Plastering | 5 | 2 | 2 | 1 |
| ECG Recording | 20 | — | — | 20 |
| ABG Sampling | 5 | — | — | 5 |

The system tracks progress and alerts when minimum requirements are not met before rotation completion.

### 12.5.5 Intern Evaluation

| Evaluation Aspect | Weight | Evaluator |
|-------------------|--------|-----------|
| **Clinical Knowledge** | 25% | Supervising Consultant |
| **Clinical Skills** | 25% | Senior Resident |
| **Professional Behavior** | 15% | HOD / Consultant |
| **Communication** | 10% | Nursing feedback |
| **Attendance & Punctuality** | 15% | System (auto-tracked) |
| **Log Book Completion** | 10% | System (auto-calculated) |

Rating Scale: 1 (Poor) → 2 (Below Average) → 3 (Average) → 4 (Good) → 5 (Excellent)

**Completion Certificate:** Auto-generated when all rotations completed with satisfactory ratings and minimum procedure requirements met.

---

## 12.6 Resident Doctor Management

### 12.6.1 Resident Extended Profile

In addition to standard doctor profile:

| Field | Type | Description |
|-------|------|-------------|
| PG Course | MD / MS / DNB / Diploma | Post-graduate course |
| PG Specialty | Dropdown | Specialty (General Medicine, Surgery, Ortho, etc.) |
| PG Year | 1 / 2 / 3 | Current year of residency |
| PG Start Date | Date | Residency start |
| PG End Date | Date | Expected completion |
| Thesis Title | Text | Research thesis (mandatory for MD/MS) |
| Thesis Guide | Doctor reference | Faculty advisor |
| Thesis Status | Not Started / In Progress / Submitted / Approved | — |
| Academic Presentations | List | Conference presentations, journal publications |
| Examination Schedule | List | University exam dates |

### 12.6.2 Resident Duty Roster

| Shift Type | Timing | Description |
|-----------|--------|-------------|
| **Morning Duty** | 8 AM – 2 PM | Regular OPD/IPD duty |
| **Afternoon Duty** | 2 PM – 8 PM | Evening shift |
| **Night Duty** | 8 PM – 8 AM | Overnight call |
| **Emergency Duty** | 24 hours | ER posting |
| **OT Duty** | Per schedule | Assigned to specific surgeries |
| **On-Call** | From home | Available for emergency call-back |
| **Off Day** | — | Mandatory weekly off |

**Roster Rules (Configurable):**
- Maximum 80 hours work per week (NMC guideline)
- Minimum 1 day off per 7 days
- No more than 3 consecutive night duties
- Post night duty → mandatory next morning off
- Resident swap requests allowed with approval
- Festival/exam leave priority

### 12.6.3 Senior Resident (SR) — Additional Responsibilities

| Responsibility | Description |
|---------------|-------------|
| **First-Level Escalation** | JRs escalate clinical doubts and emergencies to SR first |
| **Intern Supervision** | Oversee intern work, co-sign procedures |
| **Emergency Response** | First responder for ward emergencies |
| **OPD In-Charge** | Run OPD clinics under consultant supervision |
| **Teaching** | Conduct bedside teaching for interns and JR-1s |
| **Administrative** | Ward administration, bed management coordination |

---

## 12.7 Cross-Department Management

### 12.7.1 Cross-Department Transfer (Permanent)

Used when a doctor is **permanently moved** from one department to another.

```
Transfer Request
(by Admin / HOD / Doctor Self-Request)
          │
     ┌────▼──────────────┐
     │ Request Created    │ → Reason, from dept, to dept
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Source HOD          │ → Approves release from current dept
     │ Approval            │    (may require notice period)
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Destination HOD    │ → Approves acceptance into new dept
     │ Approval            │
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Admin / MS         │ → Final approval
     │ Approval            │
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Transfer Executed  │ → On effective date:
     │                    │    • Primary department updated
     │                    │    • System access updated
     │                    │    • OPD schedule updated
     │                    │    • Patient reassignment triggered
     │                    │    • Notification to all stakeholders
     └──────────────────┘
```

**Transfer Data:**

| Field | Type | Mandatory |
|-------|------|-----------|
| Transfer ID | Auto-generated | System |
| Doctor | Reference | ✅ |
| From Department | Dropdown | System (current) |
| To Department | Dropdown | ✅ |
| Transfer Type | Permanent / Temporary / Rotation | ✅ |
| Reason | Text | ✅ |
| Effective Date | Date | ✅ |
| End Date | Date | Conditional (temporary only) |
| Patient Handover Plan | Text | ✅ |
| Source HOD Approval | Signature + Date | ✅ |
| Destination HOD Approval | Signature + Date | ✅ |
| Admin Approval | Signature + Date | ✅ |

### 12.7.2 Cross-Department Rotation (Temporary / Periodic)

Used for **interns, residents, and periodic departmental rotations:**

| Rotation Type | Used For | Duration | Description |
|--------------|---------|----------|-------------|
| **Intern CRRI Rotation** | Interns | 15 days – 2 months | Mandatory departmental rotations per NMC |
| **Resident Rotation** | JR / SR | 1–3 months | Sub-specialty exposure (e.g., Cardiology resident doing Echo rotation) |
| **Cross-Training** | Any doctor | 1–4 weeks | Learning a new technique/skill in another department |
| **Emergency Pool** | Residents | 1 month | ER duty rotation for all departments |
| **Rural/PHC Posting** | Interns | 1 month | Community health posting |
| **ICU Rotation** | Residents | 1–2 months | Critical care exposure for non-ICU residents |
| **Night Duty Pool** | Residents | Monthly | Cross-department night coverage |

**System Behavior During Rotation:**
1. Doctor's **primary department remains unchanged** (for payroll, reporting)
2. **Current rotation department** updated (for clinical access)
3. System access:
   - Granted for rotation department (clinical read/write)
   - Maintained for primary department (read-only or limited)
4. OPD schedule adjusted
5. Patient list updated to show rotation department patients
6. Reporting structure temporarily changed to rotation department HOD/SR
7. At rotation end → auto-revert all access to primary department

### 12.7.3 Cross-Department Consultation (Ad-Hoc)

When a doctor from one department is asked to see a patient in another department:

```
Primary Doctor Requests Cross-Consultation
          │
     ┌────▼──────────────┐
     │ Referral Created   │ → From: Dr. Mehta (Medicine)
     │                    │    To: Cardiology
     │                    │    Reason: "ECG shows ST elevation,
     │                    │    please review for possible ACS"
     │                    │    Priority: Urgent
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Notification to    │ → Alert sent to on-duty Cardiologist
     │ Consulting Dept    │    with patient summary auto-attached
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Consultant Reviews │ → Cardiologist scans patient QR
     │ Patient            │    Views records, examines patient
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Consultation Note  │ → Documented in patient record
     │ Created            │    Tagged as "Cross-Dept Consultation"
     │                    │    with specialty, recommendations
     └────┬──────────────┘
          │
     ┌────▼──────────────┐
     │ Primary Doctor     │ → Notified of consultation note
     │ Notified           │    Reviews recommendations
     └──────────────────┘
```

**Cross-Consultation Data:**

| Field | Type | Description |
|-------|------|-------------|
| Referral ID | Auto-generated | — |
| Requesting Doctor | Reference | Who is asking |
| Requesting Department | Auto-populated | — |
| Consulting Department | Dropdown | Target department |
| Consulting Doctor | Optional | Specific doctor or "Any available" |
| Patient UHID | Reference | — |
| Reason for Referral | Text | Clinical question/concern |
| Priority | Routine / Urgent / Emergency | — |
| Status | Requested → Accepted → In Progress → Completed → Acknowledged | — |
| Consultation Note | Clinical note | By consulting doctor |
| Recommendations | Text | Treatment recommendations |
| Response Time | Auto-calculated | Time from request to first response |
| TAT | Auto-calculated | Total turnaround time |

### 12.7.4 Multi-Department Privileges

For senior doctors who practice across departments:

| Example | Primary Dept | Additional Dept(s) | Use Case |
|---------|-------------|-------------------|----------|
| Cardiothoracic Surgeon | Cardiac Surgery | Cardiology, Thoracic Surgery | Operates and also manages cardiac patients |
| Intensivist | Critical Care | Medicine, Surgery, Neuro | Manages ICU patients from all departments |
| Interventional Radiologist | Radiology | Cardiology, Vascular Surgery, GI | Performs procedures across departments |
| Pain Specialist | Anesthesia | Orthopedics, Oncology, Palliative | Pain management across departments |
| Onco-Surgeon | Surgical Oncology | General Surgery, GI Surgery | Operates on cancer cases across surgical depts |

System provides:
- Clinical access to patient records in all assigned departments
- Separate OPD schedules per department
- Department-specific dashboards
- Unified patient list across all departments
- Proper billing routing based on where service was rendered

---

## 12.8 On-Call & Duty Roster Management

### 12.8.1 Roster Generation

```
Month-End Planning
       │
  ┌────▼──────────────┐
  │ Admin / HOD Creates│ → Select month, department
  │ New Roster         │    System loads doctor list + rules
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Auto-Generate      │ → Algorithm distributes duties:
  │ (AI-Assisted)      │    • Equal distribution of night/weekend duties
  │                    │    • Respect max-hour limits
  │                    │    • Account for leaves and exams
  │                    │    • Ensure minimum coverage per shift
  │                    │    • Senior + Junior pairing for nights
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Manual Adjustment  │ → HOD reviews and tweaks assignments
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Publish            │ → Roster published, notifications sent
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Swap Requests      │ → Doctors can request duty swaps
  │                    │    (Requires mutual consent + HOD approval)
  └──────────────────┘
```

### 12.8.2 Roster Dashboard

```
┌────────────────────────────────────────────────────────────────────┐
│  ON-CALL ROSTER - GENERAL MEDICINE              June 2026         │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  Date │ Morning (8A-2P) │ Afternoon (2P-8P) │ Night (8P-8A)      │
│  ─────┼─────────────────┼───────────────────┼─────────────────    │
│  21   │ Dr. Mehta (Cons) │ Dr. Sharma (Cons) │ Dr. Verma (SR)    │
│  Sat  │ Dr. Raj (SR)     │ Dr. Priya (SR)    │ Dr. Kumar (JR-2)  │
│       │ Dr. Anita (JR-2) │ Dr. Suresh (JR-1) │ Dr. Neha (JR-1)  │
│       │ Int. Rahul       │ Int. Meera         │ Int. Arjun        │
│  ─────┼─────────────────┼───────────────────┼─────────────────    │
│  22   │ Dr. Sharma (Cons)│ Dr. Mehta (Cons)  │ Dr. Raj (SR)      │
│  Sun  │ Dr. Priya (SR)   │ Dr. Verma (SR)    │ Dr. Anita (JR-2)  │
│       │ Dr. Suresh (JR-1)│ Dr. Kumar (JR-2)  │ Dr. Suresh (JR-1) │
│       │ Int. Meera       │ Int. Arjun         │ Int. Rahul        │
│  ─────┼─────────────────┼───────────────────┼─────────────────    │
│  ...  │ ...              │ ...               │ ...                │
│                                                                    │
│  Coverage Summary:                                                │
│  ✅ All shifts covered  │  ⚠️ Dr. Neha: 4 night duties (max 3)   │
│  Leave: Dr. Patel (22-25 Jun)                                     │
│                                                                    │
│  [Auto-Generate] [Edit] [Publish] [Download PDF]                  │
└────────────────────────────────────────────────────────────────────┘
```

---

## 12.9 Doctor Performance Analytics

### 12.9.1 Individual Doctor Dashboard

| Metric | Description |
|--------|-------------|
| **OPD Volume** | Patients seen per day/week/month (trend) |
| **IPD Census** | Current inpatients under care |
| **Average Consultation Time** | OPD avg consultation duration |
| **Revenue Generated** | Consultation + procedures + surgeries |
| **Surgery Volume** | Surgeries performed by type (trend) |
| **Complication Rate** | Post-surgical complications % |
| **Readmission Rate** | 30-day readmission for discharged patients |
| **Average Length of Stay** | For inpatients under care |
| **Mortality Rate** | In-hospital mortality % |
| **Documentation Compliance** | % of notes completed and signed on time |
| **Patient Satisfaction** | Average feedback score |
| **Pending Actions** | Unsigned notes, unreviewed results |
| **CME Credits** | Continuing education credits earned |
| **Attendance** | Duty attendance percentage |

### 12.9.2 Department Analytics

| Metric | Description |
|--------|-------------|
| **Department Census** | Total OPD + IPD volume |
| **Doctor Utilization** | Patient volume per doctor (load balancing) |
| **Intern/Resident Distribution** | Trainee allocation across department |
| **On-Call Coverage** | Coverage gaps or overloaded doctors |
| **Cross-Consultation TAT** | Average response time for referrals |
| **Teaching Metrics** | Trainee evaluations, log book completion |

---

## 12.10 Leave Management (Doctors)

### 12.10.1 Leave Types

| Leave Type | Eligibility | Max Days/Year | Approval |
|-----------|-------------|:-------------:|----------|
| **Casual Leave (CL)** | All | 12 | HOD |
| **Privilege Leave (PL)** | Permanent staff | 30 | HOD + Admin |
| **Sick Leave (SL)** | All | 20 | HOD (medical certificate if > 2 days) |
| **Academic Leave** | Faculty / Residents | 15 | HOD + Dean |
| **Conference Leave** | Faculty / Consultants | 10 | HOD |
| **Compensatory Off** | After extra duty | As earned | HOD |
| **Maternity Leave** | Female doctors | 180 | Admin |
| **Paternity Leave** | Male doctors | 15 | Admin |
| **Exam Leave** | Residents | As per exam schedule | HOD |
| **On-Duty Leave** | All | As required | HOD |
| **Without Pay Leave** | All | Case by case | Admin + Dean |

### 12.10.2 Leave Workflow

```
Doctor Applies for Leave
       │
  ┌────▼──────────┐
  │ System Checks  │ → Sufficient leave balance?
  │                │    Coverage available for duties?
  │                │    No existing leave clash?
  └────┬──────────┘
       │
  ┌────▼──────────┐
  │ HOD Approval   │ → Notified via app
  │ (Level 1)      │    Can approve / reject / modify dates
  └────┬──────────┘
       │ (If > X days or PL)
  ┌────▼──────────┐
  │ Admin Approval │ → For extended leaves
  │ (Level 2)      │
  └────┬──────────┘
       │
  ┌────▼──────────────────────────┐
  │ Approved                      │
  │ → Duty roster auto-updated    │
  │ → Substitute doctor assigned  │
  │ → OPD slots blocked           │
  │ → Patients notified of change │
  │ → Leave balance updated       │
  └───────────────────────────────┘
```

---

[→ Next: Table of Contents](./00_Table_of_Contents.md)
