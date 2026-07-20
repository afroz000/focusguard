package com.afroz.focusguard.auth.service;

import com.afroz.focusguard.auth.dto.LoginRequest;
import com.afroz.focusguard.auth.dto.LoginResponse;
import com.afroz.focusguard.exception.InvalidCredentialsException;
import com.afroz.focusguard.user.entity.User;
import com.afroz.focusguard.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.afroz.focusguard.auth.jwt.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService= jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password")
                );

        boolean passwordMatches = passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token, "Bearer");
    }
}