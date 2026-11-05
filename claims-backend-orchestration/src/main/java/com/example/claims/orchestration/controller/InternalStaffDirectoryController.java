package com.example.claims.orchestration.controller;

import com.example.claims.orchestration.dto.StaffOfficerResponse;
import com.example.claims.orchestration.service.StaffDirectoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/staff")
public class InternalStaffDirectoryController {

    private final StaffDirectoryService staffDirectoryService;

    public InternalStaffDirectoryController(StaffDirectoryService staffDirectoryService) {
        this.staffDirectoryService = staffDirectoryService;
    }

    @GetMapping("/officers")
    public ResponseEntity<List<StaffOfficerResponse>> getActiveOfficers() {
        return ResponseEntity.ok(staffDirectoryService.getActiveOfficers());
    }
}
