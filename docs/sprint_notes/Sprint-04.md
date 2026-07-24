# Sprint 04 — JWT Validation & Authorization

## Sprint Goal

Implement JWT-based authentication for protected APIs.

The objective of this sprint was to validate JWTs received from clients, authenticate users using Spring Security, and secure all business endpoints while allowing authentication-related endpoints to remain publicly accessible.

---

# Features Implemented

## JwtService Enhancements

Extended JwtService to support JWT validation.

Added support for:

- extracting username
- extracting expiration time
- checking token expiration
- validating JWT ownership
- parsing signed JWT claims

---

## JwtAuthenticationFilter

Implemented a custom JWT authentication filter using `OncePerRequestFilter`.

Responsibilities:

- intercept every incoming request
- extract Bearer token
- validate JWT
- load user from database
- authenticate the request
- populate `SecurityContextHolder`
- continue the filter chain

---

## CustomUserDetailsService

Implemented a custom `UserDetailsService` to replace Spring Security's default in-memory authentication.

Responsibilities:

- load users from MySQL
- convert `User` entity into Spring Security's `UserDetails`
- throw `UsernameNotFoundException` when necessary

---

## Security Configuration

Configured Spring Security to:

- use stateless authentication
- register JWT filter
- allow public authentication endpoints
- protect all remaining endpoints

Public endpoints:

- `/api/v1/health`
- `/api/v1/users/register`
- `/api/v1/auth/login`

---

## Temporary Protected Endpoint

Created a temporary protected endpoint to verify JWT authentication.

This endpoint will be removed once the first authenticated business API is implemented.

---

# Testing Performed

## Login

Verified:

- valid credentials return JWT
- invalid credentials return authentication failure

---

## Protected Endpoint

Verified:

- request without JWT returns `401 Unauthorized`
- request with valid JWT returns `200 OK`
- request with modified JWT returns `401 Unauthorized`

---

## Filter Execution

Verified that:

- JWT filter executes once per request
- authenticated user is stored inside `SecurityContextHolder`
- protected controller executes only after successful authentication

---

# Challenges Faced

## Legacy Password Data

An existing database user contained a plain-text password created before BCrypt hashing was introduced.

Attempting authentication resulted in a server error because BCrypt expected an encoded password.

Resolution:

Registered a new user through the registration endpoint so the password was stored correctly as a BCrypt hash.

---

## Default Spring Security Authentication

Initially Spring Security authenticated users using its default in-memory user.

Implemented `CustomUserDetailsService` to authenticate users stored in MySQL instead.

---

# Outcome

FocusGuard now supports complete JWT-based authentication.

Protected endpoints can only be accessed using a valid JWT.

The authentication flow has been verified end-to-end.

---

# Next Sprint

The next sprint will focus on introducing authorization and building authenticated business functionality on top of the authentication infrastructure implemented in this sprint.