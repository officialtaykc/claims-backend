package com.example.claims.api.service;

import com.example.claims.common.dto.ClaimSubmissionRequest;
import com.example.claims.common.dto.ClaimSubmittedEvent;
import com.example.claims.common.dto.ClaimSubmittedResponse;
import com.example.claims.common.enums.ClaimStatus;
import com.example.claims.common.enums.Priority;
import com.example.claims.api.kafka.ClaimEventProducer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ClaimSubmissionService {

    private final ClaimEventProducer claimEventProducer;

    public ClaimSubmissionService(ClaimEventProducer claimEventProducer) {
        this.claimEventProducer = claimEventProducer;
    }

    public ClaimSubmittedResponse submitClaim(ClaimSubmissionRequest request) {
        String claimReference = generateClaimReference();
        UUID eventId = UUID.randomUUID();

        ClaimSubmittedEvent event = new ClaimSubmittedEvent();
        event.setEventId(eventId);
        event.setClaimReference(claimReference);
        event.setClaimantName(request.getClaimantName());
        event.setClaimantEmail(request.getClaimantEmail());
        event.setClaimantPhone(request.getClaimantPhone());
        event.setMarket(request.getMarket());
        event.setClaimType(request.getClaimType());
        event.setPolicyNumber(request.getPolicyNumber());
        event.setIncidentDate(request.getIncidentDate());
        event.setIncidentLocation(request.getIncidentLocation());
        event.setIncidentCategory(request.getIncidentCategory());
        event.setIncidentDescription(request.getIncidentDescription());
        event.setAssetReference(request.getAssetReference());
        event.setEstimatedLoss(request.getEstimatedLoss());
        event.setCurrency(request.getCurrency());
        event.setPriority(request.getPriority() == null ? Priority.NORMAL : request.getPriority());
        event.setMotorDetails(request.getMotorDetails());
        event.setPropertyDetails(request.getPropertyDetails());

        claimEventProducer.publish(event);

        return new ClaimSubmittedResponse(claimReference, eventId, ClaimStatus.NEW,
                "Claim submitted successfully. It will appear in the system shortly.");
    }

    private String generateClaimReference() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return "CLM-" + datePart + "-" + UUID.randomUUID();
    }
}
