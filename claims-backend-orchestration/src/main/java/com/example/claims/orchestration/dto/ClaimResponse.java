package com.example.claims.orchestration.dto;

import com.example.claims.common.enums.InfoRequestStatus;
import com.example.claims.orchestration.entity.Claim;
import com.example.claims.orchestration.entity.ClaimInformationRequest;
import com.example.claims.orchestration.entity.MotorClaimDetail;
import com.example.claims.orchestration.entity.PropertyClaimDetail;
import com.example.claims.common.dto.MotorClaimDetails;
import com.example.claims.common.dto.PropertyClaimDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClaimResponse {

    private UUID id;
    private String claimReference;
    private String claimantName;
    private String claimantEmail;
    private String claimantPhone;
    private String market;
    private String claimType;
    private String policyNumber;
    private LocalDate incidentDate;
    private String incidentLocation;
    private String incidentCategory;
    private String incidentDescription;
    private String assetReference;
    private java.math.BigDecimal estimatedLoss;
    private String currency;
    private String status;
    private String priority;
    private String assignedTo;
    private String recommendationOutcome;
    private java.math.BigDecimal recommendationAmount;
    private String recommendationReason;
    private String recommendationBy;
    private LocalDateTime recommendationDate;
    private java.math.BigDecimal decisionAmount;
    private String decisionReason;
    private LocalDateTime decisionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<InfoRequestDto> informationRequests;
    private List<DocumentDto> documents;
    private MotorClaimDetails motorDetails;
    private PropertyClaimDetails propertyDetails;

    public static ClaimResponse from(Claim claim,
                                     List<ClaimInformationRequest> infoRequests,
                                     List<com.example.claims.orchestration.entity.ClaimDocument> documents) {
        ClaimResponse r = new ClaimResponse();
        r.id = claim.getId();
        r.claimReference = claim.getClaimReference();
        r.claimantName = claim.getClaimantName();
        r.claimantEmail = claim.getClaimantEmail();
        r.claimantPhone = claim.getClaimantPhone();
        r.market = claim.getMarket();
        r.claimType = claim.getClaimType().name();
        r.policyNumber = claim.getPolicyNumber();
        r.incidentDate = claim.getIncidentDate();
        r.incidentLocation = claim.getIncidentLocation();
        r.incidentCategory = claim.getIncidentCategory();
        r.incidentDescription = claim.getIncidentDescription();
        r.assetReference = claim.getAssetReference();
        r.estimatedLoss = claim.getEstimatedLoss();
        r.currency = claim.getCurrency();
        r.status = claim.getStatus().name();
        r.priority = claim.getPriority().name();
        r.assignedTo = claim.getAssignedTo();
        r.decisionAmount = claim.getDecisionAmount();
        r.decisionReason = claim.getDecisionReason();
        r.decisionDate = claim.getDecisionDate();
        r.createdAt = claim.getCreatedAt();
        r.updatedAt = claim.getUpdatedAt();
        r.informationRequests = infoRequests.stream().map(InfoRequestDto::from).collect(Collectors.toList());
        r.documents = documents.stream().map(DocumentDto::from).collect(Collectors.toList());
        r.motorDetails = motorDetailsFrom(claim.getMotorDetails());
        r.propertyDetails = propertyDetailsFrom(claim.getPropertyDetails());
        return r;
    }

    public static ClaimResponse fromStaff(Claim claim,
                                          List<ClaimInformationRequest> infoRequests,
                                          List<com.example.claims.orchestration.entity.ClaimDocument> documents) {
        ClaimResponse response = from(claim, infoRequests, documents);
        response.recommendationOutcome = claim.getRecommendationOutcome() == null
                ? null : claim.getRecommendationOutcome().name();
        response.recommendationAmount = claim.getRecommendationAmount();
        response.recommendationReason = claim.getRecommendationReason();
        response.recommendationBy = claim.getRecommendationBy();
        response.recommendationDate = claim.getRecommendationDate();
        return response;
    }

    private static MotorClaimDetails motorDetailsFrom(MotorClaimDetail entity) {
        if (entity == null) {
            return null;
        }
        MotorClaimDetails details = new MotorClaimDetails();
        details.setVehicleRegistration(entity.getVehicleRegistration());
        details.setVehicleModel(entity.getVehicleModel());
        details.setDriverName(entity.getDriverName());
        details.setDriverLicenceNumber(entity.getDriverLicenceNumber());
        details.setThirdPartyInvolved(entity.getThirdPartyInvolved());
        details.setPoliceReportNumber(entity.getPoliceReportNumber());
        details.setRepairWorkshop(entity.getRepairWorkshop());
        details.setClaimCategory(entity.getClaimCategory());
        return details;
    }

    private static PropertyClaimDetails propertyDetailsFrom(PropertyClaimDetail entity) {
        if (entity == null) {
            return null;
        }
        PropertyClaimDetails details = new PropertyClaimDetails();
        details.setPropertyType(entity.getPropertyType());
        details.setPropertyAddress(entity.getPropertyAddress());
        details.setAffectedAreas(entity.getAffectedAreas());
        details.setDamageToBuilding(entity.getDamageToBuilding());
        details.setDamageToContents(entity.getDamageToContents());
        details.setCauseOfLoss(entity.getCauseOfLoss());
        details.setEstimatedRepairCost(entity.getEstimatedRepairCost());
        details.setEstimatedContentsLoss(entity.getEstimatedContentsLoss());
        return details;
    }

    public UUID getId() {
        return id;
    }

    public String getClaimReference() {
        return claimReference;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public String getClaimantEmail() {
        return claimantEmail;
    }

    public String getClaimantPhone() {
        return claimantPhone;
    }

    public String getMarket() {
        return market;
    }

    public String getClaimType() {
        return claimType;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public String getIncidentLocation() {
        return incidentLocation;
    }

    public String getIncidentCategory() {
        return incidentCategory;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public String getAssetReference() {
        return assetReference;
    }

    public java.math.BigDecimal getEstimatedLoss() {
        return estimatedLoss;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getRecommendationOutcome() {
        return recommendationOutcome;
    }

    public java.math.BigDecimal getRecommendationAmount() {
        return recommendationAmount;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public String getRecommendationBy() {
        return recommendationBy;
    }

    public LocalDateTime getRecommendationDate() {
        return recommendationDate;
    }

    public java.math.BigDecimal getDecisionAmount() {
        return decisionAmount;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public LocalDateTime getDecisionDate() {
        return decisionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<InfoRequestDto> getInformationRequests() {
        return informationRequests;
    }

    public List<DocumentDto> getDocuments() {
        return documents;
    }

    public MotorClaimDetails getMotorDetails() {
        return motorDetails;
    }

    public PropertyClaimDetails getPropertyDetails() {
        return propertyDetails;
    }

    public static class InfoRequestDto {
        private UUID id;
        private String requestMessage;
        private String requestedBy;
        private LocalDate dueDate;
        private InfoRequestStatus requestStatus;
        private String responseMessage;
        private LocalDateTime requestedAt;
        private LocalDateTime respondedAt;

        public static InfoRequestDto from(ClaimInformationRequest r) {
            InfoRequestDto d = new InfoRequestDto();
            d.id = r.getId();
            d.requestMessage = r.getRequestMessage();
            d.requestedBy = r.getRequestedBy();
            d.dueDate = r.getDueDate();
            d.requestStatus = r.getRequestStatus();
            d.responseMessage = r.getResponseMessage();
            d.requestedAt = r.getRequestedAt();
            d.respondedAt = r.getRespondedAt();
            return d;
        }

        public UUID getId() {
            return id;
        }

        public String getRequestMessage() {
            return requestMessage;
        }

        public String getRequestedBy() {
            return requestedBy;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public InfoRequestStatus getRequestStatus() {
            return requestStatus;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public LocalDateTime getRequestedAt() {
            return requestedAt;
        }

        public LocalDateTime getRespondedAt() {
            return respondedAt;
        }
    }

    public static class DocumentDto {
        private UUID id;
        private String fileName;
        private String contentType;
        private String uploadedBy;
        private LocalDateTime uploadedAt;
        private UUID informationRequestId;

        public static DocumentDto from(com.example.claims.orchestration.entity.ClaimDocument d) {
            DocumentDto dto = new DocumentDto();
            dto.id = d.getId();
            dto.fileName = d.getFileName();
            dto.contentType = d.getContentType();
            dto.uploadedBy = d.getUploadedBy();
            dto.uploadedAt = d.getUploadedAt();
            dto.informationRequestId = d.getInformationRequest() == null
                    ? null : d.getInformationRequest().getId();
            return dto;
        }

        public UUID getId() {
            return id;
        }

        public String getFileName() {
            return fileName;
        }

        public String getContentType() {
            return contentType;
        }

        public String getUploadedBy() {
            return uploadedBy;
        }

        public LocalDateTime getUploadedAt() {
            return uploadedAt;
        }

        public UUID getInformationRequestId() {
            return informationRequestId;
        }
    }
}
