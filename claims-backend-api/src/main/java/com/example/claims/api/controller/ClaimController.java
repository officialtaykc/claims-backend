package com.example.claims.api.controller;

import com.example.claims.common.dto.ClaimSubmissionRequest;
import com.example.claims.common.dto.ClaimSubmittedResponse;
import com.example.claims.api.service.ClaimSubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimSubmissionService claimSubmissionService;

    public ClaimController(ClaimSubmissionService claimSubmissionService) {
        this.claimSubmissionService = claimSubmissionService;
    }

    @PostMapping
    public ResponseEntity<ClaimSubmittedResponse> submitClaim(
            @Valid @RequestBody ClaimSubmissionRequest request) {
        ClaimSubmittedResponse response = claimSubmissionService.submitClaim(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
