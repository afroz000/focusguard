# Focus Sessions

---

# Overview

A Focus Session is the core domain object of FocusGuard.

It represents a scheduled period during which a user intends to focus on productive work. During an active focus session, the browser extension will eventually enforce website blocking rules by communicating with the backend.

Although this ticket only allows session creation, it establishes the foundation for several future features including:

- Active session detection
- Website blocking
- Session editing
- Session cancellation
- Productivity analytics
- Daily statistics
- Weekly reports
- Browser synchronization

Because this entity sits at the center of the application, its design decisions are important.

---

# Why create a separate FocusSession entity?

It may seem tempting to simply add a few columns inside the User table such as

```
focus_start
focus_end
status
```

This approach quickly breaks down.

A user may have

- today's session
- tomorrow's session
- multiple sessions every day
- completed sessions
- cancelled sessions

One row inside the User table cannot represent an unlimited number of focus sessions.

Instead we normalize the database.

```
User

1
│
│
├───────────────┐
│               │
│               │
▼               ▼

Session 1

Session 2

Session 3
```

One user owns many sessions.

Each session belongs to exactly one user.

This is called a **One-to-Many relationship**.

---

# Database Design

The table is

```
focus_sessions
```

Columns

```
id

user_id

name

start_time

end_time

status

created_at
```

Each column has a specific responsibility.

---

## id

Primary Key.

Every session needs a unique identifier.

Example

```
1

2

3

4
```

Without a primary key we cannot

- update
- delete
- retrieve

a specific session.

---

## user_id

This is the most important column.

Instead of storing the user's email or username repeatedly inside every session, we store only the user's ID.

Example

Users table

|id|email|
|--|------|
|5|abc@gmail.com|

Sessions

|id|user_id|
|--|-------|
|1|5|
|2|5|
|3|5|

This is called a **Foreign Key**.

Benefits

- avoids duplicate data
- faster joins
- maintains referential integrity
- saves storage

---

# Why Foreign Keys Matter

Suppose foreign keys did not exist.

```
Session

user_id = 9000
```

But User 9000 does not exist.

Now the database contains invalid data.

Foreign Keys prevent this.

The database itself rejects invalid relationships before they are stored.

This is one of the biggest advantages of relational databases.

---

# ON DELETE CASCADE

Our migration uses

```sql
ON DELETE CASCADE
```

Suppose

```
User

5
```

owns

```
Session A

Session B

Session C
```

If the user is deleted, what should happen?

Without CASCADE

```
User deleted

↓

Sessions still remain

↓

Invalid user references
```

Those sessions now point to a user that no longer exists.

This creates orphan records.

With

```
ON DELETE CASCADE
```

the database automatically removes every session belonging to that user.

```
Delete User

↓

Delete Session A

↓

Delete Session B

↓

Delete Session C
```

No orphan records remain.

---

# Why We Added Indexes

Indexes make searching much faster.

Without an index

Finding

```
user_id = 5
```

means checking every row.

```
1

2

3

4

5

6

7

8

9

...
```

This is called a Full Table Scan.

Time complexity

```
O(n)
```

With an index

The database maintains an internal search structure similar to a balanced tree.

Instead of checking every row

```
1

2

3

...

1000000
```

it jumps directly near the required rows.

Time complexity becomes approximately

```
O(log n)
```

for lookup.

---

# Why We Indexed user_id

Future APIs will frequently execute queries like

```sql
SELECT *
FROM focus_sessions
WHERE user_id = ?;
```

Without an index, this becomes slower as the table grows.

By indexing

```
user_id
```

the database can locate all sessions for a user efficiently.

---

# Why We Indexed status

Future APIs may execute

```sql
SELECT *
FROM focus_sessions
WHERE status='ACTIVE';
```

or

```sql
SELECT *
FROM focus_sessions
WHERE status='SCHEDULED';
```

These queries become significantly faster because the database can search the index instead of scanning every row.

---

# Understanding Entity Mapping

Our Java class is

```java
@Entity
@Table(name = "focus_sessions")
public class FocusSession
```

This tells Hibernate

```
FocusSession

↓

maps to

↓

focus_sessions
```

Whenever we save a Java object

```java
repository.save(session);
```

Hibernate automatically converts it into SQL.

We do not write INSERT statements manually.

This process is called **Object Relational Mapping (ORM)**.

---

# What is ORM?

ORM stands for

Object Relational Mapping.

Simple explanation

Java understands objects.

Databases understand tables.

ORM translates between them.

```
Java Object

↓

Hibernate

↓

SQL

↓

Database
```

Without ORM we would manually write

```sql
INSERT INTO ...
```

```sql
UPDATE ...
```

```sql
DELETE ...
```

```sql
SELECT ...
```

for every operation.

Hibernate performs these automatically.

---

# Why @ManyToOne?

Inside FocusSession

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
```

Many sessions belong to one user.

```
Session A

\
 \
  \
   User

  /
 /

Session B

```

Therefore

```
Many

↓

One
```

If we accidentally used

```
@OneToOne
```

Hibernate would assume one user can own only one session.

That would completely break our application.

Choosing the correct relationship annotation is critical.

# Understanding FetchType.LAZY

Our entity contains

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
```

One important decision here is the fetch type.

Hibernate supports two primary loading strategies.

```
EAGER
```

and

```
LAZY
```

---

## EAGER Loading

Suppose we load one session.

```java
FocusSession session = repository.findById(1L).get();
```

With

```java
fetch = FetchType.EAGER
```

Hibernate automatically performs something similar to

```
Load FocusSession

↓

Immediately Load User
```

Even if we never use the User object.

Example

```
Session

↓

User

↓

Address

↓

Roles

↓

Permissions

...
```

This can result in a large number of unnecessary database queries and significantly increase memory usage.

---

## LAZY Loading

With

```java
fetch = FetchType.LAZY
```

Hibernate loads only the session initially.

```
FocusSession
```

The associated User is loaded **only when it is actually accessed**.

Example

```java
FocusSession session = repository.findById(id).get();
```

At this point

```
User NOT loaded.
```

Only when we execute

```java
session.getUser();
```

does Hibernate execute another SQL query to retrieve the user.

This saves both memory and database calls when the relationship is not required.

---

## Why We Chose LAZY

Most APIs dealing with focus sessions do not need the entire User object.

Examples

- Create Session
- Cancel Session
- Complete Session
- Update Session
- List Sessions

In all these cases, the user has already been authenticated, so loading the complete User every time would waste resources.

Therefore

```
LAZY
```

is the better default choice.

---

## Common Mistake

Many beginners assume

```
LAZY = Always Better
```

This is not true.

If an API immediately accesses the related object every single time, EAGER loading may actually be simpler.

Choosing the fetch strategy depends on the application's access patterns.

---

# Understanding @JoinColumn

Our mapping contains

```java
@JoinColumn(name = "user_id")
```

This tells Hibernate which database column stores the foreign key.

Without it, Hibernate attempts to infer the column name automatically.

Explicitly specifying the column name avoids ambiguity and makes the mapping easier to understand.

Database

```
focus_sessions

↓

user_id

↓

users.id
```

---

# Understanding @CreationTimestamp

Our entity contains

```java
@CreationTimestamp
private LocalDateTime createdAt;
```

Hibernate automatically sets this field when the entity is first inserted into the database.

We never write

```java
session.setCreatedAt(...)
```

ourselves.

---

## Why Not Set It Manually?

Suppose every developer writes

```java
session.setCreatedAt(LocalDateTime.now());
```

Problems

- Easy to forget
- Duplicate code
- Different developers may implement it differently
- Harder to maintain

Automatic generation ensures consistency.

---

## Database Timestamp vs Hibernate Timestamp

Another option is

```sql
DEFAULT CURRENT_TIMESTAMP
```

inside the SQL migration.

Both approaches work.

### Database Timestamp

Advantages

- Works regardless of programming language.
- Generated entirely by MySQL.
- Useful when multiple applications write to the same database.

Disadvantages

- Java object may not immediately know the generated value unless refreshed.

---

### Hibernate @CreationTimestamp

Advantages

- Very simple to use.
- Automatically populated in the entity.
- Keeps timestamp management inside the application layer.

Disadvantages

- Depends on Hibernate.

For FocusGuard, either approach is acceptable. We chose `@CreationTimestamp` because the project already uses Hibernate as its ORM.

---

# Why Enum Instead of String?

The session status is represented by

```java
public enum SessionStatus {
    SCHEDULED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}
```

Imagine storing status as a plain String.

```java
session.setStatus("scheduled");
```

Elsewhere

```java
session.setStatus("Schedule");
```

Or

```java
session.setStatus("scheduld");
```

These mistakes compile successfully but create inconsistent data.

Enums eliminate this entire category of errors.

Only predefined values are allowed.

---

# Why @Enumerated(EnumType.STRING)?

Our entity uses

```java
@Enumerated(EnumType.STRING)
```

Hibernate stores

```
SCHEDULED
```

instead of

```
0
```

Why?

Suppose the enum initially is

```
0 -> SCHEDULED

1 -> ACTIVE

2 -> COMPLETED
```

Months later we add

```
PAUSED
```

at the top.

Now

```
0 -> PAUSED

1 -> SCHEDULED

2 -> ACTIVE

3 -> COMPLETED
```

Existing database values become incorrect.

By storing

```
"SCHEDULED"
```

instead of

```
0
```

the database remains stable even if the enum order changes.

This is considered a best practice.

---

# Why Use DTOs?

The client sends

```
JSON
```

The database stores

```
Entities
```

These should not be the same object.

Instead we introduce DTOs.

Request

```
Client

↓

CreateSessionRequest

↓

Entity

↓

Database
```

Response

```
Database

↓

Entity

↓

SessionResponse

↓

Client
```

---

## Benefits of DTOs

- Hide internal fields.
- Prevent accidental data exposure.
- Keep API contracts stable.
- Allow validation.
- Decouple database schema from API design.

---

## Why Not Return the Entity?

Suppose the User entity contains

- password
- roles
- enabled
- email
- createdAt

Returning entities directly may accidentally expose sensitive information.

DTOs ensure only the required fields are returned.

---

# Validation

The request uses

```java
@NotBlank
```

for the session name.

This prevents

```
""

"   "

null
```

from being accepted.

---

The timestamps use

```java
@NotNull
```

to ensure they are present.

---

They also use

```java
@Future
```

which ensures the client cannot schedule a session in the past.

Without validation, invalid data reaches the service layer and eventually the database.

Validation stops bad requests as early as possible.

---

# SecurityContextHolder

After JWT authentication succeeds, Spring Security stores the authenticated user in the `SecurityContext`.

We retrieve it using

```java
Authentication authentication =
    SecurityContextHolder.getContext().getAuthentication();

String email = authentication.getName();
```

This gives us the identity of the currently logged-in user.

The client never sends a `userId`.

This is a crucial security decision.

If the client supplied

```json
{
    "userId": 42
}
```

a malicious user could simply change the value and create sessions for another account.

By deriving ownership from the authenticated JWT, we ensure users can create sessions **only for themselves**.

---

# Common Mistakes

1. Using `FetchType.EAGER` everywhere.
2. Returning entities directly from controllers.
3. Storing enums using `ORDINAL`.
4. Accepting `userId` from the client.
5. Forgetting validation annotations.
6. Omitting indexes on frequently queried columns.
7. Not using foreign keys.
8. Forgetting `ON DELETE CASCADE` when appropriate.
9. Mixing persistence logic inside controllers.
10. Skipping DTOs because "the entity already has the same fields."

---

# Best Practices Learned

- Model real-world relationships using proper JPA associations.
- Keep controllers thin and move business logic to services.
- Use DTOs as the API boundary.
- Prefer `EnumType.STRING` over `ORDINAL`.
- Use `FetchType.LAZY` unless there is a clear reason for eager loading.
- Derive ownership from the authenticated principal, never from client input.
- Add indexes for columns that will be searched frequently.
- Let Hibernate or the database generate creation timestamps automatically.
- Validate requests before business logic executes.
- Design today's entity with future features in mind rather than only today's requirements.

---

# Key Takeaways

By completing FG-110, we learned how to:

- Design a normalized relational schema.
- Model a One-to-Many relationship using JPA.
- Map Java objects to database tables with Hibernate.
- Choose appropriate fetch strategies.
- Use foreign keys and cascading deletes correctly.
- Protect APIs by deriving user identity from JWT authentication.
- Build clean REST APIs using DTOs.
- Validate input before persistence.
- Apply best practices that scale as the application grows.

These concepts form the foundation for future FocusGuard features such as session activation, overlap detection, analytics, browser synchronization, and website blocking.