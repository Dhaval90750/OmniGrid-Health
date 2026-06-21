# 7. Functional Requirements — Operations Management

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 7.1 Overview

The Operations Management module handles all **non-clinical support services** that are essential for the smooth functioning of a hospital. These services directly impact patient experience, safety, regulatory compliance, and operational costs.

**Sub-Modules:**

1. Housekeeping Management
2. Facility & Maintenance Management
3. Patient Transport Management
4. Ambulance Fleet Management
5. Dietary & Kitchen Management
6. Biomedical Waste Management
7. Security Management
8. Energy & Utility Management
9. Space & Infrastructure Management
10. Staff Scheduling (Operations)
11. Help Desk / Service Request Management
12. Mortuary Management

---

## 7.2 Housekeeping Management

### 7.2.1 Housekeeping Zones

| Zone Type | Description | Cleaning Frequency |
|-----------|-------------|-------------------|
| **Critical Zone** | OT, ICU, NICU, CSSD, Labor Room, Burn Ward | Every 2–4 hours + between patients |
| **Semi-Critical Zone** | Wards, Procedure rooms, Lab, Pharmacy, ER | Every 4–6 hours |
| **Non-Critical Zone** | Corridors, Waiting areas, Offices, Cafeteria | Every 6–8 hours |
| **Restrooms** | Patient, visitor, and staff restrooms | Every 2–4 hours |
| **External Areas** | Parking, Gardens, Entrance | Daily |

### 7.2.2 Housekeeping Workflow

#### Routine Cleaning

```
Shift Starts
     │
┌────▼──────────┐
│ Task Assignment│ → Auto-assigned based on zone schedule
│ (Auto/Manual)  │    Pushed to housekeeping staff's mobile app
└────┬──────────┘
     │
┌────▼──────────┐
│ Staff Check-In │ → Scan zone QR code to mark arrival
│ at Zone        │    Timestamp and location recorded
└────┬──────────┘
     │
┌────▼──────────┐
│ Cleaning       │ → Follow zone-specific checklist
│ Execution      │    (mop, disinfect, restock supplies, etc.)
└────┬──────────┘
     │
┌────▼──────────┐
│ Photo Evidence │ → Take before/after photos
│ (Optional)     │    (mandatory for incident cleaning)
└────┬──────────┘
     │
┌────▼──────────┐
│ Checklist      │ → Mark checklist items as completed
│ Completion     │    (verified by QR scan at zone)
└────┬──────────┘
     │
┌────▼──────────┐
│ Supervisor     │ → Supervisor verifies & approves
│ Verification   │    (Random audit percentage configurable)
└──────────────┘
```

#### Bed Turnaround (Discharge Cleaning)

```
Patient Discharged
       │
  ┌────▼──────────────┐
  │ Auto-Notification  │ → Housekeeping alerted immediately
  │ to Housekeeping    │    Ward + Bed number + Room type
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Staff Assigned     │ → Nearest available staff assigned
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Terminal Cleaning  │ → Complete room/bed cleaning
  │ Performed          │    Linen change, surface disinfection
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Cleaned & Ready    │ → Bed status updated to "Available"
  │ (QR Scan to confirm)│   Bed Management dashboard updated
  └──────────────────┘
```

**Bed Turnaround KPI Target:** < 45 minutes (General), < 60 minutes (ICU/Isolation)

### 7.2.3 Housekeeping Checklist Items (Sample)

| Zone | Checklist Items |
|------|----------------|
| **Patient Room** | Floor mopping, surface wiping, bathroom cleaning, dustbin emptying, linen check, hand sanitizer refill, tissue refill, drinking water check |
| **OT** | Terminal cleaning protocol, UV sterilization, air handling unit check, equipment surface wipe, OT table cleaning, light cleaning |
| **ICU** | Bed area cleaning, monitor wiping, ventilator exterior cleaning, floor mopping, bathroom cleaning, sharp container check |
| **Corridor** | Floor mopping, wall wiping, signage cleaning, fire extinguisher check, hand rub dispenser refill |
| **Restroom** | Toilet cleaning, basin cleaning, floor mopping, mirror cleaning, soap/tissue refill, odor control |

### 7.2.4 Supply Management

- Track consumption of housekeeping supplies (disinfectants, mops, garbage bags, hand sanitizer, tissue)
- Auto-indent generation when supplies below threshold
- Chemical safety data sheets (SDS) available for all chemicals used
- Dilution tracking (correct dilution of cleaning chemicals)

### 7.2.5 Housekeeping Reports

| Report | Description |
|--------|-------------|
| **Task Completion Rate** | % of scheduled tasks completed on time |
| **Bed Turnaround Time** | Average time from discharge notification to bed ready |
| **Zone Compliance** | Cleaning frequency adherence by zone |
| **Staff Productivity** | Tasks completed per staff per shift |
| **Supply Consumption** | Chemical and supply usage by zone |
| **Audit Score** | Random audit results and trends |
| **Incident Log** | Spills, special cleaning requests, complaints |

---

## 7.3 Facility & Maintenance Management

### 7.3.1 Maintenance Types

| Type | Description | Examples |
|------|-------------|---------|
| **Preventive Maintenance (PM)** | Scheduled maintenance to prevent breakdowns | HVAC filter change, elevator servicing, generator testing |
| **Corrective Maintenance (CM)** | Repair after breakdown/malfunction | Plumbing leak, electrical fault, AC breakdown |
| **Predictive Maintenance** | Data-driven maintenance based on equipment performance | Vibration analysis, thermal imaging, oil analysis |
| **Condition-Based** | Maintenance triggered by specific condition monitoring | UPS battery health, chiller performance metrics |
| **Statutory Compliance** | Legally mandated inspections and certifications | Fire safety, electrical safety, elevator certification, boiler inspection |

### 7.3.2 Work Order Management

#### Work Order Lifecycle

```
Issue Reported (by any staff via app/web)
       │
  ┌────▼──────────────┐
  │ Work Order Created │ → Auto-assigned ticket number
  │ (WO)               │    Category, priority, location
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Priority Assessment│ → Critical / High / Medium / Low
  │ & Assignment       │    Assigned to maintenance technician
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Technician         │ → Travel to location, diagnose issue
  │ Dispatched         │
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Diagnosis &        │ → Identify root cause
  │ Parts Requirement  │    Request parts from inventory (if needed)
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Repair / Fix       │ → Execute repair work
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Testing &          │ → Verify fix, functional testing
  │ Verification       │
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Closure &          │ → Mark WO as complete
  │ User Confirmation  │    Requester confirms resolution
  └──────────────────┘
```

#### Work Order Data

| Field | Type | Mandatory |
|-------|------|-----------|
| WO Number | Auto-generated | System |
| Category | Electrical / Plumbing / HVAC / Civil / Biomedical / IT / Carpentry / Other | ✅ |
| Sub-Category | Dropdown (filtered) | ✅ |
| Priority | Critical / High / Medium / Low | ✅ |
| Location | Building → Floor → Ward/Room | ✅ |
| Description | Text | ✅ |
| Photo | Image | ❌ |
| Reported By | User | System |
| Assigned To | Technician | ✅ |
| SLA Target | Auto-calculated based on priority | System |
| Parts Used | From inventory | ❌ |
| Labor Hours | Number | ✅ |
| Cost | Auto-calculated (parts + labor) | System |
| Root Cause | Text | ✅ (at closure) |
| Resolution | Text | ✅ (at closure) |
| Status | Open → In Progress → Awaiting Parts → Completed → Verified | System |

#### SLA Targets

| Priority | Response Time | Resolution Time |
|----------|--------------|-----------------|
| **Critical** (patient safety risk) | 15 minutes | 2 hours |
| **High** (service disruption) | 30 minutes | 4 hours |
| **Medium** (comfort issue) | 2 hours | 24 hours |
| **Low** (cosmetic/minor) | 4 hours | 72 hours |

### 7.3.3 Preventive Maintenance Scheduling

| Equipment Category | PM Frequency | Checklist Items |
|-------------------|-------------|-----------------|
| **HVAC (Central AC)** | Monthly | Filter cleaning, coil inspection, refrigerant check, thermostat calibration |
| **Elevators** | Monthly | Safety check, cable inspection, motor inspection, battery backup test |
| **Generators (DG Sets)** | Weekly (run test), Monthly (full service) | Oil level, coolant, battery, load test, auto-changeover test |
| **UPS Systems** | Quarterly | Battery health, input/output voltage, load capacity test |
| **Fire Systems** | Monthly (inspection), Annual (full test) | Extinguisher check, sprinkler test, alarm test, pump test |
| **Water Treatment** | Daily | TDS check, chlorination, culture testing |
| **Boilers** | Monthly | Pressure test, safety valve test, water quality |
| **Medical Gas Pipeline** | Monthly | Pressure check, leak test, alarm test |
| **Electrical Panels** | Quarterly | Thermography, connection tightness, breaker testing |

### 7.3.4 Biomedical Equipment Maintenance

| Feature | Description |
|---------|-------------|
| **PM Scheduling** | Per manufacturer-recommended frequency |
| **Calibration** | Track calibration due dates, results, certificates |
| **Breakdown Logging** | Log equipment failures with impact assessment |
| **Downtime Tracking** | Total downtime per equipment per month |
| **Vendor Escalation** | Auto-escalate to AMC vendor if in-house cannot resolve |
| **Condemnation Process** | Equipment condemnation workflow with committee approval |
| **Replacement Planning** | Track equipment age, maintenance cost trend, replacement recommendations |

### 7.3.5 Maintenance Reports & KPIs

| Report/KPI | Description |
|-----------|-------------|
| **PM Compliance Rate** | % of scheduled PMs completed on time (Target: > 95%) |
| **MTBF** | Mean Time Between Failures per equipment category |
| **MTTR** | Mean Time To Repair per category |
| **WO Aging Report** | Open work orders by age bucket |
| **SLA Compliance** | % of WOs resolved within SLA target |
| **Cost Report** | Maintenance cost by category, location, equipment |
| **Equipment Uptime** | Availability percentage for critical equipment |
| **Statutory Compliance** | Status of all statutory certifications and inspections |

---

## 7.4 Patient Transport Management

### 7.4.1 Internal Transport

| Transport Type | Description | Assets |
|---------------|-------------|--------|
| **Wheelchair** | Ambulatory patients needing assistance | Wheelchairs (tracked by barcode) |
| **Stretcher** | Non-ambulatory patients | Stretchers, trolleys |
| **Bed Transport** | ICU/critical patients moved in bed | Patient beds |
| **Porter Service** | Manual transport by orderlies | — |
| **Pneumatic Tube** | Lab sample transport | Tube system stations |

### 7.4.2 Transport Request Workflow

```
Transport Requested (by nurse/receptionist)
       │
  ┌────▼──────────────┐
  │ Request Created    │ → From: Ward 3, Bed 12
  │                    │    To: Radiology
  │                    │    Type: Wheelchair
  │                    │    Priority: Normal
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Auto-Assigned to   │ → Nearest available porter
  │ Porter             │    Push notification sent
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Porter En Route    │ → Status updated, ETA shown
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Patient Picked Up  │ → QR scan confirms patient identity
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Patient Delivered  │ → QR scan at destination
  │                    │    Transport time logged
  └──────────────────┘
```

---

## 7.5 Ambulance Fleet Management

### 7.5.1 Fleet Inventory

| Field | Description |
|-------|-------------|
| Vehicle Number | Registration number |
| Vehicle Type | BLS (Basic Life Support) / ALS (Advanced Life Support) / Patient Transport / Mortuary |
| Equipment List | Defibrillator, oxygen, stretcher, monitor, etc. |
| Driver Assigned | Current driver |
| Paramedic Assigned | Current paramedic |
| GPS Location | Real-time location |
| Status | Available / On Trip / Under Maintenance / Out of Service |
| Insurance Validity | Expiry tracking |
| Fitness Certificate | Validity tracking |
| Fuel Log | Fuel filled, consumption per trip |
| Maintenance Log | Service history, next service due |

### 7.5.2 Dispatch Workflow

```
Request Received (ER call / 108 / Hospital transfer)
       │
  ┌────▼──────────────┐
  │ Location Received  │ → Pick-up location, patient condition
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Nearest Ambulance  │ → GPS-based nearest available vehicle
  │ Identified         │    BLS/ALS based on reported condition
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Dispatched         │ → Driver/paramedic notified
  │                    │    ETA calculated and communicated
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ En Route           │ → Real-time tracking on map
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Patient Contact    │ → Assessment begins
  │                    │    Pre-hospital care documented
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ En Route to        │ → Pre-alert sent to ER
  │ Hospital           │    Vitals, ETA shared
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Patient Handed     │ → Handover to ER team
  │ Over to ER         │    Run sheet completed
  └──────────────────┘
```

---

## 7.6 Dietary & Kitchen Management

### 7.6.1 Kitchen Operations

| Feature | Description |
|---------|-------------|
| **Menu Planning** | Weekly/monthly menu creation by dietitian |
| **Diet Order Integration** | Patient diet orders auto-routed to kitchen |
| **Meal Preparation** | Quantity estimation based on census, diet type count |
| **Meal Tray Assembly** | Patient-specific tray with name, UHID, diet type, bed number |
| **Distribution** | Floor-wise, ward-wise meal distribution with timing |
| **Special Diets** | Diabetic, Renal, Cardiac, Soft, Liquid, NPO tracking |
| **Allergy Alerts** | Patient food allergies flagged on tray ticket |
| **Supplemental Feeds** | Between-meal snacks, nutritional supplements |
| **Staff Meals** | Staff cafeteria management (separate) |
| **Visitor/Attendant Meals** | Paid meal service for attendants |

### 7.6.2 Meal Summary Dashboard

```
┌──────────────────────────────────────────────────────────────┐
│  KITCHEN DASHBOARD - LUNCH                 Date: 21-Jun-2026 │
├──────────────────────────────────────────────────────────────┤
│  Total Patients: 412                                         │
│  NPO: 35 │ Liquid: 18 │ Soft: 25 │ Regular: 290             │
│  Diabetic: 55 │ Renal: 12 │ Cardiac: 28 │ Low Sodium: 15    │
│  Special Request: 8                                          │
├──────────────────────────────────────────────────────────────┤
│  Ward-wise Meal Count:                                       │
│  General Med: 52 │ General Surg: 43 │ Ortho: 25              │
│  Cardiology: 18  │ Pediatrics: 18   │ OB/GYN: 24             │
│  Private: 65     │ Suite: 15        │ Others: 117             │
│  (ICU meals managed separately with enteral feed tracking)   │
├──────────────────────────────────────────────────────────────┤
│  Distribution Status:                                        │
│  🟢 Floor 1: Delivered  🟡 Floor 2: In Progress              │
│  ⚪ Floor 3: Pending    ⚪ Floor 4: Pending                   │
└──────────────────────────────────────────────────────────────┘
```

### 7.6.3 Kitchen Compliance

| Requirement | Tracking |
|------------|---------|
| **FSSAI License** | Validity tracking |
| **Food Safety Audit** | Monthly internal audit scores |
| **Temperature Logging** | Refrigerator/freezer temperature logs (2× daily) |
| **Water Quality** | Daily water testing results |
| **Staff Hygiene** | Health checkup records, uniform compliance |
| **Pest Control** | Pest control schedule and records |
| **HACCP Compliance** | Critical control point monitoring |

---

## 7.7 Biomedical Waste Management

### 7.7.1 Waste Categories (as per BMW Rules, 2016)

| Category | Color Code | Container | Examples | Treatment |
|----------|-----------|-----------|----------|-----------|
| **Yellow** | 🟡 Yellow | Non-chlorinated bag | Human tissue, body parts, expired medicines, chemical waste | Incineration |
| **Red** | 🔴 Red | Non-chlorinated bag | Contaminated recyclables (tubing, bottles, catheters) | Autoclaving → Shredding |
| **White** | ⚪ White | Puncture-proof container | Sharps (needles, blades, broken glass) | Autoclaving → Shredding |
| **Blue** | 🔵 Blue | Cardboard box | Glassware (non-contaminated), metallic implants | Autoclaving → Recycling |

### 7.7.2 BMW Tracking

```
Waste Generated (at Point of Care)
       │
  ┌────▼──────────────┐
  │ Segregation at     │ → Staff segregates into correct color bin
  │ Source             │    (Compliance monitored via audits)
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Collection         │ → Scheduled collection from each ward/dept
  │ (by trained staff) │    Weight recorded per collection point
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Transport to       │ → Designated route, designated time
  │ Waste Storage      │    Spill kit available
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Central Waste      │ → Temporary storage (max 48 hours)
  │ Storage Area       │
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Handover to CBWTF  │ → Common Biomedical Waste Treatment Facility
  │ (Authorized vendor)│    Manifest form generated (6 copies)
  └────┬──────────────┘
       │
  ┌────▼──────────────┐
  │ Confirmation       │ → Treatment confirmation received
  │ of Treatment       │    Records maintained for 5 years
  └──────────────────┘
```

### 7.7.3 BMW Reports

| Report | Frequency | Submitted To |
|--------|-----------|-------------|
| **Daily Waste Generation** | Daily | Internal |
| **Monthly Summary** | Monthly | State Pollution Control Board |
| **Annual Report** | Annual | CPCB / SPCB |
| **Segregation Audit** | Monthly | Infection Control Committee |
| **Incident Report** | As needed | Hospital Admin |

---

## 7.8 Security Management

### 7.8.1 Features

| Feature | Description |
|---------|-------------|
| **CCTV Integration** | View live feeds, event-based recording |
| **Access Control** | Card/biometric access for restricted areas (OT, NICU, Drug Store, Server Room) |
| **Visitor Management** | Visitor registration, badge printing, time-limited access, visitor count per patient |
| **Incident Logging** | Security incidents documented with location, time, involved persons, action taken |
| **Guard Patrol** | Guard patrol route tracking via NFC/QR checkpoints |
| **Emergency Codes** | Code Blue (cardiac arrest), Code Red (fire), Code Pink (infant abduction), Code Grey (combative person), Code Black (bomb threat) |
| **Vehicle Management** | Parking allocation, vehicle entry/exit log, VIP parking |
| **Key Management** | Digital key register for restricted areas |
| **Lost & Found** | Track lost and found items |

---

## 7.9 Energy & Utility Management

### 7.9.1 Monitored Utilities

| Utility | Monitoring Points |
|---------|------------------|
| **Electricity** | Total consumption, floor-wise, department-wise, DG set usage, solar generation |
| **Water** | Total consumption, hot water, RO plant output, STP output |
| **Medical Gases** | Oxygen (bulk + cylinder), Nitrous Oxide, Medical Air, Vacuum, CO2 |
| **HVAC** | Temperature, humidity by zone, AHU performance, chiller efficiency |
| **Steam/Hot Water** | Boiler output, steam consumption |
| **Fuel** | Diesel (for DG), LPG/PNG (for kitchen/boiler) |

### 7.9.2 Features

| Feature | Description |
|---------|-------------|
| **Real-Time Monitoring** | IoT sensors for energy consumption, gas pressure, water flow |
| **Alarm Thresholds** | Auto-alerts for abnormal consumption, low gas pressure, high temperature |
| **Cost Tracking** | Monthly utility costs, cost per bed per day |
| **Benchmarking** | Compare consumption against industry benchmarks |
| **Green Initiatives** | Track solar generation, rainwater harvesting, waste recycling |
| **Compliance** | Biomedical waste disposal, emission monitoring, noise levels |

### 7.9.3 Medical Gas Pipeline Monitoring

| Parameter | Alert Threshold |
|-----------|----------------|
| **Oxygen Pressure** | < 3.5 bar or > 5.0 bar |
| **Vacuum Pressure** | < -400 mmHg |
| **Medical Air Pressure** | < 3.5 bar |
| **Bulk Oxygen Level** | < 30% capacity |
| **Cylinder Reserve** | < 20% of safety stock |

---

## 7.10 Mortuary Management

### 7.10.1 Features

| Feature | Description |
|---------|-------------|
| **Body Receipt** | Log body receipt with time of death, ward, attending doctor |
| **Storage** | Mortuary slot assignment, temperature monitoring |
| **Identification** | Body tag with patient details, QR-linked |
| **Documentation** | Death certificate, MLC documentation, embalming records |
| **Release Process** | Body release to family/authorities with photo ID verification, police clearance (for MLC) |
| **Refrigeration Monitoring** | Temperature logs, alert on malfunction |
| **Time Tracking** | Duration in mortuary, auto-alerts for bodies > 48 hours |

---

## 7.11 Help Desk / Service Request Portal

### 7.11.1 Overview

A unified service request portal for all non-clinical operational requests:

| Request Category | Examples |
|-----------------|---------|
| **Housekeeping** | Spill cleanup, extra cleaning, bed making |
| **Maintenance** | AC not working, light out, leaking pipe |
| **IT Support** | Printer issue, network down, system access |
| **Transport** | Wheelchair request, patient transfer |
| **Dietary** | Meal change, special request |
| **Linen** | Extra blankets, pillow replacement |
| **Security** | Visitor issue, lost item, access card |
| **Biomedical** | Equipment malfunction, consumable request |

### 7.11.2 Request Flow

1. **Request Created** (via mobile app, web portal, or phone call to help desk)
2. **Auto-Categorized** (AI-assisted categorization from description)
3. **Auto-Assigned** (to relevant department/person based on category + location)
4. **SLA Timer Starts**
5. **Status Updates** (visible to requester in real-time)
6. **Resolution & Feedback** (requester rates the service)

### 7.11.3 Help Desk KPIs

| KPI | Target |
|-----|--------|
| First Response Time | < 15 minutes |
| Resolution Rate (within SLA) | > 90% |
| Customer Satisfaction Score | > 4.0 / 5.0 |
| Repeat Request Rate | < 5% |
| Escalation Rate | < 10% |

---

## 7.12 Operations Dashboards

### 7.12.1 Operations Manager Dashboard

```
┌──────────────────────────────────────────────────────────────────┐
│  OPERATIONS DASHBOARD                       Date: 21-Jun-2026   │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐  ┌────────────────┐ │
│  │ HOUSEKEEPING     │  │ MAINTENANCE      │  │ TRANSPORT      │ │
│  │ Tasks Today: 245 │  │ Open WOs: 34     │  │ Trips Today: 67│ │
│  │ Completed: 198   │  │ Critical: 2      │  │ Ambulances: 5  │ │
│  │ Pending: 47      │  │ SLA Breach: 5    │  │ Available: 3   │ │
│  │ Compliance: 81%  │  │ PM Due: 12       │  │ Avg Wait: 8min │ │
│  └──────────────────┘  └──────────────────┘  └────────────────┘ │
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐  ┌────────────────┐ │
│  │ DIETARY          │  │ WASTE MGMT       │  │ SECURITY       │ │
│  │ Meals Served: 845│  │ Waste Today:     │  │ Visitors: 312  │ │
│  │ Special Diets: 95│  │  Yellow: 45 kg   │  │ Incidents: 0   │ │
│  │ Complaints: 2    │  │  Red: 23 kg      │  │ Patrol: ✅ OK  │ │
│  │ Rating: 4.2/5    │  │  White: 8 kg     │  │ CCTV: ✅ All Up│ │
│  └──────────────────┘  └──────────────────┘  └────────────────┘ │
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐  ┌────────────────┐ │
│  │ ENERGY           │  │ HELP DESK        │  │ LINEN          │ │
│  │ Power: 3,450 kWh │  │ Open Tickets: 28 │  │ Clean Stock:   │ │
│  │ Water: 85 KL     │  │ Avg Resolution:  │  │  Sheets: 450   │ │
│  │ O2: 78% Level    │  │  2.3 hours       │  │  Gowns: 200    │ │
│  │ DG Runtime: 2 hr │  │ Satisfaction:    │  │ Soiled Return: │ │
│  │ Solar: 120 kWh   │  │  4.1 / 5.0       │  │  85% on time   │ │
│  └──────────────────┘  └──────────────────┘  └────────────────┘ │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 7.13 Operations Staff Scheduling

### 7.13.1 Shift Management

| Shift | Typical Timing | Coverage |
|-------|---------------|----------|
| **Morning** | 6:00 AM – 2:00 PM | Full staffing |
| **Afternoon** | 2:00 PM – 10:00 PM | Full staffing |
| **Night** | 10:00 PM – 6:00 AM | Reduced staffing |

### 7.13.2 Features

| Feature | Description |
|---------|-------------|
| **Roster Creation** | Monthly roster creation by supervisor |
| **Shift Swap** | Staff can request shift swaps (approval required) |
| **Leave Management** | Apply for leave, approval workflow, auto-substitute assignment |
| **Attendance Tracking** | Biometric/mobile check-in, GPS verification for field staff |
| **Overtime Tracking** | Auto-calculate overtime hours |
| **Skill Matrix** | Track staff certifications and capabilities for appropriate task assignment |
| **Performance Tracking** | Task completion rate, feedback scores, incident involvement |

---

[→ Next: AI Features & Future Roadmap](./08_AI_Features.md)
