# Authentication

## Goal

Authentication verifies the identity of a user before allowing access to protected resources.

Authentication answers:

> Who are you?

Authorization answers:

> What are you allowed to do?

---

# Login Flow

Client

↓

POST /api/v1/auth/login

↓

AuthController

↓

AuthService

↓

UserRepository.findByEmail()

↓

PasswordEncoder.matches()

↓

Generate JWT

↓

Return LoginResponse

---

# Components

## LoginRequest

Represents the incoming request body.

Fields:

- email
- password

Validation is performed using:

- @Email
- @NotBlank

---

## LoginResponse

Represents the response after successful login.

Current fields:

- token
- type

Currently the token is a temporary placeholder.

---

## AuthController

Responsibilities:

- Accept login requests.
- Validate request body.
- Delegate business logic to AuthService.
- Return ApiResponse.

Endpoint:

POST /api/v1/auth/login

---

## AuthService

Contains authentication business logic.

Responsibilities:

- Find user by email.
- Verify password.
- Generate token.
- Return LoginResponse.

---

## UserRepository

Authentication reuses:

findByEmail(String email)

Spring Data JPA automatically generates the SQL query.

---

## Password Verification

Passwords are never compared directly.

Instead:

PasswordEncoder.matches(rawPassword, encodedPassword)

is used.

Why?

Passwords are stored as BCrypt hashes.

The original password cannot be recovered from the database.

---

## InvalidCredentialsException

A single generic exception is thrown when:

- email does not exist
- password is incorrect

This avoids revealing whether an email exists in the system.

---

# JwtService

The JwtService is responsible for generating signed JWTs after successful authentication.

Current implementation includes:

- Subject (email)
- Issued At (iat)
- Expiration Time (exp)
- HMAC SHA signing using a secret key

JWT Structure:

Header.Payload.Signature

The token is digitally signed so that it cannot be modified without the server's secret key.

The signing key and expiration time are configured through application.properties.

---

# Authentication vs Authorization

Authentication

- Verifies identity.

Authorization

- Determines permissions after authentication succeeds.

Example:

Authentication

"Is this really Alice?"

Authorization

"Can Alice access the Admin dashboard?"

---

# Key Learnings

- Authentication verifies a user's identity.
- Authorization determines what an authenticated user is allowed to access.
- Separate authentication logic from user management logic.
- Passwords should always be stored as BCrypt hashes.
- Never compare hashed passwords manually; use `PasswordEncoder.matches()`.
- JWTs are digitally signed, not encrypted.
- A JWT consists of a Header, Payload, and Signature.
- JWT payloads are readable by anyone, but only the server can generate a valid signature using its secret key.
- Never hardcode secret keys; store them in external configuration (and in production, use environment variables or a secrets manager).
- Keep authentication endpoints (e.g., `/api/v1/auth/login`) publicly accessible while protecting business endpoints.
- Return a consistent API response structure for easier client integration.