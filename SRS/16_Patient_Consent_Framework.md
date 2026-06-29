# 16. Patient Consent Framework

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 16.1 Overview
The Patient Consent Framework handles the digital capture, storage, and retrieval of informed consents required for hospital admission, procedures, blood transfusions, and clinical research.

## 16.2 Consent Types
1. **General Consent for Treatment:** Signed at the time of first registration or admission. Covers routine care, vitals, and basic diagnostics.
2. **Procedural / Surgical Consent:** Required for invasive procedures (e.g., surgery, endoscopy). Details the specific risks, alternatives, and surgeons involved.
3. **Blood Transfusion Consent:** Specific consent for receiving blood products.
4. **Research Consent:** For participation in clinical trials.
5. **Telemedicine Consent:** Covers the limitations of remote assessment and data privacy.

## 16.3 Consent Capture Modes
- **Patient Portal:** Patients can review and e-sign standard consents from their mobile app/portal before arriving.
- **Tablet/Kiosk:** Bedside signing using a stylus or finger on a hospital-provided tablet.
- **Proxy Consent:** If a patient is a minor or incapacitated, the system allows capturing the guardian/proxy details and signature.
- **Video Consent:** For high-risk procedures or remote patients, the system supports attaching a video recording to the consent record.

## 16.4 Data Model & Storage
- Consent documents are generated as PDFs capturing the patient's UHID, date, time, IP address (if external), and a cryptographic hash of the signature.
- Stored immutably with versioning.
- Revocation: Patients can revoke consent at any time, which logs a timestamped revocation event.

---
[← Back to Table of Contents](./00_Table_of_Contents.md)
