package com.example.claims.orchestration.dto;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardSummaryResponse {

    private long totalClaims;
    private long newClaims;
    private long inReview;
    private long additionalInfoRequired;
    private long pendingDecision;
    private long approved;
    private long rejected;
    private long settled;
    private long overdueInfoRequests;
    private BigDecimal estimatedOutstandingExposure;
    private BigDecimal approvedExposure;
    private Map<String, Long> claimsByMarket;
    private Map<String, Long> claimsByType;

    public long getTotalClaims() {
        return totalClaims;
    }

    public void setTotalClaims(long totalClaims) {
        this.totalClaims = totalClaims;
    }

    public long getNewClaims() {
        return newClaims;
    }

    public void setNewClaims(long newClaims) {
        this.newClaims = newClaims;
    }

    public long getInReview() {
        return inReview;
    }

    public void setInReview(long inReview) {
        this.inReview = inReview;
    }

    public long getAdditionalInfoRequired() {
        return additionalInfoRequired;
    }

    public void setAdditionalInfoRequired(long additionalInfoRequired) {
        this.additionalInfoRequired = additionalInfoRequired;
    }

    public long getPendingDecision() {
        return pendingDecision;
    }

    public void setPendingDecision(long pendingDecision) {
        this.pendingDecision = pendingDecision;
    }

    public long getApproved() {
        return approved;
    }

    public void setApproved(long approved) {
        this.approved = approved;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getSettled() {
        return settled;
    }

    public void setSettled(long settled) {
        this.settled = settled;
    }

    public long getOverdueInfoRequests() {
        return overdueInfoRequests;
    }

    public void setOverdueInfoRequests(long overdueInfoRequests) {
        this.overdueInfoRequests = overdueInfoRequests;
    }

    public BigDecimal getEstimatedOutstandingExposure() {
        return estimatedOutstandingExposure;
    }

    public void setEstimatedOutstandingExposure(BigDecimal estimatedOutstandingExposure) {
        this.estimatedOutstandingExposure = estimatedOutstandingExposure;
    }

    public BigDecimal getApprovedExposure() {
        return approvedExposure;
    }

    public void setApprovedExposure(BigDecimal approvedExposure) {
        this.approvedExposure = approvedExposure;
    }

    public Map<String, Long> getClaimsByMarket() {
        return claimsByMarket;
    }

    public void setClaimsByMarket(Map<String, Long> claimsByMarket) {
        this.claimsByMarket = claimsByMarket;
    }

    public Map<String, Long> getClaimsByType() {
        return claimsByType;
    }

    public void setClaimsByType(Map<String, Long> claimsByType) {
        this.claimsByType = claimsByType;
    }
}
