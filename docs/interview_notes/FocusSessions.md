# Focus Sessions - Interview Notes

---

# FG-110 Interview Questions

---

# Question 1

## Explain the architecture used for the Create Focus Session feature.

### Answer

The feature follows the standard Spring Boot layered architecture.

```
Client
    │
    ▼
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

Each layer has a single responsibility.

**Controller**

- Receives HTTP request
- Validates DTO
- Calls Service
- Returns HTTP response

**Service**

- Contains business logic
- Gets authenticated user
- Creates session
- Maps DTOs
- Saves entity

**Repository**

- Performs database operations
- Extends JpaRepository

---

### Follow-up

Why shouldn't the controller contain business logic?

### Answer

Controllers should remain thin.

Business logic belongs inside the Service layer because:

- Easier testing
- Better maintainability
- Reusable logic
- Separation of concerns

---

# Question 2

## Why did you create a separate FocusSession entity?

### Answer

One user can create many focus sessions.

Storing session information inside the User table would violate database normalization.

Instead we created

```
User

1

↓

Many

↓

FocusSession
```

This allows unlimited sessions per user.

---

### Follow-up

What relationship exists between User and FocusSession?

### Answer

```
One User

↓

Many FocusSessions
```

JPA annotation

```java
@ManyToOne
private User user;
```

---

# Question 3

## Why did you use @ManyToOne?

### Answer

Each session belongs to exactly one user.

One user may own multiple sessions.

Therefore

```
Many Sessions

↓

One User
```

If we used

```java
@OneToOne
```

a user could only have one session.

---

### Follow-up

When would you use OneToOne?

### Answer

Examples

- User ↔ Passport
- User ↔ Aadhaar
- Employee ↔ Locker

---

# Question 4

## Explain FetchType.LAZY.

### Answer

LAZY delays loading related entities until they are actually required.

Initially

```
Session
```

is loaded.

Only when

```java
session.getUser()
```

is called does Hibernate retrieve the User.

Benefits

- Less memory
- Faster queries
- Better scalability

---

### Follow-up

When might EAGER loading be appropriate?

### Answer

When every query always requires the related object.

Even then, EAGER should be chosen carefully because it increases database work.

---

# Question 5

## Why use DTOs?

### Answer

DTOs separate the API from the database model.

Benefits

- Hide internal fields
- Prevent password leakage
- API remains stable
- Easier validation
- Loose coupling

---

### Follow-up

What problems occur if entities are returned directly?

### Answer

Possible problems

- Password exposure
- Infinite JSON recursion
- API tightly coupled to database
- Difficult future refactoring

---

# Question 6

## Why not accept userId from the client?

### Answer

Because it is insecure.

Suppose

```json
{
    "userId": 8
}
```

A malicious user can change it to

```json
{
    "userId": 2
}
```

and create sessions for another account.

Instead we determine ownership from the authenticated JWT.

---

### Follow-up

How is the authenticated user obtained?

### Answer

```java
Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String email = authentication.getName();
```

---

# Question 7

## Why use SecurityContextHolder?

### Answer

After JWT authentication succeeds, Spring Security stores the authenticated user in the SecurityContext.

The service retrieves the currently logged-in user from there.

This prevents trusting client input.

---

# Question 8

## Why use @CreationTimestamp?

### Answer

Hibernate automatically populates the creation timestamp during insertion.

Benefits

- No duplicate code
- Consistent timestamps
- Developers cannot forget to populate it

---

### Follow-up

Could this also be implemented in SQL?

### Answer

Yes.

Using

```sql
DEFAULT CURRENT_TIMESTAMP
```

Both approaches are valid.

---

# Question 9

## Why use Enum instead of String?

### Answer

Strings allow invalid values.

Example

```
scheduled

Schedule

Scheduld
```

Enums restrict values to predefined constants.

This improves type safety.

---

### Follow-up

Why EnumType.STRING instead of ORDINAL?

### Answer

ORDINAL stores

```
0

1

2
```

If enum order changes, existing data becomes invalid.

STRING stores

```
SCHEDULED

ACTIVE

COMPLETED
```

which remains stable.

---

# Question 10

## Why add indexes?

### Answer

Frequently searched columns should be indexed.

In our project

```
user_id

status
```

will be searched often.

Indexes significantly reduce lookup time.

---

### Follow-up

What happens without indexes?

### Answer

The database performs a Full Table Scan.

As the table grows, queries become slower.

---

# Question 11

## Explain the request flow.

### Answer

```
Client

↓

JWT Filter

↓

Controller

↓

Service

↓

Repository

↓

MySQL

↓

Repository

↓

Service

↓

Controller

↓

Client
```

---

# Question 12

## Why use validation annotations?

### Answer

Validation rejects invalid requests before business logic executes.

Examples

```java
@NotBlank

@NotNull

@Future
```

This prevents invalid data from reaching the database.

---

### Follow-up

Where does validation occur?

### Answer

Immediately after Spring converts the request body into the DTO.

If validation fails, the controller method is never executed.

---

# Question 13

## What future improvements would you make?

### Answer

- Prevent overlapping sessions
- Validate endTime > startTime
- Add session editing
- Add cancellation
- Add session history
- Add pagination
- Add filtering
- Add analytics
- Add active session endpoint
- Add recurring sessions

---

# Question 14

## Why use Flyway?

### Answer

Flyway versions database schema changes.

Benefits

- Reproducible databases
- Team consistency
- Version control
- Automatic migration
- Easy rollback strategy

---

# Question 15

## Why use LocalDateTime?

### Answer

The application stores local timestamps without timezone information.

It integrates well with Java Time API and Hibernate.

For globally distributed systems, ZonedDateTime or Instant may be preferred.

---

# Common Interview Mistakes

Candidates often

- Return entities directly.
- Put business logic inside controllers.
- Accept userId from clients.
- Use EAGER loading everywhere.
- Store enums as ORDINAL.
- Skip DTOs.
- Forget validation.
- Ignore indexes.
- Forget foreign keys.
- Treat services as repositories.

---

# Senior-Level Discussion Points

A senior interviewer may ask:

- How would you prevent overlapping sessions?
- How would you handle concurrent requests?
- Would optimistic locking be useful?
- Would Redis improve performance?
- How would you support recurring sessions?
- How would you notify the browser extension when a session becomes ACTIVE?
- How would you scale this service for millions of users?
- Would you partition the sessions table?
- How would you archive old sessions?
- Would you use scheduled jobs or event-driven architecture for activating sessions?

These questions extend beyond the current implementation but demonstrate how the design can evolve as the application grows.