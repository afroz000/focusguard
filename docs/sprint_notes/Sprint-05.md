# Sprint 05 – Focus Session Management

---

# Sprint Objective

The objective of Sprint 05 is to introduce the core concept of a **Focus Session** into the FocusGuard backend.

A Focus Session represents a scheduled period during which a user intends to remain productive. Future browser extension functionality will communicate with the backend to determine whether a focus session is active and enforce website blocking rules accordingly.

This sprint establishes the backend foundation for that workflow by allowing authenticated users to create focus sessions and persist them in the database.

---

# Ticket Information

**Ticket ID:** FG-110

**Title:** Create Focus Session

**Priority:** High

**Status:** Completed

---

# User Story

As a logged-in user,

I want to create a focus session containing a name, start time and end time,

So that I can schedule a period during which distracting websites should be blocked.

---

# Acceptance Criteria

The feature is considered complete when:

- Only authenticated users can create focus sessions.
- Every focus session belongs to exactly one authenticated user.
- Anonymous users cannot access the endpoint.
- Session name is mandatory.
- Start time is mandatory.
- End time is mandatory.
- Both timestamps must represent future times.
- Newly created sessions must always start with status `SCHEDULED`.
- Creation timestamp must be generated automatically.
- Session owner must be determined from the authenticated JWT instead of accepting a userId in the request.
- Session must be persisted successfully.
- HTTP status must be **201 Created**.
- Response must contain the created session information.
- Sensitive user information must never be returned.
- Flyway migration must create the required database schema.
- Feature must compile successfully.
- Feature must be tested through Postman.
- Persisted data must be verified directly in MySQL.

---

# Functional Requirements

The endpoint should:

1. Accept an authenticated request.
2. Read the authenticated user's email from Spring Security.
3. Load the corresponding User entity.
4. Create a new FocusSession entity.
5. Associate the session with the authenticated user.
6. Set the initial status to `SCHEDULED`.
7. Persist the entity.
8. Return a DTO representing the newly created session.

---

# API

## Endpoint

```
POST /api/v1/sessions
```

---

## Authentication

```
Authorization: Bearer <JWT>
```

---

## Request

```json
{
    "name": "DSA Practice",
    "startTime": "2026-07-30T22:00:00",
    "endTime": "2026-07-30T22:50:00"
}
```

---

## Successful Response

```http
HTTP 201 Created
```

```json
{
    "id": 1,
    "name": "DSA Practice",
    "startTime": "2026-07-30T22:00:00",
    "endTime": "2026-07-30T22:50:00",
    "status": "SCHEDULED",
    "createdAt": "2026-07-30T21:49:09.264173"
}
```

---

# HTTP Status Codes

| Status | Meaning |
|---------|----------|
|201|Session created successfully|
|400|Validation failed|
|401|JWT missing or invalid|
|500|Unexpected server error|

---

# Database Changes

Created Flyway migration

```
V2__create_focus_sessions_table.sql
```

---

# Table

```
focus_sessions
```

---

## Columns

|Column|Type|Description|
|------|----|-----------|
|id|BIGINT|Primary Key|
|user_id|BIGINT|Owner of the session|
|name|VARCHAR(100)|Session name|
|start_time|DATETIME|Scheduled start|
|end_time|DATETIME|Scheduled end|
|status|VARCHAR(30)|Current lifecycle status|
|created_at|DATETIME|Creation timestamp|

---

## Constraints

Primary Key

```
id
```

Foreign Key

```
focus_sessions.user_id
        ->
users.id
```

Delete Rule

```
ON DELETE CASCADE
```

---

## Indexes

```
idx_focus_sessions_user_id
```

```
idx_focus_sessions_status
```

---

# Files Added

```
src/main/resources/db/migration/
    V2__create_focus_sessions_table.sql
```

```
src/main/java/com/afroz/focusguard/session/entity/

    FocusSession.java
    SessionStatus.java
```

```
src/main/java/com/afroz/focusguard/session/repository/

    FocusSessionRepository.java
```

```
src/main/java/com/afroz/focusguard/session/dto/

    CreateSessionRequest.java
    SessionResponse.java
```

```
src/main/java/com/afroz/focusguard/session/service/

    FocusSessionService.java
```

```
src/main/java/com/afroz/focusguard/session/controller/

    FocusSessionController.java
```

---

# Implementation Summary

## Entity

Created

```
FocusSession
```

Fields

- id
- user
- name
- startTime
- endTime
- status
- createdAt

---

## Enum

Created

```
SessionStatus
```

Values

- SCHEDULED
- ACTIVE
- COMPLETED
- CANCELLED

---

## Repository

Created

```
FocusSessionRepository
```

Extends

```
JpaRepository<FocusSession, Long>
```

---

## DTOs

Created

```
CreateSessionRequest
```

Created

```
SessionResponse
```

---

## Service

Created

```
FocusSessionService
```

Responsibilities

- obtain authenticated user
- create entity
- map request DTO
- save entity
- map response DTO

---

## Controller

Created

```
FocusSessionController
```

Endpoint

```
POST /api/v1/sessions
```

Returns

```
201 Created
```

---

# Validation

Implemented

```
@NotBlank
```

for

```
name
```

Implemented

```
@NotNull
```

for

```
startTime
```

Implemented

```
@NotNull
```

for

```
endTime
```

Implemented

```
@Future
```

for both timestamps.
# Authentication Flow

The endpoint is protected using JWT authentication.

When a request reaches the controller, Spring Security first validates the JWT supplied in the `Authorization` header.

If the token is valid, Spring creates an `Authentication` object and stores it inside the `SecurityContext`.

The service retrieves the authenticated user using:

```java
Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

String email = authentication.getName();
```

The email is then used to retrieve the corresponding `User` entity from the database.

```java
User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));
```

This guarantees that the session always belongs to the currently authenticated user.

The client never sends a `userId`.

This prevents malicious users from creating sessions on behalf of other users simply by changing an ID inside the request body.

---

# Request Lifecycle

```
Client
    │
    │ POST /api/v1/sessions
    │
    ▼
Spring Security
    │
    │ Validate JWT
    ▼
SecurityContext
    │
    ▼
FocusSessionController
    │
    ▼
FocusSessionService
    │
    ├── Get authenticated email
    ├── Load User
    ├── Create FocusSession
    ├── Save using Repository
    │
    ▼
FocusSessionRepository
    │
    ▼
MySQL
    │
    ▼
SessionResponse
    │
    ▼
Client
```

---

# Testing Performed

## Project Build

The project was rebuilt successfully.

```
Build completed successfully
```

---

## Endpoint Testing

Request sent through Postman.

```
POST /api/v1/sessions
```

Authentication

```
Bearer JWT Token
```

Result

```
HTTP 201 Created
```

The API returned the newly created session successfully.

---

## Database Verification

The following SQL was executed.

```sql
USE focusguard;

SELECT * FROM focus_sessions;
```

Verified values.

```
id = 1

user_id = 5

name = DSA Practice

start_time = 2026-07-30 22:00:00

end_time = 2026-07-30 22:50:00

status = SCHEDULED

created_at = 2026-07-30 21:49:09
```

The database values matched the API response exactly.

---

# Files Modified

```
V2__create_focus_sessions_table.sql

FocusSession.java

SessionStatus.java

FocusSessionRepository.java

CreateSessionRequest.java

SessionResponse.java

FocusSessionService.java

FocusSessionController.java

Sprint-05.md

FocusSessions.md

FocusSession_Architecture.md

InterviewNotes/FocusSessions.md
```

---

# Known Improvements

The current implementation satisfies the acceptance criteria.

However, the following improvements can be implemented in future tickets.

## Business Validation

Currently both timestamps are validated individually.

The application should additionally ensure

```
endTime > startTime
```

---

## Session Overlap Detection

A user should not be allowed to schedule overlapping focus sessions.

Example

```
Session A

7 PM
to
8 PM

Session B

7:30 PM
to
8:20 PM
```

The second request should be rejected.

---

## Session Duration Validation

Minimum duration should be configurable.

Maximum duration should also be configurable.

---

## Session Editing

Allow users to update scheduled sessions.

---

## Session Cancellation

Allow users to cancel scheduled sessions.

---

## Session History

Allow users to retrieve previous focus sessions.

---

## Active Session Detection

Allow the browser extension to query

```
/api/v1/sessions/active
```

to determine whether blocking should currently be enabled.

---

# Definition of Done

- [x] Feature branch created
- [x] Flyway migration created
- [x] Migration executed successfully
- [x] Database verified
- [x] Entity created
- [x] Enum created
- [x] Repository created
- [x] DTOs created
- [x] Service created
- [x] Controller created
- [x] JWT authentication integrated
- [x] Validation added
- [x] Endpoint tested
- [x] Database verified manually
- [x] Build passed
- [x] Sprint documentation completed

Remaining Git Tasks

- [ ] Git Status
- [ ] Commit
- [ ] Push
- [ ] Pull Request
- [ ] Review
- [ ] Merge into develop
- [ ] Delete Remote Branch
- [ ] Delete Local Branch
- [ ] Pull develop
- [ ] Verify clean working tree

---

# Sprint Outcome

Sprint 05 successfully introduced the first version of the Focus Session domain model into FocusGuard.

Users can now create authenticated focus sessions that are securely associated with their own accounts.

The feature establishes the foundation required for future work involving website blocking, browser extension synchronization, session activation, scheduling, analytics, and productivity tracking.

The implementation follows the same layered architecture established in previous sprints, consisting of Controller, Service, Repository, Entity, DTO, and Flyway migration layers. It also maintains the project's security model by deriving ownership from the authenticated JWT rather than trusting client-supplied identifiers.

FG-110 is complete and ready for source control, pull request creation, and merge into the `develop` branch.