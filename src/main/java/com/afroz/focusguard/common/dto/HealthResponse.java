package com.afroz.focusguard.common.dto;

import java.time.Instant;

public class HealthResponse {

    private String status;
    private String service;
    private String version;
    private Instant timestamp;

    public HealthResponse() {
    }

    public HealthResponse(String status,
                          String service,
                          String version,
                          Instant timestamp) {
        this.status = status;
        this.service = service;
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}