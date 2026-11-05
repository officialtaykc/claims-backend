package com.example.claims.orchestration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ClaimantPolicyResponse(
        UUID id,
        String policyNumber,
        String claimType,
        String productName,
        String market,
        String currency,
        String insuredItem,
        BigDecimal coverageAmount,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        boolean active,
        String claimantName,
        String claimantEmail,
        String claimantPhone) {
}
