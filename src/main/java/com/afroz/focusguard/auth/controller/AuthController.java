package com.afroz.focusguard.auth.controller;

import com.afroz.focusguard.auth.dto.LoginRequest;
import com.afroz.focusguard.auth.dto.LoginResponse;
import com.afroz.focusguard.auth.service.AuthService;
import com.afroz.focusguard.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        LoginResponse response = authService.login(loginRequest);

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response)
        );
    }
}