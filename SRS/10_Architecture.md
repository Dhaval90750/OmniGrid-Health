# 10. System Architecture

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 10.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           CLIENT LAYER                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │  Web App      │  │  Mobile App  │  │  Tablet App  │  │  Kiosk App   │   │
│  │  (React/Next) │  │  (Android)   │  │  (Bedside)   │  │  (Check-in)  │   │
│  │               │  │  Kotlin      │  │              │  │              │   │
│  │  Used by:     │  │              │  │  Used by:    │  │  Used by:    │   │
│  │  • Admin      │  │  Used by:    │  │  • Nurses    │  │  • Patients  │   │
│  │  • Reception  │  │  • Doctors   │  │  • ICU       │  │  • Visitors  │   │
│  │  • Billing    │  │  • Nurses    │  │              │  │              │   │
│  │  • Management │  │  • Lab Tech  │  │              │  │              │   │
│  │  • Doctors    │  │  • Pharmacy  │  │              │  │              │   │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
│         │                 │                 │                  │            │
└─────────┼─────────────────┼─────────────────┼──────────────────┼────────────┘
          │                 │                 │                  │
          └─────────────────┴─────────┬───────┴──────────────────┘
                                      │
                              ┌───────▼───────┐
                              │  API GATEWAY   │
                              │  (Kong/Nginx)  │
                              │  • Rate Limit  │
                              │  • Auth Check  │
                              │  • Load Balance│
                              │  • SSL Term.   │
                              └───────┬───────┘
                                      │
┌─────────────────────────────────────┼───────────────────────────────────────┐
│                           SERVICE LAYER                                     │
├─────────────────────────────────────┼───────────────────────────────────────┤
│                                     │                                       │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                    SPRING BOOT APPLICATION                            │  │
│  │                    (Modular Monolith / Microservices-Ready)           │  │
│  │                                                                       │  │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │  │
│  │  │ Patient     │ │ Clinical    │ │ Lab         │ │ Radiology   │    │  │
│  │  │ Service     │ │ Service     │ │ Service     │ │ Service     │    │  │
│  │  │ • Register  │ │ • Notes     │ │ • Orders    │ │ • Orders    │    │  │
│  │  │ • Admission │ │ • Diagnosis │ │ • Samples   │ │ • PACS      │    │  │
│  │  │ • ADT       │ │ • Prescribe │ │ • Results   │ │ • Reports   │    │  │
│  │  │ • QR        │ │ • SOAP      │ │ • QC        │ │ • Voice     │    │  │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘    │  │
│  │                                                                       │  │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │  │
│  │  │ Pharmacy    │ │ Billing     │ │ Inventory   │ │ Operations  │    │  │
│  │  │ Service     │ │ Service     │ │ Service     │ │ Service     │    │  │
│  │  │ • Dispense  │ │ • Invoice   │ │ • Stock     │ │ • Housekeep │    │  │
│  │  │ • Stock     │ │ • Insurance │ │ • Procure   │ │ • Mainten.  │    │  │
│  │  │ • Narcotics │ │ • Payment   │ │ • Assets    │ │ • Transport │    │  │
│  │  │ • Formulary │ │ • Claims    │ │ • Vendors   │ │ • Dietary   │    │  │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘    │  │
│  │                                                                       │  │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │  │
│  │  │ Nursing     │ │ OT          │ │ Auth        │ │ Notification│    │  │
│  │  │ Service     │ │ Service     │ │ Service     │ │ Service     │    │  │
│  │  │ • Vitals    │ │ • Scheduling│ │ • Login     │ │ • Push      │    │  │
│  │  │ • MAR       │ │ • WHO Chk   │ │ • RBAC      │ │ • SMS       │    │  │
│  │  │ • Assess.   │ │ • Implants  │ │ • JWT       │ │ • WhatsApp  │    │  │
│  │  │ • Handover  │ │ • OT Notes  │ │ • MFA       │ │ • Email     │    │  │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘    │  │
│  │                                                                       │  │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                    │  │
│  │  │ Analytics   │ │ Audit       │ │ Document    │                    │  │
│  │  │ Service     │ │ Service     │ │ Service     │                    │  │
│  │  │ • Dashboards│ │ • Logging   │ │ • Upload    │                    │  │
│  │  │ • Reports   │ │ • Compliance│ │ • OCR       │                    │  │
│  │  │ • KPIs      │ │ • Trail     │ │ • Template  │                    │  │
│  │  └─────────────┘ └─────────────┘ └─────────────┘                    │  │
│  │                                                                       │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
          │              │              │              │
┌─────────┼──────────────┼──────────────┼──────────────┼──────────────────────┐
│         │    DATA & INFRASTRUCTURE LAYER              │                      │
├─────────┼──────────────┼──────────────┼──────────────┼──────────────────────┤
│         │              │              │              │                      │
│  ┌──────▼──────┐ ┌─────▼──────┐ ┌────▼──────┐ ┌────▼──────┐              │
│  │ PostgreSQL  │ │ Redis      │ │ Elastic   │ │ Kafka /   │              │
│  │ (Primary DB)│ │ (Cache +   │ │ Search    │ │ RabbitMQ  │              │
│  │             │ │  Sessions) │ │           │ │ (Events)  │              │
│  │ • Patient   │ │ • Session  │ │ • Patient │ │ • Lab     │              │
│  │ • Clinical  │ │ • Cache    │ │   Search  │ │   Results │              │
│  │ • Lab       │ │ • Real-time│ │ • Drug    │ │ • Alerts  │              │
│  │ • Billing   │ │   Pub/Sub  │ │   Search  │ │ • Audit   │              │
│  │ • Inventory │ │ • Rate     │ │ • Full    │ │   Events  │              │
│  │ • Audit     │ │   Limiting │ │   Text    │ │ • Notif.  │              │
│  └─────────────┘ └────────────┘ └───────────┘ └───────────┘              │
│                                                                          │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐                     │
│  │ MinIO / S3   │ │ PACS Storage │ │ AI Engine    │                     │
│  │ (Object      │ │ (DICOM)      │ │              │                     │
│  │  Storage)    │ │              │ │ • Whisper    │                     │
│  │ • Documents  │ │ • X-Ray      │ │   (STT)     │                     │
│  │ • Reports    │ │ • CT/MRI     │ │ • Medical   │                     │
│  │ • Images     │ │ • USG        │ │   NLP       │                     │
│  │ • Audio      │ │ • Mammo      │ │ • LLM       │                     │
│  │ • Backups    │ │              │ │   (Local)   │                     │
│  └──────────────┘ └──────────────┘ └──────────────┘                     │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 10.2 Technology Stack

### 10.2.1 Backend

| Component | Technology | Version | Justification |
|-----------|-----------|---------|---------------|
| **Language** | Java | 21 (LTS) | Enterprise-grade, strong typing, vast healthcare ecosystem |
| **Framework** | Spring Boot | 3.3+ | Industry standard for enterprise Java, excellent ecosystem |
| **ORM** | Hibernate / JPA | 6.4+ | Robust ORM with PostgreSQL optimization |
| **API** | Spring WebFlux + Spring MVC | — | REST APIs with reactive support for real-time features |
| **Security** | Spring Security | 6.x | OAuth2, JWT, RBAC, method-level security |
| **Messaging** | Apache Kafka | 3.7+ | Event streaming for high-throughput async operations |
| **Task Scheduling** | Spring Batch + Quartz | — | Scheduled reports, batch processing, data archival |
| **WebSocket** | Spring WebSocket + STOMP | — | Real-time dashboards, notifications, bed status |
| **Search** | Elasticsearch | 8.x | Full-text search for patients, drugs, diagnoses |
| **Cache** | Redis | 7.x | Session management, caching, real-time pub/sub |
| **PDF Generation** | Apache PDFBox / iText | — | Discharge summaries, reports, prescriptions |
| **QR Generation** | ZXing | — | QR code generation for patient wristbands |
| **DICOM** | dcm4che | 5.x | DICOM protocol handling, PACS integration |
| **HL7** | HAPI FHIR | 7.x | HL7 FHIR R4 + HL7 v2.x support |

### 10.2.2 Frontend (Web)

| Component | Technology | Version | Justification |
|-----------|-----------|---------|---------------|
| **Framework** | React | 18+ | Component-based, large ecosystem, excellent developer tools |
| **Meta-Framework** | Next.js | 14+ | SSR, routing, API routes, optimized builds |
| **State Management** | Zustand / Redux Toolkit | — | Predictable state management |
| **UI Components** | Ant Design / Custom | 5.x | Enterprise-grade components, form handling, tables |
| **Charts** | Apache ECharts / Recharts | — | Clinical dashboards, vital trends, analytics |
| **Real-Time** | Socket.IO Client | — | WebSocket connection for live updates |
| **DICOM Viewer** | Cornerstone.js / OHIF | — | In-browser medical image viewing |
| **Forms** | React Hook Form + Zod | — | Performant forms with validation |
| **Date/Time** | Day.js | — | Lightweight date handling |
| **PDF Viewer** | react-pdf | — | In-browser report viewing |
| **Theme** | CSS Variables + Light Mode | — | System-wide light mode theme |

### 10.2.3 Mobile (Android)

| Component | Technology | Version | Justification |
|-----------|-----------|---------|---------------|
| **Language** | Kotlin | 2.0+ | Modern, concise, official Android language |
| **UI** | Jetpack Compose | — | Declarative UI, modern Android UI toolkit |
| **Architecture** | MVVM + Clean Architecture | — | Testable, maintainable |
| **Networking** | Retrofit + OkHttp | — | Type-safe HTTP client |
| **Local DB** | Room | — | SQLite abstraction for offline data |
| **DI** | Hilt | — | Dependency injection |
| **Camera/QR** | CameraX + ML Kit | — | QR scanning |
| **Voice** | Speech-to-Text (on-device) | — | Offline voice input |
| **Push** | Firebase Cloud Messaging | — | Push notifications |
| **Offline Sync** | WorkManager + Custom Sync | — | Background data sync |

### 10.2.4 AI/ML Stack

| Component | Technology | Justification |
|-----------|-----------|---------------|
| **Speech-to-Text** | Whisper Large V3 (self-hosted) | Best accuracy for medical speech, multi-language |
| **NLP Pipeline** | spaCy + Custom Medical NER | Entity extraction from clinical text |
| **Medical LLM** | Llama-3 / Mistral (fine-tuned, self-hosted) | No PHI sent externally, customizable |
| **Prediction Models** | XGBoost / PyTorch | Sepsis, readmission, ICU transfer prediction |
| **Model Serving** | TorchServe / TensorRT | GPU-optimized inference |
| **ML Ops** | MLflow | Model versioning, experiment tracking |
| **Training Data** | De-identified hospital data | Privacy-compliant training |

### 10.2.5 Database

| Database | Usage | Justification |
|----------|-------|---------------|
| **PostgreSQL 16** | Primary OLTP database | ACID compliance, JSON support, robust, enterprise-proven in healthcare |
| **Redis 7** | Caching, sessions, real-time | Sub-millisecond latency, pub/sub for WebSocket |
| **Elasticsearch 8** | Search engine | Full-text search, fuzzy matching, auto-suggest |
| **TimescaleDB** (PostgreSQL extension) | Time-series data | Vitals trends, IoT sensor data, performance metrics |
| **MinIO** | Object storage | S3-compatible, on-premises, documents/images/audio |

### 10.2.6 DevOps & Infrastructure

| Component | Technology |
|-----------|-----------|
| **Containerization** | Docker |
| **Orchestration** | Kubernetes (K8s) or Docker Swarm |
| **CI/CD** | Jenkins / GitLab CI / GitHub Actions |
| **Monitoring** | Prometheus + Grafana |
| **Logging** | ELK Stack (Elasticsearch, Logstash, Kibana) |
| **APM** | Elastic APM / Jaeger (tracing) |
| **Secrets** | HashiCorp Vault |
| **IaC** | Terraform / Ansible |
| **Version Control** | Git (GitLab / GitHub) |
| **Code Quality** | SonarQube |

---

## 10.3 API Design

### 10.3.1 API Standards

| Standard | Specification |
|----------|--------------|
| **Protocol** | REST (primary), GraphQL (for complex queries), gRPC (for internal service-to-service) |
| **Format** | JSON (application/json) |
| **Versioning** | URI-based: `/api/v1/`, `/api/v2/` |
| **Authentication** | Bearer token (JWT) |
| **Pagination** | Cursor-based for large datasets, offset-based for simple lists |
| **Filtering** | Query parameters: `?status=active&department=cardiology` |
| **Sorting** | `?sort=created_at&order=desc` |
| **Error Format** | RFC 7807 Problem Details |
| **Rate Limiting** | `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset` headers |
| **HATEOAS** | Links in responses for discoverability |
| **Documentation** | OpenAPI 3.1 (Swagger) |

### 10.3.2 Core API Modules

| Module | Base Path | Key Endpoints |
|--------|-----------|--------------|
| **Auth** | `/api/v1/auth` | `POST /login`, `POST /refresh`, `POST /logout`, `POST /mfa/verify` |
| **Patients** | `/api/v1/patients` | `POST /`, `GET /{uhid}`, `GET /search`, `PUT /{uhid}`, `GET /{uhid}/qr` |
| **Admissions** | `/api/v1/admissions` | `POST /`, `GET /{id}`, `PUT /{id}/transfer`, `PUT /{id}/discharge` |
| **Clinical Notes** | `/api/v1/clinical-notes` | `POST /`, `GET /{patientId}`, `POST /{id}/sign`, `POST /voice` |
| **Prescriptions** | `/api/v1/prescriptions` | `POST /`, `GET /{patientId}`, `POST /interaction-check` |
| **Lab Orders** | `/api/v1/lab/orders` | `POST /`, `GET /{id}`, `PUT /{id}/results`, `PUT /{id}/authorize` |
| **Radiology** | `/api/v1/radiology` | `POST /orders`, `GET /{id}`, `POST /{id}/report` |
| **Pharmacy** | `/api/v1/pharmacy` | `POST /dispense`, `GET /stock`, `POST /return` |
| **Billing** | `/api/v1/billing` | `POST /invoices`, `POST /payments`, `GET /{patientId}/statement` |
| **Beds** | `/api/v1/beds` | `GET /dashboard`, `POST /allocate`, `PUT /{id}/transfer` |
| **Inventory** | `/api/v1/inventory` | `GET /stock`, `POST /indent`, `POST /grn`, `POST /issue` |
| **Operations** | `/api/v1/operations` | `POST /work-orders`, `GET /housekeeping/tasks`, `GET /dashboard` |
| **Notifications** | `/api/v1/notifications` | `GET /`, `PUT /{id}/read`, `GET /unread-count` |
| **Analytics** | `/api/v1/analytics` | `GET /dashboard/{type}`, `POST /reports/generate` |
| **AI** | `/api/v1/ai` | `POST /voice-to-notes`, `POST /soap-generate`, `POST /icd-suggest` |

---

## 10.4 Data Flow Architecture

### 10.4.1 Real-Time Event Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ Application │────▶│   Kafka     │────▶│ Consumer    │
│ (Producer)  │     │   Topics    │     │ Services    │
└─────────────┘     └─────────────┘     └─────────────┘
                           │
                    ┌──────┴──────┐
              ┌─────▼────┐  ┌────▼─────┐
              │Audit Log │  │Dashboard │
              │Consumer  │  │Consumer  │
              │(Write to │  │(Push via │
              │ Audit DB)│  │WebSocket)│
              └──────────┘  └──────────┘
```

**Key Event Topics:**

| Topic | Events |
|-------|--------|
| `patient.events` | registered, updated, admitted, discharged, transferred |
| `clinical.events` | note_created, note_signed, diagnosis_added, prescription_created |
| `lab.events` | order_created, sample_collected, result_ready, critical_value |
| `radiology.events` | order_created, study_completed, report_ready, critical_finding |
| `pharmacy.events` | dispensed, returned, stock_low |
| `billing.events` | invoice_created, payment_received, claim_submitted |
| `bed.events` | allocated, released, transfer, maintenance |
| `alert.events` | critical_lab, deterioration, allergy, code_blue |
| `audit.events` | all_actions (immutable log) |
| `inventory.events` | stock_updated, indent_created, po_created, grn_received |
| `operations.events` | work_order_created, task_completed, maintenance_due |

---

## 10.5 Database Schema Overview

### 10.5.1 Schema Organization

| Schema | Tables (Estimated) | Description |
|--------|-------------------|-------------|
| `patient` | 15–20 | Patient registration, demographics, contacts, insurance |
| `clinical` | 25–30 | Clinical notes, diagnoses, prescriptions, procedures, consents |
| `adt` | 10–15 | Admissions, transfers, discharges, bed management |
| `lab` | 20–25 | Lab orders, samples, results, QC, reference ranges |
| `radiology` | 10–15 | Radiology orders, reports, PACS references |
| `pharmacy` | 15–20 | Drug master, dispensing, stock, narcotic register |
| `nursing` | 10–15 | Vitals, MAR, assessments, shift notes |
| `ot` | 10–12 | Surgery scheduling, OT notes, implant tracking |
| `billing` | 15–20 | Invoices, payments, insurance claims, tariffs |
| `inventory` | 20–25 | Item master, PO, GRN, stock, vendors, assets |
| `operations` | 15–20 | Work orders, housekeeping, transport, dietary, waste |
| `notification` | 5–8 | Alerts, notifications, escalations, templates |
| `auth` | 8–10 | Users, roles, permissions, sessions, audit |
| `master` | 15–20 | ICD codes, drug database, department, ward, bed config |
| `analytics` | 5–10 | Materialized views, aggregated reports |
| **TOTAL** | **200–260** | — |

### 10.5.2 Key Entity Relationships

```
Patient (1) ──── (N) Visits
Patient (1) ──── (N) Admissions
Admission (1) ── (N) Bed Allocations
Admission (1) ── (N) Clinical Notes
Admission (1) ── (N) Lab Orders
Lab Order (1) ── (N) Lab Results
Admission (1) ── (N) Prescriptions
Prescription (1) (N) Prescription Lines
Admission (1) ── (N) Radiology Orders
Admission (1) ── (1) Discharge Summary
Admission (1) ── (N) Billing Invoices
Patient (1) ──── (1) EMR (lifelong record)
```

---

## 10.6 Integration Architecture

### 10.6.1 External Integrations

| System | Protocol | Purpose |
|--------|----------|---------|
| **Lab Analyzers** | ASTM / HL7 v2.x | Bidirectional result transfer |
| **Bedside Monitors** | HL7 v2.x | Real-time vitals feed |
| **PACS** | DICOM | Image storage and retrieval |
| **Payment Gateway** | REST API | Online payments |
| **SMS Gateway** | REST API | Patient/staff notifications |
| **WhatsApp Business** | WhatsApp Cloud API | Patient communication |
| **Email** | SMTP | Notifications, reports |
| **ABDM** | FHIR R4 + ABDM APIs | Health ID, Health Records |
| **Government Portals** | REST/SOAP | Birth/death registration, disease reporting |
| **Insurance/TPA Portals** | REST API / EDI | Pre-auth, claims |
| **Drug Database** | REST API | Drug information, interactions |
| **ICD Database** | Local DB | Diagnosis coding |
| **Biometric Devices** | SDK | Staff authentication |
| **Barcode/QR Printers** | Direct Protocol | Wristband and label printing |

---

[→ Next: UI Screens & Estimated Effort](./11_UI_Screens_Effort.md)
