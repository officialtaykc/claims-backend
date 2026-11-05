package com.example.claims.orchestration.dto;

import com.example.claims.common.enums.ClaimStatus;

import java.time.LocalDateTime;

public class ClaimStatusHistoryResponse {

    private String previousStatus;
    private String newStatus;
    private String changedBy;
    private String comment;
    private LocalDateTime changedAt;

    public ClaimStatusHistoryResponse(ClaimStatus previousStatus, ClaimStatus newStatus,
                                      String changedBy, String comment, LocalDateTime changedAt) {
        this.previousStatus = previousStatus == null ? null : previousStatus.name();
        this.newStatus = newStatus.name();
        this.changedBy = changedBy;
        this.comment = comment;
        this.changedAt = changedAt;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
