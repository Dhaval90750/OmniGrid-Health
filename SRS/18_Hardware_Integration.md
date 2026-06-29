# Hardware & Device Integration (SRS 18)

## 1. Overview
The HIS requires seamless integration with physical hospital hardware, including thermal printers, biometric scanners, RFID gates, and complex laboratory analyzers. This document specifies the communication protocols and hardware requirements.

## 2. Printer Integrations

### 2.1 Thermal Wristband Printers (Zebra/TSC)
- **Purpose**: Printing patient identification wristbands (with barcodes/QR codes) at the registration and IPD admission desks.
- **Protocol**: ZPL (Zebra Programming Language) or TSPL.
- **Workflow**: 
  - The Spring Boot backend generates a base64 encoded ZPL template injected with patient data (UHID, Name, Blood Group, Allergy Alerts).
  - The web browser sends the ZPL directly to the local network printer via a Raw Print server (e.g., QZ Tray).

### 2.2 Barcode Printers (Lab Samples)
- **Purpose**: Printing small labels for vacutainers during sample collection.
- **Requirements**: Chemical-resistant, smudge-proof labels. Must print the Sample ID barcode natively to ensure high scan readability.

## 3. Laboratory Analyzer Integration (LIMS)

### 3.1 Bi-directional Interface
- **Purpose**: Automating the transfer of test orders to lab machines and receiving results back without manual data entry.
- **Protocols Supported**: 
  - **HL7 v2.x**: Standard messaging over TCP/IP (MLLP).
  - **ASTM E1381 / E1394**: Serial communication protocol commonly used by older Sysmex or Beckman Coulter analyzers.
- **Workflow**:
  1. **Order Broadcast**: When a sample is collected and marked 'Accepted' in the HIS, an HL7 ORM (Order Message) is broadcast to the analyzer.
  2. **Result Reception**: The analyzer performs the test and sends an HL7 ORU (Observation Result) back to the HIS middleware.
  3. **Auto-Verification**: The HIS parses the result. If within normal ranges, it auto-verifies; if abnormal, it flags for pathologist review.

## 4. IoT & Biometrics

### 4.1 Biometric Scanners (Staff Attendance & Approvals)
- **Devices**: SecuGen or Mantra fingerprint scanners.
- **Integration**: USB-based local service that captures the fingerprint template (ISO 19794-2 standard) and sends the hash to the HIS backend for authentication.
- **Use Cases**: Clock-in/Clock-out for staff, and biometric sign-off for high-risk medications (e.g., Narcotics).

### 4.2 RFID Tracking
- **Infant Security**: Active RFID tags on newborn anklets. If the tag leaves the designated maternity ward without authorization, an alarm is triggered in the HIS and security systems.
- **Asset Tracking**: Passive RFID tags on expensive portable equipment (e.g., Mobile X-Ray, Infusion Pumps) to track location across hospital zones.
