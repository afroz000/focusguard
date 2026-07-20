package com.afroz.focusguard.auth.jwt;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generateToken(String email) {
        return "temporary-token-for-" + email;
    }
}