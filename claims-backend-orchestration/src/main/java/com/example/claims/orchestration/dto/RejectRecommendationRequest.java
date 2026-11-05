package com.example.claims.orchestration.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectRecommendationRequest {

    @NotBlank(message = "rejectedBy is required")
    private String rejectedBy;

    @NotBlank(message = "reason is required")
    private String reason;

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
