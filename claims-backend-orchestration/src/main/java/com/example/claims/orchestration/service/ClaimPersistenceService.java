package com.example.claims.orchestration.service;

import com.example.claims.common.dto.ClaimSubmittedEvent;
import com.example.claims.common.enums.ClaimStatus;
import com.example.claims.orchestration.config.ClaimsProperties;
import com.example.claims.orchestration.entity.Claim;
import com.example.claims.orchestration.entity.ClaimStatusHistory;
import com.example.claims.orchestration.entity.MotorClaimDetail;
import com.example.claims.orchestration.entity.PropertyClaimDetail;
import com.example.claims.orchestration.repository.ClaimRepository;
import com.example.claims.orchestration.repository.ClaimStatusHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClaimPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(ClaimPersistenceService.class);

    private static final Set<ClaimStatus> ACTIVE_STATUSES = EnumSet.of(
            ClaimStatus.NEW,
            ClaimStatus.IN_REVIEW,
            ClaimStatus.ADDITIONAL_INFO_REQUIRED,
            ClaimStatus.PENDING_DECISION,
            ClaimStatus.APPROVED,
            ClaimStatus.PARTIALLY_APPROVED,
            ClaimStatus.SETTLEMENT_PENDING);

    private final ClaimRepository claimRepository;
    private final ClaimStatusHistoryRepository historyRepository;
    private final List<String> officers;

    public ClaimPersistenceService(ClaimRepository claimRepository,
                                   ClaimStatusHistoryRepository historyRepository,
                                   ClaimsProperties claimsProperties) {
        this.claimRepository = claimRepository;
        this.historyRepository = historyRepository;
        this.officers = List.copyOf(claimsProperties.getOfficers());
    }

    @Transactional
    public void persistClaim(ClaimSubmittedEvent event) {
        if (claimRepository.existsByEventId(event.getEventId())) {
            log.warn("Duplicate claim event ignored eventId={} claimReference={}",
                    event.getEventId(), event.getClaimReference());
            return;
        }

        Claim claim = new Claim();
        claim.setEventId(event.getEventId());
        claim.setClaimReference(event.getClaimReference());
        claim.setClaimantName(event.getClaimantName());
        claim.setClaimantEmail(event.getClaimantEmail());
        claim.setClaimantPhone(event.getClaimantPhone());
        claim.setMarket(event.getMarket());
        claim.setClaimType(event.getClaimType());
        claim.setPolicyNumber(event.getPolicyNumber());
        claim.setIncidentDate(event.getIncidentDate());
        claim.setIncidentLocation(event.getIncidentLocation());
        claim.setIncidentCategory(event.getIncidentCategory());
        claim.setIncidentDescription(event.getIncidentDescription());
        claim.setAssetReference(event.getAssetReference());
        claim.setEstimatedLoss(event.getEstimatedLoss());
        claim.setCurrency(event.getCurrency());
        claim.setPriority(event.getPriority());
        claim.setStatus(ClaimStatus.NEW);
        claim.setAssignedTo(selectOfficer());
        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        if (event.getMotorDetails() != null) {
            claim.setMotorDetails(MotorClaimDetail.from(event.getMotorDetails()));
        }
        if (event.getPropertyDetails() != null) {
            claim.setPropertyDetails(PropertyClaimDetail.from(event.getPropertyDetails()));
        }

        Claim saved = claimRepository.save(claim);

        ClaimStatusHistory history = new ClaimStatusHistory();
        history.setClaim(saved);
        history.setPreviousStatus(null);
        history.setNewStatus(ClaimStatus.NEW);
        history.setChangedBy("system");
        history.setComment("Claim submitted via Kafka event " + event.getEventId()
                + "; auto-assigned to " + saved.getAssignedTo());
        history.setChangedAt(LocalDateTime.now());
        historyRepository.save(history);

        log.info("Persisted claim claimReference={} id={} status=NEW assignedTo={}",
                saved.getClaimReference(), saved.getId(), saved.getAssignedTo());
    }

    /**
     * Workbasket allocation: pick the configured officer with the fewest
     * currently active claims. Ties are broken alphabetically for determinism.
     */
    private String selectOfficer() {
        if (officers == null || officers.isEmpty()) {
            return null;
        }
        Map<String, Long> activeCounts = claimRepository.findAll().stream()
                .filter(c -> c.getAssignedTo() != null && ACTIVE_STATUSES.contains(c.getStatus()))
                .collect(Collectors.groupingBy(Claim::getAssignedTo, Collectors.counting()));

        return officers.stream()
                .min((a, b) -> {
                    int cmp = Long.compare(
                            activeCounts.getOrDefault(a, 0L),
                            activeCounts.getOrDefault(b, 0L));
                    return cmp != 0 ? cmp : a.compareTo(b);
                })
                .orElse(null);
    }
}
