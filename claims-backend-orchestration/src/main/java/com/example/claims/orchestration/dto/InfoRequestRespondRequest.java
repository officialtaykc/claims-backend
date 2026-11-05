package com.example.claims.orchestration.dto;

import jakarta.validation.constraints.NotBlank;

public class InfoRequestRespondRequest {

    @NotBlank(message = "responseMessage is required")
    private String responseMessage;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
