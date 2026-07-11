# ADR-001: Use Feature-Based Package Structure

## Status

Accepted

---

## Date

2026-07-11

---

## Context

As FocusGuard grows, the number of controllers, services, repositories, entities and DTOs will increase.

We needed to decide how the project should be organized.

---

## Decision

We chose a **feature-based package structure**.

Example:

```
health/
auth/
user/
session/
browser/
```

instead of

```
controller/
service/
repository/
entity/
dto/
```

---

## Rationale

A feature-based structure:

- Keeps related code together.
- Makes navigation easier.
- Scales well as the application grows.
- Reduces package clutter.
- Makes refactoring simpler.

---

## Alternatives Considered

### Layer-Based Structure

```
controller/
service/
repository/
entity/
dto/
```

Advantages:

- Easy for beginners.
- Common in tutorials.

Disadvantages:

- Files for one feature are scattered across many packages.
- Harder to navigate in large applications.
- Becomes difficult to maintain as the project grows.

---

## Consequences

### Positive

- Better scalability.
- Easier maintenance.
- Better organization.
- Matches many modern Spring Boot projects.

### Negative

- Slightly unfamiliar for beginners.
- Requires thinking in terms of business features instead of technical layers.

---

## Decision Owner

FocusGuard Engineering Team