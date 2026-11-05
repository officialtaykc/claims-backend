package com.example.claims.orchestration.service;

import com.example.claims.common.dto.PaymentProcessedEvent;
import com.example.claims.common.dto.PaymentRequestedEvent;
import com.example.claims.common.enums.ClaimStatus;
import com.example.claims.common.enums.ClaimType;
import com.example.claims.common.enums.InfoRequestStatus;
import com.example.claims.orchestration.dto.*;
import com.example.claims.orchestration.entity.Claim;
import com.example.claims.orchestration.entity.ClaimAttachmentContent;
import com.example.claims.orchestration.entity.ClaimDocument;
import com.example.claims.orchestration.entity.ClaimInformationRequest;
import com.example.claims.orchestration.entity.ClaimStatusHistory;
import com.example.claims.orchestration.exception.InvalidStatusTransitionException;
import com.example.claims.orchestration.exception.ResourceNotFoundException;
import com.example.claims.orchestration.repository.ClaimAttachmentContentRepository;
import com.example.claims.orchestration.repository.ClaimDocumentRepository;
import com.example.claims.orchestration.repository.ClaimInformationRequestRepository;
import com.example.claims.orchestration.repository.ClaimRepository;
import com.example.claims.orchestration.repository.ClaimStatusHistoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    private static final int MAX_ATTACHMENT_COUNT = 5;
    private static final long MAX_ATTACHMENT_SIZE = 10L * 1024 * 1024;
    private static final long MAX_TOTAL_ATTACHMENT_SIZE = 20L * 1024 * 1024;
    private static final List<String> ALLOWED_ATTACHMENT_TYPES = List.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "text/plain",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    private final ClaimRepository claimRepository;
    private final ClaimStatusHistoryRepository historyRepository;
    private final ClaimInformationRequestRepository infoRequestRepository;
    private final ClaimDocumentRepository documentRepository;
    private final ClaimAttachmentContentRepository attachmentContentRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public ClaimService(ClaimRepository claimRepository,
                        ClaimStatusHistoryRepository historyRepository,
                        ClaimInformationRequestRepository infoRequestRepository,
                        ClaimDocumentRepository documentRepository,
                        ClaimAttachmentContentRepository attachmentContentRepository,
                        JdbcTemplate jdbcTemplate,
                        ApplicationEventPublisher eventPublisher) {
        this.claimRepository = claimRepository;
        this.historyRepository = historyRepository;
        this.infoRequestRepository = infoRequestRepository;
        this.documentRepository = documentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.eventPublisher = eventPublisher;
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

    public ClaimResponse getStaffClaimByReference(String claimReference) {
        return toStaffResponse(findClaim(claimReference));
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
                                                             InfoRequestRespondRequest request,
                                                             List<MultipartFile> files) {
        Claim claim = findClaim(claimReference);
        ClaimInformationRequest infoRequest = infoRequestRepository
                .findByIdAndClaimId(requestId, claim.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Information request not found: " + requestId));
        if (infoRequest.getRequestStatus() == InfoRequestStatus.RESPONDED) {
            throw new IllegalArgumentException("Information request has already been responded to");
        }
        infoRequest.setResponseMessage(request.getResponseMessage() == null
                || request.getResponseMessage().isBlank() ? null : request.getResponseMessage().trim());
        infoRequest.setRequestStatus(InfoRequestStatus.RESPONDED);
        infoRequest.setRespondedAt(LocalDateTime.now());
        ClaimInformationRequest savedRequest = infoRequestRepository.save(infoRequest);
        List<ClaimDocument> uploadedDocuments =
                saveDocuments(claim, savedRequest, files, claim.getClaimantEmail());
        if (savedRequest.getResponseMessage() == null && uploadedDocuments.isEmpty()) {
            throw new IllegalArgumentException("Provide a response message or attach at least one file");
        }

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
        if (target == ClaimStatus.PENDING_DECISION
                || target == ClaimStatus.APPROVED
                || target == ClaimStatus.PARTIALLY_APPROVED
                || target == ClaimStatus.REJECTED
                || target == ClaimStatus.SETTLEMENT_PENDING
                || target == ClaimStatus.SETTLED) {
            throw new InvalidStatusTransitionException(
                    "Status " + target + " must be advanced through its designated workflow action");
        }
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
                                                          InfoRequestCreateRequest request,
                                                          List<MultipartFile> files) {
        Claim claim = findClaim(claimReference);
        if (claim.getStatus() != ClaimStatus.IN_REVIEW
                && claim.getStatus() != ClaimStatus.ADDITIONAL_INFO_REQUIRED) {
            throw new InvalidStatusTransitionException(
                    "Additional information can only be requested while a claim is under review");
        }
        requireActiveStaffMember(request.getRequestedBy(), "OFFICER");
        if (claim.getAssignedTo() == null
                || !claim.getAssignedTo().equalsIgnoreCase(request.getRequestedBy().trim())) {
            throw new IllegalArgumentException(
                    "Only the assigned claims officer can request additional information");
        }
        ClaimInformationRequest infoRequest = new ClaimInformationRequest();
        infoRequest.setClaim(claim);
        infoRequest.setRequestMessage(request.getRequestMessage());
        infoRequest.setRequestedBy(request.getRequestedBy());
        infoRequest.setDueDate(request.getDueDate());
        infoRequest.setRequestStatus(InfoRequestStatus.OPEN);
        infoRequest.setRequestedAt(LocalDateTime.now());
        ClaimInformationRequest saved = infoRequestRepository.save(infoRequest);
        saveDocuments(claim, saved, files, request.getRequestedBy());

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
    public List<ClaimResponse.DocumentDto> uploadClaimDocuments(
            String claimReference, String uploadedBy, List<MultipartFile> files) {
        Claim claim = findClaim(claimReference);
        if (uploadedBy == null || !claim.getClaimantEmail().equalsIgnoreCase(uploadedBy.trim())) {
            throw new IllegalArgumentException("Only the claimant can upload claim documents");
        }
        List<ClaimDocument> saved = saveDocuments(claim, null, files, uploadedBy);
        return saved.stream().map(ClaimResponse.DocumentDto::from).toList();
    }

    @Transactional(readOnly = true)
    public DocumentContent getDocumentContent(String claimReference, UUID documentId) {
        Claim claim = findClaim(claimReference);
        ClaimDocument document = documentRepository.findByIdAndClaimId(documentId, claim.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found: " + documentId));
        ClaimAttachmentContent attachmentContent = attachmentContentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document content is unavailable: " + documentId));
        return new DocumentContent(document.getFileName(), document.getContentType(),
                attachmentContent.getContent());
    }

    @Transactional
    public ClaimResponse submitForDecision(String claimReference, SubmitForDecisionRequest request) {
        Claim claim = findClaim(claimReference);
        if (claim.getStatus() != ClaimStatus.IN_REVIEW) {
            throw new InvalidStatusTransitionException(
                    "Only claims in IN_REVIEW can be submitted for manager review");
        }
        ClaimStatus recommendation = request.getRecommendationOutcome();
        if (recommendation != ClaimStatus.APPROVED
                && recommendation != ClaimStatus.PARTIALLY_APPROVED
                && recommendation != ClaimStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Recommendation outcome must be APPROVED, PARTIALLY_APPROVED or REJECTED");
        }
        if (recommendation != ClaimStatus.REJECTED && request.getRecommendationAmount() == null) {
            throw new IllegalArgumentException("Recommendation amount is required for approval");
        }
        if (request.getRecommendationAmount() != null
                && request.getRecommendationAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Recommendation amount cannot be negative");
        }
        requireActiveStaffMember(request.getSubmittedBy(), "OFFICER");
        if (claim.getAssignedTo() == null
                || !claim.getAssignedTo().equalsIgnoreCase(request.getSubmittedBy().trim())) {
            throw new IllegalArgumentException("Only the assigned claims officer can submit this claim");
        }
        boolean hasOpenRequests = infoRequestRepository.findByClaimIdOrderByRequestedAtAsc(claim.getId())
                .stream().anyMatch(item -> item.getRequestStatus() == InfoRequestStatus.OPEN);
        if (hasOpenRequests) {
            throw new IllegalStateException(
                    "Claim cannot be submitted while additional information requests remain open");
        }

        claim.setRecommendationOutcome(recommendation);
        claim.setRecommendationAmount(request.getRecommendationAmount());
        claim.setRecommendationReason(request.getRecommendationReason().trim());
        claim.setRecommendationBy(request.getSubmittedBy().trim());
        claim.setRecommendationDate(LocalDateTime.now());
        claim.setStatus(ClaimStatus.PENDING_DECISION);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepository.save(claim);
        recordHistory(claim, ClaimStatus.IN_REVIEW, ClaimStatus.PENDING_DECISION,
                request.getSubmittedBy(), "Submitted recommendation: " + recommendation);
        return toResponse(claim);
    }

    @Transactional
    public ClaimResponse rejectRecommendation(String claimReference, RejectRecommendationRequest request) {
        Claim claim = findClaim(claimReference);
        if (claim.getStatus() != ClaimStatus.PENDING_DECISION
                || claim.getRecommendationOutcome() == null) {
            throw new InvalidStatusTransitionException(
                    "Only a claim with an officer recommendation awaiting review can be returned");
        }
        requireActiveStaffMember(request.getRejectedBy(), "MANAGER");
        if (claim.getAssignedTo() != null
                && claim.getAssignedTo().equalsIgnoreCase(request.getRejectedBy().trim())) {
            throw new IllegalArgumentException("The assigned claims officer cannot review their own recommendation");
        }

        claim.setStatus(ClaimStatus.IN_REVIEW);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepository.save(claim);
        recordHistory(claim, ClaimStatus.PENDING_DECISION, ClaimStatus.IN_REVIEW,
                request.getRejectedBy(), "Recommendation returned for reassessment: "
                        + request.getReason().trim());
        return toResponse(claim);
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
        requireActiveStaffMember(request.getDecidedBy(), "MANAGER");
        if (claim.getAssignedTo() != null
                && claim.getAssignedTo().equalsIgnoreCase(request.getDecidedBy().trim())) {
            throw new IllegalArgumentException("The assigned claims officer cannot approve their own claim");
        }
        if (outcome != ClaimStatus.REJECTED && request.getDecisionAmount() == null) {
            throw new IllegalArgumentException("Decision amount is required for approval");
        }
        if (request.getDecisionAmount() != null
                && request.getDecisionAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Decision amount cannot be negative");
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
        if (outcome == ClaimStatus.APPROVED || outcome == ClaimStatus.PARTIALLY_APPROVED) {
            claim.setStatus(ClaimStatus.SETTLEMENT_PENDING);
            claim.setUpdatedAt(LocalDateTime.now());
            claimRepository.save(claim);
            recordHistory(claim, outcome, ClaimStatus.SETTLEMENT_PENDING,
                    "system", "Payment request submitted to payment team");

            PaymentRequestedEvent paymentRequest = new PaymentRequestedEvent();
            paymentRequest.setEventId(UUID.randomUUID());
            paymentRequest.setClaimReference(claim.getClaimReference());
            paymentRequest.setAmount(claim.getDecisionAmount());
            paymentRequest.setCurrency(claim.getCurrency());
            eventPublisher.publishEvent(paymentRequest);
        }
        return toResponse(claim);
    }

    @Transactional
    public void markPaymentSettled(PaymentProcessedEvent event) {
        Claim claim = findClaim(event.getClaimReference());
        if (claim.getStatus() == ClaimStatus.SETTLED) {
            return;
        }
        if (claim.getStatus() != ClaimStatus.SETTLEMENT_PENDING) {
            throw new InvalidStatusTransitionException(
                    "Payment completion received for claim " + claim.getClaimReference()
                            + " in status " + claim.getStatus());
        }
        claim.setStatus(ClaimStatus.SETTLED);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepository.save(claim);
        recordHistory(claim, ClaimStatus.SETTLEMENT_PENDING, ClaimStatus.SETTLED,
                "payment-team", "Payment processed: " + event.getPaymentReference());
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
        r.setSettled(countByStatus(all, ClaimStatus.SETTLED));

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
                    w.setSettled(countByStatus(claims, ClaimStatus.SETTLED));
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

    private ClaimResponse toStaffResponse(Claim claim) {
        return ClaimResponse.fromStaff(claim,
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

    private List<ClaimDocument> saveDocuments(Claim claim, ClaimInformationRequest infoRequest,
                                               List<MultipartFile> files, String uploadedBy) {
        if (files == null || files.stream().allMatch(MultipartFile::isEmpty)) {
            return List.of();
        }
        if (files.size() > MAX_ATTACHMENT_COUNT) {
            throw new IllegalArgumentException("A maximum of " + MAX_ATTACHMENT_COUNT
                    + " files can be uploaded at once");
        }
        long totalSize = 0;
        List<ClaimDocument> documents = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            if (file.getSize() > MAX_ATTACHMENT_SIZE) {
                throw new IllegalArgumentException("Each attachment must be 10 MB or smaller");
            }
            totalSize += file.getSize();
            if (totalSize > MAX_TOTAL_ATTACHMENT_SIZE) {
                throw new IllegalArgumentException("Attachments must total 20 MB or less");
            }
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_ATTACHMENT_TYPES.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException(
                        "Unsupported attachment type. Upload PDF, JPEG, PNG, text or Word files");
            }

            ClaimDocument document = new ClaimDocument();
            document.setClaim(claim);
            document.setInformationRequest(infoRequest);
            document.setFileName(safeFileName(file.getOriginalFilename()));
            document.setContentType(contentType.toLowerCase());
            document.setUploadedBy(uploadedBy);
            document.setUploadedAt(LocalDateTime.now());
            ClaimDocument saved = documentRepository.save(document);
            ClaimAttachmentContent content = new ClaimAttachmentContent();
            content.setAttachmentId(saved.getId());
            try {
                content.setContent(file.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to read attachment " + saved.getFileName(), e);
            }
            attachmentContentRepository.save(content);
            documents.add(saved);
        }
        return documents;
    }

    private String safeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Attachment filename is required");
        }
        String normalized = fileName.replace('\\', '/');
        String safeName = normalized.substring(normalized.lastIndexOf('/') + 1)
                .replaceAll("[\\r\\n]", "");
        if (safeName.isBlank() || safeName.length() > 255) {
            throw new IllegalArgumentException("Attachment filename must be 1 to 255 characters");
        }
        return safeName;
    }

    private void requireActiveStaffMember(String email, String role) {
        Boolean active = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1
                    FROM claims_staff_accounts
                    WHERE LOWER(email) = LOWER(?)
                      AND UPPER(role) = ?
                      AND is_active = TRUE
                )
                """, Boolean.class, email.trim(), role);
        if (!Boolean.TRUE.equals(active)) {
            throw new IllegalArgumentException("An active " + role.toLowerCase()
                    + " account is required for this action");
        }
    }

    private long countByStatus(List<Claim> claims, ClaimStatus status) {
        return claims.stream().filter(c -> c.getStatus() == status).count();
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public record DocumentContent(String fileName, String contentType, byte[] content) {
    }
}
