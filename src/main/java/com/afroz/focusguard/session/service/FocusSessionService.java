package com.afroz.focusguard.session.service;

import com.afroz.focusguard.session.dto.CreateSessionRequest;
import com.afroz.focusguard.session.dto.SessionResponse;
import com.afroz.focusguard.session.entity.FocusSession;
import com.afroz.focusguard.session.entity.SessionStatus;
import com.afroz.focusguard.session.repository.FocusSessionRepository;
import com.afroz.focusguard.user.entity.User;
import com.afroz.focusguard.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;
    private final UserRepository userRepository;

    public FocusSessionService(FocusSessionRepository focusSessionRepository,
                               UserRepository userRepository) {
        this.focusSessionRepository = focusSessionRepository;
        this.userRepository = userRepository;
    }

    public SessionResponse createSession(CreateSessionRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FocusSession session = new FocusSession();
        session.setUser(user);
        session.setName(request.getName().trim());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setStatus(SessionStatus.SCHEDULED);

        FocusSession savedSession = focusSessionRepository.save(session);

        SessionResponse response = new SessionResponse();
        response.setId(savedSession.getId());
        response.setName(savedSession.getName());
        response.setStartTime(savedSession.getStartTime());
        response.setEndTime(savedSession.getEndTime());
        response.setStatus(savedSession.getStatus());
        response.setCreatedAt(savedSession.getCreatedAt());

        return response;
    }
}