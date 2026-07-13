package com.afroz.focusguard.user.controller;

import com.afroz.focusguard.common.response.ApiResponse;
import com.afroz.focusguard.user.dto.RegisterUserRequest;
import com.afroz.focusguard.user.dto.RegisterUserResponse;
import com.afroz.focusguard.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> registerUser(
            @Valid @RequestBody RegisterUserRequest request) {

        RegisterUserResponse response = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }
}