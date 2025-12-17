# Design Overview

## Overview
Interviewer defines weekly availability rules and a weekly cap. System generates discrete slots for the next two weeks. Candidates list open slots, book one, and may reschedule. Concurrency is handled with unique constraints and optimistic locking.

## API (v1)
- POST /api/v1/interviewers/{id}/availability-rules
- GET  /api/v1/interviewers/{id}/availability-rules
- PUT  /api/v1/interviewers/{id}/weekly-cap
- GET  /api/v1/interviewers/{id}/weekly-cap
- POST /api/v1/interviewers/{id}/slots
- GET  /api/v1/interviewers/{id}/slots?status=OPEN&cursor&limit&from&to
- POST /api/v1/bookings
- PATCH /api/v1/bookings/{id}/reschedule
- DELETE /api/v1/bookings/{id}
- GET  /api/v1/candidates/{id}/bookings

## Schema
See V1__init.sql for tables:
interviewer, candidate, availability_rule, weekly_cap, generated_slot, booking, weekly_counter, idempotency_key.

## Concurrency
- Unique index on confirmed booking per slot.
- Weekly counters per interviewer per ISO week; increment guarded by optimistic locking with limited retries.
- Booking flow in a transaction: check slot open, cap available, insert booking, increment counter.

## Pagination
Cursor based on `(start_at, id)` with ascending order and `nextCursor`.

## Errors
- 400 validation
- 404 not found
- 409 conflict (slot taken, cap exceeded, concurrent update)
- 422 business rule violation

## Trade-offs
- Pre-generated slots simplify availability queries but use storage; acceptable for two-week horizon.
- Cursor pagination avoids duplicates/gaps under concurrent updates vs limit/offset.
