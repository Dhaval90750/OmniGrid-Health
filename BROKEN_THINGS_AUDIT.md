# MedCore HIS — "What's Broken" Audit

Full-codebase + SRS audit. Every file in `codebase/` (backend, frontend, mobile, db, docker) and every SRS doc was read. This lists **concrete defects** — bugs, security holes, broken wiring, schema risks, and SRS contradictions — grouped by severity.

Date: 2026-06-22

---

## 🔴 CRITICAL — fix before anything else

### Security / secrets
1. **Production DB password committed to git.** `codebase/backend/src/main/resources/application.yml:6-8` contains live Supabase host, user, and password (`fzk#k92a/GsENV7`) in plaintext. It is tracked (not gitignored). **Rotate the password now**, move to env vars, and scrub git history.
2. **Hardcoded default JWT signing secret.** `security/JwtUtils.java:18` — `@Value("${...:MedcoreSuperSecretKey...}")`. If the env var is unset, every token is signed with a public, source-visible key → anyone can forge admin tokens. Full auth bypass.
3. **Public self-signup with privilege escalation.** `controller/AuthController.java:95-118` + `security/SecurityConfig.java:57` — `/api/v1/auth/**` is `permitAll()`, and `/signup` takes a free-text `role` field (`dto/SignupRequest.java`) that creates/assigns any role including `ROLE_ADMIN`. Anyone on the internet can mint an admin account.
4. **Seeded admin with weak credentials.** `config/DatabaseSeeder.java:46` seeds `admin / admin123` unconditionally, even in prod.

### Build / deploy will fail
5. **Flyway disabled while `ddl-auto: validate`.** `application.yml:13,21` — migrations never run, so a fresh DB has no schema and the app aborts on startup. The schema only exists because someone applied `supabase_migration.sql` out-of-band. (README claims Flyway runs automatically — it does not.)
6. **`invoice_items.updated_at` / `payments.updated_at` missing from their own migration.** `db/migration/V4__init_billing_schema.sql` omits `updated_at`; both entities extend `BaseEntity` (needs `updated_at NOT NULL`). The fix migration `V5__add_base_columns.sql` is a **no-op because Flyway is off** → `validate` aborts on the billing tables if the live DB lacks those columns.
7. **Mobile app cannot build.** `codebase/mobile/` has **no Gradle wrapper** (`gradlew`/`gradle-wrapper.properties`) and **no `res/` directory** — `AndroidManifest.xml:11-19` references `@mipmap/ic_launcher`, `@string/app_name`, `@style/Theme.MedCoreHIS` that don't exist. Guaranteed build failure.

### Clinical/data-loss bugs (frontend)
8. **Doctor visit workspace saves nothing.** `frontend/src/app/doctor/visit/[id]/page.tsx:33-36` — clinical notes, diagnoses, prescriptions are collected then thrown away (`alert("Saved!")`, no API call). Data loss.
9. **Wrong-patient safety risk.** Same file `:42-55` — patient identity, UHID, and **allergy (Penicillin)** are hardcoded ("Rahul Sharma 34M") regardless of the `[id]` route. A doctor sees a fake patient's allergies.

### SRS-level
10. **Compliance claimed but never specified.** HIPAA/DISHA/ABDM/ISO 27001/IEC 62304 appear in headline tables, but consent management, breach notification, FHIR interop, and data anonymization have **no functional requirements or build tasks**. For a healthcare system this is the most serious spec gap.
11. **ABDM consent management entirely missing** despite being legally mandated for the claimed ABDM integration.

---

## 🟠 HIGH — major breakage / security

### Backend security & access control
- **`@PreAuthorize` commented out** on `PatientController.java:29,36,42` (and others) — any authenticated user of any role can read/register/search patients (PHI).
- **IDOR / PHI leak:** `VisitController.java:25-28` trusts `doctorId` from the URL path (code comment admits it should come from the JWT) — any user can read any doctor's visits.
- **Actuator wide open:** `SecurityConfig.java:58` — `/actuator/**` is `permitAll()`; wildcard exposes future actuator endpoints unauthenticated.
- **Wildcard CORS** (`@CrossOrigin(origins="*")`) on every controller, combined with `Authorization` headers.
- **JWT validation weak:** `JwtUtils.java:45` uses `.parse()` instead of `parseClaimsJws()` — doesn't assert the token is a signed JWS.

### Backend broken business logic (core features are stubs)
- **Bed allocation is fake:** `service/AdmissionService.java:28-37` never fetches/validates a bed or marks it OCCUPIED (logic only in comments) → double-booking possible.
- **Pharmacy stock never depletes:** `PharmacyController.java:34` always calls `dispensePrescription(record, List.of())` with an empty deductions list → dispensing never reduces inventory; `StockMovement` never persisted; negative stock allowed.
- **Radiology report never saved:** `service/RadiologyService.java:37-42` mutates order status but never persists the report (`"Logic to persist report would go here"`); no controller even calls it; NPE on null order.
- **UHID race condition:** `service/PatientService.java:60-64` generates UHID from `count()+1` (non-atomic) → duplicate UHIDs / unique-constraint violations under concurrency.
- **Analytics dashboard is hardcoded mock:** `controller/AnalyticsController.java:17-55` returns fake revenue/census/KPIs.
- **Discharge doesn't free the bed / close admission:** `service/DischargeService.java:27-30`.

### Frontend broken wiring
- **Four pages bypass the auth'd API client** and hit `http://localhost:8080/...` via bare `fetch` with no JWT header: `scribe/page.tsx:36`, `staff/page.tsx:15`, `auxiliary/page.tsx:15`, `analytics/page.tsx:15`. They will 401 against a secured backend and fail in any non-local env.
- **401 redirect loop:** `lib/api.ts:34-35` redirects to `/login` on 401 but only clears `localStorage`, not the `omnigrid_token` cookie; `middleware.ts:6,15` trusts that stale cookie → bounces the user back. User can get stuck.
- **Tri-store token desync + XSS exposure:** `store/useAuthStore.ts` keeps the JWT in localStorage *and* a non-HttpOnly, non-Secure cookie *and* zustand-persist — three sources of truth, all XSS-readable.
- **Register payload mismatch:** `register/page.tsx:37` sends single `role` string; backend signup/role conventions (`ROLE_` prefix, `roles` set) likely reject it → 400 (matches the "role prefix doubling" commit history).
- **Crash on missing name:** `patients/[id]/page.tsx:56` — `patient.firstName[0]` throws if name is null/empty.
- **Silent search failure:** `patients/page.tsx:22` calls `/patients/search?q=` on mount; on 404 it only `console.log`s → table shows "No patients found" forever, no error UI.
- **Stub clinical/financial actions presented as working:** `admissions/new/page.tsx` (admit = alert only), `billing/invoice/[id]/page.tsx` (payment = alert only, ignores `[id]`), `discharge/page.tsx` (discharge/mortality = local mock), plus lab/radiology/pharmacy/nursing/icu/ot/inventory/operations pages — every action is an `alert()` with no persistence.
- **Unguarded API shape access** crashes pages: `scribe/page.tsx:41` (no `response.ok` check before reading `aiResult.vitals.blood_pressure`), `analytics/page.tsx:52,98` (nested `data.executive_overview.*` with no shape guard).

### Config / infra
- **Backend ignores the docker-compose Postgres** and points at remote Supabase (`application.yml:6`); following the README runs local dev **against production**.
- **docker-compose defines no app services** — only postgres/redis/elasticsearch — so it can't run the stack; backend `Dockerfile` is unreferenced.
- **README is wrong:** claims "Flyway will automatically execute V1–V18" and that local setup uses the docker Postgres — both false.
- **`@EnableJpaAuditing` dependency:** `BaseEntity.java:24-30` `created_at`/`updated_at` are `nullable=false` and rely on auditing being enabled; if it isn't wired, every insert fails. (Verify it's actually enabled.)

### SRS-level (HIGH)
- **AI scheduling self-contradiction:** doc 8 marks 4 AI features "Phase 1 Critical," but docs 11/13/14 build all AI in Phases 4–5 with **0 AI/ML staff in Phase 1** and only **11 total AI person-months** — yet the Phase-1 drug-interaction engine depends on it.
- **QR codes embed plaintext PHI** (name, DOB, blood group, allergy flag) — `SRS/03 §3.2.3` — directly contradicting the SRS's own encryption/privacy requirements.
- **HL7/FHIR interoperability** claimed pervasively but only one Phase-5 task exists; inbound HL7 v2.x parsing for analyzers/monitors has no requirement.
- **Estimates don't reconcile:** 248 person-months vs 24 months vs 14–18 FTE; "no SPOF + DR auto-failover" vs a single-site BOM (single NAS/UPS/backup server).
- **TOC missing docs 13 & 14**, and "500+ tasks" includes whole subsystems (ABAC, SSO/LDAP, smart-card/biometric, TimescaleDB, GraphQL/gRPC, consent, breach notification) with **zero tasks**.

---

## 🟡 MEDIUM — should fix

### Backend
- Raw `@RequestBody Entity` with no `@Valid`/DTO across most controllers (`AdmissionController.java:25`, IPD billing, claims, etc.) → mass-assignment (client can set id/status/dates).
- NPE risks on null collections/fields: `LabService.java:33` (samples), `PrescriptionService.java:32,37` (lines, durationDays), `OtService.java:39` (booking), `BillingService.java:34` (payment amount unvalidated — null/negative accepted).
- Wrong HTTP status: `PatientService.java:74` throws raw `RuntimeException` → 500 instead of 404. No global `@ControllerAdvice`.
- Unbounded `findAll()` / search (no pagination) across Patient, Inventory, Operations, Staff, IpdBilling, Auxiliary.
- `AiService.java` is keyword-matching mock returning fake `confidence_score: 0.92`.
- `BillingService.java:30` receipt number from `currentTimeMillis()` (collisions); payment persistence relies on cascade, returned id may be unset.
- Migration hazard: V4 and V14 share the description `init_billing_schema` (checksum/rename risk if Flyway re-enabled).
- `supabase_migration.sql` is a second source of truth with no `flyway_schema_history` and no `IF NOT EXISTS` — re-enabling Flyway will fail on "already exists"; seed INSERTs aren't idempotent (`ON CONFLICT` missing).

### Frontend
- Hardcoded API base-URL fallback `http://localhost:8080/api/v1` (`lib/api.ts:4`) breaks any deploy without `NEXT_PUBLIC_API_URL`.
- `middleware.ts:23` matcher doesn't exclude `public/` assets → `/logo.png` gets redirected to `/login` when unauthenticated.
- Cookie set without `Secure` flag (`useAuthStore.ts:29`).
- Currency mix: `$` vs `₹` in `billing/page.tsx`; unformatted raw integers (`₹150000`).
- Hardcoded "System Status / Welcome Dr. Smith / Quick Stats" mock data on home & dashboards presented as live.
- Login endpoint name risk (`/auth/login` vs Spring's `/auth/signin`); patient-create redirect assumes `response.data.id` exists.
- ICU GCS scoring is clinically nonstandard (`icu/page.tsx:243` — intubated modeled as `val:0`, total = `eye+1+motor`).
- Uncontrolled `defaultValue` dispense inputs never read on submit (`pharmacy/page.tsx:142`).

### Mobile / infra
- Mobile: deprecated manifest `package` attribute with AGP 8.2 namespace; JDK 1.8 target with Compose 1.5.4 (expects JDK 17); CAMERA/RECORD_AUDIO permissions declared but never requested; no HTTP client / no cleartext config → no backend connectivity; MainActivity is a TODO stub.
- docker-compose: no healthchecks (README tells you to manually "wait 30 seconds"); hardcoded creds; obsolete `version:` key.

### SRS (MEDIUM, selection)
- Counting inconsistencies everywhere: doctor types 20 vs 21, modules 27+ vs 30+, tables 200–260 vs schema sums, lab tests 90 vs 100+ vs 500+, screen tallies that don't sum to 350.
- Architecture undecided: doc 10 specifies WebFlux **and** MVC (reactive+blocking with JPA — anti-pattern), REST+GraphQL+gRPC+HATEOAS with only REST tasked, TimescaleDB orphaned.
- Privilege matrices don't cover all enumerated roles (doc 2 §2.3 12 cols vs 14 roles; doc 12 §12.4.2 15 cols vs 21 doctor types → undefined privileges).
- Retention policy stated 3+ ways (7yr / 10yr / indefinite); RPO < 5 min contradicted by hourly-snapshot backup; 99.95%+ uptime / "no SPOF" contradicted by single-site BOM.
- Session-timeout policy inconsistent per-role vs consolidated table.

---

## 🟢 LOW — cleanup / polish

- `JwtUtils.java:21` `jwtExpirationMs` is `int` (overflows > ~24.8 days); no refresh-token mechanism.
- SLF4J bug: `JwtAuthenticationFilter.java:45` passes exception as `{}` arg → stack trace not logged.
- No `equals()/hashCode()` on entities; `show-sql: true` in committed config.
- `BillItem` off-pattern (no `BaseEntity`, no `updated_at`) — consistent with its table but inconsistent with the model.
- BigDecimal precision/scale not declared on entities vs pinned `DECIMAL(n,2)` in DDL (matters if you switch off `validate`).
- `PatientRepository.java:22` `:keyword` param without `@Param` (relies on `-parameters` compile flag).
- Dockerfile runs JVM as root, no non-root `USER`.
- Frontend: many dead buttons (no `onClick`), `catch (err: any)` everywhere, unused imports/state, typo "Sign & Finalize Encouter", `globals.css` Arial overrides the loaded Inter font, logout is a `<div onClick>` (a11y).
- Two overlapping `.gitignore` files; infra ports bound to host (conflicts); release build `isMinifyEnabled = false`.
- SRS: dead term "COAS" defined but never used; gender enum mismatch (Transgender vs Other); sample roster published in a state that violates its own stated rules.

---

## Bottom line

The repo is best described as a **broad UI/scaffold prototype, not a working system**:

- **~13 of ~24 frontend pages are pure mock/alert stubs** — clinical and financial actions silently do nothing.
- **The 3 most important backend workflows are non-functional stubs:** bed allocation, pharmacy stock depletion, radiology report persistence.
- **It won't deploy cleanly** anywhere but the one hand-seeded Supabase DB (Flyway off + `validate`), and the **mobile module doesn't build at all**.
- **Multiple CRITICAL security holes:** committed prod DB password, default JWT secret, open self-signup → admin, seeded admin/admin123.
- **The SRS itself is internally inconsistent** on scope, schedule, staffing, and — most seriously — claims healthcare compliance (HIPAA/ABDM/consent) that has no requirements or tasks behind it.

Suggested first wave: rotate the DB password + remove it from git → fix JWT secret/signup/admin-seed → decide Flyway-vs-validate and make a fresh DB bootable → then start replacing the frontend stubs and backend stub workflows with real wiring.
