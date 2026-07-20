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

- token
- type

Token currently uses a temporary placeholder.

---

# Next Sprint

Implement real JWT generation.

Implement JWT validation filter.

Secure protected endpoints.