package com.example.claims.orchestration.entity;

import com.example.claims.common.enums.ClaimStatus;
import com.example.claims.common.enums.ClaimType;
import com.example.claims.common.enums.Priority;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claims", indexes = {
        @Index(name = "idx_claims_claim_reference", columnList = "claim_reference"),
        @Index(name = "idx_claims_claimant_email", columnList = "claimant_email"),
        @Index(name = "idx_claims_status", columnList = "status"),
        @Index(name = "idx_claims_assigned_to", columnList = "assigned_to")
})
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "claim_reference", nullable = false, unique = true)
    private String claimReference;

    @Column(name = "claimant_name", nullable = false)
    private String claimantName;

    @Column(name = "claimant_email", nullable = false)
    private String claimantEmail;

    @Column(name = "claimant_phone")
    private String claimantPhone;

    @Column(name = "market", nullable = false)
    private String market;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false)
    private ClaimType claimType;

    @Column(name = "policy_number", nullable = false)
    private String policyNumber;

    @Column(name = "incident_date", nullable = false)
    private LocalDate incidentDate;

    @Column(name = "incident_location")
    private String incidentLocation;

    @Column(name = "incident_category")
    private String incidentCategory;

    @Column(name = "incident_description", columnDefinition = "TEXT")
    private String incidentDescription;

    @Column(name = "asset_reference")
    private String assetReference;

    @Column(name = "estimated_loss", precision = 15, scale = 2)
    private BigDecimal estimatedLoss;

    @Column(name = "currency", length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "decision_amount", precision = 15, scale = 2)
    private BigDecimal decisionAmount;

    @Column(name = "decision_reason", columnDefinition = "TEXT")
    private String decisionReason;

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private MotorClaimDetail motorDetails;

    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private PropertyClaimDetail propertyDetails;

    public MotorClaimDetail getMotorDetails() {
        return motorDetails;
    }

    public void setMotorDetails(MotorClaimDetail motorDetails) {
        this.motorDetails = motorDetails;
        if (motorDetails != null) {
            motorDetails.setClaim(this);
        }
    }

    public PropertyClaimDetail getPropertyDetails() {
        return propertyDetails;
    }

    public void setPropertyDetails(PropertyClaimDetail propertyDetails) {
        this.propertyDetails = propertyDetails;
        if (propertyDetails != null) {
            propertyDetails.setClaim(this);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
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

    public LocalDateTime getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(LocalDateTime decisionDate) {
        this.decisionDate = decisionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
