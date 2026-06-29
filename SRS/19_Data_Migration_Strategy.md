# Legacy Data Migration & ETL Strategy (SRS 19)

## 1. Overview
Transitioning a hospital from a legacy HIS (or paper-based records) to OmniGrid-Health requires a robust Extract, Transform, Load (ETL) strategy. This document outlines the protocols to ensure zero data loss, maintain clinical continuity, and ensure regulatory compliance during the migration.

## 2. Migration Scope

### 2.1 In-Scope Data
- **Patient Demographics**: UHID, Name, Contact, Address, Insurance details.
- **Master Data**: Departments, Doctors, Wards, Beds, Services, Tariff lists.
- **Active Clinical Data**: Current IPD admissions, active prescriptions, pending lab orders.
- **Historical Clinical Data**: Past visit summaries, discharge notes, historical lab results (last 5 years).
- **Inventory**: Current stock levels, active batches, expiry dates.

### 2.2 Out-of-Scope Data
- Legacy system audit logs (to be archived locally, not migrated).
- PACS Images older than 10 years (to remain in deep archive).

## 3. ETL Process Architecture

### 3.1 Extract
- **Database Dumps**: Direct SQL exports from the legacy system (e.g., MySQL, Oracle) into flat CSV files or JSON dumps.
- **API Extraction**: If the legacy system has an API, build a Spring Batch reader to paginated-fetch records.

### 3.2 Transform
- **Data Cleansing**: Normalize phone numbers (e.g., appending +91), standardizing date formats to ISO 8601, and deduplicating patient records using exact string matching on Name + Phone + DOB.
- **Terminology Mapping**: 
  - Map legacy custom diagnosis codes to standard ICD-10.
  - Map legacy lab test names to LOINC codes.
  - Map legacy drug names to standard RxNorm or local pharmacopeia.
- **Entity Resolution**: Generating new UUIDs for the new system while maintaining a mapping table (`legacy_id` -> `new_uuid`) to preserve relational integrity.

### 3.3 Load
- **Spring Batch Processing**: Utilize Spring Batch for high-throughput, chunk-based writing to the PostgreSQL database.
- **Validation Gates**: Every chunk (e.g., 1000 records) is validated against JPA constraints. Failures are written to a `migration_dead_letter_queue` table for manual review.

## 4. Migration Execution Strategy

### 4.1 The "Big Bang" vs. Phased Approach
- **Recommendation**: Phased parallel run.
- **Phase 1 (Master Data)**: Migrate all static master data (Wards, Doctors, Tariffs) 1 month prior to go-live.
- **Phase 2 (Historical Data)**: Migrate historical patient records 1 week prior.
- **Phase 3 (Cut-over)**: Migrate active IPD patients and inventory stock levels during a low-traffic window (e.g., Sunday 2:00 AM).

### 4.2 Data Verification
- **Reconciliation Reports**: Automated scripts to compare row counts and financial totals between the legacy database and the new PostgreSQL database.
- **Clinical Sign-off**: Department heads manually verify a random sample of 50 patient records to ensure clinical data integrity before final sign-off.
