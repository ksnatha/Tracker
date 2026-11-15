> **FILL-IN INSTRUCTIONS (delete this block when done):** This file has one-time placeholders written like `<THIS>`. Search the file for `<` and replace each with your real value. Items marked [CONFIRM] are assumptions to check. Do not start a Devin session that relies on this file until no placeholders remain.

# Database naming conventions

**Status: PROVISIONAL.** These are placeholders until the DBAs confirm the real conventions. Anything named under them must be marked provisional in the PR. Keep names generated from one place so renaming is mechanical.

## Things to confirm with the DBAs

- Table, column, constraint, index and sequence naming patterns, prefixes and suffixes: `<CONFIRM>`
- Maximum identifier length: `<CONFIRM, e.g. 30 or 128>`
- Schema per service, tablespaces, and who owns each schema: `<CONFIRM>`
- Standard audit columns (created and modified user and time, version column): `<CONFIRM>`
- Rules for CLOB and JSON columns, partitioning and retention: `<CONFIRM>`
- Oracle version and `COMPATIBLE` setting: `<CONFIRM, e.g. 21 and the compatible value>`

## Provisional defaults (used until confirmed)

- Uppercase unquoted identifiers in snake_case
- Names short enough to stay under 30 characters
- Primary key: `<TABLE>_PK`; foreign key: `<TABLE>_<REF>_FK`; index: `<TABLE>_<COLUMNS>_IX`; sequence: `<TABLE>_SEQ`
- Audit columns: `CREATED_AT`, `CREATED_BY`, `UPDATED_AT`, `UPDATED_BY`, `VERSION_NO`

## Confirmed on

`<DATE AND DBA NAME, or "not yet confirmed">`
