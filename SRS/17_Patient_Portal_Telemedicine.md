# Patient Portal & Telemedicine (SRS 17)

## 1. Overview
The Patient Portal is a secure, patient-facing web and mobile application that allows patients to manage their healthcare journey. It bridges the gap between the hospital's internal HIS and the patient's personal device, offering features like appointment booking, billing, and remote telemedicine consultations.

## 2. Core Workflows

### 2.1 Patient Self-Registration & Onboarding
- **Account Creation**: Patients can register using their mobile number (OTP verification via Twilio) or email.
- **ABHA ID Linking**: Integration with the Ayushman Bharat Digital Mission (ABDM) to fetch or create an ABHA ID, automatically pulling basic demographic data.
- **Profile Completion**: Patients upload government ID (Aadhaar/PAN) for identity verification, which is securely stored in the document vault.

### 2.2 Appointment Booking & Scheduling
- **Search & Filter**: Search for doctors by specialty, symptoms, or location.
- **Slot Selection**: View real-time availability of doctors and book a 15-30 minute time slot.
- **Payment Gateway**: Integration with Stripe/Razorpay for advance consultation fees.
- **Reminders**: Automated SMS and WhatsApp reminders 24 hours and 1 hour prior to the appointment.

### 2.3 Accessing Clinical Records
- **Lab Reports**: View and download PDF lab reports once verified by the pathologist.
- **Radiology**: View PACS images (X-Rays, MRIs) via a web-based zero-footprint DICOM viewer.
- **Prescriptions**: Access digital e-prescriptions with active refill requests.
- **Discharge Summaries**: Download comprehensive discharge summaries post-hospitalization.

### 2.4 Online Bill Payment
- **Invoice Dashboard**: View active invoices, historic receipts, and insurance claim status.
- **Partial Payments**: Ability to pay partial advance amounts for scheduled surgeries.

---

## 3. Telemedicine (Remote Consultation)

### 3.1 Pre-Consultation
- **Triage Questionnaire**: Patients fill out a quick digital SOAP (Subjective) form before the call.
- **Vital Upload**: Patients can manually input current vitals (BP, SpO2, Temp) or connect supported IoT devices.

### 3.2 Secure Video Calling
- **WebRTC Infrastructure**: Peer-to-peer encrypted video calling using a STOMP WebSocket signaling server.
- **Doctor's Interface**: The doctor sees the video feed on the left and the patient's EMR/SOAP note interface on the right.
- **Screen Sharing**: Doctors can share their screen to review radiology images or lab trends with the patient.
- **Bandwidth Fallback**: Automatic fallback to audio-only if the patient's network degrades.

### 3.3 Post-Consultation
- **e-Prescription Generation**: Doctor generates an e-prescription immediately available in the portal.
- **Follow-up Scheduling**: Automated prompt to book a follow-up visit based on the doctor's recommendation.
