package com.example.claims.orchestration.service;

import com.example.claims.common.enums.ClaimStatus;
import com.example.claims.common.enums.ClaimType;
import com.example.claims.common.enums.InfoRequestStatus;
import com.example.claims.orchestration.dto.*;
import com.example.claims.orchestration.entity.Claim;
import com.example.claims.orchestration.entity.ClaimInformationRequest;
import com.example.claims.orchestration.entity.ClaimStatusHistory;
import com.example.claims.orchestration.exception.InvalidStatusTransitionException;
import com.example.claims.orchestration.exception.ResourceNotFoundException;
import com.example.claims.orchestration.repository.ClaimDocumentRepository;
import com.example.claims.orchestration.repository.ClaimInformationRequestRepository;
import com.example.claims.orchestration.repository.ClaimRepository;
import com.example.claims.orchestration.repository.ClaimStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimStatusHistoryRepository historyRepository;
    private final ClaimInformationRequestRepository infoRequestRepository;
    private final ClaimDocumentRepository documentRepository;

    public ClaimService(ClaimRepository claimRepository,
                        ClaimStatusHistoryRepository historyRepository,
                        ClaimInformationRequestRepository infoRequestRepository,
                        ClaimDocumentRepository documentRepository) {
        this.claimRepository = claimRepository;
        this.historyRepository = historyRepository;
        this.infoRequestRepository = infoRequestRepository;
        this.documentRepository = documentRepository;
    }

    // ---------- Claimant-facing ----------

    public List<ClaimResponse> getClaimsByEmail(String email) {
        return claimRepository.findByClaimantEmailOrderByCreatedAtDesc(email).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClaimResponse getClaimByReference(String claimReference) {
        return toResponse(findClaim(claimReference));
    }

    public List<ClaimStatusHistoryResponse> getHistory(String claimReference) {
        Claim claim = findClaim(claimReference);
        return historyRepository.findByClaimIdOrderByChangedAtAsc(claim.getId()).stream()
                .map(h -> new ClaimStatusHistoryResponse(h.getPreviousStatus(), h.getNewStatus(),
                        h.getChangedBy(), h.getComment(), h.getChangedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ClaimResponse.InfoRequestDto respondToInfoRequest(String claimReference, UUID requestId,
                                                             InfoRequestRespondRequest request) {
        Claim claim = findClaim(claimReference);
        ClaimInformationRequest infoRequest = infoRequestRepository
                .findByIdAndClaimId(requestId, claim.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Information request not found: " + requestId));
        if (infoRequest.getRequestStatus() == InfoRequestStatus.RESPONDED) {
            throw new IllegalArgumentException("Information request has already been responded to");
        }
        infoRequest.setResponseMessage(request.getResponseMessage());
        infoRequest.setRequestStatus(InfoRequestStatus.RESPONDED);
        infoRequest.setRespondedAt(LocalDateTime.now());
        ClaimInformationRequest savedRequest = infoRequestRepository.save(infoRequest);

        List<ClaimInformationRequest> claimRequests =
                infoRequestRepository.findByClaimIdOrderByRequestedAtAsc(claim.getId());
        boolean allRequestsResponded = !claimRequests.isEmpty() && claimRequests.stream()
                .allMatch(item -> item.getRequestStatus() == InfoRequestStatus.RESPONDED);
        if (allRequestsResponded && claim.getStatus() == ClaimStatus.ADDITIONAL_INFO_REQUIRED) {
            claim.setStatus(ClaimStatus.IN_REVIEW);
            claim.setUpdatedAt(LocalDateTime.now());
            claimRepository.save(claim);
            recordHistory(claim, ClaimStatus.ADDITIONAL_INFO_REQUIRED, ClaimStatus.IN_REVIEW,
                    claim.getClaimantEmail(), "All requested information has been provided");
        }
        return ClaimResponse.InfoRequestDto.from(savedRequest);
    }

    // ---------- Staff-facing ----------

    public List<ClaimResponse> searchQueue(ClaimStatus status, ClaimType claimType,
                                           String market, String assignedTo) {
        return claimRepository.searchQueue(status, claimType, market, assignedTo).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClaimResponse assignClaim(String claimReference, AssignRequest request) {
        Claim claim = findClaim(claimReference);
        claim.setAssignedTo(request.getAssignedTo());
        claim.setUpdatedAt(LocalDateTime.now());
        return toResponse(claimRepository.save(claim));
    }

    @Transactional
    public ClaimResponse updateStatus(String claimReference, StatusUpdateRequest request) {
        Claim claim = findClaim(claimReference);
        ClaimStatus target = request.getNewStatus();
        if (!claim.getStatus().canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(
                    "Cannot transition claim " + claimReference + " from " + claim.getStatus()
                            + " to " + target);
        }
        ClaimStatus previous = claim.getStatus();
        claim.setStatus(target);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepository.save(claim);
        recordHistory(claim, previous, target, request.getChangedBy(), request.getComment());
        return toResponse(claim);
    }

    @Transactional
    public ClaimResponse.InfoRequestDto createInfoRequest(String claimReference,
                                                          InfoRequestCreateRequest request) {
        Claim claim = findClaim(claimReference);
        ClaimInformationRequest infoRequest = new ClaimInformationRequest();
        infoRequest.setClaim(claim);
        infoRequest.setRequestMessage(request.getRequestMessage());
        infoRequest.setRequestedBy(request.getRequestedBy());
        infoRequest.setDueDate(request.getDueDate());
        infoRequest.setRequestStatus(InfoRequestStatus.OPEN);
        infoRequest.setRequestedAt(LocalDateTime.now());
        ClaimInformationRequest saved = infoRequestRepository.save(infoRequest);

        if (claim.getStatus() != ClaimStatus.ADDITIONAL_INFO_REQUIRED
                && claim.getStatus().canTransitionTo(ClaimStatus.ADDITIONAL_INFO_REQUIRED)) {
            ClaimStatus previous = claim.getStatus();
            claim.setStatus(ClaimStatus.ADDITIONAL_INFO_REQUIRED);
            claim.setUpdatedAt(LocalDateTime.now());
            claimRepository.save(claim);
            recordHistory(claim, previous, ClaimStatus.ADDITIONAL_INFO_REQUIRED,
                    request.getRequestedBy(), "Additional information requested");
        }
        return ClaimResponse.InfoRequestDto.from(saved);
    }

    @Transactional
    public ClaimResponse makeDecision(String claimReference, DecisionRequest request) {
        Claim claim = findClaim(claimReference);
        ClaimStatus outcome = request.getOutcome();
        if (outcome != ClaimStatus.APPROVED
                && outcome != ClaimStatus.PARTIALLY_APPROVED
                && outcome != ClaimStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Decision outcome must be APPROVED, PARTIALLY_APPROVED or REJECTED");
        }
        if (!claim.getStatus().canTransitionTo(outcome)) {
            throw new InvalidStatusTransitionException(
                    "Cannot record decision on claim " + claimReference + " in status "
                            + claim.getStatus());
        }
        ClaimStatus previous = claim.getStatus();
        claim.setStatus(outcome);
        claim.setDecisionAmount(request.getDecisionAmount());
        claim.setDecisionReason(request.getDecisionReason());
        claim.setDecisionDate(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepository.save(claim);
        recordHistory(claim, previous, outcome, request.getDecidedBy(),
                "Decision: " + request.getDecisionReason());
        return toResponse(claim);
    }

    // ---------- Management dashboard ----------

    public DashboardSummaryResponse getDashboardSummary() {
        DashboardSummaryResponse r = new DashboardSummaryResponse();
        List<Claim> all = claimRepository.findAll();
        r.setTotalClaims(all.size());
        r.setNewClaims(countByStatus(all, ClaimStatus.NEW));
        r.setInReview(countByStatus(all, ClaimStatus.IN_REVIEW));
        r.setAdditionalInfoRequired(countByStatus(all, ClaimStatus.ADDITIONAL_INFO_REQUIRED));
        r.setPendingDecision(countByStatus(all, ClaimStatus.PENDING_DECISION));
        r.setApproved(countByStatus(all, ClaimStatus.APPROVED)
                + countByStatus(all, ClaimStatus.PARTIALLY_APPROVED));
        r.setRejected(countByStatus(all, ClaimStatus.REJECTED));
        r.setSettled(countByStatus(all, ClaimStatus.SETTLED)
                + countByStatus(all, ClaimStatus.SETTLEMENT_PENDING)
                + countByStatus(all, ClaimStatus.CLOSED));

        LocalDate today = LocalDate.now();
        r.setOverdueInfoRequests(infoRequestRepository.findAll().stream()
                .filter(i -> i.getRequestStatus() == InfoRequestStatus.OPEN
                        && i.getDueDate() != null && i.getDueDate().isBefore(today))
                .count());

        r.setEstimatedOutstandingExposure(nullToZero(claimRepository.sumEstimatedLossOutstanding()));
        r.setApprovedExposure(nullToZero(claimRepository.sumApprovedExposure()));

        r.setClaimsByMarket(all.stream()
                .collect(Collectors.groupingBy(Claim::getMarket, Collectors.counting())));
        r.setClaimsByType(all.stream()
                .collect(Collectors.groupingBy(c -> c.getClaimType().name(), Collectors.counting())));
        return r;
    }

    public WorkloadResponse getWorkload() {
        Map<String, List<Claim>> byOfficer = claimRepository.findAll().stream()
                .filter(c -> c.getAssignedTo() != null)
                .collect(Collectors.groupingBy(Claim::getAssignedTo));

        List<WorkloadResponse.OfficerWorkload> officers = byOfficer.entrySet().stream()
                .map(e -> {
                    WorkloadResponse.OfficerWorkload w = new WorkloadResponse.OfficerWorkload(e.getKey());
                    List<Claim> claims = e.getValue();
                    w.setTotalClaims(claims.size());
                    w.setNewClaims(countByStatus(claims, ClaimStatus.NEW));
                    w.setInReview(countByStatus(claims, ClaimStatus.IN_REVIEW));
                    w.setAdditionalInfoRequired(countByStatus(claims, ClaimStatus.ADDITIONAL_INFO_REQUIRED));
                    w.setPendingDecision(countByStatus(claims, ClaimStatus.PENDING_DECISION));
                    w.setApproved(countByStatus(claims, ClaimStatus.APPROVED)
                            + countByStatus(claims, ClaimStatus.PARTIALLY_APPROVED));
                    w.setRejected(countByStatus(claims, ClaimStatus.REJECTED));
                    w.setSettledOrClosed(countByStatus(claims, ClaimStatus.SETTLEMENT_PENDING)
                            + countByStatus(claims, ClaimStatus.SETTLED)
                            + countByStatus(claims, ClaimStatus.CLOSED));
                    return w;
                })
                .sorted(Comparator.comparing(WorkloadResponse.OfficerWorkload::getOfficerName))
                .collect(Collectors.toList());
        return new WorkloadResponse(officers);
    }

    // ---------- helpers ----------

    private Claim findClaim(String claimReference) {
        return claimRepository.findByClaimReference(claimReference)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Claim not found: " + claimReference));
    }

    private ClaimResponse toResponse(Claim claim) {
        return ClaimResponse.from(claim,
                infoRequestRepository.findByClaimIdOrderByRequestedAtAsc(claim.getId()),
                documentRepository.findByClaimId(claim.getId()));
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

    private long countByStatus(List<Claim> claims, ClaimStatus status) {
        return claims.stream().filter(c -> c.getStatus() == status).count();
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
