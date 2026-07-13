# User Entity

## Purpose

The `User` entity represents a registered user of the FocusGuard application.

It is mapped to the `users` table in MySQL using JPA and Hibernate. Each `User` object represents one row in that table.

---

## Where It Is Used In Our Project

Current usage:

- `UserRepository`
- `UserService`
- User registration flow

Future usage:

- Login
- JWT authentication
- Profile management
- Password reset
- Focus-session ownership

---

## Database Mapping

| Java Field | Database Column |
|---|---|
| `id` | `id` |
| `firstName` | `first_name` |
| `lastName` | `last_name` |
| `email` | `email` |
| `password` | `password` |
| `createdAt` | `created_at` |

---

## Our Implementation

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

---

## Understanding Our Code

### `@Entity`

Marks the class as a JPA entity.

Without it, Hibernate will not manage or persist the class.

### `@Table(name = "users")`

Maps the class to the `users` table.

### `@Id`

Marks the entity's primary-key field.

### `@GeneratedValue(strategy = GenerationType.IDENTITY)`

Uses MySQL's `AUTO_INCREMENT` mechanism to generate the ID.

### `@Column`

Defines how a field maps to a database column.

Example:

```java
@Column(name = "first_name", nullable = false, length = 100)
private String firstName;
```

This means:

- Column name: `first_name`
- Value cannot be `NULL`
- Maximum length: 100 characters

### `@CreationTimestamp`

Automatically sets the creation time when Hibernate inserts the entity.

```java
@CreationTimestamp
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;
```

We do not manually set this field during registration.

---

## Request Flow

```text
Postman
   ↓
UserController
   ↓
RegisterUserRequest
   ↓
UserService
   ↓
User object
   ↓
UserRepository
   ↓
Hibernate
   ↓
MySQL users table
```

---

## Internal Working

When `userRepository.save(user)` is called:

1. Spring Data JPA delegates to the JPA implementation.
2. Hibernate examines the `User` entity.
3. Hibernate generates an `INSERT` SQL statement.
4. MySQL stores the row.
5. MySQL generates the ID.
6. Hibernate returns the persisted entity with generated values such as `id` and `createdAt`.

---

## Entity vs DTO

| Entity | DTO |
|---|---|
| Represents database data | Represents API data |
| Managed by Hibernate | Not managed by Hibernate |
| Used for persistence | Used for request/response transfer |
| May contain sensitive fields | Exposes only required fields |
| Should normally remain internal | Safe to return to clients |

Initially, the controller returned the `User` entity directly. This exposed the password in the API response.

We replaced it with:

```java
RegisterUserResponse
```

This response contains only:

- `id`
- `firstName`
- `lastName`
- `email`
- `createdAt`

---

## Best Practices

- Keep entities focused on persistence.
- Return DTOs instead of entities.
- Never expose password fields.
- Avoid placing controller logic inside entities.
- Keep API contracts separate from database models.
- Let Flyway manage schema changes.

---

## Common Mistakes

- Returning entities directly from controllers.
- Storing plain-text passwords.
- Mixing API validation with persistence mapping.
- Assuming `@Column(unique = true)` is enough for user-friendly duplicate handling.
- Manually setting fields that Hibernate can manage automatically.

---

# Interview Questions

## 1. What is a JPA entity?

### Answer

A JPA entity is a Java class mapped to a database table using JPA annotations. Each object represents one row in that table.

### Explanation

JPA defines the mapping contract, while Hibernate is the implementation currently performing the actual persistence work.

---

## 2. What is Hibernate?

### Answer

Hibernate is an ORM framework and a JPA implementation. It maps Java objects to relational database tables and generates SQL for persistence operations.

### Explanation

In FocusGuard, Hibernate converts a `User` object into an `INSERT` statement when `userRepository.save(user)` is called.

---

## 3. What is ORM?

### Answer

ORM means Object-Relational Mapping. It maps object-oriented classes and fields to relational tables and columns.

### Explanation

It allows us to work mainly with Java objects instead of manually converting database rows into objects.

---

## 4. Why use `GenerationType.IDENTITY`?

### Answer

We use `IDENTITY` because MySQL generates the primary key using `AUTO_INCREMENT`.

### Explanation

The ID is assigned by the database during insertion and then populated back into the entity.

---

## 5. What does `@CreationTimestamp` do?

### Answer

It automatically populates the field with the current timestamp when the entity is first persisted.

### Explanation

This avoids manually assigning creation timestamps in service code.

---

## 6. Why should entities not be returned directly from REST APIs?

### Answer

Entities may contain sensitive or internal fields and tightly couple the API contract to the database model. DTOs allow the application to expose only the required data.

### Explanation

Our original registration response exposed the password because we returned `User` directly. `RegisterUserResponse` fixed that issue.

---

## 7. What is the difference between an entity and a DTO?

### Answer

An entity represents persistent database data and is managed by Hibernate. A DTO transfers data between layers or between the client and server and is not managed by Hibernate.

### Explanation

`User` is the persistence model. `RegisterUserRequest` and `RegisterUserResponse` are API models.

---

# Real Interview Discussion

## Interviewer

Why didn't you return the `User` entity directly?

## Strong Answer

Initially, I did return the `User` entity directly. While testing the registration endpoint in Postman, I noticed that the response exposed the password field.

To fix this, I introduced a `RegisterUserResponse` DTO. It exposes only the fields required by the client and keeps sensitive fields such as `password` internal to the backend.

This also decouples the API contract from the persistence model, so changes to the entity or database schema do not automatically affect API consumers.

## Why This Is a Strong Answer

It shows:

- Security awareness
- Practical debugging experience
- Understanding of DTOs
- Separation of concerns
- Ability to justify an architectural improvement

## Weak Answer

> I used DTOs because tutorials say entities should not be returned.

This does not show ownership or understanding.

---

## Follow-up: Why not just use `@JsonIgnore` on the password?

### Strong Answer

`@JsonIgnore` would hide the password during serialization, but the API would still be directly coupled to the entity.

A response DTO gives stronger separation between the database model and the public API contract and allows both to evolve independently.

---

## Follow-up: Why keep the entity and DTO separate?

### Strong Answer

The entity describes how data is stored. The DTO describes how data is exchanged.

Keeping them separate improves security, maintainability, and flexibility.

---

## Things to Revise

- JPA entity
- Hibernate
- ORM
- `@Entity`
- `@Table`
- `@Id`
- `@GeneratedValue`
- `@Column`
- `@CreationTimestamp`
- Entity lifecycle
- Entity vs DTO
- Why entities should not be returned directly

---

## Related Files

```text
src/main/java/com/afroz/focusguard/user/entity/User.java
src/main/java/com/afroz/focusguard/user/dto/RegisterUserRequest.java
src/main/java/com/afroz/focusguard/user/dto/RegisterUserResponse.java
src/main/java/com/afroz/focusguard/user/repository/UserRepository.java
src/main/java/com/afroz/focusguard/user/service/UserService.java
src/main/java/com/afroz/focusguard/user/controller/UserController.java
src/main/resources/db/migration/V1__create_users_table.sql
```

---

## Sprint References

- Sprint 2
- FG-108: User Entity
- FG-110: Registration API
- FG-111: Validation and secure password storage