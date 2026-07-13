# Sprint 02 – User Registration

## Sprint Goal

Build the complete user registration feature with proper layering, validation, exception handling and secure password storage.

---

# Tickets Completed

| Ticket | Description | Status |
|---------|-------------|--------|
| FG-106 | Configure MySQL and Flyway | ✅ |
| FG-107 | Project Documentation Structure | ✅ |
| FG-108 | User Entity | ✅ |
| FG-109 | User Repository | ✅ |
| FG-110 | User Registration API | ✅ |
| FG-111 | Validation, Duplicate Email Check & BCrypt | ✅ |

---

# Features Implemented

- MySQL integration
- Flyway migration
- User entity
- User repository
- Registration endpoint
- Request DTO
- Response DTO
- Validation
- Global exception handling
- Duplicate email detection
- BCrypt password hashing
- End-to-end testing using Postman

---

# Architecture Decisions

## Response DTO

Originally, the API returned the `User` entity directly.

After testing with Postman, I noticed that the password was exposed in the API response.

To solve this, I introduced `RegisterUserResponse`.

This separates the persistence model from the API contract and prevents exposing sensitive fields.

---

## Dedicated Database User

Instead of connecting Spring Boot using the MySQL root account, a dedicated application user (`focusguard_app`) was created.

Benefits:

- Principle of least privilege
- Improved security
- Better production practice

---

## BCrypt

Passwords are now hashed before being stored.

The database stores:

```
$2a$10$...
```

instead of the original password.

---

# Problems Faced

## Problem 1

Returned Entity instead of DTO.

### Solution

Introduced `RegisterUserResponse`.

---

## Problem 2

Passwords stored in plain text.

### Solution

Configured `PasswordEncoder` and BCrypt.

---

## Problem 3

Validation exceptions returned HTTP 500.

### Solution

Added a dedicated `MethodArgumentNotValidException` handler returning HTTP 400.

---

## Problem 4

Duplicate email registrations returned a generic server error.

### Solution

Created `EmailAlreadyExistsException` and returned HTTP 409 Conflict.

---

# Mistakes Made

- Returned entities directly from the controller.
- Stored passwords in plain text initially.
- Used `RuntimeException` before introducing a custom exception.
- Initially connected to MySQL as `root` before switching to a dedicated application user.
- Focused on making the feature work first before improving API design.

---

# Lessons Learned

- DTOs protect the API contract.
- Validation should happen before business logic.
- Services should contain business logic.
- Repositories should only interact with the database.
- Passwords must always be hashed.
- Exception handling should be centralized.
- Proper HTTP status codes improve API quality.

---

# Interview Questions

- Why shouldn't entities be returned directly?
- Why DTO?
- Why BCrypt?
- Why 409 Conflict?
- Why 400 Bad Request?
- Why Service layer?
- Why Repository?
- How does JpaRepository work?
- What is Hibernate?
- What is ORM?

(Refer to the learning notes for detailed answers.)

---

# Retrospective

## What Went Well

- Feature-based package structure remained clean.
- Git history is organized.
- Every feature was tested using Postman.
- Documentation was maintained alongside development.

## What Can Be Improved

- Introduce unit tests in a future sprint.
- Improve commit granularity for larger features.
- Add integration tests before deployment.

---

# Tech Lead Feedback

Overall Rating: **9.5 / 10**

Strengths:

- Clean layered architecture.
- Good Git discipline.
- Security improvements made incrementally.
- Professional API responses.
- Good documentation.

Next Sprint Focus:

- Authentication
- JWT
- Login
- Authorization

---

# Sprint Summary

Sprint 2 successfully transformed FocusGuard from a basic Spring Boot application into a backend capable of registering users securely.

Major concepts learned:

- Spring Data JPA
- Hibernate
- DTOs
- Validation
- Exception Handling
- BCrypt
- Layered Architecture
- Professional API Design