package com.example.claims.api.controller;

import com.example.claims.api.service.OrchestrationClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Thin proxy for all read/write endpoints other than claim submission.
 * Forwards to the orchestration service and returns its response unchanged.
 */
@RestController
public class ApiProxyController {

    private final OrchestrationClient orchestrationClient;

    public ApiProxyController(OrchestrationClient orchestrationClient) {
        this.orchestrationClient = orchestrationClient;
    }

    @RequestMapping(value = "/api/**",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH,
                    RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxy(HttpServletRequest request,
                                        @RequestBody(required = false) String body) {
        return orchestrationClient.forward(request, body);
    }

    @PostMapping(value = "/api/claims/{claimReference}/documents",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadClaimDocuments(
            HttpServletRequest request,
            @RequestPart("uploadedBy") String uploadedBy,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return orchestrationClient.forwardMultipart(request, Map.of(), Map.of("uploadedBy", uploadedBy), files);
    }

    @PostMapping(value = "/api/staff/claims/{claimReference}/information-requests",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createInformationRequest(
            HttpServletRequest request,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return orchestrationClient.forwardMultipart(request, Map.of("request", requestJson), Map.of(), files);
    }

    @PostMapping(value = "/api/claims/{claimReference}/information-requests/{requestId}/response",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> respondToInformationRequest(
            HttpServletRequest request,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return orchestrationClient.forwardMultipart(request, Map.of("request", requestJson), Map.of(), files);
    }

    @GetMapping("/api/claims/{claimReference}/documents/{documentId}/content")
    public ResponseEntity<byte[]> downloadDocument(HttpServletRequest request) {
        return orchestrationClient.forwardBytes(request);
    }
}
