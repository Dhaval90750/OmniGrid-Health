# MedCore HIS — Software Requirements Specification

## Table of Contents

**Version:** 1.0 | **Date:** 2026-06-21 | **Theme:** Light Mode (System-Wide)

---

> **MedCore HIS** is an enterprise-grade Hospital Information System integrating HIS, EMR, LIS, RIS, PIS, and AI-powered clinical workflows into a single QR-centric platform.

---

### Document Index

| # | Section | File | Key Topics |
|---|---------|------|------------|
| 1 | [Introduction](./01_Introduction.md) | `01_Introduction.md` | Purpose, scope, definitions, acronyms, QR-centric architecture, design philosophy |
| 2 | [User Roles & Access Control](./02_User_Roles_Access_Control.md) | `02_User_Roles_Access_Control.md` | 14 user roles, RBAC + ABAC model, permission matrices, authentication, break-the-glass protocol |
| 3 | [Patient Lifecycle & Clinical Workflows](./03_Patient_Lifecycle.md) | `03_Patient_Lifecycle.md` | Registration, OPD/IPD, admission, doctor assessment, AI voice notes, SOAP generation, discharge, emergency/casualty, MLC |
| 4 | [Clinical Modules](./04_Clinical_Modules.md) | `04_Clinical_Modules.md` | EMR, LIS (with 100+ test catalog), Radiology/PACS, Prescription (with CDS), Nursing, ICU, OT, Infection Control, Blood Bank, Diet |
| 5 | [Administrative & Support Modules](./05_Administrative_Modules.md) | `05_Administrative_Modules.md` | Billing/RCM, Insurance/TPA, Bed Management, Pharmacy, Emergency, Document Management, Notifications, Audit Trail, MRD |
| 6 | [Inventory Management](./06_Inventory_Management.md) | `06_Inventory_Management.md` | Store hierarchy, item master, procurement (indent → PO → GRN), stock operations, implant tracking, fixed assets, vendor management, CSSD, linen |
| 7 | [Operations Management](./07_Operations_Management.md) | `07_Operations_Management.md` | Housekeeping, maintenance/work orders, patient transport, ambulance fleet, dietary/kitchen, biomedical waste, security, energy, mortuary, help desk |
| 8 | [AI Features & Roadmap](./08_AI_Features.md) | `08_AI_Features.md` | Voice-to-clinical-notes, SOAP generation, ICD coding, drug interactions, AI doctor assistant, risk prediction (sepsis/readmission), radiology AI, governance |
| 9 | [Non-Functional Requirements](./09_Non_Functional_Requirements.md) | `09_Non_Functional_Requirements.md` | Performance targets, availability (99.95%), security, scalability, light mode UI design system, compatibility, localization, deployment options |
| 10 | [System Architecture](./10_Architecture.md) | `10_Architecture.md` | High-level architecture, tech stack (Spring Boot + React + Android + PostgreSQL), API design, event-driven data flow, database schema (200+ tables), integrations |
| 11 | [UI Screens & Effort Estimation](./11_UI_Screens_Effort.md) | `11_UI_Screens_Effort.md` | 350 screens (250 web + 100 mobile), 27+ modules, ~248 person-months, team composition, light mode design tokens, testing strategy |
| 12 | [Doctor & Medical Staff Management](./12_Doctor_Staff_Management.md) | `12_Doctor_Staff_Management.md` | Full doctor hierarchy (Dean → Intern), credentialing, privilege matrix, intern CRRI rotations, cross-department transfers, on-call rosters, performance analytics, leave management |

---

### Module Summary

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         MEDCORE HIS — MODULE MAP                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─── CLINICAL ──────────────────────────────────────────────────────────┐  │
│  │  Patient Registration │ OPD │ IPD/ADT │ EMR │ Clinical Notes         │  │
│  │  Prescriptions │ Lab (LIS) │ Radiology (RIS) │ Nursing │ ICU         │  │
│  │  OT Management │ Infection Control │ Blood Bank │ Diet & Nutrition   │  │
│  │  Emergency/Casualty │ Discharge │ AI Voice │ AI SOAP │ AI Coding     │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  ┌─── ADMINISTRATIVE ────────────────────────────────────────────────────┐  │
│  │  Billing │ Insurance/TPA │ Bed Management │ Pharmacy                  │  │
│  │  Document Management │ Medical Records (MRD) │ Notification Engine   │  │
│  │  Audit Trail │ System Configuration │ Role & Permission Management   │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  ┌─── DOCTOR & STAFF ────────────────────────────────────────────────────┐  │
│  │  Doctor Hierarchy (Dean → Intern) │ Credentialing & Privileges       │  │
│  │  Intern Rotation (CRRI) │ Resident Management │ Cross-Dept Transfer  │  │
│  │  On-Call Roster │ Leave Management │ Performance Analytics            │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  ┌─── INVENTORY ─────────────────────────────────────────────────────────┐  │
│  │  Central/Sub-Store Management │ Procurement (Indent → PO → GRN)      │  │
│  │  Stock Operations │ Implant Tracking │ Fixed Assets │ Vendor Mgmt    │  │
│  │  CSSD │ Linen & Laundry │ ABC/VED Analysis                           │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  ┌─── OPERATIONS ────────────────────────────────────────────────────────┐  │
│  │  Housekeeping │ Facility Maintenance │ Patient Transport              │  │
│  │  Ambulance Fleet │ Dietary/Kitchen │ Biomedical Waste │ Security     │  │
│  │  Energy/Utility │ Mortuary │ Help Desk │ Staff Scheduling            │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  ┌─── ANALYTICS & AI ───────────────────────────────────────────────────┐  │
│  │  Hospital Dashboard │ Doctor Dashboard │ Lab Dashboard │ Revenue     │  │
│  │  AI Voice-to-Notes │ AI SOAP │ AI Risk Prediction │ AI Coding       │  │
│  │  Custom Report Builder │ Quality Indicators │ KPI Monitoring         │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  ┌─── PLATFORM ─────────────────────────────────────────────────────────┐  │
│  │  QR-Centric Tracking │ RBAC/ABAC │ Audit Engine │ Notification       │  │
│  │  HL7 FHIR │ DICOM │ ABDM │ Multi-Language │ Light Mode Theme        │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

### Key Numbers

| Metric | Value |
|--------|-------|
| **Total Modules** | 30+ |
| **Total Screens** | ~350 (250 web + 100 mobile) |
| **Database Tables** | ~200–260 |
| **API Endpoints** | ~500+ |
| **User Roles** | 14 |
| **Doctor Types** | 20 |
| **Lab Tests Supported** | 500+ (expandable) |
| **Radiology Modalities** | 11 |
| **Estimated Effort** | ~248 person-months |
| **Estimated Timeline** | 21–30 months |
| **Peak Team Size** | 20–25 |
| **UI Theme** | ☀️ Light Mode (System-Wide) |

---

### Standards Compliance

| Standard | Coverage |
|----------|---------|
| **ICD-10 / ICD-11** | Diagnosis coding |
| **SNOMED CT** | Clinical terminology |
| **LOINC** | Lab test identification |
| **HL7 FHIR R4** | Health data interoperability |
| **DICOM** | Medical imaging |
| **ABDM / ABHA** | India Digital Health Stack |
| **NABH** | Hospital accreditation readiness |
| **HIPAA** | Data privacy (international) |
| **NMC Guidelines** | Doctor training & rotation compliance |

---

*Document generated: 2026-06-21 | MedCore HIS v1.0 SRS*
