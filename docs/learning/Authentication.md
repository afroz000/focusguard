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

# Temporary JwtService

Today's implementation returns:

temporary-token-for-email

This verifies the authentication flow before implementing real JWT generation.

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

- Separate authentication from user management.
- Never compare encrypted passwords manually.
- Use PasswordEncoder.matches().
- Return a consistent response structure.
- Keep authentication endpoints public.