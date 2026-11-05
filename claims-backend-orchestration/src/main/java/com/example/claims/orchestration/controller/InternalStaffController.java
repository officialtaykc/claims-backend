package com.example.claims.orchestration.controller;

import com.example.claims.common.enums.ClaimStatus;
import com.example.claims.common.enums.ClaimType;
import com.example.claims.orchestration.dto.*;
import com.example.claims.orchestration.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Internal staff/management API consumed by claims-backend-api.
 * Mirrors the public /api/staff routes under /internal/staff.
 */
@RestController
@RequestMapping("/internal/staff")
public class InternalStaffController {

    private final ClaimService claimService;

    public InternalStaffController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/claims")
    public ResponseEntity<List<ClaimResponse>> searchQueue(
            @RequestParam(required = false) ClaimStatus status,
            @RequestParam(required = false) ClaimType claimType,
            @RequestParam(required = false) String market,
            @RequestParam(required = false) String assignedTo) {
        return ResponseEntity.ok(claimService.searchQueue(status, claimType, market, assignedTo));
    }

    @GetMapping("/claims/{claimReference}")
    public ResponseEntity<ClaimResponse> getClaim(@PathVariable String claimReference) {
        return ResponseEntity.ok(claimService.getClaimByReference(claimReference));
    }

    @PatchMapping("/claims/{claimReference}/assign")
    public ResponseEntity<ClaimResponse> assignClaim(
            @PathVariable String claimReference,
            @Valid @RequestBody AssignRequest request) {
        return ResponseEntity.ok(claimService.assignClaim(claimReference, request));
    }

    @PatchMapping("/claims/{claimReference}/status")
    public ResponseEntity<ClaimResponse> updateStatus(
            @PathVariable String claimReference,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(claimService.updateStatus(claimReference, request));
    }

    @PostMapping("/claims/{claimReference}/information-requests")
    public ResponseEntity<ClaimResponse.InfoRequestDto> createInfoRequest(
            @PathVariable String claimReference,
            @Valid @RequestBody InfoRequestCreateRequest request) {
        return ResponseEntity.ok(claimService.createInfoRequest(claimReference, request));
    }

    @PostMapping("/claims/{claimReference}/decision")
    public ResponseEntity<ClaimResponse> makeDecision(
            @PathVariable String claimReference,
            @Valid @RequestBody DecisionRequest request) {
        return ResponseEntity.ok(claimService.makeDecision(claimReference, request));
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryResponse> dashboardSummary() {
        return ResponseEntity.ok(claimService.getDashboardSummary());
    }

    @GetMapping("/dashboard/workload")
    public ResponseEntity<WorkloadResponse> workload() {
        return ResponseEntity.ok(claimService.getWorkload());
    }
}
