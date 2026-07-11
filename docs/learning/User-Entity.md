# User Entity

## What is an Entity?

An Entity is a Java class that represents a table in the database.

Each object of the class represents one row in that table.

Example:

```
users
```

↓

```
User.java
```

One row in the `users` table becomes one `User` object in Java.

---

## Why do we use an Entity?

Instead of writing SQL and manually converting rows into Java objects, JPA/Hibernate performs this mapping automatically.

This allows us to work with Java objects instead of database rows.

---

## Annotations Used

### @Entity

Marks the class as a JPA entity.

Without this annotation, Hibernate ignores the class.

---

### @Table(name = "users")

Maps the entity to the `users` table.

---

### @Id

Marks the primary key.

---

### @GeneratedValue(strategy = GenerationType.IDENTITY)

Tells the database to generate the primary key automatically using AUTO_INCREMENT.

---

### @Column

Maps a Java field to a database column.

It can also specify:

- name
- nullable
- unique
- length

---

### @CreationTimestamp

Automatically stores the current timestamp when the entity is first inserted into the database.

---

## Entity vs Table

| Database | Java |
|----------|------|
| Table | Entity |
| Row | Object |
| Column | Field |

---

# Interview Questions

## What is an Entity?

### Answer

An Entity is a Java class mapped to a database table using JPA annotations.

Each object represents one row in the table.

---

## Why do we use @Entity?

### Answer

It tells Hibernate that the class should be managed as a database entity.

Without `@Entity`, Hibernate will not create or map the class.

---

## What is the purpose of @CreationTimestamp?

### Answer

It automatically populates the field with the current timestamp when the entity is inserted into the database.

This avoids manually setting timestamps in application code.