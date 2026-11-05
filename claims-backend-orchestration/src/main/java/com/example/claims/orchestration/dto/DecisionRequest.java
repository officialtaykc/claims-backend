package com.example.claims.orchestration.dto;

import com.example.claims.common.enums.ClaimStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class DecisionRequest {

    @NotNull(message = "outcome is required (APPROVED, PARTIALLY_APPROVED or REJECTED)")
    private ClaimStatus outcome;

    private BigDecimal decisionAmount;

    @NotBlank(message = "decisionReason is required")
    private String decisionReason;

    @NotBlank(message = "decidedBy is required")
    private String decidedBy;

    public ClaimStatus getOutcome() {
        return outcome;
    }

    public void setOutcome(ClaimStatus outcome) {
        this.outcome = outcome;
    }

    public BigDecimal getDecisionAmount() {
        return decisionAmount;
    }

    public void setDecisionAmount(BigDecimal decisionAmount) {
        this.decisionAmount = decisionAmount;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(String decidedBy) {
        this.decidedBy = decidedBy;
    }
}
