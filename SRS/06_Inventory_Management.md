# 6. Functional Requirements — Inventory Management

[← Back to Table of Contents](./00_Table_of_Contents.md)

---

## 6.1 Overview

The Inventory Management module manages the complete lifecycle of all hospital supplies — from procurement to consumption. It covers medical consumables, surgical items, general stores, linen, biomedical equipment, and fixed assets. This module integrates tightly with:

- **Pharmacy** — Drug inventory (managed within Pharmacy module, but procurement flows through Inventory)
- **OT Management** — Surgical consumables, implants
- **Nursing** — Ward consumables
- **Lab** — Reagents, consumables
- **Operations** — Housekeeping supplies, maintenance spares
- **Billing** — Consumable charges to patients
- **Finance** — Purchase accounting, asset depreciation

---

## 6.2 Inventory Organization

### 6.2.1 Store Hierarchy

```
CENTRAL STORES (MAIN WAREHOUSE)
├── Medical Stores
│   ├── Surgical Consumables (Sutures, Dressings, Catheters, Drains, Tubes)
│   ├── IV Fluids & Solutions
│   ├── Syringes, Needles, Cannulas
│   ├── Gloves, Masks, PPE
│   ├── Diagnostic Kits (Rapid Tests, Glucometer Strips)
│   └── Implants & Prosthetics
│
├── General Stores
│   ├── Stationery & Printing
│   ├── Housekeeping Supplies (Disinfectants, Cleaning Agents, Mops)
│   ├── Kitchen Supplies
│   ├── Linen & Uniforms
│   ├── Electrical & Plumbing Spares
│   └── IT Consumables (Cartridges, Cables)
│
├── Lab Stores
│   ├── Reagents (Hematology, Biochemistry, Microbiology)
│   ├── Lab Consumables (Tubes, Slides, Cover Slips, Petri Dishes)
│   ├── Controls & Calibrators
│   └── Blood Bank Supplies (Blood Bags, Anticoagulants)
│
├── Biomedical Engineering Stores
│   ├── Spare Parts (Equipment-specific)
│   ├── Batteries, Sensors, Electrodes
│   └── Calibration Tools
│
└── Pharmacy Central Store (Drug Warehouse)
    └── (Managed by Pharmacy Module — linked for procurement)
```

### 6.2.2 Sub-Stores (Department-Level)

Each department/ward maintains a sub-store:

| Sub-Store | Managed By | Items |
|-----------|-----------|-------|
| OT Sub-Store | OT Nurse/Technician | Surgical consumables, sutures, drapes, implants |
| ICU Sub-Store | ICU Nurse | Central line kits, ABG syringes, ventilator circuits |
| Ward Sub-Store (per ward) | Ward Nurse | Dressings, IV sets, catheters, gloves |
| ER Sub-Store | ER Nurse | Emergency supplies, resuscitation equipment |
| Lab Sub-Store | Lab Manager | Reagents, consumables for daily use |
| Radiology Sub-Store | Radiology Tech | Contrast media, films (if applicable) |
| CSSD Sub-Store | CSSD Technician | Sterilization indicators, packing material |

---

## 6.3 Item Master Data

### 6.3.1 Item Master Fields

| Field | Type | Mandatory | Description |
|-------|------|-----------|-------------|
| Item Code | Auto-generated | System | Unique identifier (e.g., `MED-SUR-00124`) |
| Item Name | Text | ✅ | Standard name |
| Item Category | Dropdown | ✅ | Surgical Consumable / General / Lab / Biomedical / Linen / IT / etc. |
| Item Sub-Category | Dropdown | ✅ | Further classification |
| Item Type | Dropdown | ✅ | Consumable / Reusable / Capital Equipment / Implant |
| UOM (Unit of Measure) | Dropdown | ✅ | Nos / Box / Pack / Roll / Liter / Kg / Set |
| Conversion Factor | Number | ❌ | 1 Box = 100 Nos (for purchase vs issue unit conversion) |
| HSN Code | Text | ✅ | For GST |
| GST Rate | Percentage | ✅ | 5% / 12% / 18% / 28% |
| Reorder Level | Number | ✅ | Minimum stock threshold to trigger reorder |
| Reorder Quantity | Number | ✅ | Standard order quantity |
| Maximum Level | Number | ✅ | Maximum stock to hold |
| Lead Time | Days | ✅ | Average vendor delivery time |
| Safety Stock | Number | System | Auto-calculated: Average daily consumption × Lead time |
| ABC Classification | Dropdown | System | A (high value) / B (medium) / C (low) — auto-calculated |
| VED Classification | Dropdown | ✅ | Vital / Essential / Desirable |
| Critical Flag | Boolean | ❌ | For items that cannot be out of stock (life-saving) |
| Patient Chargeable | Boolean | ✅ | Whether cost is billed to patient |
| Charge Code | Text | Conditional | Billing tariff code (if chargeable) |
| Shelf Life | Days | ❌ | For items with expiry |
| Storage Conditions | Dropdown | ❌ | Room Temp / Refrigerated / Frozen / Light-Sensitive |
| Image | Photo | ❌ | Item photo for identification |
| Approved Vendors | Multi-select | ❌ | List of approved vendors for this item |
| Specifications | Text | ❌ | Technical specifications |
| Substitute Items | Multi-select | ❌ | Alternate acceptable items |

### 6.3.2 Item Classification Matrix

| Classification | Basis | Categories |
|---------------|-------|-----------|
| **ABC Analysis** | Annual consumption value | A: Top 70% value (≈10% items), B: Next 20% (≈20% items), C: Bottom 10% (≈70% items) |
| **VED Analysis** | Criticality | V: Life-saving, no alternative; E: Essential for operations; D: Desirable but can defer |
| **FSN Analysis** | Movement frequency | F: Fast-moving; S: Slow-moving; N: Non-moving |
| **HML Analysis** | Unit price | H: High cost; M: Medium cost; L: Low cost |
| **SDE Analysis** | Procurement difficulty | S: Scarce; D: Difficult; E: Easy to procure |

---

## 6.4 Procurement Workflow

### 6.4.1 End-to-End Procurement Flow

```
Need Identified
      │
  ┌───▼────────────┐
  │ Purchase Indent │ → Department raises indent (manual or auto-trigger)
  │ (Requisition)   │    Items, quantities, urgency, justification
  └───┬────────────┘
      │
  ┌───▼────────────┐
  │ Indent Approval│ → Multi-level approval based on value
  │                │    Level 1: Dept Head (< ₹50K)
  │                │    Level 2: Admin (₹50K–₹5L)
  │                │    Level 3: Finance/CEO (> ₹5L)
  └───┬────────────┘
      │
  ┌───▼────────────┐
  │ Quotation /    │ → Request for Quotation (RFQ) to approved vendors
  │ Rate Contract  │    Compare quotes, negotiate rates
  └───┬────────────┘
      │
  ┌───▼────────────────┐
  │ Purchase Order (PO)│ → Generate PO with terms & conditions
  │ Generation          │    PO number, delivery date, payment terms
  └───┬────────────────┘
      │
  ┌───▼────────────┐
  │ PO Approval    │ → Financial approval per authority matrix
  └───┬────────────┘
      │
  ┌───▼────────────┐
  │ PO Dispatch    │ → Send PO to vendor (email/portal)
  │ to Vendor      │
  └───┬────────────┘
      │
  ┌───▼────────────┐
  │ Goods Receipt  │ → Physical receipt, quantity check, quality inspection
  │ Note (GRN)     │    Batch number, expiry date, MFG date recorded
  └───┬────────────┘
      │
  ┌───▼────────────┐
  │ Quality Check  │ → Visual inspection + specification verification
  │ (QC)           │    Accept / Reject / Partial Accept
  └───┬────────────┘
      │
  ┌───▼────────────┐
  │ Stock Updated  │ → Inventory updated, mapped to store location
  └───┬────────────┘
      │
  ┌───▼────────────┐
  │ Invoice Entry  │ → Vendor invoice matched against PO and GRN
  │ & 3-Way Match  │    PO Amount = GRN Amount = Invoice Amount
  └───┬────────────┘
      │
  ┌───▼────────────┐
  │ Payment        │ → Payment processed per vendor terms
  │ Processing     │    (Linked to Finance/Accounts module)
  └──────────────┘
```

### 6.4.2 Purchase Indent (Requisition)

| Field | Type | Mandatory |
|-------|------|-----------|
| Indent Number | Auto-generated | System |
| Indent Date | Date | System |
| Requesting Department | Dropdown | ✅ |
| Requested By | User | System |
| Priority | Routine / Urgent / Emergency | ✅ |
| Justification | Text | ✅ (for non-routine) |
| **Item Lines:** | | |
| Item Code/Name | Searchable | ✅ |
| Requested Quantity | Number | ✅ |
| Required By Date | Date | ✅ |
| Current Stock | Auto-displayed | System |
| Last Purchase Rate | Auto-displayed | System |
| Remarks | Text | ❌ |

### 6.4.3 Auto-Indent (System-Generated)

The system automatically generates purchase indents when:

| Trigger | Condition |
|---------|-----------|
| **Reorder Point** | Stock falls below reorder level |
| **Consumption Forecast** | AI predicts stock-out based on consumption trend |
| **Scheduled Replenishment** | Regular periodic orders (e.g., weekly linen, monthly stationery) |
| **Critical Item Alert** | VED "Vital" item below safety stock |

### 6.4.4 Purchase Order (PO)

| Field | Type | Mandatory |
|-------|------|-----------|
| PO Number | Auto-generated | System |
| PO Date | Date | System |
| Vendor | Searchable | ✅ |
| Delivery Address | Dropdown (store) | ✅ |
| Expected Delivery Date | Date | ✅ |
| Payment Terms | Dropdown | ✅ |
| **Item Lines:** | | |
| Item Code/Name | From indent | ✅ |
| Quantity | Number | ✅ |
| Unit Rate | Currency | ✅ |
| Discount | Percentage/Amount | ❌ |
| GST | Auto-calculated | System |
| Total Amount | Auto-calculated | System |
| **Terms & Conditions** | Text | ✅ |
| **Attachments** | File | ❌ |

### 6.4.5 Goods Receipt Note (GRN)

| Field | Type | Mandatory |
|-------|------|-----------|
| GRN Number | Auto-generated | System |
| GRN Date | Date | System |
| Against PO | PO reference | ✅ |
| Vendor | Auto-populated | System |
| Delivery Challan No. | Text | ✅ |
| **Item Lines:** | | |
| Item Code/Name | From PO | ✅ |
| PO Quantity | Auto-displayed | System |
| Received Quantity | Number | ✅ |
| Accepted Quantity | Number | ✅ |
| Rejected Quantity | Auto-calculated | System |
| Rejection Reason | Text | Conditional |
| Batch Number | Text | ✅ (for consumables) |
| Manufacturing Date | Date | ✅ |
| Expiry Date | Date | ✅ (for items with shelf life) |
| **Quality Check:** | | |
| Visual Inspection | Pass/Fail | ✅ |
| Specification Match | Pass/Fail | ✅ |
| Sample Testing | Pass/Fail/NA | ❌ |
| QC Comments | Text | ❌ |
| Received By | User | System |

---

## 6.5 Stock Operations

### 6.5.1 Stock Issue (to Department)

| Field | Type | Mandatory |
|-------|------|-----------|
| Issue Number | Auto-generated | System |
| Issue Date | Date | System |
| Issue To (Department) | Dropdown | ✅ |
| Requested By | User | ✅ |
| **Item Lines:** | | |
| Item Code/Name | Searchable | ✅ |
| Requested Quantity | Number | ✅ |
| Issued Quantity | Number | ✅ |
| Batch Number | Auto-selected (FEFO) | System |
| Expiry Date | Auto-displayed | System |
| **Patient Linkage:** | | |
| UHID (if patient-specific) | Patient search | ❌ |
| Consumption Type | Ward Stock / Patient Specific / Emergency | ✅ |

### 6.5.2 Patient-Level Consumption Tracking

For items directly used on a patient (and chargeable):

```
Nurse Scans Patient QR
        │
   ┌────▼──────────┐
   │ Select Item    │ → Scan item barcode or search
   │ (Barcode Scan) │
   └────┬──────────┘
        │
   ┌────▼──────────┐
   │ Enter Quantity │ → How many units used
   └────┬──────────┘
        │
   ┌────▼──────────┐
   │ Auto-Billed   │ → Charge added to patient's bill
   │ + Stock Updated│    Ward sub-store stock decremented
   └──────────────┘
```

**Examples of patient-chargeable consumables:**
- IV cannula, IV set, syringe
- Urinary catheter, Ryles tube
- Surgical dressings, wound care supplies
- Oxygen delivery devices
- Central line kit, chest drain kit
- Blood glucose strips

### 6.5.3 Inter-Store Transfer

| Field | Description |
|-------|-------------|
| Transfer Number | Auto-generated |
| From Store | Source store |
| To Store | Destination store |
| Items | With batch and quantity |
| Reason | Regular replenishment / Emergency / Redistribution |
| Issued By | Source store user |
| Received By | Destination store user |

### 6.5.4 Stock Return

| Return Type | Flow |
|------------|------|
| **Ward → Central Store** | Excess stock returned from sub-store to central |
| **Patient Return** | Unused patient items returned (credit note generated for billing) |
| **Vendor Return** | Defective/expired items returned to vendor (debit note generated) |

### 6.5.5 Stock Adjustment

| Adjustment Type | Description |
|----------------|-------------|
| **Positive Adjustment** | Stock found excess during physical count |
| **Negative Adjustment** | Stock found short (breakage, theft, miscounting) |
| **Write-Off** | Expired/damaged stock written off with approval |
| **Sample Conversion** | Free samples converted to stock (if applicable) |

All adjustments require:
- Reason documentation
- Approval (above threshold value)
- Audit trail entry

---

## 6.6 Implant & High-Value Item Tracking

### 6.6.1 Implant Master

| Field | Type | Mandatory |
|-------|------|-----------|
| Implant Type | Dropdown | ✅ |
| Manufacturer | Text | ✅ |
| Brand/Model | Text | ✅ |
| Serial Number | Text | ✅ |
| Lot/Batch Number | Text | ✅ |
| Size | Text | ✅ |
| Material | Text | ✅ |
| MRP | Currency | ✅ |
| Purchase Price | Currency | ✅ |
| Vendor | Reference | ✅ |
| Expiry Date | Date | ✅ |
| Regulatory Approval | Text | ✅ |
| USFDA / CE Marking | Boolean | ✅ |
| Barcode/UDI | Text | ✅ |

### 6.6.2 Implant Traceability

```
Implant Received (GRN with serial number)
        │
   ┌────▼──────────┐
   │ Stored in      │ → OT sub-store or central store
   │ Inventory      │    Tracked by serial number
   └────┬──────────┘
        │
   ┌────▼──────────┐
   │ Issued to OT  │ → For specific surgery
   └────┬──────────┘
        │
   ┌────▼──────────────┐
   │ Used in Surgery   │ → Linked to patient UHID, surgery record
   │ (or Returned)     │    Surgeon confirms implant details
   └────┬──────────────┘
        │
   ┌────▼──────────────┐
   │ Implant Sticker   │ → Sticker from implant packaging attached to:
   │ Documentation     │    • Patient's OT record
   │                   │    • Patient's file
   │                   │    • Insurance claim documents
   └────┬──────────────┘
        │
   ┌────▼──────────┐
   │ Billed to     │ → MRP or negotiated rate charged to patient
   │ Patient       │
   └──────────────┘
```

### 6.6.3 Implant Recall Management

If a manufacturer issues an implant recall:
1. System searches all implants by manufacturer + lot number
2. Identifies all patients who received the recalled implant
3. Generates patient list with contact details
4. Triggers notification to treating surgeons
5. Tracks follow-up actions

---

## 6.7 Fixed Asset Management

### 6.7.1 Asset Master

| Field | Type | Mandatory |
|-------|------|-----------|
| Asset Code | Auto-generated | System |
| Asset Name | Text | ✅ |
| Category | Medical Equipment / IT / Furniture / Vehicle / Building / Other | ✅ |
| Sub-Category | Text | ✅ |
| Make/Model | Text | ✅ |
| Serial Number | Text | ✅ |
| Purchase Date | Date | ✅ |
| Purchase Value | Currency | ✅ |
| Vendor | Reference | ✅ |
| Warranty Start | Date | ✅ |
| Warranty End | Date | ✅ |
| Location | Department/Ward/Room | ✅ |
| Custodian | User | ✅ |
| Status | Active / Under Repair / Condemned / Disposed | ✅ |
| Depreciation Method | SLM / WDV | ✅ |
| Useful Life | Years | ✅ |
| Current Book Value | Auto-calculated | System |
| AMC/CMC Contract | Reference | ❌ |
| Insurance | Policy details | ❌ |
| Calibration Required | Boolean | ❌ |
| Calibration Frequency | Months | Conditional |
| Last Calibration Date | Date | Conditional |
| Next Calibration Due | Auto-calculated | System |
| Photo | Image | ❌ |
| QR/Barcode | Auto-generated | System |

### 6.7.2 Asset Lifecycle

```
Procurement → Installation → Commissioning → Active Use → Maintenance/Repair → Condemnation → Disposal
```

### 6.7.3 AMC/CMC Management

| Feature | Description |
|---------|-------------|
| **Contract Tracking** | AMC/CMC start date, end date, vendor, coverage details |
| **Renewal Alerts** | Automated alerts 60/30/15 days before contract expiry |
| **Call Logging** | Log service calls against contract, track resolution time |
| **SLA Tracking** | Response time, resolution time vs. contractual SLA |
| **Cost Tracking** | AMC cost vs. breakdown maintenance cost analysis |
| **Preventive Maintenance** | Schedule PM activities per manufacturer recommendations |

---

## 6.8 Vendor Management

### 6.8.1 Vendor Master

| Field | Type | Mandatory |
|-------|------|-----------|
| Vendor Code | Auto-generated | System |
| Vendor Name | Text | ✅ |
| Vendor Type | Manufacturer / Distributor / Importer / Service Provider | ✅ |
| Contact Person | Text | ✅ |
| Phone | Phone | ✅ |
| Email | Email | ✅ |
| Address | Text | ✅ |
| GSTIN | Text | ✅ |
| PAN | Text | ✅ |
| Bank Details | IFSC, Account Number, Account Name | ✅ |
| Drug License (if pharma) | Text + Document | Conditional |
| FSSAI License (if food) | Text + Document | Conditional |
| Product Categories | Multi-select | ✅ |
| Payment Terms | Dropdown | ✅ |
| Credit Days | Number | ✅ |
| Credit Limit | Currency | ❌ |
| Rating | 1–5 Stars | System (auto-calculated) |
| Status | Active / Blacklisted / Inactive | ✅ |

### 6.8.2 Vendor Performance Scoring

| Parameter | Weight | Measurement |
|-----------|--------|-------------|
| **Delivery Timeliness** | 30% | % of POs delivered on/before due date |
| **Quality** | 30% | % of items accepted vs. received |
| **Price Competitiveness** | 20% | Comparative rate analysis |
| **Service & Support** | 10% | Response to complaints, after-sales |
| **Documentation** | 10% | Invoice accuracy, certificate availability |

Auto-calculated quarterly. Vendors below threshold → review for blacklisting.

---

## 6.9 CSSD (Central Sterile Supply Department)

### 6.9.1 CSSD Workflow

```
Used Instruments (from OT/Ward)
          │
     ┌────▼──────────┐
     │ Decontamination│ → Soak, wash, rinse
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Inspection     │ → Check functionality, cleanliness
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Packing        │ → Set assembly, wrapping, labeling
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Sterilization  │ → Autoclave / ETO / Plasma
     │ (with indicator│    Batch tracking, BI/CI logging
     │  tracking)     │
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Storage        │ → Sterile storage with expiry tracking
     └────┬──────────┘
          │
     ┌────▼──────────┐
     │ Issue to OT/   │ → FIFO issue with sterility verification
     │ Ward           │
     └──────────────┘
```

### 6.9.2 CSSD Tracking

| Tracked Item | Details |
|-------------|---------|
| **Instrument Sets** | Each set has a unique barcode, contents list, and usage history |
| **Sterilization Cycles** | Cycle number, load contents, temperature, pressure, duration, BI/CI results |
| **Recall Capability** | If a BI (Biological Indicator) fails, system identifies all items from that batch and the patients they were used on |

---

## 6.10 Inventory Reports & Analytics

### 6.10.1 Standard Reports

| Report | Description | Frequency |
|--------|-------------|-----------|
| **Stock Status** | Current stock by item, batch, expiry, location | Real-time |
| **Consumption Report** | Usage by department, item, period | Daily/Weekly/Monthly |
| **Expiry Report** | Items expiring in next 30/60/90/180 days | Weekly |
| **Reorder Report** | Items below reorder level | Daily |
| **Non-Moving Items** | Items with zero consumption for 90+ days | Monthly |
| **Purchase Analysis** | Spending by vendor, category, department | Monthly |
| **GRN Pending** | POs without GRN (deliveries overdue) | Weekly |
| **Vendor Performance** | Scorecard by vendor | Quarterly |
| **ABC/VED Matrix** | Cross-classification for priority management | Quarterly |
| **Stock Variance** | Physical count vs. system stock discrepancies | After physical audit |
| **Implant Usage** | Implants used by surgeon, procedure, vendor | Monthly |
| **Patient Consumption** | Consumables charged to specific patients | On-demand |
| **Cost per Patient** | Average consumable cost per patient per day by ward | Monthly |
| **Wastage Report** | Expired, damaged, written-off items with value | Monthly |

### 6.10.2 Inventory KPIs

| KPI | Formula | Target |
|-----|---------|--------|
| **Inventory Turnover Ratio** | Cost of Goods Consumed / Average Inventory | > 6× per year |
| **Stock-Out Rate** | (Days out of stock / Total days) × 100 | < 2% |
| **Wastage Rate** | (Value wasted / Value consumed) × 100 | < 1% |
| **Expiry Rate** | (Value expired / Value purchased) × 100 | < 0.5% |
| **GRN-PO Variance** | (GRN value – PO value) / PO value × 100 | < 2% |
| **Indent to PO Time** | Average days from indent approval to PO issue | < 3 days |
| **PO to Delivery Time** | Average days from PO to GRN | < lead time |
| **Physical Count Accuracy** | (Matched items / Total items) × 100 | > 95% |

---

## 6.11 Linen & Laundry Management

### 6.11.1 Linen Inventory

| Item Type | Examples |
|-----------|---------|
| **Bed Linen** | Bed sheets, pillow covers, mattress protectors, blankets |
| **Patient Wear** | Patient gowns, pajamas |
| **OT Linen** | OT gowns, drapes, towels |
| **Staff Uniforms** | Doctor coats, nurse uniforms, housekeeping uniforms |
| **Miscellaneous** | Curtains, mop cloths, dusters |

### 6.11.2 Linen Lifecycle Tracking

```
Fresh Linen (from Laundry/New Purchase)
         │
    ┌────▼──────┐
    │ Issue to   │ → Track which ward/department received
    │ Ward/Dept  │    (barcode or RFID on each piece)
    └────┬──────┘
         │
    ┌────▼──────┐
    │ In Use     │ → Assigned to patient bed / OT
    └────┬──────┘
         │
    ┌────▼──────┐
    │ Soiled     │ → Collected in soiled linen bags
    │ Collection │    (color-coded: regular, infected, OT)
    └────┬──────┘
         │
    ┌────▼──────┐
    │ Laundry    │ → Washing, drying, ironing, folding
    │ Processing │    Track cycle count per linen item
    └────┬──────┘
         │
    ┌────▼──────────┐
    │ Quality Check  │ → Inspect for damage, stains, tears
    │ (Condemn if bad)│
    └────┬──────────┘
         │
    ┌────▼──────┐
    │ Ready for  │ → Back to clean linen store
    │ Reissue    │
    └───────────┘
```

---

[→ Next: Operations Management](./07_Operations_Management.md)
