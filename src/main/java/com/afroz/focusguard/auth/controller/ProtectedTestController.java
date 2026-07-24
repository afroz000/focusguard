package com.afroz.focusguard.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//this is temporary and will be removed once the first authenticated feature endpoint is added
@RestController
@RequestMapping("/api/v1/test")
public class ProtectedTestController {

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "You accessed a protected endpoint";
    }
}