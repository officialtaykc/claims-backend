package com.example.claims.orchestration.dto;

import com.example.claims.common.enums.ClaimStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SubmitForDecisionRequest {

    @NotBlank(message = "submittedBy is required")
    private String submittedBy;

    @NotNull(message = "recommendationOutcome is required")
    private ClaimStatus recommendationOutcome;

    private BigDecimal recommendationAmount;

    @NotBlank(message = "recommendationReason is required")
    private String recommendationReason;

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public ClaimStatus getRecommendationOutcome() {
        return recommendationOutcome;
    }

    public void setRecommendationOutcome(ClaimStatus recommendationOutcome) {
        this.recommendationOutcome = recommendationOutcome;
    }

    public BigDecimal getRecommendationAmount() {
        return recommendationAmount;
    }

    public void setRecommendationAmount(BigDecimal recommendationAmount) {
        this.recommendationAmount = recommendationAmount;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }
}
