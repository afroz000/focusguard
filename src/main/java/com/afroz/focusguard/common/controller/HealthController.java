package com.afroz.focusguard.common.controller;

import com.afroz.focusguard.common.dto.HealthResponse;
import com.afroz.focusguard.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public ApiResponse<HealthResponse> health(){
        HealthResponse healthResponse=new HealthResponse(
                "UP",
                "focusguard-backend",
                "0.0.1",
                Instant.now()
        );
        return ApiResponse.success("FocusGuard service is healthy", healthResponse);

    }
}