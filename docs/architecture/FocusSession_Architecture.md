# Focus Session Architecture

---

# Overview

The Focus Session feature introduces the core domain object of the FocusGuard application.

Its primary responsibility is to allow authenticated users to create and manage scheduled focus periods.

This document explains how the feature fits into the overall backend architecture, how requests flow through the system, the responsibilities of each layer, and the reasoning behind the design decisions.

---

# High-Level Architecture

```
                        Browser Extension
                               │
                               │ HTTPS
                               ▼
                    Spring Boot REST API
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
        ▼                      ▼                      ▼
 Authentication         Session Module        Future Modules
       │                      │
       ▼                      ▼
  Spring Security      FocusSessionController
                               │
                               ▼
                     FocusSessionService
                               │
              ┌────────────────┴───────────────┐
              │                                │
              ▼                                ▼
      UserRepository               FocusSessionRepository
              │                                │
              └──────────────┬─────────────────┘
                             ▼
                          MySQL Database
```

---

# Layered Architecture

The project follows a layered architecture.

Each layer has one clearly defined responsibility.

```
Controller
        │
        ▼
Service
        │
        ▼
Repository
        │
        ▼
Database
```

This separation keeps the project modular and maintainable.

---

# Controller Layer

Class

```
FocusSessionController
```

Responsibilities

- Expose REST endpoint
- Accept HTTP requests
- Validate request DTO
- Call service
- Return HTTP response

The controller contains **no business logic**.

Example

```
Client

↓

POST /api/v1/sessions

↓

Controller

↓

Service
```

---

# Service Layer

Class

```
FocusSessionService
```

Responsibilities

- Retrieve authenticated user
- Load User entity
- Create FocusSession
- Apply business rules
- Save entity
- Convert Entity to Response DTO

The service acts as the application's business layer.

Future business rules such as overlapping session detection, duration limits, or scheduling constraints will also belong here.

---

# Repository Layer

Repositories

```
UserRepository

FocusSessionRepository
```

Responsibilities

- Perform database operations
- Hide SQL from higher layers
- Provide CRUD operations through Spring Data JPA

By extending `JpaRepository`, common operations such as save, findById, delete, and findAll are inherited automatically.

---

# Entity Layer

Entities represent database tables.

```
User
```

↓

maps to

↓

```
users
```

---

```
FocusSession
```

↓

maps to

↓

```
focus_sessions
```

Hibernate performs this mapping automatically.

---

# DTO Layer

DTOs separate the REST API from the persistence model.

Request Flow

```
JSON

↓

CreateSessionRequest

↓

FocusSession
```

Response Flow

```
FocusSession

↓

SessionResponse

↓

JSON
```

Benefits

- Prevents exposing internal fields
- Stable API contract
- Easier validation
- Cleaner architecture

---

# Authentication Flow

Every request passes through Spring Security before reaching the controller.

```
HTTP Request

↓

JWT Filter

↓

Validate Token

↓

Load User Details

↓

Create Authentication Object

↓

SecurityContextHolder

↓

Controller
```

If authentication fails

↓

```
401 Unauthorized
```

is returned immediately.

The controller is never executed.

---

# Request Lifecycle

The following sequence describes the complete request flow.

```
Client

│

│ POST /api/v1/sessions

▼

Spring Security

│

│ Validate JWT

▼

SecurityContextHolder

│

▼

FocusSessionController

│

▼

FocusSessionService

│

├── Read authenticated email

├── Load User

├── Create FocusSession

├── Set status = SCHEDULED

├── Save

│

▼

FocusSessionRepository

│

▼

Hibernate

│

▼

MySQL

│

▼

FocusSessionRepository

│

▼

FocusSessionService

│

▼

SessionResponse

│

▼

Controller

│

▼

HTTP 201 Created
```

---

# Database Relationship

```
users

+----------------+
| id             |
| first_name     |
| last_name      |
| email          |
| password       |
+----------------+
        │
        │
        │ One
        │
        ▼
Many
        │
        ▼

focus_sessions

+----------------+
| id             |
| user_id (FK)   |
| name           |
| start_time     |
| end_time       |
| status         |
| created_at     |
+----------------+
```

A single user can own multiple focus sessions.

Each session belongs to exactly one user.

---

# Component Responsibilities

| Component | Responsibility |
|-----------|----------------|
| Controller | HTTP communication |
| DTO | Request/Response contract |
| Service | Business logic |
| Repository | Persistence |
| Entity | Database mapping |
| Hibernate | ORM |
| Flyway | Schema versioning |
| Spring Security | Authentication |
| MySQL | Data storage |

---

# Why This Design?

The architecture intentionally separates concerns.

Instead of allowing every class to perform every task, each layer performs one job well.

Benefits

- Easier testing
- Easier debugging
- Easier maintenance
- Better scalability
- Cleaner codebase
- Improved readability

---

# Why Business Logic Belongs in the Service

The controller should never contain business rules.

Incorrect

```
Controller

↓

Validate

↓

Create Entity

↓

Repository.save()

↓

Business Rules
```

Correct

```
Controller

↓

Service

↓

Business Logic

↓

Repository
```

This keeps controllers lightweight and allows business logic to be reused by other APIs.

---

# Why the Session Owns the User Reference

Instead of storing only the user ID as a primitive value, the entity stores

```java
private User user;
```

Benefits

- Natural object-oriented design
- Hibernate manages relationships
- Easy navigation between entities
- Referential integrity

---

# Scalability Considerations

The current implementation supports future growth.

Possible future enhancements include

- Session overlap detection
- Session editing
- Session cancellation
- Active session lookup
- Browser extension synchronization
- Notifications
- Productivity analytics
- Recurring sessions
- Pagination
- Search and filtering

The existing architecture supports these additions without major refactoring.

---

# Potential Future Architecture

```
Browser Extension

↓

API Gateway

↓

Authentication Service

↓

Focus Session Service

↓

Notification Service

↓

Analytics Service

↓

Redis Cache

↓

MySQL
```

As the application grows, responsibilities can be split into separate services without changing the external API significantly.

---

# Key Design Principles Followed

- Layered Architecture
- Separation of Concerns
- Single Responsibility Principle (SRP)
- RESTful API Design
- DTO Pattern
- Repository Pattern
- ORM using Hibernate
- Database Normalization
- Secure Authentication using JWT
- Schema Versioning using Flyway

---

# Architecture Summary

The Focus Session module establishes the foundation for the rest of the FocusGuard application.

The design emphasizes clean separation of responsibilities, secure ownership through JWT authentication, and maintainable database modeling. By following established Spring Boot patterns—Controller, Service, Repository, Entity, and DTO—the implementation remains easy to extend with future capabilities such as website blocking, recurring sessions, analytics, and browser extension integration while keeping the codebase modular and easy to maintain.