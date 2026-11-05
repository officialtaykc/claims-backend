package com.example.claims.common.dto;

import com.example.claims.common.enums.ClaimStatus;

import java.util.UUID;

public class ClaimSubmittedResponse {

    private String claimReference;
    private UUID eventId;
    private ClaimStatus status;
    private String message;

    public ClaimSubmittedResponse(String claimReference, UUID eventId, ClaimStatus status, String message) {
        this.claimReference = claimReference;
        this.eventId = eventId;
        this.status = status;
        this.message = message;
    }

    public String getClaimReference() {
        return claimReference;
    }

    public UUID getEventId() {
        return eventId;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
