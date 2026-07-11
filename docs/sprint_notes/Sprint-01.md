# Sprint 01 - Project Foundation

## Sprint Goal

Build the engineering foundation of the FocusGuard backend by setting up the project, establishing development conventions, configuring the database, and introducing database versioning with Flyway.

---

# Tickets Completed

| Ticket | Status | Description |
|---------|--------|-------------|
| FG-101 | ✅ | Initialize Spring Boot project |
| FG-102 | ✅ | Create feature-based package structure |
| FG-103 | ✅ | Build Health API |
| FG-106 | ✅ | Configure MySQL |
| FG-107 | ✅ | Configure Flyway and create the initial database migration |

---

# Features Delivered

- Spring Boot project initialized
- Feature-based package structure
- Health Check API
- Generic API Response Wrapper
- Spring Security configuration
- MySQL integration
- Dedicated database user (`focusguard_app`)
- Flyway integration
- Initial database migration
- `users` table created

---

# Git Workflow

## Branches

- `main`
- `develop`
- `feature/user-registration`

## Commits

- FG-101 Initialize Spring Boot project
- FG-101 Rename application class to `FocusGuardApplication`
- FG-103 Establish API foundation with Health API
- FG-106 Configure MySQL and add initial Flyway migration

---

# Engineering Decisions

## Why feature-based package structure?

Instead of grouping files by technical layer (controller, service, repository), we grouped them by business feature.

Example:

```
health/
user/
auth/
session/
```

This approach scales better as the project grows because everything related to one feature stays together.

---

## Why MySQL?

We chose MySQL because:

- Production-ready
- Excellent Spring Boot support
- Easy local setup
- Widely used in industry

---

## Why create a dedicated database user?

Instead of connecting the application using the MySQL `root` account, we created:

```
focusguard_app
```

Reason:

Applications should only have the permissions they actually require.

This follows the **Principle of Least Privilege**.

---

## Why Flyway?

Instead of allowing Hibernate to modify the schema automatically, we use Flyway to manage the database through version-controlled SQL migration files.

Benefits:

- Version-controlled schema
- Reproducible environments
- Easy collaboration
- Production-ready workflow

---

# Database

Database:

```
focusguard
```

Tables:

- users
- flyway_schema_history

---

# Concepts Learned

- Spring Boot project structure
- Feature branches
- MySQL datasource configuration
- Hikari Connection Pool
- Flyway migrations
- Database versioning
- Dedicated database users
- Principle of Least Privilege

---

# Interview Questions

## 1. Why should applications not connect using the MySQL root account?

### Answer

Applications should connect using a dedicated database user with only the permissions required by the application.

This limits the impact of security vulnerabilities and follows the Principle of Least Privilege.

---

## 2. Why is Flyway preferred over `spring.jpa.hibernate.ddl-auto=update`?

### Answer

Flyway manages database schema changes using version-controlled migration files.

This ensures every developer and every deployment uses exactly the same database schema.

`ddl-auto=update` changes the schema automatically at application startup, which is convenient during development but not recommended for production because changes are not tracked.

---

## 3. What is a database migration?

### Answer

A migration is a version-controlled SQL script that changes the database schema.

Example:

```
V1__create_users_table.sql
```

Each migration executes only once.

---

## 4. What is a feature branch?

### Answer

A feature branch is a Git branch dedicated to building one business feature.

Example:

```
feature/user-registration
```

The feature is developed and tested in isolation before being merged into the `develop` branch.

---

# Mistakes & Lessons Learned

## Mistake 1

Initially, only the `develop` branch existed.

### Lesson

A professional Git workflow should begin with both:

- `main`
- `develop`

before creating feature branches.

---

## Mistake 2

We initially connected to MySQL using the `root` account.

### Lesson

Applications should use a dedicated database user.

We created:

```
focusguard_app
```

and configured Spring Boot to use that account instead.

---

## Mistake 3

Some Java classes were accidentally created as packages.

### Lesson

IntelliJ allows both packages and Java classes to have similar names.

Always verify whether you're creating a **Package** or a **Java Class**.

---

## Mistake 4

We delayed creating the first feature branch until Sprint 2.

### Lesson

Every new business feature should begin on its own feature branch.

Example:

```
feature/user-registration
```

---

## Mistake 5

We started documentation after completing most of Sprint 1.

### Lesson

Documentation should evolve alongside the code.

Every ticket is only complete after:

- Code
- Testing
- Review
- Documentation

are all finished.

---

# Revision Checklist

Before starting Sprint 2, I should confidently explain:

- Spring Boot project structure
- Feature-based package structure
- Feature branches
- MySQL datasource configuration
- Dedicated database users
- Flyway migrations
- Database versioning
- Why Flyway is preferred over `ddl-auto=update`

---

# Sprint Retrospective

## What went well

- Successfully established the engineering foundation.
- Professional Git workflow introduced.
- MySQL and Flyway configured successfully.
- First database migration executed successfully.
- Health API working.
- Project documentation initiated.

---

## What can be improved

- Begin documentation from the first ticket.
- Create feature branches before implementation begins.
- Continue keeping commits small and focused.
- Follow the complete workflow for every ticket:
    - Design
    - Implementation
    - Testing
    - Code Review
    - Documentation
    - Commit

---

# Sprint Outcome

**Status:** ✅ Completed

The FocusGuard project now has a production-style engineering foundation and is ready to begin implementing business features in Sprint 2.