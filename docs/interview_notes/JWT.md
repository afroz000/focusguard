# JWT Authentication Interview Notes

## 1. What is JWT?

JWT (JSON Web Token) is a compact, signed token used to authenticate users in stateless applications.

A JWT allows the server to verify the identity of a client without storing session information.

---

## 2. Why use JWT instead of HTTP Sessions?

HTTP Sessions

- Server stores session data.
- Requires server-side memory.
- Harder to scale across multiple servers.

JWT

- Stateless.
- Server stores nothing.
- Every request contains its own authentication information.
- Better suited for REST APIs and microservices.

---

## 3. What are the parts of a JWT?

A JWT has three parts:

- Header
- Payload
- Signature

Format:

```
Header.Payload.Signature
```

The payload is readable.

Only the signature is used to verify authenticity.

---

## 4. Is JWT encrypted?

No.

JWT is digitally signed, not encrypted.

Anyone can decode the Header and Payload.

Only the server can generate a valid Signature using the secret key.

---

## 5. What information is stored in your JWT?

Currently:

- Subject (email)
- Issued At
- Expiration Time

---

## 6. How does login work?

Client

↓

POST /api/v1/auth/login

↓

AuthController

↓

AuthService

↓

UserRepository

↓

PasswordEncoder.matches()

↓

JwtService.generateToken()

↓

Return JWT

---

## 7. What happens when a protected request arrives?

Client Request

↓

JwtAuthenticationFilter

↓

Extract Bearer Token

↓

Validate JWT

↓

Load UserDetails

↓

Create Authentication

↓

Store Authentication in SecurityContextHolder

↓

Spring Security Authorization

↓

Controller

---

## 8. Why do we use OncePerRequestFilter?

Because authentication should happen exactly once for every HTTP request.

It avoids duplicate authentication logic within the same request.

---

## 9. Why create a CustomUserDetailsService?

Spring Security expects a UserDetailsService.

By implementing our own service we can authenticate users stored in MySQL instead of using Spring Security's default in-memory users.

---

## 10. What is SecurityContextHolder?

SecurityContextHolder stores the Authentication object for the current request.

After authentication succeeds, Spring Security uses this information to determine whether the request is allowed to access protected resources.

---

## 11. Why register JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter?

Because the JWT must be validated before Spring Security performs authorization.

If authentication is not established first, protected endpoints cannot be accessed.

---

## 12. What does SessionCreationPolicy.STATELESS do?

It disables HTTP session creation.

Every request must contain a valid JWT.

No authentication state is stored on the server.

---

## 13. Why do we use PasswordEncoder.matches()?

Passwords are stored as BCrypt hashes.

During login:

- the incoming password is hashed
- BCrypt compares it with the stored hash
- the original password is never recovered

---

## 14. Difference between Authentication and Authorization?

Authentication

"Who are you?"

Authorization

"What are you allowed to access?"

Authentication always happens before authorization.

---

## 15. How did you test your implementation?

Verified:

- login returns JWT
- protected endpoint rejects anonymous requests (401)
- protected endpoint accepts valid JWT (200)
- modified JWT returns 401
- JWT filter authenticates users successfully

---

## Common Follow-up Questions

### Why doesn't the server need to store sessions?

Because every request carries a signed JWT.

---

### Can a client modify the payload?

Yes.

But changing the payload changes the signature.

The server detects this and rejects the token.

---

### Can someone read my JWT?

Yes.

JWT payloads are Base64 encoded, not encrypted.

Never store sensitive information inside the payload.

---

### Why is JWT suitable for browser extensions?

Because the extension can attach the JWT in the Authorization header with every API request without relying on server-side sessions.

---

## Interview Summary

Authentication Flow

Login

↓

Generate JWT

↓

Client stores JWT

↓

Client sends JWT

↓

Filter validates JWT

↓

Authentication stored in SecurityContextHolder

↓

Controller executes