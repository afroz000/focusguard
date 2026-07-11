# ADR-002: Use MySQL with Flyway for Database Management

## Status

Accepted

---

## Date

2026-07-11

---

## Context

FocusGuard requires a persistent relational database to store users, focus sessions, blocked websites, and future application data.

We also needed a reliable way to manage database schema changes as the project evolves.

---

## Decision

- Use **MySQL** as the relational database.
- Use **Flyway** for database versioning and schema migrations.
- Connect to the database using a dedicated application user (`focusguard_app`) instead of the MySQL `root` account.

---

## Rationale

### Why MySQL?

- Production-ready
- Excellent Spring Boot support
- Easy local development
- Widely used in industry

### Why Flyway?

- Version-controlled database schema
- Repeatable deployments
- Consistent database state across environments
- Production-friendly migration strategy

### Why a dedicated database user?

Using a dedicated application user follows the **Principle of Least Privilege**.

The application only receives the permissions it requires and does not use the powerful `root` account.

---

## Alternatives Considered

### PostgreSQL

Advantages

- Powerful
- Rich feature set
- Excellent production database

Reason not chosen

- MySQL was already installed and sufficient for this project.

---

### Hibernate `ddl-auto=update`

Advantages

- Very easy to use during development.
- Automatically updates the schema.

Reason not chosen

- Schema changes are not version-controlled.
- Not suitable for production environments.
- Difficult to audit database changes.

---

## Consequences

### Positive

- Production-style database management.
- Safer schema evolution.
- Easier collaboration.
- Better interview discussion topics.

### Negative

- Requires writing migration files.
- Slightly more setup compared to `ddl-auto=update`.

---

## Decision Owner

FocusGuard Engineering Team