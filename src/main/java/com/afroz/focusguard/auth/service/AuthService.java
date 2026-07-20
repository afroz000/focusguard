package com.afroz.focusguard.auth.service;

import com.afroz.focusguard.auth.dto.LoginRequest;
import com.afroz.focusguard.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);
}