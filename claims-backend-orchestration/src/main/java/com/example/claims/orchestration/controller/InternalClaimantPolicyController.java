package com.example.claims.orchestration.controller;

import com.example.claims.orchestration.dto.ClaimantPolicyResponse;
import com.example.claims.orchestration.service.ClaimantPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/claimant/policies")
public class InternalClaimantPolicyController {

    private final ClaimantPolicyService policyService;

    public InternalClaimantPolicyController(ClaimantPolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public ResponseEntity<List<ClaimantPolicyResponse>> getPolicies(
            @RequestParam String claimantEmail) {
        return ResponseEntity.ok(policyService.getPolicies(claimantEmail));
    }
}
