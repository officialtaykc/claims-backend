package com.example.claims.common.dto;

import com.example.claims.common.enums.ClaimType;
import com.example.claims.common.enums.Priority;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ClaimSubmissionRequest {

    @NotBlank(message = "claimantName is required")
    private String claimantName;

    @NotBlank(message = "claimantEmail is required")
    @Email(message = "claimantEmail must be a valid email")
    private String claimantEmail;

    private String claimantPhone;

    @NotBlank(message = "market is required")
    private String market;

    @NotNull(message = "claimType is required (MOTOR or PROPERTY)")
    private ClaimType claimType;

    @NotBlank(message = "policyNumber is required")
    private String policyNumber;

    @NotNull(message = "incidentDate is required")
    private LocalDate incidentDate;

    private String incidentLocation;
    private String incidentCategory;

    @NotBlank(message = "incidentDescription is required")
    private String incidentDescription;

    private String assetReference;

    @NotNull(message = "estimatedLoss is required")
    @DecimalMin(value = "0.0", message = "estimatedLoss must be >= 0")
    private BigDecimal estimatedLoss;

    @NotBlank(message = "currency is required")
    @Size(min = 3, max = 3, message = "currency must be a 3-letter code")
    private String currency;

    private Priority priority;

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getClaimantEmail() {
        return claimantEmail;
    }

    public void setClaimantEmail(String claimantEmail) {
        this.claimantEmail = claimantEmail;
    }

    public String getClaimantPhone() {
        return claimantPhone;
    }

    public void setClaimantPhone(String claimantPhone) {
        this.claimantPhone = claimantPhone;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public ClaimType getClaimType() {
        return claimType;
    }

    public void setClaimType(ClaimType claimType) {
        this.claimType = claimType;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getIncidentLocation() {
        return incidentLocation;
    }

    public void setIncidentLocation(String incidentLocation) {
        this.incidentLocation = incidentLocation;
    }

    public String getIncidentCategory() {
        return incidentCategory;
    }

    public void setIncidentCategory(String incidentCategory) {
        this.incidentCategory = incidentCategory;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public String getAssetReference() {
        return assetReference;
    }

    public void setAssetReference(String assetReference) {
        this.assetReference = assetReference;
    }

    public BigDecimal getEstimatedLoss() {
        return estimatedLoss;
    }

    public void setEstimatedLoss(BigDecimal estimatedLoss) {
        this.estimatedLoss = estimatedLoss;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
