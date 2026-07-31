package com.afroz.focusguard.session.controller;

import com.afroz.focusguard.session.dto.CreateSessionRequest;
import com.afroz.focusguard.session.dto.SessionResponse;
import com.afroz.focusguard.session.service.FocusSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
public class FocusSessionController {

    private final FocusSessionService focusSessionService;

    public FocusSessionController(FocusSessionService focusSessionService) {
        this.focusSessionService = focusSessionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponse createSession(@Valid @RequestBody CreateSessionRequest request) {
        return focusSessionService.createSession(request);
    }
}