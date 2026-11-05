package com.example.claims.common.dto;

import com.example.claims.common.enums.ClaimType;
import com.example.claims.common.enums.Priority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ClaimSubmittedEvent {

    private UUID eventId;
    private String claimReference;
    private String claimantName;
    private String claimantEmail;
    private String claimantPhone;
    private String market;
    private ClaimType claimType;
    private String policyNumber;
    private LocalDate incidentDate;
    private String incidentLocation;
    private String incidentCategory;
    private String incidentDescription;
    private String assetReference;
    private BigDecimal estimatedLoss;
    private String currency;
    private Priority priority;
    private MotorClaimDetails motorDetails;
    private PropertyClaimDetails propertyDetails;

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getClaimReference() {
        return claimReference;
    }

    public void setClaimReference(String claimReference) {
        this.claimReference = claimReference;
    }

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

    public MotorClaimDetails getMotorDetails() {
        return motorDetails;
    }

    public void setMotorDetails(MotorClaimDetails motorDetails) {
        this.motorDetails = motorDetails;
    }

    public PropertyClaimDetails getPropertyDetails() {
        return propertyDetails;
    }

    public void setPropertyDetails(PropertyClaimDetails propertyDetails) {
        this.propertyDetails = propertyDetails;
    }
}
