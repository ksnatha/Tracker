# Periodic Data Extract API — Design Document

## 1. Overview

An enterprise GET API that exposes reporting data to external client systems on a scheduled, periodic basis (frequency may change over time — currently expected multiple times a day, triggered via Autosys).

Data spans a **parent domain** and, over time, **multiple child domains** (each backed by its own dedicated reporting view). We're starting with the parent domain only; child domains will be added incrementally without reworking this design.

**Core principle:** separate *generating* the data from *serving* the data.

- **Generation** happens once per scheduled run (triggered by Autosys), producing a full snapshot for all domains together.
- **Serving** happens whenever a client calls the API — they're reading something already built, not triggering fresh computation.

---

## 2. Source Data

- Data originates from **reporting views**, refreshed every 4 hours from transaction data.
- Each domain (parent, domain1, domain2, ...) has its own dedicated view.
- Child domain views may have a foreign key relationship back to the parent view. This is a genuine DB-level constraint upstream — the extract job does not need to independently validate referential integrity.
- The extract job is scheduled **1 hour after** the reporting-data sync job, ensuring it always reads a fully-refreshed, stable view — no risk of reading mid-refresh.

---

## 3. Generation Model

- **One Autosys trigger, one job run, all domains extracted within it.**
- **All domains must succeed for the batch to be considered valid.** If any single domain fails, the entire batch fails — no partial batches are persisted or exposed to clients.
- Nothing is written to storage until every domain has been successfully extracted. On full success, the control record and all domain payloads are persisted together as one unit.
- On failure, no client-visible batch is created (a failed run may optionally be logged internally for ops visibility, without exposing any data).

This keeps the client-facing contract simple: **if a batch exists and is discoverable via the API, it is complete and trustworthy across all domains.**

---

## 4. Storage Model

Rather than writing to a filesystem or object store, batch data is stored directly in the database:

- **Control table** — one row per batch run (metadata, overall status, timestamps).
- **Child table** — one row per *domain* within a batch (the actual data payload as a blob, plus per-domain reconciliation fields).

This keeps generation atomic (control record + all domain blobs written in a single transaction) and avoids managing separate file storage infrastructure.

### 4.1 Control Table

| Column | Purpose |
|---|---|
| `batch_id` | Unique identifier for the run |
| `business_date` | The date this data represents |
| `scheduled_time` | The actual time this run was triggered for (no fixed AM/PM cycle — frequency-agnostic by design) |
| `generated_at` | Timestamp generation completed |
| `status` | `COMPLETE` or `FAILED` (batch-level — all-or-nothing) |
| `schema_version` | Overall batch schema version reference |

### 4.2 Child Table (one row per domain, per batch)

| Column | Purpose |
|---|---|
| `batch_id` | Links back to the control table row |
| `domain` | e.g. `parent`, `domain1`, `domain2` |
| `data_blob` | The JSON payload for this domain, this batch (optionally gzip-compressed) |
| `compression` | `NONE` / `GZIP` |
| `record_count` | Row count for this domain |
| `checksum` | Hash of this domain's data, for reconciliation |
| `schema_version` | Schema version for this specific domain (domains can evolve independently over time) |

Primary key: `(batch_id, domain)`.

**Note:** a retention/purge policy is required for the child table (e.g., 90 days), since data now lives in the DB rather than external storage and will otherwise grow indefinitely.

---

## 5. Manifest Design

The manifest is the client-facing header/trailer summary — enough for a client to know what's in a batch and to reconcile against it, without pulling the full data.

Since a batch spans multiple domains, the manifest contains a **per-domain breakdown** inside an overall batch summary.

### Sample: Batch Manifest

```json
{
  "batchId": "20260731-180000",
  "scheduledTime": "2026-07-31T18:00:00-05:00",
  "generatedAt": "2026-07-31T18:01:47-05:00",
  "status": "COMPLETE",
  "domains": [
    {
      "domain": "parent",
      "recordCount": 15340,
      "checksum": "a1b2c3d4e5f6...",
      "schemaVersion": "1.0"
    },
    {
      "domain": "domain1",
      "recordCount": 42110,
      "checksum": "f6e5d4c3b2a1...",
      "schemaVersion": "1.0"
    }
  ]
}
```

Since the batch is all-or-nothing, a manifest that exists always represents a fully complete batch across all domains — there is no `PARTIAL` state to handle on the client side.

Adding a new domain later is purely additive — a new entry appears in the `domains` array. Existing clients consuming only `parent` are unaffected.

---

## 6. API Endpoints

| Endpoint | Purpose |
|---|---|
| `GET /extracts?date=...` | Discover what batches ran on a given date — returns manifest summaries |
| `GET /extracts/{batchId}/manifest` | Manifest (all domains) for one specific batch |
| `GET /extracts/{batchId}/data/{domain}` | Data for one specific domain within a specific batch |
| `GET /extracts/{batchId}/data/all` | All domains for a specific batch, bundled (zip) |
| `GET /extracts/latest/manifest` | Manifest for the most recent complete batch — no batchId needed |
| `GET /extracts/latest/data/{domain}` | Data for one domain from the most recent complete batch |
| `GET /extracts/latest/data/all` | All domains from the most recent complete batch, bundled (zip) |

Most day-to-day consumers are expected to use the `latest` endpoints directly. The date-based discovery endpoint exists mainly for support/reconciliation lookups (e.g., "what did we send last Tuesday").

### 6.1 Sample: `GET /extracts?date=2026-07-31`

```json
{
  "date": "2026-07-31",
  "batches": [
    {
      "batchId": "20260731-060000",
      "scheduledTime": "2026-07-31T06:00:00-05:00",
      "status": "COMPLETE"
    },
    {
      "batchId": "20260731-180000",
      "scheduledTime": "2026-07-31T18:00:00-05:00",
      "status": "COMPLETE"
    }
  ]
}
```

### 6.2 Sample: `GET /extracts/{batchId}/data/{domain}` (e.g. `parent`)

```json
{
  "batchId": "20260731-180000",
  "domain": "parent",
  "recordCount": 15340,
  "records": [
    {
      "accountId": "A100234",
      "customerName": "Jane Doe",
      "loanAmount": 245000.00,
      "status": "PENDING_APPROVAL",
      "currency": "USD",
      "country": "US",
      "originationDate": "2024-03-12"
    }
  ]
}
```

### 6.3 `GET /extracts/{batchId}/data/all` (bundled)

Returns a **zip file** containing:
- `manifest.json`
- one file per domain (`parent.json`, `domain1.json`, ...)

Zip format is chosen over a single combined JSON so each domain remains independently structured and consumable, and to align with typical enterprise file-drop/ingestion patterns.

### 6.4 Reconciliation Flow (per domain)

1. Client fetches the manifest → gets expected `recordCount` and `checksum` per domain.
2. Client downloads data for a domain (individually, or via the `all` bundle).
3. Client independently counts records and recomputes the checksum for that domain.
4. Compares against the manifest values → confirms complete, uncorrupted transfer before loading.

---

## 7. Lookup / Reference Values — Convention

For any field backed by a lookup (status, category, currency, country, etc.):

- **Always send the code, never an internal surrogate ID and never the current display label.**
  - Example: `"status": "PENDING_APPROVAL"` — not `"status": 3"` and not `"status": "Pending Approval"`.
- **Enterprise/industry-standard values** (currency, country) use the standard code — ISO 4217 for currency, ISO 3166 for country.
- **Application-local values** (status, category, etc.) use the stable business mnemonic stored in the DB — the same code the application itself uses internally, not the human-readable label shown on the UI.
- **Why:** display labels can change over time (e.g., "Pending Approval" → "Awaiting Approval") without changing business meaning. Sending the label would silently break any client-side logic keyed off it. The code is the stable contract; the label is a presentation concern.
- Consumers that need current display labels should be pointed to a small **reference/lookup endpoint** (e.g. `GET /reference/status-codes`) rather than having labels embedded in every record of every extract.

---

## 7a. Alternate Option — Codeset DTO (Code + Value)

Instead of sending the code alone, local codeset fields (status, category, question/answer codes, etc.) can optionally be sent as a small object carrying both the code and its display value at the time of generation:

```json
"status": {
  "code": "PENDING_APPROVAL",
  "value": "Pending Approval"
}
```

- The **code remains the only field clients should use for logic/filtering/joins** — this doesn't change the convention in Section 7, it only adds the current display value alongside it for convenience.
- The `value` is a **point-in-time snapshot** — what the label was at the time this batch was generated — not a stable identifier. It may differ from the current live label shown on the UI if the label has since changed.
- Benefit: self-contained records — clients don't need a separate call to a reference/lookup endpoint just to display something meaningful.
- Trade-off: display labels re-enter the payload, and generation requires resolving each code to its current value at extraction time (joins against the local codeset table).
- If adopted, apply the **same `{ "code": "...", "value": "..." }` shape consistently** across every codeset field in every domain — do not mix shapes (e.g. `description`/`label` elsewhere).

This is an alternative to the reference-endpoint approach in Section 7, not a replacement — teams can choose either per field/domain as needed, but should stay consistent within a given field across batches.

---

## 8. Autosys Integration

- Autosys triggers the extraction job at the scheduled time(s) — no cron logic inside the application.
- Scheduled 1 hour after the upstream reporting-data sync job, to guarantee reads happen against a fully-refreshed view.
- The job exits with a success/failure code; Autosys uses this to gate any dependent downstream jobs.
- The extraction job and the always-on API service are **the same Spring Boot application**, started two different ways:
  - **Batch mode** (CLI, triggered by Autosys) — runs the extraction, persists the batch if and only if all domains succeed, then exits.
  - **Web mode** (long-running service) — serves the API endpoints to client systems.

---

## 9. Auth

All endpoints are secured via **JWT**, reusing the existing authentication setup already in place in other modules.

---

## 10. Key Design Principles (Summary)

- **All-or-nothing batches.** A batch is only ever visible to clients once every domain in it has succeeded. No partial data is ever exposed.
- **Frequency-agnostic.** Batches are identified by actual scheduled time, not a fixed cycle label — schedule changes don't require design changes.
- **Additive domain growth.** New domains are added as new entries in the manifest and new rows in the child table — never reshaping what already exists.
- **DB is the source of truth.** No filesystem dependency; control + child tables drive both the API and internal audit/history.
- **Codes over labels, codes over surrogate IDs.** Lookup values are shared as stable business codes; display labels are a separate, client-owned concern.
- **Reconciliation is per domain.** Each domain has its own count and checksum, allowing precise validation on the client side.
