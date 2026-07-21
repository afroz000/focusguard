# Sprint 03

## Goal

Implement authentication.

---

# Features Completed

- Created feature/user-authentication branch.
- Designed authentication flow.
- Added LoginRequest DTO.
- Added LoginResponse DTO.
- Created AuthController.
- Created AuthService.
- Created AuthServiceImpl.
- Added InvalidCredentialsException.
- Implemented login endpoint.
- Verified passwords using BCrypt.
- Added temporary JwtService.
- Successfully tested login endpoint using Postman.

---

# APIs

POST

/api/v1/auth/login

---

# Current Response

Returns

- JWT Token
- Token Type (Bearer)

JWT contains:

- Subject (email)
- Issued At
- Expiration Time
- Digital Signature

Successfully verified using Postman.

---

# Next Sprint

- Implement JWT validation.
- Extract username from JWT.
- Create JwtAuthenticationFilter.
- Secure protected APIs.
- Implement Authorization header validation.