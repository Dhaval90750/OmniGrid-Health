# MedCore HIS — Complete SRS vs Codebase Audit

This document presents a comprehensive, module-by-module audit comparing the requirements specified in the **System Requirements Specification (SRS)** against the actual implementation in the codebase (Database, Spring Boot Backend, and Next.js Frontend).

---

## Overall Implementation Summary

| Category | SRS Target | Codebase Current State | Completion % |
| :--- | :--- | :--- | :---: |
| **Database Tables** | 200–260 Tables | ~53 Tables | **20%** |
| **API Endpoints** | 500+ APIs | ~40 stubs/basic CRUD | **8%** |
| **Frontend Screens** | 250+ Screens | ~24 screens (mostly mockups) | **10%** |
| **Functional Modules** | 30+ Modules | 2 (Authentication & Patient Registration) | **7%** |
| **Seed/Master Data** | ICD-10, Wards, Beds, Tests, Drugs | Basic seeds for users & staff only | **1%** |

---

## Module-by-Module Gap Analysis

### 1. Authentication & Access Control (SRS §2)
* **SRS Requirement:** 14 user roles, RBAC, session management, MFA, PIN/biometric login, user account registration/creation.
* **Codebase Status:**
  * **Database:** `users`, `roles`, `permissions`, `role_permissions`, `user_roles` exist.
  * **Backend:** `AuthController` (`/api/v1/auth/login`) uses BCrypt and JWT. *Note: Fixed role doubling bug.*
  * **Frontend:** Protected routes with middleware redirecting to `/login`.
  * **What's Missing:** User Registration/Creation endpoint (no `/auth/register` exists), CRUD endpoints for user/role management, session tracking, MFA, biometric login, and 13 out of 14 default roles.

### 2. Patient Registration & Profile (SRS §3.2)
* **SRS Requirement:** Demographic capturing, photo capture, unique UHID, QR codes, emergency contact relationships.
* **Codebase Status:**
  * **Database:** `patients` table has fields for detailed demographics.
  * **Backend:** `PatientService` handles registrations, auto-generates UHIDs (`MED-YYYY-XXXXXX`), and encodes QR codes.
  * **Frontend:** Fully responsive form in `/patients/new` with state/country dropdowns, emergency contact relations, and Aadhaar inputs. Profile screen shows the live QR code.
  * **What's Missing:** Patient photo uploads, duplicate detection algorithms, patient search/merge functionality.

### 3. OPD & Visit Management (SRS §3.3)
* **SRS Requirement:** Visit workflows (walk-in, follow-up, referral), token queues, queue display systems.
* **Codebase Status:**
  * **Database:** Basic `visits` table (status, chief complaint).
  * **Backend:** `VisitController` contains stub endpoints.
  * **Frontend:** Visited queues are simulated/hardcoded in mock dashboards.
  * **What's Missing:** Token generation service, live queue status dashboard, doctor allocation system.

### 4. Admission, Discharge, and Transfer / ADT (SRS §3.4)
* **SRS Requirement:** Bed availability checking, ward transfers, advance deposit collection, automatic bed release.
* **Codebase Status:**
  * **Database:** `admissions` table exists.
  * **Backend:** Basic `AdmissionService` with simple CRUD.
  * **Frontend:** Admissions list and new admission screens exist as static mockups.
  * **What's Missing:** Real-time bed selection, transfer tracking logs, advance payments integration.

### 5. Doctor Workflow & Clinical Notes (SRS §3.5-3.7)
* **SRS Requirement:** HPI notes, physical examinations, SOAP templates, ICD-10 diagnosis search, AI voice dictation.
* **Codebase Status:**
  * **Database:** `clinical_notes` and `diagnoses` tables exist.
  * **Backend:** No dedicated controller; no active ICD-10 search API.
  * **Frontend:** Mock doctor dashboard and visit summary sheets.
  * **What's Missing:** SOAP template generator, active ICD-10 lookup backend integration, voice recorder UI.

### 6. Laboratory Information System / LIS (SRS §4.2)
* **SRS Requirement:** Test ordering, sample collection (barcodes), analyzer interfaces, reference ranges, authorization flow.
* **Codebase Status:**
  * **Database:** `lab_tests`, `lab_orders`, `lab_samples`, and `lab_results` tables exist.
  * **Backend:** `LabService` with basic CRUD.
  * **Frontend:** LIS dashboard mockup.
  * **What's Missing:** Automated barcode generator/reader integration, abnormal value flags, approval workflows, reference range validations.

### 7. Radiology Information System / RIS (SRS §4.3)
* **SRS Requirement:** Modality selection, PACS/DICOM viewer integration, reporting templates (BI-RADS).
* **Codebase Status:**
  * **Database:** `radiology_orders`, `radiology_reports`, `radiology_templates` exist.
  * **Backend:** `RadiologyService` with CRUD.
  * **Frontend:** RIS report viewer mockup.
  * **What's Missing:** Actual DICOM link or media uploader, template-based auto-fill reporting.

### 8. Pharmacy Management (SRS §5.4)
* **SRS Requirement:** Stock tracking, batch expiries, FEFO enforcement, controlled/narcotic drug registers.
* **Codebase Status:**
  * **Database:** `pharmacy_stock`, `stock_movements`, `dispensing_records` exist.
  * **Backend:** `PharmacyService` with basic inventory updates.
  * **Frontend:** Pharmacy page mockup.
  * **What's Missing:** FEFO stock depletion validation, narcotic registry logging, auto-billing integration.

### 9. Billing, Invoices, and Revenue (SRS §5.1)
* **SRS Requirement:** OPD/IPD/Pharmacy invoicing, tariff masters, package configurations, GST/tax calculations.
* **Codebase Status:**
  * **Database:** `invoices`, `invoice_items`, `payments`, `ipd_bills`, `bill_items` exist.
  * **Backend:** `BillingService` and `IpdBillingService` (basic CRUD).
  * **Frontend:** Invoices list and details mockup.
  * **What's Missing:** Automatic charge capturing from visits/orders, tax calculators, invoice printing, refund processing.

### 10. Operations & Housekeeping (SRS §7)
* **SRS Requirement:** Work orders, porter requests, housekeeping checklists.
* **Codebase Status:**
  * **Database:** `housekeeping_tasks`, `work_orders`, `transport_requests` exist.
  * **Backend:** `OperationsService` with simple CRUD.
  * **Frontend:** Operations checklist mockup.
  * **What's Missing:** Live ticket assignments, SLA breach warnings, ambulance/dietary modules.

---

## Technical Debt & Infrastructure Gaps
1. **Security Rules:** Many controllers have commented-out `@PreAuthorize` annotations, letting any authenticated user access all APIs.
2. **Flyway Migrations:** Versioned DB migrations are disabled (`flyway.enabled: false`). The schema relies on manual/hibernate validation checks.
3. **Error Handling:** Missing a global `@ControllerAdvice` handler, meaning exceptions result in raw, unstyled 500 error pages.
4. **Pagination:** API endpoints return unbounded lists, which will degrade performance as data grows.
