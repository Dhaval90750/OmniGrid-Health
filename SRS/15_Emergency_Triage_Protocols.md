# 15. Emergency & Triage Protocols

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 15.1 Overview
The Emergency Module handles unscheduled, critical patient arrivals. It focuses on rapid registration, immediate clinical assessment (triage), and fast-tracking to appropriate care areas (Resuscitation, ER Beds, OT).

## 15.2 Triage Scoring System
We use a 5-level Emergency Severity Index (ESI):
- **Level 1 (Resuscitation):** Immediate life-saving intervention required (e.g., cardiac arrest).
- **Level 2 (Emergent):** High risk situation, confused/lethargic/disoriented, or severe pain/distress.
- **Level 3 (Urgent):** Multiple resources required (e.g., lab + x-ray), stable vitals.
- **Level 4 (Less Urgent):** One resource required.
- **Level 5 (Non-Urgent):** No resources required (e.g., prescription refill).

## 15.3 Workflow: Unknown Patient (John Doe)
1. Patient arrives unconscious/unidentified.
2. System generates an emergency UHID (e.g., `EMG-2026-0001`).
3. Patient registered with alias "Unknown Male 1".
4. Triage and treatment begin immediately.
5. Once identified, the emergency profile is merged with the real profile via `POST /api/v1/patients/merge`.

## 15.4 Mass Casualty Incident (MCI) Protocol
- Triggers MCI mode in the system, bypassing standard billing checks.
- Bulk rapid registration UI enabled.
- Auto-alerts sent to on-call surgeons, intensivists, and blood bank.

---
[← Back to Table of Contents](./00_Table_of_Contents.md)
