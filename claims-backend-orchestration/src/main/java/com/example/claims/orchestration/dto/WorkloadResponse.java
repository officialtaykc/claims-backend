package com.example.claims.orchestration.dto;

import java.util.List;

public class WorkloadResponse {

    private List<OfficerWorkload> officers;

    public WorkloadResponse(List<OfficerWorkload> officers) {
        this.officers = officers;
    }

    public List<OfficerWorkload> getOfficers() {
        return officers;
    }

    public static class OfficerWorkload {
        private String officerName;
        private long totalClaims;
        private long newClaims;
        private long inReview;
        private long additionalInfoRequired;
        private long pendingDecision;
        private long approved;
        private long rejected;
        private long settled;

        public OfficerWorkload(String officerName) {
            this.officerName = officerName;
        }

        public String getOfficerName() {
            return officerName;
        }

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
    }
}
