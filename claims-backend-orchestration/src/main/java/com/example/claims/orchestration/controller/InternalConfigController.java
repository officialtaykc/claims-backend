package com.example.claims.orchestration.controller;

import com.example.claims.orchestration.service.ClaimFormOptionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/config")
public class InternalConfigController {

    private final ClaimFormOptionsService claimFormOptionsService;

    public InternalConfigController(ClaimFormOptionsService claimFormOptionsService) {
        this.claimFormOptionsService = claimFormOptionsService;
    }

    @GetMapping("/motor-claims")
    public ResponseEntity<Map<String, List<String>>> motorClaimOptions() {
        return ResponseEntity.ok(claimFormOptionsService.getOptionsByClaimType("MOTOR"));
    }

    @GetMapping("/property-claims")
    public ResponseEntity<Map<String, List<String>>> propertyClaimOptions() {
        return ResponseEntity.ok(claimFormOptionsService.getOptionsByClaimType("PROPERTY"));
    }
}
