package com.example.claims.orchestration.service;

import com.example.claims.common.dto.ClaimSubmittedEvent;
import com.example.claims.common.enums.ClaimStatus;
import com.example.claims.orchestration.entity.Claim;
import com.example.claims.orchestration.entity.ClaimStatusHistory;
import com.example.claims.orchestration.entity.MotorClaimDetail;
import com.example.claims.orchestration.entity.PropertyClaimDetail;
import com.example.claims.orchestration.repository.ClaimRepository;
import com.example.claims.orchestration.repository.ClaimStatusHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClaimPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(ClaimPersistenceService.class);

    private final ClaimRepository claimRepository;
    private final ClaimStatusHistoryRepository historyRepository;
    private final JdbcTemplate jdbcTemplate;

    public ClaimPersistenceService(ClaimRepository claimRepository,
                                   ClaimStatusHistoryRepository historyRepository,
                                   JdbcTemplate jdbcTemplate) {
        this.claimRepository = claimRepository;
        this.historyRepository = historyRepository;
        this.jdbcTemplate = jdbcTemplate;
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
        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        if (event.getMotorDetails() != null) {
            claim.setMotorDetails(MotorClaimDetail.from(event.getMotorDetails()));
        }
        if (event.getPropertyDetails() != null) {
            claim.setPropertyDetails(PropertyClaimDetail.from(event.getPropertyDetails()));
        }

        Claim saved = claimRepository.saveAndFlush(claim);

        recordHistory(saved, null, ClaimStatus.NEW, "system",
                "Claim submitted via Kafka event " + event.getEventId());

        String officerEmail = selectNextOfficer();
        saved.setAssignedTo(officerEmail);
        saved.setStatus(ClaimStatus.IN_REVIEW);
        saved.setUpdatedAt(LocalDateTime.now());
        claimRepository.save(saved);
        recordHistory(saved, ClaimStatus.NEW, ClaimStatus.IN_REVIEW, officerEmail,
                "Claim assigned to officer and moved to In Review");

        log.info("Routed claim claimReference={} id={} status=IN_REVIEW assignedTo={}",
                saved.getClaimReference(), saved.getId(), officerEmail);
    }

    private String selectNextOfficer() {
        List<String> officers = jdbcTemplate.queryForList("""
                SELECT email
                FROM claims_staff_accounts
                WHERE UPPER(role) = 'OFFICER' AND is_active = TRUE
                ORDER BY username
                """, String.class);
        if (officers.isEmpty()) {
            throw new IllegalStateException("Cannot route claim: no active claims officers are configured");
        }

        Long nextIndex = jdbcTemplate.queryForObject("""
                SELECT next_index
                FROM claim_assignment_state
                WHERE allocation_key = 'CLAIMS_OFFICERS'
                FOR UPDATE
                """, Long.class);
        if (nextIndex == null) {
            throw new IllegalStateException("Claims officer assignment cursor is not initialized");
        }

        String selectedOfficer = officers.get((int) (nextIndex % officers.size()));
        jdbcTemplate.update("""
                UPDATE claim_assignment_state
                SET next_index = next_index + 1
                WHERE allocation_key = 'CLAIMS_OFFICERS'
                """);
        return selectedOfficer;
    }

    private void recordHistory(Claim claim, ClaimStatus previous, ClaimStatus next,
                               String changedBy, String comment) {
        ClaimStatusHistory history = new ClaimStatusHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previous);
        history.setNewStatus(next);
        history.setChangedBy(changedBy);
        history.setComment(comment);
        history.setChangedAt(LocalDateTime.now());
        historyRepository.save(history);
    }
}
