# 11. UI Screens & Estimated Effort

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 11.1 Screen Inventory

### 11.1.1 Module-Wise Screen Breakdown

| # | Module | Web Screens | Mobile Screens | Total |
|---|--------|:-----------:|:--------------:|:-----:|
| 1 | **Authentication & User Management** | 8 | 4 | 12 |
| 2 | **Patient Registration** | 6 | 3 | 9 |
| 3 | **OPD Management** | 8 | 4 | 12 |
| 4 | **Admission / ADT** | 10 | 4 | 14 |
| 5 | **Doctor Workflow / Clinical Notes** | 12 | 8 | 20 |
| 6 | **AI Voice & SOAP** | 4 | 4 | 8 |
| 7 | **Prescription Management** | 6 | 4 | 10 |
| 8 | **Nursing Module** | 10 | 8 | 18 |
| 9 | **Laboratory (LIS)** | 14 | 6 | 20 |
| 10 | **Radiology (RIS)** | 10 | 3 | 13 |
| 11 | **Pharmacy** | 12 | 4 | 16 |
| 12 | **ICU Management** | 8 | 4 | 12 |
| 13 | **OT Management** | 10 | 3 | 13 |
| 14 | **Bed Management** | 6 | 2 | 8 |
| 15 | **Billing & Payments** | 12 | 2 | 14 |
| 16 | **Insurance & TPA** | 8 | 1 | 9 |
| 17 | **Discharge** | 6 | 3 | 9 |
| 18 | **Inventory Management** | 16 | 4 | 20 |
| 19 | **Operations Management** | 14 | 6 | 20 |
| 20 | **Blood Bank** | 6 | 1 | 7 |
| 21 | **Infection Control** | 6 | 2 | 8 |
| 22 | **Diet & Nutrition** | 6 | 2 | 8 |
| 23 | **Medical Records (MRD)** | 6 | 1 | 7 |
| 24 | **Document Management** | 4 | 2 | 6 |
| 25 | **Notification Center** | 3 | 3 | 6 |
| 26 | **Dashboards & Analytics** | 12 | 4 | 16 |
| 27 | **Audit Trail** | 4 | 0 | 4 |
| 28 | **System Configuration / Master Data** | 15 | 0 | 15 |
| 29 | **Emergency / Casualty** | 6 | 4 | 10 |
| 30 | **Help Desk / Service Requests** | 4 | 3 | 7 |
| | **GRAND TOTAL** | **250** | **100** | **350** |

---

### 11.1.2 Key Screen Descriptions

#### Authentication & User Management (12 screens)
| Screen | Platform | Description |
|--------|----------|-------------|
| Login | Web + Mobile | Username/password + MFA option |
| PIN Login | Mobile | Quick PIN entry for ward devices |
| Biometric Login | Mobile | Fingerprint authentication |
| Forgot Password | Web | Password reset flow |
| User List | Web | Searchable user directory with role filters |
| User Create/Edit | Web | Create user with role, department, permissions |
| Role Management | Web | Create/edit roles with permission matrix |
| My Profile | Web + Mobile | View/edit own profile, change password |

#### Patient Registration (9 screens)
| Screen | Platform | Description |
|--------|----------|-------------|
| Patient Search | Web + Mobile | Search by UHID, name, mobile, QR scan |
| New Registration | Web | Full registration form with photo capture |
| Quick Registration (ER) | Web + Mobile | Minimal fields for emergency |
| Edit Demographics | Web | Update patient information |
| Patient Card Print | Web | Print patient card with QR |
| Wristband Print | Web | Print QR wristband for inpatient |
| Duplicate Review | Web | Review potential duplicate patients |

#### Doctor Workflow (20 screens)
| Screen | Platform | Description |
|--------|----------|-------------|
| My Patients | Web + Mobile | Today's patient list (OPD + IPD) |
| Patient Summary Dashboard | Web + Mobile | 360° patient view on QR scan |
| Clinical Note - HPI | Web + Mobile | History of Present Illness entry |
| Clinical Note - Examination | Web + Mobile | Physical examination with body diagrams |
| Diagnosis Entry | Web + Mobile | ICD search, multiple diagnoses |
| Voice Dictation | Mobile | Voice recording for clinical notes |
| AI Note Review | Web + Mobile | Review AI-generated clinical note |
| SOAP Note View | Web + Mobile | Auto-generated SOAP with edit capability |
| Order Entry (Lab) | Web + Mobile | Order lab tests with order sets |
| Order Entry (Radiology) | Web + Mobile | Order imaging studies |
| Referral | Web | Create inter/external referral |
| Progress Note | Web + Mobile | Daily progress note for IPD |
| Consent Form | Web | Generate and capture consent |
| Discharge Initiation | Web | Approve discharge, review summary |

#### Nursing Module (18 screens)
| Screen | Platform | Description |
|--------|----------|-------------|
| Ward Dashboard | Web + Mobile | Ward overview with patient list, alerts |
| Vital Entry | Web + Mobile | Enter vitals with trends chart |
| Vital Trends | Web + Mobile | Graphical vital trends with NEWS score |
| MAR (Medication Admin) | Web + Mobile | Medication administration record |
| Barcode Verification | Mobile | Scan med barcode + patient QR |
| Nursing Assessment | Web + Mobile | Falls risk, pressure ulcer, pain |
| I/O Charting | Web + Mobile | Intake/output recording |
| Shift Handover | Web | SBAR handover form |
| Incident Report | Web + Mobile | Report adverse events |
| Wound Assessment | Mobile | Wound photo + measurement |

#### Laboratory (20 screens)
| Screen | Platform | Description |
|--------|----------|-------------|
| Lab Worklist | Web + Mobile | Pending orders by section |
| Sample Collection | Mobile | Barcode labeling, collection confirmation |
| Sample Reception | Web | Accept/reject samples |
| Result Entry | Web | Manual result entry with reference ranges |
| Analyzer Interface | Web | View auto-populated results from analyzers |
| QC Dashboard | Web | Quality control monitoring |
| Report Preview | Web | Preview report before authorization |
| Report Authorization | Web | Pathologist review and sign |
| Critical Value Alert | Web + Mobile | Critical value notification with acknowledge |
| TAT Dashboard | Web | Turnaround time monitoring |
| Test Catalog Management | Web | Manage test profiles, panels |
| Reference Range Config | Web | Configure age/gender-specific ranges |
| Reagent Management | Web | Track reagent stock, lot, expiry |

#### Inventory Management (20 screens)
| Screen | Platform | Description |
|--------|----------|-------------|
| Stock Dashboard | Web | Real-time stock levels by store, category |
| Item Master | Web | Create/edit items with classifications |
| Purchase Indent | Web + Mobile | Raise purchase requisition |
| Indent Approval | Web | Multi-level approval workflow |
| RFQ (Request for Quotation) | Web | Compare vendor quotes |
| Purchase Order | Web | Create/approve purchase orders |
| Goods Receipt (GRN) | Web + Mobile | Receive goods, quality check |
| Stock Issue | Web + Mobile | Issue to departments, patient-level |
| Inter-Store Transfer | Web | Transfer between stores |
| Stock Return | Web | Returns (ward, patient, vendor) |
| Stock Adjustment | Web | Write-off, positive/negative adjustments |
| Physical Stock Audit | Web + Mobile | Physical count entry and reconciliation |
| Vendor Master | Web | Vendor profiles and performance |
| Asset Register | Web | Fixed asset tracking |
| AMC/CMC Management | Web | Contract tracking |
| Implant Register | Web | Implant traceability |
| Expiry Management | Web | Expiring items alert and action |
| Reports & Analytics | Web | Consumption, variance, ABC analysis |

#### Operations Management (20 screens)
| Screen | Platform | Description |
|--------|----------|-------------|
| Operations Dashboard | Web | Overview of all operations KPIs |
| Housekeeping Schedule | Web | Zone-wise cleaning schedule |
| Housekeeping Task Tracker | Web + Mobile | Track cleaning task completion |
| Bed Turnaround | Web + Mobile | Discharge cleaning workflow |
| Work Order Create | Web + Mobile | Raise maintenance request |
| Work Order List | Web | View/manage all work orders |
| Work Order Detail | Web + Mobile | Track work order lifecycle |
| PM Schedule | Web | Preventive maintenance calendar |
| Equipment Register | Web | Equipment list with maintenance history |
| Transport Request | Web + Mobile | Internal patient transport |
| Ambulance Tracking | Web | Fleet tracking on map |
| Kitchen Dashboard | Web | Meal counts, diet orders, preparation status |
| Diet Order | Web + Mobile | Patient diet order entry |
| BMW Tracking | Web + Mobile | Biomedical waste logging |
| Visitor Management | Web | Visitor registration, badge printing |
| Security Log | Web | Incident logging |
| Linen Management | Web | Linen stock and laundry tracking |
| Help Desk | Web + Mobile | Service request portal |
| Energy Dashboard | Web | Utility consumption monitoring |

#### Dashboards & Analytics (16 screens)
| Screen | Platform | Description |
|--------|----------|-------------|
| Hospital Overview | Web | Key metrics summary |
| OPD Dashboard | Web | OPD volume, wait times, doctor utilization |
| IPD Dashboard | Web | Census, occupancy, discharges, LOS |
| ER Dashboard | Web | Arrivals, triage distribution, wait times |
| Lab Dashboard | Web + Mobile | Volume, TAT, critical values |
| Radiology Dashboard | Web | Volume, TAT by modality |
| Pharmacy Dashboard | Web | Dispensing volume, stock alerts |
| Revenue Dashboard | Web | Revenue, collections, outstanding |
| Infection Control Dashboard | Web | HAI rates, antibiogram |
| Doctor Productivity | Web | Patient volume, documentation completion |
| Quality Indicators | Web | NABH indicators, mortality, readmission |
| Operations Dashboard | Web | Maintenance, housekeeping, utilities |
| Inventory Dashboard | Web | Stock levels, consumption, procurement |
| Custom Report Builder | Web | Drag-and-drop report creation |

---

## 11.2 Development Effort Estimation

### 11.2.1 Phase-Wise Breakdown

| Phase | Modules | Duration | Team Size |
|-------|---------|----------|-----------|
| **Phase 1 (MVP)** | Patient Registration, OPD, IPD/ADT, Doctor Workflow, Prescription, Lab (basic), Bed Management, Billing (basic), Auth/RBAC, QR System | 6–8 months | 12–15 developers |
| **Phase 2** | Full LIS, Radiology, Pharmacy, Nursing, ICU, Discharge, Insurance/TPA, Full Billing | 4–6 months | 12–15 developers |
| **Phase 3** | OT Management, Inventory, Operations, Infection Control, Blood Bank, Diet, MRD | 4–6 months | 10–12 developers |
| **Phase 4** | AI Voice-to-Text, AI SOAP, AI Coding, Analytics/Dashboards, Mobile App, Document Mgmt | 4–6 months | 10–12 developers |
| **Phase 5** | AI Risk Prediction, AI Radiology, Advanced Analytics, ABDM Integration, Performance Optimization | 3–4 months | 8–10 developers |
| **TOTAL** | All 27+ modules | **21–30 months** | **Peak 15** |

### 11.2.2 Team Composition

| Role | Count | Responsibility |
|------|:-----:|---------------|
| **Project Manager** | 1 | Overall project management, stakeholder communication |
| **Product Owner (Domain Expert)** | 1 | Healthcare domain expertise, requirement validation |
| **Technical Architect** | 1 | System design, technology decisions, code review |
| **Backend Developers (Java/Spring)** | 5–6 | API development, business logic, integrations |
| **Frontend Developers (React)** | 3–4 | Web application, dashboards |
| **Mobile Developers (Android/Kotlin)** | 2 | Mobile app development |
| **AI/ML Engineer** | 1–2 | Voice, NLP, prediction models |
| **Database Engineer** | 1 | Schema design, query optimization, migrations |
| **QA Engineers** | 2–3 | Testing, automation, performance testing |
| **DevOps Engineer** | 1 | CI/CD, infrastructure, deployment |
| **UI/UX Designer** | 1–2 | Design system, screen design, usability testing |
| **Technical Writer** | 1 | API docs, user guides, admin guides |
| **TOTAL** | **20–25** | — |

### 11.2.3 Effort Estimation (Person-Months)

| Module | Backend | Frontend | Mobile | AI/ML | QA | Total PM |
|--------|:-------:|:--------:|:------:|:-----:|:--:|:--------:|
| Auth & User Mgmt | 3 | 2 | 1 | 0 | 1 | 7 |
| Patient Registration | 2 | 2 | 1 | 0 | 1 | 6 |
| OPD Management | 3 | 2 | 1 | 0 | 1 | 7 |
| IPD / ADT | 4 | 3 | 1 | 0 | 2 | 10 |
| Doctor Workflow | 5 | 4 | 3 | 0 | 2 | 14 |
| AI Voice & SOAP | 2 | 2 | 2 | 4 | 1 | 11 |
| Prescription | 3 | 2 | 1 | 1 | 1 | 8 |
| Nursing | 4 | 3 | 3 | 0 | 2 | 12 |
| Laboratory (LIS) | 6 | 4 | 2 | 0 | 2 | 14 |
| Radiology | 4 | 3 | 1 | 1 | 2 | 11 |
| Pharmacy | 4 | 3 | 1 | 0 | 2 | 10 |
| ICU Management | 3 | 2 | 1 | 0 | 1 | 7 |
| OT Management | 3 | 3 | 1 | 0 | 1 | 8 |
| Bed Management | 2 | 2 | 1 | 0 | 1 | 6 |
| Billing | 5 | 4 | 1 | 0 | 2 | 12 |
| Insurance / TPA | 3 | 2 | 0 | 0 | 1 | 6 |
| Discharge | 3 | 2 | 1 | 1 | 1 | 8 |
| Inventory | 5 | 4 | 1 | 0 | 2 | 12 |
| Operations | 5 | 4 | 2 | 0 | 2 | 13 |
| Blood Bank | 2 | 2 | 0 | 0 | 1 | 5 |
| Infection Control | 2 | 2 | 1 | 0 | 1 | 6 |
| Diet & Nutrition | 2 | 2 | 1 | 0 | 1 | 6 |
| MRD | 2 | 2 | 0 | 0 | 1 | 5 |
| Document Mgmt | 2 | 1 | 1 | 0 | 1 | 5 |
| Notifications | 2 | 1 | 1 | 0 | 1 | 5 |
| Dashboards | 3 | 5 | 2 | 0 | 1 | 11 |
| Audit Trail | 2 | 1 | 0 | 0 | 1 | 4 |
| System Config | 3 | 3 | 0 | 0 | 1 | 7 |
| Emergency / Casualty | 3 | 2 | 2 | 0 | 1 | 8 |
| AI Risk Prediction | 1 | 1 | 0 | 4 | 1 | 7 |
| Integration Layer | 4 | 0 | 0 | 0 | 2 | 6 |
| **TOTAL** | **97** | **73** | **30** | **11** | **37** | **~248 PM** |

---

## 11.3 UI Design System (Light Mode)

### 11.3.1 Color Palette

| Token | Hex | Usage |
|-------|-----|-------|
| `--color-primary` | `#1A73E8` | Primary actions, links, active states |
| `--color-primary-light` | `#E8F0FE` | Primary backgrounds, hover states |
| `--color-primary-dark` | `#1557B0` | Primary text on light backgrounds |
| `--color-background` | `#FFFFFF` | Page background |
| `--color-surface` | `#F8F9FA` | Card backgrounds, panels |
| `--color-surface-hover` | `#F1F3F4` | Hover states on surfaces |
| `--color-border` | `#DADCE0` | Borders, dividers |
| `--color-text-primary` | `#202124` | Headings, primary text |
| `--color-text-secondary` | `#5F6368` | Secondary text, labels |
| `--color-text-tertiary` | `#80868B` | Placeholder text, disabled |
| `--color-success` | `#34A853` | Success states, available beds |
| `--color-warning` | `#FBBC04` | Warnings, abnormal values |
| `--color-error` | `#EA4335` | Errors, critical alerts, occupied beds |
| `--color-info` | `#4285F4` | Informational states |
| `--color-critical-bg` | `#FCE8E6` | Critical alert background |
| `--color-warning-bg` | `#FEF7E0` | Warning background |
| `--color-success-bg` | `#E6F4EA` | Success background |
| `--color-info-bg` | `#E8F0FE` | Info background |

### 11.3.2 Typography

| Element | Font | Size | Weight | Color |
|---------|------|------|--------|-------|
| **Page Title** | Inter | 24px | 600 | `--color-text-primary` |
| **Section Title** | Inter | 18px | 600 | `--color-text-primary` |
| **Card Title** | Inter | 16px | 500 | `--color-text-primary` |
| **Body Text** | Inter | 14px | 400 | `--color-text-primary` |
| **Secondary Text** | Inter | 14px | 400 | `--color-text-secondary` |
| **Label** | Inter | 12px | 500 | `--color-text-secondary` |
| **Small/Caption** | Inter | 12px | 400 | `--color-text-tertiary` |
| **Button** | Inter | 14px | 500 | Varies by button type |
| **Input Field** | Inter | 14px | 400 | `--color-text-primary` |

### 11.3.3 Component Styling

| Component | Style |
|-----------|-------|
| **Cards** | White background, 1px border `#DADCE0`, 8px border-radius, subtle shadow `0 1px 3px rgba(0,0,0,0.08)` |
| **Buttons (Primary)** | `#1A73E8` background, white text, 6px border-radius, 8px 24px padding |
| **Buttons (Secondary)** | White background, `#1A73E8` border and text |
| **Buttons (Danger)** | `#EA4335` background, white text |
| **Inputs** | White background, 1px border `#DADCE0`, 6px border-radius, 40px height |
| **Tables** | Header: `#F8F9FA` background, rows: white with `#F8F9FA` alternating, 1px border |
| **Sidebar** | White background, 260px width, `#F8F9FA` active item, `#1A73E8` active indicator |
| **Top Bar** | White background, 64px height, subtle bottom shadow |
| **Tags/Badges** | Rounded pill shape, colored background with matching text |
| **Modals** | White background, 12px border-radius, overlay `rgba(0,0,0,0.3)` |
| **Toast Notifications** | White background, left colored border (4px), icon + text |
| **Tabs** | Bottom border indicator `#1A73E8`, 2px thickness |

---

## 11.4 Verification & Quality Plan

### 11.4.1 Testing Strategy

| Test Type | Coverage | Tools |
|-----------|---------|-------|
| **Unit Tests** | > 80% code coverage | JUnit 5, Mockito (Backend), Jest (Frontend) |
| **Integration Tests** | All API endpoints, DB interactions | Spring Boot Test, Testcontainers |
| **E2E Tests** | Critical user journeys (Registration → Discharge) | Cypress / Playwright |
| **Performance Tests** | Load, stress, endurance | JMeter, Gatling |
| **Security Tests** | OWASP Top 10, pen testing | OWASP ZAP, Burp Suite |
| **Accessibility Tests** | WCAG 2.1 AA compliance | axe-core, Lighthouse |
| **Mobile Tests** | Device compatibility, offline sync | Espresso (Android) |
| **Clinical Validation** | Workflow correctness with domain experts | Manual + automated |
| **Usability Tests** | User acceptance with actual hospital staff | Moderated sessions |

### 11.4.2 Acceptance Criteria Framework

Every user story must have:
1. **Functional Acceptance:** Feature works as specified
2. **Performance Acceptance:** Response time within SLA
3. **Security Acceptance:** No unauthorized access possible
4. **Audit Acceptance:** Action logged in audit trail
5. **Error Handling:** Graceful error with user-friendly message
6. **Offline Handling (Mobile):** Behavior defined for offline scenario

---

[← Back to Table of Contents](./00_Table_of_Contents.md)
