> **TEMPLATE:** Copy this file, rename it, and fill in the placeholders. Delete this block in the copy.

# Cross-slice conventions

Rules that apply to every slice. Fill in from the legacy behavior and the approved decisions. Do not guess: mark unknowns as OPEN and ask.

## Money and decimals
- Type and scale: <e.g. BigDecimal, scale 2>
- Rounding mode and where it applies: <MODE>
- Currency handling: <RULE>

## Dates and times
- Storage type and time zone: <RULE>
- Business-day or cut-off rules: <RULE>

## Strings
- Trimming, case, and empty-string handling (Oracle treats empty string as NULL): <RULE>

## Ordering and pagination
- Default sort and tie-breaking: <RULE>
- Default page size and maximum: <RULE>

## Identifiers
- Human-readable ID formats and reset rules: <RULE>

## Error model
- Error body shape and codes: <LINK TO CONTRACTS>

## Status values and transitions
- Allowed statuses and transitions: <LINK TO SPEC>
