# User Repository

## Purpose

The `UserRepository` provides the data access layer for the `User` entity.

It is responsible for interacting with the database without requiring us to write SQL for common CRUD operations.

---

## Where It Is Used In Our Project

Current usage:

- User Registration
- Duplicate email validation

Future usage:

- Login
- Fetch user profile
- Password reset
- JWT authentication
- Account management

---

## Our Implementation

```java
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
```

---

## Understanding Our Code

### JpaRepository<User, Long>

```java
extends JpaRepository<User, Long>
```

This tells Spring Data JPA:

- The Entity is `User`
- The Primary Key type is `Long`

By extending `JpaRepository`, we automatically inherit many database operations.

Examples:

```java
save()

findById()

findAll()

deleteById()

existsById()

count()
```

No implementation is required.

---

### Why is it an Interface?

We never wrote:

```java
class UserRepositoryImpl
```

Spring Boot automatically creates the implementation during application startup using dynamic proxies.

That implementation becomes a Spring Bean and is injected into our service.

---

### existsByEmail()

```java
boolean existsByEmail(String email);
```

Spring automatically generates SQL similar to:

```sql
SELECT COUNT(*)
FROM users
WHERE email = ?
```

If the count is greater than zero:

```
true
```

otherwise:

```
false
```

---

### findByEmail()

```java
Optional<User> findByEmail(String email);
```

Spring automatically generates SQL similar to:

```sql
SELECT *
FROM users
WHERE email = ?
```

We haven't used this method yet, but it will be useful during Login.

---

## Internal Working

```
UserService
      │
calls existsByEmail()
      │
Spring Data JPA
      │
Hibernate
      │
Generated SQL
      │
MySQL
      │
Result
```

---

## Query Derivation

Spring Data JPA understands method names.

Example:

```java
findByEmail(...)
```

becomes

```sql
SELECT *
FROM users
WHERE email = ?
```

Similarly:

```java
existsByEmail(...)
```

becomes

```sql
SELECT COUNT(*)
FROM users
WHERE email = ?
```

No SQL was written by us.

---

## Best Practices

- Keep repositories focused on database operations.
- Put business logic in services, not repositories.
- Return `Optional<T>` when an object may not exist.
- Prefer query derivation before writing custom SQL.

---

## Common Mistakes

- Writing business logic inside repositories.
- Returning `null` instead of `Optional`.
- Injecting repositories directly into controllers.
- Writing custom SQL when Spring Data can derive the query automatically.

---

# Interview Questions

## 1. What is JpaRepository?

### Answer

`JpaRepository` is a Spring Data JPA interface that provides CRUD operations and additional JPA functionality for an entity.

### Explanation

Instead of writing SQL for basic operations, we inherit methods such as `save()`, `findAll()` and `deleteById()`.

---

## 2. Why is UserRepository an interface?

### Answer

Spring Data JPA automatically creates the implementation at runtime.

### Explanation

We only define the contract. Spring generates the implementation and registers it as a Spring Bean.

---

## 3. How does Spring know what SQL to generate?

### Answer

Spring parses repository method names using query derivation.

### Explanation

For example:

```java
existsByEmail(...)
```

is converted into a SQL query checking whether a row with the given email exists.

---

## 4. Why return Optional<User> instead of User?

### Answer

The requested user may not exist.

`Optional` forces the caller to explicitly handle that possibility instead of risking a `NullPointerException`.

---

## 5. Why shouldn't business logic be placed inside repositories?

### Answer

Repositories should only communicate with the database.

Business rules belong in the Service layer to maintain separation of concerns.

---

## Real Interview Discussion

### Interviewer

How were you able to query the database without writing SQL?

### Strong Answer

I used Spring Data JPA's query derivation feature.

By defining methods such as `existsByEmail()` and `findByEmail()`, Spring generated the required SQL automatically at runtime based on the method names.

For basic CRUD operations, I simply extended `JpaRepository`, which already provides methods like `save()`, `findById()` and `deleteById()`.

---

### Why This Is A Strong Answer

It demonstrates:

- Knowledge of Spring Data JPA
- Understanding of dynamic proxy generation
- Understanding of query derivation
- Awareness of separation of concerns

---

### Weak Answer

Spring somehow writes the SQL automatically.

---

## Things To Revise

- JpaRepository
- CRUD methods
- Query derivation
- Optional
- Dynamic proxy generation
- Repository pattern

---

## Related Files

```text
src/main/java/com/afroz/focusguard/user/repository/UserRepository.java
src/main/java/com/afroz/focusguard/user/service/UserService.java
src/main/java/com/afroz/focusguard/user/entity/User.java
```

---

## Sprint References

- Sprint 2
- FG-109: User Repository
- FG-110: User Registration
- FG-111: Duplicate Email Validation