package com.example.claims.common.enums;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum ClaimStatus {
    NEW,
    IN_REVIEW,
    ADDITIONAL_INFO_REQUIRED,
    PENDING_DECISION,
    APPROVED,
    PARTIALLY_APPROVED,
    REJECTED,
    SETTLEMENT_PENDING,
    SETTLED;

    private static final Map<ClaimStatus, Set<ClaimStatus>> ALLOWED_TRANSITIONS = Map.of(
            NEW, EnumSet.of(IN_REVIEW),
            IN_REVIEW, EnumSet.of(ADDITIONAL_INFO_REQUIRED, PENDING_DECISION),
            ADDITIONAL_INFO_REQUIRED, EnumSet.of(IN_REVIEW),
            PENDING_DECISION, EnumSet.of(APPROVED, PARTIALLY_APPROVED, REJECTED),
            APPROVED, EnumSet.of(SETTLEMENT_PENDING),
            PARTIALLY_APPROVED, EnumSet.of(SETTLEMENT_PENDING),
            REJECTED, EnumSet.noneOf(ClaimStatus.class),
            SETTLEMENT_PENDING, EnumSet.of(SETTLED),
            SETTLED, EnumSet.noneOf(ClaimStatus.class)
    );

    public boolean canTransitionTo(ClaimStatus target) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, EnumSet.noneOf(ClaimStatus.class)).contains(target);
    }
}
