package com.example.claims.orchestration.controller;

import com.example.claims.orchestration.dto.*;
import com.example.claims.orchestration.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Internal API consumed by claims-backend-api. Not exposed publicly.
 * Mirrors the public /api/claims routes under /internal/claims.
 */
@RestController
@RequestMapping("/internal/claims")
public class InternalClaimController {

    private final ClaimService claimService;

    public InternalClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public ResponseEntity<List<ClaimResponse>> getClaimsByEmail(@RequestParam String claimantEmail) {
        return ResponseEntity.ok(claimService.getClaimsByEmail(claimantEmail));
    }

    @GetMapping("/{claimReference}")
    public ResponseEntity<ClaimResponse> getClaim(@PathVariable String claimReference) {
        return ResponseEntity.ok(claimService.getClaimByReference(claimReference));
    }

    @GetMapping("/{claimReference}/history")
    public ResponseEntity<List<ClaimStatusHistoryResponse>> getHistory(@PathVariable String claimReference) {
        return ResponseEntity.ok(claimService.getHistory(claimReference));
    }

    @PostMapping("/{claimReference}/information-requests/{requestId}/response")
    public ResponseEntity<ClaimResponse.InfoRequestDto> respondToInfoRequest(
            @PathVariable String claimReference,
            @PathVariable UUID requestId,
            @Valid @RequestBody InfoRequestRespondRequest request) {
        return ResponseEntity.ok(claimService.respondToInfoRequest(claimReference, requestId, request));
    }

}
