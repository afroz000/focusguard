# JWT Authentication Architecture

## Objective

Implement a stateless authentication mechanism using Spring Security and JWT to secure FocusGuard's REST APIs.

---

# High-Level Architecture

                Client
                   │
                   │ HTTP Request
                   ▼
        JwtAuthenticationFilter
                   │
                   ▼
             JwtService
         (Validate JWT)
                   │
                   ▼
      CustomUserDetailsService
                   │
                   ▼
           UserRepository
                   │
                   ▼
                MySQL
                   │
                   ▼
      SecurityContextHolder
                   │
                   ▼
      Spring Security Authorization
                   │
                   ▼
             REST Controller

---

# Components

## JwtService

Responsible for:

- generating JWTs
- validating JWTs
- extracting claims
- checking expiration
- verifying token ownership

It contains no database logic.

---

## JwtAuthenticationFilter

Acts as the entry point for every incoming request.

Responsibilities:

- inspect Authorization header
- extract Bearer token
- validate JWT
- authenticate user
- populate SecurityContextHolder

The filter does not contain business logic.

---

## CustomUserDetailsService

Acts as the bridge between Spring Security and the application's user database.

Responsibilities:

- load users from MySQL
- convert User entity into UserDetails

---

## UserRepository

Responsible only for data access.

Authentication logic is intentionally kept outside the repository.

---

## SecurityContextHolder

Stores the authenticated user for the lifetime of the current HTTP request.

Controllers and other Spring Security components retrieve authentication information from here instead of validating the JWT again.

---

# Design Decisions

## Stateless Authentication

Chosen because:

- REST APIs should remain stateless.
- The server does not store user sessions.
- Horizontal scaling becomes simpler.
- Browser extensions can send the JWT with every request.

---

## Filter-Based Authentication

Authentication is implemented using a servlet filter instead of controller logic because:

- every request passes through the filter
- authentication occurs before controller execution
- controllers remain focused on business logic

---

## Separation of Responsibilities

Each class has a single responsibility.

| Component | Responsibility |
|-----------|----------------|
| AuthController | Handle HTTP requests |
| AuthService | Login business logic |
| JwtService | JWT generation and validation |
| JwtAuthenticationFilter | Authenticate every request |
| CustomUserDetailsService | Load users |
| UserRepository | Database access |

This separation improves maintainability and testability.

---

# Request Lifecycle

1. Client sends a request with a Bearer token.
2. JwtAuthenticationFilter intercepts the request.
3. JwtService validates the JWT.
4. CustomUserDetailsService loads the user.
5. Authentication is stored in SecurityContextHolder.
6. Spring Security evaluates authorization rules.
7. The request reaches the controller if authentication succeeds.

---

# Current Limitations

Current implementation supports authentication only.

Future enhancements include:

- role-based authorization
- refresh tokens
- logout/token revocation
- email verification
- password reset