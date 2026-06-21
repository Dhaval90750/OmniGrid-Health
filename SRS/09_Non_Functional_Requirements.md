# 9. Non-Functional Requirements

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 9.1 Performance Requirements

### 9.1.1 Response Time

| Operation Type | Target (P95) | Maximum (P99) |
|---------------|-------------|---------------|
| Page load (web) | < 2 seconds | < 4 seconds |
| Page load (mobile) | < 3 seconds | < 5 seconds |
| QR scan to patient display | < 1.5 seconds | < 3 seconds |
| Search (patient, drug, test) | < 500 ms | < 1.5 seconds |
| Form submission (save) | < 1 second | < 3 seconds |
| Report generation (simple) | < 5 seconds | < 10 seconds |
| Report generation (complex/analytics) | < 15 seconds | < 30 seconds |
| Dashboard load | < 3 seconds | < 5 seconds |
| Voice-to-text processing | < 5 seconds | < 10 seconds |
| AI SOAP generation | < 8 seconds | < 15 seconds |
| Lab result notification | < 2 seconds (from authorization) | < 5 seconds |
| Real-time bed dashboard update | < 1 second | < 3 seconds |
| PDF generation (discharge summary) | < 5 seconds | < 10 seconds |
| DICOM image load (initial) | < 5 seconds | < 10 seconds |
| Billing invoice generation | < 3 seconds | < 5 seconds |

### 9.1.2 Throughput

| Metric | Target |
|--------|--------|
| Concurrent users | 2,000 |
| API requests per second | 5,000 |
| Database transactions per second | 10,000 |
| Concurrent report generations | 50 |
| WebSocket connections | 5,000 |
| File uploads per minute | 100 |

### 9.1.3 Data Volume

| Data Type | Annual Growth | 5-Year Projection |
|-----------|--------------|-------------------|
| Patient records | 200,000 new/year | 1,000,000 records |
| Lab results | 5,000,000 results/year | 25,000,000 results |
| Clinical notes | 2,000,000 notes/year | 10,000,000 notes |
| Radiology images (DICOM) | 10 TB/year | 50 TB |
| Documents (scanned) | 2 TB/year | 10 TB |
| Audit logs | 500 GB/year | 2.5 TB |
| Total storage | ~15 TB/year | ~75 TB |

---

## 9.2 Availability & Reliability

### 9.2.1 Availability Targets

| System Component | Target Uptime | Max Planned Downtime | Max Unplanned Downtime |
|-----------------|-------------|---------------------|----------------------|
| **Core Application (Web)** | 99.95% | 4 hours/month (off-peak) | 22 min/month |
| **Core Application (Mobile)** | 99.95% | Same as web | 22 min/month |
| **Database** | 99.99% | 2 hours/month | 4.3 min/month |
| **PACS / Imaging** | 99.9% | 4 hours/month | 43 min/month |
| **AI Services** | 99.5% | 8 hours/month (with manual fallback) | 3.6 hours/month |
| **Notification Engine** | 99.95% | 2 hours/month | 22 min/month |
| **Reporting / Analytics** | 99.5% | 8 hours/month | 3.6 hours/month |

### 9.2.2 Recovery Objectives

| Metric | Target |
|--------|--------|
| **RPO (Recovery Point Objective)** | < 5 minutes (maximum data loss in disaster) |
| **RTO (Recovery Time Objective)** | < 1 hour (maximum time to restore service) |
| **Backup Frequency** | Continuous replication + hourly snapshots + daily full backup |
| **Backup Retention** | Daily: 30 days, Weekly: 12 months, Monthly: 7 years |
| **Disaster Recovery** | Active-passive DR site with auto-failover |
| **DR Testing** | Quarterly DR drill with documented results |

### 9.2.3 Fault Tolerance

| Strategy | Description |
|----------|-------------|
| **Database Replication** | Synchronous replication to standby (PostgreSQL streaming replication) |
| **Application Clustering** | Minimum 3 application server instances with load balancing |
| **Cache Layer** | Redis Sentinel / Redis Cluster (minimum 3 nodes) |
| **Message Queue** | RabbitMQ / Kafka cluster (minimum 3 nodes) |
| **Storage** | RAID-10 for database, S3-compatible object storage with replication |
| **Network** | Redundant network paths, dual ISP |
| **Power** | UPS + DG set + solar (for data center) |
| **No Single Point of Failure** | Every component has redundancy |

---

## 9.3 Security Requirements

### 9.3.1 Data Encryption

| Data State | Encryption |
|-----------|-----------|
| **Data at Rest** | AES-256 encryption for database, file storage, backups |
| **Data in Transit** | TLS 1.3 for all HTTP traffic, TLS for database connections |
| **Sensitive Fields** | Column-level encryption for Aadhaar, biometrics, HIV status |
| **Backup Encryption** | All backups encrypted with separate key management |
| **Key Management** | HSM (Hardware Security Module) or KMS for key storage |

### 9.3.2 Application Security

| Measure | Implementation |
|---------|---------------|
| **OWASP Top 10** | All OWASP vulnerabilities addressed (Injection, XSS, CSRF, etc.) |
| **Input Validation** | Server-side validation for all inputs, parameterized queries |
| **Session Security** | HTTP-only cookies, secure flag, SameSite, anti-CSRF tokens |
| **API Security** | JWT with short expiry (15 min access, 7 day refresh), rate limiting |
| **CORS** | Whitelist-based CORS policy |
| **CSP** | Content Security Policy headers |
| **File Upload** | Virus scanning, file type validation, size limits |
| **SQL Injection** | Prepared statements, ORM-based queries |
| **XSS Prevention** | Output encoding, CSP, input sanitization |
| **Rate Limiting** | 100 requests/min per user (configurable) |

### 9.3.3 Network Security

| Measure | Implementation |
|---------|---------------|
| **WAF** | Web Application Firewall with OWASP CRS rules |
| **DDoS Protection** | Network-level and application-level DDoS mitigation |
| **VPN** | Admin access only via VPN |
| **Network Segmentation** | DMZ, Application zone, Database zone, Management zone |
| **IDS/IPS** | Intrusion Detection and Prevention System |
| **Vulnerability Scanning** | Monthly automated scans + quarterly penetration testing |
| **SIEM** | Security Information and Event Management for log correlation |

### 9.3.4 Compliance Security

| Requirement | Implementation |
|------------|---------------|
| **Audit Logging** | Immutable audit logs for all data access and modifications |
| **Data Masking** | Automatic masking of sensitive data in non-production environments |
| **Data Anonymization** | De-identification for research/analytics datasets |
| **Consent Management** | Digital consent recording with timestamp and scope |
| **Data Retention** | Automated enforcement of retention policies |
| **Right to Access** | Patient can request their complete medical record |
| **Breach Notification** | Automated breach detection and notification workflow |

---

## 9.4 Scalability Requirements

### 9.4.1 Horizontal Scaling

| Component | Scaling Strategy |
|-----------|-----------------|
| **Application Servers** | Auto-scaling based on CPU/memory/request count |
| **Database** | Read replicas for reporting workloads, connection pooling |
| **Cache** | Redis Cluster with hash-based sharding |
| **File Storage** | S3-compatible object storage (infinitely scalable) |
| **Search** | Elasticsearch cluster with horizontal scaling |
| **Message Queue** | Kafka/RabbitMQ cluster partitioning |

### 9.4.2 Vertical Scaling Limits

| Component | Minimum | Recommended | Maximum |
|-----------|---------|-------------|---------|
| **App Server** | 4 vCPU, 8 GB RAM | 8 vCPU, 16 GB RAM | 16 vCPU, 32 GB RAM |
| **Database Server** | 8 vCPU, 32 GB RAM | 16 vCPU, 64 GB RAM | 32 vCPU, 128 GB RAM |
| **Cache Server** | 4 vCPU, 16 GB RAM | 8 vCPU, 32 GB RAM | 16 vCPU, 64 GB RAM |
| **Search Server** | 4 vCPU, 16 GB RAM | 8 vCPU, 32 GB RAM | 16 vCPU, 64 GB RAM |
| **AI Server** | 8 vCPU, 32 GB RAM, 1 GPU | 16 vCPU, 64 GB RAM, 2 GPU | 32 vCPU, 128 GB RAM, 4 GPU |

---

## 9.5 Usability Requirements

### 9.5.1 Design Standards

| Requirement | Specification |
|------------|--------------|
| **Theme** | **Light mode system-wide** — Clean, clinical, professional appearance |
| **Color Palette** | Primary: Professional blue (#1A73E8). Neutral backgrounds: White (#FFFFFF), Light gray (#F8F9FA). Status colors: Standard (Red/Yellow/Green for alerts) |
| **Typography** | Inter or Roboto font family. Base size: 14px. Minimum: 12px |
| **Contrast** | WCAG 2.1 AA minimum (4.5:1 for normal text, 3:1 for large text) |
| **Responsive** | Web: 1024px–2560px. Tablet: 768px–1024px. Mobile: 320px–768px |
| **Language** | English (primary), Hindi (secondary), configurable regional languages |
| **RTL Support** | Support for Right-to-Left languages (future) |
| **Screen Reader** | ARIA labels for all interactive elements |
| **Keyboard Navigation** | Full keyboard navigation support |

### 9.5.2 Clinical UX Principles

| Principle | Implementation |
|-----------|---------------|
| **Minimal Clicks** | Maximum 3 clicks to reach any patient data from dashboard |
| **QR-First** | QR scan opens patient context in < 2 seconds |
| **Context Preservation** | Patient context maintained across module navigation |
| **Smart Defaults** | Pre-fill forms with most common values, remember user preferences |
| **Typeahead Search** | All search fields have autocomplete with fuzzy matching |
| **Keyboard Shortcuts** | Power-user keyboard shortcuts for common actions |
| **Error Prevention** | Confirmations for destructive actions, undo support for recent changes |
| **Offline Indicators** | Clear indication when mobile app is offline, with sync status |
| **Loading States** | Skeleton screens, progress indicators for long operations |
| **Zero State** | Helpful empty states with guidance ("No patients assigned. Start by scanning a QR code.") |

### 9.5.3 Mobile-Specific UX

| Requirement | Specification |
|------------|--------------|
| **One-Hand Operation** | Primary actions reachable with thumb (bottom navigation) |
| **QR Camera** | Fast camera launch for QR scanning (< 1 second) |
| **Voice Input** | Large, accessible voice recording button |
| **Notifications** | Push notifications with quick-action buttons |
| **Offline Data** | Patient data for assigned patients cached locally |
| **Biometric Unlock** | Fingerprint / Face ID for quick app access |
| **Haptic Feedback** | Vibration for successful scan, alerts |

---

## 9.6 Compatibility Requirements

### 9.6.1 Web Browsers

| Browser | Minimum Version |
|---------|----------------|
| Google Chrome | Last 2 major versions |
| Mozilla Firefox | Last 2 major versions |
| Microsoft Edge | Last 2 major versions |
| Safari | Last 2 major versions |
| Opera | Last 2 major versions |

### 9.6.2 Mobile Platforms

| Platform | Minimum Version |
|---------|----------------|
| Android | Android 10 (API 29) and above |
| iOS | iOS 15 and above (future consideration) |

### 9.6.3 Integration Standards

| Standard | Purpose |
|----------|---------|
| **HL7 FHIR R4** | Health data interoperability |
| **HL7 v2.x** | Legacy system integration |
| **DICOM** | Medical imaging |
| **ASTM E1381** | Lab analyzer integration |
| **IHE Profiles** | Cross-enterprise document sharing (XDS), Patient Demographics Query (PDQ) |
| **ABDM** | India Health Stack integration (ABHA, Health Records) |

### 9.6.4 Hardware Integration

| Device Type | Integration Method |
|------------|-------------------|
| Lab Analyzers | ASTM / HL7 via serial / TCP/IP |
| Bedside Monitors | HL7 via TCP/IP |
| Barcode Scanners | USB HID / Bluetooth |
| QR Printers (Wristband) | Direct printing protocol (Zebra, TSC) |
| Document Scanners | TWAIN / WIA |
| Webcams | WebRTC |
| Biometric Readers | SDK integration |
| POS Terminals | ISO 8583 / vendor SDK |
| PACS | DICOM protocol |
| Pharmacy Dispensing | Vendor-specific API |

---

## 9.7 Localization & Internationalization

### 9.7.1 Language Support

| Language | Status | Coverage |
|---------|--------|---------|
| English | Phase 1 | Full system |
| Hindi | Phase 1 | UI labels, patient communication, reports |
| Marathi | Phase 2 | UI labels, patient communication |
| Tamil | Phase 2 | UI labels, patient communication |
| Telugu | Phase 2 | UI labels, patient communication |
| Bengali | Phase 2 | UI labels, patient communication |
| Kannada | Phase 3 | UI labels, patient communication |
| Gujarati | Phase 3 | UI labels, patient communication |

### 9.7.2 Regional Configuration

| Configuration | Options |
|--------------|---------|
| **Date Format** | DD-MM-YYYY (India default), MM/DD/YYYY, YYYY-MM-DD |
| **Time Format** | 12-hour (default), 24-hour (clinical areas) |
| **Currency** | INR (₹) default, USD, AED, others configurable |
| **Number Format** | Indian numbering (12,34,567) vs Western (1,234,567) |
| **Weight/Height** | kg/cm (default), lbs/inches |
| **Temperature** | °F (default for India clinical), °C |

---

## 9.8 Deployment & Infrastructure

### 9.8.1 Deployment Options

| Option | Description | Best For |
|--------|-------------|---------|
| **On-Premises** | Deployed on hospital's own servers | Large hospitals with IT infrastructure, data sovereignty requirements |
| **Private Cloud** | Dedicated cloud infrastructure (AWS/Azure/GCP) | Medium-large hospitals wanting cloud benefits with isolation |
| **Hybrid** | Critical data on-premises, analytics/AI on cloud | Hospitals with regulatory constraints but wanting AI capabilities |
| **SaaS (Multi-Tenant)** | Shared cloud infrastructure, data isolated per tenant | Small-medium hospitals, quick deployment |

### 9.8.2 Recommended Infrastructure (On-Premises, 1000-bed Hospital)

| Component | Specification | Quantity |
|-----------|-------------|---------|
| **Application Servers** | 16 vCPU, 32 GB RAM, 500 GB SSD | 3 (load balanced) |
| **Database Server (Primary)** | 32 vCPU, 128 GB RAM, 2 TB NVMe SSD (RAID-10) | 1 |
| **Database Server (Standby)** | 32 vCPU, 128 GB RAM, 2 TB NVMe SSD | 1 |
| **Cache Server (Redis)** | 8 vCPU, 64 GB RAM | 3 (Sentinel cluster) |
| **Search Server (Elasticsearch)** | 16 vCPU, 64 GB RAM, 1 TB SSD | 3 (cluster) |
| **AI/ML Server** | 16 vCPU, 64 GB RAM, NVIDIA A100/H100 GPU | 1–2 |
| **PACS Storage** | 128 TB expandable NAS/SAN | 1 |
| **Object Storage** | 50 TB expandable (MinIO/Ceph) | 1 cluster |
| **Load Balancer** | Hardware LB or HAProxy | 2 (HA pair) |
| **Firewall/WAF** | Enterprise firewall | 2 (HA pair) |
| **Backup Server** | Dedicated backup with tape/disk | 1 |
| **Network** | 10 Gbps backbone, 1 Gbps to endpoints | — |
| **UPS** | 30 min runtime for all servers | 1 |

---

## 9.9 Maintainability

| Requirement | Specification |
|------------|--------------|
| **Code Quality** | SonarQube with quality gates (0 critical, 0 blocker bugs) |
| **Test Coverage** | > 80% unit test coverage, > 60% integration test coverage |
| **CI/CD** | Automated build, test, deploy pipeline |
| **Blue-Green Deployment** | Zero-downtime deployments |
| **Feature Flags** | Gradual feature rollout with kill switch |
| **Monitoring** | Prometheus + Grafana for infrastructure, APM for application |
| **Logging** | Centralized logging (ELK Stack) with correlation IDs |
| **Alerting** | PagerDuty/Opsgenie for production alerts |
| **Documentation** | API documentation (OpenAPI/Swagger), developer guide, ops runbook |
| **Database Migrations** | Versioned migrations (Flyway/Liquibase), backward-compatible |

---

[→ Next: System Architecture](./10_Architecture.md)
