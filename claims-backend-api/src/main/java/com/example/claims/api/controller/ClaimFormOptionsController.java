package com.example.claims.api.controller;

import com.example.claims.api.service.ClaimFormOptionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ClaimFormOptionsController {

    private final ClaimFormOptionsService claimFormOptionsService;

    public ClaimFormOptionsController(ClaimFormOptionsService claimFormOptionsService) {
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
