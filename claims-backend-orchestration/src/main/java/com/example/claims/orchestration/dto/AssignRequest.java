package com.example.claims.orchestration.dto;

import jakarta.validation.constraints.NotBlank;

public class AssignRequest {

    @NotBlank(message = "assignedTo is required")
    private String assignedTo;

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
