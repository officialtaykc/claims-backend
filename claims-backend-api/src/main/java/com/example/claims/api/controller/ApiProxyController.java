package com.example.claims.api.controller;

import com.example.claims.api.service.OrchestrationClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Thin proxy for all read/write endpoints other than claim submission.
 * Forwards to the orchestration service and returns its response unchanged.
 */
@RestController
public class ApiProxyController {

    private final OrchestrationClient orchestrationClient;

    public ApiProxyController(OrchestrationClient orchestrationClient) {
        this.orchestrationClient = orchestrationClient;
    }

    @RequestMapping(value = "/api/**",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH,
                    RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxy(HttpServletRequest request,
                                        @RequestBody(required = false) String body) {
        return orchestrationClient.forward(request, body);
    }
}
