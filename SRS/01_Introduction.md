# Software Requirements Specification (SRS)

## MedCore HIS — Hospital Information System

**Version:** 1.0  
**Date:** 2026-06-21  
**Document Type:** Software Requirements Specification (IEEE 830 Standard)  
**Classification:** Confidential  
**Design Theme:** Light Mode (System-Wide)

---

# 1. Introduction

## 1.1 Purpose

This Software Requirements Specification (SRS) document provides a complete and detailed description of the requirements for **MedCore HIS** — an enterprise-grade Hospital Information System designed to serve large hospitals handling thousands of patients daily.

MedCore HIS is not a simple Patient Management System. It is a unified platform that integrates:

- **Hospital Information System (HIS)** — Centralized hospital operations management
- **Electronic Medical Record (EMR)** — Lifelong digital patient health records
- **Clinical CRM** — Patient relationship and follow-up management
- **Laboratory Information System (LIS)** — End-to-end lab workflow automation
- **Radiology Information System (RIS)** — Imaging workflow and PACS integration
- **Pharmacy Information System (PIS)** — Drug dispensing, stock, and prescription management
- **Doctor Workflow Platform** — AI-assisted clinical documentation and decision support
- **Nursing Information System (NIS)** — Bedside care tracking and shift management
- **Revenue Cycle Management (RCM)** — Billing, insurance, and financial operations

This document is intended for:

| Audience | Usage |
|----------|-------|
| Development Team | Build and implement the system |
| QA Team | Derive test cases and validation criteria |
| Project Managers | Track scope and deliverables |
| Hospital Stakeholders | Validate that requirements match operational needs |
| UI/UX Designers | Understand workflows and design screens |
| Compliance Officers | Verify regulatory adherence |

## 1.2 Scope

### 1.2.1 System Name

**MedCore HIS** (Hospital Information System)

### 1.2.2 System Boundaries

MedCore HIS covers the entire patient lifecycle from the moment a patient walks into the hospital until discharge, follow-up, and long-term medical record management. The system boundary includes:

**In Scope:**

- Patient Registration and unique identification (UHID + QR Code)
- Outpatient Department (OPD) management
- Inpatient Department (IPD) management
- Emergency / Casualty / Trauma management
- Doctor workflow and clinical documentation
- AI-powered voice-to-clinical-notes
- AI SOAP note generation
- Laboratory Information System with multi-department support
- Radiology module with PACS integration
- Pharmacy and drug dispensing
- Prescription management with drug interaction checking
- Nursing workflows and vital monitoring
- ICU management with real-time monitoring
- Operation Theatre scheduling and management
- Bed management with real-time dashboards
- Billing and revenue cycle management
- Insurance and TPA (Third Party Administrator) claim processing
- Discharge workflow with automated summary generation
- Infection control and antimicrobial stewardship
- Blood bank management
- Diet and nutrition management
- Medical Records Department (MRD) and coding
- Role-based access control (RBAC)
- Audit trail and compliance logging
- Real-time alerts and notification engine
- Management dashboards and analytics
- Mobile application for clinical staff
- QR-code-based patient tracking across all departments

**Out of Scope (Phase 1):**

- Telemedicine/video consultation
- Patient-facing mobile app (patient portal)
- Multi-hospital federation
- Research data warehouse
- Genomics integration
- Wearable device integration

### 1.2.3 Scale Parameters

| Parameter | Target |
|-----------|--------|
| Concurrent Users | 500–2,000 |
| Daily Patient Registrations | 500–5,000 |
| Daily OPD Visits | 2,000–10,000 |
| Active IPD Patients | 500–3,000 |
| Daily Lab Tests | 5,000–20,000 |
| Daily Radiology Studies | 200–1,000 |
| Total Hospital Beds | 500–5,000 |
| Medical Records Retention | 10+ years |
| System Availability | 99.95% uptime |
| Response Time (P95) | < 500ms for clinical screens |

## 1.3 Definitions, Acronyms, and Abbreviations

| Term | Definition |
|------|-----------|
| **UHID** | Unique Hospital Identification Number — A permanent, system-generated identifier assigned to every patient on first registration. This ID persists across all visits, admissions, and departments. |
| **QR Code** | Quick Response Code — A machine-readable 2D barcode encoding the patient's UHID and visit context. Printed on wristbands for inpatients and on registration slips for outpatients. |
| **HIS** | Hospital Information System — The overarching platform managing all hospital operations. |
| **EMR** | Electronic Medical Record — Digital version of a patient's paper chart containing medical history, diagnoses, medications, treatment plans, immunization dates, allergies, radiology images, and laboratory results. |
| **LIS** | Laboratory Information System — Software that manages the ordering, processing, and reporting of laboratory tests. |
| **RIS** | Radiology Information System — Software for managing radiology imaging workflows. |
| **PACS** | Picture Archiving and Communication System — Medical imaging storage and retrieval system using DICOM standard. |
| **DICOM** | Digital Imaging and Communications in Medicine — The international standard for medical images and related information. |
| **HL7** | Health Level Seven — International standard for electronic health information exchange. |
| **FHIR** | Fast Healthcare Interoperability Resources — Modern standard for exchanging healthcare information electronically. |
| **ICD-10** | International Classification of Diseases, 10th Revision — WHO standard for coding diagnoses. |
| **ICD-11** | International Classification of Diseases, 11th Revision — Latest WHO classification. |
| **SNOMED CT** | Systematized Nomenclature of Medicine — Clinical terminology system. |
| **LOINC** | Logical Observation Identifiers Names and Codes — Standard for identifying medical laboratory observations. |
| **SOAP** | Subjective, Objective, Assessment, Plan — Standard format for clinical notes. |
| **OPD** | Outpatient Department — For patients who visit but are not admitted. |
| **IPD** | Inpatient Department — For patients who are admitted and stay in the hospital. |
| **ICU** | Intensive Care Unit — Critical care ward. |
| **NICU** | Neonatal Intensive Care Unit — Critical care for newborns. |
| **OT** | Operation Theatre — Surgical facility. |
| **TPA** | Third Party Administrator — Insurance intermediary. |
| **RBAC** | Role-Based Access Control — Security model restricting system access based on user roles. |
| **ABAC** | Attribute-Based Access Control — Fine-grained access control using user/resource attributes. |
| **ABDM** | Ayushman Bharat Digital Mission — India's national digital health ecosystem. |
| **ABHA** | Ayushman Bharat Health Account — Unique health ID under ABDM. |
| **MRD** | Medical Records Department — Department managing physical and digital patient records. |
| **ADT** | Admission, Discharge, Transfer — Core patient movement workflow. |
| **TAT** | Turnaround Time — Time from order to result delivery. |
| **MLC** | Medico-Legal Case — Cases requiring legal documentation. |
| **BRD** | Business Requirements Document |
| **SRS** | Software Requirements Specification |
| **NLP** | Natural Language Processing — AI technology for understanding human language. |
| **LLM** | Large Language Model — AI model for text generation and understanding. |
| **COAS** | Clinical Observations Access Service — Standard for clinical data access. |

## 1.4 References

| Reference | Description |
|-----------|-------------|
| IEEE 830-1998 | IEEE Recommended Practice for Software Requirements Specifications |
| HL7 FHIR R4 | [https://hl7.org/fhir/](https://hl7.org/fhir/) |
| ICD-10 | [https://icd.who.int/browse10](https://icd.who.int/browse10) |
| ICD-11 | [https://icd.who.int/browse/2024-01/mms/en](https://icd.who.int/browse/2024-01/mms/en) |
| SNOMED CT | [https://www.snomed.org/](https://www.snomed.org/) |
| LOINC | [https://loinc.org/](https://loinc.org/) |
| DICOM Standard | [https://www.dicomstandard.org/](https://www.dicomstandard.org/) |
| NABH Standards | National Accreditation Board for Hospitals and Healthcare Providers |
| HIPAA | Health Insurance Portability and Accountability Act (US) |
| ABDM Standards | [https://abdm.gov.in/](https://abdm.gov.in/) |
| DISHA Bill (India) | Digital Information Security in Healthcare Act |

## 1.5 System Overview

### 1.5.1 The QR-Centric Architecture

The foundational design principle of MedCore HIS is **QR-Centric Patient Tracking**. Every interaction with the patient, across every department, begins with a QR scan.

```
┌─────────────────────────────────────────────────────────────────┐
│                      PATIENT ARRIVES                            │
│                           │                                     │
│                    ┌──────▼──────┐                               │
│                    │ REGISTRATION │                              │
│                    │  UHID + QR   │                              │
│                    └──────┬──────┘                               │
│                           │                                     │
│              ┌────────────┼────────────┐                        │
│              │            │            │                        │
│        ┌─────▼─────┐ ┌───▼────┐ ┌─────▼──────┐                │
│        │  OPD Visit │ │  IPD   │ │ Emergency  │                │
│        └─────┬─────┘ │Admission│ │  Casualty  │                │
│              │        └───┬────┘ └─────┬──────┘                │
│              │            │            │                        │
│              └────────────┼────────────┘                        │
│                           │                                     │
│            ┌──────────────┼──────────────┐                      │
│            │              │              │                      │
│      ┌─────▼─────┐ ┌─────▼─────┐ ┌─────▼─────┐               │
│      │  Doctor    │ │   Lab     │ │ Radiology │               │
│      │ Assessment│ │  Orders   │ │  Orders   │               │
│      └─────┬─────┘ └─────┬─────┘ └─────┬─────┘               │
│            │              │              │                      │
│      ┌─────▼─────┐ ┌─────▼─────┐ ┌─────▼─────┐               │
│      │Prescription│ │  Results  │ │  Reports  │               │
│      └─────┬─────┘ └─────┬─────┘ └─────┬─────┘               │
│            │              │              │                      │
│            └──────────────┼──────────────┘                      │
│                           │                                     │
│                    ┌──────▼──────┐                               │
│                    │  PHARMACY   │                               │
│                    └──────┬──────┘                               │
│                           │                                     │
│                    ┌──────▼──────┐                               │
│                    │   BILLING   │                               │
│                    └──────┬──────┘                               │
│                           │                                     │
│                    ┌──────▼──────┐                               │
│                    │  DISCHARGE  │                               │
│                    └──────┬──────┘                               │
│                           │                                     │
│                    ┌──────▼──────┐                               │
│                    │  FOLLOW-UP  │                               │
│                    └─────────────┘                               │
└─────────────────────────────────────────────────────────────────┘
```

### 1.5.2 Design Philosophy

| Principle | Description |
|-----------|-------------|
| **QR-First** | Every patient interaction starts with a QR scan. No manual searching. |
| **Zero Duplicate Records** | UHID ensures a single record per patient for life. |
| **Real-Time** | All dashboards, bed status, lab results update in real-time via WebSocket. |
| **AI-Assisted** | Voice dictation, SOAP generation, clinical coding — AI augments, humans approve. |
| **Mobile-First Clinical** | Doctors and nurses use mobile devices at bedside. |
| **Audit Everything** | Every action is logged with who, what, when, where, and from which device. |
| **Light Mode UI** | System-wide light mode theme with clean, clinical aesthetics. |
| **Offline Capable** | Mobile apps work offline and sync when connectivity returns. |
| **Standards Compliant** | ICD-10/11, SNOMED CT, LOINC, HL7 FHIR, DICOM for interoperability. |
| **NABH Ready** | All workflows designed to meet NABH accreditation requirements. |
