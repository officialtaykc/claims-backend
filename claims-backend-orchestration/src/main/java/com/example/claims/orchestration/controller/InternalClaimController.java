package com.example.claims.orchestration.controller;

import com.example.claims.orchestration.dto.*;
import com.example.claims.orchestration.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Internal API consumed by claims-backend-api. Not exposed publicly.
 * Mirrors the public /api/claims routes under /internal/claims.
 */
@RestController
@RequestMapping("/internal/claims")
public class InternalClaimController {

    private final ClaimService claimService;

    public InternalClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public ResponseEntity<List<ClaimResponse>> getClaimsByEmail(@RequestParam String claimantEmail) {
        return ResponseEntity.ok(claimService.getClaimsByEmail(claimantEmail));
    }

    @GetMapping("/{claimReference}")
    public ResponseEntity<ClaimResponse> getClaim(@PathVariable String claimReference) {
        return ResponseEntity.ok(claimService.getClaimByReference(claimReference));
    }

    @GetMapping("/{claimReference}/history")
    public ResponseEntity<List<ClaimStatusHistoryResponse>> getHistory(@PathVariable String claimReference) {
        return ResponseEntity.ok(claimService.getHistory(claimReference));
    }

    @PostMapping(value = "/{claimReference}/information-requests/{requestId}/response",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClaimResponse.InfoRequestDto> respondToInfoRequest(
            @PathVariable String claimReference,
            @PathVariable UUID requestId,
            @Valid @RequestPart("request") InfoRequestRespondRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ResponseEntity.ok(
                claimService.respondToInfoRequest(claimReference, requestId, request, files));
    }

    @PostMapping(value = "/{claimReference}/information-requests/{requestId}/response",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClaimResponse.InfoRequestDto> respondToInfoRequestJson(
            @PathVariable String claimReference,
            @PathVariable UUID requestId,
            @Valid @RequestBody InfoRequestRespondRequest request) {
        return ResponseEntity.ok(
                claimService.respondToInfoRequest(claimReference, requestId, request, null));
    }

    @PostMapping(value = "/{claimReference}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ClaimResponse.DocumentDto>> uploadClaimDocuments(
            @PathVariable String claimReference,
            @RequestPart("uploadedBy") String uploadedBy,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ResponseEntity.ok(claimService.uploadClaimDocuments(claimReference, uploadedBy, files));
    }

    @GetMapping("/{claimReference}/documents/{documentId}/content")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable String claimReference, @PathVariable UUID documentId) {
        ClaimService.DocumentContent document =
                claimService.getDocumentContent(claimReference, documentId);
        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
        if (document.contentType() != null) {
            try {
                contentType = MediaType.parseMediaType(document.contentType());
            } catch (IllegalArgumentException ignored) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }
        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(document.fileName(), StandardCharsets.UTF_8)
                                .build().toString())
                .body(document.content());
    }

}
